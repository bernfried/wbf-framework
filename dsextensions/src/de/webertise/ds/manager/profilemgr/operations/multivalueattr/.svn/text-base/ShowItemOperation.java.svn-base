package de.webertise.ds.manager.profilemgr.operations.multivalueattr;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

import de.reddot.api.common.attribute.CoaAttribute;
import de.reddot.api.common.session.CoaSession;
import de.reddot.api.common.user.CoaUser;
import de.webertise.ds.manager.profilemgr.config.PMOOperation;
import de.webertise.ds.manager.profilemgr.config.PMOOutputParameter;
import de.webertise.ds.manager.profilemgr.config.ProfileManagerObject;
import de.webertise.ds.manager.profilemgr.service.PMOValidationResult;
import de.webertise.ds.manager.profilemgr.service.PMOValidationResultItem;
import de.webertise.ds.services.LogServices;

public class ShowItemOperation extends AbstractOperation {

	// declare log services
	private LogServices log = LogServices.getInstance("wbtServices");

	@SuppressWarnings("deprecation")
	@Override
	public StringBuffer execute(CoaSession session, ProfileManagerObject pmo, PMOOperation op, PMOValidationResult valResult) {

		log.debug("wbtServices", "ShowItemOperation - execute: reached");
		
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
			// get attribute values
			String[] attrValues = attr.getValues();
			log.debug("wbtServices", "ShowItemOperation - execute: attribute '" + pmo.getAttributePath() + "' has " + attrValues.length + " values.");
			for (int i=0; i<attrValues.length; i++) {
				// get a single value
				String attrValue = attrValues[i];
				log.debug("wbtServices", "ShowItemOperation - execute: value[" + i + "] = '" + attrValue + "'");
				// split value in object properties
				StringTokenizer props = new StringTokenizer(attrValue, pmo.getAttributeSeparator());
				// iterate through all properties
				Hashtable<String,String> keyValueList = new Hashtable<String,String>();
				while (props.hasMoreTokens()) {
					String prop = props.nextToken();
					log.debug("wbtServices", "ShowItemOperation - execute: property '" + prop + "'");
					// get key and value
					StringTokenizer keyValue = new StringTokenizer(prop, pmo.getKeyValueSeparator());
					// write to hashtable
					String key = keyValue.nextToken();
					String value = keyValue.nextToken();
					// add to hashtable
					keyValueList.put(key, value);
				}

				// compare validated request parameter id with current values of id
				PMOValidationResultItem item = valResult.getValResultItem("id");
				String idValue = keyValueList.get("id");
				if (idValue.equals(item.getValidatedValue())) {
					// build xml based on output parameter definition
					Hashtable<String, PMOOutputParameter> pmoOutputParams = op.getPmoOutputParameter();
					// set item element
					sbXmlResult.append("<item>");
					// create xml element for each output parameter
					Enumeration<String> paramsEnum = pmoOutputParams.keys();
					while (paramsEnum.hasMoreElements()) {
						// get key
						String key = paramsEnum.nextElement();
						log.debug("wbtServices", "ShowItemOperation - execute: output parameter key '" + key + "'");
						// get output parameter object
						PMOOutputParameter pmoOutputParam = pmoOutputParams.get(key);
						// get current value
						String value = keyValueList.get(pmoOutputParam.getProperty());
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
		} else {
			sbXmlResult.append("<info code='showFailed'>empty</info>");
		}
		
		// close root tag
		sbXmlResult.append("</result>");
		
		return sbXmlResult;
	}

}
