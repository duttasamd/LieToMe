package nlpproject;

import java.util.concurrent.ConcurrentHashMap;

public class FactChecker {
	public static String extractInfo(Tenor tenor, String text) {
		String extract = null;
		
		text = text.toLowerCase();
		
		if(LieToMe.DEBUG) {
			System.out.println("Search for : " + tenor.value);
		}
		
		String[] statements = text.split("\n");
		for(String statement : statements) {
			//System.out.println("Search: " + tenor.value.toLowerCase() + " : " + statement);
			statement = statement.trim().replaceAll("[.,;]", "");
			
			if(statement.contains(tenor.value.toLowerCase())) {
				if(LieToMe.DEBUG) {
					System.out.println("Match : " + statement);
				}
				tenor.cumulativeRating = 1.0;
				extract = statement;
				break;
			}
		}
		
		if(extract == null || extract.equals("")) {
			if(LieToMe.DEBUG) {
				System.out.println("No exact match : " + tenor.cumulativeRating);
			}
			int maxMatchCount = 0;
			String currentExtract = null;
			for(String statement : statements) {
				int matchCount = 0;
				for(String valueToken : tenor.value.toLowerCase().split(" ")) {
//					valueToken = valueToken.replace("(", "\\(");
//					valueToken = valueToken.replace(")", "\\)");
					if(LieToMe.DEBUG) {
						System.out.println("Trying to Match - " + valueToken);
					}
					if(valueToken.contains("(") || valueToken.contains(")")) {
						if(statement.contains(valueToken)) {
							if(LieToMe.DEBUG) {
								System.out.println("Match - " + valueToken);
							}
							//System.out.println("Match - " + valueToken);
							currentExtract = statement;
							matchCount++;
						}
					} else {
						if(statement.matches(".*\\b" + valueToken + "\\b.*")) {
							if(LieToMe.DEBUG) {
								System.out.println("Match - " + valueToken);
							}
							//System.out.println("Match - " + valueToken);
							currentExtract = statement;
							matchCount++;
						}
					}
				}
				if(matchCount > maxMatchCount) {
					maxMatchCount = matchCount;
					extract = currentExtract;
				}
			}
			
			if(LieToMe.DEBUG) {
				System.out.println("value length : " + tenor.value.split(" ").length);
				System.out.println("cumulative : " + (((double)maxMatchCount/tenor.value.split(" ").length) - 0.5));
			}
			if(maxMatchCount > 0) {
				tenor.cumulativeRating = (((double)maxMatchCount/tenor.value.split(" ").length) - 0.5);
			} else {
				tenor.cumulativeRating = - 1.0;
			}
		}
		
		if(LieToMe.DEBUG) {
			System.out.println("After extract match : " + tenor.cumulativeRating);
		}
		
		
		return extract;
	}
	
	private static ConcurrentHashMap<String, Double> getSynonymRatings(String property) {
		ConcurrentHashMap<String, Double> matchMap = new ConcurrentHashMap<String, Double>();
		
		for(String prop : KnowledgeBase.propertySynonyms.keySet()) {
			ConcurrentHashMap<String, Double> syns = KnowledgeBase.propertySynonyms.get(prop);
			double rating = -2.0;
			for(String syn : syns.keySet()) {
				if(LieToMe.DEBUG) {
					System.out.println("Property : " + property + " Checking - " + syn);		
				}
				if(property.toLowerCase().contains(syn) && rating < syns.get(syn)) {
					if(LieToMe.DEBUG) {
						System.out.println("Property : " + property + " Matches - " + syn);			
					}
					rating = syns.get(syn);
				}
			}
			matchMap.put(prop, rating);
		}
		
		return matchMap;
	}
	
	private static double getMatchRating(ConcurrentHashMap<String, Double> synRatings, String wikiExtract) {
		double matchRating = 0;
		
		
		wikiExtract = wikiExtract.toLowerCase();
		for(String key : synRatings.keySet()) {
			
			if(wikiExtract.contains(key)) {
				if(LieToMe.DEBUG) {
					System.out.println("Match - " + key + " " + synRatings.get(key));
				}
				matchRating += synRatings.get(key);
			}
		}
		
		if(LieToMe.DEBUG) {
			System.out.println("Match Rating : " + matchRating);
		}

		return matchRating;
	}
	
	public static double getRating(Tenor tenor) {
		double rating = 0.0;
		ConcurrentHashMap<String, Double> synRatings = FactChecker.getSynonymRatings(tenor.property);
		tenor.cumulativeRating += FactChecker.getMatchRating(synRatings, tenor.wikiExtract);
		
		if(tenor.cumulativeRating >= 0) {
			rating = 1.0;
		} else {
			rating = -1.0;
		}
		tenor.estimatedTruthValue = rating;
		return rating;
	}
}
