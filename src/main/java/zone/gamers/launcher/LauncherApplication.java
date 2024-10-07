package zone.gamers.launcher;

import com.sun.tools.javac.Main;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
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
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class LauncherApplication extends Application {

    private ComboBox<String> serverComboBox = new ComboBox<>();
    private String availableVersionsGame;
    private ListView<String> availableServersList;
    private Button playButton;
    private String serverGame;


    @Override
    public void start(Stage stage) throws IOException {
        addIcon(stage);

        serverComboBox.getItems().addAll("1 Овощной");
        availableServersList = new ListView<>();
        playButton = new Button("PLAY");

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
        // Button playButton = new Button("PLAY");
        configurePlayButton(playButton); // Метод для конфигурации кнопки

        //Гиперссылка vk
        var vkImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/vk.png")));
        ImageView vkIcon = new ImageView(vkImg);
        Hyperlink vk = new Hyperlink("", vkIcon);
        configHVK(vk);
        //discord
        var dcImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/discord.png")));
        ImageView dcIcon = new ImageView(dcImg);
        Hyperlink dc = new Hyperlink("", dcIcon);
        configHDC(dc);


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

    private void configHDC(Hyperlink dc) {
        dc.setLayoutX(510);
        dc.setLayoutY(680);
        dc.setOnAction(event -> {
            getHostServices().showDocument("https://discord.gg/B7sh4vR7dM");
        });
    }


    private void configHVK(Hyperlink vk) {
        vk.setLayoutX(450);
        vk.setLayoutY(680);
        vk.setOnAction(event -> {
            getHostServices().showDocument("https://vk.com/ovoschipz");
        });
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
        root.getChildren().add(0, bgView); // Устанавливаем фоновое изображение на задний план
    }

    // Метод для конфигурации кнопки "PLAY"
    private void configurePlayButton(Button playButton) {
        playButton.setLayoutX(900);
        playButton.setLayoutY(700);
        playButton.setId("playButton");
        playButton.setOnAction(e -> launchGame());
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
        String selectServer = availableServersList.getSelectionModel().getSelectedItem();

        if (selectServer == null) {
            System.out.println("Пожалуйста выберите сервер");
            return;
        }

        String fileName = "game.exe"; // Имя файла
        List<String> searchDirectories = List.of("C:\\", "D:\\", "E:\\", "F:\\"); // Список разделов
        String currentGameVersion = "1.0.0"; // Версия текущей игры
        String serverGameVersion = getServerGameVersion(selectServer); // Метод для получения версии игры с сервера

        try {
            Path gamePath = null;

            for (String directory : searchDirectories) {
                try (Stream<Path> paths = Files.walk(Paths.get(directory))) {
                    gamePath = paths
                            .filter(Files::isRegularFile)
                            .filter(path -> path.getFileName().toString().equalsIgnoreCase(fileName))
                            .findFirst()
                            .orElse(null);
                }
                if (gamePath != null) break; // Прерываем, если файл найден
            }

            if (gamePath == null) {
                playButton.setText("Download");
                showAlert("Игра не найдена.");
                //  downloadGame(fileName);
                return;
            } else if (!currentGameVersion.equals(serverGameVersion)) {
                playButton.setText("Update");
                showAlert("Необходимо обновление игры.");
                return;
            } else {
                playButton.setText("Play");
            }

            ProcessBuilder processBuilder = new ProcessBuilder(gamePath.toString());
            Process process = processBuilder.start();
            System.out.println("Игра запущена: " + gamePath);
        } catch (IOException e) {
            showAlert("Ошибка при поиске игры: " + e.getMessage());
        }
    }

    private void downloadGame(String server, String fileName) {
        String fileUrl = server + "/path/to/" + fileName;

        // Открываем диалог выбора директории
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = fileChooser.showSaveDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            // Получаем выбранную директорию
            Path saveDir = fileChooser.getSelectedFile().toPath();
            Path savePath = saveDir.resolve(fileName);

            // Создаем директорию если она ещё не существует
            try {
                Files.createDirectories(savePath.getParent());
            } catch (IOException e) {
                showAlert("Ошибка при создании директории: " + e.getMessage());
                return;
            }

            // Скачиваем файл
            try (BufferedInputStream in = new BufferedInputStream(new URL(fileUrl).openStream());
                 FileOutputStream fileOutputStream = new FileOutputStream(savePath.toString())) {
                byte[] dataBuffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                    fileOutputStream.write(dataBuffer, 0, bytesRead);
                }
                showAlert("Игра успешно загружена.");
                playButton.setText("PLAY");
            } catch (IOException e) {
                showAlert("Ошибка при скачивании игры: " + e.getMessage());
            }
        } else {
            showAlert("Скачивание отменено.");
        }
    }

    private String getServerGameVersion(String server) {
        // Имплементация для получения версии игры с сервера
        return "1.0.1"; // Заглушка, возвращающая версию сервера
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