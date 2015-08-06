package pokerBot;

import java.util.ArrayList;
import java.util.Collections;

public class Tester {
	public static void main(String[] args){
		Card c = new Card(52);
		Card c2 = new Card(40);
		Card[] cs = new Card[2];
		cs[0]=c;
		cs[1]=c2;
		Card[] cs2 = new Card[2];
		cs2[0]=new Card(27);
		cs2[1]=new Card(39);
		
		ArrayList<Card> board = new ArrayList<Card>(5);
		board.add(new Card(1));
		board.add(new Card(2));
		board.add(new Card(3));
		
		long b = System.nanoTime();
		double p = Range.calculateEquity(cs,cs2, board);
		long a = System.nanoTime();
		
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
 