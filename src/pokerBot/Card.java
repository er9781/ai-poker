package pokerBot;

import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;


public class Card implements Comparable<Card> {
	
	private final int cardNum;
	private final int numberValue;
	private final int suitValue;
	
	//contains the string representation of the card. Used for toString(). Will be computed once 
	//(only if the name is null)
	private String name = null;
	
	public JLabel pic;
	/**
	 * 
	 * @param num: a number between 1 and 52
	 */
	public Card(int num){
		//may want validation
		if(num < 1 || num > 52){
			throw new IllegalArgumentException(
					"Cards may be initialized with numbers between 1 and 52");
		}
		
		//set cardNum
		cardNum = num;
		
		//set numberValue
		numberValue = num%13;
		
		//set suitValue
		if(num<=13)
			suitValue = 1;
		else if(num<=26)
			suitValue = 2;
		else if (num<=39)
			suitValue = 3;
		else
			suitValue = 4;
		
		//load picture
		try{
			pic = new JLabel(new ImageIcon(ImageIO.read(new File("images/cards/"+(num)+".png"))));
		}catch(Exception ignore){}
	}
	
	/**
	 * 
	 * @return the card number for the card (1 to 52)
	 */
	public int getNum(){
		return cardNum;
	}
	public int getSuitValue(){
		return suitValue;
	}
	public int getNumberValue(){
		return numberValue;
	}
	private String getName(){
		String name = "";
		
		//add number we set up 0 as two since this gives easy comparisons with %13
		switch(numberValue){
		case 0: name+="2";break;
		case 1: name+="3";break;
		case 2: name+="4";break;
		case 3: name+="5";break;
		case 4: name+="6";break;
		case 5: name+="7";break;
		case 6: name+="8";break;
		case 7: name+="9";break;
		case 8: name+="t";break;
		case 9: name+="j";break;
		case 10:name+="q";break;
		case 11:name+="k";break;
		case 12:name+="a";break;
		}
		
		//add suit
		switch(suitValue){
		case 1: name+="C";break;
		case 2: name+="D";break;
		case 3: name+="H";break;
		case 4: name+="S";break;
		}
		
		return name;
	}
	
	/**
	 * Compares the number value (2 through Ace) of the two cards.
	 * Standard comparator function. Returns -1 for >, 1 for < and 0 for equal.
	 */
	public int compareTo(Card card2) {
		if (cardNum%13 > card2.cardNum%13)
			return -1;
		else if (cardNum%13 < card2.cardNum%13)
			return 1;
		else
			return 0;
	}
	
//	public int compareTo(Card card2){
//		return cardNum - card2.cardNum;
//	}
	
	/**
	 * @override(non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		//if the name has not been set yet, then build the name string.
		if(this.name == null){
			this.name= this.getName();
		}
		
		return this.name;
	}
	
	public Card clone(){
		return new Card(cardNum);
	}
	
	/**
	 * Two cards are equal if they represent the same underlying card (ie, 52 distinct cards).
	 * This differs from the compareTo method which compares their number 
	 * values (1-13 for 2 through Ace)
	 * @param card2
	 * @return
	 */
	public boolean equals(Card card2){
		return (cardNum == card2.cardNum);
	}
	
}
