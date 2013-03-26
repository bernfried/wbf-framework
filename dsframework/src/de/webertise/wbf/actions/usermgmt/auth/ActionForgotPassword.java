/*
 * ActionForgotPassword.java
 *
 * Created on 15. März 2003, 15:26
 */

package de.webertise.wbf.actions.usermgmt.auth;

import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;

import de.reddot.api.common.content.CoaContent;
import de.reddot.api.common.content.CoaContentData;
import de.reddot.api.common.session.CoaSession;
import de.reddot.api.common.user.CoaUser;
import de.reddot.api.common.user.CoaUserController;
import de.reddot.api.web.io.WebletRequest;
import de.webertise.ds.services.ContentServices;
import de.webertise.ds.services.DynaMentServices;
import de.webertise.wbf.actions.ActionConstants;
import de.webertise.wbf.base.action.ActionResponseItem;
import de.webertise.wbf.services.FreemarkerEngine;
import de.webertise.wbf.services.MD5Hash;
import de.webertise.wbf.weblet.MasterWeblet;

/**
 * 
 * @author bernfried howe
 */
public class ActionForgotPassword extends de.webertise.wbf.base.action.AbstractAction {

  /** Creates a new instance of ActionForgotPassword */
  public ActionForgotPassword() {
  }

  public boolean execute(CoaSession session, WebletRequest request) {

    log.debug(LOG_CATEGORY, "ActionForgotPassword - execute: Reached.");

    // create new user object
    CoaUser user = CoaUserController.selectUser((String) this.getRequestParameter("login"));

    // create token for confirmation process
    String hashStr = (String) this.getRequestParameter("login") + System.currentTimeMillis();
    String md5Str = MD5Hash.getHash(hashStr);
    user.setCoaAttribute("profile.token.forgotpassword.confirmation", md5Str);
    CoaUserController.updateUser(user);

    // send confirmation email
    boolean sendOk = sendConfirmationEmail(session, request, user, md5Str);
    if (sendOk) {
      // action ok
      this.setResponseParameter(ActionResponseItem.TARGET_GLOBAL_MESSAGE, ActionResponseItem.GLOBAL_MESSAGE_SUCCESS_KEY, "forgotpassword.succeeded", false, false);
      this.setErrorCode(Integer.toString(ACTION_EXECUTE_RESULT_OK));
      this.setActionForwardName("ok");
    } else {
      // action failed
      this.setResponseParameter(ActionResponseItem.TARGET_GLOBAL_MESSAGE, ActionResponseItem.GLOBAL_MESSAGE_SUCCESS_KEY, "forgotpassword.failed", false, false);
      this.setErrorCode(Integer.toString(ACTION_EXECUTE_FAILED));
      this.setActionForwardName("nok");
      log.warn(LOG_CATEGORY, "ActionForgotPassword - execute: sending confirmation email failed for user with login '" + user.getLogin() + "'.");
    }
    return true;
  }

  /**
   * Validate Action Request
   */
  public boolean validate(CoaSession session, WebletRequest request) {
    log.debug(LOG_CATEGORY, "ActionForgotPassword - validate: Reached.");

    // get request parameter
    boolean valid = false;
    valid = validateAllRequestParameters(session, request);

    // check if user already exists
    if (valid) {
      String login = (String) this.getRequestParameter("login");
      if (login != null && !login.equals("") && CoaUserController.existsUser(login) == CoaUserController.RESULT_NOT_FOUND) {
        this.setErrorCode("login", ActionConstants.ACTION_CUSTOM_VALIDATION_ERROR_10);
        this.setErrorCode(Integer.toString(ACTION_VALIDATION_RESULT_NOK));
        this.setActionForwardName("nok");
        return false;
      } else {
        return true;
      }
    } else {
      this.setErrorCode(Integer.toString(ACTION_VALIDATION_RESULT_NOK));
      this.setActionForwardName("nok");
      return false;
    }
  }

  private boolean sendConfirmationEmail(CoaSession session, WebletRequest request, CoaUser user, String md5Str) {

    // get application name
    String applName = request.getParameter("wbf.requestctrldata.application");
    String project = request.getParameter("wbf.requestctrldata.project");

    Locale locale = new Locale(user.getLanguage());
    ContentServices cs = new ContentServices();
    CoaContent ctnt = cs.getContent(session, project, "templates/emails/", "confirm-forgot-password.ftl");
    if (ctnt != null) {
      CoaContentData ctntData = ctnt.getCoaContentData(locale, CoaContent.LOCALE_POLICY_DEFAULT, false);
      if (ctntData != null) {

        // get custom personalized body text
        Map<String, String> data = new Hashtable<String, String>();
        data.put("login", (String) this.getRequestParameter("login"));
        data.put("rdeHttpServer", (String) request.getParameter("rdeHttpServer"));
        data.put("rdePrefix", (String) request.getParameter("rdePrefix"));
        data.put("project", project);
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
        settings.put("recipients", (String) user.getCoaAttribute("profile.email").getValue());
        settings.put("subject", ctnt.getAttributeValue("subject", locale));
        settings.put("bodyType", DynaMentServices.MESSAGE_DYNAMENT_BODY_TYPE_HTML);
        settings.put("bodyText", bodyTextReplaced);
        settings.put("sendType", DynaMentServices.MESSAGE_DYNAMENT_SEND_TYPE_DIRECT);

        // execute sql statement
        DynaMentServices dmServices = new DynaMentServices();
        return dmServices.executeMessageDynaMent(session, settings);

      } else {
        log.debug(LOG_CATEGORY, "ActionEditRegistration - sendConfirmationEmail: email body template with name '/templates/emails/confirm-forgot-password.ftl' has no data for locale '" + locale
            + "'.");
        return false;
      }
    } else {
      log.debug(LOG_CATEGORY, "ActionEditRegistration - sendConfirmationEmail: email body template with name '/templates/emails/confirm-forgot-password.ftl' was not found.");
      return false;
    }
  }

}
