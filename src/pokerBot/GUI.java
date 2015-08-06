package pokerBot;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;

public class GUI extends JFrame {
	
	GameController game;
	JPanel underFrame = new JPanel();
	
	JPanel south = new JPanel();
	JButton callButton = new JButton("Call");
	JButton foldButton = new JButton("Fold");
	JButton raiseButton = new JButton("Raise");
	JSlider raiseAmountSlider = new JSlider();
	JTextField raiseAmountField = new JTextField("50",8);
	
	JPanel center = new JPanel();
	JLabel potLabel = new JLabel("Pot size: $0");
	JPanel boardPanel = new JPanel();
//	ArrayList<JLabel> cardImages = new ArrayList<JLabel>(53);
	
	JPanel north = new JPanel();
	ArrayList<JLabel> stackLabels;
	private ArrayList<Player> players;
	
	JPanel east = new JPanel();
	JButton endGameButton = new JButton("End Game (after this hand)");
	JButton beginGameButton = new JButton("Begin Game");
	JButton logButton = new JButton("View Log");
	
	Player curPlayer;
	
	public GUI(ArrayList<Player> players, GameController game){
		this.players = players;
		this.game = game;
		
		buildSouthPanel();
		buildCenterPanel();
		buildNorthPanel();
		buildEastPanel();
		
		//build underFrame
		underFrame.setLayout(new BorderLayout());
		underFrame.add(south,BorderLayout.SOUTH);
		underFrame.add(center,BorderLayout.CENTER);
		underFrame.add(north,BorderLayout.NORTH);
		underFrame.add(east,BorderLayout.EAST);
		
		setButtonsEnabled(false);
		
		add(underFrame);
//		setResizable(false);
		setSize(new Dimension(800,600));
		setMinimumSize(new Dimension(600,450));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocation(Toolkit.getDefaultToolkit().getScreenSize().width/2-getSize().width/2,Toolkit.getDefaultToolkit().getScreenSize().height/2-getSize().height/2+30);
		setTitle("AI-Poker Interface");
		setIconImage(Toolkit.getDefaultToolkit().getImage("images\\redCircle.png"));
		setVisible(true);
	}

	private void buildEastPanel(){
		endGameButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				endGameButton.setEnabled(false);
				game.endGameRequested = true;
			}
		});
		beginGameButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				beginGameButton.setEnabled(false);
				endGameButton.setEnabled(true);
				game.newGame();
			}
		});
		logButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				new LogFrame(game);
			}
		});
		endGameButton.setEnabled(false);
		east.setLayout(new BoxLayout(east,BoxLayout.Y_AXIS));
		east.add(beginGameButton);
		east.add(endGameButton);
		east.add(logButton);
	}
	private void buildSouthPanel(){
		raiseAmountSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				raiseAmountField.setText(""+raiseAmountSlider.getValue());
			}
		});
		raiseAmountField.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				raiseAmountSlider.setValue(Integer.parseInt(raiseAmountField.getText()));
			}
		});
		
		PlainDocument doc = new PlainDocument();
		doc.setDocumentFilter(new DocumentFilter() {
		    public void insertString(FilterBypass fb, int off, String str, AttributeSet attr) 
		        throws BadLocationException 
		    {
		        fb.insertString(off, str.replaceAll("\\D++", ""), attr);  // remove non-digits
		    }
		    
		    public void replace(FilterBypass fb, int off, int len, String str, AttributeSet attr) 
		        throws BadLocationException 
		    {
		        fb.replace(off, len, str.replaceAll("\\D++", ""), attr);  // remove non-digits
		    }
		});

		raiseAmountField.setDocument(doc);
		ActionListener lis = new pokerResponseListener();
		foldButton.addActionListener(lis);
		callButton.addActionListener(lis);
		raiseButton.addActionListener(lis);
		
		south.add(foldButton);
		south.add(callButton);
		south.add(raiseAmountSlider);
		south.add(raiseAmountField);
		south.add(raiseButton);
	}
	private void buildCenterPanel(){
		potLabel.setFont(new Font("Serif", Font.BOLD, 26));
		center.add(potLabel);
		buildBoardPanel();
		center.add(boardPanel);
	}
	public void buildBoardPanel(){
		JPanel tmp = new JPanel();
		ArrayList<Card> curBoard = game.getBoard();
		int i=0;
		for(;i<curBoard.size();i++){
			tmp.add(curBoard.get(i).pic);
		}
		for(;i<5;i++){
			try{tmp.add(new JLabel(new ImageIcon(ImageIO.read(new File("images/cards/"+"b1fv"+".png")))));}catch(Exception e){}
		}
		tmp.setBackground(Color.BLUE);
		center.remove(boardPanel);
		boardPanel = tmp;
		center.add(tmp);
		center.revalidate();
		center.repaint();
	}
	private void buildNorthPanel(){
		for(int i=0;i<GameController.getNumPlayers();i++){
			north.add(players.get(i).panel);
		}
		north.setLayout(new GridLayout(1,game.getNumPlayers()));
	}

	public void setButtonsEnabled(boolean val) {
		raiseButton.setEnabled(val);
		raiseAmountField.setEnabled(val);
		raiseAmountSlider.setEnabled(val);
		foldButton.setEnabled(val);
		callButton.setEnabled(val);
	}
	
	public void setUpPlayerAction(Player player){
		curPlayer = player;
		
		foldButton.setEnabled(true);
		callButton.setEnabled(true);
		if(game.getCurrentBet() == 0 || (game.getCurrentBet() == game.bigBlind && curPlayer.hasOption))
			callButton.setText("Check");
		else
			callButton.setText("Call");
		if(player.stack > 2*game.getCurrentBet() - game.getPreviousBet()){
			raiseButton.setText("Raise");
			if(game.getCurrentBet() > 0){
				raiseAmountField.setEnabled(true);
				raiseAmountField.setText(""+(2*game.getCurrentBet() - game.getPreviousBet()));
				raiseAmountSlider.setEnabled(true);
				raiseAmountSlider.setMinimum(2*game.getCurrentBet() - game.getPreviousBet());
				raiseAmountSlider.setMaximum(curPlayer.stack);
				raiseAmountSlider.setValue(raiseAmountSlider.getMinimum());
				raiseButton.setEnabled(true);
			}else{
				if(curPlayer.stack < game.bigBlind){
					raiseButton.setText("Raise All-In");
					raiseButton.setEnabled(true);
					raiseAmountField.setEnabled(false);
				}else{
					raiseAmountField.setEnabled(true);
					raiseAmountField.setText(""+game.bigBlind);
					raiseAmountSlider.setEnabled(true);
					raiseAmountSlider.setMinimum(game.bigBlind);
					raiseAmountSlider.setMaximum(curPlayer.stack);
					raiseAmountSlider.setValue(raiseAmountSlider.getMinimum());
					raiseButton.setEnabled(true);
				}
			}
		}else if(player.stack > game.getCurrentBet()){
			//can only raise all-in
			raiseButton.setText("Raise All-In");
			raiseButton.setEnabled(true);
			raiseAmountField.setEnabled(false);
//			raiseAmountSlider.setMinimum(2* game.getCurrentBet() - game.getPreviousBet());
//			raiseAmountSlider.setMaximum(player.stack);
		}
		curPlayer.panel.setBackground(Color.GREEN);
		
		repaint();
	}
	
	private class pokerResponseListener implements ActionListener{
		public void actionPerformed(ActionEvent event) {
			if(event.getSource().equals(callButton)){
				//call actions
				curPlayer.response = game.getCurrentBet();
			}else if(event.getSource().equals(raiseButton)){
				//raise actions
				curPlayer.response = raiseAmountSlider.getValue();
			}else if(event.getSource().equals(foldButton)){
				//fold actions
				curPlayer.response = -1;
			}
			
			
			raiseButton.setText("Raise");
			//deactivate buttons no matter what
			setButtonsEnabled(false);
			
			curPlayer.panel.setBackground(Color.GRAY);
			repaint();
			
			//return control to the player
			curPlayer.actionPerformed();
		}
	}
}
