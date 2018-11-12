
package dk.aau.cs.idq.algorithm.simplequery;


/**
 * test the TopKIDRS algorithms
 * 
 * @author lihuan
 * @version 0.1 / 2014.10.21
 * @see dk.aau.cs.idq.algorithm.simplequery.Algorithms
 */
public class TopKIDRS {

	/**
	 * @param args
	 */
	/*
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		OTTGen ottGen = new OTTGen();
		int t_min = ottGen.generateOTT(6,0);
		
		
		SimpleQueriesGen queriesGen = new SimpleQueriesGen(40);  //QueriesGenConstant.numberOfQueries
		List<Par> queries = queriesGen.getQueries();
		
		int pK = 10;

		long startTime = System.nanoTime();
		//List<Density> topk1PASS = Algorithms.TopkIDRs1PASS(queries, t_min, pK);
		long endtime = System.nanoTime();
		System.out.println("topk1PASS Running Time:\t" + (endtime-startTime)/(1e9) + "s");
		
		
		startTime = System.nanoTime();
		List<Density> topk = Algorithms.TopkIDRs(queries, t_min, pK);
		endtime = System.nanoTime();
		System.out.println("topk Running Time:\t" + (endtime-startTime)/(1e9) + "s");
		
		
		startTime = System.nanoTime();
		List<Density> topkGT = Algorithms.TopkIDRsGT(queries, t_min, pK);
		endtime = System.nanoTime();
		System.out.println("topkGT Running Time:\t" + (endtime-startTime)/(1e9) + "s");
		
		
		for(Density item : topk){
			System.out.println(item.getQuery().getmID()+ ">" + item.getDensity());
		}
		
		System.out.println("--------------------------");
		
		for(Density item : topkGT){
			System.out.println(item.getQuery().getmID()+ ">" + item.getDensity());
		}
		
		
	}
	*/
}
