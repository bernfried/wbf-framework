/** 
 * 
 * 
 * @author Bernfried Howe, bernfried.howe@webertise.de
 * @version 1.0
 */
package de.webertise.ds.events;

import java.util.Locale;
import java.util.Map;

import de.reddot.api.common.content.CoaContent;
import de.reddot.api.common.session.CoaSession;
import de.reddot.idea.apiutil.common.session.CoaIoletSession;
import de.webertise.ds.services.ContentServices;
import de.webertise.ds.services.LogServices;

/**
 * The content will be read and changed as follows:
 * 
 * @author Bernfried Howe
 * @version 1.0
 */
public class OnUpdateContentEvent implements EventConstants {

	// declare log services
	private LogServices log = LogServices.getInstance("wbtServices");

	/**
	 * Update content on event onContentUpdate e.g. when content is published form MS to DS.
	 * 
	 * @param paramMap		Parameter Map of events
	 * 
	 */
	@SuppressWarnings({ "rawtypes" })
	public void doUpdate(Map paramMap) {

		if (paramMap != null && !paramMap.isEmpty() && paramMap.containsKey("coaContent") && paramMap.get("coaContent") != null) {

			if (paramMap.containsKey("session") && paramMap.get("session") != null) {
				
				// read session
				CoaSession session = (CoaIoletSession) paramMap.get("session");
				
				// get content object
				CoaContent coaContent = (CoaContent) paramMap.get("coaContent");
				
				if (coaContent != null) {
					
					// add other event tasks here
					
					
					// check, if content update is allowed
					String updateFlag = coaContent.getAttributeValue(CONTENT_ATTRIBUTE_NAME_UPDATE_FLAG, Locale.GERMAN);
					
					// Only content which has updateFlag = true will be considered. This must be set in the Import DM 
					// in Management Server and avoids that the update done here will not cause an endless loop.
					if (updateFlag != null && updateFlag.equals("true")) {
						
						// add content updates here
						
						
						// important: this flag needs to be set to false in order to avoid an endless loop
						coaContent.setAttribute(OnUpdateContentEvent.CONTENT_ATTRIBUTE_NAME_UPDATE_FLAG, Locale.GERMAN, "false");
		
						// update content
						ContentServices ctntService = new ContentServices();
						int retCode = ctntService.updateContent(session, coaContent);
						log.debug("wbtServices", "OnUpdateContent - doUpdate: return code of doUpdateContent is '" + retCode + "'.");
						
					}
											
				} else {
					log.debug("wbtServices", "OnUpdateContent error: coaContent is null");
				}				
			} else {
				log.debug("wbtServices", "OnUpdateContent error: could not load coaContentData or is null");
			}
		} else {
			log.debug("wbtServices", "OnUpdateContent error: could not get parameterMap or is null");
		}
	}

}
