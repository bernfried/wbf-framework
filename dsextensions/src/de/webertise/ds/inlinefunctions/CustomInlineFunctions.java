/** 
 * Custom inline functions for Delivery Server
 * 
 * @author Bernfried Howe, bernfried.howe@webertise.de
 * @version 1.0
 */
package de.webertise.ds.inlinefunctions;

import de.webertise.ds.services.LogServices;

public class CustomInlineFunctions {
	
	// declare log services
	private LogServices log = LogServices.getInstance("wbtServices");

		
	public String doSample(String value) {
		
		log.debug("wbtServices", "WebertiseInlineFunctions - doSample: got value '" + value + "'");
		
		// do something
		
		
		return value;
	}

}
