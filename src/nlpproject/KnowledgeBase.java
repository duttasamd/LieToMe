package nlpproject;

import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class KnowledgeBase {
	public static final String KnowledgeBaseXML = "res/knowledge_base.xml";
	public static ConcurrentHashMap<String, ConcurrentHashMap<String, Double>> propertySynonyms;
	
	public static void init() {
		propertySynonyms = new ConcurrentHashMap<String, ConcurrentHashMap<String, Double>>();
		//System.out.println("Fetching node list..");
		NodeList nodeList = IOProcessor.readKnowledgeBaseXML(KnowledgeBaseXML);
		if(nodeList != null) {
			for(int i=0; i<nodeList.getLength(); i++) {
				Node iNode = nodeList.item(i);
				
				if (iNode.getNodeType() == Node.ELEMENT_NODE) {
					Element el = (Element) iNode;
					
					String property = el.getAttribute("propertyName");
					ConcurrentHashMap<String, Double> synMap = null;
					
					NodeList synonyms = el.getChildNodes();
					if(synonyms != null && synonyms.getLength() > 0) {
						synMap = new ConcurrentHashMap<String, Double>();
						for(int j=0; j<synonyms.getLength(); j++) {
							Node synNode = synonyms.item(j);
							
							try {
								Element synel = (Element) synNode;
								String synName = synel.getAttribute("synonymName");
								String synVal = synel.getTextContent();
								synMap.put(synName, Double.parseDouble(synVal));
							} catch(Exception ex) {
								ex.printStackTrace();
							}
						}

						propertySynonyms.put(property, synMap);
					}
				}
			}
		}
	}
	
	public static void commitUpdatedKnowledgeBase() {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();

			Document doc = dBuilder.newDocument();
			
			Element rootElement = doc.createElement("root");
			doc.appendChild(rootElement);
			
			for(String property : propertySynonyms.keySet()) {
				Element prop = doc.createElement("property");
		        rootElement.appendChild(prop);
		        
		        Attr attr = doc.createAttribute("propertyName");
		        attr.setValue(property);
		        prop.setAttributeNode(attr);
		        
		        ConcurrentHashMap<String, Double> synonyms = propertySynonyms.get(property);
		        for (String synonym : synonyms.keySet()) {
		        	Element syn = doc.createElement("synonym");
		        	prop.appendChild(syn);
			        
			        Attr synAttr = doc.createAttribute("synonymName");
			        synAttr.setValue(synonym);
			        syn.setAttributeNode(synAttr);
			        syn.appendChild(doc.createTextNode(Double.toString(synonyms.get(synonym))));
				}
			}	
			
			
			IOProcessor.writeKnowledgeBaseToFile(new DOMSource(doc), KnowledgeBaseXML);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}
}
