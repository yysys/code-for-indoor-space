
package dk.aau.cs.idq.algorithm.simplequery;

import java.util.ArrayList;
import java.util.List;

import dk.aau.cs.idq.indoorentities.Door;
import dk.aau.cs.idq.indoorentities.IndoorSpace;
import dk.aau.cs.idq.indoorentities.LeavePair;
import dk.aau.cs.idq.indoorentities.NextPossiblePar;
import dk.aau.cs.idq.indoorentities.Par;

/**
 * Indoor Buffer Region 
 * 
 * @author lihuan
 * @version 0.1 / 2014.10.15
 * 
 */
public class IndoorBufferRegion {
	
	private Par Query;																			// the query
	
	private double maxMovingDistance;															// the maximum walking distance
	
	private List<Par> fullyCoveredPar = new ArrayList<Par>();									// the fullyCovered partitions
	
	private List<NextPossiblePar> overlappedPar = new ArrayList<NextPossiblePar>();				// the overlapped partitions
	

	/**
	 * Constructor Function
	 * 
	 * @param query
	 * @param maxMovingDistance
	 */
	public IndoorBufferRegion(Par query, double maxMovingDistance) {
		super();
		this.Query = query;
		this.setMaxMovingDistance(maxMovingDistance);
		getURPar(query, maxMovingDistance);
	}

	/**
	 * get UR partitions
	 * 
	 * @param maxMovingDistance 
	 * @param queries
	 */
	private void getURPar(Par query, double maxMovingDistance) {
		// TODO Auto-generated method stub
		
		this.fullyCoveredPar.add(query);
		List<LeavePair> leaveablePars = query.getLeaveablePars();
		//System.out.println(leaveablePars.size());
		for(LeavePair leaveablePar : leaveablePars){
			double canbefullycovered = leaveablePar.canBeFullyCovered(maxMovingDistance);
			Par curPar = IndoorSpace.gPartitions.get(leaveablePar.getmParID());
			Door curDoor = IndoorSpace.gDoors.get(leaveablePar.getmDoorID());
			if(canbefullycovered > 0){
				this.fullyCoveredPar.add(curPar);
				getExtendedURPar(query, curPar, curDoor, maxMovingDistance);
			}else if(canbefullycovered == 0){
				this.fullyCoveredPar.add(curPar);
			}else{
				this.overlappedPar.add(new NextPossiblePar(curDoor, curPar, maxMovingDistance));
			}
		}
		
	
	}

	/**
	 * get extended UR partitions
	 * 
	 * @param pos 
	 * @param curPar
	 * @param curPar 
	 * @param remainDist
	 */
	private void getExtendedURPar(Par prePar, Par par, Door pos, double maxMovingDistance) {
		// TODO Auto-generated method stub
		List<LeavePair> leaveablePars = par.getLeaveablePars();
		for(LeavePair leaveablePar : leaveablePars){
			Par curPar = IndoorSpace.gPartitions.get(leaveablePar.getmParID());
			Door curDoor = IndoorSpace.gDoors.get(leaveablePar.getmDoorID());
			double remainDist = maxMovingDistance - curDoor.eDist(pos);
			if(curPar.getmID() != prePar.getmID()){
				double canGo = leaveablePar.canBeFullyCovered(remainDist);
				if(canGo > 0){
					this.fullyCoveredPar.add(curPar);
					getExtendedURPar(par, curPar, curDoor, remainDist);
				}else if(canGo == 0){
					this.fullyCoveredPar.add(curPar);
				}else{
					this.overlappedPar.add(new NextPossiblePar(curDoor, curPar, maxMovingDistance));
				}
			}
		}
	}

	/**
	 * @return the queries
	 */
	public Par getQueries() {
		return Query;
	}

	/**
	 * @param query
	 */
	public void setQueries(Par query) {
		Query = query;
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
	 * @return the overlappedPar
	 */
	public List<NextPossiblePar> getOverlappedPar() {
		return overlappedPar;
	}

	/**
	 * @param overlappedPar the overlappedPar to set
	 */
	public void setOverlappedPar(List<NextPossiblePar> overlappedPar) {
		this.overlappedPar = overlappedPar;
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
	
	
	
}
