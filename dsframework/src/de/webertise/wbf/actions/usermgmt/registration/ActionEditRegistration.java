/*
 * ActionInit.java
 *
 * Created on 15. März 2003, 15:26
 */

package de.webertise.wbf.actions.usermgmt.registration;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import de.reddot.api.common.content.CoaContent;
import de.reddot.api.common.content.CoaContentData;
import de.reddot.api.common.session.CoaSession;
import de.reddot.api.common.user.CoaUser;
import de.reddot.api.common.user.CoaUserController;
import de.reddot.api.common.user.CoaUserGroup;
import de.reddot.api.web.io.WebletRequest;
import de.webertise.ds.services.ContentServices;
import de.webertise.ds.services.DynaMentServices;
import de.webertise.wbf.base.action.ActionResponseItem;
import de.webertise.wbf.services.FreemarkerEngine;
import de.webertise.wbf.services.MD5Hash;
import de.webertise.wbf.weblet.MasterWeblet;
import de.webertise.wbf.actions.ActionConstants;

/**
 * 
 * @author bernfried howe
 */
public class ActionEditRegistration extends de.webertise.wbf.base.action.AbstractAction {

  /** Creates a new instance of ActionInit */
  public ActionEditRegistration() {
  }

  public boolean execute(CoaSession session, WebletRequest request) {

    log.debug(LOG_CATEGORY, "ActionEditRegistration - execute: Reached.");

    // create new user object
    CoaUser user = CoaUserController.getNewUser((String) this.getRequestParameter("login"));

    // set properties
    user.setPassword((String) this.getRequestParameter("password"));
    user.setName((String) this.getRequestParameter("firstName") + " " + (String) this.getRequestParameter("lastName"));
    user.setLastName((String) this.getRequestParameter("lastName"));
    user.setFirstName((String) this.getRequestParameter("firstName"));
    user.setEmail((String) this.getRequestParameter("email"));
    user.setLanguage((String) session.getAttribute("rdeCurrentLocale"));
    user.setStatus(CoaUser.STATUS_DISABLED);

    // set properties
    user.setCoaAttribute("profile.greeting", (String) this.getRequestParameter("greeting"));
    user.setCoaAttribute("profile.title", (String) this.getRequestParameter("title"));
    user.setCoaAttribute("profile.firstName", (String) this.getRequestParameter("firstName"));
    user.setCoaAttribute("profile.lastName", (String) this.getRequestParameter("lastName"));
    user.setCoaAttribute("profile.email", (String) this.getRequestParameter("email"));
    user.setCoaAttribute("profile.timezone", (String) this.getRequestParameter("timezone"));
    user.setCoaAttribute("rde.locale", (String) session.getAttribute("rdeCurrentLocale"));

    // find out which roles and user groups need to be assigned for ths registration
    String applName = request.getParameter("wbf.requestctrldata.application");
    String roles[] = MasterWeblet.applicationSetting.getApplication(applName).getRoles();
    if (roles!=null) {
      for(int i=0; i<roles.length; i++) {
        user.setRole(roles[i]);
      }
    }
    String groupsStr = (String) this.getRequestParameter("groups");
    log.debug(LOG_CATEGORY, "ActionEditRegistration - execute: Groups '" + groupsStr + "'");
    if (groupsStr!=null) {
      String[] groups = groupsStr.split(";");
      for(int i=0; i<groups.length; i++) {
        log.debug(LOG_CATEGORY, "ActionEditRegistration - execute: Group '" + groups[i] + "'");
        CoaUserGroup group = CoaUserController.selectUserGroup(groups[i]);
        user.addToGroup(group);
      }
    }

    // create token for confirmation process
    String hashStr = (String) this.getRequestParameter("email") + System.currentTimeMillis();
    String md5Str = MD5Hash.getHash(hashStr);
    user.setCoaAttribute("profile.token.registration.confirmation", md5Str);

    // send confirmation email
    boolean sendOk = sendConfirmationEmail(session, request, user, md5Str);
    if (sendOk) {
      // write user to repository
      CoaUserController.insertUser(user);
      // action ok
      this.setResponseParameter(ActionResponseItem.TARGET_GLOBAL_MESSAGE, ActionResponseItem.GLOBAL_MESSAGE_SUCCESS_KEY, "registration.succeeded", false, false);
      this.setErrorCode(Integer.toString(ACTION_EXECUTE_RESULT_OK));
      this.setActionForwardName("ok");
    } else {
      // action failed
      this.setErrorCode(Integer.toString(ACTION_EXECUTE_FAILED));
      this.setActionForwardName("nok");
      log.warn(LOG_CATEGORY, "ActionEditRegistration - execute: sending confirmation email failed for new user with login '" + user.getLogin() + "'.");

      // get timezones
      List<String> tmList = Arrays.asList(TimeZone.getAvailableIDs());
      this.setResponseParameter(ActionResponseItem.TARGET_SESSION_TRANSIENT, "registration.timezones", tmList, false, false);

    }
    return true;
  }

  public boolean validate(CoaSession session, WebletRequest request) {
    log.debug(LOG_CATEGORY, "ActionEditRegistration - validate: Reached.");

    // get request parameter
    boolean valid = false;
    valid = validateAllRequestParameters(session, request);

    // check if user already exists
    String login = (String) this.getRequestParameter("login");
    if (login != null && !login.equals("") && CoaUserController.existsUser(login) != CoaUserController.RESULT_NOT_FOUND) {
      this.setErrorCode("login", ActionConstants.ACTION_CUSTOM_VALIDATION_ERROR_10);
      valid = false;
    } else {
      // check if password and password 2 are identical
      String p1 = (String) this.getRequestParameter("password");
      String p2 = (String) this.getRequestParameter("password2");
      if (p1 != null && p2 != null && !p1.equals(p2)) {
        this.setErrorCode("password", ActionConstants.ACTION_CUSTOM_VALIDATION_ERROR_10);
        this.setErrorCode("password2", ActionConstants.ACTION_CUSTOM_VALIDATION_ERROR_10);
        valid = false;
      }
    }

    // check if validation failed
    if (!valid) {
      this.removeRequestParameter("password");
      this.removeRequestParameter("password2");
      this.setErrorCode(Integer.toString(ACTION_VALIDATION_RESULT_NOK));
      this.setActionForwardName("nok");
      // get timezones
      List<String> tmList = Arrays.asList(TimeZone.getAvailableIDs());
      this.setResponseParameter(ActionResponseItem.TARGET_SESSION_TRANSIENT, "registration.timezones", tmList, false, false);
      return false;
    } else {
      return true;
    }
  }

  private boolean sendConfirmationEmail(CoaSession session, WebletRequest request, CoaUser user, String md5Str) {

    // get application name
    String applName = request.getParameter("wbf.requestctrldata.application");
    String project = request.getParameter("wbf.requestctrldata.project");

    Locale locale = new Locale(user.getLanguage());
    ContentServices cs = new ContentServices();
    CoaContent ctnt = cs.getContent(session, project, "templates/emails/", "confirm-registration.ftl");
    if (ctnt != null) {
      CoaContentData ctntData = ctnt.getCoaContentData(locale, CoaContent.LOCALE_POLICY_DEFAULT, false);
      if (ctntData != null) {

        // get custom personalized body text
        Map<String, String> data = new Hashtable<String, String>();
        data.put("lastName", (String) this.getRequestParameter("lastName"));
        data.put("login", (String) this.getRequestParameter("login"));
        data.put("greeting", (String) this.getRequestParameter("greeting"));
        data.put("rdeHttpServer", (String) request.getParameter("rdeHttpServer"));
        data.put("rdePrefix", (String) request.getParameter("rdePrefix"));
        data.put("project", "project");
        data.put("token", md5Str);
        String bodyTextReplaced = FreemarkerEngine.executeTemplate(ctntData.getText(), data);
        log.trace(LOG_CATEGORY, "ActionEditRegistration - sendConfirmationEmail: Email text generated: '" + bodyTextReplaced + "'");

        // set parameter for message service
        String smtpConnector = MasterWeblet.applicationSetting.getApplication(applName).getCustomSetting(applName + ".smtp-connector-alias");
        log.trace(LOG_CATEGORY, "ActionEditRegistration - sendConfirmationEmail: Custom Setting for smpt-connector: '" + smtpConnector + "'");
        Hashtable<String, String> settings = new Hashtable<String, String>();
        settings.put("mode", "update");
        settings.put("connector", smtpConnector);
        settings.put("sender", ctnt.getAttributeValue("email-sender", locale));
        settings.put("recipients", (String) this.getRequestParameter("email"));
        settings.put("subject", ctnt.getAttributeValue("subject", locale));
        settings.put("bodyType", DynaMentServices.MESSAGE_DYNAMENT_BODY_TYPE_HTML);
        settings.put("bodyText", bodyTextReplaced);
        settings.put("sendType", DynaMentServices.MESSAGE_DYNAMENT_SEND_TYPE_DIRECT);

        // execute sql statement
        DynaMentServices dmServices = new DynaMentServices();
        return dmServices.executeMessageDynaMent(session, settings);

      } else {
        log.debug(LOG_CATEGORY, "ActionEditRegistration - sendConfirmationEmail: email body template with name '/templates/emails/confirm-registration.ftl' has no data for locale '" + locale + "'.");
        return false;
      }
    } else {
      log.debug(LOG_CATEGORY, "ActionEditRegistration - sendConfirmationEmail: email body template with name '/templates/emails/confirm-registration.ftl' was not found.");
      return false;
    }
  }

}
