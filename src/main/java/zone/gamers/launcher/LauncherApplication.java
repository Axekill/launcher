package zone.gamers.launcher;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class LauncherApplication extends Application {


    @Override
    public void start(Stage stage) throws IOException {
        addIcon(stage);

        Pane root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("launcher.fxml")));

        Scene scene = new Scene(root, 1024, 768);

        Image bgImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/11.jpg")));
        ImageView bgView = new ImageView(bgImage);
        Button playButton = new Button("PLAY");
        playButton.setLayoutX(900);
        playButton.setLayoutY(700);
        playButton.setId("playButton");

        root.getChildren().addAll(bgView, playButton);
        stage.setTitle("ОВОЩИ!!!");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }


    public void addIcon(Stage stage) throws IOException {
        try {
            stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/ico.jpg"))));
        } catch (Exception e) {
            System.out.println("Не удалось загрузить значок");
        }
        stage.show();
    }


    public static void main(String[] args) {
        launch();
    }
}