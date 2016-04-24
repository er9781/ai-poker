package equityCalculations;

import java.util.ArrayList;
import java.util.Collections;

import pokerBot.Card;
import pokerBot.Deck;
import pokerBot.Hand;
import pokerBot.Utilities;

public class EquitySimulation {

	
	/**
	 * 
	 * @param wholeCards1
	 * @param wholeCards2
	 * @param board
	 * @param numBoardsToTry
	 * @return
	 */
	public static EquityBreakdown calculateEquityBySimulation(Card[] wholeCards1, Card[] wholeCards2, ArrayList<Card> board, int numBoardsToTry){
		System.out.println("beginning simulation");
		if(board == null)
			board = new ArrayList<Card>(0);
		
		int numWin = 0;
		int numTie = 0;
		
		//sample the deck and each time figure out the winner
		Card[] excludedCards = new Card[4];
		excludedCards[0] = wholeCards1[0];
		excludedCards[1] = wholeCards1[1];
		excludedCards[2] = wholeCards2[0];
		excludedCards[3] = wholeCards2[1];
		
		Deck deck = new Deck(excludedCards);
		
		Card[] sample;
		Card[] cardsWithBoard = new Card[7];
		for(int i=0; i < numBoardsToTry; i++){
			System.out.println("Run "+(i+1)+" out of "+numBoardsToTry);
			//sample the deck
			sample = deck.sample(5 - board.size());
			
			Utilities.printArray(sample);
			//get the best hand for both players
			System.arraycopy(wholeCards1,0,cardsWithBoard,0,2);
			System.arraycopy(board.toArray(),0, cardsWithBoard, 2, board.size());
			System.arraycopy(sample, 0, cardsWithBoard, 2+board.size(), sample.length);
			Hand hand1 = Hand.bestHand(cardsWithBoard);

			System.arraycopy(wholeCards2,0,cardsWithBoard,0,2);
			//don't need since board is already copied in from hand1
			Hand hand2 = Hand.bestHand(cardsWithBoard);
			
			//get which hand is best and update counters accordingly
			int comparison = hand1.compareTo(hand2);
			if(comparison > 0)
				numWin++;
			else if(comparison == 0)
				numTie++;
			
		}
		
		//calc % win and return
		double pwin = 1.0*numWin/numBoardsToTry;
		double ptie = 1.0*numTie/numBoardsToTry;
		
		return new EquityBreakdown(pwin, ptie);
	}
}
