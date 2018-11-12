package dk.aau.cs.idq.datagen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import dk.aau.cs.idq.indoorentities.IndoorSpace;
import dk.aau.cs.idq.indoorentities.Par;
import dk.aau.cs.idq.indoorentities.Query;
import dk.aau.cs.idq.indoorentities.Rect;
import dk.aau.cs.idq.indoorentities.SubQuery;
import dk.aau.cs.idq.utilities.DataGenConstant;

/**
 * Generate the Complex Queries
 * 
 * @author lihuan
 * @version 0.1 / 2014.10.20
 * 
 */
public class ComplexQueriesGen {

	private List<Query> Queries = new ArrayList<Query>();				// the generated Queries

	public static int Margin = 4;										// the margin of random-decided range of Queries

	public String querisDir = System.getProperty("user.dir")
			+ "/complex_queries";
	
	public static double percentileP = 0.5;
	
	public static double percentilePP = 0.4;
	
	public int countTotal = 0;
	
	
	public ComplexQueriesGen(int numberOfQueries, int flag) {
		
		int numberPP = (int) (numberOfQueries * percentilePP);
		int numberP = (int) (numberOfQueries * percentileP);
		int numberSP = numberOfQueries - numberPP - numberP;
		
		querisDir = querisDir + "_" + numberOfQueries + "_" + numberPP + "_" + numberP + "_" + numberSP +".txt";
		File dir = new File(querisDir);

		if (dir.exists() && (flag == 0)) {
			
			try {
				FileReader frQueries = new FileReader(querisDir);
				BufferedReader brQueries = new BufferedReader(frQueries);
				String readoneline;
				while ((readoneline = brQueries.readLine()) != null) {
					Query query = Query.parse(readoneline);
					this.Queries.add(query);
				}
				brQueries.close();
				frQueries.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} else {
			
			Random random = new Random();
			List<Integer> already = new ArrayList<Integer>();
			FileWriter fwQueries;

			try {
				fwQueries = new FileWriter(querisDir);
				
				genPPQueries(random, already, numberPP);
				genPQueries(random, already, numberP);
				genSPQueries(random, already, numberSP);
				
				for(Query query : this.Queries){
					fwQueries.write(query.toString() + "\n");
				}
				
				fwQueries.flush();
				fwQueries.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void genSPQueries(Random random, List<Integer> already,int numberSP) {
		// TODO Auto-generated method stub
		
		int countSP = 0;
		
		while(countSP < numberSP){
			int xc = random
					.nextInt((int) DataGenConstant.floorRangeX + 1);
			int yc = random
					.nextInt((int) DataGenConstant.floorRangeY + 1);

			int xMargin = random.nextInt(Margin) + 1;
			int yMargin = random.nextInt(Margin) + 1;

			double xMin, xMax, yMin, yMax;

			xMin = ((xc - xMargin) > 0) ? (xc - xMargin) : 0;
			xMax = ((xc + xMargin) < DataGenConstant.floorRangeX) ? (xc + xMargin)
					: DataGenConstant.floorRangeX;
			yMin = ((yc - yMargin) > 0) ? (yc - yMargin) : 0;
			yMax = ((yc + yMargin) < DataGenConstant.floorRangeY) ? (yc + yMargin)
					: DataGenConstant.floorRangeY;

			List<SubQuery> candidate = considerablePart(xMin, xMax,
					yMin, yMax);
			if (candidate != null) {
				Query query = new Query(xMin, xMax, yMin, yMax, countTotal,
						candidate);
				// System.out.println(query);
				query.updatemFloor(random
						.nextInt(DataGenConstant.nFloor)); // assign a floor
				this.Queries.add(query);
				countSP++;
				countTotal++;
			}
		}
	}

	private void genPQueries(Random random, List<Integer> already, int numberP) {
		// TODO Auto-generated method stub
		
		int parSize = IndoorSpace.gPartitions.size();
		int countP = 0;
		
		while(countP < numberP){
			if(already.size() >= parSize){
				break;
			}
			int pickone = random.nextInt(parSize);
			if (!already.contains(pickone)) {
				Par queryPar = IndoorSpace.gPartitions.get(pickone);
				SubQuery subquery = new SubQuery(queryPar.getX1(), queryPar.getX2(), queryPar.getY1(), queryPar.getY2(), queryPar);
				List<SubQuery> subqueries = new ArrayList<SubQuery>();
				subqueries.add(subquery);
				Query query = new Query(queryPar.getX1(), queryPar.getX2(), queryPar.getY1(), queryPar.getY2(), countTotal, subqueries);
				query.updatemFloor(queryPar.getmFloor()); // assign a floor
				this.Queries.add(query);
				countP++;
				already.add(pickone);
				countTotal++;
			}
		}
	}

	private void genPPQueries(Random random, List<Integer> already, int numberPP) {
		// TODO Auto-generated method stub
		
		
		int parSize = IndoorSpace.gPartitions.size();
		int countPP = 0;
		
		while(countPP < numberPP){
			if(already.size() >= parSize){
				break;
			}
			
			int pickone = random.nextInt(parSize);
			
			if (!already.contains(pickone)) {
				Par queryPar = IndoorSpace.gPartitions.get(pickone);
				
				double width = queryPar.getWidth();
				double height = queryPar.getHeight();
				
				if(width>1 && height>1){
				
					double qWidth = (random.nextInt((int)width-1)+1);
					double qHeight = (random.nextInt((int)height-1)+1);
					
					double x0 = queryPar.getX1() + random.nextInt((int)(width-qWidth+1));
					double y0 = queryPar.getY1() + random.nextInt((int)(height-qHeight+1));
					
					
					SubQuery subquery = new SubQuery((x0), (x0+qWidth), (y0), (y0+qHeight), queryPar);
					List<SubQuery> subqueries = new ArrayList<SubQuery>();
					subqueries.add(subquery);
					Query query = new Query((x0), (x0+qWidth), (y0), (y0+qHeight), countTotal, subqueries);
					query.updatemFloor(queryPar.getmFloor()); // assign a floor
					this.Queries.add(query);
					countPP++;
					already.add(pickone);
					countTotal++;
					
				}
			}
		}
		

	}

	/**
	 * if the random-decided range can produce a qualified query, then return the considerable part
	 * 
	 * @param xMin
	 * @param xMax
	 * @param yMin
	 * @param yMax
	 * @return the considerable part
	 */
	private List<SubQuery> considerablePart(double xMin, double xMax,
			double yMin, double yMax) {
		// TODO Auto-generated method stub

		Rect orgQuery = new Rect(xMin, xMax, yMin, yMax);

		// if the rectangle has a part outside the building, it will not be considered
		if ((xMax <= DataGenConstant.outRectLeft.getX2())
				&& (orgQuery.intersection(DataGenConstant.outRectLeft) != null)) {
			return null;
		}
		if ((xMin >= DataGenConstant.outRectRight.getX1())
				&& (orgQuery.intersection(DataGenConstant.outRectRight) != null)) {
			return null;
		}
		if ((yMax <= DataGenConstant.outRectTop.getY2())
				&& (orgQuery.intersection(DataGenConstant.outRectTop) != null)) {
			return null;
		}
		if ((yMin >= DataGenConstant.outRectBottom.getY1())
				&& (orgQuery.intersection(DataGenConstant.outRectBottom) != null)) {
			return null;
		}

		
		Iterable<Par> rectArray = IndoorSpace.gRTree.find(xMin, yMin, xMax,
				yMax);
		List<Par> pars = new ArrayList<Par>();
		for (Par par : rectArray) {
			// System.out.println(par);
			pars.add(par);
		}

		List<SubQuery> result = new ArrayList<SubQuery>();
		
		//outside the building, it will not be considered
		if (pars.size() <= 1){
			return null;
		} else {
			for (Par par : pars) {
				Rect rect = orgQuery.intersection(par);
				SubQuery subquery = new SubQuery(rect, par);
				result.add(subquery);
			}
			// all the subQueries should be connected
			if (isConnected(result)) {
				return result;
			} else
				return null;
		}

	}

	/**
	 * test if all the subQueries are connected
	 * 
	 * @param result
	 * @return a boolean value
	 */
	private boolean isConnected(List<SubQuery> result) {
		// TODO Auto-generated method stub
		int[][] instantConnected = new int[result.size()][result.size()];
		for (int i = 0; i < result.size(); i++) {
			SubQuery testSubQuery = result.get(i);
			int connectedCount = 0;
			for (int j = 0; j < result.size(); j++) {
				if ((i != j) && testSubQuery.isConnectedWith(result.get(j))) {
					connectedCount++;
					instantConnected[i][j] = 1;
					instantConnected[j][i] = 1;
				}
			}
			
			// exist one is not connected to the others
			if (connectedCount == 0) {
				return false;
			}
		}
		
		List<Integer> cl = new ArrayList<Integer>();
		cl.add(0);
		for (int i = 0; i < result.size(); i++) {
			for (int j = 0; j < result.size(); j++) {
				if (instantConnected[i][j] == 1 && cl.contains(i) && (!cl.contains(j))) {
					cl.add(j);
				}
			}
		}

		if (cl.size() == result.size()) {
			return true;
		} else
			return false;
	}

	/**
	 * @return the queries
	 */
	public List<Query> getQueries() {
		return Queries;
	}

	/**
	 * @param queries
	 *            the queries to set
	 */
	public void setQueries(List<Query> queries) {
		Queries = queries;
	}

}
