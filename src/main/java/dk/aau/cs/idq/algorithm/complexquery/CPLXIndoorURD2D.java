package dk.aau.cs.idq.algorithm.complexquery;

import java.util.ArrayList;
import java.util.List;

import dk.aau.cs.idq.indoorentities.IndoorSpace;
import dk.aau.cs.idq.indoorentities.NextPossiblePar;
import dk.aau.cs.idq.indoorentities.Par;
import dk.aau.cs.idq.indoorentities.Point;
import dk.aau.cs.idq.indoorentities.Query;
import dk.aau.cs.idq.indoorentities.Rect;
import dk.aau.cs.idq.indoorentities.SampledPoint;
import dk.aau.cs.idq.utilities.DataGenConstant;

/**
 * CPLXIndoorUR
 * the uncertain region for a Sample Point during a walking period
 * 
 * @author lihuan
 * @version 0.1 / 2014.10.21
 */
public class CPLXIndoorURD2D {

	private SampledPoint mPoint;													// the position of the Sample

	private int recordTime;															// the record time of this Sample

	private int curTime;															// the current time

	private List<Point> monteCarloPoints = new ArrayList<Point>();					// the MonteCarlo Points
	
	private int flag;																// 0 means no needs to calculate MonteCarlo Points
	
	private Par curINPar;															// current par of this Sample Point
	
	private List<NextPossiblePar> uncertainPars;									// uncertain next possible pars


	/**
	 * Constructor Function
	 * 
	 * @param mPoint
	 * @param curTime
	 * @param flag
	 */
	public CPLXIndoorURD2D(SampledPoint mPoint, int curTime, int flag) {
		this.setmPoint(mPoint);
		
		this.setRecordTime(IndoorSpace.OTT.get(mPoint.getObjID()));
		this.setCurTime(curTime);
		this.flag = flag;
		if (flag == 1) {
			monteCarloPoints();
		}
	}

	
	/**
	 * generate the MonteCarlo Points
	 */
	private void monteCarloPoints() {
		// TODO Auto-generated method stub
		//System.out.println("------------");
		if (this.curTime > this.recordTime) {
			
			List<String> contains = new ArrayList<String>();
			
			// System.out.println(this.mPoint);
			// System.out.println("size:" + uncertainPars.size());
			if(this.uncertainPars == null){
				this.uncertainPars = this.mPoint.getUncertainParsD2D((this.curTime - this.recordTime));
			}
			
			for (NextPossiblePar uncertainPar : uncertainPars) {
				
				//System.out.println(uncertainPar);
				
				Par unPar = uncertainPar.getPossibleNextPar();
				//Door unDoor = uncertainPar.getPossibleTroughDoor();
				//double onGoingDist = uncertainPar.getDistanceToContinue();
				
				Boolean Flagcovered = Boolean.FALSE;
				if(uncertainPar.isFullyCovered()){
					//System.out.println(uncertainPar.getPossibleNextPar().getmID());
					Flagcovered = Boolean.TRUE;
				}
				
				for(Point point : unPar.getMCPoints()){
					if(!contains.contains(point)){
						if(Flagcovered || uncertainPar.Contain(point)){
							this.monteCarloPoints.add(point);
						}
					}
				}
				
			}
			//System.out.println("---------");
			// System.out.println("1step:" + this.monteCarloPoints.size());
			if(this.curINPar == null){
				this.curINPar = this.mPoint.getCurPar();
			}
			
			double onGoingDist = (this.curTime - this.recordTime)
					* DataGenConstant.maxVelocity;
			// System.out.println("onGoingDist" + onGoingDist);

			for(Point point : this.curINPar.getMCPoints()){
				if(!contains.contains(point)){
					if(this.mPoint.eDist(point) <= onGoingDist){
						this.monteCarloPoints.add(point);
					}
				}
			}

			// System.out.println("2step:" + this.monteCarloPoints.size());

		} else {
			this.monteCarloPoints.add(new Point(this.mPoint.getSampledX(),
					this.mPoint.getSampledY(), this.mPoint.getmFloor()));
		}
	}

	
	/**
	 * get the intersection area(probability)
	 * 
	 * @param query
	 * @return probability
	 */
	public double getUnionPart(Query query) {

		// System.out.println(this.curTime + " > >" + this.recordTime);
		// System.out.println(this.curTime + " > >" + this.recordTime + "||" + this.monteCarloPoints.size());

		int sum = 0;

		for (Point point : this.monteCarloPoints) {
			// System.out.println(query);
			// System.out.println(point);
			if (query.contain(point)) {
				//System.out.println(point.getCurrentPar());
				sum++;
			}
		}

		return (double) sum / this.monteCarloPoints.size();
	}

	
	/**
	 * @return the mPoint
	 */
	public SampledPoint getmPoint() {
		return mPoint;
	}

	/**
	 * @param mPoint
	 *            the mPoint to set
	 */
	public void setmPoint(SampledPoint mPoint) {
		this.mPoint = mPoint;
	}

	/**
	 * @return the recordTime
	 */
	public int getRecordTime() {
		return recordTime;
	}

	/**
	 * @param recordTime
	 *            the recordTime to set
	 */
	public void setRecordTime(int recordTime) {
		this.recordTime = recordTime;
	}

	/**
	 * @return the curTime
	 */
	public int getCurTime() {
		return curTime;
	}

	/**
	 * @param curTime
	 *            the curTime to set
	 */
	public void setCurTime(int curTime) {
		this.curTime = curTime;
	}

	/**
	 * @return the monteCarloPoints
	 */
	public List<Point> getMonteCarloPoints() {
		return monteCarloPoints;
	}

	/**
	 * @param monteCarloPoints
	 *            the monteCarloPoints to set
	 */
	public void setMonteCarloPoints(List<Point> monteCarloPoints) {
		this.monteCarloPoints = monteCarloPoints;
	}

	/**
	 * @return the flag
	 */
	public int getFlag() {
		return flag;
	}

	/**
	 * @param flag the flag to set
	 */
	public void setFlag(int flag) {
		this.flag = flag;
		if(this.flag == 1){
			monteCarloPoints();
		}
	}

	/**
	 * the topological relation with the Query's Uncertainty Region
	 * 
	 * @param query
	 * @return result 0.not in UR; 2. not fully in UR; 3. fully in UR
	 */
	public int topoloyRelation(Query query) {
		// TODO Auto-generated method stub
		
		if (this.curTime > this.recordTime) {
			// System.out.println(this.mPoint);
			
			int nonInterset = 0;
			int fullcontained = 0;
			
			double maxGoing = (this.curTime - this.recordTime) * DataGenConstant.maxVelocity;
			this.curINPar = this.mPoint.getCurPar();
			
			Rect rect = new Rect(this.mPoint.getSampledX() - maxGoing, this.mPoint.getSampledX() + maxGoing, this.mPoint.getSampledY() - maxGoing, this.mPoint.getSampledY() + maxGoing);
			rect.setmFloor(this.mPoint.getmFloor());
			Rect intersect = this.curINPar.intersection(rect);
			
			if(!query.contain(intersect)){
				if(query.intersection(intersect) != null){
					return 1;
				}else{
					nonInterset++;
				}
			}else{
				fullcontained++;
			}
			
			this.uncertainPars = this.mPoint.getUncertainParsD2D((this.curTime - this.recordTime));
			
			for(NextPossiblePar uncertainPar : uncertainPars){
					if(uncertainPar.isFullyCovered()){
						Par unPar = uncertainPar.getPossibleNextPar();
						if(!query.contain(unPar)){
							if(query.intersection(unPar) != null){
								return 1;
							}else{
								nonInterset++;
							}
						}else{
							fullcontained++;
						}
					}else{
						Rect mbr = uncertainPar.getMBR();
						if(!query.contain(mbr)){
							if(query.intersection(mbr) != null){
								return 1;
							}else{
								nonInterset++;
							}
						}else{
							fullcontained++;
						}
					}
			}
			
			if(nonInterset == (uncertainPars.size() + 1)){
				return 0;
			}
			if(fullcontained == (uncertainPars.size() + 1)){
				return 2;
			}else{
				return 1;
			}
			
		} else {
			if(query.contain(new Point(this.mPoint.getSampledX(), this.mPoint.getSampledY(), this.mPoint.getmFloor()))){
				return 2;
			}else
				return 0;
		}

	}

}
