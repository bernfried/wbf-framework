package de.webertise.ds.manager.profilemgr.config;

import java.util.Hashtable;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;


public class ProfileManagerHandler implements ContentHandler, ProfileManagerConstants {

	private Hashtable<String,ProfileManagerObject> allPMOs = new Hashtable<String,ProfileManagerObject>();
	private Hashtable<String,PMOOperation> allOperations;
	private Hashtable<String,PMOValidation> allValidations;
	private Hashtable<String,PMOInputParameter> allInputParams;
	private Hashtable<String,PMOOutputParameter> allOutputParams;
	private String currentValue;
	private ProfileManagerObject pmo;
	private PMOOperation pmoOperation;
	private PMOValidation pmoValidation;
	private PMOInputParameter pmoInputParam;
	private PMOOutputParameter pmoOutputParam;
	private int curParamType = 0;

	// read all characters of an element
	public void characters(char[] ch, int start, int length) throws SAXException {
		currentValue = new String(ch, start, length);
	}

	// is called for each start tag
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		
		// System.out.println("StartElement: localName: '" + localName + "' / atts(name): '" + atts.getValue("name") + "'");

		// <pm-object> element
		if (localName.equals("pm-object")) {
			// create new pmo object
			pmo = new ProfileManagerObject();
			// set name based on pm-object name attribute
			pmo.setName(atts.getValue(CONFIG_PM_OBJECT_ATTRIBUTE_NAME));
		}
		
		// <operations> element
		if (localName.equals("operations")) {
			allOperations = new Hashtable<String,PMOOperation>();
		}
			
		// <operation> element
		if (localName.equals("operation")) {
			// create new pmoOperation object
			pmoOperation = new PMOOperation();
			// set name based on <operation> name attribute
			pmoOperation.setName(atts.getValue(CONFIG_OPERATION_ATTRIBUTE_NAME));
			// set name based on <operation> name attribute
			pmoOperation.setType(atts.getValue(CONFIG_OPERATION_ATTRIBUTE_TYPE));
		}

		// <input-parameters> element
		if (localName.equals("input-parameters")) {
			allInputParams = new Hashtable<String,PMOInputParameter>();
		}

		// <input-parameter> element
		if (localName.equals("input-parameter")) {
			// create new pmoInputParameter object
			pmoInputParam = new PMOInputParameter();
			// set name based on <input-parameter> name attribute
			pmoInputParam.setName(atts.getValue("name"));
			// set persistent based on <input-parameter> persist attribute
			if (atts.getValue(CONFIG_INPUT_PARAMETER_ATTRIBUTE_PERSIST)!=null && 
					atts.getValue(CONFIG_INPUT_PARAMETER_ATTRIBUTE_PERSIST).equals("true")) {
				pmoInputParam.setPersistent(true);
			} else {
				pmoInputParam.setPersistent(false);
			}
			// set input parameter type
			if (atts.getValue(CONFIG_INPUT_PARAMETER_ATTRIBUTE_TYPE)!=null) {
				String value = atts.getValue(CONFIG_INPUT_PARAMETER_ATTRIBUTE_TYPE);
				if (value.equals("number")) {
					pmoInputParam.setType(INPUT_PARAMETER_TYPE_NUMBER);				
				} else if (value.equals("boolean")) {
					pmoInputParam.setType(INPUT_PARAMETER_TYPE_BOOLEAN);				
				} else if (value.equals("date")) {
					pmoInputParam.setType(INPUT_PARAMETER_TYPE_DATE);				
				} else {
					pmoInputParam.setType(INPUT_PARAMETER_TYPE_STRING);
				}
			} else {
				pmoInputParam.setType(INPUT_PARAMETER_TYPE_STRING);
			}
			// init optional fields
			pmoInputParam.setMandatory(false);
			pmoInputParam.setHtmlEncoding("");
			pmoInputParam.setUrlEncoding("");
			pmoInputParam.setStandardValue("");
			// set flag, which type of parameter is currently read
			curParamType = PARAMETER_TYPE_INPUT;
		}
				
		// <output-parameters> element
		if (localName.equals("output-parameters")) {
			allOutputParams = new Hashtable<String,PMOOutputParameter>();
		}
			
		// <output-parameter> element
		if (localName.equals("output-parameter")) {
			// create new pmoOutputParameter object
			pmoOutputParam = new PMOOutputParameter();
			// set name based on <output-parameter> name attribute
			pmoOutputParam.setName(atts.getValue("name"));
			// set property based on <output-parameter> element-name attribute
			pmoOutputParam.setProperty(atts.getValue("property"));
			// set flag, which type of parameter is currently read
			curParamType = PARAMETER_TYPE_OUTPUT;
		}

		// <validations> element
		if (localName.equals("validations")) {
			allValidations = new Hashtable<String,PMOValidation>();
		}
			
		// <validation> element
		if (localName.equals("validation")) {
			// create new pmoOperation object
			pmoValidation = new PMOValidation();
			// set name based on <operation> name attribute
			pmoValidation.setName(atts.getValue("name"));
		}
	
	}

	// is called for each end tag
	public void endElement(String uri, String localName, String qName) throws SAXException {
		
		// eliminate spaces
		currentValue = currentValue.trim();
		// System.out.println("End-Element: localName: '" + localName + "' / currentValue: '" + currentValue + "'");

		// **************************************************
		// * child elements of <pm-object>
		// **************************************************
		// set attributePath property
		if (localName.equals("attribute-path")) {
			pmo.setAttributePath(currentValue);
		}
		// set attributeSeparator property
		if (localName.equals("attribute-separator")) {
			pmo.setAttributeSeparator(currentValue);
		}
		// set separatorString property
		if (localName.equals("key-value-separator")) {
			pmo.setKeyValueSeparator(currentValue);
		}

		// **************************************************
		// * child elements of <operation>
		// **************************************************
		// set operation classpath property
		if (localName.equals("classpath")) {
			pmoOperation.setOperationClasspath(currentValue);
		}
		// set operation target property
		if (localName.equals("target")) {
			pmoOperation.setTarget(currentValue);
		}

		// *************************************************************
		// * child elements of <input-parameter> and <output-parameter>
		// *************************************************************
		// set standardValue property
		if (localName.equals("standard-value")) {
			pmoInputParam.setStandardValue(currentValue);
		}
		// set mandatory property
		if (localName.equals("mandatory")) { 
			if (currentValue!=null && currentValue.equals("true")) {
				pmoInputParam.setMandatory(true);
			} else {
				pmoInputParam.setMandatory(false);
			}
		}
		// set htmlEncoding property
		if (localName.equals("html-encoding")) {
			if (currentValue!=null && (currentValue.equals("encode") || currentValue.equals("decode"))) {
				pmoInputParam.setHtmlEncoding(currentValue);
			} else {
				pmoInputParam.setHtmlEncoding("");
			}
		}

		// set urlEncoding property
		if (localName.equals("url-encoding")) {
			if (curParamType == PARAMETER_TYPE_INPUT) {
				if (currentValue!=null && (currentValue.equals("encode") || currentValue.equals("decode"))) {
					pmoInputParam.setUrlEncoding(currentValue);
				} else {
					pmoInputParam.setUrlEncoding("");
				}
			} else if (curParamType == PARAMETER_TYPE_OUTPUT) {
				if (currentValue!=null && (currentValue.equals("encode") || currentValue.equals("decode"))) {
					pmoOutputParam.setUrlEncoding(currentValue);
				} else {
					pmoOutputParam.setUrlEncoding("");
				}
			}
		}
		// set asCData property
		if (localName.equals("as-cdata")) {
			if (currentValue!=null && currentValue.equals("true")) {
				pmoOutputParam.setAsCData(true);
			} else {
				pmoOutputParam.setAsCData(false);
			}
		}
		// set inputFormat property
		if (localName.equals("input-format")) {
			pmoInputParam.setInputFormat(currentValue);
		}
		// set persistFormat property
		if (localName.equals("persist-format")) {
			pmoInputParam.setPersistFormat(currentValue);
		}
				
		// **************************************************
		// * child elements of <validation>
		// **************************************************
		// set minLength property
		if (localName.equals("min-length")) {
			int minLength = 0;
			try {
				minLength = Integer.parseInt(currentValue);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
			pmoValidation.setMinLength(minLength);
		}
		// set maxLength property
		if (localName.equals("max-length")) {
			int maxLength = 0;
			try {
				maxLength = Integer.parseInt(currentValue);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
			pmoValidation.setMaxLength(maxLength);
		}
		// set regEx property
		if (localName.equals("reg-expr")) {
			pmoValidation.setRegEx(currentValue);
		}

		// **************************************************
		// * store objects to hashtables
		// **************************************************
		
		// store input parameter object in hashtable
		if (localName.equals("input-parameter")) {
			allInputParams.put(pmoInputParam.getName(), pmoInputParam);
		}		
		
		// store output parameter object in hashtable
		if (localName.equals("output-parameter")) {
			allOutputParams.put(pmoOutputParam.getName(), pmoOutputParam);
		}		

		// store input parameters hashtable to operation hashtable
		if (localName.equals("input-parameters")) {
			pmoOperation.setPmoInputParameter(allInputParams);
		}		
		
		// store output parameters hashtable to operation hashtable
		if (localName.equals("output-parameters")) {
			pmoOperation.setPmoOutputParameter(allOutputParams);
		}		

		// add operation to hashtable
		if (localName.equals("operation")) {
			allOperations.put(pmoOperation.getName(), pmoOperation);
		}
		
		// add operations to hashtable
		if (localName.equals("operations")) {
			pmo.setPmoOperations(allOperations);
		}
		
		// add validation to hashtable
		if (localName.equals("validation")) {
			allValidations.put(pmoValidation.getName(), pmoValidation);
		}
		
		// add operations to hashtable
		if (localName.equals("validations")) {
			pmo.setPmoValidations(allValidations);
		}
		
		// store pm-object object in hashtable
		if (localName.equals("pm-object")) {
			allPMOs.put(pmo.getName(), pmo);
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
	
	public Hashtable<String,ProfileManagerObject> getAllPMOs() {
		return this.allPMOs;
	}
	
	
}
