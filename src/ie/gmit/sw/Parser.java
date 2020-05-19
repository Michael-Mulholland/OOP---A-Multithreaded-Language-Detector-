package ie.gmit.sw;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;

/**
 * Parser class implements Runnable 
 * It parses the language dataset and adds each line of text to the BlockingQueue.
 * 
 * @author G00362383 - Michael Mulholland
 * @version 1.0
 * @since Oxygen.3a Release (4.7.3a)
 *
 */
public class Parser implements Runnable{

	// variables
	private BlockingQueue<QueryFile> blockQue = null;
	private String file;

	// constructer
	public Parser(String file, BlockingQueue<QueryFile> queue) {
		super();
		this.file = file;
		this.blockQue = queue;
	}

	/**
	 * parses the language dataset that is entered by the user. Splits each line of text at the '@' symbol, saving the language text and language
	 * into a String Array. 
	 * The language text and language name are then passed to a constructor in the QueryFile class to make new objects
	 * which are then added to the BlockingQueue.  
	 * Finally, to stop the BlockingQueue, it is poisoned.  
	 */
	@Override
	public void run() {

		try {
			// BufferedReader to point at the file
			BufferedReader  br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

			// set the String to nul
			String line = null;

			// while line is not equals to null
			while ((line = br.readLine()) != null) {
				// for each line split at the '@' symbol
				String[] record = line.trim().split("@");

				// record length is not equal to two, continue
				if(record.length != 2) {
					continue;
				}		

				// put text and language onto the Blocking Queue
				blockQue.put(new QueryFile(record[0], record[1]));
			}

			// close BufferedReader
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// Poison the Blocking Queue
				blockQue.put(new QueryFile("Poison", "Finished"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
