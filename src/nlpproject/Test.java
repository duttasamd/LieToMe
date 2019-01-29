package nlpproject;

import java.util.concurrent.ConcurrentHashMap;

public class Test {

	public static void main(String[] args) {	
		
		//String data = WikiConnect.searchAndFetch("Danger (company)");
		
		//System.out.println(data);
//		KnowledgeBase.init();
//		ConcurrentHashMap<String, Double> synMap = new ConcurrentHashMap<String, Double>();
//		synMap.put("born", 1.0);
//		synMap.put("birth", 1.0);
//		synMap.put("birth place", 1.0);
//		synMap.put("first", 0.8);
//		synMap.put("nascence", 1.0);
//		KnowledgeBase.propertySynonyms.put("born", synMap);
//		
//		synMap = new ConcurrentHashMap<String, Double>();
//		synMap.put("died", 1.0);
//		synMap.put("death", 1.0);
//		synMap.put("death place", 1.0);
//		synMap.put("last", 0.8);
//		synMap.put("last place", 0.8);
//		KnowledgeBase.propertySynonyms.put("died", synMap);
//		
//		synMap = new ConcurrentHashMap<String, Double>();
//		synMap.put("founded", 1.0);
//		synMap.put("foundation place", 1.0);
//		synMap.put("first", 0.8);
//		synMap.put("innovation place", 1.0);
//		synMap.put("headquarters", 1.0);
//		KnowledgeBase.propertySynonyms.put("founded", synMap);
//		
//		synMap = new ConcurrentHashMap<String, Double>();
//		synMap.put("spouse", 1.0);
//		synMap.put("partner", 1.0);
//		synMap.put("better half", 1.0);
//		synMap.put("half", 0.5);
//		synMap.put("better", 0.5);
//		KnowledgeBase.propertySynonyms.put("spouse", synMap);
//		
//		synMap = new ConcurrentHashMap<String, Double>();
//		synMap.put("partner", 1.0);
//		synMap.put("spouse", 1.0);
//		synMap.put("better half", 1.0);
//		synMap.put("half", 0.5);
//		synMap.put("better", 0.5);
//		KnowledgeBase.propertySynonyms.put("partner", synMap);
//		
//		synMap = new ConcurrentHashMap<String, Double>();
//		synMap.put("role", 0.8);
//		synMap.put("star", 0.8);
//		synMap.put("stars", 1.0);
//		KnowledgeBase.propertySynonyms.put("star", synMap);
//		
//		synMap = new ConcurrentHashMap<String, Double>();
//		synMap.put("writer", 1.0);
//		synMap.put("author", 1.0);
//		KnowledgeBase.propertySynonyms.put("author", synMap);
//		
//		synMap = new ConcurrentHashMap<String, Double>();
//		synMap.put("award", 1.0);
//		synMap.put("honour", 1.0);
//		synMap.put("honor", 1.0);
//		KnowledgeBase.propertySynonyms.put("award", synMap);
//		
//		KnowledgeBase.commitUpdatedKnowledgeBase();
		
//		for(String prop : KnowledgeBase.propertySynonyms.keySet()) {
//			ConcurrentHashMap<String, Double> syns = KnowledgeBase.propertySynonyms.get(prop);
//			System.out.println("Prop : " + prop);
//			for(String syn : syns.keySet()) {
//				System.out.println("Syn : " + syn + " Value: " + syns.get(syn));
//			}
//			System.out.println("========");
//		}
		
//		String property = "nascence place";
//		String wikiExtract = "Born in Washington D.C.";
//		
//		ConcurrentHashMap<String, Double> synRatings = FactChecker.getSynonymRatings(property);
//		
//		for(String key : synRatings.keySet()) {
//			System.out.println("Key : " + key + " Value : " + synRatings.get(key));
//		}
//		
//		double matchRating = FactChecker.getMatchRating(synRatings, wikiExtract);
//		System.out.println("Match Rating : " + matchRating);
		
		String statement = "I am Samrat Dutta.";
		String value = "Samrat";
		
		if(statement.matches(".*\\b" + value + "\\b.*")) {
			System.out.println("Matches");
		} else {
			System.out.println("doesn't match");
		}
	}

}
