package pokerBot;

public class Deck {

	private Card[] cards = new Card[52];
	
//	public int cardsDealt = 0;
	
	public Deck(){
		//initialize array
		for(int i=0;i<52;i++)
			cards[i] = new Card(i+1);//cards use nums from 1 to 52

		//start with shuffled deck
		shuffle();
	}
	
//	public Card getTopCard(){
//		cardsDealt++;
//		return cards[cardsDealt-1];
//	}
	
	public Card getCard(int i){
		return cards[i];
	}
	
	public void shuffle(){
		for(int i=0;i<52;i++){
			//choose random integer from current spot to the end
			int rand = (int) ( Math.random() * (52-i) + i );
			
			//swap current spot with random choice
			Card tmp = cards[rand];
			cards[rand] = cards[i];
			cards[i] = tmp;
			
		}
	}
	
}
