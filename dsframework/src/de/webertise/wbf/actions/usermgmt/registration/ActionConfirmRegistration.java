/*
 * ActionInit.java
 *
 * Created on 15. März 2003, 15:26
 */

package de.webertise.wbf.actions.usermgmt.registration;

import de.reddot.api.common.attribute.CoaAttribute;
import de.reddot.api.common.session.CoaSession;
import de.reddot.api.common.user.CoaUser;
import de.reddot.api.common.user.CoaUserController;
import de.reddot.api.web.io.WebletRequest;
import de.webertise.wbf.base.action.ActionResponseItem;

/**
 * 
 * @author bernfried howe
 */
public class ActionConfirmRegistration extends de.webertise.wbf.base.action.AbstractAction {

  /** Creates a new instance of ActionInit */
  public ActionConfirmRegistration() {
  }

  public boolean execute(CoaSession session, WebletRequest request) {

    log.debug(LOG_CATEGORY, "ActionConfirmRegistration - execute: Reached.");

    // create new user object
    CoaUser user = CoaUserController.selectUser((String) this.getRequestParameter("login"));

    // check token
    CoaAttribute attrToken = user.getCoaAttribute("profile.token.registration.confirmation");
    if (attrToken != null && attrToken.getValue().equals((String) this.getRequestParameter("token"))) {
      user.setStatus(CoaUser.STATUS_ACTIVE);
      user.setCoaAttribute("profile.activated", "" + System.currentTimeMillis());
      user.setCoaAttribute("profile.token.registration.confirmation", "");
      CoaUserController.updateUser(user);

      // action ok
      this.setResponseParameter(ActionResponseItem.TARGET_GLOBAL_MESSAGE, ActionResponseItem.GLOBAL_MESSAGE_SUCCESS_KEY, "registration.confirmation.succeeded", false, false);
      this.setErrorCode(Integer.toString(ACTION_EXECUTE_RESULT_OK));
      this.setActionForwardName("ok");
    } else {
      // action ok
      this.setResponseParameter(ActionResponseItem.TARGET_GLOBAL_MESSAGE, ActionResponseItem.GLOBAL_MESSAGE_SUCCESS_KEY, "registration.confirmation.failed", false, false);
      this.setErrorCode(Integer.toString(ACTION_EXECUTE_FAILED));
      this.setActionForwardName("nok");
    }

    return true;
  }

  public boolean validate(CoaSession session, WebletRequest request) {
    log.debug(LOG_CATEGORY, "ActionEditRegistration - validate: Reached.");

    // get request parameter
    boolean valid = false;
    valid = validateAllRequestParameters(session, request);

    // check if validation failed
    if (!valid) {
      this.setErrorCode(Integer.toString(ACTION_VALIDATION_RESULT_NOK));
      this.setActionForwardName("nok");
      return false;
    } else {
      return true;
    }
  }

}
