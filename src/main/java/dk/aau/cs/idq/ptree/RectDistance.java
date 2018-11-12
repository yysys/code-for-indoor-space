
package dk.aau.cs.idq.ptree;

import org.khelekore.prtree.DistanceCalculator;
import org.khelekore.prtree.PointND;

import dk.aau.cs.idq.indoorentities.Rect;


/**
 * to calculate the distance for a given Rect.class
 * 
 * 
 * @author lihuan
 * @version 0.1 / 2014.10.06
 * @see org.khelekore.prtree.DistanceCalculator
 *
 */
public class RectDistance implements DistanceCalculator<Rect> {

	/**
	 * Distance Calculator
	 * 
	 * @see org.khelekore.prtree.DistanceCalculator#distanceTo(java.lang.Object, org.khelekore.prtree.PointND)
	 */
	@Override
	public double distanceTo(Rect t, PointND p) {
		// TODO Auto-generated method stub
		return t.getMinDist(p);
	}

}
