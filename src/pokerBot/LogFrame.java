package pokerBot;

import java.awt.Dimension;
import java.awt.TextArea;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

public class LogFrame {
	
	GameController game;
	JFrame f = new JFrame();
	TextArea logArea = new TextArea();
	JScrollPane scrollPane = new JScrollPane(logArea);
	
	public static boolean logIsOpen = false;
	
	//TODO set up log frame to update with game actions
	
	public LogFrame(GameController game){
		if(logIsOpen)
			return;
		logIsOpen = true;
		this.game = game;
		loadLog();
		
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setPreferredSize(new Dimension(400,400));
		f.add(scrollPane);
		
		f.setTitle("Log");
		f.setResizable(false);
		f.addWindowListener(new WindowAdapter() {
		    public void windowClosing(WindowEvent e) {
		        logIsOpen=false;
		        f.dispose();
		    }
		});
		f.setIconImage(Toolkit.getDefaultToolkit().getImage("images/redCircle.png"));
		f.pack();
		f.setVisible(true);
	}
	
	private void loadLog(){
		Scanner file = null;
		try{
			file = new Scanner(new File("games/log"+game.id+".txt"));
			while(file.hasNextLine())
				logArea.append(file.nextLine()+"\n");
		}catch(Exception e){logArea.append("No log file to show.");}
	}
}
