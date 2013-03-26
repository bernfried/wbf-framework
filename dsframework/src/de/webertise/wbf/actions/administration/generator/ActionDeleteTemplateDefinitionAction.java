package de.webertise.wbf.actions.administration.generator;

import de.reddot.api.common.content.CoaContent;
import de.reddot.api.common.session.CoaSession;
import de.reddot.api.web.io.WebletRequest;
import de.webertise.ds.services.ContentServices;
import de.webertise.wbf.weblet.MasterWebletConstants;

/**
 * 
 * @author bernfried howe
 */
public class ActionDeleteTemplateDefinitionAction extends de.webertise.wbf.base.action.AbstractAction {

  /** Creates a new instance of ActionDeleteTemplateDefinitionAction */
  public ActionDeleteTemplateDefinitionAction() {
  }

  public boolean execute(CoaSession session, WebletRequest request) {

    log.debug(LOG_CATEGORY, "ActionDeleteTemplateDefinitionAction - execute: Reached.");

    // get content
    ContentServices cs = new ContentServices();
    
    String ctntGroup = "gen-templates/" + this.getRequestParameter("definition") + "/instances/" + this.getRequestParameter("instance") + "/";
    log.debug(LOG_CATEGORY, "ActionDeleteTemplateDefinitionAction - execute: read content from content group '" + ctntGroup + "'" );
    
    CoaContent ctnt = cs.getContent(session, MasterWebletConstants.WBF_PROJECT_NAME, ctntGroup, "definition.xml");
    if (ctnt!=null) {
      // update content
      ctnt.deleteAttribute((String) this.getRequestParameter("action"));
      cs.updateContent(session, ctnt);
      // action ok
      this.setActionForwardName("ok");
      this.setErrorCode(Integer.toString(ACTION_EXECUTE_RESULT_OK));
      this.setErrorCode("delete_action", "0");
      
    } else {
      // acion failed - show error
      this.setActionForwardName("nok");
      this.setErrorCode("delete_action", "10");
      this.setErrorCode(Integer.toString(ACTION_EXECUTE_FAILED));
    }

    // clean up action response
    request.removeParameter("action");

    return true;
  }

  public boolean validate(CoaSession session, WebletRequest request) {
    log.debug(LOG_CATEGORY, "ActionDeleteTemplateDefinitionAttribute - validate: Reached.");
    
    // get request parameter
    boolean valid = false;
    valid = validateAllRequestParameters(session, request);
    if (!valid) {
      this.setActionForwardName("nok");
      this.setErrorCode(Integer.toString(ACTION_VALIDATION_RESULT_NOK));
    }
    
    return valid;
  }

}
