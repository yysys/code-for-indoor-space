
package dk.aau.cs.idq.ptree;

import java.util.Iterator;

import org.khelekore.prtree.PRTree;

import dk.aau.cs.idq.indoorentities.IdrObj;
import dk.aau.cs.idq.indoorentities.IndoorSpace;
import dk.aau.cs.idq.indoorentities.Par;

public class RTreeTest {

	public static void main(String []args) {
		

		// TODO Auto-generated method stub
		PRTree<Par> tree = new PRTree<Par>(
				new RectConverter(), 30);
		//Rect rx = new Rect(0.9, 0.9, 1, 1);
		//tree.load(Collections.singletonList(rx));
		
		
		tree.load(IndoorSpace.gPartitions);
		System.out.println(tree.getNumberOfLeaves());
		
		Iterator<IdrObj> objItr = IndoorSpace.gIdrObjs.iterator();
		while(objItr.hasNext()){
			IdrObj curObj = objItr.next();
			System.out.println("------------------------------------------");
			System.out.println("This is the Object #" + curObj.getmID() + " " + curObj.toString());
			Iterable<Par> RectArray = tree.find(curObj.getmTruePos().getX(),curObj.getmTruePos().getY(),curObj.getmTruePos().getX(),curObj.getmTruePos().getY());
			for (Par r : RectArray) {
				System.out.println("found a rectangle #" + r.getmID());
				System.out.println("leavable pairs:" + r.toStringLeaveablePars().toString());

			}
		}

		/*
		DistanceCalculator<Rect> dc = new RectDistance ();
		PointND p = new Point(10, 10);
		int maxHits = 5;
		List<DistanceResult<Rect>> nnRes =
		    tree.nearestNeighbour (dc, new AcceptAllFilter(), maxHits, p);
		Object[] resArray =  nnRes.toArray();
		for(Object item: resArray){
			DistanceResult<Rect> itemres = (DistanceResult<Rect>)item;
			System.out.println(itemres.get().toString() + itemres.getDistance());
		}
		//System.out.println ("Nearest neighbours are: " + nnRes.toArray().toString());
		*/
	}

}
