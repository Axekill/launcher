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

        // Загружаем FXML
        //  Pane root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("launcher.fxml")));
        Pane root = new Pane();

        // Настраиваем сцену и фон
        Scene scene = new Scene(root, 1024, 768);
        setBackground(root); // Метод для установки фона
        stage.setTitle("ОВОЩИ!!!");
        stage.setResizable(false);
        stage.setScene(scene);

        // Кнопка "PLAY"
        Button playButton = new Button("PLAY");
        configurePlayButton(playButton); // Метод для конфигурации кнопки

        root.getChildren().addAll(
                new Label("версии игры:"), versionComboBox,
                new Label("Серверы:"), serverComboBox,
                playButton
        );

        stage.show();
        loadAvailableVersions();
        loadAvailableServers();
        configVersionCB(versionComboBox);
        configServerCB(serverComboBox);
    }

    private void configServerCB(ComboBox<String> serverComboBox) {
        serverComboBox.setLayoutX(100);
        serverComboBox.setLayoutY(700);
    }

    private void configVersionCB(ComboBox<String> versionComboBox) {
        versionComboBox.setLayoutX(200);
        versionComboBox.setLayoutY(700);
    }

    // Метод для установки фона
    private void setBackground(Pane root) {
        Image bgImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/11.jpg")));
        ImageView bgView = new ImageView(bgImage);
        root.getChildren().add(0, bgView); // Устанавливаем фоновое изображение на задний план
    }

    // Метод для конфигурации кнопки "PLAY"
    private void configurePlayButton(Button playButton) {
        playButton.setLayoutX(900);
        playButton.setLayoutY(700);
        playButton.setId("playButton");
        playButton.setOnAction(e -> launchGame());
    }


    private void loadAvailableVersions() {
        try {
            // Убедитесь, что URL правильный и сервер доступен
            URL url = new URL("https://example.com/api/versions"); // Замените на URL вашего сервера
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            // Проверяем статус ответа
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                List<String> versions = new ArrayList<>();

                // Читаем данные из потока
                while ((inputLine = in.readLine()) != null) {
                    versions.add(inputLine.trim()); // Убираем пробелы для аккуратности
                }

                availableVersionsList.getItems().clear(); // Очистить старые данные перед добавлением новых
                availableVersionsList.getItems().addAll(versions);
                in.close();
            } else {
                showAlert("Ошибка: Сервер вернул статус " + responseCode);
            }
            conn.disconnect();

        } catch (IOException e) {
            showAlert("Ошибка при загрузке доступных версий: " + e.getMessage());
            e.printStackTrace(); // Для отладки
        }
    }

    private void loadAvailableServers() {
        try {
            URL url = new URL("https://example.com/api/servers"); // Замените на URL вашего сервера
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            // Проверяем код ответа сервера
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                List<String> servers = new ArrayList<>();

                // Читаем данные из потока
                while ((inputLine = in.readLine()) != null) {
                    servers.add(inputLine.trim()); // Убираем пробелы
                }

                availableServersList.getItems().clear(); // Очистка списка перед добавлением новых данных
                availableServersList.getItems().addAll(servers);
                in.close();
            } else {
                showAlert("Ошибка: Сервер вернул статус " + responseCode);
            }
            conn.disconnect();

        } catch (IOException e) {
            showAlert("Ошибка при загрузке доступных серверов: " + e.getMessage());
            e.printStackTrace(); // Для отладки
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