
package dk.aau.cs.idq.utilities;

import java.math.BigDecimal;

import dk.aau.cs.idq.indoorentities.Rect;

/**
 * Constant Values in Data Generating
 * 
 * @author lihuan
 * @version 0.1 / 2014.10.06
 *
 */
public class DataGenConstant {

	/************************************************
	 * PARAMETERS FOR INDOOR SPACES
	 ************************************************/
	/** dimensions of the floor */
	public static double floorRangeX = 60;
	public static double floorRangeY = 60;

	/** numbers of the floor */
	public static int nFloor = 10;

	/** length of stairway between two floors */
	public static double lenStairway = 5;

	/** length of stairway of the whole building */
	public static double buildingHeight = lenStairway * (nFloor - 1);

	/** four outdoor rectangles */
	public static Rect outRectTop = new Rect(10, 50, 0, 5);
	public static Rect outRectBottom = new Rect(10, 50, 55, 60);
	public static Rect outRectLeft = new Rect(0, 5, 10, 50);
	public static Rect outRectRight = new Rect(55, 60, 10, 50);

	/************************************************
	 * ID COUNTERS FOR INDOOR ENTITIES
	 ************************************************/
	/** the ID counter of Partitions */
	public static int mID_Par = 0;

	/** the ID counter of Doors */
	public static int mID_Door = 0;

	/** the ID counter of Indoor Objects */
	public static int mID_IdrObj = 0;
	
	/** the ID counter of Queries */
	public static int mID_Queries = 0;
	
	/** the ID counter of SampledPoints */
	public static int mID_SampledPoints = 0;

	/************************************************
	 * PARAMETERS FOR R-TREE
	 ************************************************/
	/** branch factor of the R-Tree */
	public static int RTree_BranchFactor = 20;

	/************************************************
	 * PARAMETERS FOR GENERATING INDOOR OBJECTS
	 ************************************************/
	/** total life span (unit: second) */
	public static int totalLifecycle = 7200;

	/** numbers of generated objects per floor */
	public static int nObjPerFloor = 1000;

	/** numbers of generated objects in total */
	public static int nObjects = nFloor * nObjPerFloor;

	/** numbers of new added objects per second */
	public static int newEnterObjects = 1;

	/************************************************
	 * PARAMETERS FOR MOVING OF INDOOR OBJECTS
	 ************************************************/
	/** maximum moving speed of indoor objects */
	public static double maxVelocity = 1;

	/** time interval (second) */
	public static int atomicInterval = 1;

	/** the longest distance that object can move to in the next time interval */
	public static double maxAtomicWalkingDistance = maxVelocity
			* atomicInterval;

	/** probability of entering the stairway for a random-walking object */
	public static int probEnterStairway = 70;

	/************************************************
	 * PARAMETERS FOR UNCERTAIN SAMPLING OF INDOOR OBJECTS
	 ************************************************/
	/** margin for uncertain box */
	public static double UNCERTAINTY_SIZE = 0.25;
	
	/** radius for uncertain region */
	public static double UNCERTAINTY_RADIUS = 0.25;

	/** number of sampling points in uncertain box */
	public static int SAMPLE_SIZE = 1;

	/** contributes of each sampling point in uncertain box */
	public static double percentContributes = new BigDecimal(
			(1.0 / SAMPLE_SIZE)).setScale(2, BigDecimal.ROUND_HALF_UP)
			.doubleValue();

	/************************************************
	 * PARAMETERS FOR DRAWING THE FLOOR
	 ************************************************/
	/** the zoom level : enlarge the whole floor */
	public static int zoomLevel = 10;

}
