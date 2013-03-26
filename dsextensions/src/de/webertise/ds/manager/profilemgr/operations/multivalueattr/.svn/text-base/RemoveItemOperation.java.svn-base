package de.webertise.ds.manager.profilemgr.operations.multivalueattr;

import de.reddot.api.common.attribute.CoaAttribute;
import de.reddot.api.common.session.CoaSession;
import de.reddot.api.common.user.CoaUser;
import de.webertise.ds.manager.profilemgr.config.PMOOperation;
import de.webertise.ds.manager.profilemgr.config.ProfileManagerObject;
import de.webertise.ds.manager.profilemgr.service.PMOValidationResult;
import de.webertise.ds.services.LogServices;

public class RemoveItemOperation extends AbstractOperation {

	// declare log services
	private LogServices log = LogServices.getInstance("wbtServices");

	@Override
	public StringBuffer execute(CoaSession session, ProfileManagerObject pmo, PMOOperation op, PMOValidationResult valResult) {

		log.debug("wbtServices", "RemoveItemOperation - execute: reached");
		
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
			id = "id" + pmo.getKeyValueSeparator() + id;

			// get attribute values
			String[] attrValues = attr.getValues();
			log.debug("wbtServices", "RemoveItemOperation - execute: attribute '" + pmo.getAttributePath() + "' has " + attrValues.length + " values.");
			for (int i=0; i<attrValues.length; i++) {
				
				// get a single value
				String attrValue = attrValues[i];
				log.debug("wbtServices", "RemoveItemOperation - execute: value[" + i + "] = '" + attrValue + "'");
				
				// compare
				if (attrValue.startsWith(id)) {
					attr.removeValue(attrValue);
					sbXmlResult.append("<info code='deleted'>attribute with id '" + id + "' deleted</info>");
					break;
				}
			
			}
		} else {
			sbXmlResult.append("<info code='empty'>empty</info>");
		}
		
		// close root tag
		sbXmlResult.append("</result>");
		
		return sbXmlResult;
	}

}
