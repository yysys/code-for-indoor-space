package dk.aau.cs.idq.experiment;

import java.util.List;

import dk.aau.cs.idq.algorithm.complexquery.CPLXAlgorithms;
import dk.aau.cs.idq.algorithm.complexquery.CPLXDensity;
import dk.aau.cs.idq.datagen.ComplexQueriesGen;
import dk.aau.cs.idq.datagen.OTTGen;
import dk.aau.cs.idq.indoorentities.IndoorSpace;
import dk.aau.cs.idq.indoorentities.Query;

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
public class CPLXVaryK implements Analysis{
	
	public int pK = 0;														// the parameter K
	
	public int maxpK = 25;													// the maximum value of parameter K
	
	public int numberOfQueries = 60;										// the number of generated queries
	
	public int t_min;														// the minimum time
	
	public List<Query> queries;												// the generated queries

	public CPLXVaryK(){
		
		OTTGen ottGen = new OTTGen();
		this.t_min = ottGen.generateOTT(20,0);
		
		ComplexQueriesGen queriesGen = new ComplexQueriesGen(numberOfQueries,1);  //QueriesGenConstant.numberOfQueries
		this.queries = queriesGen.getQueries();
		
		System.out.println(IndoorSpace.observedObjs.size());
		
		//this.analyzeEfficiency();
		this.analyzeEffectiveness();
	}

	@Override
	public void analyzeEfficiency() {
		// TODO Auto-generated method stub
		
		double []runningTime1PASS = new double[maxpK];
		double []runningTime = new double[maxpK];
		
		CPLXAlgorithms.TopkIDRs1PASS(queries, t_min, 1);
		
		for(pK = 1; pK <= maxpK; pK++){
			
			long startTime = System.currentTimeMillis();
			for(int j = 0; j < 10; j++){CPLXAlgorithms.TopkIDRs1PASS(queries, t_min, pK);};
			long endtime = System.currentTimeMillis();
			//System.out.println("topk1PASS Running Time:\t" + (endtime-startTime) + "ns");
			runningTime1PASS[pK-1] = (endtime-startTime);
			
			startTime = System.currentTimeMillis();
			for(int j = 0; j < 10; j++){CPLXAlgorithms.TopkIDRs(queries, t_min, pK);}
			endtime = System.currentTimeMillis();
			
			//System.out.println("topk Running Time:\t" + (endtime-startTime) + "ns");
			runningTime[pK-1] = (endtime-startTime);
		}
		
		for(pK = 1; pK <= maxpK; pK++){
			System.out.println("top" + pK + " 1PASS Running Time:\t" + runningTime1PASS[pK-1]/(10) + "ms");
		}
		
		for(pK = 1; pK <= maxpK; pK++){
			System.out.println("top" + pK + " Running Time:\t" + runningTime[pK-1]/(10) + "ms");
		}
		
	}

	@Override
	public void analyzeEffectiveness() {
		// TODO Auto-generated method stub
		
		double []mK = new double[maxpK];
		double []mR = new double[maxpK];
		//double []mP = new double[maxpK];
		
		List<CPLXDensity> topk1PASS = CPLXAlgorithms.TopkIDRs1PASS(queries, t_min, queries.size());
		List<CPLXDensity> topkGT = CPLXAlgorithms.TopkIDRsGT(queries, t_min, queries.size());
		
		for(pK = 1; pK <= maxpK; pK++){
			Metrics metrics = new Metrics(topk1PASS.subList(0, pK), topkGT.subList(0, pK));
			mK[pK-1] = metrics.calKandullTau();
			mR[pK-1] = metrics.calRecall();
			//mP[pK-1] = metrics.calPrecision();
			//System.out.println("top" + pK + "\t" + mK[pK-1] + "\t" + mR[pK-1] + "\t" + mP[pK-1]);
			System.out.println(mK[pK-1]);
			//System.out.println(mR[pK-1]);
		}
		System.out.println("----------");
		for(pK = 1; pK <= maxpK; pK++){
			System.out.println(mR[pK-1]);
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new CPLXVaryK();
	}

}
