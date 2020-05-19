package ie.gmit.sw;

/**
 * QueryFile class is used to create query type objects.
 * 
 * @author G00362383 - Michael Mulholland
 * @version 1.0
 * @since Oxygen.3a Release (4.7.3a)
 *
 */
public class QueryFile {
	
	// declare variables
	private String queryText;
	private String queryLanguage;
	
	// constructor
	public QueryFile(String queryText, String language) {
		super();
		this.queryText = queryText;
		queryLanguage = language;
	}

	// Accessor methods
	public String getQueryText() {
		return queryText;
	}

	public void setQueryText(String queryText) {
		this.queryText = queryText;
	}

	public String getQueryLanguage() {
		return queryLanguage;
	}

	public void setQueryLanguage(String language) {
		queryLanguage = language;
	}
}
