package pokerBot;

import equityCalculations.DbInterface;
import equityCalculations.EquityBreakdown;

public class Tester {
	
	public static void populatePreflopEquityTable(){
		//assebmle all preflop hands
//		Card[][] preflopHands = new Card[1326][2];
//		int counter = 0;
//		for(int i = 1; i <= 52; i++){
//			for(int j = 1; j <= 52; j++){
//				if(i != j){
//					preflopHands[counter][0] = new Card(i);
//					preflopHands[counter][1] = new Card(j);
//					counter++;
//				}
//			}
//		}
		
		//set up database connection
		DbInterface dbConn = new DbInterface();
		
		//assemble all cards to avoid repetition of card constructors
		Card[] deck = new Card[52];
		for(int i = 1; i <= 52; i++)
			deck[i-1] = new Card(i);
		
		//for each combo of cards in hand1, loop over all combos of cards in hand 2 which do not include those in hand 1
		for(int i1 = 1; i1 <= 52; i1++){
			for(int j1 = 1; j1 <= 52; j1++){
				//cannot have duplicate card as whole cards
				if(i1 == j1)
					continue;
				
				Card[] hand1 = {deck[i1-1], deck[j1-1]};
				
				//for each pair of whole cards, get a second pair of whole cards
				for(int i2 = 1; i2 <= 52; i2++){
					for(int j2 = 1; j2 <= 52; j2++){
						//cannot have duplicate card as whole card. Nor can we have a card which is already used by hand1
						if(i2 == j2 || i2 == i1 || i2 == j1 || j2 == i1 || j2 == j1)
							continue;
						
						Card[] hand2 = {deck[i2-1], deck[j2-1]};
						
						//get preflop equity(which will calculate them for the database if non-existent
						EquityBreakdown e = dbConn.getPreflopEquity(hand1, hand2);
						
						//do nothing with result
						
						
					}
				}
				
			}
		}
	}
	
	public static void main(String[] args){
//		Card c = new Card(51);
//		Card c2 = new Card(50);
//		Card[] cs = new Card[2];
//		cs[0]=c;
//		cs[1]=c2;
//		String csName = c.getName()+c2.getName();
//		Card[] cs2 = new Card[2];
//		cs2[0]=new Card(7);
//		cs2[1]=new Card(6);
//		String cs2Name = cs2[0].getName()+cs2[1].getName();
		
		populatePreflopEquityTable();
		
//		for(int i=1;i<=52;i++){
//			Card ctmp = new Card(i);
//			System.out.println(i+": "+ctmp.getName());
//		}
		
//		long start = System.nanoTime();
//		double rand= Math.random();
//		System.out.println((System.nanoTime()-start) +"s to generate random");
		
//		int[] arr = new int[100];
		
//		start = System.nanoTime();
//		arr[7] = 5;
//		System.out.println((System.nanoTime()-start) +"s to assign");
		
		
		
		DbInterface dbConn = new DbInterface();
//		EquityBreakdown equity = dbConn.getPreflopEquity(cs, cs2);
//		System.out.println("asked for equity of "+csName+" vs "+cs2Name+": "+equity);
		
//		ArrayList<Card> board = new ArrayList<Card>(5);
//		board.add(new Card(1));
//		board.add(new Card(2));
//		board.add(new Card(3));
		
//		long b = System.nanoTime();
//		double p = Range.calculateEquity(cs,cs2, board);
//		long a = System.nanoTime();
		
//		System.out.println((a-b)/1000000000.0);
//		System.out.println(100*p);
//		System.out.println(new Card(1));
		
//		ArrayList<Card> cs = new ArrayList<Card>(5);
//		cs. add(c);
//		cs.add(c2);
//		cs.add(new Card(2));
//		cs.add(new Card(4));
//		cs.add(new Card(8));
//		for(Card ca:cs)
//			System.out.println(ca);
//		Hand hand = new Hand(cs);
//		
//		ArrayList<Card> cs2 = new ArrayList<Card>(5);
//		cs2.add(new Card(15));
//		cs2.add(new Card(16));
//		cs2.add(new Card(17));
//		cs2.add(new Card(18));
//		cs2.add(new Card(19));
//		Hand hand2 = new Hand(cs2);
//		
//		ArrayList<Hand> hs = new ArrayList<Hand>(4);
//		hs.add(hand);
//		hs.add(hand2);
//		
//		System.out.println(hand);
//		System.out.println(hand2);
//		System.out.println(Collections.max(hs));
//		Card[] cs = new Card[2];
//		cs[0]=new Card(50);
//		cs[1]=new Card(51);
//		System.out.println(Range.cardsToIndex(cs));
//		int in = 1325;
//		System.out.println("input index: "+in);
//		System.out.println("returned index: "+Range.cardsToIndex(Range.indexToCards(in)));
//		for(int i=0;i<1326;i++){
//			if( i != Range.cardsToIndex(Range.indexToCards(i)) ){
//				System.out.println("mismatch on index: "+i);
//			}
//		}
		System.out.println("done");
	}
}
 