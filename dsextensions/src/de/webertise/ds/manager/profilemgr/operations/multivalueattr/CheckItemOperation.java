package de.webertise.ds.manager.profilemgr.operations.multivalueattr;

import de.reddot.api.common.attribute.CoaAttribute;
import de.reddot.api.common.session.CoaSession;
import de.reddot.api.common.user.CoaUser;
import de.webertise.ds.manager.profilemgr.config.PMOOperation;
import de.webertise.ds.manager.profilemgr.config.ProfileManagerObject;
import de.webertise.ds.manager.profilemgr.service.PMOValidationResult;
import de.webertise.ds.services.LogServices;

public class CheckItemOperation extends AbstractOperation {

	// declare log services
	private LogServices log = LogServices.getInstance("wbtServices");

	@Override
	public StringBuffer execute(CoaSession session, ProfileManagerObject pmo, PMOOperation op, PMOValidationResult valResult) {

		log.debug("wbtServices", "CheckItemOperation - execute: reached");
		
		// declaration
		StringBuffer sbXmlResult = new StringBuffer();
		
		// create root tag 
		sbXmlResult.append("<result>");
		
		// get session user
		CoaUser user = session.getCoaUser();
		
		// get user attribute
		CoaAttribute attr = user.getCoaAttribute(pmo.getAttributePath());
		
		// check if attribute exists already and has values
		if (attr != null && attr.hasValues()) {
			
			// build unique id key/value String
			String id = valResult.getValResultItem("id").getValidatedValue();
			String idKV = "id" + pmo.getKeyValueSeparator() + id;

			// get attribute values
			String[] attrValues = attr.getValues();
			log.debug("wbtServices", "CheckItemOperation - execute: attribute '" + pmo.getAttributePath() + "' has " + attrValues.length + " values.");
			boolean exists = false;
			for (int i=0; i<attrValues.length; i++) {
				
				// get a single value
				String attrValue = attrValues[i];
				log.debug("wbtServices", "CheckItemOperation - execute: value[" + i + "] = '" + attrValue + "'");
				
				// compare
				if (attrValue.startsWith(idKV)) {
					sbXmlResult.append("<position>" + (i+1) + "</position>");
					exists = true;
					break;
				}
			
			}
			
			if (!exists) {
				sbXmlResult.append("<exists>false</exists>");
			} else {
				sbXmlResult.append("<exists>true</exists>");
			}
			
		} else {
			sbXmlResult.append("<info code='empty'>attribute list is empty</info>");
			sbXmlResult.append("<exists>false</exists>");
		}
		
		// close root tag
		sbXmlResult.append("</result>");
		
		return sbXmlResult;
	}

}
