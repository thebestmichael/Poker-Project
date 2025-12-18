package project3.server;

import project3.shared.Card;
import java.util.ArrayList;
import java.util.Collections;

//Representing 52 playing cards
public class Deck {
    private ArrayList<Card> cards;

    public Deck() {
        this.cards = new ArrayList<>();
        reset();
    }
    
    //Clears the deck and creates 52 new cards, then shuffles
    public void reset() {
        cards.clear();
        char[] suits = {'C', 'D', 'H', 'S'};
        for (char s : suits) {
            for (int r = 2; r <= 14; r++) {
                cards.add(new Card(s, r));
            }
        }
        Collections.shuffle(cards); //Shuffles
    }

    //Removes and draws top card from deck
    public Card draw() {
        if (cards.isEmpty()) return null;
        return cards.remove(0);
    }
}