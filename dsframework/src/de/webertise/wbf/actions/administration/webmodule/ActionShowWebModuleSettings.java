package de.webertise.wbf.actions.administration.webmodule;

import java.util.Hashtable;

import de.reddot.api.common.session.CoaSession;
import de.reddot.api.web.io.WebletRequest;
import de.webertise.wbf.base.action.ActionResponseItem;
import de.webertise.wbf.base.webmodule.WebModuleSetting;
import de.webertise.wbf.weblet.MasterWebletConstants;

/**
 * 
 * @author bernfried howe
 */
public class ActionShowWebModuleSettings extends de.webertise.wbf.base.action.AbstractAction {

  /** Creates a new instance of ActionShowWebModuleSettings */
  public ActionShowWebModuleSettings() {
  }

  public boolean execute(CoaSession session, WebletRequest request) {
    log.debug(LOG_CATEGORY, "ActionShowWebModuleSettings - execute: Reached.");

    // *************************************************************************
    // * read the web module settings
    // *************************************************************************
    WebModuleSetting wms = new WebModuleSetting();
    boolean resultWebModuleSettings = wms.readWebModuleSettingsFromContent(session, MasterWebletConstants.WBF_PROJECT_NAME, MasterWebletConstants.WBF_WEB_MODULE_SETTINGS_CONTENT_NAME);
    if (!resultWebModuleSettings) {
      log.debug(LOG_CATEGORY, "ActionShowWebModuleSettings - execute: Reading the Web Module Settings failed.");
      this.setErrorCode(Integer.toString(ACTION_EXECUTE_FAILED));
      this.setActionForwardName("nok");
    } else {
      log.debug(LOG_CATEGORY, "ActionShowWebModuleSettings - execute: Reading the Web Module Settings succeeded.");

      // fill action response
      Hashtable<String, String> settings = new Hashtable<String, String>();

      settings.put("actionParameterName", wms.getActionParameterName());
      settings.put("applicationParameterName", wms.getApplicationParameterName());
      settings.put("webModuleParameterName", wms.getWebModuleParameterName());
      settings.put("projectParameterName", wms.getProjectParameterName());
      settings.put("refererPageParameterName", wms.getRefererPageParameterName());

      settings.put("defaultApplication", wms.getDefaultApplication());
      settings.put("defaultProject", wms.getDefaultProject());
      settings.put("defaultWebModule", wms.getDefaultWebModule());

      if (wms.isDebug()) {
        settings.put("debugMode", "on");
      } else {
        settings.put("debugMode", "off");
      }

      if (wms.isWbfDefaultWeblet()) {
        settings.put("wbfDefaultWeblet", "on");
      } else {
        settings.put("wbfDefaultWeblet", "off");
      }

      this.setResponseParameter(ActionResponseItem.TARGET_SESSION_TRANSIENT, "action_request_initial", settings, false, false);
      this.setErrorCode(Integer.toString(ACTION_EXECUTE_RESULT_OK));
      this.setActionForwardName("ok");
    }

    return true;
  }

  public boolean validate(CoaSession session, WebletRequest request) {
    log.debug(LOG_CATEGORY, "ActionShowWebModuleSettings - validate: Reached.");

    return true;
  }

}
