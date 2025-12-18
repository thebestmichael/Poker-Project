package project3.shared;

import java.io.Serializable;

public class Card implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private char suit;
    private int rank;

    public Card(char suit, int rank) {
        this.suit = suit;
        this.rank = rank;
    }

    //Gets the suit
    public char getSuit() {
        return suit;
    }

    //Gets the rank
    public int getRank() {
        return rank;
    }

    @Override
    public String toString() {
        String valStr;
        // Logic to convert rank to text (Ace, King, etc.)
        if (rank == 14) valStr = "Ace";
        else if (rank == 13) valStr = "King";
        else if (rank == 12) valStr = "Queen";
        else if (rank == 11) valStr = "Jack";
        else valStr = String.valueOf(rank);
        
        return valStr + " of " + suit;
    }
}