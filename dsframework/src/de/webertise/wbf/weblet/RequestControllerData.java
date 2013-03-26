package de.webertise.wbf.weblet;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import de.reddot.api.web.io.WebletRequest;
import de.webertise.ds.services.LogServices;
import de.webertise.wbf.base.action.AbstractAction;
import de.webertise.wbf.base.action.ActionResponseItem;
import de.webertise.wbf.base.application.Application;
import de.webertise.wbf.base.application.ApplicationSetting;
import de.webertise.wbf.base.project.ProjectSetting;
import de.webertise.wbf.base.webmodule.WebModuleSetting;

public class RequestControllerData {

  // constants
  private final static String       LOG_CATEGORY                                  = "wbf_weblet";
  public final static int           PARAMETER_NOT_FOUND                           = 0;
  public final static int           PARAMETER_NOT_FOUND_AND_SET_TO_DEFAULT        = 1;
  public final static int           PARAMETER_FOUND_IN_URI                        = 2;
  public final static int           PARAMETER_FOUND_IN_REQUEST_PARAMETER          = 3;
  public final static int           PARAMETER_FOUND_IN_PREFIXED_REQUEST_PARAMETER = 4;
  public final static int           PARAMETER_VALUE_WRONG_AND_SET_TO_DEFAULT      = 5;

  // variables
  private LogServices               log                                           = LogServices.getInstance(LOG_CATEGORY);

  // properties
  private String                    queryString                                   = "";
  private String                    requestUri                                    = "";
  private String                    remoteAddr                                    = "";
  private String                    contextPath                                   = "";

  // properties with getter and setter
  private String                    webModuleName                                 = "";
  private String                    applicationName                               = "";
  private String                    projectName                                   = "";
  private String                    actionName                                    = "";
  private String                    refererPageName                               = "";

  private int                       projectNameFound                              = PARAMETER_NOT_FOUND;
  private int                       webModuleNameFound                            = PARAMETER_NOT_FOUND;
  private int                       applNameFound                                 = PARAMETER_NOT_FOUND;
  private int                       actionNameFound                               = PARAMETER_NOT_FOUND;
  private int                       refererPageFound                              = PARAMETER_NOT_FOUND;

  private ArrayList<String>         uriParts                                      = new ArrayList<String>();
  private Hashtable<String, String> queryParams                                   = new Hashtable<String, String>();
  private String                    partBefore                                    = "";

  public RequestControllerData(WebletRequest request, ProjectSetting ps, WebModuleSetting wms, ApplicationSetting as) {

    // get http servlet request from weblet request
    HttpServletRequest httpRequest = request.getHttpServletRequest();

    // get required request information
    this.queryString = httpRequest.getQueryString();
    this.requestUri = httpRequest.getRequestURI();
    this.remoteAddr = httpRequest.getRemoteAddr();
    this.contextPath = httpRequest.getContextPath();

    // logging
    log.debug(LOG_CATEGORY, "RequestControllerData - RequestControllerData: queryString '" + this.queryString + "'");
    log.debug(LOG_CATEGORY, "RequestControllerData - RequestControllerData: requestUri '" + this.requestUri + "'");
    log.debug(LOG_CATEGORY, "RequestControllerData - RequestControllerData: remoteAddr '" + this.remoteAddr + "'");
    log.debug(LOG_CATEGORY, "RequestControllerData - RequestControllerData: contextPath '" + this.contextPath + "'");

    // build uri parts
    StringTokenizer stUriParts = new StringTokenizer(requestUri, "/");
    while(stUriParts.hasMoreTokens()) {
      String token = stUriParts.nextToken();
      if (!token.startsWith("SID-"))
        this.uriParts.add(token);
    }

    // identify part before rcd in URL. Can be e.g.
    this.partBefore = identifyUriPartBeforeRCD(request);
    log.debug(LOG_CATEGORY, "RequestControllerData - RequestControllerData: Identified part before in uri '" + this.partBefore + "'");

    // get post request parameter from request
    @SuppressWarnings("unchecked")
    Enumeration<String> paraNames = request.getParameterNames();
    while(paraNames.hasMoreElements()) {
      String paraName = paraNames.nextElement();
      String paraValue = request.getParameter(paraName);
      this.queryParams.put(paraName, paraValue);
    }

    // start identification
    getProjectName(ps, wms);
    getWebModuleName(wms);
    getApplicationName(wms, as);
    getActionName(wms, as);
    getRefererPageName(wms);

    // write result to request for usage in actions
    request.setParameter("wbf.requestctrldata.project", this.projectName);
    request.setParameter("wbf.requestctrldata.webmodule", this.webModuleName);
    request.setParameter("wbf.requestctrldata.application", this.applicationName);
    request.setParameter("wbf.requestctrldata.action", this.actionName);
    request.setParameter("wbf.requestctrldata.referer", this.refererPageName);

  }

  private String identifyUriPartBeforeRCD(WebletRequest request) {

    String rdePrefix = request.getParameter("rdePrefix"); // e.g. cps
    String rdeServletMapping = request.getParameter("rdeServletMapping"); // e.g. rde

    // define all possible cases
    // "cps/rde/wbf" or "cps/rde" or "rde/wbf" or "cps/wbf" or "cps" or "rde" or "wbf"
    Hashtable<String, String> cases = new Hashtable<String, String>();
    cases.put(rdePrefix, rdePrefix);
    cases.put(rdePrefix + "/" + rdeServletMapping, rdeServletMapping);
    cases.put(rdePrefix + "/" + rdeServletMapping + "/" + MasterWebletConstants.WEBLET_ALIAS, MasterWebletConstants.WEBLET_ALIAS);
    cases.put(rdePrefix + "/" + MasterWebletConstants.WEBLET_ALIAS, MasterWebletConstants.WEBLET_ALIAS);
    cases.put(rdeServletMapping, rdeServletMapping);
    cases.put(rdeServletMapping + "/" + MasterWebletConstants.WEBLET_ALIAS, MasterWebletConstants.WEBLET_ALIAS);
    cases.put(MasterWebletConstants.WEBLET_ALIAS, MasterWebletConstants.WEBLET_ALIAS);

    String partBefore = "";
    String cmpStr = "";
    if (uriParts.size() >= 3) {
      cmpStr = uriParts.get(0) + "/" + uriParts.get(1) + "/" + uriParts.get(2);
      partBefore = cases.get(cmpStr);
      if (partBefore != null) {
        return partBefore;
      }
    }
    if (uriParts.size() >= 2) {
      cmpStr = uriParts.get(0) + "/" + uriParts.get(1);
      partBefore = cases.get(cmpStr);
      if (partBefore != null) {
        return partBefore;
      }
    }
    if (uriParts.size() >= 1) {
      cmpStr = uriParts.get(0);
      partBefore = cases.get(cmpStr);
      if (partBefore != null) {
        return partBefore;
      }
    }

    return null;
  }

  private void getProjectName(ProjectSetting ps, WebModuleSetting wms) {

    log.debug(LOG_CATEGORY, "RequestControllerData - getProjectName: reached");

    // identify web module name from url parameter
    String pName = this.queryParams.get(wms.getProjectParameterName());
    if (pName == null || pName.equals("")) {
      // identify project name from uri; should be first part after wbf/
      for (int i = 0; i < this.uriParts.size(); i++) {
        if ((this.partBefore == null || this.uriParts.get(i).equals(this.partBefore)) && (i + 1 < this.uriParts.size())) {
          pName = this.uriParts.get(++i);
          this.projectNameFound = PARAMETER_FOUND_IN_URI;
          log.debug(LOG_CATEGORY, "RequestControllerData - getProjectName: found in request uri '" + this.requestUri + "' searching after '" + this.partBefore + "'");
          break;
        }
      }
    } else {
      this.projectNameFound = PARAMETER_FOUND_IN_REQUEST_PARAMETER;
      log.debug(LOG_CATEGORY, "RequestControllerData - getProjectName: found in request parameter with name '" + wms.getProjectParameterName() + "'");
    }
    // check if found wmName finds a web module
    if (pName == null || ps.getProject(pName) == null) {
      pName = wms.getDefaultProject();
      this.projectNameFound = PARAMETER_NOT_FOUND_AND_SET_TO_DEFAULT;
      log.debug(LOG_CATEGORY, "RequestControllerData - getProjectName: set to default project");
    }
    this.projectName = pName;
    log.debug(LOG_CATEGORY, "RequestControllerData - getProjectName: finally used '" + pName + "'");
  }

  private void getWebModuleName(WebModuleSetting wms) {

    log.debug(LOG_CATEGORY, "RequestControllerData - getWebModuleName: reached");

    // identify web module name from url parameter
    String wmName = this.queryParams.get(wms.getWebModuleParameterName());
    if (wmName == null || wmName.equals("")) {
      // identify wmName from uri; should be first part after projectName/ or wbf/
      String partBefore = this.partBefore;
      if (this.projectNameFound > 0) {
        partBefore = this.projectName;
      }
      for (int i = 0; i < this.uriParts.size(); i++) {
        if ((partBefore == null || this.uriParts.get(i).equals(partBefore)) && (i + 1 < this.uriParts.size())) {
          wmName = this.uriParts.get(++i);
          this.webModuleNameFound = PARAMETER_FOUND_IN_URI;
          log.debug(LOG_CATEGORY, "RequestControllerData - getWebModuleName: found in request uri '" + this.requestUri + "' searching after '" + partBefore + "'");
          break;
        }
      }
    } else {
      this.webModuleNameFound = PARAMETER_FOUND_IN_REQUEST_PARAMETER;
      log.debug(LOG_CATEGORY, "RequestControllerData - getWebModuleName: found in request parameter with name '" + wms.getWebModuleParameterName() + "'");
    }
    // check if found wmName finds a web module
    if (wmName == null || (wmName != null && wms.getWebModule(wmName) == null)) {
      wmName = wms.getDefaultWebModule();
      this.webModuleNameFound = PARAMETER_NOT_FOUND_AND_SET_TO_DEFAULT;
      log.debug(LOG_CATEGORY, "RequestControllerData - getWebModuleName: set to default web module");
    }
    this.webModuleName = wmName;
    log.debug(LOG_CATEGORY, "RequestControllerData - getWebModuleName: finally used '" + wmName + "'");
  }

  private void getApplicationName(WebModuleSetting wms, ApplicationSetting as) {

    log.debug(LOG_CATEGORY, "RequestControllerData - getApplicationName: reached");

    // identify appl name from url parameter
    String applName = this.queryParams.get(wms.getApplicationParameterName());
    if (applName == null || applName.equals("")) {
      // identify appl name from uri; should be first part after wmName or wbf
      String partBefore = this.partBefore;
      if (this.webModuleNameFound == PARAMETER_FOUND_IN_URI) {
        partBefore = this.webModuleName;
      } else if (this.projectNameFound == PARAMETER_FOUND_IN_URI) {
        partBefore = this.projectName;
      }
      for (int i = 0; i < this.uriParts.size(); i++) {
        if (partBefore == null || this.uriParts.get(i).equals(partBefore) && (i + 1 < this.uriParts.size())) {
          applName = this.uriParts.get(++i);
          this.applNameFound = PARAMETER_FOUND_IN_URI;
          log.debug(LOG_CATEGORY, "RequestControllerData - getApplicationName: found in request uri '" + this.requestUri + "' searching after '" + partBefore + "'");
          break;
        }
      }
    } else {
      this.applNameFound = PARAMETER_FOUND_IN_REQUEST_PARAMETER;
      log.debug(LOG_CATEGORY, "RequestControllerData - getApplicationName: found in request parameter with name '" + wms.getApplicationParameterName() + "'");
    }
    // check if found applName finds an application
    if (applName == null || (applName != null && as.getApplication(applName) == null)) {
      applName = wms.getDefaultApplication();
      this.applNameFound = PARAMETER_NOT_FOUND_AND_SET_TO_DEFAULT;
      log.debug(LOG_CATEGORY, "RequestControllerData - getApplicationName: set to default application");
    }

    this.applicationName = applName;
    log.debug(LOG_CATEGORY, "RequestControllerData - getApplicationName: finally used '" + applName + "'");

  }

  private void getActionName(WebModuleSetting wms, ApplicationSetting as) {

    log.debug(LOG_CATEGORY, "RequestControllerData - getActionName: reached");

    // identify appl name from url parameter
    String actionName = this.queryParams.get(wms.getActionParameterName());
    if (actionName == null || actionName.equals("")) {
      // identify action name from uri; should be first part after applName or after wmName or after wbf
      String partBefore = this.partBefore;
      if (this.applNameFound == PARAMETER_FOUND_IN_URI) {
        partBefore = this.applicationName;
      } else if (this.webModuleNameFound == PARAMETER_FOUND_IN_URI) {
        partBefore = this.webModuleName;
      } else if (this.projectNameFound == PARAMETER_FOUND_IN_URI) {
        partBefore = this.projectName;
      }
      for (int i = 0; i < this.uriParts.size(); i++) {
        if (partBefore == null || this.uriParts.get(i).equals(partBefore) && (i + 1 < this.uriParts.size())) {
          actionName = this.uriParts.get(++i);
          log.debug(LOG_CATEGORY, "RequestControllerData - getActionName: found in request uri '" + this.requestUri + "' searching after '" + partBefore + "'");
          this.actionNameFound = PARAMETER_FOUND_IN_URI;
          break;
        }
      }
    } else {
      this.actionNameFound = PARAMETER_FOUND_IN_REQUEST_PARAMETER;
      log.debug(LOG_CATEGORY, "RequestControllerData - getActionName: found in request parameter with name '" + wms.getActionParameterName() + "'");
    }

    if (actionName == null && this.applicationName != null) {
      // check for form action parameter with action prefix
      Application a = as.getApplication(this.applicationName);
      Enumeration<String> keys = this.queryParams.keys();
      while(keys.hasMoreElements()) {
        String key = keys.nextElement();
        if (key.startsWith(a.getActionPrefix())) {
          actionName = key.replace(a.getActionPrefix(), "");
          log.debug(LOG_CATEGORY, "RequestControllerData - getActionName: found in prefixed action request parameter with name '" + key + "'");
          this.actionNameFound = PARAMETER_FOUND_IN_PREFIXED_REQUEST_PARAMETER;
          break;
        }
      }
    }

    // check if found action name finds an action mapping
    if (actionName == null) {
      actionName = as.getInitAction();
      this.actionNameFound = PARAMETER_NOT_FOUND_AND_SET_TO_DEFAULT;
      log.debug(LOG_CATEGORY, "RequestControllerData - getActionName: set to default action");
    } else if (!as.getApplication(this.applicationName).checkActionMapping(actionName)) {
      actionName = as.getInitAction();
      this.actionNameFound = PARAMETER_VALUE_WRONG_AND_SET_TO_DEFAULT;
      log.debug(LOG_CATEGORY, "RequestControllerData - getActionName: set to default action");
    }
    this.actionName = actionName;
    log.debug(LOG_CATEGORY, "RequestControllerData - getActionName: finally used '" + actionName + "'");

  }

  private void getRefererPageName(WebModuleSetting wms) {

    log.debug(LOG_CATEGORY, "RequestControllerData - getRefererPageName: reached");

    // identify appl name from url parameter
    String refPageName = this.queryParams.get(wms.getRefererPageParameterName());
    if (refPageName == null || refPageName.equals("")) {
      // identify action name from uri; should be first part after applName or after wmName or after wbf
      String partBefore = this.partBefore;
      if (this.actionNameFound == PARAMETER_FOUND_IN_URI) {
        partBefore = this.actionName;
      } else if (this.applNameFound == PARAMETER_FOUND_IN_URI) {
        partBefore = this.applicationName;
      } else if (this.webModuleNameFound == PARAMETER_FOUND_IN_URI) {
        partBefore = this.webModuleName;
      } else if (this.projectNameFound == PARAMETER_FOUND_IN_URI) {
        partBefore = this.projectName;
      }
      // get rest of request uri after partBefore + "/"
      if (partBefore != null) {
        int pos = this.requestUri.indexOf("/" + partBefore + "/");
        if (pos != -1) {
          int beginIndex = pos + ("/" + partBefore + "/").length();
          refPageName = this.requestUri.substring(beginIndex);
        }
      } else {
        refPageName = this.requestUri;
      }
      this.refererPageFound = PARAMETER_FOUND_IN_URI;
      log.debug(LOG_CATEGORY, "RequestControllerData - getRefererPageName: found '" + refPageName + "' in request uri '" + this.requestUri + "' searching after '" + partBefore + "'");

    } else {
      this.refererPageFound = PARAMETER_FOUND_IN_REQUEST_PARAMETER;
      log.debug(LOG_CATEGORY, "RequestControllerData - getRefererPageName: found in request parameter with name '" + wms.getRefererPageParameterName() + "'");
    }

    this.refererPageName = refPageName;
    log.debug(LOG_CATEGORY, "RequestControllerData - getRefererPageName: finally used '" + refPageName + "'");

  }

  /**
   * @return the queryString
   */
  public String getQueryString() {
    return queryString;
  }

  /**
   * @param queryString the queryString to set
   */
  public void setQueryString(String queryString) {
    this.queryString = queryString;
  }

  /**
   * @return the requestUri
   */
  public String getRequestUri() {
    return requestUri;
  }

  /**
   * @param requestUri the requestUri to set
   */
  public void setRequestUri(String requestUri) {
    this.requestUri = requestUri;
  }

  /**
   * @return the remoteAddr
   */
  public String getRemoteAddr() {
    return remoteAddr;
  }

  /**
   * @param remoteAddr the remoteAddr to set
   */
  public void setRemoteAddr(String remoteAddr) {
    this.remoteAddr = remoteAddr;
  }

  /**
   * @return the contextPath
   */
  public String getContextPath() {
    return contextPath;
  }

  /**
   * @param contextPath the contextPath to set
   */
  public void setContextPath(String contextPath) {
    this.contextPath = contextPath;
  }

  /**
   * @return the webModuleName
   */
  public String getWebModuleName() {
    return webModuleName;
  }

  /**
   * @param webModuleName the webModuleName to set
   */
  public void setWebModuleName(String webModuleName) {
    this.webModuleName = webModuleName;
  }

  /**
   * @return the applicationName
   */
  public String getApplicationName() {
    return applicationName;
  }

  /**
   * @param applicationName the applicationName to set
   */
  public void setApplicationName(String applicationName) {
    this.applicationName = applicationName;
  }

  /**
   * @return the projectName
   */
  public String getProjectName() {
    return projectName;
  }

  /**
   * @param projectName the projectName to set
   */
  public void setProjectName(String projectName) {
    this.projectName = projectName;
  }

  /**
   * @return the actionName
   */
  public String getActionName() {
    return actionName;
  }

  /**
   * @param actionName the actionName to set
   */
  public void setActionName(String actionName) {
    this.actionName = actionName;
  }

  public String getRefererPageName() {
    return refererPageName;
  }

  public void setRefererPageName(String refererPageName) {
    this.refererPageName = refererPageName;
  }

  /**
   * @return the actionNameFound
   */
  public int getActionNameFound() {
    return actionNameFound;
  }

  public String getAsXml() {
    StringBuffer sb = new StringBuffer();

    sb.append("<requestctrldata>");
    sb.append("<webmodule source='" + this.webModuleNameFound + "'>" + this.webModuleName + "</webmodule>");
    sb.append("<application source='" + this.applNameFound + "'>" + this.applicationName + "</application>");
    sb.append("<action source='" + this.actionNameFound + "'>" + this.actionName + "</action>");
    sb.append("<project source='" + this.projectNameFound + "'>" + this.projectName + "</project>");
    sb.append("<refererPageName source='" + this.refererPageFound + "'>" + this.refererPageName + "</refererPageName>");
    sb.append("</requestctrldata>");

    return sb.toString();
  }

  public void writeToActionResponse(AbstractAction abstractActionObject) {

    // general parameters
    abstractActionObject.setResponseParameter(ActionResponseItem.TARGET_SESSION_TRANSIENT, "wbf.webModuleName", this.getWebModuleName(), true, false);
    abstractActionObject.setResponseParameter(ActionResponseItem.TARGET_SESSION_TRANSIENT, "wbf.applicationName", this.getApplicationName(), true, false);
    abstractActionObject.setResponseParameter(ActionResponseItem.TARGET_SESSION_TRANSIENT, "wbf.actionName", this.getActionName(), true, false);
    abstractActionObject.setResponseParameter(ActionResponseItem.TARGET_SESSION_TRANSIENT, "wbf.refererPageName", this.getRefererPageName(), true, false);

    abstractActionObject.setResponseParameter(ActionResponseItem.TARGET_SESSION_TRANSIENT, "wbf.contextPath", this.contextPath, true, false);
    abstractActionObject.setResponseParameter(ActionResponseItem.TARGET_SESSION_TRANSIENT, "wbf.queryString", this.queryString, true, false);
    abstractActionObject.setResponseParameter(ActionResponseItem.TARGET_SESSION_TRANSIENT, "wbf.remoteAddr", this.remoteAddr, true, false);
    abstractActionObject.setResponseParameter(ActionResponseItem.TARGET_SESSION_TRANSIENT, "wbf.requestUri", this.requestUri, true, false);

  }

}
