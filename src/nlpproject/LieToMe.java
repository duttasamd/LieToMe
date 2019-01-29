package nlpproject;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.w3c.dom.Document;

public class LieToMe {
	//public final static boolean DEBUG = true;
	public static boolean DEBUG;
	
	public static String CONFIG_XML = "config.xml";
	public static String TEST_FILE;
	public static String TRAIN_FILE;
	public static String OUTPUT_FILE;
	public static String RESULT_FILE;
	public static String MINITEST_FILE;
	public static String MINITESTOUTPUT_FILE;
	public static int NUM_THREADS_WIKIFETCH;
	public static String KnowledgeBaseXML;

	public static void main(String[] args) {	

		
		ArrayList<Tenor> tenorList = new ArrayList<Tenor>();	

		init();
		
		if(LieToMe.DEBUG) {
			System.out.println("DEBUG MODE");
		}
		
		System.out.println("Fetching All Sentences.");
		ArrayList<String> sentenceList;
		
		if(DEBUG) {
			sentenceList = IOProcessor.getAllSentences(TRAIN_FILE);
		} else {
			sentenceList = IOProcessor.getAllSentences(TEST_FILE);
		}	
		
		System.out.println("Training processor with models.");
		long startTime = System.currentTimeMillis();
		Processor pc = new Processor();
		long endTime = System.currentTimeMillis();
		System.out.println("Training complete. This took: " + (endTime - startTime) + " seconds.");
		
		ExecutorService es = Executors.newFixedThreadPool(NUM_THREADS_WIKIFETCH);
		
		startTime = System.currentTimeMillis();
		System.out.println("Tokenizing sentences. Finding Names and POS.");
		for(String sentence : sentenceList) {
			Tenor tenor = new Tenor(sentence.trim());
        	pc.tokenize(tenor);			
			pc.findNames(tenor);			
			pc.findPOS(tenor);
			tenorList.add(tenor);
		}
		endTime = System.currentTimeMillis();
		pc.cleanup();
		System.out.println("Processing complete. This took : " + (endTime - startTime) + " miliseconds.");
		
		System.out.println("Fetching WikiData...");
		startTime = System.currentTimeMillis();
		for(Tenor tenor : tenorList) {			
			es.execute(new TenorProcessor(tenor));
		}
		
		es.shutdown();
        try {
            es.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        endTime = System.currentTimeMillis();
        System.out.println("Wiki Fetch complete. This took: " + TimeUnit.MILLISECONDS.toSeconds(endTime - startTime) + " seconds.");
		
        
        
        int positives = 0;
        int negatives = 0;
        int falsePositives = 0;
        int falseNegatives = 0;
        int correct = 0;
        
        for(Tenor tenor : tenorList) { 
        	if(tenor.wikiExtract == null || tenor.wikiExtract.isEmpty()) {
    			tenor.estimatedTruthValue = -1.0;
    		} else {
    			FactChecker.getRating(tenor);
    		}
        	
        	if(tenor.truthValue == 0.0) {
        		if(tenor.estimatedTruthValue == -1.0) {
        			correct++;
        			negatives++;
        		} else {
        			positives++;
        			falsePositives++;
        		}
        	} else {
        		if(tenor.estimatedTruthValue == 1.0) {
        			correct++;
        			positives++;
        		} else {
        			negatives++;
        			falseNegatives++;
        		}
        	}        	
        }
        
		if(DEBUG) {
			System.out.println("Total Lines : " + sentenceList.size()
			+ " Correct: " + correct + " Incorrect: " + (falsePositives + falseNegatives) + " False +ve: " + falsePositives + " False -ve: " + falseNegatives
			+ " Positive: " + positives + " Negative: " + negatives + " Total Processed: " + (positives + negatives) + " Correct %: " + ((double) correct/(positives + negatives) * 100));
		} else {
			System.out.println("Total Lines Processed : " + sentenceList.size() + " Total positive estimates : " + positives + " Total negative estimates : " + negatives);
		}

		
		System.out.println("Writing to outputfile.");
		
		if(DEBUG) {
			IOProcessor.writeFactCheckResultToFile(tenorList, MINITESTOUTPUT_FILE);
		} else {
			IOProcessor.writeFactCheckResultToFile(tenorList, OUTPUT_FILE);
			IOProcessor.writeResultsToFile(tenorList, RESULT_FILE);
		}
    	
    	
    	System.out.println("End of task.");
	}
	
	public static class TenorProcessor implements Runnable {
        Tenor tenor;
        public TenorProcessor(Tenor tenor) {
            this.tenor = tenor;
        }

        @Override
        public void run() {
			String wikiInfo = DataManager.fetchWikiData(tenor.wikiSearchTerm);
			tenor.wikiInfo = wikiInfo;
			String extract = FactChecker.extractInfo(tenor, wikiInfo);
			if(extract == null && !tenor.verb.trim().equals("is")) {
				tenor.wikiSearchTerm = tenor.value.replaceAll("'s*", "");
				tenor.value = tenor.subject;
				tenor.subject = tenor.wikiSearchTerm;
				if(tenor.property == null || tenor.property.equals("")) {
					tenor.property = tenor.verb;
				}
				wikiInfo = DataManager.fetchWikiData(tenor.wikiSearchTerm);
				if(wikiInfo != null && !wikiInfo.equals("")) {
					extract = FactChecker.extractInfo(tenor, wikiInfo);
				}
			}
			tenor.wikiExtract = extract;
        }
    }
	
	public static void init() {		
		
		try {
			Properties prop = new Properties();
			prop.loadFromXML(new FileInputStream(CONFIG_XML));
			
			String debug = prop.getProperty("debug");
			if(debug.equals("1")) {
				DEBUG = true;
			} else {
				DEBUG = false;
			}
			TEST_FILE = prop.getProperty("inputFile");
			TRAIN_FILE = prop.getProperty("debugInputFile");
			OUTPUT_FILE = prop.getProperty("outputFile");
			MINITESTOUTPUT_FILE = prop.getProperty("debugOutputFile");
			RESULT_FILE = prop.getProperty("resultFile");
			KnowledgeBaseXML = prop.getProperty("knowledgeFile");
			NUM_THREADS_WIKIFETCH = Integer.parseInt(prop.getProperty("numThreadsWiki"));
			
			System.out.println("Loaded Configuration File.");
		} catch(Exception ex) {
			
			System.out.println("Couldn't load Configuration File." + ex.getMessage());
			 DEBUG = false;
			 TEST_FILE = "res/test.tsv";
			 TRAIN_FILE = "res/train.tsv";
			 OUTPUT_FILE = "res/output.ttl";
			 RESULT_FILE = "res/result.ttl";
			 MINITEST_FILE = "res/minitest.tsv";
			 MINITESTOUTPUT_FILE = "res/minitestoutput.tsv";
			 KnowledgeBaseXML = "res/knowledge_base.xml";
			 NUM_THREADS_WIKIFETCH = 10;
		}
		
		DataManager.init();
		KnowledgeBase.init();
	}
}
