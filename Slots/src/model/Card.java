/**
 * Card Class - defines a card in a standard deck.
 */
package model;

/**
 * @author retro
 *
 */
public class Card {
	public final static int SPADES = 0;
	public final static int HEARTS = 1;
	public final static int DIAMONDS = 2;
	public final static int CLUBS = 3;
	public final static int JOKER = 4;
	
	public final static int JACK = 11;
	public final static int QUEEN = 12;
	public final static int KING = 13;
	public final static int ACE = 14;
	
	private final int suit;
	private final int rank;
	
	public Card(){ //create a new card. Default is Joker.
		suit = JOKER;
		rank = 1;
	}
	
	public Card(int s){ // Create a new card. Default is Joker if suit is specified as such.
		if(s == JOKER){ // Otherwise, not enough info to create a new card.
			suit = JOKER;
			rank = 1;
		} else {
			throw new IllegalArgumentException("Illegal number of parameters");
		}
	}
	
	public Card(int r, int s){ // Create a new card with specified rank and suit.
		if (s != SPADES && 
			s != HEARTS &&
			s != DIAMONDS &&
			s != CLUBS &&
			s != JOKER){
				throw new IllegalArgumentException("Illegal suit");
		}
		if (s != JOKER &&
			(r < 1 || r > 14)){
				throw new IllegalArgumentException("Illegal rank");
		}
		if (s == JOKER &&
			r != 1){
				throw new IllegalArgumentException("Illegal rank");
		}
		rank = r;
		suit = s;
	}
	
	public int getSuit(){ // return the suit of the current card. (int)
		return suit;
	}
	
	public int getRank(){ // return the rank of the current card. (int)
		return rank;
	}
	
	public String getSuitAsString(){ // return the suit of the current card as a String.
		switch(suit){
		case SPADES: return "S";
		case HEARTS: return "H";
		case DIAMONDS: return "D";
		case CLUBS: return "C";
		default: return "J";
		
		}
	}
	
	public String getRankAsString(){ // return the rank of the current card as a String.
		if(suit == JOKER){
			return "" + rank; //should always be 1
		} else {
			switch(rank){
			case 2: return "2";
			case 3: return "3";
			case 4: return "4";
			case 5: return "5";
			case 6: return "6";
			case 7: return "7";
			case 8: return "8";
			case 9: return "9";
			case 10: return "10";
			case JACK: return "J";
			case QUEEN: return "Q";
			case KING: return "K";
			default: return "A";
			}
		}
	}
	
	public String toString(){ // returns a String representation of the card.
		if(suit == JOKER){
			return "J";
		} else {
			return getRankAsString() + getSuitAsString();
		}
	}
	
	public Card random(){ // creates a random card and returns it.
		int randS = (int)(Math.random()*5);
		if (randS == JOKER){
			return new Card(); // Don't need to calculate rank if Joker.
		}
		int randR = (int)(Math.random()*13 + 2);
		return new Card(randR,randS);
	}
}
