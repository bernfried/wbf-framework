package de.webertise.ds.manager.targetmgr.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

// import de.webertise.ds.services.LogServices;


public class TargetManagerConfig implements TargetManagerConstants {
	
	// declare log services
	// private LogServices log = LogServices.getInstance();
	
	// instance of this singleton
    private static TargetManagerConfig instance = new TargetManagerConfig();
    
    // Hashtable which holds the ProfileManagerObjects
    private Hashtable<String,TargetManagerObject> targetManagerObjects = new Hashtable<String,TargetManagerObject>();
 
    /**
     * Constructor
     * 
     * Reads the settings and stores them in a profile manager object
     */
    private TargetManagerConfig() {
    	
    	// read the config from xml
    	readConfigFromXml();
    	
    }
 
    /**
     * getInstance
     * 
     * Provides the instance of the singleton
     */
    public static TargetManagerConfig getInstance() {
        return instance;
    }
    
    
    private void readConfigFromXml() {
    	
    	// read from xml
    	InputStream xml = null;
    	try {
			xml = TargetManagerConfig.class.getResourceAsStream(CONFIG_CONTENT_NAME);
		} catch (NullPointerException e) {
			// log.debug("TargetManagerConfig - readConfigFromXml: NullPointerException");
			e.printStackTrace();
		}
    	
		// check if config.xml could be read
		if (xml != null) {
			TargetManagerHandler tmh = new TargetManagerHandler();
			InputSource input = new InputSource(xml);
			XMLReader xmlReader = null;
			try {
				xmlReader = XMLReaderFactory.createXMLReader();
				xmlReader.setContentHandler(tmh);
				xmlReader.parse(input);
			} catch (IOException e) {
				// log.debug("TargetManagerConfig - readConfigFromXml: IOException");
				e.printStackTrace();
			} catch (SAXException e) {
				// log.debug("TargetManagerConfig - readConfigFromXml: SAXException");
				e.printStackTrace();
			}
			
			this.targetManagerObjects = tmh.getAllTMOs();
		}
    }
    
    public TargetManagerObject getConfig(String key) {
    	return targetManagerObjects.get(key);
    }
    
}