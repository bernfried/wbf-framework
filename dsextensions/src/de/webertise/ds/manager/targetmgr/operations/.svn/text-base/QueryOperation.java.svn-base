/** 
 * 
 * 
 * @author Bernfried Howe, bernfried.howe@webertise.de
 * @version 1.0
 */
package de.webertise.ds.manager.targetmgr.operations;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import de.reddot.api.common.session.CoaSession;
import de.webertise.ds.manager.profilemgr.config.PMOOperation;
import de.webertise.ds.manager.profilemgr.config.ProfileManagerObject;
import de.webertise.ds.manager.profilemgr.service.PMOValidationResult;
import de.webertise.ds.manager.profilemgr.service.PMOValidationResultItem;
import de.webertise.ds.manager.targetmgr.config.TMOBracket;
import de.webertise.ds.manager.targetmgr.config.TargetManagerObject;
import de.webertise.ds.services.DynaMentServices;
import de.webertise.ds.services.LogServices;

public class QueryOperation extends AbstractOperation {

	// declare log services
	private LogServices log = LogServices.getInstance("wbtServices");

	@Override
	public StringBuffer execute(CoaSession session, ProfileManagerObject pmo, TargetManagerObject tmo, PMOOperation op, PMOValidationResult valResult) {

		log.debug("wbtServices", "QueryOperation - execute: reached");
		
		// declaration
		StringBuffer sbXmlResult = new StringBuffer();
		
		// create root tag 
		sbXmlResult.append("<result>");
		
		// create query string
		String constraint = createConstraintString(valResult, tmo);
		log.debug("wbtServices", "QueryOperation - execute- Query: " + constraint);
		sbXmlResult.append("<constraint><![CDATA[" + constraint + "]]></constraint>");

		// create parameters for target DM 
		String parameters = createParameterString(valResult);
		log.debug("wbtServices", "QueryOperation - execute- Parameters: " + parameters);
		sbXmlResult.append("<parameters><![CDATA[" + parameters + "]]></parameters>");
		
		// execute target DM
		String targetDMResult = DynaMentServices.executeTargetDynaMent(session, parameters, constraint);
		log.debug("wbtServices", "QueryOperation - execute- Target DM Result: " + targetDMResult);
		
		// append target dm result
		sbXmlResult.append(targetDMResult);
		
		// close root tag
		sbXmlResult.append("</result>");
		
		return sbXmlResult;
	}

	
	private String createParameterString(PMOValidationResult valResult) {

		StringBuffer parameters = new StringBuffer();
		
		if (valResult.getValResultItem("project") != null && !valResult.getValResultItem("project").getValidatedValue().equals("")) {
			parameters.append("project='" + valResult.getValResultItem("project").getValidatedValue() + "'");
		} else {
			log.debug("wbtServices", "QueryOperation - createParameterString - project parameter is missing!");
		}
		if (valResult.getValResultItem("group") != null && !valResult.getValResultItem("group").getValidatedValue().equals("")) {
			parameters.append(" group='" + valResult.getValResultItem("group").getValidatedValue() + "'");
		} else {
			log.debug("wbtServices", "QueryOperation - createParameterString - group parameter is missing!");
		}
		if (valResult.getValResultItem("locale") != null && !valResult.getValResultItem("locale").getValidatedValue().equals("")) {
			parameters.append(" locale='" + valResult.getValResultItem("locale").getValidatedValue() + "'");
		} else {
			log.debug("wbtServices", "QueryOperation - createParameterString - locale parameter is missing!");
		}
		if (valResult.getValResultItem("chunksize") != null && !valResult.getValResultItem("chunksize").getValidatedValue().equals("")) {
			parameters.append(" chunksize='" + valResult.getValResultItem("chunksize").getValidatedValue() + "'");
		} else {
			parameters.append(" chunksize='10'");
		}
		if (valResult.getValResultItem("chunk") != null && !valResult.getValResultItem("chunk").getValidatedValue().equals("")) {
			parameters.append(" chunk='" + valResult.getValResultItem("chunk").getValidatedValue() + "'");
		} else {
			parameters.append(" chunk='1'");
		}
		if (valResult.getValResultItem("maxhits") != null && !valResult.getValResultItem("maxhits").getValidatedValue().equals("")) {
			parameters.append(" maxhits='" + valResult.getValResultItem("maxhits").getValidatedValue() + "'");
		} else {
			parameters.append(" maxhits='500'");
		}
		if (valResult.getValResultItem("sortedby") != null && !valResult.getValResultItem("sortedby").getValidatedValue().equals("")) {
			parameters.append(" sortedby='" + valResult.getValResultItem("sortedby").getValidatedValue() + "'");
		} else {
			log.debug("wbtServices", "QueryOperation - createParameterString - sortby parameter is missing!");
		}
		if (valResult.getValResultItem("sortorder") != null && !valResult.getValResultItem("sortorder").getValidatedValue().equals("")) {
			parameters.append(" sortorder='" + valResult.getValResultItem("sortorder").getValidatedValue() + "'");
		} else {
			parameters.append(" sortorder='asc'");
		}
		if (valResult.getValResultItem("db-structure") != null && !valResult.getValResultItem("db-structure").getValidatedValue().equals("")) {
			parameters.append(" db-structure='" + valResult.getValResultItem("db-structure").getValidatedValue() + "'");
		}
		if (valResult.getValResultItem("attributepath") != null && !valResult.getValResultItem("attributepath").equals("")) {
			parameters.append(" attributepath='" + valResult.getValResultItem("attributepath").getValidatedValue() + "'");
		} else {
			parameters.append(" attributepath='.'");
		}
		if (valResult.getValResultItem("confirm") != null && !valResult.getValResultItem("confirm").getValidatedValue().equals("")) {
			parameters.append(" confirm='" + valResult.getValResultItem("confirm").getValidatedValue() + "'");
		} else {
			parameters.append(" confirm='chunk'");
		}
		if (valResult.getValResultItem("item-tag") != null && !valResult.getValResultItem("item-tag").getValidatedValue().equals("")) {
			parameters.append(" item-tag='" + valResult.getValResultItem("item-tag").getValidatedValue() + "'");
		} else {
			parameters.append(" item-tag='item'");
		}
		if (valResult.getValResultItem("tag") != null && !valResult.getValResultItem("tag").getValidatedValue().equals("")) {
			parameters.append(" tag='" + valResult.getValResultItem("tag").getValidatedValue() + "'");
		} else {
			parameters.append(" tag='items'");
		}
		
		return parameters.toString();
	}

	private static String createConstraintString(PMOValidationResult valResult, TargetManagerObject tmo) {

		// declarations
		Map<String,String> subQueries = new LinkedHashMap<String,String>();
		Hashtable<String,String> validParents = new Hashtable<String,String>();

		// run through all bracket/part objects in correct order and build sub query strings
		Map<String,TMOBracket> brackets = tmo.getBrackets();
		Iterator<String> bracketList = brackets.keySet().iterator();
		while (bracketList.hasNext()) {
			String key = bracketList.next();
			TMOBracket bracket = brackets.get(key);
			if (bracket.getType()!=null) {
				PMOValidationResultItem resItem = valResult.getValResultItem(bracket.getInputParameterName());
				if (resItem!=null && !resItem.getValidatedValue().equals("")) {
					String subQueryString = bracket.getQueryPart(resItem.getValidatedValue());
					if (bracket.isValidSubQuery()) {
						subQueries.put(key, subQueryString);
						validParents.put(bracket.getParent(), "true");
					}
				}
			}
		}
		
		bracketList = brackets.keySet().iterator();
		String result = concatSubQueries(brackets, bracketList, subQueries);
		return result;
	}

	private static String concatSubQueries(
					Map<String,TMOBracket> brackets, 
					Iterator<String> bracketList, 
					Map<String,String> subQueries  ) {
		
		StringBuffer result = new StringBuffer();
		int countBrackets = 0;
		String prevKey = "";
		
		// build overall query string
		while (bracketList.hasNext()) {
			
			String key = bracketList.next();
			TMOBracket bracket = brackets.get(key);
			TMOBracket prevBracket = brackets.get(prevKey);
			
			// check group value
			if (bracket.isValidSubQuery()) {
				// no change of group
				if (countBrackets > 0) {
					result.append(" " + bracket.getLogicalOperatorBracket() + " "); 
				}
				result.append(subQueries.get(key));
				countBrackets++;
			} else if (bracket.getType()==null) {
				// group change
				String subQueryResult = concatSubQueries(brackets, bracketList, subQueries);
				if (!subQueryResult.trim().equals("")) {
					result.append(" " + prevBracket.getLogicalOperatorBracket() + " (" + subQueryResult + ") " ); 
				}
			}
			prevKey = bracket.getKey();
		}
		
		return result.toString();
	}
	
}
