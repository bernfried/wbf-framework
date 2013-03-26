/*
 * ActionInit.java
 *
 * Created on 15. März 2003, 15:26
 */

package de.webertise.wbf.actions.administration.webmodule;

import java.util.Enumeration;
import java.util.Locale;

import de.reddot.api.common.content.CoaContent;
import de.reddot.api.common.session.CoaSession;
import de.reddot.api.web.io.WebletRequest;
import de.webertise.ds.services.ContentServices;
import de.webertise.wbf.base.action.ActionResponseItem;
import de.webertise.wbf.weblet.MasterWebletConstants;

/**
 * 
 * @author bernfried howe
 */
public class ActionEditWebModuleSettings extends de.webertise.wbf.base.action.AbstractAction {

  /** Creates a new instance of ActionInit */
  public ActionEditWebModuleSettings() {
  }

  public boolean execute(CoaSession session, WebletRequest request) {
    log.debug(LOG_CATEGORY, "ActionEditWebModuleSettings - execute: Reached.");

    // get content
    ContentServices cs = new ContentServices();
    CoaContent ctnt = cs.getContent(session, MasterWebletConstants.WBF_PROJECT_NAME, "", MasterWebletConstants.WBF_WEB_MODULE_SETTINGS_CONTENT_NAME);
    if (ctnt != null) {
      // write all request parameter to corresponding content attributes
      Enumeration<String> keys = this.getRequest().keys();
      while(keys.hasMoreElements()) {
        String key = keys.nextElement();
        ctnt.setAttribute("webModuleSettings.general." + key, Locale.ENGLISH, (String) this.getRequestParameter(key));
      }
      // update content
      cs.updateContent(session, ctnt);
      // set session flag for re-initialization of webmodule settings
      session.setAttribute(MasterWebletConstants.RELOAD_SETTINGS_SESSION_PARAMETER_NAME, "true");
      // action ok
      this.setActionForwardName("ok");
      this.setErrorCode(Integer.toString(ACTION_EXECUTE_RESULT_OK));
      this.setResponseParameter(ActionResponseItem.TARGET_SESSION, "webmodule.settings.edit", "true", false, false);

    } else {
      // acion failed - show error
      super.setActionForwardName("nok");
      this.setErrorCode(Integer.toString(ACTION_EXECUTE_FAILED));
    }

    return true;

  }

  public boolean validate(CoaSession session, WebletRequest request) {
    log.debug(LOG_CATEGORY, "ActionEditWebModuleSettings - validate: Reached.");

    // get request parameter
    boolean valid = false;
    valid = validateAllRequestParameters(session, request);

    // check if validation failed
    if (!valid) {
      this.setErrorCode(Integer.toString(ACTION_VALIDATION_RESULT_NOK));
      super.setActionForwardName("nok");
      return false;
    } else {
      return true;
    }

  }

}
