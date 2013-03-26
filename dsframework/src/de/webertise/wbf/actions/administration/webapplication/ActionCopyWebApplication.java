/*
 * ActionInit.java
 *
 * Created on 15. März 2003, 15:26
 */

package de.webertise.wbf.actions.administration.webapplication;

import java.util.Enumeration;
import java.util.Hashtable;
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
public class ActionCopyWebApplication extends de.webertise.wbf.base.action.AbstractAction {

  /** Creates a new instance of ActionInit */
  public ActionCopyWebApplication() {
  }

  public boolean execute(CoaSession session, WebletRequest request) {
    log.debug(LOG_CATEGORY, "ActionCopyWebApplication - execute: Reached.");

    // get content
    ContentServices cs = new ContentServices();
    CoaContent ctnt = cs.getContent(session, MasterWebletConstants.WBF_PROJECT_NAME, "", MasterWebletConstants.WBF_WEB_APPLICATION_SETTINGS_CONTENT_NAME);
    if (ctnt != null) {

      // copy application
      String sourceApplName = (String) this.getRequestParameter("sourceApplName");
      String targetApplName = (String) this.getRequestParameter("targetApplName");
      Hashtable<String, String> attrList = ContentServices.getContentAttributesAsHashtable(session, "webApplicationSettings.applications." + sourceApplName, MasterWebletConstants.WBF_PROJECT_NAME,
          MasterWebletConstants.WBF_WEB_APPLICATION_SETTINGS_CONTENT_NAME, Locale.ENGLISH);

      if (attrList.size() > 0) {
        Enumeration<String> attrListEnum = attrList.keys();
        while(attrListEnum.hasMoreElements()) {
          String path = attrListEnum.nextElement();
          String value = attrList.get(path);
          if (value != null) {
            String[] values = value.split(";");
            ctnt.setAttribute(path.replace(sourceApplName, targetApplName), Locale.ENGLISH, values);
          } else {
            ctnt.setAttribute(path.replace(sourceApplName, targetApplName), Locale.ENGLISH, "");
          }
        }

        // update content
        cs.updateContent(session, ctnt);

        // set session flag for re-initialization of webmodule settings
        session.setAttribute(MasterWebletConstants.RELOAD_SETTINGS_SESSION_PARAMETER_NAME, "true");

        // action ok
        this.setActionForwardName("ok");
        this.setErrorCode(Integer.toString(ACTION_EXECUTE_RESULT_OK));
        this.setResponseParameter(ActionResponseItem.TARGET_SESSION, "webapplication.settings.copy", "true", false, false);

      } else {
        // action ok
        this.setActionForwardName("nok");
        this.setErrorCode(Integer.toString(ACTION_EXECUTE_FAILED));
        this.setResponseParameter(ActionResponseItem.TARGET_SESSION, "webapplication.settings.copy", "false", false, false);
      }

    } else {
      // action failed - show error
      super.setActionForwardName("nok");
      this.setErrorCode(Integer.toString(ACTION_EXECUTE_FAILED));
      this.setResponseParameter(ActionResponseItem.TARGET_SESSION, "webapplication.settings.copy", "false", false, false);
    }

    return true;

  }

  public boolean validate(CoaSession session, WebletRequest request) {
    log.debug(LOG_CATEGORY, "ActionCopyWebApplication - validate: Reached.");

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
