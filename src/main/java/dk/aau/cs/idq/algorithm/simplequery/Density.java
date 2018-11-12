
package dk.aau.cs.idq.algorithm.simplequery;

import dk.aau.cs.idq.indoorentities.Par;

/**
 * Density
 * 
 * @author lihuan
 * @version 0.1 / 2014.10.15
 * @see java.lang.Comparable
 */
public class Density implements Comparable<Object> {

	private Par query;									// the query

	private int flag;									// the flag : IBR, IRHP, IRP

	private double count;								// the count

	private double density;								// the calculated density

	/**
	 * Constructor Function
	 * 
	 * @param query
	 * @param flag
	 * @param count
	 */
	public Density(Par query, int flag, double count) {
		super();
		this.query = query;
		this.flag = flag;
		this.count = count;
		this.density = (double) (this.count) / (this.query.getmArea());
	}

	/**
	 * @return the query
	 */
	public Par getQuery() {
		return query;
	}

	/**
	 * @param query
	 *            the query to set
	 */
	public void setQuery(Par query) {
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
		Density other = (Density) o;
		if (this.density < other.density) {
			return 1;
		} else if (this.density == other.density) {
			if (this.query.getmID() < other.query.getmID()) {
				return 1;
			} else if (this.query.getmID() == other.query.getmID()) {
				return 0;
			} else
				return -1;
		} else
			return -1;
	}

	/**
	 * toString
	 * 
	 * @return flag+queryID+density
	 */
	public String toString() {
		return this.flag + "\t" + this.query.getmID() + "\t\t\t" + this.density;
	}

}
