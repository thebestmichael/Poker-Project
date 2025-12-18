package project3.server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;

import project3.shared.Card;
import project3.shared.PokerInfo;

public class Server {

    private int port;
    private ArrayList<ClientThread> clients = new ArrayList<>();
    private Consumer<Serializable> callback;
    private ServerSocket serverSocket; 

    public Server(int port, Consumer<Serializable> callback) {
        this.port = port;
        this.callback = callback;
        Thread serverThread = new Thread(() -> {
            try {
                serverSocket = new ServerSocket(port);
                safeCallback("Server started on port: " + port);

                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    ClientThread cf = new ClientThread(clientSocket);
                    clients.add(cf);
                    cf.start();
                    safeCallback("Client connected. Total clients: " + clients.size());
                }
            } catch (Exception e) {
                safeCallback("Server socket closed or error: " + e.getMessage());
                e.printStackTrace(); // PRINT ERROR TO CONSOLE
            }
        });
        serverThread.setDaemon(true);
        serverThread.start();
    }
    
    // Wrapper to prevent callback crashes from killing the thread
    private void safeCallback(Serializable data) {
        try {
            callback.accept(data);
        } catch (Exception e) {
            System.err.println("GUI Callback Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            if (serverSocket != null) serverSocket.close();
            for(ClientThread c : clients) {
                c.closeConnection();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public class ClientThread extends Thread {
        Socket socket;
        ObjectOutputStream out;
        ObjectInputStream in;
        
        ArrayList<Card> deck;
        ArrayList<Card> playerHand;
        ArrayList<Card> dealerHand;
        int anteBet;
        int pairPlusBet;
        int playBet;
        int totalWinnings;

        ClientThread(Socket socket) {
            this.socket = socket;
        }

        public void closeConnection() {
            try {
                if (out != null) out.close();
                if (in != null) in.close();
                if (socket != null) socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void run() {
            try {
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());
                socket.setTcpNoDelay(true);

                while (true) {
                    PokerInfo info = (PokerInfo) in.readObject();
                    
                    switch (info.messageType) {
                        case PokerInfo.ANTE_BET:
                            this.anteBet = info.anteBet;
                            this.pairPlusBet = info.pairPlusBet;
                            this.totalWinnings = 0;
                            safeCallback("Client placed Ante: $" + anteBet + ", PairPlus: $" + pairPlusBet);
                            
                            dealNewHand();
                            
                            PokerInfo response = new PokerInfo();
                            response.messageType = PokerInfo.DEAL_HAND;
                            response.playerHand = this.playerHand;
                            response.dealerHand = this.dealerHand; 
                            
                            out.writeObject(response);
                            out.reset();
                            safeCallback("Cards dealt to client.");
                            break;

                        case PokerInfo.PLAY_BET:
                            this.playBet = info.playBet;
                            safeCallback("Client plays. Calculating results...");
                            evaluateGame();
                            break;

                        case PokerInfo.FOLD:
                            safeCallback("Client folded.");
                            handleFold();
                            break;
                            
                        case PokerInfo.NEW_GAME:
                            this.totalWinnings = 0;
                            safeCallback("Client requested fresh start.");
                            break;
                            
                        case PokerInfo.DISCONNECT:
                            safeCallback("Client disconnected.");
                            clients.remove(this);
                            closeConnection();
                            return;
                    }
                }
            } catch (Exception e) {
                System.err.println("CRITICAL ERROR IN CLIENT THREAD:");
                e.printStackTrace(); // THIS WILL SHOW YOU THE ERROR
                safeCallback("Client connection lost/Error occurred.");
                clients.remove(this);
            }
        }
        
        private void dealNewHand() {
            deck = new ArrayList<>();
            char[] suits = {'C', 'D', 'H', 'S'};
            for (char s : suits) {
                for (int i = 2; i <= 14; i++) {
                    deck.add(new Card(s, i));
                }
            }
            java.util.Collections.shuffle(deck);
            
            playerHand = new ArrayList<>();
            dealerHand = new ArrayList<>();
            
            for(int i=0; i<3; i++) playerHand.add(deck.remove(0));
            for(int i=0; i<3; i++) dealerHand.add(deck.remove(0));
        }
        
        private void evaluateGame() throws Exception {
            try {
                int amountWon = 0;
                String message = "";
                
                // Logic checks
                int ppWinnings = ThreeCardLogic.evalPPWinnings(playerHand, pairPlusBet);
                amountWon += ppWinnings;
                
                if (!ThreeCardLogic.isDealerQualified(dealerHand)) {
                    amountWon += anteBet; 
                    message = "Dealer does not qualify. Ante wins 1:1. Play bet pushes.";
                    if (ppWinnings > 0) message += "\nPair Plus Won $" + ppWinnings;
                } else {
                    int result = ThreeCardLogic.compareHands(dealerHand, playerHand);
                    if (result == 2) { 
                        amountWon += (anteBet + playBet); 
                        message = "You Beat the Dealer!";
                        if (ppWinnings > 0) message += "\nPair Plus Won $" + ppWinnings;
                    } else if (result == 1) { 
                        amountWon -= (anteBet + playBet); 
                        message = "Dealer Wins.";
                        if (ppWinnings > 0) message += "\nPair Plus Won $" + ppWinnings;
                    } else {
                        message = "It's a Tie.";
                        if (ppWinnings > 0) message += "\nPair Plus Won $" + ppWinnings;
                    }
                }
                
                PokerInfo resultInfo = new PokerInfo();
                resultInfo.messageType = PokerInfo.GAME_RESULT;
                resultInfo.amountWon = amountWon;
                resultInfo.totalWinnings = amountWon;
                resultInfo.playerHand = playerHand;
                resultInfo.dealerHand = dealerHand; 
                resultInfo.gameMessage = message;
                
                out.writeObject(resultInfo);
                out.reset();
            } catch (Exception e) {
                System.err.println("Error evaluating game:");
                e.printStackTrace();
                throw e; // Rethrow to main loop to handle disconnect
            }
        }
        
        private void handleFold() {
            try {
                int loss = anteBet + pairPlusBet;
                PokerInfo resultInfo = new PokerInfo();
                resultInfo.messageType = PokerInfo.GAME_RESULT; 
                resultInfo.amountWon = -loss;
                resultInfo.gameMessage = "You Folded. Lost Ante and Pair Plus.";
                
                out.writeObject(resultInfo);
                out.reset();
            } catch (Exception e) {
                System.err.println("Error handling fold:");
                e.printStackTrace();
            }
        }
    }
}