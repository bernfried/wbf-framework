package de.webertise.ds.manager.profilemgr.operations.multivalueattr;

import de.reddot.api.common.attribute.CoaAttribute;
import de.reddot.api.common.session.CoaSession;
import de.reddot.api.common.user.CoaUser;
import de.webertise.ds.manager.profilemgr.config.PMOOperation;
import de.webertise.ds.manager.profilemgr.config.ProfileManagerObject;
import de.webertise.ds.manager.profilemgr.service.PMOValidationResult;
import de.webertise.ds.services.LogServices;

public class MoveItemOperation extends AbstractOperation {

	// declare log services
	private LogServices log = LogServices.getInstance("wbtServices");

	@Override
	public StringBuffer execute(CoaSession session, ProfileManagerObject pmo, PMOOperation op, PMOValidationResult valResult) {

		log.debug("wbtServices", "MoveItemOperation - execute: reached");
		
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
			log.debug("wbtServices", "MoveItemOperation - execute: attribute '" + pmo.getAttributePath() + "' has " + attrValues.length + " values.");
			
			// declare variables for move
			int iPos = 0;
			for (int i=0; i<attrValues.length; i++) {
				// get a single value
				String attrValue = attrValues[i];
				log.debug("wbtServices", "MoveItemOperation - execute: value[" + i + "] = '" + attrValue + "'");
				// compare 
				if (attrValue.startsWith(id)) {
					iPos = i;
					break;
				}
			}
			
			// get position type 
			String[] chgAttrValues = new String[attrValues.length];
			String position = valResult.getValResultItem("position").getValidatedValue();
			if (position.equals("top")) {
				// build new array
				if (iPos > 0) {
					chgAttrValues = changeAttrValueOrder(attrValues, iPos, 0, true);
					sbXmlResult.append("<info code='moveOk'>moved from pos '" + (iPos+1) + "' to '" + 1 + "'</info>");
				} else {
					sbXmlResult.append("<info code='moveFailed'>move from pos '" + (iPos+1) + "' to '" + 1 + "' failed.</info>");
				}
			}
			if (position.equals("bottom")) {
				// build new array
				if (iPos < attrValues.length-1) {
					chgAttrValues = changeAttrValueOrder(attrValues, iPos, attrValues.length-1, false);
					sbXmlResult.append("<info code='moveOk'>moved from pos '" + (iPos+1) + "' to '" + attrValues.length + "'</info>");
				} else {
					sbXmlResult.append("<info code='moveFailed'>move from pos '" + (iPos+1) + "' to '" + attrValues.length + "' failed.</info>");
				}
			}
			if (position.equals("up")) {
				// build new array
				if (iPos > 0) {
					chgAttrValues = changeAttrValueOrder(attrValues, iPos, iPos-1, true);
					sbXmlResult.append("<info code='moveOk'>moved from pos '" + (iPos+1) + "' to '" + iPos + "'</info>");
				} else {
					sbXmlResult.append("<info code='moveFailed'>move from pos '" + (iPos+1) + "' to '" + iPos + "' failed.</info>");
				}
			}
			if (position.equals("down")) {
				// build new array
				if (iPos < attrValues.length-1) {
					chgAttrValues = changeAttrValueOrder(attrValues, iPos, iPos+1, false);
					sbXmlResult.append("<info code='moveOk'>moved from pos '" + (iPos+1) + "' to '" + (iPos+2) + "'</info>");
				} else {
					sbXmlResult.append("<info code='moveFailed'>move from pos '" + (iPos+1) + "' to '" + attrValues.length + "' failed.</info>");
				}
			}
			
			// store changed order
			attr.removeAllValues();
			attr.setValues(chgAttrValues);
			
		} else {
			sbXmlResult.append("<info code='moveFailed'>attribute list is empty</info>");
		}
		
		// close root tag
		sbXmlResult.append("</result>");
		
		return sbXmlResult;
	}

	/**
	 * @param attrValues
	 * @param curPos current position of item to move
	 * @param newPos new position for item
	 * @param moveUp flag if item will be moved up or down
	 */
	private static String[] changeAttrValueOrder(String[] attrValues, int curPos, int newPos, boolean moveUp) {
		String[] chgAttrValues = new String[attrValues.length];
		// System.out.println("#### curPos: " + curPos + " / newPos: " + newPos);
		int j=0;
		for (int i=0; i<attrValues.length; i++) {
			if (i!=curPos && i!=newPos) {
				chgAttrValues[j++] = attrValues[i];
			} else if (i==newPos && moveUp) {
				chgAttrValues[j++] = attrValues[curPos];
				chgAttrValues[j++] = attrValues[newPos];
			} else if (i==newPos && !moveUp) {
				chgAttrValues[j++] = attrValues[newPos];
				chgAttrValues[j++] = attrValues[curPos];
			} 
		}
		return chgAttrValues;
	}

}
