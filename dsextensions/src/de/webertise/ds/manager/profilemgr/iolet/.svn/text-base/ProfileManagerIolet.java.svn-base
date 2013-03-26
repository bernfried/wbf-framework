package de.webertise.ds.manager.profilemgr.iolet;

import de.reddot.api.app.io.IoletRequest;
import de.reddot.api.app.io.IoletResponse;
import de.reddot.api.app.io.IoletResponseFactory;
import de.reddot.api.app.iolet.IoletAdapter;
import de.reddot.api.common.session.CoaSession;
import de.webertise.ds.manager.profilemgr.config.PMOOperation;
import de.webertise.ds.manager.profilemgr.config.ProfileManagerConfig;
import de.webertise.ds.manager.profilemgr.config.ProfileManagerConstants;
import de.webertise.ds.manager.profilemgr.config.ProfileManagerObject;
import de.webertise.ds.manager.profilemgr.service.ProfileManagerService;
import de.webertise.ds.services.LogServices;

public class ProfileManagerIolet extends IoletAdapter implements ProfileManagerConstants {
	
	// declare log services
	private LogServices log = LogServices.getInstance("wbtServices");
	
	public IoletResponse doManage( CoaSession session, IoletRequest request) throws Exception {
		
		// declarations
		StringBuffer sbResponse = new StringBuffer();
		StringBuffer sbValidation = new StringBuffer();
		StringBuffer sbResult = new StringBuffer();
		IoletResponse response = IoletResponseFactory.getIoletResponse("");

		
		// get parameters
		String object = (String) request.getParameter(PARAM_NAME_OBJECT);
		String operation = (String) request.getParameter(PARAM_NAME_OPERATION);
		
		// prepare xml response 
		sbResponse.append("<doManage>");
		sbResponse.append("<request-parameter>");
		sbResponse.append("<object><![CDATA[" + object + "]]></object>");
		sbResponse.append("<operation><![CDATA[" + operation + "]]></operation>");
		sbResponse.append("</request-parameter>");
		
		// init xml result part => will be overwritten if object/operation can be found in config
		sbResult.append("<result code='-1'>failed</result>");
		
		// prepare xml validation part
		sbValidation.append("<call-result>");
		
		// check input parameter object and operation
		if (object == null || object.equals("")) {
			log.debug("wbtServices", "ProfileManagerIolet - doManage: object parameter is empty.");
			sbValidation.append("<object>parameter is empty</object>");
		} else {
			log.debug("wbtServices", "ProfileManagerIolet - doManage: object parameter has value: '" + object + "'");
		}
		if (operation == null || operation.equals("")) {
			log.debug("wbtServices", "ProfileManagerIolet - doManage: operation parameter is empty.");
			sbValidation.append("<operation>parameter is empty</operation>");
		} else {
			log.debug("wbtServices", "ProfileManagerIolet - doManage: operation parameter has value: '" + operation + "'");
		}
		
		// get operation object
		ProfileManagerConfig pmoConfig = ProfileManagerConfig.getInstance();
		ProfileManagerObject pmo = pmoConfig.getConfig(object);
		
		// check, if pmo configuration could be found in config
		if (pmo != null) {
			// get operation
			PMOOperation op = pmo.getOperation(operation);

			// check if operation exists in config
			if (op != null) {
				ProfileManagerService pms = new ProfileManagerService();
				pms.executeOperation(session, request, pmo, op);
				sbResult = pms.getXmlResult();	
				if (pms.isValOk()) {
					session.setAttribute(object + "." + operation + ".valid", "true");
				} else {
					session.setAttribute(object + "." + operation + ".valid", "false");
				}
				session.setAttribute(object + "." + operation + ".xml", sbResult.toString());
			} else {
				log.debug("wbtServices", "ProfileManagerIolet - doManage: operation not found for '" + operation + "'.");
				sbValidation.append("<operation><![CDATA[operation '" + operation + "' not found]]></operation>");
			}
		} else {
			log.debug("wbtServices", "ProfileManagerIolet - doManage: pmo not found for '" + object + "'.");
			sbValidation.append("<object><![CDATA[object '" + object + "' not found]]></object>");
		}
		sbValidation.append("</call-result>");
		
		// build final xml response
		sbResponse.append(sbValidation);
		sbResponse.append(sbResult);
		sbResponse.append("</doManage>");
		
		// create iolet response
		response.setValue(sbResponse.toString());
		response.setParameter(IoletResponse.XML_TEXT, Boolean.TRUE);
		return response;
	}

}
