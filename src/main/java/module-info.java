module zone.gamers.launcher {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.desktop;
    requires jdk.compiler;

    opens zone.gamers.launcher to javafx.fxml;
    exports zone.gamers.launcher;
    exports zone.gamers.launcher.controller;
    opens zone.gamers.launcher.controller to javafx.fxml;
}