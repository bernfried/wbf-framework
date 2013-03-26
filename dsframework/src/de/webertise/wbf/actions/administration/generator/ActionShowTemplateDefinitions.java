package de.webertise.wbf.actions.administration.generator;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import de.reddot.api.common.content.CoaContent;
import de.reddot.api.common.content.CoaContentController;
import de.reddot.api.common.content.CoaContentControllerFactory;
import de.reddot.api.common.content.CoaContentGroup;
import de.reddot.api.common.content.CoaContentGroupController;
import de.reddot.api.common.session.CoaSession;
import de.reddot.api.exception.AccessNotGrantedException;
import de.reddot.api.web.io.WebletRequest;
import de.webertise.wbf.base.action.ActionResponseItem;
import de.webertise.wbf.actions.ActionConstants;
import de.webertise.wbf.weblet.MasterWebletConstants;

/**
 * 
 * @author bernfried howe
 */
public class ActionShowTemplateDefinitions extends de.webertise.wbf.base.action.AbstractAction {

  /** Creates a new instance of ActionInit */
  public ActionShowTemplateDefinitions() {
  }

  public boolean execute(CoaSession session, WebletRequest request) {

    log.debug(LOG_CATEGORY, "ActionShowTemplateDefinitions - execute: Reached.");

    // *******************************************************************************************
    // * read all defined templates (content groups under gen-templates)
    // *******************************************************************************************
    CoaContentGroupController cgCtrl = CoaContentControllerFactory.getCoaContentGroupController();
    CoaContentGroup cg = null;
    try {
      cg = cgCtrl.getCoaContentGroupTree(session, MasterWebletConstants.WBF_PROJECT_NAME, "gen-templates", 2);
    } catch (AccessNotGrantedException e) {
      super.setActionForwardName("nok");
      this.setErrorCode("gen-templates", ActionConstants.ACTION_CUSTOM_VALIDATION_ERROR_10);
      this.setErrorCode(Integer.toString(ACTION_EXECUTE_FAILED));
      return true;
    }

    // iterate through all children and create a response list
    if (cg != null) {
      // result hashtable
      Hashtable<String, String> result = new Hashtable<String, String>();
      log.debug(LOG_CATEGORY, "ActionShowTemplateDefinitions - execute: Get children of content group: " + cg.getPath());

      @SuppressWarnings("unchecked")
      Iterator<CoaContentGroup> cgChildren = cg.getChildren();
      while(cgChildren.hasNext()) {
        CoaContentGroup child = cgChildren.next();
        result.put(child.getName(), child.getDescription());
        log.debug(LOG_CATEGORY, "ActionShowTemplateDefinitions - execute: added '" + child.getName() + " - " + child.getDescription() + "' to result list.");
      }
      // return result list
      this.setResponseParameter(ActionResponseItem.TARGET_SESSION_TRANSIENT, "definitions", result, false, false);
    }

    // *******************************************************************************************
    // * read all defined instances (content groups under gen-templates/<def.template>/instances)
    // *******************************************************************************************
    String defName = request.getParameter("definition");
    if (defName != null && !defName.equals("")) {
      // read existing instances
      try {
        cg = cgCtrl.getCoaContentGroupTree(session, MasterWebletConstants.WBF_PROJECT_NAME, "gen-templates/" + defName + "/instances", 2);
      } catch (AccessNotGrantedException e) {
        super.setActionForwardName("nok");
        this.setErrorCode("instances", ActionConstants.ACTION_CUSTOM_VALIDATION_ERROR_10);
        this.setErrorCode(Integer.toString(ACTION_EXECUTE_FAILED));
        return true;
      }
      // iterate through all children and create a response list
      if (cg != null) {
        // result hashtable
        Hashtable<String, String> result = new Hashtable<String, String>();
        log.debug(LOG_CATEGORY, "ActionShowTemplateDefinitions - execute: Get children of content group: " + cg.getPath());

        @SuppressWarnings("unchecked")
        Iterator<CoaContentGroup> cgChildren = cg.getChildren();
        while(cgChildren.hasNext()) {
          CoaContentGroup child = cgChildren.next();
          result.put(child.getName(), child.getName());
          log.debug(LOG_CATEGORY, "ActionShowTemplateDefinitions - execute: added '" + child.getName() + " - " + child.getName() + "' to result list.");
        }
        // return result list
        this.setResponseParameter(ActionResponseItem.TARGET_SESSION_TRANSIENT, "instances", result, false, false);
      }

      // *******************************************************************************************
      // * read all settings of the selected instance (if selected)
      // *******************************************************************************************
      String instanceName = request.getParameter("instance");
      if (instanceName != null && !instanceName.equals("")) {
        // read definition.xml content from instance content group
        String path = "gen-templates/" + defName + "/instances/" + instanceName + "/definition.xml";
        log.debug(LOG_CATEGORY, "ActionShowTemplateDefinitions - execute: Read attributes from '" + path + "'");
        // read general settings
        String result = getTemplateDefinitionAsXml(session, "root", MasterWebletConstants.WBF_PROJECT_NAME, path, Locale.ENGLISH);
        this.setResponseParameter(ActionResponseItem.TARGET_SESSION_TRANSIENT, "instance", result, false, false);
      }
    }
    
    // *******************************************************************************************
    // * provide available project/application names in response for drop-down list
    // *******************************************************************************************
    String projects = (String) this.getRequestParameter("general-projects");
    if (projects!=null) {
      List<String> pList = new ArrayList<String>();
      StringTokenizer stProj = new StringTokenizer(projects,"|");
      while (stProj.hasMoreTokens()) {
        pList.add(stProj.nextToken());
      }
      this.setResponseParameter(ActionResponseItem.TARGET_SESSION_TRANSIENT, "available-projects", pList, false, false);
    }
    String apps = (String) this.getRequestParameter("general-applications");
    if (apps!=null) {
      List<String> aList = new ArrayList<String>();
      StringTokenizer stApp = new StringTokenizer(apps,"|");
      while (stApp.hasMoreTokens()) {
        aList.add(stApp.nextToken());
      }
      this.setResponseParameter(ActionResponseItem.TARGET_SESSION_TRANSIENT, "available-applications", aList, false, false);
    }

    // finish action
    super.setActionForwardName("ok");
    this.setErrorCode(Integer.toString(ACTION_EXECUTE_RESULT_OK));
    return true;
  }

  public boolean validate(CoaSession session, WebletRequest request) {
    log.debug(LOG_CATEGORY, "ActionShowTemplateDefinitions - validate: Reached.");
    // get request parameter
    validateAllRequestParameters(session, request);
    return true;
  }

  private String getTemplateDefinitionAsXml(CoaSession session, String attrRootPath, String project, String fileName, Locale locale) {

    // declarations
    CoaContent content = null;
    StringBuffer sb = new StringBuffer();

    // get a CoaContenController
    CoaContentController contentController = CoaContentControllerFactory.getCoaContentController();

    // get content
    try {
      if (contentController.coaContentExists(session, project, fileName, CoaContent.STATUS_FINAL_ONLY)) {
        content = contentController.readCoaContent(session, project, fileName, CoaContent.STATUS_FINAL_ONLY);
      } else {
        log.debug(LOG_CATEGORY, "Content '" + project + "/" + fileName + "' not found.");
      }
    } catch (AccessNotGrantedException e) {
      log.debug(LOG_CATEGORY, "AccessNotGrantedException for Content '" + project + "/" + fileName + "'.");
    }

    if (content != null) {
      @SuppressWarnings("unchecked")
      Iterator<String> attrIter = content.getAttributePaths(attrRootPath, true);
      sb.append("<instance>");
      while(attrIter.hasNext()) {
        String attrPath = attrIter.next();
        String attrValues[] = content.getAttributeValues(attrRootPath + "." + attrPath, locale);
        String attrValue = "";
        if (attrValues != null) {
          if (attrValues.length > 1) {
            for (int i=0; i<attrValues.length; i++) {
              attrValue += "[" + attrValues[i] + "]";
            }
          } else {
            attrValue = attrValues[0];
          }
        }
        int pos = attrPath.lastIndexOf(".");
        String name = attrPath.substring(pos + 1);

        // match attributes
        if (attrPath.matches("^attributes.([a-zA-Z0-9])*$")) {
          sb.append("<attribute path='root." + attrPath + "'>" + attrValue + "</attribute>");

          // match attribute settings
        } else if (attrPath.matches("^attributes.([a-zA-Z0-9])*.([a-zA-Z0-9])*$")) {
          sb.append("<attribute-setting path='root." + attrPath + "' name='" + name + "'>" + attrValue + "</attribute-setting>");

          // match action
        } else if (attrPath.matches("^actions.([a-zA-Z0-9])*$")) {
          sb.append("<action path='root." + attrPath + "'>" + name + "</action>");

          // match action settings
        } else if (attrPath.matches("^actions.([a-zA-Z0-9])*.([a-zA-Z0-9])*$")) {
          sb.append("<action-setting path='root." + attrPath + "' name='" + name + "'>" + attrValue + "</action-setting>");

          // match forwards
        } else if (attrPath.matches("^actions.([a-zA-Z0-9])*.forwards.([a-zA-Z0-9])*$")) {
          sb.append("<forward path='root." + attrPath + "'>" + name + "</forward>");

          // match forward setting
        } else if (attrPath.matches("^actions.([a-zA-Z0-9])*.forwards.([a-zA-Z0-9])*.([a-zA-Z0-9])*$")) {
          sb.append("<forward-setting path='root." + attrPath + "' name='" + name + "'>" + attrValue + "</forward-setting>");

          // general settings
        } else {
          sb.append("<general-setting path='root." + attrPath + "'>" + attrValue + "</general-setting>");
        }
      }
      sb.append("</instance>");
    }
    return sb.toString();
  }

}
