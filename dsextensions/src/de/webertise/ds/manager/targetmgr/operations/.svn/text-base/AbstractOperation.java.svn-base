/** 
 * 
 * 
 * @author Bernfried Howe, bernfried.howe@webertise.de
 * @version 1.0
 */
package de.webertise.ds.manager.targetmgr.operations;

import de.reddot.api.common.session.CoaSession;
import de.webertise.ds.manager.profilemgr.config.PMOOperation;
import de.webertise.ds.manager.profilemgr.config.ProfileManagerObject;
import de.webertise.ds.manager.profilemgr.service.PMOValidationResult;
import de.webertise.ds.manager.targetmgr.config.TargetManagerObject;

/**
 * @author bernfried
 *
 */
public abstract class AbstractOperation {
	
	/**
	 * Implements the execute method of an target manager object based operation.
	 * 
	 * @param session 		CoaSession object
	 * @param pmo 			Profile Manager Object identified by object name
	 * @param tmo 			Target Manager object identified by target name
	 * @param op 			Operation identified by operation request parameter
	 * @param valResult 	Validation result
	 * 
	 * @return StringBuffer Returns a StringBuffer which holds the xml result returned by the iolet.
	 */
	public abstract StringBuffer execute(CoaSession session, ProfileManagerObject pmo, TargetManagerObject tmo, PMOOperation op, PMOValidationResult valResult);

}
