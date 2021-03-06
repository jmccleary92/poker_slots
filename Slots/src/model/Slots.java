/**
 * Slots Class - defines a single reel in the poker slot game.
 */
package model;

import java.util.ArrayList;

/**
 * @author retro
 *
 */
public class Slots {
	// indicates row height. Should match number of rows.
	private final static int HIGH = 0;
	private final static int MID = 1;
	private final static int LOW = 2;
	
	// size of the slot machine. 3x5 is pretty standard.
	private final static int ROWS = 3;
	private final static int COLUMNS = 5;
	private final static int TOTAL_BET_LINES = (int) Math.pow(ROWS, COLUMNS); // total possible bet lines
	
	private final static int SHUFFLES = 3; // default number of times to shuffle the cards.
	
	// hand types
	private final static int HIGH_CARD = -1;
	private final static int ONE_PAIR = 0;
	private final static int TWO_PAIR = 1;
	private final static int THREE_OF_A_KIND = 2;
	private final static int STRAIGHT = 3;
	private final static int FLUSH = 4;
	private final static int FULL_HOUSE = 5;
	private final static int FOUR_OF_A_KIND = 6;
	private final static int STRAIGHT_FLUSH = 7;
	private final static int ROYAL_FLUSH = 8;
	
	// initialize game values
	private int betLines = 0;
	private int bet = 0;
	private int balance = 0;
	private int autoSpin = 0;
	
	// default game values
	private final static int initBetLines = 1;
	private final static int initBet = 1;
	private final static int initBalance = 1000;
	private final static int initAutoSpin = 0;
	
	
	private Card[][] reels = new Card[COLUMNS][ROWS]; // defines the reels on the game
	private SlotsDeck deck = new SlotsDeck(); // create a new deck of cards to deal
	
	private ArrayList<ArrayList<Integer>> blt = new ArrayList<ArrayList<Integer>>(); // the bet lines table.
	
	public static void main(String[] args){
		Slots game0 = new Slots();
		System.out.println("Welcome to Josh's Poker Slots!");
		System.out.println("You are starting with a balance of $" + game0.balance);
		System.out.println("You are betting $" + game0.bet + " on " + game0.betLines + " bet lines");
		System.out.println("for a total bet of $" + game0.bet*game0.betLines);
		System.out.println("You have " + game0.autoSpin + " more automatic spins");
	}
	
	public Slots(){ // create a new game of slots.
		betLines = initBetLines;
		bet = initBet;
		balance = initBalance;
		autoSpin = initAutoSpin;
		blt = initializeBetlineTable();
	}

	private Slots(int bl, int b, int ba, int as){ // debug method to create a custom game
		betLines = bl;
		bet = b;
		balance = ba;
		autoSpin = as;
		blt = initializeBetlineTable();
	}
	
	public void spin(){ // simulates one "pull" of the "lever"
		if(betLines >= 1 &&
		   bet >= 1 &&
		   balance >= (bet*betLines)){ // make sure there is enough money for this to be ok.
			balance -= bet*betLines;
			deck.shuffle(SHUFFLES); // shuffle the deck then deal them out.
			for(int i = 0; i < COLUMNS; i++){
				for(int j = 0; j < ROWS; j++){
					reels[i][j] = deck.deal();
				}
			}
			addWinnings(betLines); // add money to the balance in accordance with bet lines.
		} else {
			System.out.println("Insufficient funds");
		}
	}

	private int addWinnings(int lines){ // add money to the balance in accordance with bet lines.
		int winnings = 0;
		for(int i = 0; i < lines; i++){ // for each bet line
			ArrayList<Integer> line = blt.get(i);
			ArrayList<Card> hand = new ArrayList<>();
			for(int j = 0; j < line.size(); j++){ // for each reel on the win line
				Card a = reels[j][line.get(j)]; // get the card at each reel on the win-line
				hand.add(a); // add it to a hand
			}
			winnings += winnings(hand); // add winnings from each hand on each line
		}
		System.out.println("You gained $" + winnings);
		return winnings;
	}
	
	private int winnings(ArrayList<Card> hand){ // returns the value of the specified hand.
		ArrayList<Integer> handType = handType(hand); // get the type of hand
		return handValue(handType); // get the value of the hand
	}
	
	private ArrayList<Integer> handType(ArrayList<Card> hand){ // determines the type of poker hand presented.
		// put the hand in order
		orderHand(hand);
		
		int jokerCount = 0;
		for(int i = 0; i < hand.size(); i++){ // check each card in the hand for Joker
			Card cardA = hand.get(i);
			if(cardA.getSuit() == Card.JOKER){
				jokerCount++;
			}
		} // we now know how many jokers in the hand.
		
		ArrayList<Integer> type = new ArrayList<>();
		int winRank = 1;
		
		// winRank gets the winning(non-joker) rank card in hand
		// winRank applies to 1 pair, 2 pair, 3 of a kind, full house, and 4 of a kind.
		
		if(royalFlush(hand,jokerCount)){ // check for a royal flush
			type.add(ROYAL_FLUSH);
		} else if(straightFlush(hand,jokerCount)){ // check for a straight flush
			type.add(STRAIGHT_FLUSH);
		} else if(fourOfAKind(hand,jokerCount)){ // check for a 4 of a kind
			type.add(FOUR_OF_A_KIND);
			winRank = winRank(hand,FOUR_OF_A_KIND);
			type.add(winRank);
		} else if(fullHouse(hand,jokerCount)){ // check for a full house
			type.add(FULL_HOUSE);
			winRank = winRank(hand,FULL_HOUSE);
			type.add(winRank);
		} else if(flush(hand,jokerCount)){ // check for a flush
			type.add(FLUSH);
		} else if(straight(hand,jokerCount)){ // check for a straight
			type.add(STRAIGHT);
		} else if(threeOfAKind(hand,jokerCount)){ // check for a 3 of a kind
			type.add(THREE_OF_A_KIND);
			winRank = winRank(hand,THREE_OF_A_KIND);
			type.add(winRank);
		} else if(twoPair(hand,jokerCount)){ // check for a 2 pair
			type.add(TWO_PAIR);
			winRank = winRank(hand,TWO_PAIR);
			type.add(winRank);
		} else if(pair(hand,jokerCount)){ // check for a 1 pair
			type.add(ONE_PAIR);
			winRank = winRank(hand,ONE_PAIR);
			type.add(winRank);
		}
		return type; // returns an empty list if no winning hand.
	}
	
	private int winRank(ArrayList<Card> hand, int type) { // given a hand and type,
		// determine the rank of the top winning card. This method assumes the type is valid.
		switch (type){// type can be 1 or 2 pair. 3 or 4 of a kind. or full house.
		case ONE_PAIR:
			if(hand.get(0).getSuit() == Card.JOKER){ // there can only be one Joker for there to be a pair win.
				return hand.get(1).getRank(); // return the highest non-Joker card
			} else { // if no Jokers, we need to see which cards have the same rank. iterate
				int i = 0;
				Card a = new Card();
				Card b;
				while(i < hand.size()){ // iterate through the hand
					a = hand.get(i);
					b = hand.get(i+1);
					if(a.getRank() != b.getRank()){ // check the ranks
						i++;
					} else {
						break;
					}
				}
				return a.getRank();
			}
		case TWO_PAIR:
			int i = 0; // no jokers possible for this type.
			Card a = new Card();
			Card b;
			while(i < hand.size()){
				a = hand.get(i);
				b = hand.get(i+1);
				if(a.getRank() != b.getRank()){ // iterate through the hand
					i++;
				} else {
					break;
				}
			}
			return a.getRank();
		case THREE_OF_A_KIND:
			if(hand.get(0).getSuit() == Card.JOKER){ // There could be up to two Jokers for this hand type
				if(hand.get(1).getSuit() == Card.JOKER){ // if there are two Jokers, just find the highest card rank.
					return hand.get(2).getRank();
				}
			} else { // one or no Jokers - either way, there will be two of the same rank present
				int j = 0;
				Card c = new Card();
				Card d;
				while(j < hand.size()){
					c = hand.get(j);
					d = hand.get(j+1);
					if(c.getRank() != d.getRank()){ // iterate through the hand
						j++;
					} else {
						break;
					}
				}
				return c.getRank();
			}
		case FOUR_OF_A_KIND:
			if(hand.get(0).getSuit() == Card.JOKER) { // There could be up to 3 Jokers for this hand type
				if(hand.get(2).getSuit() == Card.JOKER) { // if there are three Jokers, just find the highest rank.
					return hand.get(3).getRank();
				}
			} else { // 0, 1, or 2 Jokers - there should be at least two of the same rank.
				int j = 0;
				Card c = new Card();
				Card d;
				while(j < hand.size()){
					c = hand.get(j);
					d = hand.get(j+1);
					if(c.getRank() != d.getRank()){ // iterate through the hand
						j++;
					} else {
						break;
					}
				}
				return c.getRank();
			}
		case FULL_HOUSE: // should be the same as Three of a Kind
			if(hand.get(0).getSuit() == Card.JOKER){ // There could be up to two Jokers for this hand type
				if(hand.get(1).getSuit() == Card.JOKER){ // if there are two Jokers, just find the highest card rank.
					return hand.get(2).getRank();
				}
			} else { // one or no Jokers - either way, there will be two of the same rank present
				int j = 0;
				Card c = new Card();
				Card d;
				while(j < hand.size()){
					c = hand.get(j);
					d = hand.get(j+1);
					if(c.getRank() != d.getRank()){ // iterate through the hand
						j++;
					} else {
						break;
					}
				}
				return c.getRank();
			}
		}
		return -1; // default case if errors occur
	}

	private boolean pair(ArrayList<Card> hand, int jokerCount) {
		if(jokerCount > 1){ // more than 1 Joker means at least 3 of a kind
			return false;
		} else if(jokerCount == 1){ // must be a match with only one Joker
			return true;
		} else { // must be dealt only a single pair
			int i = 0;
			Card a = hand.get(i);
			int rank = a.getRank();
			while(a.getRank()!= rank && a != null){ // iterate through the hand
				i++;
				a = hand.get(i);
			}
			if(a == null){
				return false;
			}
			return true;
		}
	}

	private boolean twoPair(ArrayList<Card> hand, int jokerCount) {
		if(jokerCount > 0){ // must be dealt two pair
			return false;
		} else {
			int i = 0;
			int count = 0;
			Card a = hand.get(i);
			Card b = hand.get(i+1);
			while(b != null && count < 2){ // keep track of pairs while iterating
				if(a.getRank() == b.getRank()){
					count++;
					i++;
				}
				i++;
				a = hand.get(i);
				b = hand.get(i+1);
			}
			if(count == 2){
				return true;
			}
			return false;
		}
	}

	private boolean threeOfAKind(ArrayList<Card> hand, int jokerCount) {
		if(jokerCount > 2){ // more than 2 Jokers means at least 4 of a kind
			return false;
		} else if(jokerCount == 2){ // matches any card in hand with 2 Jokers
			return true;
		} else if (jokerCount == 1){ // take out the Joker and find a pair
			ArrayList<Card> newHand = hand;
			newHand.remove(0);
			return pair(newHand,0);
		} else { // check all options for spans of 3 with the same rank
			if(hand.get(0) != null && hand.get(2) != null && hand.get(0).getRank() == hand.get(2).getRank() ||
			   hand.get(2) != null && hand.get(4) != null && hand.get(2).getRank() == hand.get(4).getRank() ||
			   hand.get(1) != null && hand.get(3) != null && hand.get(1).getRank() == hand.get(3).getRank()){
				return true;
			}
		}
		return false;
	}

	private boolean straight(ArrayList<Card> hand, int jokerCount) {
		if(jokerCount > 3){
			return false; // more than 3 jokers means either a straight flush or royal flush
		} else {
			int i = jokerCount;
			int j = i;
			Card a = hand.get(j);
			Card b = hand.get(j+1);
			while(b != null && i >= 0){
				int diff = a.getRank() - b.getRank() - 1;
				if(diff > i){ // can't be connected by Jokers
					return false;
				} else {
					i -= diff; // take out as many Jokers as needed to make the straight
					j++; // go to next cards
					a = b;
					b = hand.get(j+1);
				}
			} // If we got through the whole hand, then we have a straight!
			return true;
		}
	}

	private boolean flush(ArrayList<Card> hand, int jokerCount) {
		if(jokerCount > 3){
			return false; // more than 3 Jokers means a straight or royal flush
		} else {
			int i = jokerCount;
			Card a = hand.get(i);
			int suit = a.getSuit();
			while(a != null){
				if(a.getSuit() != suit){ // cards not the same suit
					return false;
				} else {
					i++; // go to the next cards
					a = hand.get(i);
				}
			} // If we got through the whole hand, then we have a flush!
			return true;
		}
	}

	private boolean fullHouse(ArrayList<Card> hand, int jokerCount) {
		if(jokerCount > 1){
			return false; // more than 1 Joker means at least four of a kind
		} else if(jokerCount == 1){ // must be two pair with a Joker
			ArrayList<Card> newHand = hand;
			newHand.remove(0); // remove the Joker from the hand and check for 2 pair
			return twoPair(newHand,0);
		} else { // no Jokers. Must be a full-dealt hand
			Card a = hand.get(0);
			Card b = hand.get(1);
			if(a.getRank() != b.getRank()){ // first two cards must match
				return false;
			} else {
				a = hand.get(3);
				b = hand.get(4);
				if(a.getRank() != b.getRank()){ // last two cards must match
					return false;
				} else {
					a = hand.get(0);
					if (hand.get(2).getRank() != a.getRank() &&
						hand.get(2).getRank() != b.getRank()){ // middle card must match something
						return false;
					} else { // if we get here, we have a full house!
						return true;
					}
				}
			}
		}
	}

	private boolean fourOfAKind(ArrayList<Card> hand, int jokerCount) {
		if(jokerCount > 3){ // more than 3 Jokers means a straight or royal flush
			return false;
		} else if(jokerCount == 3){ // can match any other card in the hand
			return true;
		} else if(jokerCount == 2){ // 2 Jokers can match a pair in the hand
			ArrayList<Card> newHand = hand;
			newHand.remove(0);
			newHand.remove(0);
			return pair(newHand,0);
		} else if(jokerCount == 1){ // 1 Joker can match a 3 of a kind in the hand
			ArrayList<Card> newHand = hand;
			newHand.remove(0);
			return threeOfAKind(newHand,0);
		} else if(hand.get(0) != null && hand.get(3) != null && hand.get(0).getRank() == hand.get(3).getRank() ||
			      hand.get(1) != null && hand.get(4) != null && hand.get(1).getRank() == hand.get(4).getRank()){
			return true; // no Jokers. Must be dealt
		} else {
			return false;
		}
	}

	private boolean straightFlush(ArrayList<Card> hand, int jokerCount) {
		if(jokerCount > 4){ // more than 4 Jokers means an automatic royal flush
			return false;
		} else {
			// first check if its a flush
			int i = jokerCount;
			Card a = hand.get(i);
			int suit = a.getSuit();
			while(a != null){
				if(a.getSuit() != suit){ // cards not the same suit
					return false;
				} else {
					i++; // go to the next cards
					a = hand.get(i);
				}
			} // If we got through the whole hand, then we have a flush!
			
			// now check for the straight
			i = jokerCount;
			int j = i;
			a = hand.get(j);
			Card b = hand.get(j+1);
			while(b != null && i >= 0){
				int diff = a.getRank() - b.getRank() - 1;
				if(diff > i){ // can't be connected by Jokers
					return false;
				} else {
					i -= diff; // take out as many Jokers as needed to make the straight
					j++; // go to next cards
					a = b;
					b = hand.get(j+1);
				}
			} // if we make it here, it's a straight flush!
		}
		return true;
	}

	private boolean royalFlush(ArrayList<Card> hand, int jokerCount) {
		if(jokerCount == 5){ // automatic royal flush
			return true;
		} else { // need to iterate through the hand and see what we have
			// all non-Jokers must be rank 10-A
			if(hand.get(4).getRank() < 10){ // cards in order. last must be > 10 to ensure rank
				return false;
			} else {
				boolean have10 = false;
				boolean haveJ = false;
				boolean haveQ = false;
				boolean haveK = false;
				boolean haveA = false;
				int i = jokerCount;
				Card a = hand.get(i);
				int suit = a.getSuit();
				while(a != null){ // iterate through the hand and ensure we have only 1 of each type
					switch(a.getRank()){
					case 10:
						if(!have10 && a.getSuit() == suit){
							have10 = true;
						} else {
							return false;
						}
					case Card.JACK:
						if(!haveJ && a.getSuit() == suit){
							haveJ = true;
						} else {
							return false;
						}
					case Card.QUEEN:
						if(!haveQ && a.getSuit() == suit){
							haveQ = true;
						} else {
							return false;
						}
					case Card.KING:
						if(!haveK && a.getSuit() == suit){
							haveK = true;
						} else {
							return false;
						}
					case Card.ACE:
						if(!haveA && a.getSuit() == suit){
							haveA = true;
						} else {
							return false;
						}
					}
					i++;
					a = hand.get(i);
				}
				return true;
			}
		}
	}

	private void orderHand(ArrayList<Card> hand){ // puts a poker hand in Rank order.
		int size = hand.size();
		for(int i = 0; i < size-1; i++){ // for each card in the hand, check it's suit and rank to order them
			Card cardA = hand.get(i);
			Card cardB = hand.get(i+1);
			if(cardA.getSuit() != Card.JOKER){ // check if either is a Joker
				if(cardB.getSuit() == Card.JOKER || cardA.getRank() < cardB.getRank()){ //if so, put them in front.
					Card temp = cardA;// if card b is a Joker and a is not, swap them.
					hand.set(i, cardB); // or if card b has a higher rank.
					hand.set(i+1, temp);
				} // if neither above case is true, then we are fine!
			}
		}// by this point, we should have the hand in sorted order, from rank high to low.
	}
	
	private int handValue(ArrayList<Integer> type){ // calculates the value of the hand based on type and win rank
		int value = 0;
		if(type.get(0) != null){
			switch (type.get(0)){ // gets the type of poker hand. default is high card. (no win)
			// value based on probability of poker hand
			case ONE_PAIR: value = 2 + type.get(1); break;
			case TWO_PAIR: value = 20 + type.get(1); break;
			case THREE_OF_A_KIND: value = 50 + type.get(1); break;
			case STRAIGHT: value = 250; break;
			case FLUSH: value = 500; break;
			case FULL_HOUSE: value = 700 + 10 * type.get(1); break;
			case FOUR_OF_A_KIND: value = 4200 + 100 * type.get(1); break;
			case STRAIGHT_FLUSH: value = 72000; break;
			case ROYAL_FLUSH: value = 650000; break;
			default: break;
			}
		}
		return value;
	}
	
	private ArrayList<ArrayList<Integer>> initializeBetlineTable() { // creates the bet lines
		ArrayList<ArrayList<Integer>> table = new ArrayList<>(); // there are only 35 "good" ones.
		// line 1
		ArrayList<Integer> line = new ArrayList<>();
		for(int i = 0; i < 5; i++){
			line.add(MID);
		}
		table.add(line);
		line.clear();
		// line 2
		for(int i = 0; i < 5; i++){
			line.add(HIGH);
		}
		table.add(line);
		line.clear();
		// line 3
		for(int i = 0; i < 5; i++){
			line.add(LOW);
		}
		table.add(line);
		line.clear();
		// line 4
		line.add(MID);
		line.add(MID);
		line.add(HIGH);
		line.add(MID);
		line.add(MID);
		table.add(line);
		line.clear();
		// line 5
		line.add(MID);
		line.add(MID);
		line.add(LOW);
		line.add(MID);
		line.add(MID);
		table.add(line);
		line.clear();
		// line 6
		line.add(HIGH);
		line.add(HIGH);
		line.add(MID);
		line.add(HIGH);
		line.add(HIGH);
		table.add(line);
		line.clear();
		// line 7
		line.add(LOW);
		line.add(LOW);
		line.add(MID);
		line.add(LOW);
		line.add(LOW);
		table.add(line);
		line.clear();
		// line 8
		line.add(MID);
		line.add(HIGH);
		line.add(MID);
		line.add(HIGH);
		line.add(MID);
		table.add(line);
		line.clear();
		// line 9
		line.add(MID);
		line.add(LOW);
		line.add(MID);
		line.add(LOW);
		line.add(MID);
		table.add(line);
		line.clear();
		// line 10
		line.add(HIGH);
		line.add(MID);
		line.add(HIGH);
		line.add(MID);
		line.add(HIGH);
		table.add(line);
		line.clear();
		// line 11
		line.add(LOW);
		line.add(MID);
		line.add(LOW);
		line.add(MID);
		line.add(LOW);
		table.add(line);
		line.clear();
		// line 12
		line.add(MID);
		line.add(HIGH);
		line.add(HIGH);
		line.add(HIGH);
		line.add(MID);
		table.add(line);
		line.clear();
		// line 13
		line.add(MID);
		line.add(LOW);
		line.add(LOW);
		line.add(LOW);
		line.add(MID);
		table.add(line);
		line.clear();
		// line 14
		line.add(HIGH);
		line.add(MID);
		line.add(MID);
		line.add(MID);
		line.add(HIGH);
		table.add(line);
		line.clear();
		// line 15
		line.add(LOW);
		line.add(MID);
		line.add(MID);
		line.add(MID);
		line.add(LOW);
		table.add(line);
		line.clear();
		// line 16
		line.add(MID);
		line.add(HIGH);
		line.add(MID);
		line.add(LOW);
		line.add(MID);
		table.add(line);
		line.clear();
		// line 17
		line.add(MID);
		line.add(LOW);
		line.add(MID);
		line.add(HIGH);
		line.add(MID);
		table.add(line);
		line.clear();
		// line 18
		line.add(HIGH);
		line.add(MID);
		line.add(LOW);
		line.add(MID);
		line.add(HIGH);
		table.add(line);
		line.clear();
		// line 19
		line.add(LOW);
		line.add(MID);
		line.add(HIGH);
		line.add(MID);
		line.add(LOW);
		table.add(line);
		line.clear();
		// line 20
		line.add(HIGH);
		line.add(HIGH);
		line.add(MID);
		line.add(LOW);
		line.add(LOW);
		table.add(line);
		line.clear();
		// line 21
		line.add(LOW);
		line.add(LOW);
		line.add(MID);
		line.add(HIGH);
		line.add(HIGH);
		table.add(line);
		line.clear();
		// line 22
		line.add(HIGH);
		line.add(MID);
		line.add(MID);
		line.add(MID);
		line.add(LOW);
		table.add(line);
		line.clear();
		// line 23
		line.add(LOW);
		line.add(MID);
		line.add(MID);
		line.add(MID);
		line.add(HIGH);
		table.add(line);
		line.clear();
		// line 24
		line.add(HIGH);
		line.add(HIGH);
		line.add(LOW);
		line.add(HIGH);
		line.add(HIGH);
		table.add(line);
		line.clear();
		// line 25
		line.add(LOW);
		line.add(LOW);
		line.add(HIGH);
		line.add(LOW);
		line.add(LOW);
		table.add(line);
		line.clear();
		// line 26
		line.add(HIGH);
		line.add(LOW);
		line.add(MID);
		line.add(LOW);
		line.add(HIGH);
		table.add(line);
		line.clear();
		// line 27
		line.add(LOW);
		line.add(HIGH);
		line.add(MID);
		line.add(HIGH);
		line.add(LOW);
		table.add(line);
		line.clear();
		// line 28
		line.add(HIGH);
		line.add(LOW);
		line.add(HIGH);
		line.add(LOW);
		line.add(HIGH);
		table.add(line);
		line.clear();
		// line 29
		line.add(LOW);
		line.add(HIGH);
		line.add(LOW);
		line.add(HIGH);
		line.add(LOW);
		table.add(line);
		line.clear();
		// line 30
		line.add(HIGH);
		line.add(LOW);
		line.add(LOW);
		line.add(LOW);
		line.add(HIGH);
		table.add(line);
		line.clear();
		// line 31
		line.add(LOW);
		line.add(HIGH);
		line.add(HIGH);
		line.add(HIGH);
		line.add(LOW);
		table.add(line);
		line.clear();
		// line 32
		line.add(HIGH);
		line.add(LOW);
		line.add(MID);
		line.add(HIGH);
		line.add(LOW);
		table.add(line);
		line.clear();
		// line 33
		line.add(LOW);
		line.add(HIGH);
		line.add(MID);
		line.add(LOW);
		line.add(HIGH);
		table.add(line);
		line.clear();
		// line 34
		line.add(MID);
		line.add(HIGH);
		line.add(LOW);
		line.add(HIGH);
		line.add(MID);
		table.add(line);
		line.clear();
		// line 35
		line.add(MID);
		line.add(LOW);
		line.add(HIGH);
		line.add(LOW);
		line.add(MID);
		table.add(line);
		line.clear();
		return table;
	}
	
	public static int size() { // returns the number of positions in the slot machine.
		return ROWS * COLUMNS;
	}
	
	public static int getColumns() { // returns the number of columns in the slot machine.
		// will typically be 5 as a poker hand is 5 cards.
		return COLUMNS;
	}
	
	public static int getRows() { // returns the number of rows in the slot machine. 3 is default.
		return ROWS;
	}
}
