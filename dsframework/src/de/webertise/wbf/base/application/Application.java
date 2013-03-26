/*
 * ActionModuleSetting.java
 *
 * Created on 15. März 2003, 08:55
 */

package de.webertise.wbf.base.application;

import java.util.Enumeration;
import java.util.Hashtable;

import de.webertise.wbf.base.action.ActionMapping;

/**
 * 
 * @author bernfried howe
 */
public class Application {

  /**
   * Holds the name of an application
   * 
   */
  private String                           name                = "";

  /**
   * Holds the action prefix string in the controller.xml in order to identify an action string out of the request parameters. Required, because if one html form has two submit buttons with different
   * actions, the action parameter can not be set via a hidden field, but must be set as name of the submit button. This leads to two request parameter named action.x and action.y. The only way to
   * distinguish between the action parameter and others is two add an prefix like ___action.x and to search for this prefix.
   */
  private String                           actionPrefix        = "";

  /**
   * Holds the java package path to the Action classes of the application.
   */
  private String                           actionPath          = "";

  /**
   * Holds the default action, if no action can be found in url and request parameters
   */
  private String                           defaultAction       = "";

  /**
   * Holds the default template, if no template can be found in url and request parameters
   */
  private String                           defaultTemplate     = "";

  /**
   * Holds the action forward for a general error page
   */
  private String                           errorActionForward  = "";

  /**
   * Holds the required Delivery Server role for this application
   */
  private String[]                         roles               = null;

  /**
   * Holds the required Delivery Server session id Cookie name for this application
   */
  private String                           sessionIdCookieName = "wbfSessionID";

  /**
   * Holds the required Delivery Server session id Cookie name for this application
   */
  private String                           sessionIdCookiePath = "/cps";

  /**
   * Holds the list of actionMappings of this application. Key is the action.
   */
  private Hashtable<String, ActionMapping> actionMappings      = new Hashtable<String, ActionMapping>();

  /**
   * Holds the list of custom settings of this application. Key is the content attribute name.
   */
  private Hashtable<String, String>        customSettings      = new Hashtable<String, String>();

  /** Creates a new instance of ActionModuleSetting */
  public Application() {
  }

  /**************************************************************************
   * Getter
   *************************************************************************/
  public String getName() {
    return this.name;
  }

  public String getActionPrefix() {
    return this.actionPrefix;
  }

  public String getActionPath() {
    return this.actionPath;
  }

  public String getDefaultAction() {
    return this.defaultAction;
  }

  public String getDefaultTemplate() {
    return this.defaultTemplate;
  }

  public String getErrorActionForward() {
    return this.errorActionForward;
  }

  public String[] getRoles() {
    return this.roles;
  }

  public String getSessionIdCookieName() {
    return this.sessionIdCookieName;
  }

  public String getSessionIdCookiePath() {
    return this.sessionIdCookiePath;
  }

  public ActionMapping getActionMapping(String action) {
    if (this.actionMappings.get(action) == null)
      return null;
    else
      return (ActionMapping) this.actionMappings.get(action);
  }

  public Enumeration<String> getActionNames() {
    return this.actionMappings.keys();
  }

  public Hashtable<String, String> getCustomSettings() {
    return customSettings;
  }

  public String getCustomSetting(String key) {
    return this.customSettings.get(key);
  }

  /**************************************************************************
   * Setter
   *************************************************************************/

  public void setActionPrefix(String actionPrefix) {
    this.actionPrefix = actionPrefix;
  }

  public void setActionPath(String actionPath) {
    this.actionPath = actionPath;
  }

  public void setDefaultAction(String defaultAction) {
    this.defaultAction = defaultAction;
  }

  public void setErrorActionForward(String errorActionForward) {
    this.errorActionForward = errorActionForward;
  }

  public void setActionMapping(ActionMapping actionMapping) {
    this.actionMappings.put(actionMapping.getAction(), actionMapping);
  }

  public void setName(String applicationName) {
    this.name = applicationName;
  }

  public boolean checkActionMapping(String actionName) {
    if (this.actionMappings.get(actionName) == null)
      return false;
    else
      return true;
  }

  public void setRoles(String[] roles) {
    this.roles = roles;
  }

  public void setSessionIdCookieName(String value) {
    this.sessionIdCookieName = value;
  }

  public void setSessionIdCookiePath(String value) {
    this.sessionIdCookiePath = value;
  }

  public void setDefaultTemplate(String value) {
    this.defaultTemplate = value;
  }

  public void setCustomSettings(Hashtable<String, String> customSettings) {
    this.customSettings = customSettings;
  }

  public void addCustomSetting(String key, String value) {
    this.customSettings.put(key, value);
  }

  public boolean checkUserRoles(String roles) {
    // find the first corresponding role
    if (this.roles != null) {
      for (int i=0; i<this.roles.length; i++) {
        if (roles.contains("[" + this.roles[i] + "]")) {
          return true;
        }
      }
      return false;
    }  else {
      return true;
    }
  }

  public String getRolesAsString() {
    String result = "";
    if (this.roles != null) {
      for (int i=0; i<this.roles.length; i++) {
        result = result + "[" + this.roles[i] + "]";
      }
    }
    return result;
  }

}