package com.example.demojavafx;

import java.util.*;

public class Program {
    static class Move {
        public Puzzle state;
        public int tile;

        public Move(Puzzle state, int tile) {
            this.state = state;
            this.tile = tile;
        }
    }

    static class Puzzle {
        private final int size = 4;
        private int[][] board;
        private int emptyX;
        private int emptyY;

        public Puzzle() {
            board = new int[size][size];
            board = new int[][]{
                    {11, 3, 12, 15},
                    {14, 13, 5, 1},
                    {7, 8, 2, 4},
                    {6, 9, 10, 0}
            };                                  
            emptyX = 3;
            emptyY = 3;
            //initBoard();
        }

       public Puzzle(int[][] board) {
            this.board = new int[size][size];
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    this.board[i][j] = board[i][j];
                    if (board[i][j] == 0) {
                        emptyX = i;
                        emptyY = j;
                    }
                }
            }
        }

        private void initBoard() {
            int count = 1;
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (count <= 15) {
                        board[i][j] = count++;
                    } else {
                        board[i][j] = 0;
                        emptyX = i;
                        emptyY = j;
                    }
                }
            }
        }

        public void printBoard() {
            System.out.print("empty position: (" + emptyX + ";" + emptyY + ")\n");
            System.out.print("is solved: " + isSolved() + "\n");
            System.out.print("is solvable: " + isSolvable() + "\n");
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (board[i][j] != 0) {
                        System.out.print(board[i][j] + "\t");
                    } else {
                        System.out.print(" \t");
                    }
                }
                System.out.println();
            }
        }

        int getInversionCount() {
            int invCount = 0;
            for (int i = 0; i < size * size - 1; i++) {
                for (int j = i + 1; j < size * size; j++) {
                    int xi = i / size, xj = j / size, yi = i % size, yj = j % size;
                    if (board[xi][yi] > 0 && board[xj][yj] > 0 && board[xi][yi] > board[xj][yj]) {
                        invCount++;
                    }
                }
            }
            return invCount;
        }

        public boolean isSolvable() {
            final int invCount = getInversionCount();

            if (size % 2 != 0) {
                return invCount % 2 == 0;
            }
            else {
                return ((size - emptyX) % 2 == 0) == (invCount % 2 != 0);
            }
        }

        public boolean isSolved() {
            int counter = 1;
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (board[i][j] != counter) {
                        return false;
                    }
                    counter++;
                    if (counter == size * size) {
                        counter = 0;
                    }
                }
            }
            return true;
        }

        public void moveTile(int tile) {
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (board[i][j] == tile) {
                        if ((Math.abs(emptyX - i) + Math.abs(emptyY - j)) == 1) {
                            board[emptyX][emptyY] = tile;
                            board[i][j] = 0;
                            emptyX = i;
                            emptyY = j;
                        }
                        return;
                    }
                }
            }
        }
        public int calculateHeuristic() {
            int heuristic = 0;
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (board[i][j] != 0) {
                        int x = (board[i][j] - 1) / size;
                        int y = (board[i][j] - 1) % size;
                        heuristic += Math.abs(i - x) + Math.abs(j - y);
                    }
                }
            }
            return heuristic;
        }


        public List<Move> generatePossibleMoves() {
            List<Move> possibleMoves = new ArrayList<>();

            int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
            for (int[] direction : directions) {
                int newX = emptyX + direction[0];
                int newY = emptyY + direction[1];
                if (newX >= 0 && newX < size && newY >= 0 && newY < size) {
                    int[][] newBoard = new int[size][size];
                    for (int i = 0; i < size; i++) {
                        newBoard[i] = board[i].clone();
                    }
                    int movedTile = newBoard[newX][newY];
                    newBoard[emptyX][emptyY] = newBoard[newX][newY];
                    newBoard[newX][newY] = 0;

                    Puzzle newPuzzle = new Puzzle(newBoard);
                    possibleMoves.add(new Move(newPuzzle, movedTile));
                }
            }

            return possibleMoves;
        }

        public static List<Move> solvePuzzle(Puzzle puzzle) {
            int depthLimit = puzzle.calculateHeuristic();
            int iteration = 0;

            while (true) {
                Result result = depthLimitedSearch(puzzle, depthLimit, new ArrayList<>(), -1);
                System.out.println("Iteration: " + (++iteration));
                System.out.println("Current depth limit: " + depthLimit);

                if (result.found) {
                    System.out.println("Solution found after " + iteration + " iterations");
                    return result.moves;
                } else if (result.cost == Integer.MAX_VALUE) {
                    System.out.println("No solution found after " + iteration + " iterations");
                    // If the new limit is max value, no solution was found.
                    return null;
                } else {
                    // Update depthLimit to the minimum cost of all pruned nodes in the previous iteration.
                    depthLimit = result.cost;
                }
            }
        }

        private static class Result {
            boolean found;
            int cost;
            List<Move> moves;

            Result(boolean found, int cost, List<Move> moves) {
                this.found = found;
                this.cost = cost;
                this.moves = moves;
            }
        }

        private static Result depthLimitedSearch(Puzzle puzzle, int limit, List<Move> path, int lastMove) {
            int heuristic = puzzle.calculateHeuristic();
            if (heuristic == 0) {
                return new Result(true, -1, path);  // Solution found
            }

            int cost = heuristic + path.size();

            if (cost > limit) {
                // This state exceeds the current limit, prune it from the search.
                return new Result(false, cost, null);
            }

            int minCost = Integer.MAX_VALUE;

            for (Move move : puzzle.generatePossibleMoves()) {
                if (move.tile == lastMove) {
                    // Skip move that undoes the last move.
                    continue;
                }

                path.add(move);
                Result result = depthLimitedSearch(move.state, limit, path, move.tile);

                if (result.found) {
                    return result;
                }

                // Track the minimum cost of all pruned nodes.
                if (result.cost < minCost) {
                    minCost = result.cost;
                }

                path.remove(path.size() - 1);
            }

            return new Result(false, minCost, null);
        }


        public static void displayMoves(List<Move> moves) {
            if (moves == null) {
                System.out.println("No solution found");
                return;
            }

            System.out.print("Moves { ");
            for (Move move : moves) {
                if (move.tile != -1) {
                    System.out.print(move.tile + " ");
                }
            }
            System.out.print("}\n");
            System.out.print("Solution moves: " + (moves.size() - 1) + "\n");
        }

    }

    public static void main(String[] args) {
        final Puzzle puzzle = new Puzzle();

        // puzzle.moveTile(15);
        // puzzle.moveTile(14);
        // puzzle.moveTile(13);
        // puzzle.moveTile(9);
        // puzzle.moveTile(10);
        // puzzle.moveTile(6);
        // puzzle.moveTile(7);
        // puzzle.moveTile(8);
        // puzzle.moveTile(12);
        // puzzle.moveTile(11);


        puzzle.printBoard();

        List<Move> moves = Puzzle.solvePuzzle(puzzle);

        Puzzle.displayMoves(moves);

        for (var move : moves) {
            puzzle.moveTile(move.tile);
        }

        puzzle.printBoard();
    }
}