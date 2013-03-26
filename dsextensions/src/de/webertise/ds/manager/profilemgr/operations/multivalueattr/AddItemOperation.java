package de.webertise.ds.manager.profilemgr.operations.multivalueattr;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;

import de.reddot.api.common.attribute.CoaAttribute;
import de.reddot.api.common.session.CoaSession;
import de.reddot.api.common.user.CoaUser;
import de.webertise.ds.manager.profilemgr.config.PMOOperation;
import de.webertise.ds.manager.profilemgr.config.ProfileManagerObject;
import de.webertise.ds.manager.profilemgr.service.AttributeServices;
import de.webertise.ds.manager.profilemgr.service.PMOValidationResult;
import de.webertise.ds.manager.profilemgr.service.PMOValidationResultItem;
import de.webertise.ds.services.LogServices;

public class AddItemOperation extends AbstractOperation {

	// declare log services
	private LogServices log = LogServices.getInstance("wbtServices");

	@Override
	public StringBuffer execute(CoaSession session, ProfileManagerObject pmo, PMOOperation op, PMOValidationResult valResult) {

		log.debug("wbtServices","AddItemOperation - execute: reached");
		
		// declaration
		StringBuffer sbXmlResult = new StringBuffer();
		
		// create root tag 
		sbXmlResult.append("<result>");
		
		// create new item
		Hashtable<String,String> newItem = new Hashtable<String,String>();
		Hashtable<String,PMOValidationResultItem> valResultItems = valResult.getValResultItems(true);
		Enumeration<String> keys = valResultItems.keys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			newItem.put(key, valResultItems.get(key).getValidatedValue());
			log.debug("wbtServices", "AddItemOperation - execute: New Item key/value: " + key + "/" + valResultItems.get(key).getValidatedValue());
		}
		// add system date to new item
		SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
		Date dt = new Date();
		newItem.put("createdAt", df.format(dt));

		// get session user
		CoaUser user = session.getCoaUser();
		log.debug("wbtServices", "AddItemOperation - execute: User is '" + user.getLogin() + "'");
		
		// get user attribute for values and for keys
		CoaAttribute attr = user.getCoaAttribute(pmo.getAttributePath());
		if (attr!=null) log.debug("wbtServices", "AddItemOperation - execute: " + attr.getValues().length + " attribute values found: " + attr.toString());

		// check if new item exists already in attribute list (by id)
		PMOValidationResultItem newItemId = valResult.getValResultItem("id");
		if (AttributeServices.checkExistenceById(pmo, attr, newItemId)) {
			// add new item
			sbXmlResult.append("<info code='existsTrue'>new attribute could not be added because id exists already</info>");
		} else {
			// check position to add
			PMOValidationResultItem addToTop = valResult.getValResultItem("addToTop");
			boolean addToTopFlag = false;
			if (addToTop.getValidatedValue().equals("true")) {
				addToTopFlag = true;
			}

			// check if attribute exists already and has values
			if (attr != null && attr.hasValues()) {
				// get validate input value for limit and convert to int
				PMOValidationResultItem limit = valResult.getValResultItem("limit");
				int iLimit = Integer.parseInt(limit.getValidatedValue());

				if (iLimit != 0 && iLimit <= attr.getValues().length) {
					// set result info, that item could not be added because limit has been reached
					sbXmlResult.append("<info code='limitTrue'>attribute limit '" + iLimit + "' reached</info>");
					// set result info: limit reached
					PMOValidationResultItem removeLast = valResult.getValResultItem("removeLast");
					if (removeLast.getValidatedValue().equals("true")) {
						// add new item and remove last
						AttributeServices.addAttributeValue(attr, pmo, newItem, iLimit, addToTopFlag);
						// set result info, that item could not be added because limit has been reached
						sbXmlResult.append("<info code='removeLastTrue'>new attribute has been added and last item has been removed</info>");
						if (addToTopFlag) {
							// set result info, that item could not be added because limit has been reached
							sbXmlResult.append("<info code='addToTopTrue'>new attribute has been added to the top of the list</info>");
						} else {
							// set result info, that item could not be added because limit has been reached
							sbXmlResult.append("<info code='addToTopFalse'>new attribute has been added to the bottom of the list</info>");
						}
					} else {
						// add new item
						sbXmlResult.append("<info code='removeLastFalse'>new attribute could not be added because of limit</info>");
					}
				} else {
					// add new item without limit consideration as not reached yet
					AttributeServices.addAttributeValue(attr, pmo, newItem, iLimit, addToTopFlag);
					// set result info, that item could not be added because limit has been reached
					sbXmlResult.append("<info code='limitFalse'>attribute limit '" + iLimit + "' not reached</info>");
				}
					
			} else {  
				// attr has no values => create new first item
				AttributeServices.createAttributeValue(user, pmo, newItem);
				// set result info
				sbXmlResult.append("<info code='create'>new attribute value has been created</info>");
			}
		}
		
		// close root tag
		sbXmlResult.append("</result>");
		
		// log all attribute values after add operation
		AttributeServices.logCurrentAttributeValues(attr);
		
		return sbXmlResult;
	}

}
