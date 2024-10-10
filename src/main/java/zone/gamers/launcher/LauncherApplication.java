package zone.gamers.launcher;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class LauncherApplication extends Application {

    private final ComboBox<String> serverComboBox = new ComboBox<>();
    private Button playButton;

    @Override
    public void start(Stage stage) throws IOException {

        addIcon(stage);

        playButton = new Button("PLAY");

        // Загружаем FXML
        //  Pane root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("launcher.fxml")));
        AnchorPane root = new AnchorPane();

        // Настраиваем сцену и фон
        Scene scene = new Scene(root, 1024, 768);
        setBackground(root); // Метод для установки фона
        stage.setTitle("ОВОЩИ!!!");
        stage.setResizable(false);
        stage.setScene(scene);

        // Кнопка "PLAY"
        // Button playButton = new Button("PLAY");
        configurePlayButton(playButton); // Метод для конфигурации кнопки

        //Гиперссылки
        Hyperlink vk = createHyperlink("/img/vk.png", "https://vk.com/ovoschipz", 450, 680);
        Hyperlink dc = createHyperlink("/img/discord.png", "https://discord.gg/B7sh4vR7dM", 510, 680);


        Label serverLabel = new Label("Сервер");


        root.getChildren().addAll(
                serverLabel, serverComboBox,
                playButton, vk, dc
        );

        stage.show();
        loadAvailableServers();
        configServerLabel(serverLabel);
        configServerCB(serverComboBox);
    }

    private Hyperlink createHyperlink(String imgPath, String url, double posX, double posY) {
        Image img = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imgPath)));
        ImageView icon = new ImageView(img);
        Hyperlink hyperlink = new Hyperlink("", icon);
        hyperlink.setLayoutX(posX);
        hyperlink.setLayoutY(posY);
        hyperlink.setOnAction(event -> getHostServices().showDocument(url));
        return hyperlink;
    }

    private void configServerLabel(Label serverLabel) {
        serverLabel.setLayoutX(100);
        serverLabel.setLayoutY(680);
        serverLabel.setFont(new Font(14.0));
        serverLabel.setTextFill(Color.RED);
    }

    private void configServerCB(ComboBox<String> serverComboBox) {
        serverComboBox.setLayoutX(100);
        serverComboBox.setLayoutY(700);
    }

    // Метод для установки фона
    private void setBackground(Pane root) {
        Image bgImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/11.jpg")));
        ImageView bgView = new ImageView(bgImage);
        root.getChildren().addFirst(bgView); // Устанавливаем фоновое изображение на задний план
    }

    // Метод для конфигурации кнопки "PLAY"
    private void configurePlayButton(Button playButton) {
        playButton.setLayoutX(900);
        playButton.setLayoutY(700);
        playButton.setId("playButton");
        playButton.setOnAction(e -> launchGame());
    }


    private void launchGame() {
        String selectedServer = serverComboBox.getSelectionModel().getSelectedItem();

        if (selectedServer == null) {
            showAlert("Пожалуйста, выберите сервер");
            return;
        }

        String gameFileName = "game.exe"; // Имя файла игры
        List<String> searchDirectories = List.of("C:\\", "D:\\", "E:\\", "F:\\"); // Директории для поиска
        String serverGameVersion = getServerGameVersion(selectedServer);
        try {
            Optional<Path> gamePath = findGameFile(searchDirectories, gameFileName);

            if (gamePath.isEmpty()) {
                playButton.setText("Download");
                handleGameDownload(selectedServer, gameFileName);
                return;
            }

            String currentGameVersion = getCurrentGameVersion(gamePath.get());

            if (!currentGameVersion.equals(serverGameVersion)) {
                playButton.setText("Update");
                handleGameDownload(selectedServer, gameFileName);
                return;
            }

            playButton.setText("Play");
            startGame(gamePath.get());

        } catch (IOException e) {
            showAlert("Ошибка при поиске или запуске игры: " + e.getMessage());
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

                serverComboBox.getItems().clear(); // Очистка ComboBox перед добавлением новых данных
                serverComboBox.getItems().addAll(servers); // Добавление серверов в ComboBox
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


    private String getServerGameVersion(String server) {
        String versionUrl = server + "/game/version"; // URL для запроса версии игры
        StringBuilder response = new StringBuilder();

        try {
            // Создаем URL объект и открываем соединение
            HttpURLConnection connection = (HttpURLConnection) new URL(versionUrl).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);  // Тайм-аут соединения
            connection.setReadTimeout(5000);     // Тайм-аут чтения

            // Проверяем код ответа
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // Используем try-with-resources для автоматического закрытия
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                }
                return response.toString(); // Возвращаем версию игры
            } else {
                System.err.println("Ошибка: " + connection.getResponseCode());
                return "1.0"; // Возвращаем запасную версию в случае ошибки
            }
        } catch (IOException e) {
            System.err.println("Ошибка при получении версии: " + e.getMessage());
            return "1.0";
        }
    }

    private String getCurrentGameVersion(Path gamePath) throws IOException {
        Path configPath = gamePath.getParent().resolve("config.txt");

        if (!Files.exists(configPath)) {
            return "config not found"; // Обработка отсутствия файла
        }

        try {
            List<String> lines = Files.readAllLines(configPath);

            for (String line : lines) {
                if (line.startsWith("version=")) {
                    return line.split("=")[1].trim(); // Возвращаем версию после знака '=' и убираем лишние пробелы
                }
            }

            return "version not found"; // Если версия не найдена
        } catch (IOException e) {
            throw new IOException("Ошибка при чтении конфигурационного файла: " + e.getMessage());
        }
    }

    private void handleGameDownload(String server, String fileName) {
        String fileUrl = server + "/path/to/" + fileName;
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int result = fileChooser.showSaveDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedDir = fileChooser.getSelectedFile();
            new Thread(() -> {
                try {
                    URL url = new URL(fileUrl);
                    try (InputStream in = url.openStream();
                         FileOutputStream out = new FileOutputStream(new File(selectedDir, fileName))) {
                        byte[] buffer = new byte[2048];
                        int bytesRead;
                        while ((bytesRead = in.read(buffer)) != -1) {
                            out.write(buffer, 0, bytesRead);
                        }
                    }
                    showAlert("Игра успешно загружена/обновлена.");
                } catch (IOException e) {
                    showAlert("Ошибка при загрузке игры: " + e.getMessage());
                }
            }).start();
        }
    }

    private Optional<Path> findGameFile(List<String> directories, String fileName) throws IOException {
        for (String directory : directories) {
            try (Stream<Path> paths = Files.walk(Paths.get(directory))) {
                return paths
                        .filter(Files::isRegularFile)
                        .filter(path -> path.getFileName().toString().equalsIgnoreCase(fileName))
                        .findFirst(); // Возвращает найденный путь или пустой
            }
        }
        return Optional.empty(); // Если ничего не найдено
    }

    private void startGame(Path gamePath) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder(gamePath.toString());
        Process process = processBuilder.start();
        System.out.println("Игра запущена: " + gamePath);
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