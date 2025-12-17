module org.example.memberclubjavafx_assignment5 {

    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires atlantafx.base;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.feather;
    requires com.google.gson;
    requires java.sql;
    requires java.desktop;
    requires gson.extras;
    requires org.kordamp.ikonli.core;

    opens org.example.memberclubjavafx_assignment5 to javafx.fxml;
    exports org.example.memberclubjavafx_assignment5;

    opens org.example.memberclubjavafx_assignment5.model to com.google.gson, javafx.base;
    opens org.example.memberclubjavafx_assignment5.model.camping to com.google.gson;
    opens org.example.memberclubjavafx_assignment5.model.fishing to com.google.gson;
    opens org.example.memberclubjavafx_assignment5.model.vehicles to com.google.gson;
    opens org.example.memberclubjavafx_assignment5.model.enums to com.google.gson;
}