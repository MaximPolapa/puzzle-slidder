module com.example.demojavafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.logging;
    requires org.jgrapht.core;


    opens com.example.demojavafx to javafx.fxml;
    exports com.example.demojavafx;
}