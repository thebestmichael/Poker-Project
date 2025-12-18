package project3.shared;

import java.io.Serializable;
import java.util.ArrayList;

public class PokerInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    // Message Types for the log in the poker app
    public static final int CONNECT = 1; 
    public static final int ANTE_BET = 2;
    public static final int PLAY_BET = 3;
    public static final int FOLD = 4;
    public static final int DEAL_HAND = 5;
    public static final int GAME_RESULT = 6;
    public static final int DISCONNECT = 7; 
    public static final int NEW_GAME = 8; 

    // Public fields to access directly no getters or setters really needed
    public int messageType;
    public int anteBet;
    public int pairPlusBet;
    public int playBet;
    public int totalWinnings;
    public ArrayList<Card> playerHand;
    public ArrayList<Card> dealerHand;
    public String gameMessage;
    public double amountWon; 

    public PokerInfo() {
        this.playerHand = new ArrayList<>();
        this.dealerHand = new ArrayList<>();
        this.gameMessage = "";
        this.totalWinnings = 0;
    }
}