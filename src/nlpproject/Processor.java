package nlpproject;


import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import opennlp.tools.namefind.*;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.Span;

public class Processor {
	Tokenizer tokenizer;
	NameFinderME finder;
	POSTaggerME tagger = null;
	
	public Processor() {
		TokenNameFinderModel model = null;
		POSModel posModel = null;
		
		try {
			model = new TokenNameFinderModel(
					new File("res/en-ner-person.bin"));
			posModel = new POSModel(
					new File("res/en-pos-maxent.bin"));
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
		
		finder = new NameFinderME(model);
		tokenizer = WhitespaceTokenizer.INSTANCE;
		tagger = new POSTaggerME(posModel);
	}
	
	public void tokenize(Tenor tenor) {
		
		tenor.tokens = tokenizer.tokenize(tenor.sentence);
		try {
			tenor.factID = Integer.parseInt(tenor.tokens[0].trim());
			tenor.sentence = tenor.sentence.substring(tenor.sentence.indexOf("\t") + 1);
		} catch (Exception ex) {
			
		}
		
		try {
			tenor.truthValue = Double.parseDouble(tenor.tokens[tenor.tokens.length - 1].trim());
			tenor.sentence = tenor.sentence.substring(0, tenor.sentence.trim().lastIndexOf("\t"));
		} catch (Exception ex) {
			
		}
	}
	
	public String findNames(Tenor tenor) {
		String names = "";
		String namePattern = "(([A-Z][\\w-]*\\s+)+((\\(+\\w+\\)+('s*)*)) {1})";
		//String namePattern = "(([A-Z][\\w-]*\\s+)*([A-Z][\\w-]*'\\w*)+)";
		Pattern nPattern = Pattern.compile(namePattern);
		
		Matcher m = nPattern.matcher(tenor.sentence);
		
		if(m.find()) {
			if(LieToMe.DEBUG) {
				System.out.println("Found ()- " + m.group(1));				
			}
			names = m.group(1);
		} else {
			//namePattern = "(([A-Z][\\w-]*\\s+)*(\\(.*\\))'s*)";
			namePattern = "(([A-Z][\\w-]*\\s+)*([A-Z][\\w-]*'\\w*)+)";
			nPattern = Pattern.compile(namePattern);
			m = nPattern.matcher(tenor.sentence);
			if(m.find()) {
				if(LieToMe.DEBUG) {
					System.out.println("Found - " + m.group(1));				
				}
				names = m.group(1);
			}
		}
		
		//System.out.println(names);
		
		if(names == null || names.equals("")) {
			Span[] nameSpans = finder.find(tenor.tokens);		
			
			String[] spanns = Span.spansToStrings(nameSpans, tenor.tokens);
			
			for (int i = 0; i < spanns.length; i++) {
				names += spanns[i] + " "; 
			}
		}
		
		names = names.replaceAll("'s*", "").trim();
		
		tenor.extractedSubject = names;
		
		return names;
	}
	
	public void findPOS(Tenor tenor) {		
		String subject = "";
		String verb = "";
		String property = "";
		String value = "";
		String lastTag = "";
		boolean isFirstNNPSubject = false;
		
		//System.out.println("Original subject : " + tenor.subject);
		
		tenor.tags = tagger.tag(tenor.tokens);
		
		boolean firstHalf = true;
		for(int i=1; i<tenor.tags.length; i++) {
			
			String currentTag = tenor.tags[i];
			POSSample tokensTagged = new POSSample(tenor.tokens, tenor.tags);		
			tenor.taggedSentence = tokensTagged.toString();
			
			if(!currentTag.equals("Det") && Character.isUpperCase(tenor.tokens[i].charAt(0))) {
				currentTag = "NNP";
			}
			
			if(tenor.tokens[i].matches("\\(.*\\)")) {
				currentTag = "NNP";
				tenor.tags[i] = "NNP";
			}
			
			if(currentTag.equals("CD") && i < tenor.tags.length - 1) {
				currentTag = "NNP";
				tenor.tags[i] = "NNP";
			}
			
			if(currentTag.equals("NNS")) {
				isFirstNNPSubject = true;
				currentTag = "VB";
				tenor.tags[i] = "VB";
			}
			
			if(currentTag.equals("JJ")) {
				currentTag = "NN";
				tenor.tags[i] = "NN";
			}
			
			if(currentTag.equals("NN") || (currentTag.equals("."))) {
				property += tenor.tokens[i] + " ";
			} else {
				if(firstHalf) {
					if(currentTag.equals("NNP") || currentTag.equals("NNPS") || (currentTag.equals("DT") && lastTag.equals("NNP"))
							|| (currentTag.equals("Det") && lastTag.equals("NNP"))
							|| (currentTag.equals("JJ") && lastTag.equals("NNP")) 
							|| (currentTag.equals("PP") && lastTag.equals("NNP"))
							|| (currentTag.equals("IN") && lastTag.equals("NNP"))) {					
						subject += tenor.tokens[i] + " ";
					} else if(currentTag.equals("VBZ") || currentTag.equals("VB") ||
							currentTag.equals("VBD")) {
						firstHalf = false;
						verb = tenor.tokens[i];
					}
				} else {
					if(currentTag.equals("NNP")|| currentTag.equals("NNPS")|| (currentTag.equals("DT") && lastTag.equals("NNP"))
							|| (currentTag.equals("Det") && lastTag.equals("NNP"))
							|| (currentTag.equals("JJ") && lastTag.equals("NNP")) 
							|| (currentTag.equals("PP") && lastTag.equals("NNP"))
							|| (currentTag.equals("IN") && lastTag.equals("NNP"))) {					
						value += tenor.tokens[i] + " ";
					}
				}
			}
			
			lastTag = currentTag;
		}
		
		
		value = value.replaceAll("'s*", "").trim();
		
		if(!isFirstNNPSubject) {
			if(value.contains(tenor.extractedSubject) && !value.contains(",")) {
				tenor.extractedSubject = value;
				value =	subject.trim(); 
				subject = tenor.extractedSubject; 
			}
		}		 
		
		if(subject == null || subject.equals("")) {
			subject = tenor.extractedSubject;
		}
		
		tenor.verb = verb.trim().replaceAll("[.,;]", "");
		tenor.property = property.trim().replaceAll("[.,;]", "");
		tenor.value = value.trim().replaceAll("[.,;]", "");
		tenor.subject = subject.trim().replaceAll("'s*", "").replaceAll("[.,;]", "");
		tenor.wikiSearchTerm = tenor.subject;
		
		if((tenor.property == null || tenor.property.equals("")) && !tenor.verb.equals("is")) {
			tenor.property = tenor.verb;
		}
		
		if(LieToMe.DEBUG) {
			System.out.println(tenor.taggedSentence);			
		}
		
	}
	
	public void cleanup() {		 
		finder.clearAdaptiveData();
	}
}
