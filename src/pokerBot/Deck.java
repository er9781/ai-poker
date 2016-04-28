package pokerBot;

import java.util.Arrays;
import java.util.Collections;

public class Deck {

	private Card[] cards;
	private Card[] excludedCards;
	
//	public int cardsDealt = 0;
	
	/**
	 * Create a new deck with all 52 cards.
	 * The deck begins shuffled.
	 */
	public Deck(){
		this(null);
	}
	
	/**
	 * Creates a new deck with all cards except those specified by excludedCards.
	 * New decks begin shuffled.
	 * @param excludedCards an array of cards to be excluded from the new deck
	 */
	public Deck(Card[] excludedCards){
		//initialize the excludedCards field
		if(excludedCards == null){
			this.excludedCards = new Card[0];
		}else{
			this.excludedCards = excludedCards.clone();
		}
		
		//get the number values for the excluded cards for use when creating the cards.
		int[] excludedCardNums = new int[this.excludedCards.length];
		for(int i = 0; i < this.excludedCards.length; i++){
			excludedCardNums[i] = this.excludedCards[i].getNum();
		}
		Arrays.sort(excludedCardNums);
		
		//create the cards array
		cards = new Card[52 - this.excludedCards.length];
		
		//loop through the deck and set the cards to be the next non-excluded card.
		int numExcluded = 0;
		for(int i = 0; i < 52 ; i++ ){
			//if the card is in the excluded list, then skip over the current card.
			if(numExcluded < excludedCardNums.length && i == excludedCardNums[numExcluded]){
				numExcluded++;
			}else{
				//insert the new card
				cards[i - numExcluded] = new Card(i+1);
			}
		}
		
		//start with the deck shuffled
		this.shuffle();
	}
	
//	public Card getTopCard(){
//		cardsDealt++;
//		return cards[cardsDealt-1];
//	}
	
	/**
	 * Returns the card at the given index.
	 * @param index
	 * @return
	 */
	public Card getCard(int index){
		return cards[index];
	}
	
	/**
	 * Shuffles the deck.
	 */
	public void shuffle(){
		for(int i=0;i<cards.length;i++){
			//choose random integer from current spot to the end
			int rand = (int) ( Math.random() * (cards.length-i) + i );
			
			//swap current spot with random choice
			Card tmp = cards[rand];
			cards[rand] = cards[i];
			cards[i] = tmp;
			
		}
	}
	
	/**
	 * randomly returns a subset of the deck without changing the object in place. returns references to the cards sampled (cards are read-only so it's not an issue)
	 * @param numCards
	 * @return returns null if the requested sample is larger than the deck and returns array of length 0 if the requested number of cards is 0
	 */
	public Card[] sample(int numCards){
		if(numCards > cards.length){
			return null;
		}
		Card[] sample = new Card[numCards];
		int[] indices = Utilities.getIndices(cards);
		int rand;
		for(int i = 0; i < numCards; i++){
			rand = (int)(Math.random() * (cards.length - i));
			int tmp = indices[i];
			indices[i] = indices[rand];
			indices[rand] = tmp;
			sample[i] = cards[indices[i]];
		}
		
		return sample;
	}
	
	public Deck clone(){
		return new Deck(excludedCards);
	}
	
	public String toString(){
		return "Deck of "+cards.length+" card(s)";
	}
	
}
