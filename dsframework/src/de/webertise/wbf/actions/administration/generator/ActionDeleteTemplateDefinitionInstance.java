package de.webertise.wbf.actions.administration.generator;

import de.reddot.api.common.content.CoaContentControllerFactory;
import de.reddot.api.common.content.CoaContentGroupController;
import de.reddot.api.common.session.CoaSession;
import de.reddot.api.exception.AccessNotGrantedException;
import de.reddot.api.exception.LockNotGrantedException;
import de.reddot.api.web.io.WebletRequest;
import de.webertise.wbf.actions.ActionConstants;
import de.webertise.wbf.weblet.MasterWebletConstants;

/**
 * 
 * @author bernfried howe
 */
public class ActionDeleteTemplateDefinitionInstance extends de.webertise.wbf.base.action.AbstractAction {

  /** Creates a new instance of ActionDeleteTemplateDefinitionInstance */
  public ActionDeleteTemplateDefinitionInstance() {
  }

  public boolean execute(CoaSession session, WebletRequest request) {

    log.debug(LOG_CATEGORY, "ActionDeleteTemplateDefinitionInstance - execute: Reached.");

    String ctntGroup = "gen-templates/" + this.getRequestParameter("definition") + "/instances/" + this.getRequestParameter("instance");
    log.debug(LOG_CATEGORY, "ActionDeleteTemplateDefinitionInstance - execute: delete content group '" + ctntGroup + "'");

    CoaContentGroupController cgCtrl = CoaContentControllerFactory.getCoaContentGroupController();
    boolean deleteCtntGroup = false;
    try {

      cgCtrl.deleteCoaContentGroup(session, MasterWebletConstants.WBF_PROJECT_NAME, ctntGroup);
      this.setActionForwardName("ok");
      this.setErrorCode(Integer.toString(ACTION_EXECUTE_RESULT_OK));
      this.setErrorCode("delete_instance", Integer.toString(ACTION_EXECUTE_RESULT_OK));
      deleteCtntGroup = true;

      // clean up action response
      request.removeParameter("instance");

    } catch (AccessNotGrantedException e) {
      log.debug(LOG_CATEGORY, "ActionDeleteTemplateDefinitionInstance - execute: access not granted to delete content group '" + ctntGroup + "'");
      this.setErrorCode("delete_instance", ActionConstants.ACTION_CUSTOM_VALIDATION_ERROR_10);
    } catch (LockNotGrantedException e) {
      log.debug(LOG_CATEGORY, "ActionDeleteTemplateDefinitionInstance - execute: content group '" + ctntGroup + "' is locked.");
      this.setErrorCode("delete_instance", ActionConstants.ACTION_CUSTOM_VALIDATION_ERROR_11);
    }

    // delete failed
    if (!deleteCtntGroup) {
      this.setActionForwardName("nok");
      this.setErrorCode(Integer.toString(ACTION_EXECUTE_FAILED));
    }

    return true;
  }

  public boolean validate(CoaSession session, WebletRequest request) {
    log.debug(LOG_CATEGORY, "ActionDeleteTemplateDefinitionInstance - validate: Reached.");

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
