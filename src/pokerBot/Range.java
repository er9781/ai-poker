package pokerBot;

import java.util.ArrayList;

import equityCalculations.EquityBreakdown;

/**
 * This is a data structure to represent an opponent's range. It is a fixed length double array which has 
 * a function to convert a pair of cards into an index. This simply gives access by using cards as an index.
 * 
 * Should include later different tranversing methods and metrics on the range.
 * @author Simon
 *
 */
public class Range {

	/*
	 * The pairs are sorted based on their cardNums. Starting with (1,2), then (1,3) for all the 1s, then up to (2,3) and so on.
	 */
	private double[] array = new double[52*51/2];
	
	public Range(){
		//initialize the range to equal probabilities
		for(int i=0;i<array.length;i++)
			array[i] = 1.0/array.length;
	}
	
	/**
	 * Sets all range elements containing any of the given cards to 0 (cannot both have that card)
	 * @param cs
	 */
	public void setToZeroDuplicates(ArrayList<Card> cs){
		for(Card c:cs)
			setToZeroDuplicates(c);
	}
	/**
	 * Sets all range elements containing any of the given cards to 0 (cannot both have that card)
	 * @param cs
	 */
	public void setToZeroDuplicates(Card c){
		for(int i=0;i<array.length;i++){
			Card[] tmp = indexToCards(i);
			if(tmp[0] == c || tmp[1] == c)
				array[i] = 0;
		}
	}
	
	/**
	 * returns the entry at the given cards
	 * @param a pair of whole cards
	 * @return
	 */
	public double get(Card[] cards){
		return array[cardsToIndex(cards)];
	}
	public double get(int cardNum1, int cardNum2){
		int c1,c2;
		if(cardNum1<cardNum2){
			c1=cardNum1;
			c2=cardNum2;
		}else{
			c1=cardNum2;
			c2=cardNum1;
		}
		
		return array[1326 - ( ( 52 - c1 )*( 52-c2 + 1 )/2 - ( c2- c1 - 1) )];
	}
	
	/**
	 * Returns a probability of success of the given whole cards against the range given the current board cards.
	 * @param wholeCards
	 * @param board: a list of cards which are assumed community cards already. Can be null to signify no community cards yet
	 * @return
	 */
	public double calculateEquity(Card[] wholeCards, ArrayList<Card> board){
		return calculateEquity(wholeCards,board,this);
	}
	
	/**
	 * Returns a probability of success of the given whole cards against the range given the current board cards.
	 * @param wholeCards
	 * @param board: a list of cards which are assumed community cards already. Can be null to signify no community cards yet
	 * @return
	 */
	public static double calculateEquity(Card[] wholeCards, ArrayList<Card> board, Range r){
		//return probability of success: probability(success) = sum over whole range (probability success given exact opponent's cards).
		//this is since the contents of an opponent's whole cards are mutually exclusive.
		double s=0;
		for(int i=0;i<r.array.length;i++){
			//jump equity calculation if the current range item has 0% chance or one 
			//of the whole cards matches a card for this range element. this is for efficiency gains
			//We have currently commented out the checks for duplicates between whole cards, board and 
			//range since those should be dealt with by the playerBot as more board cards come out and range 
			//modifications are made.
			
//			Card[] tmp = indexToCards(i);
//			int c1 = tmp[0].getNum();
//			int c2 = tmp[1].getNum();
			if(r.array[i] == 0 
//					|| c1==wholeCards[0].getNum()
//					|| c2==wholeCards[0].getNum()
//					|| c1==wholeCards[1].getNum()
//					|| c2==wholeCards[1].getNum() 
//					|| board.contains(tmp[0])
//					|| board.contains(tmp[1])       
					)
				continue;
			
			s += r.array[i] * calculateEquity(wholeCards,indexToCards(i),board);
		}
		
		return s;
	}
	
	/**
	 * returns the probability of wholeCards1 beating wholeCards2 given the board and assuming 5 community cards total.
	 * computed by trying all possible remaining combinations. See calculateEquityBySimulation for a more efficient with slight loss
	 * of accuracy equivalent method.
	 * @param wholeCards1
	 * @param wholeCards2
	 * @param board
	 * @return
	 */
	public static double calculateEquity(Card[] wholeCards1, Card[] wholeCards2, ArrayList<Card> board){
		int numRemainingCards;
		if(board == null)
			board = new ArrayList<Card>(0);
		numRemainingCards = 5 - board.size();
		
		double winsP1 = 0;
		double winsP2 = 0;
		
		int activeIndex = numRemainingCards - 1;
		Card[] availableCards = new Card[52-2-2-board.size()];
		Card curCard = null;
		for(int i=0;i<availableCards.length;i++){
			curCard = new Card(i);
			if(!(wholeCards1[0] == curCard 
					||wholeCards1[1] == curCard
					||wholeCards2[0] == curCard
					||wholeCards2[1] == curCard
					||board.contains(curCard)))
				availableCards[i] = curCard;
		}
		int[] inds = new int[numRemainingCards];
		for(int i=0;i<inds.length;i++)
			inds[i]=i;
		inds[inds.length-1] = inds[inds.length-1]-1;
		

		ArrayList<Card> cs1 = new ArrayList<Card>(7);
		ArrayList<Card> cs2 = new ArrayList<Card>(7);
		for(int i=0;i<inds.length;i++){
			cs1.add(availableCards[inds[i]]);
			cs2.add(availableCards[inds[i]]);
		}
		cs1.add(wholeCards1[0]);
		cs1.add(wholeCards1[1]);
		cs2.add(wholeCards2[0]);
		cs2.add(wholeCards2[1]);
		cs1.addAll(board);
		cs2.addAll(board);
		
		//iterate over possible combinations
		while(activeIndex != -1){
			//update subset
			inds[activeIndex]++;//increment active index
			//update all elements after the incremented index to minimum values
			//(ie, if index 2/3 is updated to 4, then index 3 should be updated to 5.
			for(int i=activeIndex+1;i<inds.length;i++){
				inds[i] = inds[i-1]+1;
			}
			
			
			//calculate winner
			long b = System.nanoTime();
			cs1.set(activeIndex, availableCards[inds[activeIndex]]);
			int r1 = Hand.bestHand(cs1).getRank();
			cs2.set(activeIndex, availableCards[inds[activeIndex]]);
			int r2 = Hand.bestHand(cs2).getRank();
			long a = System.nanoTime();
//			System.out.println((a-b)/1000000000.0);
			
			if(r1 > r2)
				winsP1++;
			else if(r2 > r1)
				winsP2++;
			else{
				winsP1 += 0.5;
				winsP2 += 0.5;
			}
			
			

			//now update active index
			activeIndex = inds.length-1;
			for(int i=inds.length-1;i>=0;i--){
				if(inds[i] == availableCards.length-(inds.length-i)){
					activeIndex = i-1;
				}
			}
		}
		
		return winsP1*1.0/(winsP1+winsP2);
	}
	

	
	public static Card[] indexToCards(int i){
		if(i<0 || i>= 1326)
			return null;
		
		//the (int) cast just floors the value. which is necessary here
		int c1 = 52 - ( (int) Math.ceil( (-1.0 + Math.sqrt(1+8*(1326-i)))/2.0 ) );
		System.out.println(c1);
		int c2 = i+((52-c1)*(52-c1+1)/2)+c1+1-1326;
		System.out.println(c2);
		
		Card[] cs = new Card[2];
		cs[0] = new Card(c1);
		cs[1] = new Card(c2);
		return cs;
	}
	
	public static int cardsToIndex(Card[] pairCards){
		return 1326 - ( ( 52 - pairCards[0].getNum() )*( 52-pairCards[0].getNum() + 1 )/2 - ( pairCards[1].getNum() - pairCards[0].getNum() - 1) ) ;
	}
}
