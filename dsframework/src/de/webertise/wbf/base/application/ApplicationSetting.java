/*
 * ActionModuleSetting.java
 *
 * Created on 15. März 2003, 10:18
 */

package de.webertise.wbf.base.application;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;
import de.reddot.api.common.content.CoaContent;
import de.reddot.api.common.content.CoaContentController;
import de.reddot.api.common.content.CoaContentControllerFactory;
import de.reddot.api.common.session.CoaSession;
import de.reddot.api.exception.AccessNotGrantedException;
import de.webertise.ds.services.LogServices;
import de.webertise.wbf.base.action.ActionForwardConstants;
import de.webertise.wbf.base.action.ActionForwardDef;
import de.webertise.wbf.base.action.ActionMapping;
import de.webertise.wbf.base.action.ActionValidationRule;

/**
 * 
 * @author bernfried howe
 */
public class ApplicationSetting {

  private final static String            LOG_CATEGORY = "wbf_settings";

  private boolean                        created      = false;
  private Hashtable<String, Application> applications = new Hashtable<String, Application>();

  private String                         initAction   = "";
  private String                         loginAction  = "";
  private String                         logoutAction = "";

  private LogServices                    log          = LogServices.getInstance(LOG_CATEGORY);

  /** Creates a new instance of ActionModuleSetting */
  public ApplicationSetting() {
  }

  /**************************************************************************
   * Getter
   *************************************************************************/
  public boolean getCreated() {
    return this.created;
  }

  public Application getApplication(String applicationName) {
    return (Application) this.applications.get(applicationName);
  }

  public Enumeration<String> getApplicationNames() {
    return this.applications.keys();
  }

  public String getInitAction() {
    return this.initAction;
  }

  public String getLoginAction() {
    return this.loginAction;
  }

  public String getLogoutAction() {
    return this.logoutAction;
  }

  /**
   * @param appFileName
   * @return
   */
  public boolean readApplicationSettingsFromContent(CoaSession session, String projectName, String contentPath) {

    // declarations
    String logMsg = "";
    CoaContent content = null;

    log.debug(LOG_CATEGORY, "WebApplicationSetting - readWebApplicationSettingsFromContent: reached");

    // Get a CoaContenController
    CoaContentController contentController = CoaContentControllerFactory.getCoaContentController();

    // get WebApplicationSettings content
    try {
      if (contentController.coaContentExists(session, projectName, contentPath, CoaContent.STATUS_FINAL_ONLY)) {
        content = contentController.readCoaContent(session, projectName, contentPath, CoaContent.STATUS_FINAL_ONLY);
      } else {
        logMsg = "WebApplicationSettings Content '" + projectName + "/" + contentPath + "' not found.";
        log.debug(LOG_CATEGORY, "WebApplicationSetting - readWebApplicationSettingsFromContent" + logMsg);
        return false;
      }
    } catch (AccessNotGrantedException e) {
      logMsg = "AccessNotGrantedException for WebApplicationSettings Content '" + projectName + "/" + contentPath + "'.";
      log.debug(LOG_CATEGORY, "WebApplicationSetting - readWebApplicationSettingsFromContent" + logMsg);
      return false;
    }

    // *************************************************************
    // * get general application settings
    // *************************************************************
    this.initAction = content.getAttributeValue("webApplicationSettings.InitAction", Locale.ENGLISH);
    this.loginAction = content.getAttributeValue("webApplicationSettings.LoginAction", Locale.ENGLISH);
    this.logoutAction = content.getAttributeValue("webApplicationSettings.LogoutAction", Locale.ENGLISH);
    if (this.initAction == null || this.loginAction == null || this.logoutAction == null) {
      logMsg = "Application Setting - initAction, loginAction and/or logoutAction not defined in content.";
      log.debug(LOG_CATEGORY, "ApplicationSetting - readApplicationSettingsFromContent" + logMsg);
    }

    // set path to applications
    String applicationsAttrPath = "webApplicationSettings.applications";
    @SuppressWarnings("unchecked")
    Iterator<String> attrIter = content.getAttributePaths(applicationsAttrPath, false);
    if (attrIter == null) {
      logMsg = "No applications node found under '" + applicationsAttrPath + "' in content.";
      log.debug(LOG_CATEGORY, "ApplicationSetting - readApplicationSettingsFromContent" + logMsg);
      return false;
    }

    // *************************************************************
    // * get all applications
    // *************************************************************
    while(attrIter.hasNext()) {

      // get application name path
      String applicationNamePath = (String) attrIter.next();
      applicationNamePath = applicationsAttrPath + "." + applicationNamePath;
      String applicationName = applicationNamePath.substring(applicationNamePath.lastIndexOf('.') + 1, applicationNamePath.length());

      // Log this
      logMsg = "Application '" + applicationName + "' found in WebApplicationSettings content under path '" + applicationNamePath + "'";
      log.debug(LOG_CATEGORY, "ApplicationSetting - readApplicationSettingsFromContent" + logMsg);

      // create application instance
      Application application = new Application();

      // fill application object - general properties
      application.setName(applicationName);
      application.setActionPrefix(content.getAttributeValue(applicationNamePath + ".actionPrefix", Locale.ENGLISH));
      application.setActionPath(content.getAttributeValue(applicationNamePath + ".actionPath", Locale.ENGLISH));
      application.setDefaultAction(content.getAttributeValue(applicationNamePath + ".defaultAction", Locale.ENGLISH));
      application.setDefaultTemplate(content.getAttributeValue(applicationNamePath + ".defaultTemplate", Locale.ENGLISH));
      application.setErrorActionForward(content.getAttributeValue(applicationNamePath + ".errorActionForward", Locale.ENGLISH));
      application.setRoles(content.getAttributeValues(applicationNamePath + ".role", Locale.ENGLISH));
      application.setSessionIdCookieName(content.getAttributeValue(applicationNamePath + ".sessionIdCookieName", Locale.ENGLISH));
      application.setSessionIdCookiePath(content.getAttributeValue(applicationNamePath + ".sessionIdCookiePath", Locale.ENGLISH));
      if (application.getActionPrefix() == null || application.getActionPath() == null || application.getDefaultAction() == null || application.getErrorActionForward() == null
          || application.getRoles() == null || application.getSessionIdCookieName() == null || application.getSessionIdCookiePath() == null) {

        logMsg = "Application Setting - actionPrefix, viewPath, templatePath, defaultAction, errorActionForward, role, sessionIdCookieName, sessionIdCookiePath, and/or resourceFileClasspath not defined in DS Registry content.";
        log.debug(LOG_CATEGORY, "ApplicationSetting - readApplicationSettingsFromContent" + logMsg);
      }

      // get custom settings
      String customSettingsPath = applicationNamePath + ".custom";
      @SuppressWarnings("unchecked")
      Iterator<String> customSettingsIter = content.getAttributePaths(customSettingsPath, false);
      if (customSettingsIter == null) {
        log.debug(LOG_CATEGORY, "ApplicationSetting - readApplicationSettingsFromContent: no custom settings found for application with path '" + applicationNamePath + "'");
      } else {
        while(customSettingsIter.hasNext()) {
          // get application name path
          String csNamePath = (String) customSettingsIter.next();
          csNamePath = customSettingsPath + "." + csNamePath;
          String csName = csNamePath.substring(csNamePath.lastIndexOf('.') + 1, csNamePath.length());
          String csValue = content.getAttributeValue(csNamePath, Locale.ENGLISH);
          if (csValue == null) csValue = "";
          if (csName != null && !csName.equals("")) {
            application.addCustomSetting(application.getName() + "." + csName, csValue);
            log.debug(LOG_CATEGORY, "ApplicationSetting - readApplicationSettingsFromContent: custom setting added for '" + csName + "' with value '" + csValue + "'");
          }
        }
      }

      // set path to actions
      String actionsAttrPath = applicationNamePath + ".actions";
      @SuppressWarnings("unchecked")
      Iterator<String> actionsIter = content.getAttributePaths(actionsAttrPath, false);
      if (actionsIter == null) {
        logMsg = "No actions node found under '" + actionsAttrPath + "' in content.";
        log.debug(LOG_CATEGORY, "ApplicationSetting - readApplicationSettingsFromContent" + logMsg);
        return false;
      }

      // *************************************************************
      // * get all applications
      // *************************************************************
      while(actionsIter.hasNext()) {

        // get application name path
        String actionNamePath = (String) actionsIter.next();
        actionNamePath = actionsAttrPath + "." + actionNamePath;
        String actionName = actionNamePath.substring(actionNamePath.lastIndexOf('.') + 1, actionNamePath.length());
        
        // Log this
        logMsg = "Action '" + actionName + "' found in WebApplicationSettings content under path '" + actionNamePath + "'";
        log.debug(LOG_CATEGORY, "ApplicationSetting - readApplicationSettingsFromContent" + logMsg);

        // create ActionMappingInstance
        ActionMapping actionMapping = new ActionMapping();

        // fill action mapping
        actionMapping.setAction(actionName);
        actionMapping.setActionClass(content.getAttributeValue(actionNamePath + ".class", Locale.ENGLISH));
        actionMapping.setGroups(content.getAttributeValues(actionNamePath + ".group", Locale.ENGLISH));
        if (actionMapping.getAction() == null || actionMapping.getActionClass() == null) {

          logMsg = "Action name and/or class not defined.";
          log.debug(LOG_CATEGORY, "ApplicationSetting - readApplicationSettingsFromContent" + logMsg);
        }

        // set path to action forwards
        String actionForwardsAttrPath = actionNamePath + ".forwards";
        @SuppressWarnings("unchecked")
        Iterator<String> actionsForwardsIter = content.getAttributePaths(actionForwardsAttrPath, false);
        if (actionsForwardsIter == null) {
          logMsg = "No action forwards node found under '" + actionForwardsAttrPath + "' in content.";
          log.debug(LOG_CATEGORY, "ApplicationSetting - readApplicationSettingsFromContent" + logMsg);
          return false;
        }

        // *************************************************************
        // * get all action forwards
        // *************************************************************
        while(actionsForwardsIter.hasNext()) {

          // get application name path
          String actionForwardNamePath = (String) actionsForwardsIter.next();
          actionForwardNamePath = actionForwardsAttrPath + "." + actionForwardNamePath;
          String actionForwardName = actionForwardNamePath.substring(actionForwardNamePath.lastIndexOf('.') + 1, actionForwardNamePath.length());

          ActionForwardDef actionForwardDef = new ActionForwardDef();

          // fill action forward
          actionForwardDef.setName(actionForwardName);

          log.debug(LOG_CATEGORY, "ApplicationSetting - readApplicationSettingsFromContent: ActionForward with name '" + actionForwardName + "' found.");

          // fill action forward type
          String type = content.getAttributeValue(actionForwardNamePath + ".type", Locale.ENGLISH);
          if (type == null || type.equals("")) {
            actionForwardDef.setType(ActionForwardConstants.ACTION_FORWARD_TYPE_CALL_XCHG_WEBLET);
          } else {
            int i = 0;
            try {
              i = Integer.parseInt(type);
            } catch (NumberFormatException e) {
              logMsg = "NumberFormatException: type '" + type + "' is not a number.";
              log.debug(LOG_CATEGORY, "ApplicationSetting - readApplicationSettingsFromContent" + logMsg);
            }
            actionForwardDef.setType(i);
          }

          // check action forward type and read further specific attributes
          if (actionForwardDef.getType() == ActionForwardDef.ACTION_FORWARD_TYPE_CALL_XCHG_WEBLET || actionForwardDef.getType() == ActionForwardDef.ACTION_FORWARD_TYPE_CALL_XCHG_WEBLET_TO_REFERER
              || actionForwardDef.getType() == ActionForwardDef.ACTION_FORWARD_TYPE_WEBLET_REDIRECT) {
            // fill action view and template and initialize action
            actionForwardDef.setView(content.getAttributeValue(actionForwardNamePath + ".view", Locale.ENGLISH));
            actionForwardDef.setTemplate(content.getAttributeValue(actionForwardNamePath + ".template", Locale.ENGLISH));
            actionForwardDef.setAction("");
            if (actionForwardDef.getView() == null || actionForwardDef.getTemplate() == null) {
              actionForwardDef.setView("");
              actionForwardDef.setTemplate("");
              logMsg = "ActionForward view and/or template not defined in DS Registry.";
              log.debug(LOG_CATEGORY, "ApplicationSetting - readApplicationSettingsFromContent" + logMsg);
            }
          } else if (actionForwardDef.getType() == ActionForwardDef.ACTION_FORWARD_TYPE_ACTION_REDIRECT || actionForwardDef.getType() == ActionForwardDef.ACTION_FORWARD_TYPE_ACTION_FORWARD) {
            // fill action view and template and initialize action
            actionForwardDef.setView("");
            actionForwardDef.setTemplate("");
            actionForwardDef.setAction(content.getAttributeValue(actionForwardNamePath + ".action", Locale.ENGLISH));
            if (actionForwardDef.getAction() == null) {
              actionForwardDef.setAction("");
              logMsg = "ActionForward view and/or template not defined in DS Registry.";
              log.debug(LOG_CATEGORY, "ApplicationSetting - readApplicationSettingsFromContent" + logMsg);
            }
          } else {
            logMsg = "ActionForward type not defined correctly (1-4 are valid values).";
            log.debug(LOG_CATEGORY, "ApplicationSetting - readApplicationSettingsFromRegistry" + logMsg);
          }

          // add to action mapping
          actionMapping.setActionForward(actionForwardDef);
        }

        // set path to action forwards
        String actionValidationRulesAttrPath = actionNamePath + ".validationRules";
        @SuppressWarnings("unchecked")
        Iterator<String> actionValidationRulesIter = content.getAttributePaths(actionValidationRulesAttrPath, false);
        if (actionValidationRulesIter == null) {
          logMsg = "No action validation rules node found under '" + actionValidationRulesAttrPath + "' in content.";
          log.debug(LOG_CATEGORY, "ApplicationSetting - readApplicationSettingsFromContent" + logMsg);
          return false;
        }

        // *************************************************************
        // * get all validation rules
        // *************************************************************
        while(actionValidationRulesIter.hasNext()) {

          // get application name path
          String actionValidationRuleNamePath = (String) actionValidationRulesIter.next();
          actionValidationRuleNamePath = actionValidationRulesAttrPath + "." + actionValidationRuleNamePath;
          String actionValidationRuleName = actionValidationRuleNamePath.substring(actionValidationRuleNamePath.lastIndexOf('.') + 1, actionValidationRuleNamePath.length());
          
          // create new validation rule
          ActionValidationRule actionValidationRule = new ActionValidationRule();

          // fill validation rule
          actionValidationRule.setName(actionValidationRuleName);

          // set default value
          String defaultValues[] = content.getAttributeValues(actionValidationRuleNamePath, Locale.ENGLISH);
          actionValidationRule.setDefaultValues(defaultValues);

          // set pattern
          actionValidationRule.setPattern(content.getAttributeValue(actionValidationRuleNamePath + ".pattern", Locale.ENGLISH));

          // set mandatory
          String mandatory = content.getAttributeValue(actionValidationRuleNamePath + ".mandatory", Locale.ENGLISH);
          if ((mandatory == null) || mandatory.equals("") || mandatory.equals("0")) {
            logMsg = "No mandatory found or set to 0 under '" + actionValidationRuleNamePath + ".mandatory' in content.";
            log.debug(LOG_CATEGORY, "ApplicationSetting - readApplicationSettingsFromContent" + logMsg);
            actionValidationRule.setMandatory(false);
          } else {
            actionValidationRule.setMandatory(true);
          }

          // set minLength
          String minLength = content.getAttributeValue(actionValidationRuleNamePath + ".minLength", Locale.ENGLISH);
          if (minLength == null || minLength.equals("")) {
            actionValidationRule.setMinLength(-1);
          } else {
            int i = 0;
            try {
              i = Integer.parseInt(minLength);
            } catch (NumberFormatException e) {
              logMsg = "NumberFormatException: minLength '" + minLength + "' is not a number.";
              log.debug(LOG_CATEGORY, "ApplicationSetting - readApplicationSettingsFromContent" + logMsg);
            }
            actionValidationRule.setMinLength(i);
          }

          // set maxLength
          String maxLength = content.getAttributeValue(actionValidationRuleNamePath + ".maxLength", Locale.ENGLISH);
          if (maxLength == null || maxLength.equals("")) {
            actionValidationRule.setMaxLength(-1);
          } else {
            int i = 0;
            try {
              i = Integer.parseInt(maxLength);
            } catch (NumberFormatException e) {
              logMsg = "NumberFormatException: maxLength '" + maxLength + "' is not a number.";
              log.debug(LOG_CATEGORY, "ApplicationSetting - readApplicationSettingsFromContent" + logMsg);
            }
            actionValidationRule.setMaxLength(i);
          }

          // add to action mapping
          actionMapping.setValidationRule(actionValidationRule);

        }
        // add actionMapping to application
        application.setActionMapping(actionMapping);

      }

      // add application to list of applications.
      this.applications.put(applicationName, application);
    }

    this.created = true;
    return true;
  }

  public Hashtable<String, Application> getApplications() {
    return this.applications;
  }

  public String getApplicationNames(String separator) {
    String result = "";
    Enumeration<String> enumApps = this.applications.keys();
    while(enumApps.hasMoreElements()) {
      result += enumApps.nextElement() + separator;
    }
    if (!result.equals(""))
      result = result.substring(0, result.length() - 1);
    return result;
  }

}