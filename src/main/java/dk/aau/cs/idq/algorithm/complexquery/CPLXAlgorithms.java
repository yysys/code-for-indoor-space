package dk.aau.cs.idq.algorithm.complexquery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import dk.aau.cs.idq.algorithm.simplequery.Count;
import dk.aau.cs.idq.indoorentities.IdrObj;
import dk.aau.cs.idq.indoorentities.IndoorSpace;
import dk.aau.cs.idq.indoorentities.NextPossiblePar;
import dk.aau.cs.idq.indoorentities.Par;
import dk.aau.cs.idq.indoorentities.Query;
import dk.aau.cs.idq.indoorentities.SampledPoint;
import dk.aau.cs.idq.indoorentities.SubQuery;
import dk.aau.cs.idq.utilities.DataGenConstant;
import dk.aau.cs.idq.utilities.ExperimentalConstant;
import dk.aau.cs.idq.utilities.Flag;
import dk.aau.cs.idq.utilities.OTTGenConstant;

/**
 * Algorithms for processing CPLX Queries
 * 
 * @author lihuan
 * @version 0.1 / 2014.10.21
 * 
 */
public class CPLXAlgorithms {

	/**
	 * COUNT4ibr
	 * for objects overlaps the IBR
	 * 
	 * @param indoorBufferRegion
	 * @return resultCount
	 */
	private static Count COUNT4ibr(CPLXIndoorBufferRegionD2D indoorBufferRegion) {
		// TODO Auto-generated method stub

		double mCount = 0;	// initialize mCount 
		
		List<SampledPoint> sampledPointSet = new ArrayList<>();
		List<Integer> sampledPointSetIDs = new ArrayList<>();
		
		// to proceed the fullycoveredPar
		for (Par fullyCoveredPar : indoorBufferRegion.getFullyCoveredPar()) {
			//System.out.println(fullyCoveredPar.getmID());
			//System.out.println(fullyCoveredPar.getmSampledPoints().size());
			for (SampledPoint item : fullyCoveredPar.getmSampledPoints()) {
				if (!sampledPointSetIDs.contains(item.getSpID())) {
					sampledPointSet.add(item);
					sampledPointSetIDs.add(item.getSpID());
				}
			}
		}

		
		// to proceed the overlappedPar
		List<Par> fullycoveredpars = indoorBufferRegion.getFullyCoveredPar();
		for (Entry<String, NextPossiblePar> entry : indoorBufferRegion
				.getOverlappedPar().entrySet()) {
			NextPossiblePar overlappedPar = entry.getValue();
			Par curPar = overlappedPar.getPossibleNextPar();
			
			if (!fullycoveredpars.contains(curPar)) {
				//System.out.println("**" + curPar.getmID() + "\t" + overlappedPar.getDistanceToContinue());
				for (SampledPoint item : curPar.getmSampledPoints()) {
					if (!sampledPointSetIDs.contains(item.getSpID())) {
						if(entry.getValue().Contain(item)){
							sampledPointSet.add(item);
							sampledPointSetIDs.add(item.getSpID());
						}
					}
				}
			}
		}
		
		
		// to proceed the undeterminedPar
		for (SubQuery subQuery : indoorBufferRegion.getUndeterminedPar()) {
			if (!fullycoveredpars.contains(subQuery.getCoveringPar())) {
				//System.out.println("##" + subQuery.getCoveringPar().getmID());
				for (SampledPoint item : subQuery.getCoveringPar()
						.getmSampledPoints()) {
					if (!sampledPointSetIDs.contains(item.getSpID())) {
						if(indoorBufferRegion.getQuery().contain(item)){
							sampledPointSet.add(item);
							sampledPointSetIDs.add(item.getSpID());
						}else if(subQuery.getMinDist(item) <= indoorBufferRegion.getMaxMovingDistance()){
							sampledPointSet.add(item);
							sampledPointSetIDs.add(item.getSpID());
						}
						
					}
				}
			}
		}
		
		// calculate the count
		// System.out.println(sampledPointSet.size());
		for(SampledPoint sp : sampledPointSet){
			mCount = mCount + sp.getContributes();
		}
		
		Count resultCount = new Count(mCount, sampledPointSet);

		return resultCount;
	}
	

	/**
	 * COUNTp
	 * IRHP -> IRP
	 * 
	 * @param query
	 * @param resCount
	 * @param curTime
	 * @return count_u
	 */
	private static double COUNTp(Query query, Count resCount, int curTime) {
		// TODO Auto-generated method stub

		double count_u = 0;
		
		for(SampledPoint sampledPoint : resCount.getSampledPointSet()){
//			ExperimentalConstant.calculated++;
			CPLXIndoorURD2DArcs indoorUR;
			int spId = sampledPoint.getSpID();
			if(IndoorSpace.cacheURs.containsKey(spId)){
				indoorUR = IndoorSpace.cacheURs.get(spId);
			}else {
				indoorUR = new CPLXIndoorURD2DArcs(sampledPoint, curTime, 1);
				IndoorSpace.cacheURs.put(spId, indoorUR);
				ExperimentalConstant.callingCountU++;
			}
			double part = indoorUR.getUnionPart(query);
			count_u = count_u + part * sampledPoint.getContributes();

		}
		
		return count_u;
	}

	
	/**
	 * COUNT
	 * IBR -> IRHP
	 * 
	 * @param query
	 * @param resCount
	 * @param curTime
	 * @return Count
	 */
	private static Count COUNT(Query query, Count resCount, int curTime) {
		// TODO Auto-generated method stub
		
		
		double count_c = 0;
		List<SampledPoint> set_u = new ArrayList<SampledPoint>();
		
		for(SampledPoint sampledPoint : resCount.getSampledPointSet()){
			CPLXIndoorURD2DArcs indoorUR = new CPLXIndoorURD2DArcs(sampledPoint, curTime, 0);
			int flag = indoorUR.topoloyRelation(query);
			if(flag == 2){ // can be fully covered 
				count_c = count_c + sampledPoint.getContributes();
			}
			if(flag == 1){ // cannot be fully covered
				set_u.add(sampledPoint);
			}
		}
		
		// add the contributes to the count_c together
		double count_cu = count_c;
		for(SampledPoint sp : set_u){
			count_cu = count_cu + sp.getContributes();
		}
		
		return new Count(count_cu, set_u);
	}
	
	
	/**
	 * COUNT1PASS 
	 * IBR -> IRP
	 * 
	 * @param query
	 * @param resCount
	 * @param curTime
	 * @return count
	 */
	private static double COUNT1Pass(Query query, Count resCount, int curTime) {
		// TODO Auto-generated method stub

		double count = 0;
		for (SampledPoint sampledPoint : resCount.getSampledPointSet()) {

			int spId = sampledPoint.getSpID();

			CPLXIndoorURD2DArcs indoorUR;

			if(IndoorSpace.cacheURs1Pass.containsKey(spId)){
				indoorUR = IndoorSpace.cacheURs1Pass.get(spId);
			}else{
				indoorUR = new CPLXIndoorURD2DArcs(sampledPoint, curTime, 0);
			}

			int flag = indoorUR.topoloyRelation(query);
			if (flag == 2) { // can be fully covered
				count = count + sampledPoint.getContributes();
				//query.addContributes(sampledPoint.getSpID(), DataGenConstant.percentContributes);
			}
			else if (flag == 1) { // cannot be fully covered

				if(indoorUR.getFlag() != 1){
					indoorUR.setFlag(1);
					IndoorSpace.cacheURs1Pass.put(spId, indoorUR);
					ExperimentalConstant.callingCountU1Pass++;
				}

				double part = indoorUR.getUnionPart(query);
				// System.out.println(part);
				count = count + (part * sampledPoint.getContributes());
				if (part != 0) {
					//query.addContributes(sampledPoint.getSpID(), part * DataGenConstant.percentContributes);
				}
			}
			
			/*
			CPLXIndoorURD2DArcs indoorUR = new CPLXIndoorURD2DArcs(sampledPoint, curTime, 1);
			double part = indoorUR.getUnionPart(query);
			// System.out.println(part);
			count = count + (part * DataGenConstant.percentContributes);
			if (part != 0) {
				query.addContributes(sampledPoint.getSpID(), part
						* DataGenConstant.percentContributes);
			}*/
			
		}
		
		return count;
	}
	
	
	/**
	 * TopkIDRs1PASS
	 * two-phases pruning
	 * 
	 * @param queries
	 * @param t_min
	 * @param pK
	 * @return topKresult topK density Region
	 */
	public static List<CPLXDensity> TopkIDRs1PASS(List<Query> queries,
			int t_min, int pK) {

		List<CPLXDensity> topKresult = new ArrayList<CPLXDensity>();

		double maxMovingDistance = (OTTGenConstant.minSamplingPeriod - 1)
				* DataGenConstant.maxVelocity;
		int curTime = t_min + (OTTGenConstant.minSamplingPeriod - 1);

		long startTime = System.currentTimeMillis();
		for (Query query : queries) {

			CPLXIndoorBufferRegionD2D indoorBufferRegion = new CPLXIndoorBufferRegionD2D(
					query, maxMovingDistance);

			Count resCount = COUNT4ibr(indoorBufferRegion);
			IndoorSpace.hr.put(query.getQueryID(), resCount);

			CPLXDensity density = new CPLXDensity(query, Flag.IBR,
					resCount.getmCount());
			IndoorSpace.c_heap.add(density);

		}
		long endTime = System.currentTimeMillis();
		ExperimentalConstant.countibrRunningTime1PASS = ExperimentalConstant.countibrRunningTime1PASS + (endTime-startTime);
		
		
		while (!IndoorSpace.c_heap.isEmpty()) {

			CPLXDensity curDensity = IndoorSpace.c_heap.first();
			IndoorSpace.c_heap.remove(curDensity);
			//System.out.println(curDensity);

			if (curDensity.getFlag() == Flag.IBR) {

				Query query = curDensity.getQuery();
				Count resCount = IndoorSpace.hr.get(query.getQueryID());
				startTime = System.currentTimeMillis();
				double count = COUNT1Pass(curDensity.getQuery(), resCount,
						curTime);
				endTime = System.currentTimeMillis();
				ExperimentalConstant.count1passRunningTime = ExperimentalConstant.count1passRunningTime + (endTime-startTime);
				//System.out.println(query.getQueryID() + "\t" + count);

				IndoorSpace.c_heap.add(new CPLXDensity(query, Flag.IRP, count));

			}

			else {
				topKresult.add(curDensity);
				if (topKresult.size() == pK) {
					// System.out.println("TOPK DONE!");
					break;
				}
			}

		}
		
		
		return topKresult;
	}
	
	
	/**
	 * TopkIDRs
	 * three-phases pruning
	 * 
	 * @param queries
	 * @param t_min
	 * @param pK
	 * @return topKresult topK density Region
	 */
	public static List<CPLXDensity> TopkIDRs(List<Query> queries, int t_min,
			int pK) {

		List<CPLXDensity> topKresult = new ArrayList<CPLXDensity>();

		double maxMovingDistance = (OTTGenConstant.minSamplingPeriod - 1)
				* DataGenConstant.maxVelocity;
		int curTime = t_min + (OTTGenConstant.minSamplingPeriod - 1);
		
		long startTime = System.currentTimeMillis();
		for (Query query : queries) {

			CPLXIndoorBufferRegionD2D indoorBufferRegion = new CPLXIndoorBufferRegionD2D(
					query, maxMovingDistance);

			Count resCount = COUNT4ibr(indoorBufferRegion);
			IndoorSpace.hr.put(query.getQueryID(), resCount);

			CPLXDensity density = new CPLXDensity(query, Flag.IBR,
					resCount.getmCount());
			IndoorSpace.c_heap.add(density);

		}
		long endTime = System.currentTimeMillis();
		ExperimentalConstant.countibrRunningTime = ExperimentalConstant.countibrRunningTime + (endTime-startTime);
		
		
		while (!IndoorSpace.c_heap.isEmpty()) {

			CPLXDensity curDensity = IndoorSpace.c_heap.first();
			IndoorSpace.c_heap.remove(curDensity);
			//System.out.println(curDensity);
			
			if (curDensity.getFlag() == Flag.IBR) {

				Query query = curDensity.getQuery();
				Count resCount = IndoorSpace.hr.get(query.getQueryID());
				startTime = System.currentTimeMillis();
				Count hpCount = COUNT(query, resCount,curTime);
				endTime = System.currentTimeMillis();
				ExperimentalConstant.countRunningTime = ExperimentalConstant.countRunningTime + (endTime-startTime);
				if(hpCount.getSampledPointSet().size() == 0){
					IndoorSpace.c_heap.add(new CPLXDensity(query, Flag.IRP, hpCount.getmCount()));
				}else{
					IndoorSpace.hr.put(query.getQueryID(), hpCount);
					IndoorSpace.c_heap.add(new CPLXDensity(query, Flag.IRHP, hpCount.getmCount()));
				}
				
			}
			else if(curDensity.getFlag() == Flag.IRHP){
				
				Query query = curDensity.getQuery();
				Count resCount = IndoorSpace.hr.get(query.getQueryID());
				
				double count_u = 0;
				for(SampledPoint sp : resCount.getSampledPointSet()){
					count_u = count_u + sp.getContributes();
				}
				double tempCount = resCount.getmCount() - (count_u);
				
				startTime = System.currentTimeMillis();
				double count = tempCount + COUNTp(query, resCount, curTime);
				endTime = System.currentTimeMillis();
				ExperimentalConstant.countpRunningTime = ExperimentalConstant.countpRunningTime + (endTime-startTime);
				IndoorSpace.c_heap.add(new CPLXDensity(query, Flag.IRP, count));
			}
			else {
				topKresult.add(curDensity);
				if (topKresult.size() == pK) {
					// System.out.println("TOPK DONE!");
					break;
				}
			}

		}
		return topKresult;
	}
	
	
	/**
	 * calculate the Ground Truth loops
	 * 
	 * @param queries
	 * @param t_min
	 * @param pK
	 * @return topKresult topK density Region
	 */
	public static List<CPLXDensity> TopkIDRsGT(List<Query> queries, int t_min,
			int pK) {

		List<CPLXDensity> topKresult = new ArrayList<>();
		Hashtable<Integer, Double> densityHT = new Hashtable<>();

		int curTime = t_min + (OTTGenConstant.minSamplingPeriod - 1);

		for (Entry<Integer, IdrObj> entry : IndoorSpace.observedObjs.entrySet()) {
			// System.out.println("-------------------------");
			IdrObj idrobj = entry.getValue();
			SampledPoint sampledPoint = new SampledPoint(idrobj.getmID(),idrobj.getmTruePos().getX(),idrobj.getmTruePos().getY(), idrobj.getmTruePos().getmFloor(),idrobj.getCurPar());
			CPLXIndoorURD2DArcs indoorUR = new CPLXIndoorURD2DArcs(sampledPoint, curTime, 1);
			for (Query query : queries) {
				if (query.getmFloor() == sampledPoint.getmFloor()) {
					double part = indoorUR.getUnionPart(query);
					if (densityHT.containsKey(query.getQueryID())) {
						double tempCount = densityHT.get(query.getQueryID());
						densityHT.put(query.getQueryID(),
								(tempCount + part));
					} else {
						densityHT.put(query.getQueryID(), part);
					}
				}
			}
		}

		for (Query query : queries) {
			topKresult.add(new CPLXDensity(query, Flag.IRP, densityHT.get(query
					.getQueryID())));
		}

		Collections.sort(topKresult);
		return topKresult.subList(0, pK);
	}
	
	
	/**
	 * calculate the Densities by iterating through samples
	 * NMsamples
	 * 
	 * @param queries
	 * @param t_min
	 * @param pK
	 * @return topKresult topK density Region
	 */
	public static List<CPLXDensity> TopkIDRsSamples(List<Query> queries, int t_min,
			int pK) {
		

		List<CPLXDensity> topKresult = new ArrayList<CPLXDensity>();
		Hashtable<Integer, Double> densityHT = new Hashtable<Integer, Double>();

		int curTime = t_min + (OTTGenConstant.minSamplingPeriod - 1);

		for (SampledPoint sampledPoint : IndoorSpace.gSampledPoints) {
			CPLXIndoorURD2DArcs indoorUR = new CPLXIndoorURD2DArcs(sampledPoint, curTime, 1);
			ExperimentalConstant.callingCountUSamples++;
			for (Query query : queries) {
				if (query.getmFloor() == sampledPoint.getmFloor()) {
					double part = indoorUR.getUnionPart(query) * sampledPoint.getContributes();
					if (part != 0) {
						//query.addContributes(sampledPoint.getSpID(), part);
					}
					if (densityHT.containsKey(query.getQueryID())) {
						double tempCount = densityHT.get(query.getQueryID());
						densityHT.put(query.getQueryID(),
								(tempCount + part));
					} else {
						densityHT.put(query.getQueryID(), part);
					}
				}
			}
		}

		for (Query query : queries) {
			topKresult.add(new CPLXDensity(query, Flag.IRP, densityHT.get(query
					.getQueryID())));
		}

		Collections.sort(topKresult);
		return topKresult.subList(0, pK);
	}
	
	
	/**
	 * calculate the Densities with Indoor Buffer Region
	 * NMibr
	 * 
	 * @param queries
	 * @param t_min
	 * @param pK
	 * @return topKresult topK density Region
	 */
	public static List<CPLXDensity> TopkIDRswithIBR(List<Query> queries,
			int t_min, int pK) {
		
		List<CPLXDensity> topKresult = new ArrayList<CPLXDensity>();

		double maxMovingDistance = (OTTGenConstant.minSamplingPeriod - 1)
				* DataGenConstant.maxVelocity;
		int curTime = t_min + (OTTGenConstant.minSamplingPeriod - 1);

		for (Query query : queries) {

			CPLXIndoorBufferRegionD2D indoorBufferRegion = new CPLXIndoorBufferRegionD2D(
					query, maxMovingDistance);
			long startTime = System.currentTimeMillis();
			Count resCount = COUNT4ibr(indoorBufferRegion);
			long endTime = System.currentTimeMillis();
			ExperimentalConstant.naivecountibrRunningTime = ExperimentalConstant.naivecountibrRunningTime + (endTime-startTime);
			double count = 0;
			
			startTime = System.currentTimeMillis();
			for(SampledPoint sampledpoint :resCount.getSampledPointSet()){
				ExperimentalConstant.naivecalculated++;
				CPLXIndoorURD2DArcs indoorUR = new CPLXIndoorURD2DArcs(sampledpoint, curTime, 1);
				count = count + sampledpoint.getContributes() * indoorUR.getUnionPart(query);
				ExperimentalConstant.callingCountUIBR++;
			}
			endTime = System.currentTimeMillis();
			ExperimentalConstant.naivecountRunningTime = ExperimentalConstant.naivecountRunningTime + (endTime-startTime);
			
			
			CPLXDensity density = new CPLXDensity(query, Flag.IRP, count);
			topKresult.add(density);
		}
		
		long startTime = System.currentTimeMillis();
		Collections.sort(topKresult);
		long endTime = System.currentTimeMillis();
		ExperimentalConstant.naiverankingRunningTime = ExperimentalConstant.naiverankingRunningTime + (endTime-startTime);
		
		return topKresult.subList(0, pK);
	}
	
	
	/**
	 * calculate the densities with General Buffer Region
	 * NMgbr
	 * 
	 * @param queries
	 * @param t_min
	 * @param pK
	 * @return topKresult topK density Region
	 */
	public static List<CPLXDensity> TopkIDRswithGBR(List<Query> queries,
			int t_min, int pK) {
		
		List<CPLXDensity> topKresult = new ArrayList<CPLXDensity>();

		double maxMovingDistance = (OTTGenConstant.minSamplingPeriod - 1)
				* DataGenConstant.maxVelocity;
		int curTime = t_min + (OTTGenConstant.minSamplingPeriod - 1);

		for (Query query : queries) {
			
			Iterator<SampledPoint> coveredSampledPoints = IndoorSpace.gSPRTree.get(query.getmFloor()).find(query.getX1()-maxMovingDistance, query.getY1()-maxMovingDistance, query.getX2()+maxMovingDistance, query.getY2() + maxMovingDistance).iterator();

			double count = 0;
			while(coveredSampledPoints.hasNext()){
				SampledPoint sampledpoint = coveredSampledPoints.next();
				CPLXIndoorURD2DArcs indoorUR = new CPLXIndoorURD2DArcs(sampledpoint, curTime, 1);
				count = count + sampledpoint.getContributes() * indoorUR.getUnionPart(query);
				ExperimentalConstant.callingCountUGBR++;
			}
			CPLXDensity density = new CPLXDensity(query, Flag.IRP, count);
			topKresult.add(density);

		}
		
		long startTime = System.currentTimeMillis();
		Collections.sort(topKresult);
		long endTime = System.currentTimeMillis();
		ExperimentalConstant.naiverankingRunningTime = ExperimentalConstant.naiverankingRunningTime + (endTime-startTime);
		
		return topKresult.subList(0, pK);
	}

}
