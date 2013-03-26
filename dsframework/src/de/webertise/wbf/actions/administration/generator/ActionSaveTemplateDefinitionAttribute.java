package de.webertise.wbf.actions.administration.generator;

import java.util.Locale;

import de.reddot.api.common.content.CoaContent;
import de.reddot.api.common.session.CoaSession;
import de.reddot.api.web.io.WebletRequest;
import de.webertise.ds.services.ContentServices;
import de.webertise.wbf.actions.ActionConstants;
import de.webertise.wbf.weblet.MasterWebletConstants;

/**
 * 
 * @author bernfried howe
 */
public class ActionSaveTemplateDefinitionAttribute extends de.webertise.wbf.base.action.AbstractAction {

  /** Creates a new instance of ActionSaveTemplateDefinitionAttribute */
  public ActionSaveTemplateDefinitionAttribute() {
  }

  public boolean execute(CoaSession session, WebletRequest request) {

    log.debug(LOG_CATEGORY, "ActionSaveTemplateDefinitionAttribute - execute: Reached.");
    
    // get content
    ContentServices cs = new ContentServices();
    
    String ctntGroup = "gen-templates/" + this.getRequestParameter("definition") + "/instances/" + this.getRequestParameter("instance") + "/";
    log.debug(LOG_CATEGORY, "ActionSaveTemplateDefinitionGeneral - execute: read content from content group '" + ctntGroup + "'" );
    
    CoaContent ctnt = cs.getContent(session, MasterWebletConstants.WBF_PROJECT_NAME, ctntGroup, "definition.xml");
    if (ctnt!=null) {
      // update content
      String attrPath = this.getRequestParameter("attribute") + ".";
      ctnt.setAttribute(attrPath + "name", Locale.ENGLISH, (String) this.getRequestParameter("name"));
      ctnt.setAttribute(attrPath + "minLength", Locale.ENGLISH, (String) this.getRequestParameter("minLength"));
      ctnt.setAttribute(attrPath + "maxLength", Locale.ENGLISH, (String) this.getRequestParameter("maxLength"));
      ctnt.setAttribute(attrPath + "mandatory", Locale.ENGLISH, (String) this.getRequestParameter("mandatory"));
      ctnt.setAttribute(attrPath + "pattern", Locale.ENGLISH, (String) this.getRequestParameter("pattern"));
      ctnt.setAttribute(attrPath + "type", Locale.ENGLISH, (String) this.getRequestParameter("type"));
      ctnt.setAttribute(attrPath + "key", Locale.ENGLISH, (String) this.getRequestParameter("key"));
      ctnt.setAttribute(attrPath + "showInList", Locale.ENGLISH, (String) this.getRequestParameter("showInList"));

      cs.updateContent(session, ctnt);
      // action ok
      this.setActionForwardName("ok");
      this.setErrorCode(Integer.toString(ACTION_EXECUTE_RESULT_OK));
      this.setErrorCode("save_attribute", Integer.toString(ACTION_EXECUTE_RESULT_OK));
      
    } else {
      // acion failed - show error
      super.setActionForwardName("nok");
      this.setErrorCode(Integer.toString(ACTION_EXECUTE_FAILED));
      this.setErrorCode("save_attribute", ActionConstants.ACTION_CUSTOM_VALIDATION_ERROR_10);
    }
    
    return true;
  }

  public boolean validate(CoaSession session, WebletRequest request) {
    log.debug(LOG_CATEGORY, "ActionSaveTemplateDefinitionAttribute - validate: Reached.");
    
    // get request parameter
    boolean valid = false;
    valid = validateAllRequestParameters(session, request);
    if (!valid) {
      super.setActionForwardName("nok");
      this.setErrorCode(Integer.toString(ACTION_VALIDATION_RESULT_NOK));
    }
    return valid;
  }

}
