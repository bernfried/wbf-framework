/** 
 * 
 * 
 * @author Bernfried Howe, bernfried.howe@webertise.de
 * @version 1.0
 */
package de.webertise.ds.services;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringEscapeUtils;
import org.jdom.input.DOMBuilder;
import org.jdom.output.XMLOutputter;
import org.w3c.dom.Document;

import de.reddot.api.app.io.IoletRequest;
import de.reddot.api.common.connector.ConnectorParamException;
import de.reddot.api.common.connector.ConnectorService;
import de.reddot.api.common.connector.ConnectorServiceException;
import de.reddot.api.common.connector.ConnectorServiceFactory;
import de.reddot.api.common.connector.ConnectorServiceFactoryException;
import de.reddot.api.common.connector.DynamentRequest;
import de.reddot.api.common.connector.DynamentResult;
import de.reddot.api.common.connector.RdbRequest;
import de.reddot.api.common.connector.RdbResult;
import de.reddot.api.common.session.CoaSession;

public class DynaMentServices implements DynaMentServicesConstants {

	// declare log services
	private static LogServices log = LogServices.getInstance("wbtServices");
	
	// constant declarations
	public final static String MESSAGE_DYNAMENT_SEND_TYPE_QUEUE = "queue";
	public final static String MESSAGE_DYNAMENT_SEND_TYPE_DIRECT = "direct";
	public final static String MESSAGE_DYNAMENT_BODY_TYPE_TEXT = "text/plain";
	public final static String MESSAGE_DYNAMENT_BODY_TYPE_HTML = "text/html";

	public static Document executeHttpDynaMent(CoaSession session, String conName, String operation, Hashtable<String, String> parameter) {

		DynamentRequest httpDynaMent = null;
		try {
			httpDynaMent = DynamentRequest.createInstance(session);
			httpDynaMent.addParameter("mode", "operation");
			httpDynaMent.addParameter("connector", conName);
			httpDynaMent.addParameter("operation", operation);
			Enumeration<String> keys = parameter.keys();
			while (keys.hasMoreElements()) {
				String key = keys.nextElement();
				httpDynaMent.addChild("rde-rd:param", (String) parameter.get(key), "name='" + key + "'");
			}
		} catch (ConnectorParamException e) {
			log.debug("wbtServices", "DynaMentServices - executeHttpConnectorRequest: ConnectorParamException");
		}

		ConnectorService service = null;
		try {
			service = ConnectorServiceFactory.createService(session, ConnectorServiceFactory.DYNAMENT, "rde-dm:http");
		} catch (ConnectorServiceFactoryException ex) {
			log.debug("wbtServices", "** CollaborationServerIolet - executeHttpConnectorRequest: ConnectorServiceFactoryException");
		}
		if (service != null) {
			try {
				DynamentResult result = (DynamentResult) service.execute(httpDynaMent);
				log.debug("wbtServices", "DynaMentServices - executeHttpConnectorRequest - Message: " + result.getMessage());
				log.debug("wbtServices", "DynaMentServices - executeHttpConnectorRequest - ErrorCode: " + result.getCode());
				log.debug("wbtServices", "DynaMentServices - executeHttpConnectorRequest - Has Errors: " + result.hasErrors());

				// return the result
				return result.getXmlDocument();

			} catch (ConnectorServiceException ex) {
				log.debug("wbtServices", "DynaMentServices - executeHttpConnectorRequest: ConnectorServiceException");
			}
		}
		return null;

	}
	
	public static Document executeIncludeDynaMent(CoaSession session, Hashtable<String,String> settings) {

		DynamentRequest includeDynaMent = null;
		
		// get include DynaMent parameter
		String url = settings.get("url");
		String contenttype = settings.get("contenttype");

		// debug output
		log.debug("wbtServices", "********* start: DynaMentServices - executeIncludeDynaMent - mail data *****************************************************");
		log.debug("wbtServices", "* url   			: " + url);
		log.debug("wbtServices", "* contenttype   	: " + contenttype);
		log.debug("wbtServices", "********* end: DynaMentServices - executeIncludeDynaMent - mail data *****************************************************");
		
		try {
			includeDynaMent = DynamentRequest.createInstance(session);
			includeDynaMent.addParameter("content", url);
			includeDynaMent.addParameter("contenttype", contenttype);
			includeDynaMent.addParameter("report-tag", "report");
		} catch (ConnectorParamException e) {
			log.debug("wbtServices", "DynaMentServices - executeIncludeDynaMent: ConnectorParamException");
		}

		ConnectorService service = null;
		try {
			service = ConnectorServiceFactory.createService(session, ConnectorServiceFactory.DYNAMENT, "rde-dm:include");
		} catch (ConnectorServiceFactoryException ex) {
			log.debug("wbtServices", "** CollaborationServerIolet - executeIncludeDynaMent: ConnectorServiceFactoryException");
		}
		if (service != null) {
			try {
				DynamentResult result = (DynamentResult) service.execute(includeDynaMent);
				log.debug("wbtServices", "DynaMentServices - executeIncludeDynaMent - Message: " + result.getMessage());
				log.debug("wbtServices", "DynaMentServices - executeIncludeDynaMent - ErrorCode: " + result.getCode());
				log.debug("wbtServices", "DynaMentServices - executeIncludeDynaMent - Has Errors: " + result.hasErrors());

				// return the result
				return result.getXmlDocument();

			} catch (ConnectorServiceException ex) {
				log.debug("wbtServices", "DynaMentServices - executeIncludeDynaMent: ConnectorServiceException");
			}
		}
		return null;

	}


	public static String executeTargetDynaMent(CoaSession session, String parameters, String constraint) {

		// result xml as String
		StringBuffer result = new StringBuffer();
		
		// build dynament request
		DynamentRequest targetDynaMent = null;
		try {
			// create target dynament
			targetDynaMent = DynamentRequest.createInstance( session );			
		    targetDynaMent.addParameters(parameters); 
		    log.debug("wbtServices", "DynaMentServices - executeTargetDynaMent - Parameters added: " + parameters);
		    targetDynaMent.addChild("rde-dm:constraint", constraint);
		    log.debug("wbtServices", "DynaMentServices - executeTargetDynaMent - Constraint added: " + constraint);
		    
		} catch ( ConnectorParamException ex ) {
		     // handle ConnectorParamException
			result.append("<error>" + ERROR_CONTENTLISTE_CONNECTOR_PARAM_EXCEPTION + "</error>");
			log.debug("wbtServices", "DynaMentServices - executeTargetDynaMent: Connector Param Expection");
		}


		// execute target dynament
		ConnectorService service = null;
		try {
			service = ConnectorServiceFactory.createService(session, ConnectorServiceFactory.DYNAMENT, "rde-dm:target");
		} catch (ConnectorServiceFactoryException ex) {
			// handle ConnectorServiceFactoryException
			result.append("<error>" + ERROR_CONTENTLISTE_CONNECTOR_SERVICE_FACTORY_EXCEPTION + "</error>");
			log.debug("wbtServices", "DynaMentServices - executeTargetDynaMent: Connector Service Factory Expection");
		}

		if (service != null) {
			try {
				DynamentResult dynaMentResult = (DynamentResult) service.execute(targetDynaMent);
				// handle the result
				org.w3c.dom.Document xmlResponse = dynaMentResult.getXmlDocument();

				try {
					// use jdom for creating a string of the xml
					DOMBuilder builder = new DOMBuilder();
					org.jdom.Document jdomDoc = builder.build(xmlResponse);

					XMLOutputter out = new XMLOutputter();
					log.debug("wbtServices", "DynaMentServices - executeTargetDynaMent - DM-Response: " + out.outputString(jdomDoc.getRootElement()));
					result.append(out.outputString(jdomDoc.getRootElement()));

				} catch (Exception e) {
					result.append("<error>" + ERROR_CONTENTLISTE_DOMBUILDER_PARSE_EXCEPTION + "</error>");
					log.debug("wbtServices", "DynaMentServices - executeTargetDynaMent: DomBuilder Parsing Expection");
				}

			} catch (ConnectorServiceException ex) {
				// handle ConnectorServiceException
				result.append("<error>" + ERROR_CONTENTLISTE_CONNECTOR_SERVICE_EXCEPTION + "</error>");
				log.debug("wbtServices", "DynaMentServices - executeTargetDynaMent: Connector Service Expection");
			}
		}
		return result.toString();
	}
	
	
	public static String executeValidationRule(CoaSession session, IoletRequest request, String attrName, String attrValue, String project, String ruleName, String resAttrName) {

		// result xml as String
		StringBuffer result = new StringBuffer();
		
		// debug output
		log.debug("wbtServices", "********* start: DynaMentServices - executeValidationRule - attribute DynaMent settings *****************************************************");
		log.debug("wbtServices", "* attrName    : " + attrName);
		log.debug("wbtServices", "* attrValue   : " + attrValue);
		log.debug("wbtServices", "* project     : " + project);
		log.debug("wbtServices", "* ruleName    : " + ruleName);
		log.debug("wbtServices", "* resAttrName : " + resAttrName);
		log.debug("wbtServices", "********* end: DynaMentServices - executeValidationRule - attribute DynaMent settings   *****************************************************");

		// build dynament request
		DynamentRequest attrDynaMent = null;
		try {
			// create target dynament
			attrDynaMent = DynamentRequest.createInstance( session );			
		    attrDynaMent.setIoletRequest(request);
		    // attrDynaMent.addParameters(parameters); 
		    attrDynaMent.addParameter("mode", "write");
		    attrDynaMent.addParameter("source", "request");
		    attrDynaMent.addParameter("attribute", attrName);
		    attrDynaMent.addParameter("value", attrValue);
		    attrDynaMent.addParameter("rule", ruleName);
		    attrDynaMent.addParameter("result-attribute", resAttrName);
		    attrDynaMent.addParameter("process-mode", "execute");
		    
		} catch ( ConnectorParamException ex ) {
		     // handle ConnectorParamException
			result.append("<error>" + ERROR_CONTENTLISTE_CONNECTOR_PARAM_EXCEPTION + "</error>");
			log.debug("wbtServices", "DynaMentServices - executeValidationRule: Connector Param Expection");
		}


		// execute target dynament
		ConnectorService service = null;
		try {
			service = ConnectorServiceFactory.createService(session, ConnectorServiceFactory.DYNAMENT, "rde-dm:attribute");
		} catch (ConnectorServiceFactoryException ex) {
			// handle ConnectorServiceFactoryException
			result.append("<error>" + ERROR_CONTENTLISTE_CONNECTOR_SERVICE_FACTORY_EXCEPTION + "</error>");
			log.debug("wbtServices", "DynaMentServices - executeValidationRule: Connector Service Factory Expection");
		}

		if (service != null) {
			try {
				DynamentResult dynaMentResult = (DynamentResult) service.execute(attrDynaMent);
				// handle the result
				org.w3c.dom.Document xmlResponse = dynaMentResult.getXmlDocument();

				try {
					// use jdom for creating a string of the xml
					DOMBuilder builder = new DOMBuilder();
					org.jdom.Document jdomDoc = builder.build(xmlResponse);

					XMLOutputter out = new XMLOutputter();
					log.debug("wbtServices", "DynaMentServices - executeValidationRule - DM-Response: " + out.outputString(jdomDoc.getRootElement()));
					// result.append(out.outputString(jdomDoc.getRootElement()));

				} catch (Exception e) {
					result.append("<error>" + ERROR_CONTENTLISTE_DOMBUILDER_PARSE_EXCEPTION + "</error>");
					log.debug("wbtServices", "DynaMentServices - executeValidationRule: DomBuilder Parsing Expection");
				}

			} catch (ConnectorServiceException ex) {
				// handle ConnectorServiceException
				result.append("<error>" + ERROR_CONTENTLISTE_CONNECTOR_SERVICE_EXCEPTION + "</error>");
				log.debug("wbtServices", "DynaMentServices - executeValidationRule: Connector Service Expection");
			}
		}
		return result.toString();
	}

	
	// *********************************************************************
	// Send email
	// *********************************************************************
	public boolean executeMessageDynaMent(CoaSession session, Hashtable<String,String> settings) {
		
		// ****************************************************
		// read parameters
		// ****************************************************
		String connector = settings.get("connector");
		String fromEmail = settings.get("sender");
		String toRecipients = settings.get("recipients");
		String subject = settings.get("subject");
		String bodyType = settings.get("bodyType");
		String bodyText = settings.get("bodyText");
		String attContent = settings.get("attachmentReference");
		String attProject = settings.get("attachmentProject");
		String attLocale = settings.get("attachmentLocale");
		String sendType = settings.get("sendType");

		// debug output
		log.debug("wbtServices", "********* start: DynaMentServices - executeMessageDynaMent - mail data *****************************************************");
		log.debug("wbtServices", "* connector   : " + connector);
		log.debug("wbtServices", "* fromEmail   : " + fromEmail);
		log.debug("wbtServices", "* toRecipients: " + toRecipients);
		log.debug("wbtServices", "* subject     : " + subject);
		log.debug("wbtServices", "* bodyType    : " + bodyType);
		log.debug("wbtServices", "* bodyText    : " + bodyText);
		log.debug("wbtServices", "* attContent  : " + attContent);
		log.debug("wbtServices", "* attProject  : " + attProject);
		log.debug("wbtServices", "* attLocale   : " + attLocale);
		log.debug("wbtServices", "* sendType    : " + sendType);
		log.debug("wbtServices", "********* end: DynaMentServices - executeMessageDynaMent - mail data *****************************************************");
		
		// check sendType
		if (sendType == null || sendType.equals("")) {
			sendType = DynaMentServices.MESSAGE_DYNAMENT_SEND_TYPE_DIRECT;
		}
		// check bodyType
		if (bodyType == null || bodyType.equals("")) {
			bodyType = DynaMentServices.MESSAGE_DYNAMENT_BODY_TYPE_TEXT;
		}

		if (toRecipients == null) toRecipients = "";
		
		// ****************************************************
		// prepare email
		// ****************************************************
		// prepare dynament request
		DynamentRequest msgDynaMent = null;
		DynamentRequest email = null;
		DynamentRequest recipients = null;
		try {
			msgDynaMent = DynamentRequest.createInstance(session);
			msgDynaMent.addParameter("mode","smtp");
			msgDynaMent.addParameter("name", connector);
			msgDynaMent.addParameter("send-type", sendType);
			msgDynaMent.addParameter("report-tag", "report");
			
			// set email properties
			email = msgDynaMent.addChild("rde-rd:email");
			email.addChild("rde-rd:from", fromEmail);
			email.addChild("rde-rd:reply-to", fromEmail);
			email.addChild("rde-rd:subject", subject);

			// add all type of recipients
			recipients = email.addChild("rde-rd:recipients");
			StringTokenizer recTokens = new StringTokenizer(toRecipients,";");
			// add recipient list to end of body in order to show the identified DL email adresses
			bodyText = bodyText + "<br>";
			while (recTokens.hasMoreElements()){
				String item = (String) recTokens.nextElement();
				recipients.addChild("rde-rd:recipient", item, "type='to'");
			}
			
			// add attachment
			if (attContent != null && !attContent.equals("")) {
				email.addChild("rde-rd:attachment","", "type='content' project='" + attProject + "' locale='" + attLocale + "' content='" + attContent  + "'");
			}
			
			// create body text element
			email.addChild("rde-rd:body", "<![CDATA[ " + bodyText + "]]>", "type='" + bodyType + "'");

		} catch (ConnectorParamException ex) {
			// handle ConnectorParamException
			log.debug("wbtServices", "DynaMentServices - executeSmtpDynaMent: message DynaMent failed - ConnectorParamException");
			return false;
		}

		// execute dynament request
		ConnectorService service = null;
		try {
			service = ConnectorServiceFactory.createService(session, ConnectorServiceFactory.DYNAMENT, "rde-dm:message");
		} catch (ConnectorServiceFactoryException ex) {
			// handle ConnectorServiceFactoryException
			log.debug("wbtServices", "DynaMentServices - executeSmtpDynaMent: message DynaMent failed - ConnectorServiceFactoryException");
			return false;
		}

		if (service != null) {
			try {
				DynamentResult dynaMentResult = (DynamentResult) service.execute(msgDynaMent);
				
				org.w3c.dom.Document xmlResponse = dynaMentResult.getXmlDocument();
		        try {
			        // use jdom for creating a string of the xml
			        DOMBuilder builder = new DOMBuilder();
			        org.jdom.Document jdomDoc = builder.build(xmlResponse);
			        
			        // add dynament result to iolet response		
					XMLOutputter out = new XMLOutputter();
					log.debug("wbtServices", "************ DynaMentServices - executeMessageDynaMent - Message DynaMent: " + out.outputString(jdomDoc.getRootElement()));
			        
		        } catch (Exception e) {
					log.debug("wbtServices", "************ DynaMentServices - executeMessageDynaMent: DomBuilder Parsing Expection");
		        }	        

				log.debug("wbtServices", "DynaMentServices - executeMessageDynaMent: DynaMent result - Message DynaMent: " + dynaMentResult.getCode());
				if (dynaMentResult.getCode() != 0) return false;
			} catch (ConnectorServiceException ex) {
				// message dynament failed
				log.debug("wbtServices", "DynaMentServices - executeMessageDynaMent: Message DynaMent failed - ConnectorServiceException");
				return false;
			}
		} else {
			// message dynament failed
			log.debug("wbtServices", "DynaMentServices - executeMessageDynaMent: Message DynaMent failed - Service is null");
			return false;
		}

		return true;
	}
	
	
	/**
	 * 
	 * 
	 * @param session 		CoaSession
	 * @param settings 		Hashtable with all parameters 
	 */
	public RdbResult executeRdbConnector(CoaSession session, Hashtable<String,String> settings) {
		
		log.debug("wbtServices", "DynaMentServices - executeRdbDynaMent: method reached");

		// ****************************************************
		// read parameters
		// ****************************************************
		String mode = settings.get("mode");
		String sql = settings.get("sql");
		String connector = settings.get("connector");

		// debug output
		log.debug("wbtServices", "********* start: DynaMentServices - executeRdbDynaMent - rdb data *****************************************************");
		log.debug("wbtServices", "* mode      : " + mode);
		log.debug("wbtServices", "* sql       : " + sql);
		log.debug("wbtServices", "* connector : " + connector);
		log.debug("wbtServices", "********* end: DynaMentServices - executeRdbDynaMent - rdb data *****************************************************");
		
		RdbRequest rdbRequest = null;
		try {
			rdbRequest = RdbRequest.createInstance(session);
			rdbRequest.setSqlStatements(sql);
			if (mode!=null && mode.equals("update")) {
				rdbRequest.setRequestType(RdbRequest.UPDATE);
			} else {
				rdbRequest.setRequestType(RdbRequest.QUERY);
			}
		} catch (ConnectorParamException ex) {
			// handle ConnectorParamException
			log.debug("wbtServices", "DynaMentServices - executeRdbDynaMent: Database request could not be build.");
			return null;
		}
		
		// execute statement - set flags for push mail tasks in RDB table
		ConnectorService service = null;
		try {
			service = ConnectorServiceFactory.createService(session, ConnectorServiceFactory.RDB, connector);
		} catch (ConnectorServiceFactoryException ex) {
			// handle ConnectorServiceFactoryException
			log.debug("wbtServices", "DynaMentServices - executeRdbDynaMent: Error - Database Connector could not be instanciated.");
		}
		if (service != null) {
			RdbResult rdbResult = null;
			try {
				rdbResult = (RdbResult) service.execute(rdbRequest);
				
				// handle result
				if (rdbResult.hasErrors()) {
					log.debug("wbtServices", "DynaMentServices - executeRdbDynaMent: Error - rdbresult = '" + rdbResult.getFirstError().getErrorCode() + " / " + rdbResult.getFirstError().getMessage() + "'.");
				} else {
					log.debug("wbtServices", "DynaMentServices - executeRdbDynaMent: SQL Statement successfully executed.");
					return rdbResult;
				}
			} catch (ConnectorServiceException ex) {
				// handle ConnectorServiceException
				log.debug("wbtServices", "DynaMentServices - executeRdbDynaMent: Database Connector does not work.");
			}
		} else {
			log.debug("wbtServices", "DynaMentServices - executeRdbDynaMent: Error - Service is null.");
		}
		return null;
	}
	
	
	public DynamentResult executeRdbDynaMent(CoaSession session, Hashtable<String,String> settings) {

		DynamentRequest rdbDynaMent = null;
		
		// get include DynaMent parameter
		String mode = settings.get("mode");
		String alias = settings.get("alias");
		String sql = settings.get("sql");

		// escape html in sql statement
		sql = StringEscapeUtils.escapeHtml(sql);

		// debug output
		log.debug("wbtServices", "********* start: DynaMentServices - executeRdbDynaMent - mail data *****************************************************");
		log.debug("wbtServices", "* mode  			: " + mode);
		log.debug("wbtServices", "* alias		   	: " + alias);
		log.debug("wbtServices", "* sql		   	: " + sql);
		log.debug("wbtServices", "********* end: DynaMentServices - executeRdbDynaMent - mail data *****************************************************");
		
		
		try {
			rdbDynaMent = DynamentRequest.createInstance(session);
			rdbDynaMent.addParameter("mode", mode);
			rdbDynaMent.addParameter("alias", alias);
			rdbDynaMent.addParameter("sql", sql);
			rdbDynaMent.addParameter("report-tag", "report");
		} catch (ConnectorParamException e) {
			log.debug("wbtServices", "DynaMentServices - executeRdbDynaMent: ConnectorParamException");
		}

		ConnectorService service = null;
		try {
			service = ConnectorServiceFactory.createService(session, ConnectorServiceFactory.DYNAMENT, "rde-dm:rdb");
		} catch (ConnectorServiceFactoryException ex) {
			log.debug("wbtServices", "** CollaborationServerIolet - executeRdbDynaMent: ConnectorServiceFactoryException");
		}
		if (service != null) {
			try {
				DynamentResult result = (DynamentResult) service.execute(rdbDynaMent);
				log.debug("wbtServices", "DynaMentServices - executeRdbDynaMent - Message: " + result.getMessage());
				log.debug("wbtServices", "DynaMentServices - executeRdbDynaMent - ErrorCode: " + result.getCode());
				log.debug("wbtServices", "DynaMentServices - executeRdbDynaMent - Has Errors: " + result.hasErrors());

				// return the result
				return result;

			} catch (ConnectorServiceException ex) {
				log.debug("wbtServices", "DynaMentServices - executeRdbDynaMent: ConnectorServiceException");
			}
		}
		return null;

	}



	
	
	
	
}
