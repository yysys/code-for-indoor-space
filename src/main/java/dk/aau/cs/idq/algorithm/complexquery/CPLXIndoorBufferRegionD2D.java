package dk.aau.cs.idq.algorithm.complexquery;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import dk.aau.cs.idq.indoorentities.Door;
import dk.aau.cs.idq.indoorentities.IndoorSpace;
import dk.aau.cs.idq.indoorentities.NextPossiblePar;
import dk.aau.cs.idq.indoorentities.Par;
import dk.aau.cs.idq.indoorentities.Query;
import dk.aau.cs.idq.indoorentities.SubQuery;
import dk.aau.cs.idq.utilities.Constant;

/**
 * Complex Indoor Buffer Region
 * Determined with D2DMatrix
 * 
 * @author lihuan
 * @version 0.1 / 2014.10.21
 * 
 */
public class CPLXIndoorBufferRegionD2D {
	
	private Query query;																						// the query

	private double maxMovingDistance;																			// the maximum walking distance
	
	private List<Par> fullyCoveredPar = new ArrayList<Par>();													// the fullyCovered partitions

	private List<SubQuery> undeterminedPar = new ArrayList<SubQuery>();											// the undetermined partitions	

	private Hashtable<String, NextPossiblePar> overlappedPar = new Hashtable<String, NextPossiblePar>();

	
	/**
	 * Constructor Function
	 * 
	 * @param query
	 * @param maxMovingDistance
	 */
	public CPLXIndoorBufferRegionD2D(Query query, double maxMovingDistance) {
		super();
		this.query = query;
		this.setMaxMovingDistance(maxMovingDistance);
		getURPar(query, maxMovingDistance);
	}
	
	
	/**
	 * get the Uncertainty Region (consists of several Pars) of the Query
	 * 
	 * @param query
	 * @param maxMovingDistance
	 */
	private void getURPar(Query query, double maxMovingDistance) {
		// TODO Auto-generated method stub
		
		for (SubQuery subQuery : query.getSubQueries()) {
			
			Par coveredPar = subQuery.getCoveringPar();
			
			if(subQuery.getCovered() == 1){
				this.fullyCoveredPar.add(coveredPar);
			}else{
				this.undeterminedPar.add(subQuery);
			}
		}
		
		double marginLeft = query.getX1() - maxMovingDistance;
		double marginRight = query.getX2() + maxMovingDistance;
		double marginTop = query.getY1() - maxMovingDistance;
		double marginBottom = query.getY2() + maxMovingDistance;
		
		Iterable<Par> pars = IndoorSpace.gRTree.find(marginLeft, marginTop, marginRight, marginBottom);
		
		List<NextPossiblePar> total = new ArrayList<NextPossiblePar>();
		
		for(Par par : pars){
			int modeID = par.getmID();
			int realParID = query.getmFloor() * (IndoorSpace.gNumberParsPerFloor) + modeID;
			Par realPar = IndoorSpace.gPartitions.get(realParID);
			if(!query.getcoveredPars().contains(realPar)){
				List<NextPossiblePar> possiblePars = topology(realPar, query.getExitDoors());
				total.addAll(possiblePars);
			}
		}
		
		for(NextPossiblePar nextPossiblePar :total){
			if(nextPossiblePar.isFullyCovered()){
				if(!this.fullyCoveredPar.contains(nextPossiblePar.getPossibleNextPar())){
					this.fullyCoveredPar.add(nextPossiblePar.getPossibleNextPar());
				}
			}else{
				String key = nextPossiblePar.getPossibleNextPar() + "#" + nextPossiblePar.getPossibleTroughDoor();
				if(!this.overlappedPar.containsKey(key)){
					this.overlappedPar.put(key, nextPossiblePar);
				}else{
					NextPossiblePar pre = this.overlappedPar.get(key);
					if(pre.distanceToContinue < nextPossiblePar.distanceToContinue){
						this.overlappedPar.put(key, nextPossiblePar);
					}
				}
			}
		}
		
	}

	
	/**
	 * get the coverage area of the realPar
	 * 
	 * @param realPar
	 * @param map
	 * @return result nextPossiblePar
	 */
	private List<NextPossiblePar> topology(Par realPar, Map<Door, Double> map) {
		// TODO Auto-generated method stub
		
		List<NextPossiblePar> result = new ArrayList<NextPossiblePar>();
		
		List<Integer> doorIDs = realPar.getmDoors();

		for(int doorID : doorIDs){
			int modeID = doorID % IndoorSpace.gNumberDoorsPerFloor;
			double minDist = Constant.DISTMAX;
			for(Entry<Door, Double> entry : map.entrySet()){
				//System.out.println(entry.getKey().getmID() + "," + entry.getValue());
				int mode = entry.getKey().getmID() % IndoorSpace.gNumberDoorsPerFloor;
				
				double dist = IndoorSpace.gD2DMatrix[modeID][mode] + entry.getValue();
				//System.out.println(dist + ">" + distCovered);
				if(dist < minDist){
					minDist = dist;
				}
			}
			
			if(minDist < this.maxMovingDistance){
				result.add(new NextPossiblePar(IndoorSpace.gDoors.get(doorID), realPar, (this.maxMovingDistance - minDist)));
			}
		}
		
		return result;
	}

	/**
	 * @return the query
	 */
	public Query getQuery() {
		return query;
	}

	/**
	 * @param query the query to set
	 */
	public void setQuery(Query query) {
		this.query = query;
	}

	/**
	 * @return the maxMovingDistance
	 */
	public double getMaxMovingDistance() {
		return maxMovingDistance;
	}

	/**
	 * @param maxMovingDistance the maxMovingDistance to set
	 */
	public void setMaxMovingDistance(double maxMovingDistance) {
		this.maxMovingDistance = maxMovingDistance;
	}
	
	
	/**
	 * @return the fullyCoveredPar
	 */
	public List<Par> getFullyCoveredPar() {
		return fullyCoveredPar;
	}

	/**
	 * @param fullyCoveredPar the fullyCoveredPar to set
	 */
	public void setFullyCoveredPar(List<Par> fullyCoveredPar) {
		this.fullyCoveredPar = fullyCoveredPar;
	}

	/**
	 * @return the undeterminedPar
	 */
	public List<SubQuery> getUndeterminedPar() {
		return undeterminedPar;
	}

	/**
	 * @param undeterminedPar the undeterminedPar to set
	 */
	public void setUndeterminedPar(List<SubQuery> undeterminedPar) {
		this.undeterminedPar = undeterminedPar;
	}

	/**
	 * @return the overlappedPar
	 */
	public Hashtable<String, NextPossiblePar> getOverlappedPar() {
		return overlappedPar;
	}

	/**
	 * @param overlappedPar the overlappedPar to set
	 */
	public void setOverlappedPar(Hashtable<String, NextPossiblePar> overlappedPar) {
		this.overlappedPar = overlappedPar;
	}

	
	/*
	public static void main(String []args){
		
		OTTGen ottGen = new OTTGen();
		ottGen.generateOTT(21,0);
		
		
		ComplexQueriesGen queriesGen = new ComplexQueriesGen(20,0);  //QueriesGenConstant.numberOfQueries
		List<Query> queries = queriesGen.getQueries();
		
		Query mQuery = queries.get(0);
		
		double maxMovingDistance = (OTTGenConstant.minSamplingPeriod - 1)
				* DataGenConstant.maxVelocity;
		
		long startTime = System.currentTimeMillis();
		CPLXIndoorBufferRegionD2D ibr = new CPLXIndoorBufferRegionD2D(mQuery, maxMovingDistance);
		System.out.println(ibr.getOverlappedPar().size());
		long endTime = System.currentTimeMillis();
		System.out.println((endTime-startTime)); 
	}
	*/
	

}
