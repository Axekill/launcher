package zone.gamers.launcher.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class LauncherController {
    @FXML
    private Button playButton;

    @FXML
    private Pane root;

    Stage stage;

    public void onPlay(ActionEvent event) {
        stage = (Stage) playButton.getScene().getWindow();

    }
}