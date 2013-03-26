package de.webertise.ds.manager.targetmgr.config;

import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;


public class TargetManagerHandler implements ContentHandler, TargetManagerConstants {

	private Hashtable<String,TargetManagerObject> allTargets = new Hashtable<String,TargetManagerObject>();
	private Map<String,TMOBracket> allBrackets;
	private String currentValue;
	private TargetManagerObject tmoTarget;
	private TMOBracket tmoBracket;

	// read all characters of an element
	public void characters(char[] ch, int start, int length) throws SAXException {
		currentValue = new String(ch, start, length);
	}

	// is called for each start tag
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		
		// <target> element
		if (localName.equals("target")) {
			// create new tmo object
			tmoTarget = new TargetManagerObject();
			// set name based on pm-object name attribute
			tmoTarget.setName(atts.getValue(CONFIG_TMO_ATTRIBUTE_NAME));
			// log
			// System.out.println("StartElement: localName: '" + localName + "' / atts(name/id): '" + atts.getValue("name") + "'");
		}
		
		// <target> element
		if (localName.equals("constraint")) {
			// create new tmo bracket list
			allBrackets = new LinkedHashMap<String,TMOBracket>();
		}
		
		// <bracket> element
		if (localName.equals("bracket")) {
			// check if previous bracket object was empty
			if (tmoBracket!=null && atts.getValue("parent").equals(tmoBracket.getId())) {
				allBrackets.put(tmoBracket.getKey(), tmoBracket);
			}
			// create new bracket object
			tmoBracket = new TMOBracket();
			// set logical operator
			tmoBracket.setLogicalOperatorBracket(atts.getValue("log-op"));
			// set id
			tmoBracket.setId(atts.getValue("id"));
			// set parent
			tmoBracket.setParent(atts.getValue("parent"));
			// set key
			tmoBracket.setKey(atts.getValue("parent") + "-" + atts.getValue("id"));
			// log
			// System.out.println("StartElement: localName: '" + localName + "' / atts(id): '" + atts.getValue("parent") + "-"+ atts.getValue("id") + "'");
		}
			
		// <part> element
		if (localName.equals("part")) {
			// set content attribute name 
			tmoBracket.setAttributeName(atts.getValue("attr"));
			// set input parameter name
			tmoBracket.setInputParameterName(atts.getValue("iparam"));
			// set type (multi / single)
			tmoBracket.setType(atts.getValue("type"));
			// set separator
			tmoBracket.setValueSeparator(atts.getValue("separator"));
			// set compare operator
			tmoBracket.setCompareOperator(atts.getValue("comp-op"));
			// set logical operator
			tmoBracket.setLogicalOperatorValue(atts.getValue("log-op"));
			// set variableType
			tmoBracket.setVariableType(atts.getValue("var-type"));
		}
	
	}

	// is called for each end tag
	public void endElement(String uri, String localName, String qName) throws SAXException {
		
		// eliminate spaces
		currentValue = currentValue.trim();
		// System.out.println("End-Element: localName: '" + localName + "' / currentValue: '" + currentValue + "'");

		// **************************************************
		// * store objects to hashtables
		// **************************************************
		
		// store bracket object in map
		if (localName.equals("bracket")) {
			allBrackets.put(tmoBracket.getKey(), tmoBracket);
		}		
		
		// store pm-object object in hashtable
		if (localName.equals("target")) {
			tmoTarget.setBrackets(allBrackets);
			allTargets.put(tmoTarget.getName(), tmoTarget);
		}
		
	}

	public void endDocument() throws SAXException {
	}

	public void endPrefixMapping(String prefix) throws SAXException {
	}

	public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
	}

	public void processingInstruction(String target, String data) throws SAXException {
	}

	public void setDocumentLocator(Locator locator) {
	}

	public void skippedEntity(String name) throws SAXException {
	}

	public void startDocument() throws SAXException {
	}

	public void startPrefixMapping(String prefix, String uri) throws SAXException {
	}
	
	public Hashtable<String,TargetManagerObject> getAllTMOs() {
		return this.allTargets;
	}
	
	
}
