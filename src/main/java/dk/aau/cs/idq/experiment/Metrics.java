package dk.aau.cs.idq.experiment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dk.aau.cs.idq.algorithm.complexquery.CPLXDensity;
import dk.aau.cs.idq.algorithm.simplequery.Density;

/**
 * Metrics
 * 
 * 
 * @author lihuan
 * @version 0.1 / 2014.10.21
 *
 */
public class Metrics {
	
	private List<CPLXDensity> topKList = new ArrayList<CPLXDensity>();
	
	private List<CPLXDensity> gtList = new ArrayList<CPLXDensity>();
	
	private List<Integer> IDsTopKList = new ArrayList<Integer>();
	
	private List<Integer> IDsGtList = new ArrayList<Integer>();
	
	private int listSize;
	
	public Metrics(List<CPLXDensity> list1, List<CPLXDensity> list2){
		
		this.setTopKList(list1);
		this.setGtList(list2);
		listSize = list1.size();
		
		for(int i = 0; i < listSize; i++){
			IDsTopKList.add(topKList.get(i).getQuery().getQueryID());
			IDsGtList.add(gtList.get(i).getQuery().getQueryID());
		}
		
	}
	
	/**
	 * Calculate the Precision between the produced result and the ground truth
	 * 
	 */
	public double calPrecision(){
		
		int sum = 0;
		
		for(int i = 0; i < this.listSize; i++){
			if(IDsGtList.contains(IDsTopKList.get(i))){
				sum++;
			}
		}
		
		return (double) sum / this.listSize;
		
	}
	
	/**
	 * Calculate the Recall between the produced result and the ground truth
	 * 
	 */
	public double calRecall(){
		
		int sum = 0;
		
		for(int i = 0; i < this.listSize; i++){
			if(IDsTopKList.contains(IDsGtList.get(i))){
				sum++;
			}
		}
		
		return (double) sum / this.listSize;
		
	}
	
	
	public double calKandullTau(){
		
		//System.out.println(IDsTopKList);
		//System.out.println(IDsGtList);
		//System.out.println(this.listSize);
		
		if(this.listSize != 1){
			int cp = 0;
			int dp = 0;
			int tp = 0;
			
			Set<Integer> allin = new HashSet<Integer>();
			
			HashMap<Integer, Integer> rank1 = new HashMap<Integer, Integer>();
			HashMap<Integer, Integer> rank2 = new HashMap<Integer, Integer>();
		
			allin.addAll(IDsTopKList);
			allin.addAll(IDsGtList);
			
			for(int item : allin){
				//System.out.println(item);
				if(IDsTopKList.contains(item)){
					rank1.put(item, IDsTopKList.indexOf(item));
				}else{
					rank1.put(item, this.listSize);
				}
				
				if(IDsGtList.contains(item)){
					rank2.put(item, IDsGtList.indexOf(item));
				}else{
					rank2.put(item, this.listSize);
				}
			}
			
			/*
			for(Entry<Integer, Integer> entry : rank1.entrySet()){
				System.out.println(entry);
			}
			System.out.println("-----------");
			for(Entry<Integer, Integer> entry : rank2.entrySet()){
				System.out.println(entry);
			}
			System.out.println("-----------");
			*/
			Object[] allinArray = allin.toArray();
			for(int i = 0; i < allin.size(); i++){
				for(int j = i+1; j < allin.size(); j++){
					int r1 = rank1.get((int)allinArray[i]) - rank1.get((int)allinArray[j]);
					int r2 = rank2.get((int)allinArray[i]) - rank2.get((int)allinArray[j]);
					
					//System.out.println((int)allinArray[i] + "," + rank1.get((int)allinArray[i]));
					//System.out.println((int)allinArray[j] + "," + rank1.get((int)allinArray[j]));
					//System.out.println((int)allinArray[i] + "," + rank2.get((int)allinArray[i]));
					//System.out.println((int)allinArray[j] + "," + rank2.get((int)allinArray[j]));
					
					if(r1 * r2 > 0){
						cp++;
					}else if(r1 * r2 == 0){
						tp++;
					}else{
						dp++;
					}
				}
			}
			
			//System.out.println(tp);
			//System.out.println(cp);
			//System.out.println(dp);
			
			//return (tp+cp-dp)/(0.5*this.listSize*(this.listSize-1));
			
			return (double)(tp+cp-dp)/(tp+cp+dp);
			
		}else
		{
			if(IDsTopKList.get(0) == IDsGtList.get(0)){
				return 1;
			}else{
				return 0;
			}
		}
		
	}

	/**
	 * @return the topKList
	 */
	public List<CPLXDensity> getTopKList() {
		return topKList;
	}

	/**
	 * @param topKList the topKList to set
	 */
	public void setTopKList(List<CPLXDensity> topKList) {
		this.topKList = topKList;
	}

	/**
	 * @return the gtList
	 */
	public List<CPLXDensity> getGtList() {
		return gtList;
	}

	/**
	 * @param gtList the gtList to set
	 */
	public void setGtList(List<CPLXDensity> gtList) {
		this.gtList = gtList;
	}
	
	/**
	 * Calculate the Hit Ratio between the produced result and the ground truth
	 * 
	 * @param list the produced result
	 * @param list2 the ground truth
	 * @return hitRatio
	 */
	public static double calHitRatioSimple(List<Density> list, List<Density> list2){
		
		List<Integer> topKList = new ArrayList<Integer>();
		List<Integer> gtList = new ArrayList<Integer>();
		
		for(int i = 0; i < list2.size(); i++){
			topKList.add(list.get(i).getQuery().getmID());
			gtList.add(list2.get(i).getQuery().getmID());
		}
			
		int sum = 0;
		for(int i = 0; i < list.size(); i++){
			if(gtList.contains(topKList.get(i))){
				sum++;
			}
		}
		
		return (double) sum / list.size();
		
	}

}
