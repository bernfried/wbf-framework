package de.webertise.ds.manager.profilemgr.operations.multivalueattr;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import de.reddot.api.common.attribute.CoaAttribute;
import de.reddot.api.common.session.CoaSession;
import de.reddot.api.common.user.CoaUser;
import de.webertise.ds.manager.profilemgr.config.PMOOperation;
import de.webertise.ds.manager.profilemgr.config.PMOOutputParameter;
import de.webertise.ds.manager.profilemgr.config.ProfileManagerObject;
import de.webertise.ds.manager.profilemgr.service.AttributeServices;
import de.webertise.ds.manager.profilemgr.service.PMOValidationResult;
import de.webertise.ds.services.LogServices;

public class ShowAllItemsOperation extends AbstractOperation {

	// declare log services
	private LogServices log = LogServices.getInstance("wbtServices");

	@Override
	public StringBuffer execute(CoaSession session, ProfileManagerObject pmo, PMOOperation op, PMOValidationResult valResult) {

		log.debug("wbtServices", "ShowAllItemsOperation - execute: reached");
		
		// declaration
		StringBuffer sbXmlResult = new StringBuffer();
		
		// create root tag 
		sbXmlResult.append("<result>");
		
		// get session user
		CoaUser user = session.getCoaUser();
		
		// get user attribute
		CoaAttribute attr = user.getCoaAttribute(pmo.getAttributePath());
		
		// log all attribute values before showAllItems operation
		AttributeServices.logCurrentAttributeValues(attr);
		
		// check if attribute exists already and has values
		if (attr != null && attr.hasValues()) {
			
			// get attribute values
			Map<String,Hashtable<String,String>> attrValues = AttributeServices.getKeyValuePairs(attr, pmo.getKeyValueSeparator(), pmo.getAttributeSeparator());
			
			// build xml based on output parameter definition
			Hashtable<String, PMOOutputParameter> pmoOutputParams = op.getPmoOutputParameter();

			// create <items> start tag 
			sbXmlResult.append("<items hits='" + attrValues.size() + "'>");
			
			// for each attribute value
			int pos = 1; // init counter
			int last = attrValues.size();
			Iterator<String> attrValuesIter = attrValues.keySet().iterator();
			while (attrValuesIter.hasNext()) {
				
				// get key
				String keyAttr = attrValuesIter.next();
				
				// get key value pairs for each item of the attribute - keyAttr is the value of "id"
				Hashtable<String,String> keyValueList = attrValues.get(keyAttr);
				
				// create <item>...</item> xml
				createItemXml(sbXmlResult, pmoOutputParams, keyValueList, pos++, last);
				
			}
			
			// create </items> end tag 
			sbXmlResult.append("</items>");

			
		} else {
			sbXmlResult.append("<info>empty</info>");
		}
		
		// close root tag
		sbXmlResult.append("</result>");
		
		return sbXmlResult;
	}

	/**
	 * @param sbXmlResult
	 * @param pmoOutputParams
	 * @param keyValueList
	 */
	@SuppressWarnings("deprecation")
	private void createItemXml(StringBuffer sbXmlResult, Hashtable<String, PMOOutputParameter> pmoOutputParams, Hashtable<String, String> keyValueList, int pos, int last) {
		// set item element
		if (pos==last) {
			sbXmlResult.append("<item hit='" + pos + "' last='true'>");
		} else {
			sbXmlResult.append("<item hit='" + pos + "'>");
		}
		// create xml element for each output parameter
		Enumeration<String> paramsEnum = pmoOutputParams.keys();
		while (paramsEnum.hasMoreElements()) {
			// get key
			String key = paramsEnum.nextElement();
			
			// get output parameter object
			PMOOutputParameter pmoOutputParam = pmoOutputParams.get(key);
			log.debug("wbtServices", "ShowAllItemsOperation - execute: output parameter key '" + key + "' and property name is '" + pmoOutputParam.getProperty() + "'");
			
			// get current value
			String value = keyValueList.get(pmoOutputParam.getProperty());
			log.debug("wbtServices", "ShowAllItemsOperation - execute: value for property is '" + value + "'");
			
			// check if value needs to be encoded
			if (pmoOutputParam.getUrlEncoding().equals("encode")) {
				value = URLEncoder.encode(value);
			} else if (pmoOutputParam.getUrlEncoding().equals("decode")) {
				value = URLDecoder.decode(value);
			}
			// create xml element
			if (pmoOutputParam.isAsCData()) {
				sbXmlResult.append("<" + pmoOutputParam.getName() + " url-encoding='" + pmoOutputParam.getUrlEncoding() + "'><![CDATA[" + value +"]]></" + pmoOutputParam.getName() + ">");
			} else {
				sbXmlResult.append("<" + key + " url-encoding='" + pmoOutputParam.getUrlEncoding() + "'>" + value +"</" + key + ">");
			}
		}
		sbXmlResult.append("</item>");
	}

}
