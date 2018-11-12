package dk.aau.cs.idq.experiment;

import java.util.List;

import dk.aau.cs.idq.algorithm.simplequery.Algorithms;
import dk.aau.cs.idq.algorithm.simplequery.Density;
import dk.aau.cs.idq.datagen.OTTGen;
import dk.aau.cs.idq.datagen.SimpleQueriesGen;
import dk.aau.cs.idq.indoorentities.Par;

/**
 * Experimental Studies
 * Vary the parameter : K
 * 
 * 
 * @author lihuan
 * @version 0.1 / 2014.10.21
 * @see dk.aau.cs.idq.experiment.Analysis
 *
 */
public class SimpleVaryK implements Analysis{
	
	public int pK = 0;														// the parameter K
	
	public int maxpK = 20;													// the maximum value of parameter K
	
	public int numberOfQueries = 100;										// the number of generated queries
	
	public int t_min;														// the minimum time
	
	public List<Par> queries;												// the generated queries

	public SimpleVaryK(){
		
		OTTGen ottGen = new OTTGen();
		this.t_min = ottGen.generateOTT(7,0);
		
		SimpleQueriesGen queriesGen = new SimpleQueriesGen(numberOfQueries);  //QueriesGenConstant.numberOfQueries
		this.queries = queriesGen.getQueries();
		
		//this.analyzeEfficiency();
		this.analyzeEffectiveness();
	}

	@Override
	public void analyzeEfficiency() {
		// TODO Auto-generated method stub
		
		double []runningTime1PASS = new double[maxpK];
		double []runningTime = new double[maxpK];
		
		for(pK = 1; pK <= maxpK; pK++){
			
			long startTime = System.nanoTime();
			Algorithms.TopkIDRs1PASS(queries, t_min, pK);
			long endtime = System.nanoTime();
			//System.out.println("topk1PASS Running Time:\t" + (endtime-startTime) + "ns");
			runningTime1PASS[pK-1] = (endtime-startTime);
			
			startTime = System.nanoTime();
			Algorithms.TopkIDRs(queries, t_min, pK);
			endtime = System.nanoTime();
			//System.out.println("topk Running Time:\t" + (endtime-startTime) + "ns");
			runningTime[pK-1] = (endtime-startTime);
		}
		
		for(pK = 1; pK <= maxpK; pK++){
			System.out.println("top" + pK + " 1PASS Running Time:\t" + runningTime1PASS[pK-1]/(1e9) + "s");
		}
		
		for(pK = 1; pK <= maxpK; pK++){
			System.out.println("top" + pK + " Running Time:\t" + runningTime[pK-1]/(1e9) + "s");
		}
		
	}

	@Override
	public void analyzeEffectiveness() {
		// TODO Auto-generated method stub
		
		double []mHitRatio = new double[maxpK];
		
		List<Density> topk1PASS = Algorithms.TopkIDRs1PASS(queries, t_min, queries.size());
		List<Density> topkGT = Algorithms.TopkIDRsGT(queries, t_min, queries.size());
		
		for(pK = 1; pK <= maxpK; pK++){
			
			mHitRatio[pK-1] = Metrics.calHitRatioSimple(topk1PASS.subList(0, pK), topkGT.subList(0, pK));
			System.out.println("top" + pK + " HitRatio:\t" + mHitRatio[pK-1]);
		}
		
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new SimpleVaryK();
	}

}
