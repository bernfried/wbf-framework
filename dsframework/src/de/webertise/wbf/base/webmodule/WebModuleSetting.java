/*
 * ApplicationSetting.java
 *
 * Created on 7. Dezember 2002, 14:47
 */

package de.webertise.wbf.base.webmodule;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;

import de.reddot.api.common.content.CoaContent;
import de.reddot.api.common.content.CoaContentController;
import de.reddot.api.common.content.CoaContentControllerFactory;
import de.reddot.api.common.session.CoaSession;
import de.reddot.api.exception.AccessNotGrantedException;
import de.webertise.ds.services.LogServices;

/**
 * 
 * @author bernfried.howe
 */
public class WebModuleSetting {

  // constants
  private final static String          LOG_CATEGORY             = "wbf_settings";
  private LogServices                  log                      = LogServices.getInstance(LOG_CATEGORY);

  private Hashtable<String, WebModule> moduleList               = new Hashtable<String, WebModule>();
  private String                       projectParameterName     = "p";
  private String                       webModuleParameterName   = "wm";
  private String                       applicationParameterName = "wa";
  private String                       actionParameterName      = "a";
  private String                       refererPageParameterName = "rp";
  private String                       requestURI               = "";
  private String                       defaultWebModule         = "";
  private String                       defaultApplication       = "";
  private String                       defaultProject           = "";
  private boolean                      wbfDefaultWeblet         = false;
  private boolean                      debug                    = false;

  /** Creates a new instance of ApplicationSetting */
  public WebModuleSetting() {
  }

  // GETTER

  public String getProjectParameterName() {
    return this.projectParameterName;
  }

  public String getWebModuleParameterName() {
    return this.webModuleParameterName;
  }

  public String getApplicationParameterName() {
    return this.applicationParameterName;
  }

  public String getActionParameterName() {
    return this.actionParameterName;
  }

  public String getRefererPageParameterName() {
    return this.refererPageParameterName;
  }

  public String getRequestURI() {
    return this.requestURI;
  }

  public String getDefaultWebModule() {
    return this.defaultWebModule;
  }

  public String getDefaultApplication() {
    return this.defaultApplication;
  }

  public String getDefaultProject() {
    return defaultProject;
  }

  public Hashtable<String, WebModule> getWebModules() {
    return this.moduleList;
  }

  // SETTER

  public void setRequestURI(String r) {
    this.requestURI = r;
  }

  /** Creates or updates a module in module list */
  public void putWebModule(WebModule webModule) {
    if ((webModule.getName() != null) && (webModule.getClasspath() != null)) {
      moduleList.put(webModule.getName(), webModule);
    }
  }

  /** Gets a module out of the module list, null if key not exists */
  public WebModule getWebModule(String name) {
    if ((name != null) && (!name.equals(""))) {
      return (WebModule) moduleList.get(name);
    } else {
      return null;
    }
  }

  public boolean isDebug() {
    return debug;
  }

  public void setDebug(boolean debug) {
    this.debug = debug;
  }
  
  public boolean isWbfDefaultWeblet() {
    return wbfDefaultWeblet;
  }

  public void setWbfDefaultWeblet(boolean wbfDefaultWeblet) {
    this.wbfDefaultWeblet = wbfDefaultWeblet;
  }

  public boolean readWebModuleSettingsFromContent(CoaSession session, String projectName, String contentPath) {

    // declarations
    String logMsg = "";
    CoaContent content = null;

    log.debug(LOG_CATEGORY, "WebModuleSetting - readWebModuleSettingsFromContent: reached");

    // Get a CoaContenController
    CoaContentController contentController = CoaContentControllerFactory.getCoaContentController();

    // get WebModuleSettings content
    try {
      if (contentController.coaContentExists(session, projectName, contentPath, CoaContent.STATUS_FINAL_ONLY)) {
        content = contentController.readCoaContent(session, projectName, contentPath, CoaContent.STATUS_FINAL_ONLY);
      } else {
        logMsg = "WebModuleSettings Content '" + projectName + "/" + contentPath + "' not found.";
        log.debug(LOG_CATEGORY, "WebModuleSetting - readWebModuleSettingsFromContent" + logMsg);
        return false;
      }
    } catch (AccessNotGrantedException e) {
      logMsg = "AccessNotGrantedException for WebModuleSettings Content '" + projectName + "/" + contentPath + "'.";
      log.debug(LOG_CATEGORY, "WebModuleSetting - readWebModuleSettingsFromContent" + logMsg);
      return false;
    }

    this.webModuleParameterName = content.getAttributeValue("webModuleSettings.general.webModuleParameterName", Locale.ENGLISH);
    this.applicationParameterName = content.getAttributeValue("webModuleSettings.general.applicationParameterName", Locale.ENGLISH);
    this.projectParameterName = content.getAttributeValue("webModuleSettings.general.projectParameterName", Locale.ENGLISH);
    this.actionParameterName = content.getAttributeValue("webModuleSettings.general.actionParameterName", Locale.ENGLISH);
    this.refererPageParameterName = content.getAttributeValue("webModuleSettings.general.refererPageParameterName", Locale.ENGLISH);
    this.defaultWebModule = content.getAttributeValue("webModuleSettings.general.defaultWebModule", Locale.ENGLISH);
    this.defaultApplication = content.getAttributeValue("webModuleSettings.general.defaultApplication", Locale.ENGLISH);
    this.defaultProject = content.getAttributeValue("webModuleSettings.general.defaultProject", Locale.ENGLISH);

    // check debug mode
    String debugMode = content.getAttributeValue("webModuleSettings.general.debugMode", Locale.ENGLISH);
    if (debugMode != null && debugMode.equals("on")) {
      log.debug(LOG_CATEGORY, "WebModuleSetting - readWebModuleSettingsFromContent: debugMode = on");
      this.debug = true;
    } else {
      log.debug(LOG_CATEGORY, "WebModuleSetting - readWebModuleSettingsFromContent: debugMode = off");
      this.debug = false;
    }

    // check debug mode
    String wbfDefaultWeblet = content.getAttributeValue("webModuleSettings.general.wbfDefaultWeblet", Locale.ENGLISH);
    if (wbfDefaultWeblet != null && wbfDefaultWeblet.equals("on")) {
      log.debug(LOG_CATEGORY, "WebModuleSetting - readWebModuleSettingsFromContent: wbfDefaultWeblet = on");
      this.wbfDefaultWeblet = true;
    } else {
      log.debug(LOG_CATEGORY, "WebModuleSetting - readWebModuleSettingsFromContent: wbfDefaultWeblet = off");
      this.wbfDefaultWeblet = false;
    }


    @SuppressWarnings("unchecked")
    Iterator<String> attrIter = content.getAttributePaths("webModuleSettings.webModules", false);
    if (attrIter == null) {
      logMsg = "Registry Content - Attribute 'webModuleSettings.webModules' is not available.";
      log.debug(LOG_CATEGORY, "WebModuleSetting - readWebModuleSettingsFromContent" + logMsg);
      return false;
    }

    // get all web modules
    while(attrIter.hasNext()) {

      // create web module instance
      WebModule webModule = new WebModule();

      // get web module child element
      String attrPath = (String) attrIter.next();

      // get web module name
      String webModuleNamePath = "webModuleSettings.webModules." + attrPath;
      String webModuleName = attrPath.substring(attrPath.lastIndexOf('.') + 1, attrPath.length());
      webModule.setName(webModuleName);

      log.debug(LOG_CATEGORY, "WebModuleSetting - readWebModuleSettingsFromContent: web module found with name '" + webModuleName + "'");

      // get web module classpath
      String value = content.getAttributeValue(webModuleNamePath.concat(".classpath"), Locale.ENGLISH);
      if ((value == null) || (value.equals(""))) {
        logMsg = webModuleNamePath.concat(".classpath") + " not available.";
        log.debug(LOG_CATEGORY, "WebModuleSetting - readWebModuleSettingsFromContent" + logMsg);
        return false;
      } else {
        // set name of web module
        webModule.setClasspath(value);
      }

      // get web module name
      String ipAddresses[] = content.getAttributeValues(webModuleNamePath.concat(".ipSecurity"), Locale.ENGLISH);
      if (ipAddresses == null) {
        logMsg = webModuleNamePath.concat(".ipSecurity") + " not available.";
        log.debug(LOG_CATEGORY, "WebModuleSetting - readWebModuleSettingsFromContent" + logMsg);
        // set ip addresses of ip security for the web module
        webModule.addIpSecurityItem("*.*.*.*");
      } else {
        if (ipAddresses.length == 0) {
          logMsg = webModuleNamePath.concat(".ipSecurity") + " - no values specified.";
          log.debug(LOG_CATEGORY, "WebModuleSetting - readWebModuleSettingsFromContent" + logMsg);
          // set ip addresses of ip security for the web module
          webModule.addIpSecurityItem("*.*.*.*");
        } else {
          for (int j = 0; j < ipAddresses.length; j++) {
            // error handling if one of the values is empty
            if (ipAddresses[j].equals("")) {
              logMsg = webModuleNamePath.concat(".ipSecurity") + " - value " + j + " not specified.";
              log.debug(LOG_CATEGORY, "WebModuleSetting - readWebModuleSettingsFromContent" + logMsg);
            }
            // set ip addresses of ip security for the web module
            webModule.addIpSecurityItem(ipAddresses[j]);
          }
        }
      }
      this.moduleList.put(webModuleName, webModule);

    }
    return true;
  }

}
