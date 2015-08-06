package pokerBot;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Player {
	
	GameController game;
	
	int stack;
	String name;
	
	JPanel panel;
	JLabel stackLabel;
	private JLabel nameLabel;
	JLabel betLabel;
	JLabel dealerLabel;
	JPanel cardsPanel = new JPanel();
	JLabel cardsLabel = new JLabel("aCaS");
	
	private Card[] cards = new Card[2];
	
	//response stuff
	boolean hasOption = false;
	boolean hasActed = false;
	int response;
	
	public Player(String name, int stack, GameController game){
		this.name = name;
		this.stack = stack;
		this.game = game;
		buildPanel();
	}
	
	public void setWholeCards(Card one, Card two){
		cards[0] = one;
		cards[1] = two;
		cardsPanel.removeAll();
		cardsPanel.add(one.pic);
		cardsPanel.add(two.pic);
		cardsPanel.setBackground(new Color(0,0,0,0));
		cardsPanel.revalidate();
		cardsPanel.repaint();
	}
	public Card[] getWholeCards(){
		return cards;
	}
	public Hand getCurrentHand(ArrayList<Card> board){
		
		//should add section to analyze 2, 3 and 4 card hands (especially 2 since texas has that in game)
		
		ArrayList<Card> board2 = (ArrayList<Card>) board.clone();
		board2.add(cards[0]);
		board2.add(cards[1]);
		
		return Hand.bestHand(board2);
	}
	
	public void buildPanel(){
		try {dealerLabel = new JLabel(new ImageIcon(ImageIO.read(new File("images/dealer.jpg"))));} catch (IOException e) {}
		Font f = new Font("Serif", Font.BOLD, 20);
		JPanel tmp = new JPanel();
		tmp.setLayout(new BoxLayout(tmp,BoxLayout.Y_AXIS));
		nameLabel = new JLabel(name);
		nameLabel.setFont(f);
		tmp.add(nameLabel);
		stackLabel = new JLabel("Stack size $"+stack);
		stackLabel.setFont(f);
		tmp.add(stackLabel);
		try{
		cardsPanel.add(new JLabel(new ImageIcon(ImageIO.read(new File("images/cards/"+"b1fv"+".png")))));
		cardsPanel.add(new JLabel(new ImageIcon(ImageIO.read(new File("images/cards/"+"b1fv"+".png")))));
		cardsPanel.setBackground(new Color(0,0,0,0));
		}catch(Exception e){}
		tmp.add(cardsPanel);
		betLabel = new JLabel("Bet size: $0");
		betLabel.setFont(f);
		dealerLabel.setVisible(false);
		tmp.add(dealerLabel);
		tmp.add(betLabel);
		tmp.setBackground(Color.darkGray);
		tmp.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		panel = tmp;
	}

	public void updatePanel(int betSize,int previousBet){
		betLabel.setText("Bet Size: $"+betSize);
		stack = stack - betSize + previousBet;
		stackLabel.setText("Stack: $"+stack);
		game.gui.repaint();
	}
	public void updatePanel(){
		updatePanel(0,0);//just resets betSize to 0 and gets stack size to repaint
	}
	public void assignDealer(){
		dealerLabel.setVisible(true);
	}
	public void deassignDealer(){
		dealerLabel.setVisible(false);
	}
	
	/**
	 * Waits for a player to act. Player has access to his position, the bets so far, the board, and how many players remain.
	 * player has access through a variety of get methods with the variable game. for example: game.getNumPlayers();
	 * the variables themselves in game are protected and players can only access copies of certain variables
	 * @return returns -1 for a fold, 0 for a check, and a positive integer as a bet (including a call). 
	 * If the bet is not 0 or -1 or is invalid, then the game controller will ask the same player for an action again.
	 */
	public synchronized int requestAction() {
		hasActed = false;
		response = 0;//not sure if this is necessary but it can't hurt
		
		//activate buttons and set up gui legal betting conditions
		game.gui.setUpPlayerAction(this);
		hasOption = false;
		
		//wait
		while(!hasActed){
			try{
				this.wait();
			}catch(InterruptedException ignore){}
		}
		
		//get response and return it
		return response;
	}
	
	public synchronized void actionPerformed(){
		hasActed = true;
		this.notifyAll();
	}
}
