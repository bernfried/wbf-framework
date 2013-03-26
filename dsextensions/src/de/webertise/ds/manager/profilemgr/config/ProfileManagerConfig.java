package de.webertise.ds.manager.profilemgr.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

// import de.webertise.ds.services.LogServices;


public class ProfileManagerConfig implements ProfileManagerConstants {
	
	// declare log services
	// private LogServices log = LogServices.getInstance();   
	
	// instance of this singleton
    private static ProfileManagerConfig instance = new ProfileManagerConfig();
    
    // Hashtable which holds the ProfileManagerObjects
    private Hashtable<String,ProfileManagerObject> profileManagerObjects = new Hashtable<String,ProfileManagerObject>();
 
    /**
     * Constructor
     * 
     * Reads the settings and stores them in a profile manager object
     */
    private ProfileManagerConfig() {
    	
    	// read the config from xml
    	readConfigFromXml();
    	
    }
 
    /**
     * getInstance
     * 
     * Provides the instance of the singleton
     */
    public static ProfileManagerConfig getInstance() {
        return instance;
    }
    
    
    private void readConfigFromXml() {
    	
    	// read from xml
    	InputStream xml = null;
    	try {
			xml = ProfileManagerConfig.class.getResourceAsStream(CONFIG_CONTENT_NAME);
		} catch (NullPointerException e) {
			// log.debug("ProfileManagerConfig - readConfigFromXml: NullPointerException");
			e.printStackTrace();
		}
    	
		// check if config.xml could be read
		if (xml != null) {
			ProfileManagerHandler pmh = new ProfileManagerHandler();
			InputSource input = new InputSource(xml);
			XMLReader xmlReader = null;
			try {
				xmlReader = XMLReaderFactory.createXMLReader();
				xmlReader.setContentHandler(pmh);
				xmlReader.parse(input);
			} catch (IOException e) {
				// log.debug("ProfileManagerConfig - readConfigFromXml: IOException");
				e.printStackTrace();
			} catch (SAXException e) {
				// log.debug("ProfileManagerConfig - readConfigFromXml: SAXException");
				e.printStackTrace();
			}
			
			this.profileManagerObjects = pmh.getAllPMOs();
		}
    }
    
    public ProfileManagerObject getConfig(String key) {
    	return profileManagerObjects.get(key);
    }
    
}