package dk.aau.cs.idq.algorithm.simplequery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map.Entry;

import dk.aau.cs.idq.indoorentities.IdrObj;
import dk.aau.cs.idq.indoorentities.IndoorSpace;
import dk.aau.cs.idq.indoorentities.NextPossiblePar;
import dk.aau.cs.idq.indoorentities.Par;
import dk.aau.cs.idq.indoorentities.SampledPoint;
import dk.aau.cs.idq.utilities.DataGenConstant;
import dk.aau.cs.idq.utilities.Flag;
import dk.aau.cs.idq.utilities.OTTGenConstant;

/**
 * Algorithms
 * 
 * @author lihuan
 * @version 0.1 / 2014.10.15
 * 
 */
public class Algorithms {

	/**
	 * COUNT4ibr
	 * for objects overlaps the IBR
	 * 
	 * @param indoorBufferRegion
	 * @return resultCount
	 */
	public static Count COUNT4ibr(IndoorBufferRegion indoorBufferRegion) {

		double mCount = 0;
		List<SampledPoint> sampledPointSet = new ArrayList<SampledPoint>();

		for (Par fullyCoveredPar : indoorBufferRegion.getFullyCoveredPar()) {
			for (SampledPoint item : fullyCoveredPar.getmSampledPoints()) {
				sampledPointSet.add(item);
				// mCount = mCount + DataGenConstant.percentContributes;
			}
		}

		for (NextPossiblePar overlappedPar : indoorBufferRegion
				.getOverlappedPar()) {
			Par curPar = overlappedPar.getPossibleNextPar();
			for (SampledPoint item : curPar.getmSampledPoints()) {
				if (overlappedPar.Contain(item)) {
					sampledPointSet.add(item);
					// mCount = mCount + DataGenConstant.percentContributes;
				}
			}
		}

		// System.out.println("mCount:" + mCount);
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
	 */
	private static double COUNTp(Par query, Count resCount, int curTime) {
		// TODO Auto-generated method stub

		double count_u = 0;

		for (SampledPoint sampledPoint : resCount.getSampledPointSet()) {

			IndoorUR indoorUR = new IndoorUR(sampledPoint, curTime, 1);
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
	 */
	private static Count COUNT(Par query, Count resCount, int curTime) {
		// TODO Auto-generated method stub

		double count_c = 0;
		List<SampledPoint> set_u = new ArrayList<SampledPoint>();

		for (SampledPoint sampledPoint : resCount.getSampledPointSet()) {
			IndoorUR indoorUR = new IndoorUR(sampledPoint, curTime, 0);
			int flag = indoorUR.topoloyRelation(query);
			if (flag == 2) {
				count_c = count_c + sampledPoint.getContributes();
			}
			if (flag == 1) {
				set_u.add(sampledPoint);
			}
		}
		
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
	private static double COUNT1Pass(Par query, Count resCount, int curTime) {
		// TODO Auto-generated method stub

		double count = 0;

		for (SampledPoint sampledPoint : resCount.getSampledPointSet()) {
			IndoorUR indoorUR = new IndoorUR(sampledPoint, curTime, 0);
			int flag = indoorUR.topoloyRelation(query);
			if (flag == 2) {
				count = count + sampledPoint.getContributes();
			}
			if (flag == 1) {
				indoorUR.setFlag(1);
				double part = indoorUR.getUnionPart(query);
				// System.out.println(part);
				count = count + part * sampledPoint.getContributes();
			}
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
	 * @return topK density Region
	 */
	public static List<Density> TopkIDRs1PASS(List<Par> queries, int t_min,
			int pK) {

		List<Density> topKresult = new ArrayList<Density>();

		double maxMovingDistance = (OTTGenConstant.minSamplingPeriod - 1)
				* DataGenConstant.maxVelocity;

		int curTime = t_min + (OTTGenConstant.minSamplingPeriod - 1);

		double startTime = System.nanoTime();
		for (Par query : queries) {
			IndoorBufferRegion indoorBufferRegion = new IndoorBufferRegion(
					query, maxMovingDistance);

			Count resCount = COUNT4ibr(indoorBufferRegion);
			IndoorSpace.hr.put(query.getmID(), resCount);

			Density density = new Density(query, Flag.IBR, resCount.getmCount());
			IndoorSpace.heap.add(density);
		}
		double endTime = System.nanoTime();
		System.out.println("topk1PASS COUNT4ibr Running Time:\t"
				+ (endTime - startTime) / (1e9) + "s");

		while (!IndoorSpace.heap.isEmpty()) {

			Density curDensity = IndoorSpace.heap.first();
			IndoorSpace.heap.remove(curDensity);
			// System.out.println(curDensity);

			if (curDensity.getFlag() == Flag.IBR) {
				Par query = curDensity.getQuery();
				Count resCount = IndoorSpace.hr.get(query.getmID());

				double count = COUNT1Pass(curDensity.getQuery(), resCount,
						curTime);

				// System.out.println(resCount.getmCount() + ">" + count);

				IndoorSpace.heap.add(new Density(query, Flag.IRP, count));

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
	 * @return topK density Region
	 */
	public static List<Density> TopkIDRs(List<Par> queries, int t_min, int pK) {

		List<Density> topKresult = new ArrayList<Density>();

		double maxMovingDistance = (OTTGenConstant.minSamplingPeriod - 1)
				* DataGenConstant.maxVelocity;

		int curTime = t_min + (OTTGenConstant.minSamplingPeriod - 1);

		double startTime = System.nanoTime();
		for (Par query : queries) {

			IndoorBufferRegion indoorBufferRegion = new IndoorBufferRegion(
					query, maxMovingDistance);
			Count resCount = COUNT4ibr(indoorBufferRegion);
			IndoorSpace.hr.put(query.getmID(), resCount);
			Density density = new Density(query, Flag.IBR, resCount.getmCount());
			IndoorSpace.heap.add(density);
		}
		double endTime = System.nanoTime();
		System.out.println("topk COUNT4ibr Running Time:\t"
				+ (endTime - startTime) / (1e9) + "s");

		while (!IndoorSpace.heap.isEmpty()) {

			Density curDensity = IndoorSpace.heap.first();
			IndoorSpace.heap.remove(curDensity);
			// System.out.println(curDensity);

			if (curDensity.getFlag() == Flag.IBR) {
				Par query = curDensity.getQuery();
				Count resCount = IndoorSpace.hr.get(query.getmID());
				startTime = System.nanoTime();
				Count hpCount = COUNT(query, resCount, curTime);
				endTime = System.nanoTime();
				// System.out.println("topk COUNT Running Time:\t" +
				// (endTime-startTime) + "ns");

				if (hpCount.getSampledPointSet().size() == 0) {
					IndoorSpace.heap.add(new Density(query, Flag.IRP, hpCount
							.getmCount()));
					// System.out.println("IBR -> IRP\t" + resCount.getmCount()
					// + ">" + hpCount.getmCount());
				} else {
					IndoorSpace.hr.put(query.getmID(), hpCount);
					IndoorSpace.heap.add(new Density(query, Flag.IRHP, hpCount
							.getmCount()));
					// System.out.println("IBR -> IRHP\t" + resCount.getmCount()
					// + ">" + hpCount.getmCount());
				}

			} else if (curDensity.getFlag() == Flag.IRHP) {
				Par query = curDensity.getQuery();
				Count resCount = IndoorSpace.hr.get(query.getmID());
				double count_u = 0;
				for(SampledPoint sp : resCount.getSampledPointSet()){
					count_u = count_u + sp.getContributes();
				}
				double tempCount = resCount.getmCount() - (count_u);
				startTime = System.nanoTime();
				double count = tempCount + COUNTp(query, resCount, curTime);
				endTime = System.nanoTime();
				// System.out.println("topk COUNTp Running Time:\t" +
				// (endTime-startTime)/(10e9) + "s");
				// System.out.println("IRHP -> IRP\t" + resCount.getmCount() +
				// ">" + count);
				IndoorSpace.heap.add(new Density(query, Flag.IRP, count));
			} else {
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
	 * @return topK density Region
	 */
	public static List<Density> TopkIDRsGT(List<Par> queries, int t_min, int pK) {

		List<Density> topKresult = new ArrayList<Density>();
		Hashtable<Integer, Double> densityHT = new Hashtable<Integer, Double>();

		int curTime = t_min + (OTTGenConstant.minSamplingPeriod - 1);

		for (Entry<Integer, IdrObj> entry : IndoorSpace.observedObjs.entrySet()) {
			// System.out.println("-------------------------");
			IdrObj idrobj = entry.getValue();
			SampledPoint sampledPoint = new SampledPoint(idrobj.getmID(),idrobj.getmTruePos().getX(),idrobj.getmTruePos().getY(), idrobj.getmTruePos().getmFloor(),idrobj.getCurPar());
			IndoorUR indoorUR = new IndoorUR(sampledPoint, curTime, 1);
			for (Par query : queries) {
				if (query.getmFloor() == sampledPoint.getmFloor()) {
					double part = indoorUR.getUnionPart(query);
					
					if (densityHT.containsKey(query.getmID())) {
						double tempCount = densityHT.get(query.getmID());
						densityHT.put(query.getmID(), (tempCount + part));
					} else {
						densityHT.put(query.getmID(), (part));
					}
				}
			}
		}

		for (Entry<Integer, Double> entry : densityHT.entrySet()) {
			Par query = IndoorSpace.gPartitions.get(entry.getKey());
			topKresult.add(new Density(query, Flag.IRP, entry.getValue()));
		}

		Collections.sort(topKresult);
		return topKresult.subList(0, pK);
	}

	/**
	 * calculate the Ground Truth with Indoor Buffer Region
	 * 
	 * @param queries
	 * @param t_min
	 * @param pK
	 * @return topK density Region
	 */
	public static List<Density> TopkIDRsGTwithIBR(List<Par> queries, int t_min,
			int pK) {

		List<Density> topKresult = new ArrayList<Density>();

		double maxMovingDistance = (OTTGenConstant.minSamplingPeriod - 1)
				* DataGenConstant.maxVelocity;
		int curTime = t_min + (OTTGenConstant.minSamplingPeriod - 1);

		for (Par query : queries) {
			double count = 0;
			IndoorBufferRegion indoorBufferRegion = new IndoorBufferRegion(
					query, maxMovingDistance);

			for (Par fullyCoveredPar : indoorBufferRegion.getFullyCoveredPar()) {
				Par curPar = IndoorSpace.gPartitions.get(fullyCoveredPar
						.getmID());
				for (SampledPoint item : curPar.getmSampledPoints()) {
					IndoorUR indoorUR = new IndoorUR(item, curTime, 1);
					double part = indoorUR.getUnionPart(query);
					count = count + (part * item.getContributes());
				}
			}

			for (NextPossiblePar overlappedPar : indoorBufferRegion
					.getOverlappedPar()) {
				Par curPar = overlappedPar.getPossibleNextPar();
				for (SampledPoint item : curPar.getmSampledPoints()) {
					if (overlappedPar.Contain(item)) {
						IndoorUR indoorUR = new IndoorUR(item, curTime, 1);
						double part = indoorUR.getUnionPart(query);
						count = count
								+ (part * item.getContributes());
					}
				}
			}

			topKresult.add(new Density(query, Flag.IRP, count));

		}
		Collections.sort(topKresult);
		return topKresult.subList(0, pK);
	}

}
