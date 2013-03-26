/*
 * MasterServlet.java
 *
 * Created on 6. Dezember 2002, 12:10
 */

package de.webertise.wbf.weblet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;

import de.reddot.api.common.attribute.CoaAttribute;
import de.reddot.api.common.session.CoaSession;
import de.reddot.api.common.user.CoaUser;
import de.reddot.api.web.io.WebletRequest;
import de.reddot.api.web.io.WebletResponse;
import de.reddot.api.web.weblet.WebletAdapter;
import de.reddot.idea.bb.client.Weblet;
import de.webertise.ds.services.LogServices;
import de.webertise.wbf.admin.utils.Substitutor;
import de.webertise.wbf.base.action.AbstractAction;
import de.webertise.wbf.base.action.ActionForwardDef;
import de.webertise.wbf.base.action.ActionResponse;
import de.webertise.wbf.base.application.Application;
import de.webertise.wbf.base.application.ApplicationSetting;
import de.webertise.wbf.base.module.AbstractModule;
import de.webertise.wbf.base.project.ProjectSetting;
import de.webertise.wbf.base.shortcuts.ShortcutSetting;
import de.webertise.wbf.base.webmodule.WebModule;
import de.webertise.wbf.base.webmodule.WebModuleSetting;

/**
 * 
 * @author bernfried.howe
 * @version
 */
public class MasterWeblet extends WebletAdapter implements MasterWebletConstants {

  // constants
  private final static String                LOG_CATEGORY        = "wbf_weblet";

  // static variables
  public final static WebModuleSetting       webModuleSetting    = new WebModuleSetting();
  public final static ApplicationSetting     applicationSetting  = new ApplicationSetting();
  public final static ProjectSetting         projectSetting      = new ProjectSetting();
  public final static ShortcutSetting        shortcutSetting     = new ShortcutSetting();

  // variables
  private LogServices                        log                 = LogServices.getInstance(LOG_CATEGORY);
  private boolean                            initialized         = false;

  private Map<String, Class<AbstractModule>> loadedModuleClasses = new Hashtable<String, Class<AbstractModule>>();
  private Map<String, AbstractModule>        loadedModules       = new Hashtable<String, AbstractModule>();

  /**
   * Initializes the weblet.
   */
  public void init() {

    // Info log
    log.debug(LOG_CATEGORY, "MasterWeblet - init: Passed Init method.");

    // set init flag to false
    this.initialized = false;

  }

  /**
   * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
   * 
   * @param request weblet request
   * @param response weblet response
   */
  @SuppressWarnings("unchecked")
  public final void handleRequest(CoaSession session, WebletRequest request, WebletResponse response) throws Exception {

    // *************************************************************************
    // * Init Weblet reading project, web module and application settings
    // *************************************************************************
    String sInit = (String) session.getAttribute(MasterWebletConstants.RELOAD_SETTINGS_SESSION_PARAMETER_NAME);
    if (sInit != null && sInit.equals("true")) {
      this.initialized = false;
      session.removeAttribute(MasterWebletConstants.RELOAD_SETTINGS_SESSION_PARAMETER_NAME);
      log.debug(LOG_CATEGORY, "MasterWeblet - handleRequest: Init activated based on session attribute 'wbfInitialize'.");
    }
    if (!this.initialized) {
      if (!initWeblet(session)) {
        // init weblet failed
        log.debug(LOG_CATEGORY, "MasterWeblet - handleRequest: Init weblet failed.");
        createHtmlResponse(response);
        return;
      }
      initialized = true;
    }

    // activate request logging if debug mode is set to true
    log.setRequestLogging(MasterWeblet.webModuleSetting.isDebug());
    log.debug(LOG_CATEGORY, "MasterWeblet - handleRequest: MasterWeblet.webModuleSetting.isDebug() is '" + MasterWeblet.webModuleSetting.isDebug() + "'");

    // *************************************************************************
    // * get controller data from HTTP Servlet Request
    // *************************************************************************
    RequestControllerData reqCtrlData = new RequestControllerData(request, MasterWeblet.projectSetting, MasterWeblet.webModuleSetting, MasterWeblet.applicationSetting);

    // *************************************************************************
    // * create instances of web module and application data
    // *************************************************************************
    // get web module object
    WebModule webModule = MasterWeblet.webModuleSetting.getWebModule(reqCtrlData.getWebModuleName());
    if (webModule == null) {
      log.debug(LOG_CATEGORY, "MasterWeblet - handleRequest: Web module '" + reqCtrlData.getWebModuleName() + "' not defined'.");
      createHtmlResponse(response);
      return;
    }
    // get application object
    Application application = MasterWeblet.applicationSetting.getApplication(reqCtrlData.getApplicationName());
    if (application == null) {
      log.debug(LOG_CATEGORY, "MasterWeblet - handleRequest: Application name '" + reqCtrlData.getApplicationName() + "' not defined.");
      createHtmlResponse(response);
      return;
    }

    // *************************************************************************
    // * create instance of web module implementation
    // *************************************************************************
    Class<AbstractModule> abstractModuleClass = null;
    try {
      // get action class
      log.debug(LOG_CATEGORY, "MasterWeblet - handleRequest: Create new abstract module class based on '" + webModule.getClasspath() + "'");
      abstractModuleClass = loadedModuleClasses.get(webModule.getClasspath());
      if (abstractModuleClass == null) {
        abstractModuleClass = (Class<AbstractModule>) Class.forName(webModule.getClasspath());
        loadedModuleClasses.put(webModule.getClasspath(), abstractModuleClass);
      }
    } catch (ClassNotFoundException e) {
      log.debug(LOG_CATEGORY, "MasterWeblet - handleRequest: ClassNotFoundException: Creation of class '" + webModule.getClasspath() + "' failed.");
      createHtmlResponse(response);
      return;
    }
    AbstractModule abstractModuleObject = null;
    try {
      abstractModuleObject = loadedModules.get(webModule.getClasspath());
      if (abstractModuleObject == null) {
        abstractModuleObject = (AbstractModule) abstractModuleClass.newInstance();
        loadedModules.put(webModule.getClasspath(), abstractModuleObject);
      }
    } catch (InstantiationException e) {
      log.debug(LOG_CATEGORY, "MasterWeblet - handleRequest: InstantiationException: Creation of AbstractModule instance for '" + webModule.getClasspath() + "' failed.");
      createHtmlResponse(response);
      return;
    } catch (IllegalAccessException e) {
      log.debug(LOG_CATEGORY, "MasterWeblet - handleRequest: IllegalAccessException: Creation of AbstractModule instance for '" + webModule.getClasspath() + "' failed.");
      createHtmlResponse(response);
      return;
    }

    // *************************************************************************
    // * check ip security
    // *************************************************************************
    // check if request comes from localhost or 127.0.0.1
    if (!request.getHttpServletRequest().getRemoteAddr().equals("0:0:0:0:0:0:0:1") && !request.getHttpServletRequest().getRemoteAddr().equals("127.0.0.1")) {
      boolean ipAuthorized = true;
      String ipAddressFromHeader = request.getHttpServletRequest().getHeader("REMOTE_ADDR");
      String ipAddressFromRequest = request.getHttpServletRequest().getRemoteAddr();
      log.debug(LOG_CATEGORY, "MasterWeblet - handleRequest: Check IP-Address Security with IP Address (Header REMOTE_ADDR) '" + ipAddressFromHeader + "' or request.getRemoteAddr() ' "
          + ipAddressFromRequest + "'.");
      if (ipAddressFromHeader != null && !ipAddressFromHeader.equals("")) {
        ipAuthorized = webModule.checkIpSecurity(ipAddressFromHeader);
      } else {
        ipAuthorized = webModule.checkIpSecurity(ipAddressFromRequest);
      }
      if (!ipAuthorized) {
        log.debug(LOG_CATEGORY, "MasterWeblet - handleRequest: IP-Security check failed for ipAddress '" + ipAddressFromHeader + " or " + ipAddressFromRequest + "'.");
        createHtmlResponse(response);
        return;
      }
    } else {
      log.debug(LOG_CATEGORY, "MasterWeblet - handleRequest: IP-Address Security Check not needed as remote host is localhost: '" + request.getHttpServletRequest().getRemoteHost() + "'.");
    }

    // *************************************************************************
    // * write list of projects and applications always to request and other
    // * general parameters always to request
    // *************************************************************************
    request.setParameter("general-applications", applicationSetting.getApplicationNames("|"));
    request.setParameter("general-projects", projectSetting.getProjectNames("|"));
    if (webModuleSetting.isWbfDefaultWeblet()) {
      request.setParameter("wbfWeblet", "");
    } else {
      request.setParameter("wbfWeblet", MasterWebletConstants.WEBLET_ALIAS + "/");
    }

    // *************************************************************************
    // * call web module handleRequest method
    // *************************************************************************
    ActionResponse actionResponse = abstractModuleObject.handleRequest(request, response, session, webModuleSetting, applicationSetting, reqCtrlData);
    if (actionResponse == null) {
      log.debug(LOG_CATEGORY, "MasterWeblet - handleRequest: for application '" + reqCtrlData.getApplicationName() + "' and action '" + reqCtrlData.getActionName() + "' failed.");
      createHtmlResponse(response);
      return;
    }

    // *************************************************************************
    // * transform ActionResponse to xml and write to request/session
    // *************************************************************************
    String prefix = reqCtrlData.getWebModuleName() + "." + reqCtrlData.getApplicationName() + "." + reqCtrlData.getActionName() + ".";
    log.debug(LOG_CATEGORY, "MasterWeblet - handleRequest: request/session attribute prefix is '" + prefix + "'.");
    actionResponse.transferResponse(session, request, prefix);

    // *************************************************************************
    // * get actionForward
    // *************************************************************************
    ActionForwardDef afDef = actionResponse.getActionForward();

    // *************************************************************************
    // * check if action forward needs some placeholders to be replaced
    // *************************************************************************
    String view = Substitutor.substituteVariables(afDef.getView(), actionResponse.getResponse(), "[#", "#]");
    log.debug(LOG_CATEGORY, "MasterWeblet - handleRequest: replaced view string '" + afDef.getView() + "' with '" + view + "'");

    // *************************************************************************
    // * check debug mode
    // *************************************************************************
    if (webModuleSetting.isDebug()) {
      writeDebugInfo(session, request, reqCtrlData, webModule, application, afDef, log.getRequestLoggerData());
    }

    // *************************************************************************
    // * forward/redirect page
    // *************************************************************************
    if (afDef.getType() == ActionForwardDef.ACTION_FORWARD_TYPE_ACTION_REDIRECT) {
      // *************************************************************************
      // * send redirect to another action
      // *************************************************************************
      String qrFinal = reqCtrlData.getContextPath() + "/" + WEBLET_ALIAS + "/" + reqCtrlData.getProjectName() + "/" + reqCtrlData.getWebModuleName() + "/" + reqCtrlData.getApplicationName() + "/"
          + afDef.getAction();
      log.debug(LOG_CATEGORY, "MasterWeblet - handleRequest: send weblet action redirect to '" + qrFinal + "'");
      response.sendRedirect(qrFinal);

    } else if (afDef.getType() == ActionForwardDef.ACTION_FORWARD_TYPE_WEBLET_REDIRECT) {
      // *************************************************************************
      // * send redirect to another page
      // *************************************************************************
      String qrFinal = reqCtrlData.getContextPath() + "/" + reqCtrlData.getProjectName() + "/" + afDef.getTemplate() + "/-/" + view;
      log.debug(LOG_CATEGORY, "MasterWeblet - handleRequest: send weblet redirect to '" + qrFinal + "'");
      response.sendRedirect(qrFinal);

    } else if (afDef.getType() == ActionForwardDef.ACTION_FORWARD_TYPE_CALL_XCHG_WEBLET || afDef.getType() == ActionForwardDef.ACTION_FORWARD_TYPE_CALL_XCHG_WEBLET_TO_REFERER) {
      // *************************************************************************
      // * call xchg weblet or redirect
      // *************************************************************************
      Map<String, String> paraMap = (Map<String, String>) request.getHttpServletRequest().getParameterMap();

      // create final url for xchg weblet
      String qrFinal = reqCtrlData.getProjectName() + "/" + afDef.getTemplate() + "/-/" + view;
      if (afDef.getType() == ActionForwardDef.ACTION_FORWARD_TYPE_CALL_XCHG_WEBLET_TO_REFERER) {
        if (reqCtrlData.getRefererPageName() != null && !reqCtrlData.getRefererPageName().equals("")) {
          qrFinal = reqCtrlData.getProjectName() + "/" + application.getDefaultTemplate() + "/-/" + reqCtrlData.getRefererPageName();
          log.debug(LOG_CATEGORY, "MasterWeblet - handleRequest: callWeblet to referer called ('" + reqCtrlData.getRefererPageName() + "').");
        } else {
          log.debug(LOG_CATEGORY, "MasterWeblet - handleRequest: callWeblet to referer '" + reqCtrlData.getRefererPageName() + "'");
          createHtmlResponse(response);
          return;
        }
      }

      log.debug(LOG_CATEGORY, "********** Forward to formerly requested page: '" + qrFinal + "' via callWeblet 'xchg'");
      if (request.getHttpServletRequest().getMethod().equalsIgnoreCase("get")) {
        this.callWeblet(session, request, response, "xchg", qrFinal, request.getHttpServletRequest().getQueryString(), Weblet.CALLWEBLET_USERDEFINED + Weblet.CALLWEBLET_HTTPHEADER);
      } else {
        this.callWeblet(session, request, response, "xchg", qrFinal, paraMap, Weblet.CALLWEBLET_USERDEFINED + Weblet.CALLWEBLET_HTTPHEADER);
      }
    } else {
      // *************************************************************************
      // * action forward invalid
      // *************************************************************************
      log.debug(LOG_CATEGORY, "MasterWeblet - handleRequest: handleRequest for application '" + reqCtrlData.getApplicationName() + "' and action '" + reqCtrlData.getActionName() + "' failed.");
      createHtmlResponse(response);
    }

  }

  private boolean initWeblet(CoaSession session) {

    boolean initState = true;

    // *************************************************************************
    // * read the web module settings
    // *************************************************************************
    boolean resultProjectSettings = MasterWeblet.projectSetting.readProjectsFromRegistry();
    if (!resultProjectSettings) {
      log.debug(LOG_CATEGORY, "MasterWeblet - handleRequest: Init of Project Settings failed.");
      initState = false;
    } else {
      log.debug(LOG_CATEGORY, "MasterWeblet - handleRequest: Init of Project Settings succeeded. " + MasterWeblet.projectSetting.getProjects().size() + " projects found.");
    }

    // *************************************************************************
    // * read the web module settings
    // *************************************************************************
    boolean resultWebModuleSettings = MasterWeblet.webModuleSetting.readWebModuleSettingsFromContent(session, WBF_PROJECT_NAME, WBF_WEB_MODULE_SETTINGS_CONTENT_NAME);
    if (!resultWebModuleSettings) {
      log.debug(LOG_CATEGORY, "MasterWeblet - handleRequest: Init of Web Module Settings failed.");
      initState = false;
    } else {
      log.debug(LOG_CATEGORY, "MasterWeblet - handleRequest: Init of Web Module Settings succeeded. " + MasterWeblet.webModuleSetting.getWebModules().size() + " webmodules found.");
    }

    // *************************************************************************
    // * read the application settings
    // *************************************************************************
    boolean resultApplicationSettings = MasterWeblet.applicationSetting.readApplicationSettingsFromContent(session, WBF_PROJECT_NAME, WBF_WEB_APPLICATION_SETTINGS_CONTENT_NAME);
    if (!resultApplicationSettings) {
      log.debug(LOG_CATEGORY, "MasterWeblet - handleRequest: Init of Web Application Settings failed.");
      initState = false;
    } else {
      log.debug(LOG_CATEGORY, "MasterWeblet - handleRequest: Init of Web Application Settings succeeded. " + MasterWeblet.applicationSetting.getApplications().size() + " applications found.");
    }

    // *************************************************************************
    // * read the shortcut settings
    // *************************************************************************
    boolean resultShortcutSettings = MasterWeblet.shortcutSetting.readShortcutSettingsFromContent(session, WBF_PROJECT_NAME, WBF_SHORTCUT_SETTINGS_CONTENT_NAME);
    if (!resultShortcutSettings) {
      log.debug(LOG_CATEGORY, "MasterWeblet - handleRequest: Init of Shortcut Settings failed.");
      initState = false;
    } else {
      log.debug(LOG_CATEGORY, "MasterWeblet - handleRequest: Init of Shortcut Settings succeeded. " + MasterWeblet.shortcutSetting.getShortcuts().size() + " shortcuts found.");
    }

    return initState;

  }

  // *************************************************************************
  // * Returns some information of the weblet
  // *************************************************************************
  public String getWebletInfo() {
    return "MasterWeblet of wbtFramework";
  }

  // *************************************************************************
  // * Creates some html for a error message via HttpServletResponse
  // *************************************************************************
  public void createHtmlResponse(WebletResponse response) {
    HttpServletResponse httpResponse = response.getHttpServletResponse();
    PrintWriter out = null;
    try {
      out = httpResponse.getWriter();
    } catch (IOException io) {
      String msg = "IOException: HttpServletResponse PrintWriter could not be accessed.";
      log.debug(LOG_CATEGORY, "MasterWeblet - handleRequest" + msg);
    }
    httpResponse.setContentType("text/html");
    out.println("<htmtl><head><title>Error</title>");
    out.println("<body>System is not available. Please contact your administrator.</body></html>");
  }

  private void writeDebugInfo(CoaSession session, WebletRequest request, RequestControllerData reqCtrlData, WebModule webModule, Application application, ActionForwardDef afDef,
      ArrayList<String> loggerData) {

    request.setParameter("logDebugMode", "on");
    log.debug(LOG_CATEGORY, "MasterWeblet - writeDebugInfo: reached");

    StringBuffer sb = new StringBuffer();

    // create root element
    sb.append("<debuginfo>");

    // create xml for all session attributes
    @SuppressWarnings("unchecked")
    Enumeration<String> enumSessionAttr = session.getAttributeKeysEnumeration();
    sb.append("<session>");
    while(enumSessionAttr.hasMoreElements()) {
      String key = enumSessionAttr.nextElement();
      String value = "";
      if (session.getAttribute(key) instanceof String) {
        value = (String) session.getAttribute(key);
      } else {
        value = "" + session.getAttribute(key);
      }
      if (key.toLowerCase().endsWith("password") && !key.endsWith(AbstractAction.ERROR_CODE_POSTFIX)) {
        value = "*******";
      }
      sb.append("<attribute path='" + key + "'><![CDATA[" + StringEscapeUtils.escapeHtml(value) + "]]></attribute>");
    }
    sb.append("</session>");
    log.debug(LOG_CATEGORY, "MasterWeblet - writeDebugInfo: created session xml");

    // create xml for all request attributes
    @SuppressWarnings("unchecked")
    Enumeration<String> enumRequestAttr = request.getParameterNames();
    sb.append("<request>");
    while(enumRequestAttr.hasMoreElements()) {
      String key = enumRequestAttr.nextElement();
      String value = (String) request.getParameter(key);
      if (key.toLowerCase().contains("password") && !key.endsWith(AbstractAction.ERROR_CODE_POSTFIX)) {
        value = "*******";
      }
      sb.append("<attribute path='" + key + "'><![CDATA[" + StringEscapeUtils.escapeHtml(value) + "]]></attribute>");
    }
    sb.append("</request>");
    log.debug(LOG_CATEGORY, "MasterWeblet - writeDebugInfo: created request xml");

    // create xml for all user attributes
    CoaUser user = session.getCoaUser();
    @SuppressWarnings("unchecked")
    Enumeration<CoaAttribute> enumUserAttr = user.getCoaAttributeEnumeration(true);
    sb.append("<user>");
    while(enumUserAttr.hasMoreElements()) {
      CoaAttribute attr = enumUserAttr.nextElement();
      String key = attr.getPath();
      String[] values = (String[]) attr.getValues();
      String value = "";
      if (key.toLowerCase().contains("password") && !key.endsWith(AbstractAction.ERROR_CODE_POSTFIX) && !key.endsWith("_length")) {
        values = new String[1];
        values[0] = "*******";
      } else {
        for (int i = 0; i < values.length; i++) {
          value += values[i];
        }
      }
      sb.append("<attribute path='" + key + "'><![CDATA[" + StringEscapeUtils.escapeHtml(value) + "]]></attribute>");
    }
    sb.append("</user>");
    log.debug(LOG_CATEGORY, "MasterWeblet - writeDebugInfo: created user xml");

    // get xml for request controller data
    sb.append(reqCtrlData.getAsXml());
    log.debug(LOG_CATEGORY, "MasterWeblet - writeDebugInfo: created reqCtrlData xml");

    // get xml for action forward definition
    sb.append(afDef.getAsXml());
    log.debug(LOG_CATEGORY, "MasterWeblet - writeDebugInfo: created Action Forward Definition xml");

    // add logger data
    if (loggerData != null) {
      Iterator<String> iter = loggerData.iterator();
      sb.append("<logger>");
      while(iter.hasNext()) {
        sb.append("<line><![CDATA[" + StringEscapeUtils.escapeHtml(iter.next()) + "]]></line>");
      }
      sb.append("</logger>");
    }
    log.debug(LOG_CATEGORY, "MasterWeblet - writeDebugInfo: created logger xml");

    // close root element
    sb.append("</debuginfo>");

    // write to request
    log.trace(LOG_CATEGORY, "MasterWeblet - writeDebugInfo: " + sb.toString());
    request.setParameter("debuginfo", sb.toString());

  }
}