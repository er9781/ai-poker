package pokerBot;

import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;


public class Card implements Comparable<Card> {
	
	private final int cardNum;
	private final int numberValue;
	private final int suitValue;
	
	public JLabel pic;
	/**
	 * 
	 * @param num: a number between 1 and 52
	 */
	public Card(int num){
		//may want validation
		
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
		try{pic = new JLabel(new ImageIcon(ImageIO.read(new File("images/cards/"+(num)+".png"))));}catch(Exception e){}
	}
	
	public int getNum(){
		return cardNum;
	}
	public int getSuitValue(){
		return suitValue;
	}
	public int getNumberValue(){
		return numberValue;
	}
	public String getName(){
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
	
	public int compareTo(Card card2) {
		if (cardNum%13 > card2.cardNum%13)
			return -1;
		else if (cardNum%13 < card2.cardNum%13)
			return 1;
		else
			return 0;
	}
	
	/*
	 * @override(non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return getName();
	}
	
	public Card clone(){
		return new Card(cardNum);
	}
	
	public boolean equals(Card card2){
		return (cardNum == card2.cardNum);
	}
}
