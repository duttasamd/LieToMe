package nlpproject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class LieToMe {
	//public final static boolean DEBUG = true;
	public final static boolean DEBUG = false;
	public final static String TEST_FILE = "res/test.tsv";
	public final static String TRAIN_FILE = "res/train.tsv";
	public final static String OUTPUT_FILE = "res/output.ttl";
	public final static String RESULT_FILE = "res/result.ttl";
	public final static String MINITEST_FILE = "res/minitest.tsv";
	public final static String MINITESTOUTPUT_FILE = "res/minitestoutput.tsv";

	public static void main(String[] args) {	

		ArrayList<Tenor> tenorList = new ArrayList<Tenor>();	
		
		System.out.println("Fetching All Sentences.");
		ArrayList<String> sentenceList;
		
		if(DEBUG) {
			sentenceList = IOProcessor.getAllSentences(MINITEST_FILE);
		} else {
			sentenceList = IOProcessor.getAllSentences(TRAIN_FILE);
		}
		
		init();
		
		System.out.println("Training processor with models.");
		long startTime = System.currentTimeMillis();
		Processor pc = new Processor();
		long endTime = System.currentTimeMillis();
		System.out.println("Training complete. This took: " + (endTime - startTime) + " seconds.");
		
		ExecutorService es = Executors.newFixedThreadPool(10);
		
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
        
		System.out.println("Total Lines : " + sentenceList.size()
				+ " Correct: " + correct + " Incorrect: " + (falsePositives + falseNegatives) + " False +ve: " + falsePositives + " False -ve: " + falseNegatives
				+ " Positive: " + positives + " Negative: " + negatives + " Total Processed: " + (positives + negatives) + " Correct %: " + ((double) correct/(positives + negatives) * 100));

		
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
		DataManager.init();
		KnowledgeBase.init();
	}
}
