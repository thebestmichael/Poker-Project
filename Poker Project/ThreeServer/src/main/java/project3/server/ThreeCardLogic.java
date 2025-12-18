package project3.server;

import project3.shared.Card;
import java.util.ArrayList;
import java.util.Comparator;

//Contains the rules for 3 card Poker, calculates results
public class ThreeCardLogic {

	//0 high card, 1 Pair, 2 Flush, 3 Straight, 4 3 of a kind, 5 Straight flush
    public static int evalHand(ArrayList<Card> hand) {
        if(hand == null || hand.size() != 3) return 0;
        hand.sort(Comparator.comparingInt(Card::getRank));
        
        boolean flush = hand.get(0).getSuit() == hand.get(1).getSuit() && hand.get(1).getSuit() == hand.get(2).getSuit();
        boolean straight = (hand.get(0).getRank() + 1 == hand.get(1).getRank()) && (hand.get(1).getRank() + 1 == hand.get(2).getRank());
        boolean threeKind = hand.get(0).getRank() == hand.get(1).getRank() && hand.get(1).getRank() == hand.get(2).getRank();
        boolean pair = hand.get(0).getRank() == hand.get(1).getRank() || hand.get(1).getRank() == hand.get(2).getRank();

        if (straight && flush) return 5;
        if (threeKind) return 4;
        if (straight) return 3;
        if (flush) return 2;
        if (pair) return 1;
        return 0;
    }

    //Calculates winnings, returns 0 if player loses bet
    public static int evalPPWinnings(ArrayList<Card> hand, int bet) {
        int rank = evalHand(hand);
        if (rank == 5) return bet * 40;
        if (rank == 4) return bet * 30;
        if (rank == 3) return bet * 6;
        if (rank == 2) return bet * 3;
        if (rank == 1) return bet * 1;
        return 0; 
    }
    
    //Compares dealer hand vs player hand, 0 Tie, 1 Dealer Wins, 2 Player Wins
    public static int compareHands(ArrayList<Card> dealer, ArrayList<Card> player) {
        int dRank = evalHand(dealer);
        int pRank = evalHand(player);

        if (dRank > pRank) return 1;
        if (pRank > dRank) return 2;

        for (int i = 2; i >= 0; i--) {
            if (dealer.get(i).getRank() > player.get(i).getRank()) return 1;
            if (player.get(i).getRank() > dealer.get(i).getRank()) return 2;
        }
        return 0; //Tie
    }
    
    //Checks if dealer qualifies with Queen high or better
    public static boolean isDealerQualified(ArrayList<Card> hand) {
        int rank = evalHand(hand);
        if (rank > 0) return true; 
        hand.sort(Comparator.comparingInt(Card::getRank));
        return hand.get(2).getRank() >= 12;
    }
}