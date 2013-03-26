/** 
 * Iolet which can lag information to standard log file (rde.log). It 
 * uses LogServices.
 * 
 * @author Bernfried Howe, bernfried.howe@webertise.de
 * @version 1.0
 */
package de.webertise.ds.iolets;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import de.reddot.api.app.io.IoletRequest;
import de.reddot.api.app.io.IoletResponse;
import de.reddot.api.app.io.IoletResponseFactory;
import de.reddot.api.app.iolet.IoletAdapter;
import de.reddot.api.common.session.CoaSession;
import de.webertise.ds.services.DynaMentServices;
import de.webertise.ds.services.LogServices;
import de.webertise.ds.utils.Substitutor;

public class EmailHandlerIolet extends IoletAdapter {

	// declare log services
	private LogServices log = LogServices.getInstance("wbtServices");
	
	public IoletResponse doSend( CoaSession session, IoletRequest request) throws Exception {

		// declarations
		IoletResponse response = null;
		
		// log
		log.debug("wbtServices", "EmailHandlerIolet - doSend: method reached.");

		// get request parameter
		String connector = (String) request.getParameter("connector");
		String subject = (String) request.getParameter("subject");
		String body = (String) request.getParameter("body");
		String sender = (String) request.getParameter("sender");
		String receiver = (String) request.getParameter("receiver");
		String phStartTag = (String) request.getParameter("phStartTag");
		String phEndTag = (String) request.getParameter("phEndTag");
		String pattern = (String) request.getParameter("pattern");
		String css = (String) request.getParameter("css");
		
		// check parameters
		if (phStartTag == null || phStartTag.equals("")) phStartTag = "<#";
		if (phEndTag == null || phEndTag.equals("")) phEndTag = "#>";
		if (pattern == null || pattern.equals("")) pattern = "\\<\\#[\\w\\s]*\\#\\>";
		if (css == null) css = "";
		if (subject == null) subject = "";
		if (body == null) body = "";

		// analyse email text and find all placeholders incl. values
		String[] phsSubject = Substitutor.find(subject, pattern); 
		String[] phsBody = Substitutor.find(body, pattern);
		Map<String,String> allPhs = getPlaceholderValues(request, phsSubject, phsBody);
		
		subject = Substitutor.substituteVariables(subject, allPhs, "<#", "#>");
		body = Substitutor.substituteVariables(body, allPhs, "<#", "#>");
		
		// send emails
		boolean resultOk = sendEmailAsHtml(session, connector, sender, receiver, subject, body, css);
		if (!resultOk) {
			log.debug("wbtServices", "EmailHandlerIolet - doSend: Confirmation email failed");
			session.setAttribute("emailHandler.sendEmailFailed", "true");
		}
		
		// no response for this method
		response = IoletResponseFactory.getIoletResponse("");
		return response;

	}
	
	private Map<String,String> getPlaceholderValues(IoletRequest request, String[] phsSubject, String[] phsBody) {
		
		Map<String, String> result = new HashMap<String, String>();
		
		// replace placehorders in body
		for (int i=0; i<phsBody.length; i++) {
			String value = (String) request.getParameter(phsBody[i]);
			if (value != null && !value.equals("")) result.put(phsBody[i], value);
		}
		
		// replace placehorders in subject
		for (int i=0; i<phsSubject.length; i++) {
			String value = (String) request.getParameter(phsSubject[i]);
			if (value != null && !value.equals("")) result.put(phsSubject[i], value);
		}
		
		return result;
	}


	/**
	 * Sends the email to the specified email address. 
	 * 
	 * @param session				CoaSession
	 * @param smtpConnector			Name of the smtp connector
	 * @param confirmEmailAddress	Email Address of recipient
	 * @param confirmSubject		Subject of the confirmation email
	 * @param confirmBody			Body of the confirmation email
	 * @param senderEmailAddress	Email Address of the sender e.g. noreply@mobi.ch
	 * @return
	 */
	private boolean sendEmailAsHtml(	CoaSession session,
										String smtpConnector, 
										String sender,
										String receiver,
										String subject,
										String body,
										String css) {

		// log
		log.debug("wbtServices", "#### EmailHandlerIolet - sendEmailAsHtml: method reached.");
		
		// add html around body
		StringBuffer html = new StringBuffer();
		html.append("<html><head><title>" + subject + "</title>" + css + " </head><body>" + body + "</body></html>");

		// fill settings
		Hashtable<String,String> settings = new Hashtable<String,String>();
		settings.put("connector", smtpConnector);
		settings.put("sender", sender);
		settings.put("recipients", receiver);
		settings.put("subject", subject);
		settings.put("bodyText", html.toString());
		settings.put("bodyType", DynaMentServices.MESSAGE_DYNAMENT_BODY_TYPE_HTML);
		settings.put("sendType", DynaMentServices.MESSAGE_DYNAMENT_SEND_TYPE_DIRECT);
		
		log.debug("wbtServices", "#### EmailHandlerIolet - sendEmailAsHtml: Body: " + html.toString());
		session.setAttribute("budcenter.orderemail.html", html.toString());
		
		// execute sql statement
		DynaMentServices dmServices = new DynaMentServices();
		return dmServices.executeMessageDynaMent(session, settings);
	}


}
