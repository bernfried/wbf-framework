package de.webertise.ds.manager.targetmgr.service;

import de.reddot.api.app.io.IoletRequest;
import de.reddot.api.common.session.CoaSession;
import de.webertise.ds.manager.profilemgr.config.PMOOperation;
import de.webertise.ds.manager.profilemgr.config.ProfileManagerObject;
import de.webertise.ds.manager.profilemgr.service.PMOValidationResult;
import de.webertise.ds.manager.targetmgr.config.TargetManagerObject;
import de.webertise.ds.services.LogServices;
import de.webertise.ds.services.ValidationService;

public class TargetManagerService {

	// declare log services
	private LogServices log = LogServices.getInstance("wbtServices");
	
	// declare string buffer for xml response
	private StringBuffer sbXmlResult;
	
	public boolean executeOperation(
			CoaSession session, 
			IoletRequest request, 
			ProfileManagerObject pmo, 
			TargetManagerObject tmo, 
			PMOOperation op) {

		log.debug("wbtServices", "TargetManagerService - executeOperation: reached operation '" + op.getName() + "'.");
		
		// ******************************************************
		// * validate request parameters
		// ******************************************************
		PMOValidationResult valResult = ValidationService.validateInputParameters(request, pmo, op);
		
		log.debug("wbtServices", "TargetManagerService - executeOperation: valid = '" + valResult.isValid() + "'.");
		log.debug("wbtServices", "TargetManagerService - executeOperation: classpath: '" + op.getOperationClasspath() + "'.");

		if (valResult.isValid()) {
			
			// create instance of operation
			@SuppressWarnings("rawtypes")
			Class abstractOperationClass = null;
			try {
				// get action class
				abstractOperationClass = Class.forName(op.getOperationClasspath());
				
			} catch (ClassNotFoundException e) {
				log.debug("wbtServices", "TargetManagerService - executeOperation: ClassNotFoundException for classpath: '" + op.getOperationClasspath() + "'.");
			}
			
			// get object
			de.webertise.ds.manager.targetmgr.operations.AbstractOperation abstractOperationObject = null;
			try {
				abstractOperationObject = (de.webertise.ds.manager.targetmgr.operations.AbstractOperation) abstractOperationClass.newInstance();
			} catch (InstantiationException e) {
				log.debug("wbtServices", "TargetManagerService - executeOperation: InstantiationException for class: '" + op.getOperationClasspath() + "'.");
			} catch (IllegalAccessException e) {
				log.debug("wbtServices", "TargetManagerService - executeOperation: IllegalAccessException for class: '" + op.getOperationClasspath() + "'.");
			}
			
			// call execute method of operation
			sbXmlResult = ValidationService.createResultInvalidXml(request, valResult, "0");
			request.setParameter("validation.result.valid", "true");
			// call execute method of operation
			sbXmlResult.append(abstractOperationObject.execute(session, pmo, tmo, op, valResult));
			
			
		} else {
			// result is invalid => create sbResult StringBuffer
			sbXmlResult = ValidationService.createResultInvalidXml(request, valResult, "-2");
			request.setParameter("validation.result.valid", "false");
		}
		
		return true;
	}

	public StringBuffer getXmlResult() {
		return sbXmlResult;
	}

}
