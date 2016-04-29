package equityCalculations;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import pokerBot.Card;
import pokerBot.Range;

public class DbInterface {
	
	
	
	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost/";

	//  Database credentials
	static final String USER = "pokerbot";
	static final String PASS = "pokerbot";
	
	// Connection objects
	private Connection conn = null;
	private Statement stmt = null;
	
	
	public DbInterface(){
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();

			conn = DriverManager.getConnection(DB_URL + "pokerAI",USER,PASS);
			stmt = conn.createStatement();
			
			
			
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public EquityBreakdown getPreflopEquity(Card[] heroHand, Card[] villainHand){
		
		//assemble hand name strings with cards in ascending order of cardNum
		String heroHandName;
		if(heroHand[0].getNum() < heroHand[1].getNum())
			heroHandName = heroHand[0].toString()+heroHand[1].toString();
		else
			heroHandName = heroHand[1].toString()+heroHand[0].toString();
		
		String villainHandName;
		if(villainHand[0].getNum() < villainHand[1].getNum())
			villainHandName = villainHand[0].toString()+villainHand[1].toString();
		else
			villainHandName = villainHand[1].toString()+villainHand[0].toString();
		
		EquityBreakdown equityResult = null;
		
		try{
		String sql = "select * from preflop_equity where hero_hand='"+heroHandName+"' and villain_hand = '"+villainHandName+"'";
		ResultSet r = stmt.executeQuery(sql);
		
		
		while(r.next()){
			double pwin = r.getDouble("p_win");
			double ptie = r.getDouble("p_tie");

			equityResult = new EquityBreakdown(pwin,ptie);
			break;
		}
		
		
		//entry was not in database, so calculate it and insert it into the database
		if(equityResult == null){
			//calculate equity with 300 board samples
			equityResult = EquitySimulation.calculateEquityBySimulation(heroHand, villainHand, null, 300);
			
			//add result to the lookup table
			sql = "insert into preflop_equity values (\""+heroHandName+"\", \""+villainHandName+"\", "+equityResult.pWin+", "+equityResult.pTie+");";
			stmt.executeUpdate(sql);
		}
		
		}catch(Exception event){
			event.printStackTrace();
		}
		
		return equityResult;
	}
	
	
	
	public void close(){
		try{
			if(stmt != null)stmt.close();
			if(conn != null)conn.close();
		}catch(Exception e){}
	}
	
}
