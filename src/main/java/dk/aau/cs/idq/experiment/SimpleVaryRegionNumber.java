package dk.aau.cs.idq.experiment;

import java.util.List;

import dk.aau.cs.idq.algorithm.simplequery.Algorithms;
import dk.aau.cs.idq.algorithm.simplequery.Density;
import dk.aau.cs.idq.datagen.OTTGen;
import dk.aau.cs.idq.datagen.SimpleQueriesGen;
import dk.aau.cs.idq.indoorentities.Par;

/**
 * Experimental Studies
 * Vary the parameter : Number of Regions in a generated query
 * 
 * 
 * @author lihuan
 * @version 0.1 / 2014.10.21
 * @see dk.aau.cs.idq.experiment.Analysis
 *
 */
public class SimpleVaryRegionNumber implements Analysis {
	
	public int numberOfRegions = 0;								// the number of Regions
	
	public int step = 100;										// increasing step
	
	public int maxIter = 10;									// maximum Iteration
	
	public int t_min;											// the minimum time
	
	public List<Par> queries;									// the generated queries
	
	public int pK = 10;											// the parameter K
	
	/**
	 * Constructor Function
	 */
	public SimpleVaryRegionNumber(){
		
		OTTGen ottGen = new OTTGen();
		this.t_min = ottGen.generateOTT(6,0);
		
		SimpleQueriesGen queriesGen = new SimpleQueriesGen((step * maxIter));  //QueriesGenConstant.numberOfQueries
		this.queries = queriesGen.getQueries();
		
		//this.analyzeEfficiency();
		this.analyzeEffectiveness();
		
	}

	@Override
	public void analyzeEfficiency() {
		// TODO Auto-generated method stub
		
		double []runningTime1PASS = new double[maxIter];		// the runningTime of topK1PASS
		double []runningTime = new double[maxIter];				// the runningTime of topK
		
		for(int i = 1; i <= maxIter; i++){
			
			numberOfRegions = i * step;
			
			List<Par> subqueries = queries.subList(0, numberOfRegions);
			System.out.println(subqueries.size());
			long startTime = System.nanoTime();
			Algorithms.TopkIDRs1PASS(subqueries, t_min, pK);
			long endtime = System.nanoTime();
			//System.out.println("topk1PASS Running Time:\t" + (endtime-startTime) + "ns");
			runningTime1PASS[i-1] = (endtime-startTime);
			
			startTime = System.nanoTime();
			Algorithms.TopkIDRs(subqueries, t_min, pK);
			endtime = System.nanoTime();
			//System.out.println("topk Running Time:\t" + (endtime-startTime) + "ns");
			runningTime[i-1] = (endtime-startTime);
			
		}
		
		for(int i = 1; i <= maxIter; i++){
			System.out.println("numberOfRegions = " + (i * step) + " topK1PASS Running Time:\t" + runningTime1PASS[i-1]/(1e9) + "s");
		}
		
		for(int i = 1; i <= maxIter; i++){
			System.out.println("numberOfRegions = " + (i * step) + " topK Running Time:\t" + runningTime[i-1]/(1e9) + "s");
		}
	}

	@Override
	public void analyzeEffectiveness() {
		// TODO Auto-generated method stub
		
		double []mHitRatio = new double[maxIter];				// the Hit Ratio
		
		for(int i = 1; i <= maxIter; i++){
			
			numberOfRegions = i * step;
			
			List<Par> subqueries = queries.subList(0, numberOfRegions);
			
			List<Density> topk1PASS = Algorithms.TopkIDRs1PASS(subqueries, t_min, pK);
			List<Density> topkGT = Algorithms.TopkIDRsGT(subqueries, t_min, pK);
			
			mHitRatio[i-1] = Metrics.calHitRatioSimple(topk1PASS, topkGT);
			System.out.println("numberOfRegions = " + (i * step) + " HitRatio:\t" + mHitRatio[i-1]);
			
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new SimpleVaryRegionNumber();
	}

}
