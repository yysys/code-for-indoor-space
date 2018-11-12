package dk.aau.cs.idq.indoorentities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import dk.aau.cs.idq.utilities.DataGenConstant;

/**
 * Query as Complex Query each Query consists of several SubQueries
 * 
 * 
 * @author lihuan
 * @version 0.1 / 2014.10.20
 * @see dk.aau.cs.idq.indoorentities.Rect
 *
 */
public class Query extends Rect {

	private int queryID; // the query ID

	private List<SubQuery> subQueries = new ArrayList<SubQuery>(); // sub-queries
																	// of this
																	// query

	private SortedMap<Integer, Double> contributes = new TreeMap<Integer, Double>(); // each
																						// sample
																						// and
																						// its
																						// contribute
																						// when
																						// calculating
																						// density

	/**
	 * Constructor Function
	 * 
	 * @param x1
	 * @param x2
	 * @param y1
	 * @param y2
	 */
	public Query(double x1, double x2, double y1, double y2) {
		super(x1, x2, y1, y2);
		// TODO Auto-generated constructor stub
		this.queryID = ++DataGenConstant.mID_Queries;
		this.contributes = new TreeMap<Integer, Double>();
	}

	/**
	 * Constructor Function
	 * 
	 * @param x1
	 * @param x2
	 * @param y1
	 * @param y2
	 * @param queryID
	 * @param subQueries
	 */
	public Query(double x1, double x2, double y1, double y2, int queryID,
			List<SubQuery> subQueries) {
		super(x1, x2, y1, y2);
		// TODO Auto-generated constructor stub
		this.queryID = queryID;
		this.subQueries = subQueries;
		this.contributes = new TreeMap<Integer, Double>();
	}

	/**
	 * @return the queryID
	 */
	public int getQueryID() {
		return queryID;
	}

	/**
	 * @param queryID
	 *            the queryID to set
	 */
	public void setQueryID(int queryID) {
		this.queryID = queryID;
	}

	/**
	 * @return the subQueries
	 */
	public List<SubQuery> getSubQueries() {
		return subQueries;
	}

	/**
	 * @return the coveredPars
	 */
	public List<Par> getcoveredPars() {
		List<Par> coveredPars = new ArrayList<Par>();
		for (SubQuery subquery : subQueries) {
			coveredPars.add(subquery.getCoveringPar());
		}
		return coveredPars;
	}

	/**
	 * @param subQueries
	 *            the subQueries to set
	 */
	public void setSubQueries(List<SubQuery> subQueries) {
		this.subQueries = subQueries;
	}

	/**
	 * update the mFloor
	 * 
	 * @param mFloor
	 */
	public void updatemFloor(int mFloor) {
		// System.out.println(mFloor);
		this.setmFloor(mFloor);
		for (SubQuery subquery : this.subQueries) {

			if (subquery.getCoveringPar().getmID() < IndoorSpace.gNumberParsPerFloor) {
				int coveredParID_subquery = subquery.getCoveringPar().getmID()
						+ mFloor * IndoorSpace.gNumberParsPerFloor;
				subquery.setCoveringPar(IndoorSpace.gPartitions
						.get(coveredParID_subquery));
			}
			subquery.setmFloor(mFloor);
		}
	}

	public Map<Door, Double> getExitDoors() {

		List<Integer> doors = new ArrayList<Integer>();
		Map<Door, Double> exitDoors = new HashMap<Door, Double>();

		if (subQueries.size() == 1) {
			if (subQueries.get(0).getCovered() == 1) {
				List<Integer> subqueryDoors = subQueries.get(0)
						.getCoveringPar().getmDoors();
				for (int subqueryDoor : subqueryDoors) {
					if (!doors.contains(subqueryDoor)) {
						doors.add(subqueryDoor);
						Door door = IndoorSpace.gDoors.get(subqueryDoor);
						exitDoors.put(door, 0.0);
					}
				}
				return exitDoors;

			} else {

				List<Integer> subqueryDoors = subQueries.get(0)
						.getCoveringPar().getmDoors();
				for (int subqueryDoor : subqueryDoors) {
					if (!doors.contains(subqueryDoor)) {
						doors.add(subqueryDoor);
						Door door = IndoorSpace.gDoors.get(subqueryDoor);
						exitDoors.put(door, subQueries.get(0).getMinDist(door));
					}
				}
				
				return exitDoors;
			}

		} else {
			
			for (SubQuery subquery : this.subQueries) {
				List<Integer> subqueryDoors = subquery.getCoveringPar()
						.getmDoors();
				for (int subqueryDoor : subqueryDoors) {
					if (!doors.contains(subqueryDoor)
							&& (!this.contain(IndoorSpace.gDoors
									.get(subqueryDoor)))) {
						doors.add(subqueryDoor);
						Door door = IndoorSpace.gDoors.get(subqueryDoor);
						exitDoors.put(door, subquery.getMinDist(door));
					}
				}
			}

			return exitDoors;
		}
	}

	/**
	 * toString
	 * 
	 * @return queryID,x1,x2,y1,y2,mFloor@SubQueries.toString
	 * @see String dk.aau.cs.idq.indoorentities.SubQuery.toString()
	 */
	public String toString() {

		String tempString = "";

		tempString = tempString + this.queryID + "," + this.getX1() + ","
				+ this.getX2() + "," + this.getY1() + "," + this.getY2() + ","
				+ this.getmFloor() + "@";

		for (SubQuery item : this.subQueries) {
			tempString = tempString + "#" + item;
		}

		return tempString;
	}

	/**
	 * add contribute (spID, contributes}
	 * 
	 * @param spID
	 * @param contributes
	 */
	public void addContributes(int spID, double contributes) {
		this.contributes.put(spID, contributes);
	}

	/**
	 * @return the contributes
	 */
	public SortedMap<Integer, Double> getContributes() {
		return contributes;
	}

	/**
	 * @param contributes
	 *            the contributes to set
	 */
	public void setContributes(SortedMap<Integer, Double> contributes) {
		this.contributes = contributes;
	}

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		Query other = (Query) o;
		return this.queryID > other.queryID ? 1
				: (this.queryID == other.queryID ? 0 : -1);

	}

	/**
	 * parse a String into a Query
	 * 
	 * @param readoneline
	 * @return query
	 */
	public static Query parse(String readoneline) {
		// TODO Auto-generated method stub
		String[] items = readoneline.split("@");
		String[] queryinfo = (items[0]).split(",");
		List<SubQuery> subqueries = SubQuery.parse(items[1]);
		Query query = new Query(Double.valueOf(queryinfo[1]),
				Double.valueOf(queryinfo[2]), Double.valueOf(queryinfo[3]),
				Double.valueOf(queryinfo[4]), Integer.valueOf(queryinfo[0]),
				subqueries);
		query.setmFloor(Integer.valueOf(queryinfo[5]));
		return query;
	}

}
