/*
 * ActionInit.java
 *
 * Created on 15. März 2003, 15:26
 */

package de.webertise.wbf.actions.usermgmt.profile;

import java.util.List;
import java.util.TimeZone;

import de.reddot.api.common.session.CoaSession;
import de.reddot.api.common.user.CoaUser;
import de.reddot.api.common.user.CoaUserController;
import de.reddot.api.web.io.WebletRequest;
import de.webertise.wbf.actions.ActionConstants;
import de.webertise.wbf.base.action.ActionResponseItem;
import edu.emory.mathcs.backport.java.util.Arrays;

/**
 * 
 * @author bernfried howe
 */
public class ActionEditProfile extends de.webertise.wbf.base.action.AbstractAction {

  /** Creates a new instance of ActionInit */
  public ActionEditProfile() {
  }

  public boolean execute(CoaSession session, WebletRequest request) {

    log.debug(LOG_CATEGORY, "ActionEditProfile - execute: Reached.");

    // write changes to user
    CoaUser user = session.getCoaUser();

    // user attributes
    user.setCoaAttribute("profile.firstName", (String) this.getRequestParameter("firstName"));
    user.setCoaAttribute("profile.lastName", (String) this.getRequestParameter("lastName"));
    user.setCoaAttribute("profile.email", (String) this.getRequestParameter("email"));
    user.setCoaAttribute("profile.timezone", (String) this.getRequestParameter("timezone"));
    user.setCoaAttribute("rde.locale", (String) this.getRequestParameter("language"));

    // user properties
    user.setName((String) this.getRequestParameter("firstName") + " " + (String) this.getRequestParameter("lastName"));
    user.setLastName((String) this.getRequestParameter("lastName"));
    user.setFirstName((String) this.getRequestParameter("firstName"));
    user.setEmail((String) this.getRequestParameter("email"));
    user.setLanguage((String) this.getRequestParameter("language"));

    // if password should be changed (not empty)
    String password = (String) this.getRequestParameter("password");
    if (!password.equals(""))
      user.setPassword(password);

    // write user to repository
    CoaUserController.updateUser(user);

    // get data for form
    fillActionResponse();

    // action ok
    this.setErrorCode(Integer.toString(ACTION_EXECUTE_RESULT_OK));
    this.setActionForwardName("ok");
    return true;

  }

  public boolean validate(CoaSession session, WebletRequest request) {
    log.debug(LOG_CATEGORY, "ActionEditProfile - validate: Reached.");

    // get request parameter
    boolean valid = false;
    valid = validateAllRequestParameters(session, request);

    // check if password and password 2 are identical
    String p1 = (String) this.getRequestParameter("password");
    String p2 = (String) this.getRequestParameter("password2");
    if (!p1.equals(p2)) {
      this.setErrorCode("password2", ActionConstants.ACTION_CUSTOM_VALIDATION_ERROR_10);
      this.setErrorCode(Integer.toString(ACTION_VALIDATION_RESULT_NOK));
      valid = false;
    }

    // check if validation failed
    if (!valid) {
      this.setErrorCode(Integer.toString(ACTION_VALIDATION_RESULT_NOK));
      super.setActionForwardName("nok");

      // get data for form
      fillActionResponse();

      return false;
    } else {
      return true;
    }

  }

  private void fillActionResponse() {

    // remove password values from request (response)
    this.removeRequestParameter("password");
    this.removeRequestParameter("password2");

    // return validated request parameter
    this.setResponseParameter(ActionResponseItem.TARGET_SESSION_TRANSIENT, "myprofile.request", this.getRequest(), false, false);

    // get timezones
    @SuppressWarnings("unchecked")
    List<String> tmList = Arrays.asList(TimeZone.getAvailableIDs());
    this.setResponseParameter(ActionResponseItem.TARGET_SESSION_TRANSIENT, "myprofile.timezones", tmList, false, false);

  }

}
