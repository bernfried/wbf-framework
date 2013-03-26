package de.webertise.ds.manager.profilemgr.operations.multivalueattr;

import de.reddot.api.common.attribute.CoaAttribute;
import de.reddot.api.common.session.CoaSession;
import de.reddot.api.common.user.CoaUser;
import de.webertise.ds.manager.profilemgr.config.PMOOperation;
import de.webertise.ds.manager.profilemgr.config.ProfileManagerObject;
import de.webertise.ds.manager.profilemgr.service.PMOValidationResult;
import de.webertise.ds.services.LogServices;

public class RemoveAllItemsOperation extends AbstractOperation {

	// declare log services
	private LogServices log = LogServices.getInstance("wbtServices");

	@Override
	public StringBuffer execute(CoaSession session, ProfileManagerObject pmo, PMOOperation op, PMOValidationResult valResult) {

		log.debug("wbtServices", "RemoveAllItemsOperation - execute: reached");
		
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
			// remove all attribute values
			attr.removeAllValues();
			log.debug("wbtServices", "RemoveAllItemsOperation - execute: attribute '" + pmo.getAttributePath() + "' - removed all values.");
			sbXmlResult.append("<info code='removeAllOk'>removed all values</info>");

		} else {
			sbXmlResult.append("<info code='removeAllInfo'>attribute list was already empty</info>");
		}
		
		// close root tag
		sbXmlResult.append("</result>");
		
		return sbXmlResult;
	}

}
