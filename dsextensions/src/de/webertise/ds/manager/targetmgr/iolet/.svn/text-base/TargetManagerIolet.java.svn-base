package de.webertise.ds.manager.targetmgr.iolet;

import de.reddot.api.app.io.IoletRequest;
import de.reddot.api.app.io.IoletResponse;
import de.reddot.api.app.io.IoletResponseFactory;
import de.reddot.api.app.iolet.IoletAdapter;
import de.reddot.api.common.session.CoaSession;
import de.webertise.ds.manager.profilemgr.config.PMOOperation;
import de.webertise.ds.manager.profilemgr.config.ProfileManagerConfig;
import de.webertise.ds.manager.profilemgr.config.ProfileManagerConstants;
import de.webertise.ds.manager.profilemgr.config.ProfileManagerObject;
import de.webertise.ds.manager.targetmgr.config.TargetManagerConfig;
import de.webertise.ds.manager.targetmgr.config.TargetManagerObject;
import de.webertise.ds.manager.targetmgr.service.TargetManagerService;
import de.webertise.ds.services.LogServices;

public class TargetManagerIolet extends IoletAdapter implements ProfileManagerConstants {
	
	// declare log services
	private LogServices log = LogServices.getInstance("wbtServices");
	
	public IoletResponse doManage( CoaSession session, IoletRequest request) throws Exception {
		
		// declarations
		StringBuffer sbResponse = new StringBuffer();
		StringBuffer sbValidation = new StringBuffer();
		StringBuffer sbResult = new StringBuffer();
		
		// get parameters
		String object = (String) request.getParameter(PARAM_NAME_OBJECT);
		String operation = (String) request.getParameter(PARAM_NAME_OPERATION);
		String target = (String) request.getParameter(PARAM_NAME_TARGET);
		
		// prepare xml response 
		sbResponse.append("<doManage>");
		sbResponse.append("<request-parameter>");
		sbResponse.append("<object><![CDATA[" + object + "]]></object>");
		sbResponse.append("<operation><![CDATA[" + operation + "]]></operation>");
		sbResponse.append("<target><![CDATA[" + target + "]]></target>");
		sbResponse.append("</request-parameter>");
		
		// init xml result part => will be overwritten if object/operation can be found in config
		sbResult.append("<result code='-1'>failed</result>");
		
		// prepare xml validation part
		sbValidation.append("<call-result>");
		
		// check input parameter object and operation
		if (object == null || object.equals("")) {
			log.debug("wbtServices", "TargetManagerIolet - doManage: object parameter is empty.");
			sbValidation.append("<object>parameter is empty</object>");
		} else {
			log.debug("wbtServices", "TargetManagerIolet - doManage: object parameter has value: '" + object + "'");
		}
		if (operation == null || operation.equals("")) {
			log.debug("wbtServices", "TargetManagerIolet - doManage: operation parameter is empty.");
			sbValidation.append("<operation>parameter is empty</operation>");
		} else {
			log.debug("wbtServices", "TargetManagerIolet - doManage: operation parameter has value: '" + operation + "'");
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
				// get target manager object
				TargetManagerService tms = new TargetManagerService();
				TargetManagerConfig tmoConfig = TargetManagerConfig.getInstance();
				TargetManagerObject tmo = tmoConfig.getConfig(op.getTarget());
				// execute operation
				tms.executeOperation(session, request, pmo, tmo, op);
				sbResult = tms.getXmlResult();					
			} else {
				log.debug("wbtServices", "TargetManagerIolet - doManage: operation not found for '" + operation + "'.");
				sbValidation.append("<operation><![CDATA[operation '" + operation + "' not found]]></operation>");
			}
		} else {
			log.debug("wbtServices", "TargetManagerIolet - doManage: pmo not found for '" + object + "'.");
			sbValidation.append("<object><![CDATA[object '" + object + "' not found]]></object>");
		}
		sbValidation.append("</call-result>");
		
		// build final xml response
		sbResponse.append(sbValidation);
		sbResponse.append(sbResult);
		sbResponse.append("</doManage>");
		
		// create iolet response
		IoletResponse response = IoletResponseFactory.getIoletResponse(sbResponse.toString());
		response.setParameter(IoletResponse.XML_TEXT, Boolean.TRUE);
		return response;
	}

}
