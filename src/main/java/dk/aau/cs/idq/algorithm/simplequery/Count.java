/**
 * 
 */
package dk.aau.cs.idq.algorithm.simplequery;

import java.util.ArrayList;
import java.util.List;

import dk.aau.cs.idq.indoorentities.SampledPoint;

/**
 * @author lihuan
 *
 */
public class Count {
	
	private double mCount;
	
	private List<SampledPoint> sampledPointSet = new ArrayList<SampledPoint>();
	
	/**
	 * @param mCount
	 * @param sampledPointSet
	 */
	public Count(double mCount, List<SampledPoint> sampledPointSet) {
		super();
		this.mCount = mCount;
		this.setSampledPointSet(sampledPointSet);
	}

	/**
	 * @return the mCount
	 */
	public double getmCount() {
		return mCount;
	}

	/**
	 * @param mCount the mCount to set
	 */
	public void setmCount(double mCount) {
		this.mCount = mCount;
	}

	/**
	 * @return the sampledPointSet
	 */
	public List<SampledPoint> getSampledPointSet() {
		return sampledPointSet;
	}

	/**
	 * @param sampledPointSet the sampledPointSet to set
	 */
	public void setSampledPointSet(List<SampledPoint> sampledPointSet) {
		this.sampledPointSet = sampledPointSet;
	}
	
	

}
