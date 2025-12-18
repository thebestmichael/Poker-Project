package project3.client;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.application.Platform;
import project3.shared.PokerInfo;
import project3.shared.Card;
import javafx.scene.Scene;

public class GameController {
    
	//private variables below
    @FXML private TextField anteBetField;
    @FXML private TextField pairPlusBetField;
    @FXML private Label anteWagerLabel;
    @FXML private Label pairPlusWagerLabel;
    @FXML private Label playWagerLabel;
    @FXML private Label totalWinningsLabel;
    @FXML private TextArea gameLogArea;
    @FXML private Button dealButton;
    @FXML private Button playButton;
    @FXML private Button foldButton;
    @FXML private HBox playerCardBox;
    @FXML private HBox dealerCardBox;
    @FXML private VBox bettingArea;
    @FXML private VBox playFoldArea;
    @FXML private MenuItem exitMenuItem;
    @FXML private MenuItem freshStartMenuItem;
    @FXML private MenuItem newLookMenuItem;
    
    private ClientApp client;
    private int currentAnteBet = 0;
    private int currentPairPlusBet = 0;
    private boolean useAlternateStyle = false;
    
    @FXML
    public void initialize() {
        playFoldArea.setVisible(false);
        playFoldArea.setManaged(false);
    }
    
    public void setClient(ClientApp client) {
        this.client = client;
    }
    
    @FXML
    private void handleDeal() {
        String anteText = anteBetField.getText().trim();
        String pairPlusText = pairPlusBetField.getText().trim();
        
        try {
            currentAnteBet = anteText.isEmpty() ? 0 : Integer.parseInt(anteText);
            currentPairPlusBet = pairPlusText.isEmpty() ? 0 : Integer.parseInt(pairPlusText);
            
            if (currentAnteBet < 5 || currentAnteBet > 25) {
                showAlert("Invalid Bet", "Ante bet must be between $5 and $25.");
                return;
            }
            
            if (currentPairPlusBet != 0 && (currentPairPlusBet < 5 || currentPairPlusBet > 25)) {
                showAlert("Invalid Bet", "Pair Plus bet must be between $5 and $25, or $0.");
                return;
            }
            
            PokerInfo info = new PokerInfo();
            // Accessing the PUBLIC FIELDs directly
            info.messageType = PokerInfo.ANTE_BET;
            info.anteBet = currentAnteBet;
            info.pairPlusBet = currentPairPlusBet;
            
            client.send(info);
            
            anteWagerLabel.setText("Ante: $" + currentAnteBet);
            pairPlusWagerLabel.setText("Pair Plus: $" + currentPairPlusBet);
            
            bettingArea.setVisible(false);
            bettingArea.setManaged(false);
            
            addToGameLog("Bets placed. Waiting for cards...");
            
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter valid numbers for bets.");
        }
    }
    
    @FXML
    private void handlePlay() {
        PokerInfo info = new PokerInfo();
        // Accessing the PUBLIC FIELDs directly
        info.messageType = PokerInfo.PLAY_BET;
        info.playBet = currentAnteBet;
        
        client.send(info);
        
        playWagerLabel.setText("Play: $" + currentAnteBet);
        playFoldArea.setVisible(false);
        playFoldArea.setManaged(false);
        
        addToGameLog("You chose to play. Play wager: $" + currentAnteBet);
    }
    
    @FXML
    private void handleFold() {
        PokerInfo info = new PokerInfo();
        info.messageType = PokerInfo.FOLD;
        
        client.send(info);
        
        addToGameLog("You folded. Hand is over.");
    }
    
    public void displayCards(PokerInfo info) {
        playerCardBox.getChildren().clear();
        // Accessing PUBLIC FIELD directly
        if (info.playerHand != null) {
            for (Card card : info.playerHand) {
                Label cardLabel = new Label(card.toString());
                cardLabel.getStyleClass().add("card-label");
                playerCardBox.getChildren().add(cardLabel);
            }
        }
        
        dealerCardBox.getChildren().clear();
        for (int i = 0; i < 3; i++) {
            Label cardLabel = new Label("***");
            cardLabel.getStyleClass().add("card-label");
            dealerCardBox.getChildren().add(cardLabel);
        }
        
        addToGameLog("Cards dealt. Make your decision: Play or Fold?");
        
        playFoldArea.setVisible(true);
        playFoldArea.setManaged(true);
    }
    
    public void revealDealerCards(PokerInfo info) {
        dealerCardBox.getChildren().clear();
        if (info.dealerHand != null) {
            for (Card card : info.dealerHand) {
                Label cardLabel = new Label(card.toString());
                cardLabel.getStyleClass().add("card-label");
                dealerCardBox.getChildren().add(cardLabel);
            }
        }
        addToGameLog("Dealer reveals hand...");
    }
    
    public void resetForNewGame() {
        anteBetField.clear();
        pairPlusBetField.clear();
        anteWagerLabel.setText("Ante: $0");
        pairPlusWagerLabel.setText("Pair Plus: $0");
        playWagerLabel.setText("Play: $0");
        totalWinningsLabel.setText("Total Winnings: $" + client.getWinnings());
        
        playerCardBox.getChildren().clear();
        dealerCardBox.getChildren().clear();
        
        bettingArea.setVisible(true);
        bettingArea.setManaged(true);
        playFoldArea.setVisible(false);
        playFoldArea.setManaged(false);
        
        addToGameLog("New game started. Place your bets!");
    }
    
    public void displayError(String message) {
        showAlert("Error", message);
    }
    
    private void addToGameLog(String message) {
        Platform.runLater(() -> {
            gameLogArea.appendText(message + "\n");
        });
    }
    
    @FXML
    private void handleExit() {
        client.exitGame();
    }
    
    @FXML
    private void handleFreshStart() {
        client.resetGame();
        gameLogArea.clear();
        addToGameLog("Fresh start! All winnings reset to $0.");
    }
    
 // This handles functionality for the new look
    @FXML
    private void handleNewLook() {
        useAlternateStyle = !useAlternateStyle;
        String cssName = useAlternateStyle ? "game-alt.css" : "styles.css";
        
        // Try multiple ways to find the resource
        java.net.URL cssResource = getClass().getResource("/" + cssName);
        
        if (cssResource == null) {
            cssResource = getClass().getClassLoader().getResource(cssName);
        }
        
        if (cssResource == null) {
            // Last resort: standard Maven location
            cssResource = getClass().getResource(cssName);
        }

        if (cssResource == null) {
            System.err.println("CRITICAL ERROR: Could not find CSS file: " + cssName);
            showAlert("Style Error", "Could not load stylesheet: " + cssName);
            return;
        }

        System.out.println("Loading CSS from: " + cssResource.toExternalForm());

        Scene scene = gameLogArea.getScene();
        if (scene != null) {
            scene.getStylesheets().clear();
            scene.getStylesheets().add(cssResource.toExternalForm());
            
            // Force the layout update for New Look functionality
            if (scene.getRoot() != null) {
                scene.getRoot().requestLayout();
            }
        }
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}