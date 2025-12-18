package project3.client;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import project3.shared.PokerInfo;

public class ResultController {
    
    @FXML private Label resultLabel;
    @FXML private Label winningsLabel;
    @FXML private Label detailsLabel;
    @FXML private Button playAgainButton;
    @FXML private Button exitButton;
    
    private ClientApp client;
    
    public void setClient(ClientApp client) {
        this.client = client;
    }
    
    public void displayResults(PokerInfo info) {
        // Accessing the PUBLIC FIELDs directly
        client.updateWinnings(info.totalWinnings);
        
        if (info.amountWon > 0) {
            resultLabel.setText("YOU WIN!");
            resultLabel.setStyle("-fx-text-fill: green; -fx-font-size: 36px; -fx-font-weight: bold;");
        } else if (info.amountWon < 0) {
            resultLabel.setText("YOU LOSE");
            resultLabel.setStyle("-fx-text-fill: red; -fx-font-size: 36px; -fx-font-weight: bold;");
        } else {
            if (info.messageType == PokerInfo.FOLD) {
                resultLabel.setText("FOLDED");
                resultLabel.setStyle("-fx-text-fill: red; -fx-font-size: 36px; -fx-font-weight: bold;");
            } else {
                resultLabel.setText("PUSH / EVEN");
                resultLabel.setStyle("-fx-text-fill: orange; -fx-font-size: 36px; -fx-font-weight: bold;");
            }
        }
        
        winningsLabel.setText("This hand: $" + info.amountWon);
        
        if (info.gameMessage != null) {
            detailsLabel.setText(info.gameMessage);
        } else {
            detailsLabel.setText("");
        }
    }
    
    @FXML
    private void handlePlayAgain() {
        client.switchToGameScene();
    }
    
    @FXML
    private void handleExit() {
        client.exitGame();
    }
}