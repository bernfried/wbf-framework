package de.webertise.wbf.actions.usermgmt.auth;

import java.util.Enumeration;

import de.reddot.api.ObjectNotFoundException;
import de.reddot.api.common.session.CoaSession;
import de.reddot.api.common.user.CoaUser;
import de.reddot.api.common.user.CoaUserController;
import de.reddot.api.common.user.CoaUserGroup;
import de.reddot.api.web.io.WebletRequest;
import de.webertise.wbf.actions.ActionConstants;
import de.webertise.wbf.base.action.ActionResponseItem;
import de.webertise.wbf.weblet.MasterWeblet;

/**
 * 
 * @author bernfried howe
 */
public class ActionLogin extends de.webertise.wbf.base.action.AbstractAction {

  /** Creates a new instance of ActionInit */
  public ActionLogin() {
  }

  public boolean execute(CoaSession session, WebletRequest request) {

    String login = (String) this.getRequestParameter("login");
    String password = (String) this.getRequestParameter("password");
    String language = (String) this.getRequestParameter("language");

    // get general request parameter
    String applName = request.getParameter("wbf.requestctrldata.application");

    log.debug(LOG_CATEGORY, "ActionLogin - execute: Reached.");

    // get user first to check user groups and roles
    CoaUser user = null;
    if (login != null && !login.equals("")) {
      log.debug(LOG_CATEGORY, "ActionLogin - execute: Login = '" + login + "'");
      try {
        user = CoaUserController.selectUser(login);
      } catch (ObjectNotFoundException e) {
        log.debug(LOG_CATEGORY, "ActionLogin - execute: ObjectNotFoundException for Login = '" + login + "'");
      }
    }

    // check user
    if (user != null) {
      String roles = "";
      @SuppressWarnings("unchecked")
      Enumeration<String> roleEnum = user.getRolesEnumeration();
      while(roleEnum.hasMoreElements()) {
        String role = roleEnum.nextElement();
        roles = roles + "[" + role + "]";
      }
      if (!MasterWeblet.applicationSetting.getApplication(applName).checkUserRoles(roles)) {
        // an error occured: lets check 'result'
        this.setActionForwardName("nok");
        this.setErrorCode(ActionConstants.ACTION_CUSTOM_VALIDATION_ERROR_11);
      } else {
        // login against DS
        int result = session.assignUser(login, password);
        log.debug(LOG_CATEGORY, "ActionLogin - execute - Login result: " + String.valueOf(result));

        if (result == CoaSession.USERAUTH_OK) {
          // the new user is now assigned to the session
          request.setParameter("rdeLocaleAttr", language);

          // get user groups for authorization of dialog
          String userGroups = "";
          @SuppressWarnings("unchecked")
          Enumeration<CoaUserGroup> userEnum = session.getCoaUser().getGroupsEnumeration();
          while(userEnum.hasMoreElements()) {
            CoaUserGroup group = (CoaUserGroup) userEnum.nextElement();
            userGroups = userGroups.concat(group.getPath());
          }
          String userName = session.getCoaUser().getName();
          this.setResponseParameter(ActionResponseItem.TARGET_SESSION, "userName", userName, false, false);
          this.setErrorCode(Integer.toString(ACTION_EXECUTE_RESULT_OK));
          this.setActionForwardName("ok");
        } else {
          // an error occured: lets check 'result'
          this.setActionForwardName("nok");
          this.setErrorCode(ActionConstants.ACTION_CUSTOM_VALIDATION_ERROR_10);
        }
      }
      this.setResponseParameter(ActionResponseItem.TARGET_SESSION_TRANSIENT, "login", login, false, false);

    } else {
      // an error occured: lets check 'result'
      this.setActionForwardName("nok");
      this.setErrorCode(ActionConstants.ACTION_CUSTOM_VALIDATION_ERROR_10);
    }

    return true;
  }

  public boolean validate(CoaSession session, WebletRequest request) {
    log.debug(LOG_CATEGORY, "ActionLogin - validate: Reached.");

    // get request parameter
    boolean valid = false;
    valid = validateAllRequestParameters(session, request);

    if (!valid) {
      this.removeRequestParameter("password");
      this.setResponseParameter(ActionResponseItem.TARGET_SESSION_TRANSIENT, "login", (String) this.getRequestParameter("login"), false, false);
      this.setErrorCode(Integer.toString(ACTION_VALIDATION_RESULT_NOK));
      super.setActionForwardName("nok");
      return false;
    } else {
      this.setErrorCode(Integer.toString(ACTION_VALIDATION_RESULT_OK));
      return true;
    }
  }
}
