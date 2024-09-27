package zone.gamers.launcher;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class LauncherApplication extends Application {
    private ComboBox<String> versionComboBox;
    private ComboBox<String> serverComboBox;
    private ListView<String> availableVersionsList;
    private ListView<String> availableServersList;

    @Override
    public void start(Stage stage) throws IOException {
        addIcon(stage);

        versionComboBox = new ComboBox<>();
        serverComboBox = new ComboBox<>();
        availableVersionsList = new ListView<>();
        availableServersList = new ListView<>();

        Pane root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("launcher.fxml")));

        Scene scene = new Scene(root, 1024, 768);

        Image bgImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/11.jpg")));
        ImageView bgView = new ImageView(bgImage);
        Button playButton = new Button("PLAY");
        playButton.setLayoutX(900);
        playButton.setLayoutY(700);
        playButton.setId("playButton");
        playButton.setOnAction(e -> launchGame());

        root.getChildren().addAll(
                new Label("версии игры:"),
                availableVersionsList,
                new Label("Серверы:"),
                availableServersList,
                bgView, playButton);

        stage.setTitle("ОВОЩИ!!!");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();

        loadAvailableVersions();
        loadAvailableServers();
    }

    private void loadAvailableVersions() {
        try {
            URL url = new URL("https://example.com/api/versions"); // Замените на URL вашего сервера
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            List<String> versions = new ArrayList<>();

            while ((inputLine = in.readLine()) != null) {
                versions.add(inputLine); // Предполагаем, что сервер возвращает версии по одной в строке
            }

            availableVersionsList.getItems().addAll(versions);
            in.close();
            conn.disconnect();

        } catch (Exception e) {
            showAlert("Ошибка при загрузке доступных версий: " + e.getMessage());
        }
    }

    private void loadAvailableServers() {
        try {
            URL url = new URL("https://example.com/api/servers"); // Замените на URL вашего сервера
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            List<String> servers = new ArrayList<>();

            while ((inputLine = in.readLine()) != null) {
                servers.add(inputLine); // Предполагаем, что сервер возвращает сервера по одной в строке
            }

            availableServersList.getItems().addAll(servers);
            in.close();
            conn.disconnect();

        } catch (Exception e) {
            showAlert("Ошибка при загрузке доступных серверов: " + e.getMessage());
        }
    }

    private void launchGame() {
        String selectVersion = availableVersionsList.getSelectionModel().getSelectedItem();
        String selectServer = availableServersList.getSelectionModel().getSelectedItem();

        if (selectVersion == null || selectServer == null) {
            System.out.println("Пожалуйста выберите версию и сервер");
            return;
        }
        String gamePath = "C:\\Program Files\\MyGame\\" + selectVersion + "\\game.exe"; // Путь к игре

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(gamePath);
            Process process = processBuilder.start();
            System.out.println("Игра запущена: " + gamePath);

            // Закрыть лаунчер после запуска игры (опционально)
            // Stage stage = (Stage) launchButton.getScene().getWindow();
            // stage.close();
        } catch (Exception e) {
            showAlert("Ошибка при запуске игры: " + e.getMessage());
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Информация");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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