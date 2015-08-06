package pokerBot;

import java.awt.Color;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

public class GameController implements Runnable {

	public final int id = 1;
	
	private static int numPlayers = 4;
	private ArrayList<Player> players = new ArrayList<Player>(numPlayers);
	int defaultStackSize = 1000;
	
	boolean endGameRequested = false;
	
	private Deck deck = new Deck();
	private ArrayList<Card> board = new ArrayList<Card>(5);
	
	final int smallBlind = 1;
	final int bigBlind = 2;
	private int dealer = 0;
	private int potSize = 0;
	private ArrayList<Player> remainingPlayers;
	private ArrayList<Integer> bets;
	private int currentBet = 0;
	private int previousBet = 0;

	private boolean endRound = false;
	
	
	public GameController(){
		
		players.add(new Player("You",defaultStackSize,this));
		players.add(new PlayerBot("Computer",defaultStackSize,this));
		for(int i=2;i<numPlayers;i++){
			players.add(new Player("Player "+i,defaultStackSize, this));
		}
		
		gui = new GUI(players, this);
		
		try{PrintWriter w = new PrintWriter(new File("games/log"+id+".txt"));
		w.write("");
		w.close();}catch(Exception e){}
	}
	
	Thread roundThread;
	public void newGame(){
		//randomize dealer starting position
		dealer = (int) (Math.random()*players.size());
		
		//start game in new thread to allow gui to run normally
		roundThread = new Thread(this);
		roundThread.start();
	}
	
	public void run(){
		while(!endGameRequested){
			//edit settings such as dealer position
			dealer = (dealer+1) % numPlayers;

			//play out hand
			newRound();
		}
		
		//wrap up game and re-enable begin game button
		gui.beginGameButton.setEnabled(true);
		//publish game results somehow?TODO
	}
	
	public void newRound(){
		//set up surrounding things
		endRound = false;
		players.get(dealer).assignDealer();
		
		//create list of remaining players in the hand
		remainingPlayers = new ArrayList<Player>(players.size());
		for(int i=0; i<numPlayers;i++){
			remainingPlayers.add(players.get((i+dealer+1) % numPlayers));//add starting with one after dealer
			players.get(i).panel.setBackground(Color.GRAY);
		}
		gui.repaint();
		
		potSize = 0;//reset pot
		deck.shuffle();//shuffle deck
		previousBet = 0;
		currentBet = 0;
		board = new ArrayList<Card>(5);
		gui.buildBoardPanel();
		gui.potLabel.setText("Pot Size: $0");
		gui.repaint();
		
		//deal out cards
		for(int i=0;i<numPlayers;i++){
			remainingPlayers.get(i).setWholeCards(deck.getCard( 2*i ),deck.getCard( 2*i + 1 ));
		}
		
		//pre-flop betting
		initiateBetting(true);//begin with blinds bet
		
		//flop betting
		if(!endRound){
			board.add(deck.getCard(numPlayers*2    ));
			board.add(deck.getCard(numPlayers*2 + 1));
			board.add(deck.getCard(numPlayers*2 + 2));
			gui.buildBoardPanel();
			initiateBetting(false);
		}
		
		//turn betting
		if(!endRound){
			board.add(deck.getCard(numPlayers*2 + 3));
			gui.buildBoardPanel();
			initiateBetting(false);
		}
		
		//river betting
		if(!endRound){
			board.add(deck.getCard(numPlayers*2 + 4));
			gui.buildBoardPanel();
			initiateBetting(false);
		}
		

		//wait for 2 seconds to allow players to see who won
		//need to flip open cards once they're concealed TODO
		try{Thread.sleep(2000);}catch(Exception e){}
		
		//showdown
		//get best hand
		if(endRound){
			//only 1 player left, just give him pot
			remainingPlayers.get(0).stack += potSize;
			remainingPlayers.get(0).updatePanel();
			try{
				FileWriter logWriter = new FileWriter(new File("games/log"+id+".txt"),true);
				logWriter.append("- "+remainingPlayers.get(0).name+" won pot of "+potSize+"\r\n");
				logWriter.close();
			}catch(Exception e){}
		}else{
			//proceed to showdown between players
			ArrayList<Player> winningPlayers = new ArrayList<Player>(1);//should be sorted in order from first after dealer
			int maxRank = -1;
			Hand winningHand = null;
			for(Player player : remainingPlayers){
				Hand curHand = player.getCurrentHand(board);
//				System.out.println(player.name);
//				System.out.println(curHand);
//				System.out.println(curHand.getRank());
//				System.out.println("cur max rank: "+maxRank);
				if( curHand.getRank() > maxRank){
					winningPlayers = new ArrayList<Player>(1);
					winningPlayers.add(player);
					winningHand = curHand;
					maxRank = curHand.getRank();
				}else if (curHand.getRank() == maxRank){
					winningPlayers.add(player);
				}
			}
//			System.out.println("\nwinningPlayer1: "+winningPlayers.get(0).name);
			
			//split up pot
			int potSplit = potSize / winningPlayers.size();
			int remainder = potSize - potSplit * winningPlayers.size();
			
			for(int i=0;i<winningPlayers.size();i++){
				winningPlayers.get(i).stack += potSplit;
				if(i < remainder)
					winningPlayers.get(i).stack += 1;
				winningPlayers.get(i).updatePanel();
			}
			
			try{
				FileWriter logWriter = new FileWriter(new File("games/log"+id+".txt"),true);
				if(winningPlayers.size()==1)
					logWriter.append("- "+winningPlayers.get(0).name+" won pot of "+potSize+" with "+winningHand.getClassification());
				else{
					logWriter.append("- "+winningPlayers.get(0).name);
					for(int i=1;i<winningPlayers.size();i++)
						logWriter.append(", "+winningPlayers.get(i).name);
					logWriter.append(" split pot of "+potSize+" with "+winningHand.getClassification());
				}
				logWriter.append("\r\n");
				logWriter.close();
			}catch(Exception e){}
		}
		
		players.get(dealer).deassignDealer();
	}
	
	private void initiateBetting(boolean startWithBlinds) {
		
		currentBet = 0;
		previousBet = 0;
		
		//reset all bets to size 0 in the gui
		for(Player player: players)
			player.updatePanel();
		int playersToAct = remainingPlayers.size();
		bets = new ArrayList<Integer>(remainingPlayers.size());
		for(int i=0;i<remainingPlayers.size();i++)
			bets.add(0);
		
		int actingPlayer = 0;//recall that remaining players is sorted starting first after dealer
		//if blinds are there, place bets to begin with
		if(startWithBlinds){
			currentBet = bigBlind;
			if(numPlayers == 2){
				//reversed blinds
				bets.set(0,bigBlind);
				remainingPlayers.get(0).updatePanel(bigBlind,0);
				remainingPlayers.get(0).hasOption = true;
				bets.set(1,smallBlind);
				remainingPlayers.get(1).updatePanel(smallBlind,0);
				actingPlayer = 1;
			}else{
				//normal blinds
				bets.set(0, smallBlind);
				remainingPlayers.get(0).updatePanel(smallBlind,0);
				bets.set(1, bigBlind);
				remainingPlayers.get(1).updatePanel(bigBlind,0);
				remainingPlayers.get(1).hasOption = true;
				actingPlayer = 2;
			}
		}
		
		while(playersToAct > 0){
			int response = remainingPlayers.get(actingPlayer).requestAction();
			
			if(response == -1){
				//player folded, so remove him from the players remaining, add his chips to the pot,
				//and remove him from the bets. also, the acting player doesn't move since the others
				//are shifted. so only potentially need to mod if it's last to act folding.
				remainingPlayers.get(actingPlayer).panel.setBackground(Color.darkGray);
				potSize += bets.get(actingPlayer);
				remainingPlayers.get(actingPlayer).updatePanel();
				remainingPlayers.remove(actingPlayer);
				bets.remove(actingPlayer);
				if(remainingPlayers.size() == 1){
					endRound = true;
					break;
				}
				gui.potLabel.setText("Pot Size: $"+potSize);
				gui.repaint();
				playersToAct = playersToAct - 1;
				actingPlayer = actingPlayer % remainingPlayers.size();
			}else if(response == 0){
				//player checked, so make sure that's a valid play first
				if(bets.get((actingPlayer -1 + remainingPlayers.size())%remainingPlayers.size()) != 0)
					//check is not valid, so ask for action again without changing position
					continue;
				else{
					playersToAct = playersToAct - 1;
					bets.set(actingPlayer,response);
					actingPlayer = (actingPlayer + 1) % remainingPlayers.size();
				}
			}else{
				//player has made a bet. first check to see if it's valid
				if(bets.get((actingPlayer -1 + remainingPlayers.size())%remainingPlayers.size()) > response  
						||  response < -1
						||  response > remainingPlayers.get(actingPlayer).stack  
						||  response < bigBlind
						||  (  response > bets.get((actingPlayer -1 + remainingPlayers.size())%remainingPlayers.size()) 
								&& response < (2*bets.get((actingPlayer -1 + remainingPlayers.size())%remainingPlayers.size()) 
								- bets.get((actingPlayer -2 + remainingPlayers.size())%remainingPlayers.size())) )
								)
					//bet is invalid, so ask again for action from the same player
					continue;
				else{
					//bet is valid
					if(response == bets.get((actingPlayer -1 + remainingPlayers.size())%remainingPlayers.size()))
						playersToAct = playersToAct - 1;
					else
						playersToAct = remainingPlayers.size() - 1;
					
					remainingPlayers.get(actingPlayer).updatePanel(response,bets.get(actingPlayer));
					bets.set(actingPlayer,response);
					if(response != currentBet){
						previousBet = currentBet;
						currentBet = response;
					}
					actingPlayer = (actingPlayer + 1) % remainingPlayers.size();
				}
			}
		}
		
		//betting complete, place all bets into pot
		for(int bet:bets){
			potSize += bet;//recall that folds are -1 but are removed from bets as people fold
		}
		//update pot label in the gui
		gui.potLabel.setText("Pot Size: $"+potSize);
		gui.repaint();
	}
	
	
	//get methods
	/**
	 * returns a deep copy of the open cards on the board
	 * @return
	 */
	public ArrayList<Card> getBoard(){
		ArrayList<Card> tmp = new ArrayList<Card>(5);
		for(Card card : board)
			tmp.add(card.clone());
		return tmp;
	}
	public int getPotSize(){
		return potSize;
	}
	/**
	 * returns a deep copy of the current bets
	 * @return
	 */
	public ArrayList<Integer> getBets(){
		ArrayList<Integer> tmp = new ArrayList<Integer>(bets.size());
		for(Integer i : bets)
			tmp.add(i.intValue());
		return tmp;
	}
	public int getNumRemainingPlayers(){
		return remainingPlayers.size();
	}
	public int getPosition(Player player){
		return remainingPlayers.indexOf(player);
	}
	public static int getNumPlayers(){
		return numPlayers;
	}
	public ArrayList<String> getPlayerNames(){
		ArrayList<String> tmp = new ArrayList<String>(players.size());
		for(Player player : players)
			tmp.add(player.name);
		return tmp;
	}
	public String getPlayerName(int index){
		try{
			return players.get(index).name;
		}catch(Exception e){return "";}
	}
	public int getCurrentBet(){
		return currentBet;
	}
	public int getPreviousBet() {
		return previousBet;
	}
	
	GUI gui;
	static GameController gameController;
	public static void main(String[] args) {
		gameController = new GameController();
	}

	
}
