package project3.client;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class WelcomeController {

	//private variables below
    @FXML
    private TextField ipAddressField;
    
    @FXML
    private TextField portField;
    
    @FXML
    private Button connectButton;
    
    private ClientApp client;
    
    @FXML
    public void initialize() {
        ipAddressField.setText("localhost");
        portField.setText("5555");
    }
    
    public void setClient(ClientApp client) {
        this.client = client;
    }
    
    //Connection to the server and IP
    @FXML
    private void handleConnect() {
        String ip = ipAddressField.getText().trim();
        String portText = portField.getText().trim();
        
        if (ip.isEmpty() || portText.isEmpty()) {
            showAlert("Connection Error", "Please enter both IP address and port number.");
            return;
        }
        
        try {
            int port = Integer.parseInt(portText);
            
            connectButton.setDisable(true);
            connectButton.setText("Connecting...");
            
            boolean connected = client.connectToServer(ip, port);
            
            if (connected) {
                client.switchToGameScene();
            } else {
                showAlert("Connection Failed", "Could not connect to server at " + ip + ":" + port);
                connectButton.setDisable(false);
                connectButton.setText("CONNECT");
            }
        } catch (NumberFormatException e) {
            showAlert("Invalid Port", "Port number must be a valid integer.");
            connectButton.setDisable(false);
            connectButton.setText("CONNECT");
        }
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}