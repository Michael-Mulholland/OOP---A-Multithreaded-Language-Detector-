package ie.gmit.sw;

/**
 * Kmer class implements Comparable
 * compare one kmer to another by there frequency
 * this is done in decending order
 * most frequently occuring first
 * 
 * @author G00362383 - Michael Mulholland
 * @version 1.0
 * @since Oxygen.3a Release (4.7.3a) *
 */
public class Kmer implements Comparable<Kmer> {

	// variable
	private int kmer;
	private int frequency;
	private int rank;

	// constructor
	public Kmer(int kmer, int frequency) {
		super();
		this.kmer = kmer;
		this.frequency = frequency;
	}

	// accessor methods
	public int getKmer() {
		return kmer;
	}

	public void setKmer(int kmer) {
		this.kmer = kmer;
	}

	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	// compare one kmer to another by there frequency
	// this is done in decending order
	// most frequently occuring first
	@Override
	public int compareTo(Kmer next) {
		return - Integer.compare(frequency, next.getFrequency());
	}

	@Override
	public String toString() {
		return "[" + kmer + "/" + frequency + "/" + rank + "]";
	}
}