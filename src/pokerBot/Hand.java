package pokerBot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Hand implements Comparable<Hand>{
	
	private ArrayList<Card> cards = new ArrayList<Card>(5);
	private String classification;
	private int rank;
	
	public Hand(ArrayList<Card> inCards){
		if( inCards.size() != 5){
			System.out.println("A hand must have 5 cards");
			return;
		}

		//only use a copy to save in the hand
		for(Card card : inCards)
			cards.add(card.clone());
		
		Collections.sort(cards);
		classify(cards);
	}
	
	/**
	 * classifies 5 cards as a poker hand such as "two pair aces and twos" or "straight ace high"
	 * also assigns a definitive hand rank to the field rank and a name in the field variable as well.
	 * @param cards
	 * @return
	 */
	public void classify(ArrayList<Card> cards){
		//Please note that the following function assumes that the cards are already sorted in decreasing
		//order with aces high.

		
		//count number of distinct cards in number value
		int distinctCards=1;
		for(int i=1;i<=4;i++){
			if(cards.get(i).getNumberValue() != cards.get(i-1).getNumberValue())
				distinctCards++;
		}
		
		//if the number of distinct cards is not 5, then the hand is obviously not a straight or flush
		boolean isStraight = false;
		boolean isWheel = false;
		boolean isFlush = false;
		if(distinctCards==5){
			//check for straight
			//check for wheel since aces are saved as high
			if(			cards.get(0).getNumberValue()==12 
					&& 	cards.get(1).getNumberValue()==3 
					&& 	cards.get(2).getNumberValue()==2 
					&& 	cards.get(3).getNumberValue()==1 
					&& 	cards.get(4).getNumberValue()==0  ){
				isStraight = true;
				isWheel = true;
			}
			//check for non-wheel
			int consecutiveCards = 1;
			for(int i=1;i<=4;i++){
				if(cards.get(i).getNumberValue() == cards.get(i-1).getNumberValue()-1)
					consecutiveCards++;
				else
					break;
			}
			if(consecutiveCards == 5)
				isStraight = true;
			
			//check for flush
			isFlush = true;//set to true, if any card isn't the same suit as the last revert to false
			for(int i=1;i<=4;i++){
				if(cards.get(i).getSuitValue() != cards.get(i-1).getSuitValue()){
					isFlush = false;
					break;
				}
			}
		}
			
		//now we check in decreasing order of hand strength.
		//the base hand rank represents the lowest hand rank for the hand strength we're testing.
		//so the lowest rank in each section will be base hand rank + 1
		
		int baseRank = 1000000;
		
		//check for straight flush
		baseRank = baseRank - 10000;
		if(isStraight && isFlush){
			if(isWheel){
				rank = baseRank + 1;
				classification = "Straight Flush 5 High";
				return;
			}else{
				rank = baseRank + cards.get(0).getNumberValue() - 2;
				classification = "Straight Flush "+cards.get(0).toString().charAt(0)+" High";
				return;
			}
		}
		
		//check for quads and full house
		baseRank = baseRank - 10000 - 10000;
		if(distinctCards == 2){
			int tmpRank = baseRank + 10000;//work with tmp in case rank is changed to a final variable. the +1 is to start at first quads hand
			
			//check that it's quads (with 2 distinct cards it's quads or a full house)
			//the second card must be part of the quads so we get the number value of the quads from there
			int quadNum = cards.get(1).getNumberValue();
			if( quadNum == cards.get(0).getNumberValue()
					&& quadNum == cards.get(2).getNumberValue()
					&& quadNum == cards.get(3).getNumberValue() ){
				//it is quads and kicker is fifth
				tmpRank += quadNum*13;
				tmpRank += cards.get(4).getNumberValue();
				rank = tmpRank;
				classification = "Quad "+cards.get(1).toString().charAt(0)+"'s with "+cards.get(4).toString().charAt(0)+" kicker";
				return;
			}
			if( quadNum == cards.get(2).getNumberValue()
					&& quadNum == cards.get(3).getNumberValue()
					&& quadNum == cards.get(4).getNumberValue() ){
				//it is quads and kicker is first
				tmpRank += quadNum*13;
				tmpRank += cards.get(0).getNumberValue();
				rank = tmpRank;
				classification = "Quad "+cards.get(1).toString().charAt(0)+"'s with "+cards.get(0).toString().charAt(0)+" kicker";
				return;
			}
			
			tmpRank = baseRank;
			//check for which full house it is (since not quads we know it's full house)
			//the third card must be part of the set of 3, so we get the trip num there
			int tripNum = cards.get(2).getNumberValue();
			if( tripNum == cards.get(0).getNumberValue()
					&& tripNum == cards.get(1).getNumberValue() ){
				//full house with trips first and pair in 4th and 5th
				tmpRank += tripNum*13;
				tmpRank += cards.get(4).getNumberValue();
				rank = tmpRank;
				classification = "Full house "+cards.get(2).toString().charAt(0)+"'s and "+cards.get(4).toString().charAt(0)+"'s";
				return;
			}else{
				//must be full house pair first and trips second
				tmpRank += tripNum*13;
				tmpRank += cards.get(0).getNumberValue();
				rank = tmpRank;
				classification = "Full house "+cards.get(2).toString().charAt(0)+"'s and "+cards.get(0).toString().charAt(0)+"'s";
				return;
			}
		}
		
		//check for flush
		baseRank = baseRank - 10000;
		if(isFlush){
			rank = baseRank + (int)(
					Math.pow(2,cards.get(0).getNumberValue()) +
					Math.pow(2,cards.get(1).getNumberValue()) +
					Math.pow(2,cards.get(2).getNumberValue()) +
					Math.pow(2,cards.get(3).getNumberValue()) +
					Math.pow(2,cards.get(4).getNumberValue()) );
			classification = "Flush "
					+cards.get(0).toString().charAt(0) + "-"
					+cards.get(1).toString().charAt(0) + "-"
					+cards.get(2).toString().charAt(0) + "-"
					+cards.get(3).toString().charAt(0) + "-"
					+cards.get(4).toString().charAt(0) ;
			return;
		}
		
		//check for straight
		baseRank = baseRank - 10000;
		if(isStraight){
			if(isWheel){
				rank = baseRank + 1;
				classification = "Wheel";
				return;
			}else{
				rank = baseRank + cards.get(0).getNumberValue() - 2;
				classification = "Straight "+cards.get(0).toString().charAt(0)+" High";
				return;
			}
		}
		
		//check for trips and two pair
		baseRank = baseRank -10000 - 10000;
		if(distinctCards == 3){
			//first check for trips.
			int tmpRank = baseRank + 10000;
			//trips must be in middle index so get trips number there
			int tripsNum = cards.get(2).getNumberValue();
			if(tripsNum == cards.get(0).getNumberValue()){
				//trips are in 0,1,2
				rank = tmpRank 
						+ tripsNum * 13*13
						+ cards.get(3).getNumberValue() * 13
						+ cards.get(4).getNumberValue() ;
				classification = "Trip "+cards.get(2).toString().charAt(0)+"'s with "
						+cards.get(3).toString().charAt(0)+cards.get(4).toString().charAt(0)+" kickers";
				return;
			}else if(tripsNum == cards.get(1).getNumberValue()
					&& tripsNum == cards.get(3).getNumberValue() ){
				//trips are in 1,2,3
				rank = tmpRank 
						+ tripsNum * 13*13
						+ cards.get(0).getNumberValue() * 13
						+ cards.get(4).getNumberValue() ;
				classification = "Trip "+cards.get(2).toString().charAt(0)+"'s with "
						+cards.get(0).toString().charAt(0)+cards.get(4).toString().charAt(0)+" kickers";
				return;
			}else if(tripsNum == cards.get(4).getNumberValue()){
				//trips are in 2,3,4
				rank = tmpRank 
						+ tripsNum * 13*13
						+ cards.get(0).getNumberValue() * 13
						+ cards.get(1).getNumberValue() ;
				classification = "Trip "+cards.get(2).toString().charAt(0)+"'s with "
						+cards.get(0).toString().charAt(0)+cards.get(1).toString().charAt(0)+" kickers";
				return;
			}
			//otherwise it's not trips.
			
			//if not trips then it's two pair
			tmpRank = baseRank;
			int pair1 = cards.get(1).getNumberValue();
			int pair2 = cards.get(3).getNumberValue();
			tmpRank += pair1 * 13*13 + pair2*13;
			if( pair1 == cards.get(0).getNumberValue() ){
				//kicker is not first
				if( pair2 == cards.get(2).getNumberValue() ){
					//kicker is last
					rank = tmpRank + cards.get(4).getNumberValue();
					classification = "Two Pair "+cards.get(1).toString().charAt(0)+"'s and "
					+cards.get(3).toString().charAt(0)+"'s with "+cards.get(4).toString().charAt(0)+" kicker";
					return;
				}else{
					//kicker is middle
					rank = tmpRank + cards.get(2).getNumberValue();
					classification = "Two Pair "+cards.get(1).toString().charAt(0)+"'s and "
					+cards.get(3).toString().charAt(0)+"'s with "+cards.get(2).toString().charAt(0)+" kicker";
					return;
				}
			}else{
				//kicker is first
				rank = tmpRank + cards.get(0).getNumberValue();
				classification = "Two Pair "+cards.get(1).toString().charAt(0)+"'s and "
				+cards.get(3).toString().charAt(0)+"'s with "+cards.get(0).toString().charAt(0)+" kicker";
				return;
			}
		}		
		
		//check for pair
		baseRank = baseRank - 30000;
		if(distinctCards == 4){
			//must be a pair
			
			//find index where the pair begins
			int startIndex = 0;
			for(int i=1;i<=4;i++){
				startIndex = i-1;
				if(cards.get(i).getNumberValue() == cards.get(i-1).getNumberValue())
					break;
			}
			int tmpRank = baseRank;
			tmpRank += cards.get(startIndex).getNumberValue()*13*13*13;//base 13 first number should be the number of pair
			int kicker=0;
			String kickers = "";
			for(int i=0;i<=4;i++){
				if(i!=startIndex && i!=startIndex+1){
					kicker++;
					tmpRank += cards.get(i).getNumberValue()*Math.pow(13,3-kicker);
					kickers += cards.get(i).toString().charAt(0);
				}
			}
			rank = tmpRank;
			classification = "Pair of "+cards.get(startIndex).toString().charAt(0)+"'s with "+kickers+" kickers";
			return;
		}
		
		//check for high card
		baseRank = baseRank - 10000;
		rank = baseRank + (int)(
				Math.pow(2,cards.get(0).getNumberValue()) +
				Math.pow(2,cards.get(1).getNumberValue()) +
				Math.pow(2,cards.get(2).getNumberValue()) +
				Math.pow(2,cards.get(3).getNumberValue()) +
				Math.pow(2,cards.get(4).getNumberValue()) );
		classification = "High Card "
				+cards.get(0).toString().charAt(0) + "-"
				+cards.get(1).toString().charAt(0) + "-"
				+cards.get(2).toString().charAt(0) + "-"
				+cards.get(3).toString().charAt(0) + "-"
				+cards.get(4).toString().charAt(0) ;
		return;
	}
	
	/**
	 * takes the index from the hand and returns the card in that slot in the hand
	 * @param index: number from 0 to 4
	 * @return
	 */
	public Card getCard(int index){
		return cards.get(index);
	}
	public String getClassification(){
		return classification;
	}
	public int getRank(){
		return rank;
	}
	
	/**
	 * takes a list of cards and returns the best 5 card poker hand that can be made with them
	 * @param cards: a list of at least 5 cards
	 * @return returns the best 5 card poker hand out of the input cards
	 */
	public static Hand bestHand(ArrayList<Card> cards){
		if( cards.size() < 5 )
			return null;
		
		
		//TODO probably better system to identify best 5 card hand out of 5+ cards, but I haven't found it.
		//compile list of all possible hands
		ArrayList<ArrayList<Card>> allSubsets = Utilities.subset(cards,5);
		//convert lists of 5 cards to hands
		ArrayList<Hand> allHands = new ArrayList<Hand>(21);//21 is the number of combinations for 7 cards
		for (ArrayList<Card> set : allSubsets){
			allHands.add(new Hand(set));
		}
		
		//get max and return it
		return Collections.max(allHands);
	}
	public static Hand bestHand(Card[] cards){
		ArrayList<Card> cs = new ArrayList<Card>(cards.length);
		cs.addAll(Arrays.asList(cards));
		return bestHand(cs);
	}

	public int compareTo(Hand hand2) {
		if(rank>hand2.rank)
			return 1;
		if(rank<hand2.rank)
			return -1;
		return 0;
	}
	
	public String toString(){
		return classification + ": " + cards.get(0) +", "+ cards.get(1) +", "+ cards.get(2) +", "+ cards.get(3) +", "+ cards.get(4);
	}
	
	public Hand clone(){
		return new Hand(cards);
	}
}
