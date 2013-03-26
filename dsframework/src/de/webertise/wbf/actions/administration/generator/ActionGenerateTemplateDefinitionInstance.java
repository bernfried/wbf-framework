package de.webertise.wbf.actions.administration.generator;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.reddot.api.common.content.CoaContent;
import de.reddot.api.common.content.CoaContentController;
import de.reddot.api.common.content.CoaContentControllerFactory;
import de.reddot.api.common.content.CoaContentData;
import de.reddot.api.common.content.CoaContentFilter;
import de.reddot.api.common.content.CoaContentGroup;
import de.reddot.api.common.content.CoaContentGroupController;
import de.reddot.api.common.content.CoaContentPage;
import de.reddot.api.common.session.CoaSession;
import de.reddot.api.exception.AccessNotGrantedException;
import de.reddot.api.web.io.WebletRequest;
import de.webertise.ds.services.ContentServices;
import de.webertise.wbf.actions.ActionConstants;
import de.webertise.wbf.admin.generator.TemplateDefinition;
import de.webertise.wbf.admin.generator.TemplateDefinitionAction;
import de.webertise.wbf.admin.generator.TemplateDefinitionAttribute;
import de.webertise.wbf.base.action.ActionForwardDef;
import de.webertise.wbf.weblet.MasterWebletConstants;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * 
 * @author bernfried howe
 */
public class ActionGenerateTemplateDefinitionInstance extends de.webertise.wbf.base.action.AbstractAction {
  
  private final static String LOG_CATEGORY_GENERATOR = "wbf_generator";

  /** Creates a new instance of ActionGenerateTemplateDefinitionInstance */
  public ActionGenerateTemplateDefinitionInstance() {
  }

  public boolean execute(CoaSession session, WebletRequest request) {

    log.debug(LOG_CATEGORY, "ActionGenerateTemplateDefinitionInstance - execute: Reached.");

    //*******************************************************************************************
    //* read request parameter
    //*******************************************************************************************
    String definition = (String) this.getRequestParameter("definition");
    String instance = (String) this.getRequestParameter("instance");

    //*******************************************************************************************
    //* read definition of selected instance
    //*******************************************************************************************
    String ctntGroupPath = "gen-templates/" + definition + "/instances/" + instance + "/";
    Hashtable<String,String> attr = ContentServices.getContentAttributesAsHashtable(session, "root", MasterWebletConstants.WBF_PROJECT_NAME, ctntGroupPath + "definition.xml", Locale.ENGLISH);
  
    if (attr!=null) {
      log.debug(LOG_CATEGORY_GENERATOR, "ActionGenerateTemplateDefinitionInstance - execute: content attributes read from content: '" + ctntGroupPath + "definition.xml'" );      
      
      //*******************************************************************************************
      //* read root content group of template definition instance and all child content groups 
      //* where ftl templates can be found      
      //*******************************************************************************************
      CoaContentGroupController cgCtrl = CoaContentControllerFactory.getCoaContentGroupController();
      CoaContentGroup cg = null; 
      try {
        cg = cgCtrl.getCoaContentGroupTree(session, MasterWebletConstants.WBF_PROJECT_NAME, "gen-templates/" + definition, 10);
      } catch (AccessNotGrantedException e) {
        log.debug(LOG_CATEGORY_GENERATOR, "ActionGenerateTemplateDefinitionInstance - execute: read content group '" + ctntGroupPath + "' with children failed.");
        this.setActionForwardName("nok");
        this.setErrorCode("templates.generate", ActionConstants.ACTION_CUSTOM_VALIDATION_ERROR_10);
        this.setErrorCode(Integer.toString(ACTION_EXECUTE_FAILED));
        return true;
      }

      //*******************************************************************************************
      //* store all found content groups in a Hashtable
      //*******************************************************************************************
      List<String> groups = new ArrayList<String>();
      log.debug(LOG_CATEGORY_GENERATOR, "ActionGenerateTemplateDefinitionInstance - execute: Get children of content group: " + cg.getPath());
      getTemplateDefinitionContentGroups(cg, groups);

      //*******************************************************************************************
      //* create template definitions (makes access to attributes and actions easier)
      //*******************************************************************************************
      TemplateDefinition td = new TemplateDefinition();
      Enumeration<String> keys = attr.keys();
      while (keys.hasMoreElements()) {
        String key = keys.nextElement();
        String value = attr.get(key);
        log.debug(LOG_CATEGORY_GENERATOR, "ActionGenerateTemplateDefinitionInstance - execute: create template definition attribute or action for: " + key + " / " + value);
        td.addAttribute(key, value);
      }

      //*******************************************************************************************
      //* find and execute all templates
      //*******************************************************************************************
      boolean result = executeTemplates(session, definition, attr, groups, td);
      if (!result) {
        log.debug(LOG_CATEGORY_GENERATOR, "ActionGenerateTemplateDefinitionInstance - execute: ERROR - generation based on templates failed.");
        this.setActionForwardName("nok");
        this.setErrorCode("templates.generate", ActionConstants.ACTION_CUSTOM_VALIDATION_ERROR_11);
        this.setErrorCode(Integer.toString(ACTION_EXECUTE_FAILED));
        return true;
      }
      
      //*******************************************************************************************
      //* create all action declarations in WebApplicationSettings
      //*******************************************************************************************
      createActionSettings(session, attr, td);
      
    } else {
      // definition.xml could not be read correctly - no attributes found.
      log.debug(LOG_CATEGORY_GENERATOR, "ActionGenerateTemplateDefinitionInstance - execute: ERROR - definition.xml could not be read correctly - no attributes found.");
      this.setActionForwardName("nok");
      this.setErrorCode("templates.generate", ActionConstants.ACTION_CUSTOM_VALIDATION_ERROR_12);
      this.setErrorCode(Integer.toString(ACTION_EXECUTE_FAILED));
      return true;
    }
    
    this.setActionForwardName("ok");
    this.setErrorCode(Integer.toString(ACTION_EXECUTE_RESULT_OK));
    return true;
  }

  private void createActionSettings(CoaSession session, Hashtable<String, String> attr, TemplateDefinition td) {
    
    // get controller and application name
    String ctrlName = attr.get("root.controllerName");
    String targetApp = attr.get("root.targetApplication");
    
    // get web application settings content
    ContentServices cs = new ContentServices();
    CoaContent ctntWAS = cs.getContent(session, MasterWebletConstants.WBF_PROJECT_NAME, "", "WebApplicationSettings");
    
    // iterate through all actions and create attributes 
    String actionPathPrefix = "webApplicationSettings.applications." + targetApp + ".actions.";
    log.debug(LOG_CATEGORY_GENERATOR, "ActionGenerateTemplateDefinitionInstance - createActionSettings: action attribute path prefix '" + actionPathPrefix + "'");
    
    Hashtable<String,TemplateDefinitionAction> tdActions = (Hashtable<String,TemplateDefinitionAction>) td.getActions();
    Enumeration<String> actionKeys = tdActions.keys();
    while (actionKeys.hasMoreElements()) {
      String actionKey = actionKeys.nextElement();
      TemplateDefinitionAction tdAction = tdActions.get(actionKey);
      String newActionPath = actionPathPrefix + tdAction.getNameWithUpperCaseLetter() + ctrlName;
      log.debug(LOG_CATEGORY_GENERATOR, "ActionGenerateTemplateDefinitionInstance - createActionSettings: action attribute path for action '" + tdAction.getName() + "' is '" + newActionPath + "'");
      
      ctntWAS.setAttribute(newActionPath, Locale.ENGLISH, "");
      ctntWAS.setAttribute(newActionPath + ".class", Locale.ENGLISH, tdAction.getPackageName() + ".Action" + tdAction.getNameWithUpperCaseLetter());
      ctntWAS.setAttribute(newActionPath + ".group", Locale.ENGLISH, tdAction.getGroup());
      ctntWAS.setAttribute(newActionPath + ".forwards", Locale.ENGLISH, "");
      ctntWAS.setAttribute(newActionPath + ".validationRules", Locale.ENGLISH, "");

      // create validation rules
      Hashtable<String,TemplateDefinitionAttribute> tdAttrs = (Hashtable<String,TemplateDefinitionAttribute>) td.getAttributes();
      Enumeration<String> tdAttrKeys = tdAttrs.keys();
      while (tdAttrKeys.hasMoreElements()) {
        String tdAttrKey = (String) tdAttrKeys.nextElement();
        TemplateDefinitionAttribute tdAttr = tdAttrs.get(tdAttrKey);
        log.debug(LOG_CATEGORY_GENERATOR, "ActionGenerateTemplateDefinitionInstance - createActionSettings: does attribute list '" + tdAction.getAttributes() + "' contain '" + tdAttr.getName() + "'");
        if (tdAction.hasAttribute(tdAttr.getName())) {
          ctntWAS.setAttribute(newActionPath + ".validationRules." + tdAttr.getName() + ".mandatory", Locale.ENGLISH, tdAttr.getMandatory());
          ctntWAS.setAttribute(newActionPath + ".validationRules." + tdAttr.getName() + ".minLength", Locale.ENGLISH, Integer.toString(tdAttr.getMinLength()));
          ctntWAS.setAttribute(newActionPath + ".validationRules." + tdAttr.getName() + ".maxLength", Locale.ENGLISH, Integer.toString(tdAttr.getMaxLength()));
          ctntWAS.setAttribute(newActionPath + ".validationRules." + tdAttr.getName() + ".pattern", Locale.ENGLISH, tdAttr.getPattern());
        }
      }
      
      // create action forwards
      Hashtable<String,ActionForwardDef> tdForwards = (Hashtable<String,ActionForwardDef>) tdAction.getForwards();
      Enumeration<String> tdForwardKeys = tdForwards.keys();
      while (tdForwardKeys.hasMoreElements()) {
        String tdForwardKey = tdForwardKeys.nextElement();
        ActionForwardDef afDef = tdForwards.get(tdForwardKey);
        log.debug(LOG_CATEGORY_GENERATOR, "ActionGenerateTemplateDefinitionInstance - createActionSettings: read forward '" + afDef.getName() + "' with type '" + afDef.getType() + "'");
        ctntWAS.setAttribute(newActionPath + ".forwards." + afDef.getName(), Locale.ENGLISH, "");
        ctntWAS.setAttribute(newActionPath + ".forwards." + afDef.getName() + ".type", Locale.ENGLISH, Integer.toString(afDef.getType()));
        if (afDef.getType() == ActionForwardDef.ACTION_FORWARD_TYPE_CALL_XCHG_WEBLET || 
            afDef.getType() == ActionForwardDef.ACTION_FORWARD_TYPE_CALL_XCHG_WEBLET_TO_REFERER || 
            afDef.getType() == ActionForwardDef.ACTION_FORWARD_TYPE_WEBLET_REDIRECT) {
          ctntWAS.setAttribute(newActionPath + ".forwards." + afDef.getName() + ".view", Locale.ENGLISH, afDef.getView().replace("CONTROLLERNAME", ctrlName));
          ctntWAS.setAttribute(newActionPath + ".forwards." + afDef.getName() + ".template", Locale.ENGLISH, afDef.getTemplate());
        } else {
          ctntWAS.setAttribute(newActionPath + ".forwards." + afDef.getName() + ".action", Locale.ENGLISH, afDef.getAction().replace("CONTROLLERNAME", ctrlName));
        }
      }
    }
    
    // update content
    cs.updateContent(session, ctntWAS);
  }

  
  private void getTemplateDefinitionContentGroups(CoaContentGroup cg, List<String> groups) {
    @SuppressWarnings("unchecked")
    Iterator<CoaContentGroup> cgChildren = cg.getChildren();
    while (cgChildren.hasNext()) {
      CoaContentGroup child = cgChildren.next();
      groups.add(child.getPath());
      log.debug(LOG_CATEGORY_GENERATOR, "ActionGenerateTemplateDefinitionInstance - execute: added '" + child.getName() + " - " + child.getPath() + "' to result list.");
      getTemplateDefinitionContentGroups(child, groups);
    }
  } 

  
  private boolean executeTemplates(CoaSession session, String definition, Hashtable<String, String> attr, List<String> groups, TemplateDefinition td) {
   
    //**************************************************************************************************
    //* iterate through all groups, read all .ftl contents and process templates based on defintion.xml
    //**************************************************************************************************
    for (int i=0; i<groups.size(); i++) {
      log.debug(LOG_CATEGORY_GENERATOR, "ActionGenerateTemplateDefinitionInstance - executeTemplates: process content group '" + groups.get(i) + "'");

      //**************************************************************************************************
      //* filter content based on project name and content group
      //**************************************************************************************************
      CoaContentController ctntCtrl = CoaContentControllerFactory.getCoaContentController();
      CoaContentFilter filter = ctntCtrl.createContentFilter(MasterWebletConstants.WBF_PROJECT_NAME);
      filter.setContentGroupPath(groups.get(i));
      CoaContentPage page = null;
      try {
        page = ctntCtrl.getFirstCoaContentPage(session, filter, 100);
        log.debug(LOG_CATEGORY_GENERATOR, "ActionGenerateTemplateDefinitionInstance - executeTemplates: read filtered page was successful");
      } catch (AccessNotGrantedException e) {
        log.debug(LOG_CATEGORY_GENERATOR, "ActionGenerateTemplateDefinitionInstance - executeTemplates: ERROR - read filtered page failed");
      }

      //**************************************************************************************************
      //* iterate through all content of current page
      //**************************************************************************************************
      if (page!=null) {
        @SuppressWarnings("unchecked")
        Iterator<CoaContent> iter = page.iterator();
        while (iter.hasNext()) {
          // read content
          CoaContent ctnt = iter.next();
          
          //**************************************************************************************************
          //* only consider content with .ftl extension
          //**************************************************************************************************
          if (ctnt != null && ctnt.getName().endsWith(".ftl")) {
            log.debug(LOG_CATEGORY_GENERATOR, "ActionGenerateTemplateDefinitionInstance - executeTemplates: found content '" + ctnt.getName() + "'");
            
            // consider all languages
            CoaContent newCtnt = null;
            Iterator<CoaContentData> ctntDatas = ctnt.getCoaContentDatas();
            while (ctntDatas.hasNext()) {
              CoaContentData data = ctntDatas.next();
              log.debug(LOG_CATEGORY_GENERATOR, "ActionGenerateTemplateDefinitionInstance - executeTemplates: process content data for locale '" + data.getLocale() + "'");
              String dataAsText = data.getText();
              if (dataAsText!=null) {
                
                // get generated content for template (data)
                String generatedText = executeTemplate(attr, dataAsText, td);
                log.trace(LOG_CATEGORY_GENERATOR, "ActionGenerateTemplateDefinitionInstance - executeTemplates: generated result '" + generatedText + "'");
                                
                // write generated content to target project
                String targetProject = attr.get("root.targetProject");
                String ctntPath = groups.get(i).replace("gen-templates/" + definition + "/", "").replace("CONTROLLERNAME", attr.get("root.controllerName")) + "/";
                String ctntName = ctnt.getName().replace("CONTROLLERNAME", attr.get("root.controllerName")).replace(".ftl", "");
                log.debug(LOG_CATEGORY_GENERATOR, "ActionGenerateTemplateDefinitionInstance - executeTemplates: content path '" + ctntPath + "'");
                log.debug(LOG_CATEGORY_GENERATOR, "ActionGenerateTemplateDefinitionInstance - executeTemplates: content project/name '" + targetProject + "'/'" + ctntName + "'");
                int type = CoaContent.TYPE_XML;
                if (ctntName.endsWith(".html")) {
                  type = CoaContent.TYPE_HTML;
                } else if (ctntName.endsWith(".xsl")) {
                  type = CoaContent.TYPE_XSL;
                }
                log.debug(LOG_CATEGORY_GENERATOR, "ActionGenerateTemplateDefinitionInstance - executeTemplates: content type '" + type + "'");

                // create or update content
                ContentServices cs = new ContentServices();
                if (cs.existsContent(session, targetProject, ctntPath, ctntName)) {
                  if (newCtnt == null) newCtnt = cs.getContent(session, targetProject, ctntPath, ctntName);
                  cs.updateContent(session, newCtnt, generatedText, null, ";", data.getLocale());
                } else {
                  // create content
                  int result = cs.createContent(session, targetProject, ctntPath, ctntName, data.getLocale(), type, generatedText, null, ";");
                  if (result!=0) {
                    // content has no data
                    log.debug(LOG_CATEGORY_GENERATOR, "ActionGenerateTemplateDefinitionInstance - executeTemplates: content '" + targetProject + " - " + ctntPath + "/" + ctntName + " (" + data.getLocale() + ")' could not be generated.");
                  }
                }
                
              } else {
                // content has no data
                log.debug(LOG_CATEGORY_GENERATOR, "ActionGenerateTemplateDefinitionInstance - executeTemplates: content '" + ctnt.getName() + "' has no data.");
              }
            
            } // content datas of all locales
          } // .ftl content only
        } // while
      } // page!=null
    }
        
    return true;
  }


  private String executeTemplate(Hashtable<String, String> attr, String fmTemplate, TemplateDefinition td) {
    
    // get a string template loader
    StringTemplateLoader stringLoader = new StringTemplateLoader();
    
    // get the template and put it to the stringLoader
    stringLoader.putTemplate("template", fmTemplate);

    // get a template configuration
    Configuration cfg = new Configuration();
    cfg.setTemplateLoader(stringLoader);
    
    // get template
    Template template = null;
    try {
      template = cfg.getTemplate("template");
    } catch (IOException e) {
      log.debug(LOG_CATEGORY_GENERATOR, "ActionGenerateTemplateDefinitionInstance - executeTemplate: ERROR - getTemplate failed (IOException).");
      log.trace(LOG_CATEGORY_GENERATOR, "ActionGenerateTemplateDefinitionInstance - executeTemplate: Exception = " + e.getMessage());
    }
    
    // process template
    StringWriter out = new StringWriter();
    
    // data
    Map<String,Object> data = new HashMap<String,Object>();
    data.put("controllerName", attr.get("root.controllerName"));
    data.put("attributes",td.getAttributes());    
    data.put("actions",td.getActions());    
    
    try {
      template.process(data, out);
    } catch (TemplateException e) {
      log.debug(LOG_CATEGORY_GENERATOR, "ActionGenerateTemplateDefinitionInstance - executeTemplate: ERROR - getTemplate failed (TemplateException).");
      log.trace(LOG_CATEGORY_GENERATOR, "ActionGenerateTemplateDefinitionInstance - executeTemplate: Exception = " + e.getMessage());
    } catch (IOException e) {
      log.debug(LOG_CATEGORY_GENERATOR, "ActionGenerateTemplateDefinitionInstance - executeTemplate: ERROR - getTemplate failed (IOException).");
      log.trace(LOG_CATEGORY_GENERATOR, "ActionGenerateTemplateDefinitionInstance - executeTemplate: Exception = " + e.getMessage());
    }
    
    return out.toString();
  }

  public boolean validate(CoaSession session, WebletRequest request) {
    log.debug(LOG_CATEGORY, "ActionGenerateTemplateDefinitionInstance - validate: Reached.");
    // get request parameter
    validateAllRequestParameters(session, request);
    return true;
  }

   

}
