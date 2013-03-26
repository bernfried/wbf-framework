package de.webertise.ds.manager.profilemgr.service;

import de.reddot.api.app.io.IoletRequest;
import de.reddot.api.common.session.CoaSession;
import de.webertise.ds.manager.profilemgr.config.PMOOperation;
import de.webertise.ds.manager.profilemgr.config.ProfileManagerObject;
import de.webertise.ds.services.LogServices;
import de.webertise.ds.services.ValidationService;

public class ProfileManagerService {

	// declare log services
	private LogServices log = LogServices.getInstance("wbtServices");
	
	// declare string buffer for xml response
	private StringBuffer sbXmlResult;
	private boolean valOk = false;
	
	public boolean executeOperation(CoaSession session, IoletRequest request, ProfileManagerObject pmo, PMOOperation op) {

		log.debug("wbtServices", "ProfileManagerService - executePMOOperation: reached operation '" + op.getName() + "'.");
		
		// ******************************************************
		// * validate request parameters
		// ******************************************************
		PMOValidationResult valResult = ValidationService.validateInputParameters(request, pmo, op);
		
		log.debug("wbtServices", "ProfileManagerService - executePMOOperation: valid = '" + valResult.isValid() + "'.");
		log.debug("wbtServices", "ProfileManagerService - executePMOOperation: classpath: '" + op.getOperationClasspath() + "'.");

		if (valResult.isValid()) {
			
			// create instance of operation
			@SuppressWarnings("rawtypes")
			Class abstractOperationClass = null;
			try {
				// get action class
				abstractOperationClass = Class.forName(op.getOperationClasspath());
				
			} catch (ClassNotFoundException e) {
				log.debug("wbtServices", "ProfileManagerService - executePMOOperation: ClassNotFoundException for classpath: '" + op.getOperationClasspath() + "'.");
			}
			
			// get object
			de.webertise.ds.manager.profilemgr.operations.multivalueattr.AbstractOperation abstractOperationObject = null;
			try {
				abstractOperationObject = (de.webertise.ds.manager.profilemgr.operations.multivalueattr.AbstractOperation) abstractOperationClass.newInstance();
			} catch (InstantiationException e) {
				log.debug("wbtServices", "ProfileManagerService - executePMOOperation: InstantiationException for class: '" + op.getOperationClasspath() + "'.");
			} catch (IllegalAccessException e) {
				log.debug("wbtServices", "ProfileManagerService - executePMOOperation: IllegalAccessException for class: '" + op.getOperationClasspath() + "'.");
			}
			
			// call execute method of operation
			sbXmlResult = ValidationService.createResultInvalidXml(request, valResult, "0");
			valOk = true;
			
			// call execute method of operation
			sbXmlResult.append(abstractOperationObject.execute(session, pmo, op, valResult));
			
			
		} else {
			// result is invalid => create sbResult StringBuffer
			sbXmlResult = ValidationService.createResultInvalidXml(request, valResult, "-2");
			
			valOk = false;
		}
		
		return true;
	}

	public StringBuffer getXmlResult() {
		return sbXmlResult;
	}
	
	public boolean isValOk() {
		return valOk;
	}

}
