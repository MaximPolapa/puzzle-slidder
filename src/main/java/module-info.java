module com.example.demojavafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.logging;


    opens com.example.demojavafx to javafx.fxml;
    exports com.example.demojavafx;
}