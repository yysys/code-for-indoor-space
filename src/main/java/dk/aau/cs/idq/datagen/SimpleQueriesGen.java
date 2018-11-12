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


/**
 * Generate the Simple Queries
 * 
 * @author lihuan
 * @version 0.1 / 2014.10.20
 * 
 */
public class SimpleQueriesGen {

	private List<Par> Queries = new ArrayList<Par>();

	public String querisDir = System.getProperty("user.dir")
			+ "/sample_queries";
	
	public SimpleQueriesGen(int numberOfQueries) {
		querisDir = querisDir + "_" + numberOfQueries + ".txt";
		File dir = new File(querisDir);
		if (dir.exists()) {
			
			List<Integer> already = new ArrayList<Integer>();
			
			try {
				FileReader frQueries = new FileReader(querisDir);
				BufferedReader brQueries = new BufferedReader(frQueries);
				String readoneline;
				while ((readoneline = brQueries.readLine()) != null) {
					String []items = readoneline.split(",");
					for(String item : items){
						int seed = Integer.valueOf(item);
						already.add(seed);
					}
				}
				brQueries.close();
				frQueries.close();
				
				for(int seed : already){
					Par query = IndoorSpace.gPartitions.get(seed);
					this.Queries.add(query);
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {

			Random random = new Random();

			int parSize = IndoorSpace.gPartitions.size();
			List<Integer> already = new ArrayList<Integer>();

			int i = 0;

			while (i < numberOfQueries) {
				int pickone = random.nextInt(parSize);
				if (!already.contains(pickone)) {
					Par query = IndoorSpace.gPartitions.get(pickone);
					this.Queries.add(query);
					already.add(pickone);
					i++;
				}
			}

			FileWriter fwQueries;
			
			try {
				fwQueries = new FileWriter(querisDir);
				for (int item : already) {
					fwQueries.write(item + ",");
				}
				fwQueries.write("\n");
				fwQueries.flush();
				fwQueries.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * @return the queries
	 */
	public List<Par> getQueries() {
		return Queries;
	}

	/**
	 * @param queries
	 *            the queries to set
	 */
	public void setQueries(List<Par> queries) {
		Queries = queries;
	}

}
