package nlpproject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class IOProcessor {
	
	String fileName;
	
	int currentLine = 0;
	int totalLines = 0;
	
	public static ArrayList<String> getAllSentences(String fileName) {
		ArrayList<String> allSentences = null;
		
		try {
			File file = new File(fileName);
			Scanner scanner = new Scanner(new FileInputStream(file));
			allSentences = new ArrayList<String>();
			while (scanner.hasNext()) {
				allSentences.add(scanner.nextLine());
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		
		return allSentences;
	}
	
	public static void writeFactCheckResultToFile(ArrayList<Tenor> tenorList, String outputFileName) {
	     int count=0;
	    try {
	    	FileWriter fileWriter = new FileWriter(outputFileName);
		    
		    for(Tenor tenor : tenorList) {
//		    	String writeToFile = "TV:" + tenor.truthValue + "Estimated :" + tenor.estimatedTruthValue + " " + tenor.sentence 
//					+ "\nSubject: " + tenor.subject + " Search String : " + tenor.wikiSearchTerm + " Search for: " + tenor.value + " Extracted string : " + tenor.wikiExtract + "\n"
//					+ "\n\nWIKI FETCH : " + tenor.wikiInfo + "\n";
//		    	
//		    	writeToFile += "========================\n";
//		    	
//		    	fileWriter.append(writeToFile);
		    	
		    	//FALSE NEGATIVES
		    	if((tenor.truthValue == 1.0 && tenor.estimatedTruthValue != 1.0)) {
//		    		String writeToFile = "TV:" + tenor.truthValue + "Estimated :" + tenor.estimatedTruthValue + " " + tenor.sentence 
//							+ "\nSubject: " + tenor.extractedSubject + " Search String : " + tenor.wikiSearchTerm + " Search for: " + tenor.value + " Extracted string : " + tenor.wikiExtract + "\n"
//							+ "\n\nWIKI FETCH : " + tenor.wikiInfo + "\nTagged sentence : " + tenor.taggedSentence + "\n";
		    		
		    		String writeToFile = tenor.truthValue + " - " + tenor.sentence + "\n";
		    		writeToFile += tenor.estimatedTruthValue + " (" + tenor.cumulativeRating + ") " + "Topic: " + tenor.wikiSearchTerm + " Verb: " + tenor.verb + " Property: " + tenor.property + "\n";
		    		writeToFile += "Look For: " + tenor.value + " WikiExtract: " + tenor.wikiExtract + "\n";
		    		writeToFile += "WIKIDATA:\n" + tenor.wikiInfo + "\n";
				    	
			    	writeToFile += "========================\n\n\n";
			    	
			    	fileWriter.append(writeToFile);
		    	}
		    	
		    	//FALSE POSITIVES
		    	if((tenor.truthValue == 0.0 && tenor.estimatedTruthValue == 1.0)) {
//		    		String writeToFile = "TV:" + tenor.truthValue + "Estimated :" + tenor.estimatedTruthValue + " " + tenor.sentence 
//							+ "\nSubject: " + tenor.extractedSubject + " Search String : " + tenor.wikiSearchTerm + " Search for: " + tenor.value + " Extracted string : " + tenor.wikiExtract + "\n"
//							+ "\n\nWIKI FETCH : " + tenor.wikiInfo + "\nTagged sentence : " + tenor.taggedSentence + "\n";
		    		
		    		String writeToFile = tenor.truthValue + " - " + tenor.sentence + "\n";
		    		writeToFile += tenor.estimatedTruthValue + " ("+ tenor.cumulativeRating + ") " + "Topic: " + tenor.wikiSearchTerm + " Verb: " + tenor.verb + " Property: " + tenor.property + "\n";
		    		writeToFile += "Look For: " + tenor.value + " WikiExtract: " + tenor.wikiExtract + "\n";
		    		writeToFile += "WIKIDATA:\n" + tenor.wikiInfo + "\n";
				    	
			    	writeToFile += "========================\n\n\n";
			    	
			    	fileWriter.append(writeToFile);
		    	}
		    	
		    	
		    	if(tenor.wikiInfo == null || tenor.wikiInfo.equals("")) {
//		    		String writeToFile = "TV:" + tenor.truthValue + "Estimated :" + tenor.estimatedTruthValue + " " + tenor.sentence 
//							+ "\nSubject: " + tenor.extractedSubject + " Search String : " + tenor.wikiSearchTerm + " Search for: " + tenor.value + " Extracted string : " + tenor.wikiExtract + "\n"
//							+ "\n\nWIKI FETCH : " + tenor.wikiInfo + "\nTagged sentence : " + tenor.taggedSentence + "\n";
//				    	
//			    	writeToFile += "========================\n";
//			    	
//			    	fileWriter.append(writeToFile);
			    	count++;
		    	}
		    }
		    
		    fileWriter.close();
	    } catch (IOException ex) {
	    	ex.printStackTrace();
	    }
	    
	}
	
	public static void writeResultsToFile(ArrayList<Tenor> tenorList, String outputFileName) {
		String factID = "<http://swc2017.aksw.org/task2/dataset/";
		String val = "<http://swc2017.aksw.org/hasTruthValue>";
		String valType = "<http://www.w3.org/2001/XMLSchema#double>";
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(outputFileName);
		    
		    for(Tenor tenor : tenorList) {
		    	String writeToFile = factID + tenor.factID + ">" + val + "\"" + tenor.estimatedTruthValue + "\"^^" + valType + " .\n";
		    	fileWriter.write(writeToFile);
		    }

			fileWriter.close();
		}catch(Exception ex) {
			
		}
	}
	
	public static NodeList readKnowledgeBaseXML(String fileName) {
		NodeList properties = null;
		File inputFile = new File(fileName);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        try {
        	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();
            
            //System.out.println("getting properties :");
            properties = doc.getElementsByTagName("property");
        } catch (Exception ex) {
        	ex.printStackTrace();
        }
        return properties;
	}
	
	public static void writeKnowledgeBaseToFile(DOMSource domSource, String fileName) {
		try {
			System.out.println("Writing To File.");
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
	        Transformer transformer = transformerFactory.newTransformer();
	        StreamResult result = new StreamResult(new File(fileName));
	        transformer.transform(domSource, result);
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
}
