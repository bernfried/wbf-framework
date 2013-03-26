/** 
 * 
 * 
 * @author Bernfried Howe, bernfried.howe@webertise.de
 * @version 1.0
 */
package de.webertise.ds.test;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import de.webertise.ds.manager.profilemgr.service.PMOValidationResult;
import de.webertise.ds.manager.profilemgr.service.PMOValidationResultItem;
import de.webertise.ds.manager.targetmgr.config.TMOBracket;
import de.webertise.ds.manager.targetmgr.config.TargetManagerConfig;
import de.webertise.ds.manager.targetmgr.config.TargetManagerObject;

public class TestQueryBuilder {

	/** 
	 * @param args 
	 */
	public static void main(String[] args) {

		PMOValidationResult valResult = new PMOValidationResult();
		valResult.addValResultItem(new PMOValidationResultItem("channel","111","111"));
		valResult.addValResultItem(new PMOValidationResultItem("type","222","222"));
		valResult.addValResultItem(new PMOValidationResultItem("roles","333","333"));
		valResult.addValResultItem(new PMOValidationResultItem("lists","444","444"));
		valResult.addValResultItem(new PMOValidationResultItem("other1","555","555"));
		valResult.addValResultItem(new PMOValidationResultItem("other2","666","666"));
		
		// get target manager object
		TargetManagerConfig tmoConfig = TargetManagerConfig.getInstance();
		TargetManagerObject tmo = tmoConfig.getConfig("channelNews1");

		// create query string
		String query = createQueryString(valResult, tmo);

		System.out.println("Query: " + query);
		
	}
	
	private static String createQueryString(PMOValidationResult valResult, TargetManagerObject tmo) {

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
		
		// TODO: Rekursiv lšsen!!
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
