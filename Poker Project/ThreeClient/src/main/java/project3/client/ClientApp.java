package project3.client;

import project3.shared.PokerInfo;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javafx.animation.PauseTransition;
import javafx.util.Duration;

//Client app below
public class ClientApp extends Application {
    private static Scene welcomeScene;
    private static Scene gameScene;
    private static Scene resultScene;
    private static Stage primaryStage;
    
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    
    private WelcomeController welcomeController;
    private GameController gameController;
    private ResultController resultController;
    
    private int totalWinnings = 0;
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage; 
        primaryStage.setTitle("Three Card Poker Client");
        
        //Loads the welcome screen
        FXMLLoader welcomeLoader = new FXMLLoader(getClass().getResource("/welcome.fxml"));
        Parent welcomeRoot = welcomeLoader.load();
        welcomeController = welcomeLoader.getController();
        welcomeController.setClient(this);
        welcomeScene = new Scene(welcomeRoot, 800, 600);
        welcomeScene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        
        //Loads the game screen
        FXMLLoader gameLoader = new FXMLLoader(getClass().getResource("/game.fxml"));
        Parent gameRoot = gameLoader.load();
        gameController = gameLoader.getController();
        gameController.setClient(this);
        gameScene = new Scene(gameRoot, 1000, 700);
        gameScene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        
        //Loads the result screen for client
        FXMLLoader resultLoader = new FXMLLoader(getClass().getResource("/winLose.fxml"));
        Parent resultRoot = resultLoader.load();
        resultController = resultLoader.getController();
        resultController.setClient(this);
        resultScene = new Scene(resultRoot, 600, 400);
        resultScene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        
        primaryStage.setScene(welcomeScene);
        primaryStage.show();
        
        primaryStage.setOnCloseRequest(e -> {
            exitGame();
        });
    }
    
    public boolean connectToServer(String ip, int port) {
        try {
            socket = new Socket(ip, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            
            Thread listener = new Thread(new ServerListener());
            listener.setDaemon(true);
            listener.start();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public void send(PokerInfo info) {
        try {
            out.writeObject(info);
            out.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    //Handles disconnection
    public void disconnect() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void switchToGameScene() {
        Platform.runLater(() -> {
            primaryStage.setScene(gameScene);
            gameController.resetForNewGame();
        });
    }

    public void switchToResultScene(PokerInfo info) {
        Platform.runLater(() -> {
            primaryStage.setScene(resultScene);
            resultController.displayResults(info);
        });
    }
    
    public void resetGame() {
        totalWinnings = 0;
        PokerInfo info = new PokerInfo();
        info.messageType = PokerInfo.NEW_GAME;
        send(info);
        switchToGameScene();
    }
    
    public void updateWinnings(int amount) {
        totalWinnings = amount;
    }
    
    public int getWinnings() {
        return totalWinnings;
    }
    
    public void exitGame() {
        if (socket != null && !socket.isClosed()) {
            PokerInfo info = new PokerInfo();
            info.messageType = PokerInfo.DISCONNECT;
            send(info);
        }
        disconnect();
        Platform.exit();
        System.exit(0);
    }
    
    private class ServerListener implements Runnable {
        @Override
        public void run() {
            try {
                while (true) {
                    PokerInfo info = (PokerInfo) in.readObject();
                    handleServerResponse(info);
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Lost connection to server");
            }
        }
    }
    
    private void handleServerResponse(PokerInfo info) {
        Platform.runLater(() -> {
            switch (info.messageType) {
                case PokerInfo.DEAL_HAND:
                    gameController.displayCards(info);
                    break;
                case PokerInfo.GAME_RESULT:
                    // Reveal Dealer Cards first
                    gameController.revealDealerCards(info);
                    
                    // Wait 3 seconds, then show results
                    PauseTransition pause = new PauseTransition(Duration.seconds(3));
                    pause.setOnFinished(e -> switchToResultScene(info));
                    pause.play();
                    break;
            }
        });
    }
}