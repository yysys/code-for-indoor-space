
package dk.aau.cs.idq.indoorentities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import dk.aau.cs.idq.algorithm.complexquery.CPLXIndoorURD2DArcs;
import org.khelekore.prtree.PRTree;

import dk.aau.cs.idq.algorithm.complexquery.CPLXDensity;
import dk.aau.cs.idq.algorithm.simplequery.Count;
import dk.aau.cs.idq.algorithm.simplequery.Density;
import dk.aau.cs.idq.ptree.SampledPointConverter;
import dk.aau.cs.idq.ptree.RectConverter;
import dk.aau.cs.idq.utilities.DataGenConstant;

/**
 * IndoorSpace
 * models the indoor space for the whole life span
 * 
 * 
 * @author lihuan
 * @version 0.1 / 2014.10.06
 *
 */
public class IndoorSpace {
	
	/************************************************
	 * INDOOR ENTITIES
	 ************************************************/
	/** all the doors of the indoor space */
	public static List<Door> gDoors = new ArrayList<Door>();
	
	/** all the partitions of the indoor space */
	public static List<Par> gPartitions = new ArrayList<Par>();
	
	/** all the moving objects of the indoor space */
	public static List<IdrObj> gIdrObjs = new ArrayList<IdrObj>();
	
	/** all the sampled points */
	public static List<SampledPoint> gSampledPoints = new ArrayList<SampledPoint>();
	
	
	/************************************************
	 * DATA STRUCTURES
	 ************************************************/
	/** the in-memory R-Tree */
	public static PRTree<Par> gRTree = new PRTree<Par>(new RectConverter(), DataGenConstant.RTree_BranchFactor);
	
	/** the in-memory R-Tree for Objects*/
	public static List<PRTree<SampledPoint>> gSPRTree = new ArrayList<PRTree<SampledPoint>>();
	
	/** the in-memory D2D Matrix */
	public static HashMap<String, D2Ddistance> gD2D = new HashMap<String, D2Ddistance>();
	
	/** the in-memory D2D Matrix */
	public static double[][] gD2DMatrix = new double[220][220];
	
	/** the in-memory Objects Size Table */
	public static SortedMap<Integer, Integer> sizeObjsTable = new TreeMap<Integer, Integer>();
	
	/** current observed indoor objects */
	public static SortedMap<Integer, IdrObj> observedObjs = new TreeMap<Integer, IdrObj>();
	
	/** R -> 2{ObjectID} */
	public static Hashtable<Integer, Count> hr = new Hashtable<Integer, Count>();
	
	/** max-heap */
	public static SortedSet<Density> heap = new TreeSet<Density>();
	
	/** max-heap for CPLX queries */
	public static SortedSet<CPLXDensity> c_heap = new TreeSet<CPLXDensity>();
	
	/** OTT */
	public static Hashtable<Integer, Integer> OTT = new Hashtable<Integer, Integer>();
	
	/** Ground Truth */
	public static Hashtable<Integer, Integer> GroundTruth = new Hashtable<Integer, Integer>();
	
	/************************************************
	 * COUNTER
	 ************************************************/
	/** the Number of Doors per floor */
	public static int gNumberDoorsPerFloor;
	
	/** the Number of Partitions per floor */
	public static int gNumberParsPerFloor;

	public static Hashtable<Integer, CPLXIndoorURD2DArcs> cacheURs = new Hashtable<>();

	public static Hashtable<Integer, CPLXIndoorURD2DArcs> cacheURs1Pass = new Hashtable<>();

}
