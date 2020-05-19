package ie.gmit.sw;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Menu class displays the header and prompts the user to enter two files.
 * The first file is the language dataset location.
 * The second file is the query file location.
 * Two files will be used to generate a result.
 * 
 * @author G00362383 - Michael Mulholland
 * @version 1.0
 * @since Oxygen.3a Release (4.7.3a)
 */
public class Menu {

	// variables
	private int kmerSize = 4; // has to stay at 4
	private volatile boolean found = true;
	private String inFile;
	private String query;
	private int userChoice;
	File databaseFile;
	Scanner sc = new Scanner(System.in);

	/**
	 * menu() method calls the header() and start() methods 
	 * The method keeps running while found is set to true
	 * 
	 * @throws IOException
	 */
	// program menu
	public void menu() throws IOException {
		// continue while found is true
		while(found) {
			header();
			start();
			found = false;
		}
	}

	/**
	 * Displays the program header to the user
	 */
	// header method to display the header to the console
	public void header() {
		System.out.println("***************************************************");
		System.out.println("* GMIT - Dept. Computer Science & Applied Physics *");
		System.out.println("*                                                 *");
		System.out.println("*              Text Language Detector             *");
		System.out.println("*                                                 *");
		System.out.println("***************************************************");
	}

	/**
	 * start method prompts the user to enter two files.
	 * The first file is the language dataset location.
	 * The second file is the query file location.
	 * Two files will be used to generate a result.
	 * The result is the language of the text in the query file.
	 */
	private void start() {

		// prompts user to enter a file
		// error handling to see if the file exists
		do {
			System.out.println("\nEnter WiLI Data Location:");
			inFile = sc.next();
			databaseFile = new File(inFile);

			//Error Handling
			if(databaseFile.length() == 0) {
				found = false;
				System.out.println("No Such File");
			}else {
				found = true;
				System.out.println("\nBuilding subject database...please wait...");
			}
		} while (!found);

		// BlockingQueue for the the first input file
		BlockingQueue<QueryFile> queue = new ArrayBlockingQueue<QueryFile>(10);

		// passes the first input file and the BlockingQueue to the constructor in the Parser class
		Parser p = new Parser(inFile, queue);

		// creates a new instance of Database
		Database db = new Database();

		// passes the BlockingQueue, database and kmer size to the ExecuteQuery class
		ExecuteQuery exQuery = new ExecuteQuery(queue, db, kmerSize);

		// create threads
		Thread t1 = new Thread(p);
		Thread t2 = new Thread(exQuery);
		Thread t3 = new Thread(exQuery);
		Thread t4 = new Thread(exQuery);
		Thread t5 = new Thread(exQuery);

		// start threads
		t1.start();
		t2.start();
		t3.start();
		t4.start();
		t5.start();

		try {
			// join the threads
			// It will put the current thread on wait until the thread on which it is called is dead. 
			// If thread is interrupted then it will throw InterruptedException.
			t1.join();
			t2.join();
			t3.join();
			t4.join();
			t5.join();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		// get the top 300 most frequent
		db.resize(300);

		do {
			//Enter Query location
			System.out.println("\nEnter Query Location:");
			inFile = sc.next();
			databaseFile = new File(inFile);
			//Error Handling
			if(databaseFile.length() == 0) {
				found = false;
				System.out.println("\nNo Such File");
			}else {
				found = true;
				System.out.println("\nProcessing query...please wait...");			}
		} while (!found);

		query="";
		try {
			query = new String(Files.readAllBytes(Paths.get(inFile)));
		} catch (Exception e) {
			System.out.println("Error - Runner");
		}

		exQuery.analyseQuery(query);

		System.out.println("\nPress 1 to start again or 0 to exit: ");
		userChoice = sc.nextInt();

		if(userChoice == 1) {
			start();
		} else {
			System.out.println("\nThe program has ended.");
		}
	}
}
