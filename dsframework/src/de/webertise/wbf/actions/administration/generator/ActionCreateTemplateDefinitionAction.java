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
public class ActionCreateTemplateDefinitionAction extends de.webertise.wbf.base.action.AbstractAction {

  /** Creates a new instance of ActionCreateTemplateDefinitionAction */
  public ActionCreateTemplateDefinitionAction() {
  }

  public boolean execute(CoaSession session, WebletRequest request) {

    log.debug(LOG_CATEGORY, "ActionCreateTemplateDefinitionAction - execute: Reached.");

    // get parameter
    String actionName = (String) this.getRequestParameter("actionName");

    // get content
    ContentServices cs = new ContentServices();

    String ctntGroup = "gen-templates/" + this.getRequestParameter("definition") + "/instances/" + this.getRequestParameter("instance") + "/";
    log.debug(LOG_CATEGORY, "ActionCreateTemplateDefinitionAction - execute: read content from content group '" + ctntGroup + "'");

    CoaContent ctnt = cs.getContent(session, MasterWebletConstants.WBF_PROJECT_NAME, ctntGroup, "definition.xml");
    if (ctnt != null) {
      // update content
      String actionPath = "root.actions." + actionName;
      ctnt.setAttribute(actionPath, Locale.ENGLISH, actionName);
      ctnt.setAttribute(actionPath + ".attributes", Locale.ENGLISH, "");
      ctnt.setAttribute(actionPath + ".forwards", Locale.ENGLISH, "");
      ctnt.setAttribute(actionPath + ".packageName", Locale.ENGLISH, "");
      cs.updateContent(session, ctnt);
      request.setParameter("action", actionPath);
      // action ok
      this.setActionForwardName("ok");
      this.setErrorCode(Integer.toString(ACTION_EXECUTE_RESULT_OK));
      this.setErrorCode("create_action", Integer.toString(ACTION_EXECUTE_RESULT_OK));

    } else {
      // acion failed - show error
      super.setActionForwardName("nok");
      this.setErrorCode(Integer.toString(ACTION_EXECUTE_FAILED));
      this.setErrorCode("create_action", ActionConstants.ACTION_CUSTOM_VALIDATION_ERROR_10);
    }
    return true;
  }

  public boolean validate(CoaSession session, WebletRequest request) {
    log.debug(LOG_CATEGORY, "ActionCreateTemplateDefinitionAttribute - validate: Reached.");

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
