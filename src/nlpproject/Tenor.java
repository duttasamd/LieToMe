package nlpproject;

public class Tenor {	
	public int factID;
	public String sentence;
	public String taggedSentence;
	public String[] tokens;
	public String[] tags;
	public String verb;
	public String extractedSubject;
	public String subject;
	public String property;
	public String value;
	
	public String wikiSearchTerm;
	public String wikiInfo;
	public String wikiExtract;
	
	public double cumulativeRating;
	public double truthValue;
	public double estimatedTruthValue;
	
	public Tenor(String sentence) {
		super();
		this.sentence = sentence;
	}
}
