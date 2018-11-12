package dk.aau.cs.idq.algorithm.complexquery;

import java.util.Map.Entry;

import dk.aau.cs.idq.indoorentities.IndoorSpace;
import dk.aau.cs.idq.indoorentities.Query;

/**
 * Complex Density
 * 
 * @author lihuan
 * @version 0.1 / 2014.10.15
 * @see java.lang.Comparable
 */
public class CPLXDensity implements Comparable<Object> {

	private Query query;									// the query

	private int flag;										// the flag : IBR, IRHP, IRP

	private double count;									// the count

	private double density;									// the calculated density

	/**
	 * Constructor Function
	 * 
	 * @param query
	 * @param flag
	 * @param count
	 */
	public CPLXDensity(Query query, int flag, double count) {
		super();
		this.query = query;
		this.flag = flag;
		this.count = count;
		this.density = (double) (count) / (query.getmArea());
	}

	/**
	 * @return the query
	 */
	public Query getQuery() {
		return query;
	}

	/**
	 * @param query
	 *            the query to set
	 */
	public void setQuery(Query query) {
		this.query = query;
	}

	/**
	 * @return the flag
	 */
	public int getFlag() {
		return flag;
	}

	/**
	 * @param flag
	 *            the flag to set
	 */
	public void setFlag(int flag) {
		this.flag = flag;
	}

	/**
	 * @return the count
	 */
	public double getCount() {
		return count;
	}

	/**
	 * @param count
	 *            the count to set
	 */
	public void setCount(double count) {
		this.count = count;
	}

	/**
	 * @return the density
	 */
	public double getDensity() {
		return density;
	}

	/**
	 * @param density
	 *            the density to set
	 */
	public void setDensity(double density) {
		this.density = density;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		CPLXDensity other = (CPLXDensity) o;
		if (this.density < other.density) {
			return 1;
		} else if (this.density == other.density) {
			if (this.query.getQueryID() < other.query.getQueryID()) {
				return 1;
			} else if (this.query.getQueryID() == other.query.getQueryID()) {
				return 0;
			} else
				return -1;
		} else
			return -1;
	}

	/**
	 * toString
	 * 
	 * @return tempString flag+queryID+density+contributes
	 */
	public String toString() {
		String tempString = this.flag + "\t" + this.query.getQueryID() + "\t\t\t" + this.density + "\t\t\t\t";
		double count = 0;
		for(Entry<Integer, Double> entry : this.query.getContributes().entrySet()){
			tempString = tempString + "<" + entry.getKey() + "," + IndoorSpace.gSampledPoints.get(entry.getKey()-1).getCurPar().getmID() + "," + entry.getValue() + ">";
			count = count + entry.getValue();
		}
		return tempString; // + this.count + "\t" + count + "\t" + this.query.getSubQueries().size()
	}

}
