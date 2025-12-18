package project3.server;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import project3.shared.Card;

class ThreeCardLogicTest {

    ArrayList<Card> hand;

    @BeforeEach
    void setUp() {
        hand = new ArrayList<>();
    }

    // Below are helpers in order to build rank
    
    // Straight Flush: 10, 9, 8 of Hearts this is rank 5
    private ArrayList<Card> buildStraightFlush() {
        ArrayList<Card> h = new ArrayList<>();
        h.add(new Card('H', 10));
        h.add(new Card('H', 9));
        h.add(new Card('H', 8));
        return h;
    }

    // Three of a Kind: 3 Queens this is rank 4
    private ArrayList<Card> buildThreeOfAKind() {
        ArrayList<Card> h = new ArrayList<>();
        h.add(new Card('C', 12));
        h.add(new Card('H', 12));
        h.add(new Card('S', 12));
        return h;
    }

    // Straight: 5, 6, 7 Mixed Suits this is rank 3
    private ArrayList<Card> buildStraight() {
        ArrayList<Card> h = new ArrayList<>();
        h.add(new Card('C', 5));
        h.add(new Card('H', 6));
        h.add(new Card('S', 7));
        return h;
    }

    // Flush: 2, 8, Jack of Spades this is rank 2
    private ArrayList<Card> buildFlush() {
        ArrayList<Card> h = new ArrayList<>();
        h.add(new Card('S', 2));
        h.add(new Card('S', 8));
        h.add(new Card('S', 11));
        return h;
    }

    // Pair: 2 Kings, 1 Nine this is rank 1
    private ArrayList<Card> buildPair() {
        ArrayList<Card> h = new ArrayList<>();
        h.add(new Card('C', 13));
        h.add(new Card('H', 13));
        h.add(new Card('S', 9));
        return h;
    }

    // High Card: Ace, 5, 2 Mixed this is rank 0
    private ArrayList<Card> buildHighCard() {
        ArrayList<Card> h = new ArrayList<>();
        h.add(new Card('C', 14)); // Ace
        h.add(new Card('H', 5));
        h.add(new Card('S', 2));
        return h;
    }
    
    // High Card: Jack High (Not Qualified)
    private ArrayList<Card> buildLowHighCard() {
        ArrayList<Card> h = new ArrayList<>();
        h.add(new Card('C', 11)); // Jack
        h.add(new Card('H', 5));
        h.add(new Card('S', 2));
        return h;
    }

    //tests below

    @Test
    @DisplayName("evalHand: Straight Flush returns 5")
    void testEvalHandStraightFlush() {
        assertEquals(5, ThreeCardLogic.evalHand(buildStraightFlush()));
    }

    @Test
    @DisplayName("evalHand: Three of a Kind returns 4")
    void testEvalHandThreeOfAKind() {
        assertEquals(4, ThreeCardLogic.evalHand(buildThreeOfAKind()));
    }

    @Test
    @DisplayName("evalHand: Straight returns 3")
    void testEvalHandStraight() {
        assertEquals(3, ThreeCardLogic.evalHand(buildStraight()));
    }

    @Test
    @DisplayName("evalHand: Flush returns 2")
    void testEvalHandFlush() {
        assertEquals(2, ThreeCardLogic.evalHand(buildFlush()));
    }

    @Test
    @DisplayName("evalHand: Pair returns 1")
    void testEvalHandPair() {
        assertEquals(1, ThreeCardLogic.evalHand(buildPair()));
    }

    @Test
    @DisplayName("evalHand: High Card returns 0")
    void testEvalHandHighCard() {
        assertEquals(0, ThreeCardLogic.evalHand(buildHighCard()));
    }

    // Pair plus winning tests below

    @Test
    @DisplayName("evalPPWinnings: Straight Flush pays 40:1")
    void testPPStraightFlush() {
        int bet = 5;
        // 40 * 5 = 200
        assertEquals(200, ThreeCardLogic.evalPPWinnings(buildStraightFlush(), bet));
    }

    @Test
    @DisplayName("evalPPWinnings: Three of a Kind pays 30:1")
    void testPPThreeOfAKind() {
        int bet = 10;
        // 30 * 10 = 300
        assertEquals(300, ThreeCardLogic.evalPPWinnings(buildThreeOfAKind(), bet));
    }

    @Test
    @DisplayName("evalPPWinnings: Straight pays 6:1")
    void testPPStraight() {
        int bet = 5;
        // 6 * 5 = 30
        assertEquals(30, ThreeCardLogic.evalPPWinnings(buildStraight(), bet));
    }

    @Test
    @DisplayName("evalPPWinnings: Flush pays 3:1")
    void testPPFlush() {
        int bet = 5;
        // 3 * 5 = 15
        assertEquals(15, ThreeCardLogic.evalPPWinnings(buildFlush(), bet));
    }

    @Test
    @DisplayName("evalPPWinnings: Pair pays 1:1")
    void testPPPair() {
        int bet = 25;
        // 1 * 25 = 25
        assertEquals(25, ThreeCardLogic.evalPPWinnings(buildPair(), bet));
    }

    @Test
    @DisplayName("evalPPWinnings: High Card loses (returns 0)")
    void testPPLose() {
        int bet = 5;
        assertEquals(0, ThreeCardLogic.evalPPWinnings(buildHighCard(), bet));
    }

    // compare hands test below

    @Test
    @DisplayName("compareHands: Dealer higher rank wins")
    void testCompareRank() {
        ArrayList<Card> dealer = buildFlush(); // Rank 2
        ArrayList<Card> player = buildPair();  // Rank 1
        assertEquals(1, ThreeCardLogic.compareHands(dealer, player));
    }

    @Test
    @DisplayName("compareHands: Player higher rank wins")
    void testCompareRankPlayerWins() {
        ArrayList<Card> dealer = buildPair();          // Rank 1
        ArrayList<Card> player = buildStraight();      // Rank 3
        assertEquals(2, ThreeCardLogic.compareHands(dealer, player));
    }

    @Test
    @DisplayName("compareHands: Same Rank (Pair) - Dealer has higher Pair")
    void testCompareSameRankDealerWins() {
        // Dealer: Pair of Kings
        ArrayList<Card> dealer = buildPair(); 
        
        // Player: Pair of Queens
        ArrayList<Card> player = new ArrayList<>();
        player.add(new Card('D', 12));
        player.add(new Card('S', 12));
        player.add(new Card('H', 2));
        
        assertEquals(1, ThreeCardLogic.compareHands(dealer, player));
    }

    @Test
    @DisplayName("compareHands: Same Rank (Pair) - Player has higher Pair")
    void testCompareSameRankPlayerWins() {
        // Dealer: Pair of Queens
        ArrayList<Card> dealer = new ArrayList<>();
        dealer.add(new Card('D', 12));
        dealer.add(new Card('S', 12));
        dealer.add(new Card('H', 2));

        // Player: Pair of Kings
        ArrayList<Card> player = buildPair();
        
        assertEquals(2, ThreeCardLogic.compareHands(dealer, player));
    }

    @Test
    @DisplayName("compareHands: Exact Tie")
    void testCompareTie() {
        ArrayList<Card> dealer = buildStraight();
        ArrayList<Card> player = buildStraight(); // Exactly same rank values
        assertEquals(0, ThreeCardLogic.compareHands(dealer, player));
    }

    //dealer qualified tests below

    @Test
    @DisplayName("isDealerQualified: Queen High qualifies")
    void testDealerQualifiesQueen() {
        ArrayList<Card> h = new ArrayList<>();
        h.add(new Card('H', 12)); // Queen
        h.add(new Card('D', 3));
        h.add(new Card('S', 2));
        assertTrue(ThreeCardLogic.isDealerQualified(h));
    }

    @Test
    @DisplayName("isDealerQualified: King High qualifies")
    void testDealerQualifiesKing() {
        ArrayList<Card> h = new ArrayList<>();
        h.add(new Card('H', 13)); // King
        h.add(new Card('D', 3));
        h.add(new Card('S', 2));
        assertTrue(ThreeCardLogic.isDealerQualified(h));
    }

    @Test
    @DisplayName("isDealerQualified: Ace High qualifies")
    void testDealerQualifiesAce() {
        // Reuse buildHighCard which has an Ace
        assertTrue(ThreeCardLogic.isDealerQualified(buildHighCard()));
    }

    @Test
    @DisplayName("isDealerQualified: Jack High does NOT qualify")
    void testDealerFail() {
        // Jack high, 5, 2
        assertFalse(ThreeCardLogic.isDealerQualified(buildLowHighCard()));
    }
    
    @Test
    @DisplayName("isDealerQualified: Any Pair (Rank 1) always qualifies")
    void testDealerQualifiesWithPair() {
        // Pair of 2s (lowest pair)
        ArrayList<Card> h = new ArrayList<>();
        h.add(new Card('H', 2));
        h.add(new Card('D', 2));
        h.add(new Card('S', 5));
        assertTrue(ThreeCardLogic.isDealerQualified(h));
    }
}