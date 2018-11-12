package dk.aau.cs.idq.experiment;

/**
 * Analysis
 * an interface for experimental studies
 * 
 * 
 * @author lihuan
 * @version 0.1 / 2014.10.21
 *
 */
public interface Analysis {

	/** to analyze the efficiency of proposed method */
	public abstract void analyzeEfficiency();
	
	/** to analyze the effectiveness of proposed method */
	public abstract void analyzeEffectiveness();
	
}
