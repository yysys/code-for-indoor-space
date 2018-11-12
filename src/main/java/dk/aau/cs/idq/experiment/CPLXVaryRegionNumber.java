package dk.aau.cs.idq.experiment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import dk.aau.cs.idq.algorithm.complexquery.CPLXAlgorithms;
import dk.aau.cs.idq.algorithm.complexquery.CPLXDensity;
import dk.aau.cs.idq.datagen.ComplexQueriesGen;
import dk.aau.cs.idq.datagen.OTTGen;
import dk.aau.cs.idq.indoorentities.Query;
import dk.aau.cs.idq.utilities.ExperimentalConstant;

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
public class CPLXVaryRegionNumber implements Analysis {
	
	public int numberOfRegions = 0;								// the number of Regions
	
	public int step = 20;										// increasing step
	
	public int maxIter = 10;									// maximum Iteration
	
	public int t_min;											// the minimum time
	
	public List<Query> queries;									// the generated queries
	
	public int pK = 10;											// the parameter K
	
	/**
	 * Constructor Function
	 */
	public CPLXVaryRegionNumber(){
		
		OTTGen ottGen = new OTTGen();
		this.t_min = ottGen.generateOTT(20,0);
		
		ComplexQueriesGen queriesGen = new ComplexQueriesGen(197,0);  //QueriesGenConstant.numberOfQueries
		this.queries = queriesGen.getQueries();
		
		//this.analyzeEfficiency();
		this.analyzeEffectiveness();
		
	}

	@Override
	public void analyzeEfficiency() {
		// TODO Auto-generated method stub
		
		double []runningTime1PASS = new double[maxIter];		// the runningTime of topK1PASS
		double []runningTime = new double[maxIter];				// the runningTime of topK
		
		double []frac = {0.02, 0.04, 0.06, 0.08, 0.1, 0.12, 0.14};
		
		int myCount = 6;
		
		for(int i = myCount; i <= myCount; i++){    //for(int i = 1; i <= maxIter; i++){ 
			
			numberOfRegions = (int)(1410 * frac[i]);
			//System.out.println(numberOfRegions);
			//List<Query> subqueries = queries.subList(0, numberOfRegions);
			
			List<Query> subqueries = randomPickQueries(numberOfRegions);
			
			//System.out.println(subqueries.size());
			int runCount = 50;
			long startTime = System.currentTimeMillis();
			for(int j = 0; j < runCount; j++){CPLXAlgorithms.TopkIDRs1PASS(subqueries, t_min, pK);}
			long endtime = System.currentTimeMillis();
			System.out.println(ExperimentalConstant.countibrRunningTime1PASS/runCount);
			System.out.println(ExperimentalConstant.count1passRunningTime/runCount);
			runningTime1PASS[i] = (endtime-startTime);
			//System.out.println("numberOfRegions = " + (subqueries.size()) + " topK1PASS Running Time:\t" + runningTime1PASS[i]/(10) + "ms");
			System.out.println("-------------------------");
			startTime = System.currentTimeMillis();
			for(int j = 0; j < runCount; j++){CPLXAlgorithms.TopkIDRs(subqueries, t_min, pK);}
			endtime = System.currentTimeMillis();
			System.out.println(ExperimentalConstant.countibrRunningTime/runCount);
			System.out.println(ExperimentalConstant.countRunningTime/runCount);
			System.out.println(ExperimentalConstant.countpRunningTime/runCount);
			//System.out.println("topk Running Time:\t" + (endtime-startTime) + "ns");
			runningTime[i] = (endtime-startTime);
			//System.out.println("numberOfRegions = " + (subqueries.size()) + " topK Running Time:\t" + runningTime[i]/(10) + "ms");
			
		}
		
		/*
		for(int i = 1; i <= maxIter; i++){
			System.out.println("numberOfRegions = " + (i * step) + " topK1PASS Running Time:\t" + runningTime1PASS[i-1]/(10) + "ms");
		}
		
		for(int i = 1; i <= maxIter; i++){
			System.out.println("numberOfRegions = " + (i * step) + " topK Running Time:\t" + runningTime[i-1]/(10) + "ms");
		}
		*/
	}

	@Override
	public void analyzeEffectiveness() {
		// TODO Auto-generated method stub
		
		double []mK = new double[maxIter];
		double []mP = new double[maxIter];
		//double []mR = new double[maxIter];
		
		double []frac = {0.02, 0.04, 0.06, 0.08, 0.1, 0.12, 0.14};
		
		int myCount = 0;
		
		for(int i = myCount; i <= myCount; i++){    //for(int i = 1; i <= maxIter; i++){ 
			
			numberOfRegions = (int)(1410 * frac[i]);
			System.out.println(numberOfRegions);
			
			//while(j < 10){
				
				List<Query> subqueries = randomPickQueries(numberOfRegions);
				//System.out.println(subqueries.get(4).getQueryID());
				//System.out.println(subqueries.size() + " try it!");
				
				List<CPLXDensity> topk1PASS = CPLXAlgorithms.TopkIDRs1PASS(subqueries, t_min, pK);
				List<CPLXDensity> topkGT = CPLXAlgorithms.TopkIDRsGT(subqueries, t_min, pK);
				
				Metrics metrics = new Metrics(topk1PASS, topkGT);
				//double kend = metrics.calKandullTau();
				//System.out.println(kend);
				mK[i] = metrics.calKandullTau();
				mP[i] = metrics.calPrecision();

				
				//mR[i-1] = metrics.calRecall();
				//System.out.println("numberOfRegions = " + (i * step) + " HitRatio:\t" + mHitRatio[i-1]);

				System.out.print(mK[i] + "\t");
				System.out.println(mP[i]);


		}
	}

	private List<Query> randomPickQueries(int totalquerynumber) {
		// TODO Auto-generated method stub
		
		List<Query> subset = new ArrayList<Query>();
		
		int countPP = (int) (ComplexQueriesGen.percentilePP * (totalquerynumber));
		int countP = (int) (ComplexQueriesGen.percentileP * (totalquerynumber));
		int countSP = totalquerynumber - countP - countPP;
		
		//System.out.println(countPP);
		//System.out.println(countP);
		//System.out.println(countSP);
		
		
		int countPPALL = (int) (ComplexQueriesGen.percentilePP * ((197)));
		int countPALL = (int) (ComplexQueriesGen.percentileP * ((197)));
		
		//System.out.println(countPPALL);
		//System.out.println(countPALL);
		
		subset.addAll(pick(queries.subList(0, countPPALL), countPP));
		subset.addAll(pick(queries.subList(countPPALL, countPPALL + countPALL), countP));
		subset.addAll(pick(queries.subList(countPPALL + countPALL, (197)), countSP));
		
		return subset;
	}

	private List<Query> pick(List<Query> subList, int count) {
		// TODO Auto-generated method stub
		List<Query> result = new ArrayList<Query>();
		//System.out.println(subList.size());
		Set<Integer> picked = new HashSet<Integer>();
		
		Random random = new Random();
		int i = 0;
		while(i < count){
			int number = random.nextInt(count);
			if(!picked.contains(number)){
				result.add(subList.get(number));
				i++;
				picked.add(number);
			}
		}
		
		return result;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new CPLXVaryRegionNumber();
	}

}
