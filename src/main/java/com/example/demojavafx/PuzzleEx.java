package com.example.demojavafx;

import javafx.scene.control.Button;

import java.awt.*;
import javax.swing.JButton;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

class MyButton extends JButton {

    private boolean isLastButton;

    public MyButton() {
        super();
        initUI();
    }

    public MyButton(Image image) {
        super(new ImageIcon(image));
        initUI();
    }

    private void initUI() {
        isLastButton = false;
        BorderFactory.createLineBorder(Color.gray);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBorder(BorderFactory.createLineBorder(Color.yellow));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBorder(BorderFactory.createLineBorder(Color.gray));
            }
        });
    }

    public void setLastButton() {
        isLastButton = true;
    }

    public boolean isLastButton() {
        return isLastButton;
    }
}

public class PuzzleEx extends JFrame {

    private JPanel panel;
    private JButton compareButton;
    private BufferedImage source;
    private BufferedImage resized;
    private Image image;
    private MyButton lastButton;
    private int width, height;
    private List<Image> images = new ArrayList<>();
    private List<MyButton> buttons;
    private List<Point> solution;
    private List<Integer> buttonsName = new ArrayList<>();

    private final int NUMBER_OF_BUTTONS = 16; // 4x4 puzzle has 16 buttons
    private final int DESIRED_WIDTH = 800;

    public PuzzleEx() {
        initUI();
    }

    private void initUI() {
        int k = 1;
        solution = new ArrayList<>();

        solution.add(new Point(0, 0));
        solution.add(new Point(0, 1));
        solution.add(new Point(0, 2));
        solution.add(new Point(0, 3));
        solution.add(new Point(1, 0));
        solution.add(new Point(1, 1));
        solution.add(new Point(1, 2));
        solution.add(new Point(1, 3));
        solution.add(new Point(2, 0));
        solution.add(new Point(2, 1));
        solution.add(new Point(2, 2));
        solution.add(new Point(2, 3));
        solution.add(new Point(3, 0));
        solution.add(new Point(3, 1));
        solution.add(new Point(3, 2));
        solution.add(new Point(3, 3));

        buttons = new ArrayList<>();

        panel = new JPanel();
        panel.setBorder(BorderFactory.createLineBorder(Color.gray));
        panel.setLayout(new GridLayout(4, 4, 0, 0));

        compareButton = new JButton("Solve");
        compareButton.addActionListener(new CompareAction());
        panel.add(compareButton);

        add(compareButton, BorderLayout.SOUTH);

        try {
            source = loadImage();
            int h = getNewHeight(source.getWidth(), source.getHeight());
            resized = resizeImage(source, DESIRED_WIDTH, h, BufferedImage.TYPE_INT_ARGB);

        } catch (IOException ex) {
            Logger.getLogger(PuzzleEx.class.getName()).log(Level.SEVERE, null, ex);
        }

        width = resized.getWidth(null);
        height = resized.getHeight(null);

        add(panel, BorderLayout.CENTER);

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                image = createImage(new FilteredImageSource(resized.getSource(),
                        new CropImageFilter(j * width / 4, i * height / 4,
                                (width / 4), height / 4)));
                images.add(image);
                MyButton button = new MyButton(image);
                button.putClientProperty("position", new Point(i, j));
                button.setName(String.valueOf(k));
                k++;
                if (i == 3 && j == 3) {
                    lastButton = new MyButton();
                    lastButton.setBorderPainted(false);
                    lastButton.setContentAreaFilled(false);
                    lastButton.setLastButton();
                    lastButton.putClientProperty("position", new Point(i, j));
                } else {
                    buttons.add(button);
                }
            }
        }

        Collections.shuffle(buttons);
        for(MyButton button:buttons){
            buttonsName.add(Integer.valueOf(button.getName()));
        }
        while (isSolvable(buttonsName) == false){
            Collections.shuffle(buttons);
            buttonsName.clear();
            for(MyButton button:buttons){
                buttonsName.add(Integer.valueOf(button.getName()));
            }
        }

        buttons.add(lastButton);
        buttonsName.add(0);
        lastButton.setName("0");




        for (int i = 0; i < NUMBER_OF_BUTTONS; i++) {
            MyButton btn = buttons.get(i);
            panel.add(btn);
            btn.setBorder(BorderFactory.createLineBorder(Color.gray));
            btn.addActionListener(new ClickAction());
        }

        pack();
        setTitle("Puzzle");
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    //////////////////////
    public static boolean isSolvable(List<Integer> puzzle) {
        int inversions = 0;
        for (int i = 0; i < puzzle.size(); i++) {
            for (int j = i + 1; j < puzzle.size(); j++) {
                if (puzzle.get(i) > puzzle.get(j)) {
                    inversions++;
                }
            }
        }
        return (inversions % 2 == 0); // Если количество инверсий четное, то поле решаемо
    }
///////////////////////////
    private int getNewHeight(int w, int h) {
        double ratio = DESIRED_WIDTH / (double) w;
        int newHeight = (int) (h * ratio);
        return newHeight;
    }

    private BufferedImage loadImage() throws IOException {
        BufferedImage bimg = ImageIO.read(new File("D:\\myproject\\Java\\java_puzzle_slider\\src\\main\\java\\com\\example\\demojavafx\\dolphin.png"));
        return bimg;
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int width, int height, int type) throws IOException {
        BufferedImage resizedImage = new BufferedImage(width, height, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose();
        return resizedImage;
    }

    private class ClickAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            checkButton(e);
            checkSolution();
        }

        private void checkButton(ActionEvent e) {
            int lidx = 0;

            for (MyButton button : buttons) {
                if (button.isLastButton()) {
                    lidx = buttons.indexOf(button);
                }
            }

            JButton button = (JButton) e.getSource();
            int bidx = buttons.indexOf(button);

            if ((Math.abs(bidx - lidx) == 1 && bidx / 4 == lidx / 4) || // Check horizontal movement
                    (Math.abs(bidx - lidx) == 4 && bidx % 4 == lidx % 4)) { // Check vertical movement
                Collections.swap(buttons, bidx, lidx);
                updateButtons();
            }
        }

        private void updateButtons() {
            panel.removeAll();
            for (JComponent btn : buttons) {
                panel.add(btn);
            }
            panel.validate();
        }
    }

    private void checkSolution() {
        List<Point> current = new ArrayList<>();
        for (JComponent btn : buttons) {
            current.add((Point) btn.getClientProperty("position"));
        }
        if (compareList(solution, current)) {
            JOptionPane.showMessageDialog(panel, "Finished", "Congratulation", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static boolean compareList(List ls1, List ls2) {
        return ls1.toString().contentEquals(ls2.toString());
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                PuzzleEx puzzle = new PuzzleEx();
                puzzle.setVisible(true);
            }
        });
    }

    private class CompareAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            comparePuzzle();
        }
    }

    private void comparePuzzle() {
        int[][] myPuzzle = convertToListTo2DArray(buttonsName);

        Program.Puzzle puzzle = new Program.Puzzle(myPuzzle);
        puzzle.printBoard();

        List<Program.Move> moves = Program.Puzzle.solvePuzzle(puzzle);

        Program.Puzzle.displayMoves(moves);

        for (var move : moves) {
            puzzle.moveTile(move.tile);
        }

        puzzle.printBoard();

    }
    public static int[][] convertToListTo2DArray(List<Integer> list) {
        int[][] array = new int[4][4];
        int index = 0;

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                array[i][j] = list.get(index++);
            }
        }

        return array;
    }
}
//https://github.com/vesran/15-puzzle-game