package de.webertise.wbf.actions.administration.generator;

import java.util.Locale;

import de.reddot.api.common.content.CoaContent;
import de.reddot.api.common.session.CoaSession;
import de.reddot.api.web.io.WebletRequest;
import de.webertise.ds.services.ContentServices;
import de.webertise.wbf.actions.ActionConstants;
import de.webertise.wbf.weblet.MasterWebletConstants;

/**
 * 
 * @author bernfried howe
 */
public class ActionSaveTemplateDefinitionActionForward extends de.webertise.wbf.base.action.AbstractAction {

  /** Creates a new instance of ActionInit */
  public ActionSaveTemplateDefinitionActionForward() {
  }

  public boolean execute(CoaSession session, WebletRequest request) {

    log.debug(LOG_CATEGORY, "ActionSaveTemplateDefinitionActionForward - execute: Reached.");
    
    // get content
    ContentServices cs = new ContentServices();
    
    String ctntGroup = "gen-templates/" + this.getRequestParameter("definition") + "/instances/" + this.getRequestParameter("instance") + "/";
    log.debug(LOG_CATEGORY, "ActionSaveTemplateDefinitionActionForward - execute: read content from content group '" + ctntGroup + "'" );
    
    CoaContent ctnt = cs.getContent(session, MasterWebletConstants.WBF_PROJECT_NAME, ctntGroup, "definition.xml");
    if (ctnt!=null) {
      // update content
      String attrPath = this.getRequestParameter("forward") + ".";
      ctnt.setAttribute(attrPath + "type", Locale.ENGLISH, (String) this.getRequestParameter("forwardType"));
      ctnt.setAttribute(attrPath + "action", Locale.ENGLISH, (String) this.getRequestParameter("forwardActionName"));
      ctnt.setAttribute(attrPath + "template", Locale.ENGLISH, (String) this.getRequestParameter("template"));
      ctnt.setAttribute(attrPath + "view", Locale.ENGLISH, (String) this.getRequestParameter("view"));
      cs.updateContent(session, ctnt);
      // action ok
      this.setActionForwardName("ok");
      this.setErrorCode(Integer.toString(ACTION_EXECUTE_RESULT_OK));
      this.setErrorCode("save_actionforward", Integer.toString(ACTION_EXECUTE_RESULT_OK));
      
    } else {
      // acion failed - show error
      super.setActionForwardName("nok");
      this.setErrorCode(Integer.toString(ACTION_EXECUTE_FAILED));
      this.setErrorCode("save_actionforward", ActionConstants.ACTION_CUSTOM_VALIDATION_ERROR_10);
    }
    
    return true;
  }

  public boolean validate(CoaSession session, WebletRequest request) {
    log.debug(LOG_CATEGORY, "ActionSaveTemplateDefinitionActionForward - validate: Reached.");
    
    // get request parameter
    boolean valid = false;
    valid = validateAllRequestParameters(session, request);
    if (!valid) {
      super.setActionForwardName("nok");
      this.setErrorCode(Integer.toString(ACTION_VALIDATION_RESULT_NOK));
    }
    
    return valid;
  }

}
