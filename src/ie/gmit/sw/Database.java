package ie.gmit.sw;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Database class
 * 
 * add(CharSequence s, Language lang)
 * 		-converts the kmer to a HashCode and Adds the HashCode and language to the Map
 * 
 * getLanguageEntries(Language lang)
 * 		-Check to see if the database contains the Language.
 * 		-Return the Map, if the database contains the language.
 * 		-If the database does not contain the language create a new Map and add it to the database.
 * 
 * resize(int max) 
 * 		-Adds top 300 entries into the Map.
 * 
 * getTop(int max, Language lang)
 * 		-sorts out the list and Keeps the top 300 entries 
 * 		-The first language entry in will be the highest ranking one
 * 
 * 	getLanguage(Map<Integer, Kmer> query) 
 * 		-Iterate over each language entry and add a new OutOfPlaceMetric,
 * 		-pass a language and call getOutOfPlaceDistance() method passing in the query and give it the Map for the language 
 * 		-and adds to a ordered TreeSet of the query file.
 * 
 * 	getOutOfPlaceDistance(Map<Integer, Kmer> query, Map<Integer, Kmer> subject)
 * 		-Compares the query Map with the subject Map.
 * 		-Check to see if the language entry is in the Map.
 * 		-If it is not in the Map, set the distance to the number of elements in the Map plus 1.
 * 		-If it is in the Map, set the distance to the subject rank minus the query rank.
 * 
 * @author G00362383 - Michael Mulholland
 * @version 1.0
 * @since Oxygen.3a Release (4.7.3a) *
 */
public class Database {

	// 235 languages - map that maps languages to there n-grams and frequency of occurance
	private Map<Language, Map<Integer, Kmer>> db = new ConcurrentHashMap<>();

	/**
	 * Adds a kmer and language to the Map. 
	 * Convert the string to its HashCode.  
	 * Get the language map for a particular language
	 * Frequency is set to 1. If the Map already has the kmer, inceament the frequency by one
	 * otherwise re-insert into the Map and overwrite the existing kmer with the new LanguageEntry
	 * The HashCode and language is stored in the Map
	 *  
	 * @param s
	 * @param lang
	 */
	public void add(CharSequence s, Language lang) {
		// convert the string to its HashCode
		int kmer = s.hashCode();

		// getLanguageEntries(lang) - get the language map for a particular language
		Map<Integer, Kmer> langDb = getLanguageEntries(lang);

		// set frequency to 1
		int frequency = 1;

		// If the Map already has the kmer, inceament the frequency by one
		if (langDb.containsKey(kmer)) {
			frequency += langDb.get(kmer).getFrequency();
		}

		// overwrite the existing kmer with the new LanguageEntry
		langDb.put(kmer, new Kmer(kmer, frequency));
	}

	/**
	 * Creates a new Map.
	 * Check to see if the database contains the Language.
	 * Return the Map, if the database contains the language.
	 * If the database does not contain the language create a new Map and add it to the database.
	 *  
	 * @param lang
	 * @return
	 */
	private Map<Integer, Kmer> getLanguageEntries(Language lang){
		// set Map to null
		Map<Integer, Kmer> langDb = null; 

		// if statement - checks to see if the database contains the Language
		if (db.containsKey(lang)) {
			// if the database contains the language, return the Map
			langDb = db.get(lang);
		}else {
			// if the database does not contains the language
			// create a new Map and add it to the database
			langDb = new ConcurrentHashMap<Integer, Kmer>();
			db.put(lang, langDb);
		}
		return langDb;
	}

	/**
	 * Adds top 300 entries into the Map.
	 * 
	 * @param max
	 */
	public void resize(int max) {
		// call keySet() which returns all 235 languages
		Set<Language> keys = db.keySet();

		// for each language entry
		for (Language lang : keys) {
			// mapping of the int to their language
			Map<Integer, Kmer> top = getTop(max, lang);

			// re-insert into the Map
			db.put(lang, top);
		}
	}

	/**
	 * getTop() - sorts out the list and Keeps the top 300 entries 
	 * The first language entry in will be the highest ranking one
	 *
	 * @param max
	 * @param lang
	 * @return
	 */
	public Map<Integer, Kmer> getTop(int max, Language lang) {
		// ConcurrentHashMap
		Map<Integer, Kmer> temp = new ConcurrentHashMap<>();

		// temp List - gives us the set of frequencies for the language
		List<Kmer> list = new ArrayList<>(db.get(lang).values());

		// sort the list
		Collections.sort(list);

		// set rank to 1
		int rank = 1;

		// for each language entry
		for (Kmer le : list) {
			// the first language entry in will be the highest ranking one
			// set rank to 1
			le.setRank(rank);

			// put into the temp Map
			temp.put(le.getKmer(), le);		

			// once we hit the number that is specified by the user, break
			if (rank == max) break;
			// rank plus 1
			rank++;
		}

		// return temp
		return temp;
	}

	/**
	 * Iterate over each language entry and add a new OutOfPlaceMetric, pass a language and call getOutOfPlaceDistance() method 
	 * passing in the query and give it the Map for the language and adds to a ordered TreeSet of the query file.
	 * 
	 * @param query
	 * @return
	 */
	public Language getLanguage(Map<Integer, Kmer> query) {
		// TreeSet that is ordered
		TreeSet<OutOfPlaceMetric> oopm = new TreeSet<>();

		// call keySet() which returns all 235 languages
		Set<Language> langs = db.keySet();

		// for each language entry
		for (Language lang : langs) {
			// add a new OutOfPlaceMetric, pass a language and call getOutOfPlaceDistance() method 
			//passing in the query and give it the Map for the language
			//then add into the sorted TreeSet
			oopm.add(new OutOfPlaceMetric(lang, getOutOfPlaceDistance(query, db.get(lang))));
		}
		return oopm.first().getLanguage();
	}

	/**
	 * Compares the query Map with the subject Map.
	 * Get language entry from the database.
	 * Check to see if the language entry is in the Map.
	 * If it is not in the Map, set the distance to the number of elements in the Map plus 1.
	 * If it is in the Map, set the distance to the subject rank minus the query rank.
	 * Return the distance.
	 * 
	 * @param query
	 * @param subject
	 * @return
	 */
	private int getOutOfPlaceDistance(Map<Integer, Kmer> query, Map<Integer, Kmer> subject) {
		// local variable
		int distance = 0;

		// create a new TreeSet based on the query values
		Set<Kmer> les = new TreeSet<>(query.values());	

		// for each language entry
		for (Kmer q : les) {
			// get language entry from the database
			Kmer s = subject.get(q.getKmer());

			// check to see if the language entry is there
			if (s == null) {
				// set the distance 
				// for example: if there is 300 elements, then the distance will be set to 301
				distance += subject.size() + 1;
			}else {
				// set the distance to the subject rank minus the query rank
				distance += s.getRank() - q.getRank();
			}
		}

		// return the distance
		return distance;
	}
}