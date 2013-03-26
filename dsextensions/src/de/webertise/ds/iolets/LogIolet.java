/** 
 * Iolet which can lag information to standard log file (rde.log). It 
 * uses LogServices.
 * 
 * @author Bernfried Howe, bernfried.howe@webertise.de
 * @version 1.0
 */
package de.webertise.ds.iolets;

import de.reddot.api.app.io.IoletRequest;
import de.reddot.api.app.io.IoletResponse;
import de.reddot.api.app.io.IoletResponseFactory;
import de.reddot.api.app.iolet.IoletAdapter;
import de.reddot.api.common.session.CoaSession;
import de.webertise.ds.services.LogServices;

public class LogIolet extends IoletAdapter {

	// declare log services
	public final static String LOGGER_NAME = "wbtServices";
	private LogServices log = LogServices.getInstance(LOGGER_NAME);
	
	public IoletResponse doLog( CoaSession session, IoletRequest request) throws Exception {

		// declarations
		IoletResponse response = null;

		// get request parameter
		String project = (String) request.getParameter("project");
		String content = (String) request.getParameter("content");
		String debugMode = (String) request.getParameter("debugMode"); // optional - default=debug
		String sysOutFlag = (String) request.getParameter("sysOutFlag"); 
		String message = (String) request.getParameter("message");
		
		// identify debug mode
		if (debugMode != null) {
			if (debugMode.equals("debug")) {
				log.debug(LOGGER_NAME, "LogIolet - project:'" + project + "' / content: '" + content + "' / Message: '" + message + "'" );
			} else if (debugMode.equals("warn")) {
				log.warn(LOGGER_NAME, "LogIolet - project:'" + project + "' / content: '" + content + "' / Message: '" + message + "'" );
			} else if (debugMode.equals("info")) {
				log.info(LOGGER_NAME, "LogIolet - project:'" + project + "' / content: '" + content + "' / Message: '" + message + "'" );
			} else if (debugMode.equals("fatal")) {
				log.trace(LOGGER_NAME, "LogIolet - project:'" + project + "' / content: '" + content + "' / Message: '" + message + "'" );
			} else {
				log.trace(LOGGER_NAME, "LogIolet - project:'" + project + "' / content: '" + content + "' / Message: '" + message + "'" );
			}
		} else {
			log.debug(LOGGER_NAME, "LogIolet - project:'" + project + "' / content: '" + content + "' / Message: '" + message + "'" );
		}
		
		// print current logging settings
		if (sysOutFlag != null && sysOutFlag.equals("true")) log.printLogConfig(LOGGER_NAME);
		
		// no response for this method
		response = IoletResponseFactory.getIoletResponse("");
		return response;

	}
}
