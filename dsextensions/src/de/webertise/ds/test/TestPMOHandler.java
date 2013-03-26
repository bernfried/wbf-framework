/** 
 * 
 * 
 * @author Bernfried Howe, bernfried.howe@webertise.de
 * @version 1.0
 */
package de.webertise.ds.test;

import java.util.Enumeration;
import java.util.Hashtable;

import de.webertise.ds.manager.profilemgr.config.PMOInputParameter;
import de.webertise.ds.manager.profilemgr.config.PMOOperation;
import de.webertise.ds.manager.profilemgr.config.PMOOutputParameter;
import de.webertise.ds.manager.profilemgr.config.PMOValidation;
import de.webertise.ds.manager.profilemgr.config.ProfileManagerConfig;
import de.webertise.ds.manager.profilemgr.config.ProfileManagerObject;

/**
 * @author bernfried
 * 
 */
public class TestPMOHandler {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		TestProfileManagerConfig();
		
	}

	/**
	 * 
	 */
	private static void TestProfileManagerConfig() {
		
		System.out.println("Test ProfileManagerConfig - Start");
		ProfileManagerConfig config = ProfileManagerConfig.getInstance();
		
		ProfileManagerObject favoritePages = config.getConfig("favoritePages");
		System.out.println("** Name: " + favoritePages.getName());
		System.out.println("** Path: " + favoritePages.getAttributePath());
		System.out.println("** AttributeSeparator: " + favoritePages.getAttributeSeparator());
		System.out.println("** KeyValueSeparator: " + favoritePages.getKeyValueSeparator());

		// show operations
		System.out.println("**** Operations");
		Hashtable<String,PMOOperation> ops = favoritePages.getPmoOperations();
		Enumeration<String> opsEnum = ops.keys();
		while(opsEnum.hasMoreElements()) {
			String key = opsEnum.nextElement();
			System.out.println("****** Operation: " + key);
			PMOOperation op = ops.get(key);
			System.out.println("******** Classpath: " + op.getOperationClasspath());
			Hashtable<String,PMOInputParameter> iParams = op.getPmoInputParameter();
			Enumeration<String> iParamEnum = iParams.keys();
			while (iParamEnum.hasMoreElements()) {
				String keyIParam = iParamEnum.nextElement();
				System.out.println("******** Input Parameter: " + keyIParam);
				PMOInputParameter iParam = iParams.get(keyIParam);
				System.out.println("********** Name: " + iParam.getName());
				System.out.println("********** StandardValue: " + iParam.getStandardValue());
				System.out.println("********** Mandatory: " + iParam.isMandatory());
				System.out.println("********** Url-Encoding: " + iParam.getUrlEncoding());
				System.out.println("********** Html-Encoding: " + iParam.getHtmlEncoding());
				System.out.println("********** Persistent: " + iParam.isPersistent());
			}
			
			Hashtable<String,PMOOutputParameter> oParams = op.getPmoOutputParameter();
			Enumeration<String> oParamEnum = oParams.keys();
			while (oParamEnum.hasMoreElements()) {
				String keyOParam = oParamEnum.nextElement();
				System.out.println("******** Output Parameter: " + keyOParam);
				PMOOutputParameter oParam = oParams.get(keyOParam);
				System.out.println("********** Name: " + oParam.getName());
				System.out.println("********** Property: " + oParam.getProperty());
				System.out.println("********** Url-Encoding: " + oParam.getUrlEncoding());
				System.out.println("********** as-CData: " + oParam.isAsCData());
			}
		}
		// show validations
		System.out.println("**** Validations");
		Hashtable<String,PMOValidation> vals = favoritePages.getPmoValidations();
		Enumeration<String> valsEnum = vals.keys();
		while(valsEnum.hasMoreElements()) {
			String key = valsEnum.nextElement();
			System.out.println("******Validation: " + key);
			PMOValidation val = vals.get(key);
			System.out.println("******** Name: " + val.getName());
			System.out.println("******** MaxLenght: " + val.getMaxLength());
			System.out.println("******** MinLenght: " + val.getMinLength());
			System.out.println("******** RegEx: " + val.getRegEx());
		}

		System.out.println("Test ProfileManagerConfig - End");
	}

}
