package nlpproject;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WikiConnect {
	private static final String Google = "http://www.google.com/search?q=";
	private static final String WikiBaseURL = "http://en.wikipedia.org";
	private static final String WikiURL = "http://en.wikipedia.org/wiki/";
	private static final String WikiSearchURL = "http://en.wikipedia.org/w/index.php?title=Special:Search&go=GO&search=";
	
	private static final String UserAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_2) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/12.0.2 Safari/605.1.15";
	
	public static String fetch(String token) {
		String data = "";
		
		try {			
			Response res = Jsoup.connect(WikiURL + token).execute();
			String body = res.body();
			
			Document doc = Jsoup.parseBodyFragment(body);
			
			Elements el = doc.select(".infobox tr");
			
			for(Element e : el) {
				data += e.text() + "\n";
			}
			
		} catch (IOException ex) {
			
		}		

		return data;
	}
	
	public static String searchAndFetch(String token) {
		String data = "";
		
		try {		
			Document doc = Jsoup.connect(WikiSearchURL + token).userAgent(UserAgent).timeout(45000).get();
			
			String title = doc.title();
			if(title.toLowerCase().contains("search")) {
				Elements els = doc.select("a[data-serp-pos='0']");
				
				if(els != null) {
					Element el = els.first();
					if(el != null) {
						String searchTerm = el.attr("href");
						searchTerm = WikiBaseURL + searchTerm;
						doc = Jsoup.connect(searchTerm).userAgent(UserAgent).timeout(45000).get();
					}					
				}
			} 
			
			Elements el = doc.select(".infobox tr");
			
			if(el.isEmpty()) {
				Elements els = doc.select("p");
				if(els != null && els.first() != null)
					data = els.first().text().replace(". ", ".\n");
			} else {
				for(Element e : el) {
					data += e.text() + "\n";
				}
			}		
		} catch (IOException ex) {
			
		}
		

		return data;
	}

}
