package ie.gmit.sw;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ExecuteQuery class implements Runnable
 * 
 * parse()
 * 		-Get substrings of the queryText with kmers of size 2, 3 and 4.
 * 		-Add the kmer and the query language to the database
 * 
 * analyseQuery()
 * 		-Break up the query into substrings of kmers of size 2, 3, 4 and converts the substring kmers into HashCodes
 * 		-If the hashCode for the kmer exists, add 1 to the hashCodes frequency. If it does not, add the new hashCode with a frequency of 1 to the Map
 * 		-Creats a temp List for storing the values in the Map.
 * 		-Each first language entry in will be the highest ranking one then put into the sortMap Map. When rank reaches 300, break
 * 		-Output the language in which the query appears to be written in 
 * 
 * run()
 * 		-While keepRunning is true the run method will take from the head of the Blocking Queue.
 * 		-If the query language is equal to "Finished" (This means the poison has been found), keepRunning will be set to false and the method will be finished
 * 		-If the query language is not equal to "Finished", parse q 
 * 
 * @author G00362383 - Michael Mulholland
 * @version 1.0
 * @since Oxygen.3a Release (4.7.3a)
 */
public class ExecuteQuery implements Runnable{

	// declare variables
	private BlockingQueue<QueryFile> blockingQueue = null;
	private Database db = null;
	private int k;
	private boolean keepRunning = true;

	// constructor
	public ExecuteQuery(BlockingQueue<QueryFile> blockingQueue, Database db, int k) {
		super();
		this.blockingQueue = blockingQueue;
		this.db = db;
		this.k = k;
	}	

	// accessor methods
	public BlockingQueue<QueryFile> getBlockingQueue() {
		return blockingQueue;
	}

	public void setBlockingQueue(BlockingQueue<QueryFile> blockingQueue) {
		this.blockingQueue = blockingQueue;
	}

	public Database getDb() {
		return db;
	}

	public void setDb(Database db) {
		this.db = db;
	}

	public int getK() {
		return k;
	}

	public void setK(int k) {
		this.k = k;
	}

	/**
	 * Get substrings of the queryText with kmers of size 2, 3 and 4.
	 * Add the kmer and the query language to the database
	 * 
	 * @param query
	 * @param ks
	 */
	private void parse(QueryFile query, int... ks) {
		// gets the query language
		Language language = Language.valueOf(query.getQueryLanguage());	

		// adds the query text to a String so that it can be broke into kmers
		String queryText = query.getQueryText();

		// nessted for loop to get kmers of size 2, 3 and 4
		for(int i = 2; i <= k; i++) {
			for (int j = 0; j <= queryText.length() - i; j++) {
				// get substrings of the queryText with kmers of size 2, 3 and 4.
				CharSequence kmer = queryText.substring(j, j + i);

				// add the kmer and the query language to the database
				db.add(kmer, language);
			}
		}
	}// parse

	/**
	 * Creates two ConcurrentHashMaps. One for the query and one for sorting the Map.
	 * Nested for loop to break up the query into substrings of kmers of size 2, 3 and 4.
	 * Convert the substring kmers into HashCodes
	 * If the hashCode for the kmer exists, add 1 to the hashCodes frequency
	 * If the hashCode for the kmer does not exist, add the new hashCode with a frequency of 1 to the Map
	 * 
	 * Create a temp List for storing the values in the Map.
	 * Each first language entry in will be the highest ranking one then put into the sortMap Map
	 * When rank reaches 300, break
	 * Output the language in which the query appears to be written in 
	 * 
	 * @param s
	 */
	public void analyseQuery(String s) {
		// ConcurrentHashMap for the query
		Map<Integer, Kmer> queryDB = new ConcurrentHashMap<>();

		// ConcurrentHashMap to sort the query
		Map<Integer, Kmer> sortMap = new ConcurrentHashMap<>();

		// set frequency to 1
		int frequency = 1;

		// nessted for loop to get kmers of size 2, 3 and 4
		for(int i = 2; i <= 4; i++) {
			for (int j = 0; j <= s.length() - i; j++) {
				// get substrings of the query with kmers of size 2, 3 and 4.
				CharSequence kmer = s.substring(j, j + i);

				// gets the kmer hashCode and stores it into an int
				int kmerH = kmer.hashCode();

				// if statement to see if the hashCode for the kmer exists
				if (queryDB.containsKey(kmerH)) {
					// if the hashCode for the kmer exists, add 1 to the kmers frequency
					frequency += queryDB.get(kmerH).getFrequency();
				}

				// if the hashCode for the kmer does not exist, add the new hashCode with a frequency of 1 to the Map
				queryDB.put(kmerH, new Kmer(kmerH, frequency));	
			}
		}// for

		// temp List - gives us the values in the Map. 
		List<Kmer> list = new ArrayList<Kmer>(queryDB.values());

		// set rank to 1
		int rank = 1;

		// for each language entry
		for (Kmer le : list) {
			// the first language entry in will be the highest ranking one
			// set rank to 1
			le.setRank(rank);

			// put into the sortMap Map
			sortMap.put(le.getKmer(), le);	

			// when rank reaches 300, break
			if (rank == 300) break;
			rank++;
		}

		// output the language that it appears to be written in 
		System.out.println("\nThe text appears to be written in.... " + db.getLanguage(sortMap));
	}

	/**
	 * While keepRunning is true the run method will take from the head of the Blocking Queue.
	 * If the query language is equal to "Finished" (This means the poison has been found), keepRunning will be set to false and the method will be finished
	 * If the query language is not equal to "Finished", parse q 
	 */
	@Override
	public void run() {
		// keep running while true
		while (keepRunning) {
			try {
				// take from the head of the Blocking Queue
				QueryFile q = blockingQueue.take();

				// If the query language is equal to "Finished", the poison has been found
				// set keepRunning to false
				if (q.getQueryLanguage().equals("Finished")) {
					keepRunning = false;
				} else {
					// If the query language is not equal to "Finished", parse q 
					parse(q);
				}
			}catch (Exception e) {
				System.out.println("4");
				e.printStackTrace();
			}
		}
	}
}
