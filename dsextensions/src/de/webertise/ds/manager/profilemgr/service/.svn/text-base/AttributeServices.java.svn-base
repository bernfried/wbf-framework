package de.webertise.ds.manager.profilemgr.service;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;

import de.reddot.api.common.attribute.CoaAttribute;
import de.reddot.api.common.user.CoaUser;
import de.webertise.ds.manager.profilemgr.config.ProfileManagerObject;
import de.webertise.ds.services.LogServices;

public class AttributeServices {
	
	// declare log services
	private static LogServices log = LogServices.getInstance("wbtServices");

	public static Map<String,Hashtable<String,String>> getKeyValuePairs(CoaAttribute attr, String keyValueSeparator, String pairSeparator) {
		
		Map<String,Hashtable<String,String>> attrValueList = new LinkedHashMap<String,Hashtable<String,String>>();
		
		// get attribute values
		String[] attrValues = attr.getValues();
		log.debug("wbtServices", "AttributeServices - getKeyValuePairs: attribute '" + attr.getPath() + "' has " + attrValues.length + " values.");
		for (int i = 0; i < attrValues.length; i++) {
			// get a single value
			String attrValue = attrValues[i];
			log.debug("wbtServices", "AttributeServices - getKeyValuePairs: value[" + i + "] = '" + attrValue + "'");
			// split value in object properties
			StringTokenizer props = new StringTokenizer(attrValue, pairSeparator);
			// iterate through all properties
			Hashtable<String,String> keyValueList = new Hashtable<String,String>();
			String id = "";
			while (props.hasMoreTokens()) {
				String prop = props.nextToken();
				log.debug("wbtServices", "AttributeServices - getKeyValuePairs: property '" + prop + "'");
				// get key and value
				StringTokenizer keyValue = new StringTokenizer(prop, keyValueSeparator);
				// write to hashtable
				String key = keyValue.nextToken();
				String value = keyValue.nextToken();
				keyValueList.put(key, value);
				log.debug("wbtServices", "AttributeServices - getKeyValuePairs: put key/value to hashtable: '" + key + "/" + value + "'");
				// get id
				if (key.equals("id")) id = value;
			}
			// put item to pairList hashtable
			attrValueList.put(id, keyValueList);
			log.debug("wbtServices", "AttributeServices - getKeyValuePairs: Added attribute with id '" + id + "'");
			
		}
		// return the list of attribute values (Key: id / Value: hashtable with key/value pairs)
		return attrValueList;
	}

	public static void addAttributeValue(CoaAttribute attr, ProfileManagerObject pmo, Hashtable<String, String> newItem, int iLimit, boolean addToTop) {

		// declarations
		ArrayList<String> newAttrValues = new ArrayList<String>();
		
		// get string which should be stored in user attribute
		String newItemStr = createPersistString(pmo, newItem);
		
		// if item should be added to the top of the list
		if (addToTop) {
			newAttrValues.add(newItemStr);
			log.debug("wbtServices", "AttributeServices - addAttributeValue: Added value to ArrayList (new-top): " + newItemStr);
		}
		
		// get all items as string array
		String[] curAttrValues = attr.getValues();
		
		// check, which items need to be kept in new list
		int iAttrIndex = 0;
		if (iLimit == 0 || iLimit > curAttrValues.length) {
			iAttrIndex = curAttrValues.length;
		} else {
			// limit - 1 because new item needs to be considered
			iAttrIndex = iLimit-1;
		}
		for (int i=0; i<iAttrIndex; i++) {
			newAttrValues.add(curAttrValues[i]);
			log.debug("wbtServices", "AttributeServices - addAttributeValue: Added value to ArrayList (existing): " + curAttrValues[i]);
		}
		
		// if item should be added to the top of the list
		if (!addToTop) {
			newAttrValues.add(newItemStr);
			log.debug("wbtServices", "AttributeServices - addAttributeValue: Added value to ArrayList (new-bottom): " + newItemStr);
		}
		
		// remove all values and add new attr values list to it again
		attr.removeAllValues();
		for (int i=0; i<newAttrValues.size(); i++) {
			attr.addValue(newAttrValues.get(i));
			log.debug("wbtServices", "AttributeServices - addAttributeValue: Added value to user attribute: " + newAttrValues.get(i));
		}
		
	}
	
	public static void createAttributeValue(CoaUser user, ProfileManagerObject pmo, Hashtable<String, String> newItem) {

		// get string which should be stored in user attribute
		String newItemStr = createPersistString(pmo, newItem);

		// create attribute
		user.setCoaAttribute(pmo.getAttributePath(), newItemStr);
	}
	
	public static String createPersistString(ProfileManagerObject pmo, Hashtable<String, String> item) {
		StringBuffer sbResult = new StringBuffer();
		
		Enumeration<String> keys = item.keys();
		
		// put id always as first key/value pair
		sbResult.append("id" + pmo.getKeyValueSeparator() + item.get("id"));
		
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			String value = item.get(key);
			if (!key.equals("id")) sbResult.append(pmo.getAttributeSeparator() + key + pmo.getKeyValueSeparator() + value);
		}
		
		return sbResult.toString();
	}

	public static boolean checkExistenceById(ProfileManagerObject pmo, CoaAttribute attr, PMOValidationResultItem newItemId) {
		if (attr != null) {
			String[] attrValues = attr.getValues();
			if (attrValues != null && attrValues.length > 0) {
				for (int i=0; i< attrValues.length; i++) {
					if (attrValues[i].startsWith("id" + pmo.getKeyValueSeparator() + newItemId.getValidatedValue())) return true;
				}
			}
		}
		return false;
	}

	public static void logCurrentAttributeValues(CoaAttribute attr) {
		if (attr != null) {
			String[] attrValues = attr.getValues();
			if (attrValues != null && attrValues.length > 0) {
				for (int i=0; i< attrValues.length; i++) {
					log.debug("wbtServices", "AttributeServices - logCurrentAttributeValues: Value at index '" + i + "' : '" + attrValues[i] + "'");
				}
			}
		}
	}

}
