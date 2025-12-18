package project3.server;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ServerApp extends Application {
    private ListView<String> logList;
    private Server server; // using new server class
    private Button startBtn, stopBtn;
    private TextField portField;

    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("3-Card Poker Server");

        // Intro Scene
        VBox introBox = new VBox(10);
        introBox.setPadding(new Insets(20));
        introBox.setStyle("-fx-alignment: center;");
        Label portLabel = new Label("Port Number:");
        portField = new TextField("5555");
        startBtn = new Button("Start Server");
        introBox.getChildren().addAll(portLabel, portField, startBtn);
        Scene introScene = new Scene(introBox, 400, 300);

        // Log Scene
        BorderPane logRoot = new BorderPane();
        logList = new ListView<>();
        stopBtn = new Button("Stop Server");
        logRoot.setCenter(logList);
        logRoot.setBottom(stopBtn);
        Scene logScene = new Scene(logRoot, 600, 500);

        // START BUTTON ACTION
        startBtn.setOnAction(e -> {
            int port = Integer.parseInt(portField.getText());
            
            // This connects the GUI to your new Server.java file!
            server = new Server(port, data -> {
                Platform.runLater(() -> {
                    logList.getItems().add(data.toString());
                });
            });
            
            primaryStage.setScene(logScene);
        });

        // STOP BUTTON ACTION
        stopBtn.setOnAction(e -> {
            if(server != null) server.stop();
            Platform.exit();
            System.exit(0);
        });
        
        primaryStage.setOnCloseRequest(e -> {
            if(server != null) server.stop();
            Platform.exit();
            System.exit(0);
        });

        primaryStage.setScene(introScene);
        primaryStage.show();
    }
}