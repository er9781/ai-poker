package equityCalculations;

public class EquityBreakdown {
	
	public double pWin;
	public double pTie;
	public double pLose;
	
	public EquityBreakdown(double pWin, double pTie, double pLose){
		this.pWin = pWin;
		this.pTie = pTie;
		this.pLose = pLose;
	}
	
	public EquityBreakdown(double pWin, double pTie){
		this(pWin, pTie, 1-pWin-pTie);
	}
	
	public String toString(){
		return "pWin: "+pWin+", pTie: "+pTie+", pLose: "+pLose;
		
	}
	
}
