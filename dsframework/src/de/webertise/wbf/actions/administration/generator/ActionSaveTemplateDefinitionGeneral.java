package de.webertise.wbf.actions.administration.generator;

import java.util.Locale;
import de.reddot.api.common.content.CoaContent;
import de.reddot.api.common.session.CoaSession;
import de.reddot.api.web.io.WebletRequest;
import de.webertise.ds.services.ContentServices;
import de.webertise.wbf.base.action.ActionResponseItem;
import de.webertise.wbf.actions.ActionConstants;
import de.webertise.wbf.weblet.MasterWebletConstants;

/**
 * 
 * @author bernfried howe
 */
public class ActionSaveTemplateDefinitionGeneral extends de.webertise.wbf.base.action.AbstractAction {

  /** Creates a new instance of ActionSaveTemplateDefinitionGeneral */
  public ActionSaveTemplateDefinitionGeneral() {
  }

  public boolean execute(CoaSession session, WebletRequest request) {

    log.debug(LOG_CATEGORY, "ActionSaveTemplateDefinitionGeneral - execute: Reached.");
    
    // get content
    ContentServices cs = new ContentServices();
    
    String ctntGroup = "gen-templates/" + this.getRequestParameter("definition") + "/instances/" + this.getRequestParameter("instance") + "/";
    log.debug(LOG_CATEGORY, "ActionSaveTemplateDefinitionGeneral - execute: read content from content group '" + ctntGroup + "'" );
    
    CoaContent ctnt = cs.getContent(session, MasterWebletConstants.WBF_PROJECT_NAME, ctntGroup, "definition.xml");
    if (ctnt!=null) {
      // update content
      ctnt.setAttribute("root.controllerName", Locale.ENGLISH, (String) this.getRequestParameter("controllerName"));
      ctnt.setAttribute("root.targetProject", Locale.ENGLISH, (String) this.getRequestParameter("targetProject"));
      ctnt.setAttribute("root.targetApplication", Locale.ENGLISH, (String) this.getRequestParameter("targetApplication"));
      cs.updateContent(session, ctnt);
      // action ok
      this.setActionForwardName("ok");
      this.setErrorCode(Integer.toString(ACTION_EXECUTE_RESULT_OK));
      this.setErrorCode("instance_general_save", Integer.toString(ACTION_EXECUTE_RESULT_OK));
      
    } else {
      // action failed - show error
      super.setActionForwardName("nok");
      this.setErrorCode(Integer.toString(ACTION_EXECUTE_FAILED));
      this.setErrorCode("instance_general_save", ActionConstants.ACTION_CUSTOM_VALIDATION_ERROR_10);
    }
    
    this.setResponseParameter(ActionResponseItem.TARGET_SESSION, "definition", this.getRequestParameter("definition"), false, false);
    this.setResponseParameter(ActionResponseItem.TARGET_SESSION, "instance", this.getRequestParameter("instance"), false, false);
    
    return true;
  }

  public boolean validate(CoaSession session, WebletRequest request) {
    log.debug(LOG_CATEGORY, "ActionSaveTemplateDefinitionGeneral - validate: Reached.");
    
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
