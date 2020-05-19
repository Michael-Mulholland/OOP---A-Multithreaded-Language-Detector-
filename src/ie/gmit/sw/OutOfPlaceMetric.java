package ie.gmit.sw;

/**
 * OutOfPlaceMetric class implements Comparable
 * getAbsoluteDistance() - if overall distance is negative, Math.abs will change the negative number to a positive number
 * 
 * @author G00362383 - Michael Mulholland
 * @version 1.0
 * @since Oxygen.3a Release (4.7.3a)
 */
public class OutOfPlaceMetric implements Comparable<OutOfPlaceMetric>{
	// declare variables
	private Language lang;
	private int distance;

	// constructor
	public OutOfPlaceMetric(Language lang, int distance) {
		super();
		this.lang = lang;
		this.distance = distance;
	}

	// accessor methods
	public Language getLanguage() {
		return lang;
	}

	public int getAbsoluteDistance() {
		// if overall distance is negative, Math.abs will change the negative number to a positive number
		return Math.abs(distance);
	}

	// returns in ascending order
	@Override
	public int compareTo(OutOfPlaceMetric o) {
		return Integer.compare(this.getAbsoluteDistance(), o.getAbsoluteDistance());
	}

	@Override
	public String toString() {
		return "[lang=" + lang + ", distance=" + getAbsoluteDistance() + "]";
	}
}