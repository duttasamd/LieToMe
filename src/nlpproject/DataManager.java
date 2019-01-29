package nlpproject;

import java.util.concurrent.ConcurrentHashMap;

public class DataManager {
	static ConcurrentHashMap<String, String> wikiCache;
	
	public static String fetchWikiData(String searchTerm) {
		String wikiInfo;
		
		wikiInfo = wikiCache.get(searchTerm);
		
		if(wikiInfo == null || wikiInfo.equals("")) {
			wikiInfo = WikiConnect.searchAndFetch(searchTerm);
			wikiCache.put(searchTerm, wikiInfo);
		}
		
		return wikiInfo;
	}
	
	public static void init() {
		wikiCache = new ConcurrentHashMap<String, String>();
	}
}
