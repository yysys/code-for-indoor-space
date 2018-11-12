package dk.aau.cs.idq.indoorentities;

import java.util.ArrayList;
import java.util.List;


/**
 * SubQuery
 * each Query can be divided into several SubQueries (each being the intersection with a indoor Partition)
 * 
 * 
 * @author lihuan
 * @version 0.1 / 2014.10.06
 * @see dk.aau.cs.idq.indoorentities.Rect
 *
 */
public class SubQuery extends Rect {

	private Par coveringPar;						// the Partition which covers the SubQuery

	private int covered = 0;						// is fully covered by the coveringPar

	/**
	 * Constructor Functions
	 * 
	 * @param x1
	 * @param x2
	 * @param y1
	 * @param y2
	 * @param coveringPar
	 */
	public SubQuery(double x1, double x2, double y1, double y2, Par coveringPar) {
		super(x1, x2, y1, y2);
		// TODO Auto-generated constructor stub
		this.coveringPar = coveringPar;
		if (coveringPar.getmArea() == this.getmArea()) {
			covered = 1;
		}
	}

	/**
	 * Constructor Function
	 * 
	 * @param rect
	 * @param coveringPar
	 */
	public SubQuery(Rect rect, Par coveringPar) {
		super(rect);
		// TODO Auto-generated constructor stub
		this.coveringPar = coveringPar;
		if (coveringPar.getmArea() == this.getmArea()) {
			covered = 1;
		}
	}

	/**
	 * @return the coveringPar
	 */
	public Par getCoveringPar() {
		return coveringPar;
	}

	/**
	 * @param coveringPar
	 *            the coveringPar to set
	 */
	public void setCoveringPar(Par coveringPar) {
		this.coveringPar = coveringPar;
	}

	/**
	 * @return the covered
	 */
	public int getCovered() {
		return covered;
	}

	/**
	 * @param covered
	 *            the covered to set
	 */
	public void setCovered(int covered) {
		this.covered = covered;
	}

	/**
	 * Test if this SubQuery is connected with another SubQuery
	 * 
	 * @param subQuery
	 * @return boolean value
	 */
	public boolean isConnectedWith(SubQuery subQuery) {
		// TODO Auto-generated method stub

		// if they are not adjacent to each other, then return false
		if (!this.isAdjacent(subQuery)) {
			return false;
		}

		List<Integer> findDoorIDS = subQuery.coveringPar.getmDoors();

		for (int doorID : this.coveringPar.getmDoors()) {
			if (findDoorIDS.contains(doorID)) {
				Door door = IndoorSpace.gDoors.get(doorID);
				//if the door is covered by both SubQueries, then return true
				if (this.testDoor(door) && subQuery.testDoor(door)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * toString
	 * 
	 * @return coveringParID,x1,x2,y1,y2,mFloor
	 */
	public String toString() {
		return this.coveringPar.getmID() + "," + this.getX1() + ","
				+ this.getX2() + "," + this.getY1() + "," + this.getY2() + ","
				+ this.getmFloor();
	}

	/**
	 * parse a String into a class SubQuery
	 * 
	 * @param string
	 * @return List<SubQuery>
	 */
	public static List<SubQuery> parse(String string) {
		// TODO Auto-generated method stub
		List<SubQuery> subqueries = new ArrayList<SubQuery>();
		String[] items = string.split("#");
		for (String item : items) {
			if (!item.equals("")) {
				String[] subqueryinfo = item.split(",");
				SubQuery subquery = new SubQuery(
						Double.valueOf(subqueryinfo[1]),
						Double.valueOf(subqueryinfo[2]),
						Double.valueOf(subqueryinfo[3]),
						Double.valueOf(subqueryinfo[4]),
						IndoorSpace.gPartitions.get(Integer
								.valueOf(subqueryinfo[0])));
				subquery.setmFloor(Integer.valueOf(subqueryinfo[5]));
				subqueries.add(subquery);
			}
		}
		return subqueries;
	}

}
