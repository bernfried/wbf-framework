/*
 * ActionConfirmForgotPassword.java
 *
 * Created on 15. März 2003, 15:26
 */

package de.webertise.wbf.actions.usermgmt.auth;

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
public class ActionConfirmForgotPassword extends de.webertise.wbf.base.action.AbstractAction {

  /** Creates a new instance of ActionConfirmForgotPassword */
  public ActionConfirmForgotPassword() {
  }

  public boolean execute(CoaSession session, WebletRequest request) {

    log.debug(LOG_CATEGORY, "ActionConfirmForgotPassword - execute: Reached.");

    // create new user object
    String login = (String) this.getRequestParameter("login");
    CoaUser user = CoaUserController.selectUser(login);

    // check token
    CoaAttribute attrToken = user.getCoaAttribute("profile.token.forgotpassword.confirmation");
    if (attrToken != null && attrToken.getValue().equals((String) this.getRequestParameter("token"))) {

      int result = session.assignUserTrusted(login);
      if (result == 0) {
        // action ok
        user.setCoaAttribute("profile.token.forgotpassword.confirmation", "");
        CoaUserController.updateUser(user);
        this.setResponseParameter(ActionResponseItem.TARGET_GLOBAL_MESSAGE, ActionResponseItem.GLOBAL_MESSAGE_SUCCESS_KEY, "forgotpassword.confirmation.succeeded", false, false);
        this.setErrorCode(Integer.toString(ACTION_EXECUTE_RESULT_OK));
        this.setActionForwardName("ok");
      } else {
        log.debug(LOG_CATEGORY, "ActionConfirmForgotPassword - execute: assign user as trusted failed (RC: " + result + ")");
        // action nok
        this.setResponseParameter(ActionResponseItem.TARGET_GLOBAL_MESSAGE, ActionResponseItem.GLOBAL_MESSAGE_ERROR_KEY, "forgotpassword.confirmation.failed", false, false);
        this.setErrorCode(Integer.toString(ACTION_EXECUTE_FAILED));
        this.setActionForwardName("nok");
      }

    } else {
      log.debug(LOG_CATEGORY, "ActionConfirmForgotPassword - execute: invalid token.");
      // action ok
      this.setResponseParameter(ActionResponseItem.TARGET_GLOBAL_MESSAGE, ActionResponseItem.GLOBAL_MESSAGE_ERROR_KEY, "forgotpassword.confirmation.failed", false, false);
      this.setErrorCode(Integer.toString(ACTION_EXECUTE_FAILED));
      this.setActionForwardName("nok");
    }

    return true;
  }

  public boolean validate(CoaSession session, WebletRequest request) {
    log.debug(LOG_CATEGORY, "ActionConfirmForgotPassword - validate: Reached.");

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
