/**
 * Represents a deck of cards to use for slots.
 */
package model;

import java.util.ArrayList;

/**
 * @author retro
 *
 */
public class SlotsDeck {
	private ArrayList<Card> deck = new ArrayList<Card>(); //52 standard cards plus Jokers
	
	public SlotsDeck(){
		for(int j = 0; j < 4; j++){ //creates standard cards
			for(int k = 2; k <= Card.ACE; k++){
				deck.add(new Card(k,j));
			}
		}
		for(int i = 0; i < (Slots.size() + Slots.getColumns()); i++){
			deck.add(new Card());//should fill the rest of the deck with Jokers plus some extra
			// This will allow a small margin for a full board of Jokers.
		}
	}
	
	public SlotsDeck(ArrayList<Card> deck){//creates a deck of cards from another deck.
		this.deck = deck;
	}
	
	public ArrayList<Card> shuffle(){ // just shuffles the deck once.
		return this.shuffle(1);
	}
	
	public ArrayList<Card> shuffle(int t){
		if(t < 1 || t > 10){
			throw new IllegalArgumentException("Illegal number of shuffles");
		} else {
			if(!deck.isEmpty()){ // make sure we aren't shuffling nothing.
				Card a;
				for(int j = 0; j < t; j++){ // shuffle the number of times specified.
					for(int i = 0; i < deck.size()-1; i++){ // for each spot in the deck. Shuffle stuff
						a = deck.get(i);
						int pos = (int)(Math.random()*(deck.size()-i-1)+i+1);//get a random position in the deck after i
						deck.set(i, deck.get(pos));
						deck.set(pos, a);//do magic swapping stuff
					}
				}
			}
			return deck;
		}
	}
	
	public String toString(){ // print the deck out nice and pretty.
		String s = "";
		if(!deck.isEmpty()){ // make sure we aren't printing nothing.
			for(int i = 0; i < (int)Math.ceil((deck.size()/6)); i++){// print the minimum number of rows.
				for(int j = 0; j < 6; j++){ //print 6 cards per row
					// need to check if there are still cards left
					Card nextCard = deck.get((6*i)+j);
					if(nextCard != null){
						if(nextCard.getSuit() == Card.JOKER){
							s = s + nextCard.toString() + "   "; // three spaces for Jokers
						} else {
							s = s + nextCard.toString() + "  "; // two spaces for other cards
						}
					}
				}
				s = s.trim();
				s = s + "\n";//trim the end and start another row
			}
			return s;
		} else {
			throw new IllegalArgumentException("Deck is empty");
		}
	}
	
	public Card deal(){//deals the first card off the "top" and removes it from the deck.
		if(!deck.isEmpty()){
			Card c = deck.get(0);
			deck.remove(0);
			return c;
		} else {
			throw new IllegalArgumentException("Deck is empty");
		}
	}
	
	public Card deal(Card c){//deals the first instance of the specified card off the deck, if possible.
		if(deck.contains(c)){
			deck.remove(c);
			return c;
		} else {
			throw new IllegalArgumentException("Card not in deck");
		}
	}
}
