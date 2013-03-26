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
public class ActionCreateTemplateDefinitionAttribute extends de.webertise.wbf.base.action.AbstractAction {

  /** Creates a new instance of ActionInit */
  public ActionCreateTemplateDefinitionAttribute() {
  }

  public boolean execute(CoaSession session, WebletRequest request) {

    log.debug(LOG_CATEGORY, "ActionCreateTemplateDefinitionAttribute - execute: Reached.");

    // get parameter
    String attrName = (String) this.getRequestParameter("attributeName");
    
    // get content
    ContentServices cs = new ContentServices();
    
    String ctntGroup = "gen-templates/" + this.getRequestParameter("definition") + "/instances/" + this.getRequestParameter("instance") + "/";
    log.debug(LOG_CATEGORY, "ActionCreateTemplateDefinitionAttribute - execute: read content from content group '" + ctntGroup + "'" );
    
    CoaContent ctnt = cs.getContent(session, MasterWebletConstants.WBF_PROJECT_NAME, ctntGroup, "definition.xml");
    if (ctnt!=null) {
      // update content
      String attrPath = "root.attributes." + attrName;
      ctnt.setAttribute(attrPath, Locale.ENGLISH, attrName);
      ctnt.setAttribute(attrPath + ".name", Locale.ENGLISH, attrName);
      ctnt.setAttribute(attrPath + ".minLength", Locale.ENGLISH, "");
      ctnt.setAttribute(attrPath + ".maxLength", Locale.ENGLISH, "");
      ctnt.setAttribute(attrPath + ".mandatory", Locale.ENGLISH, "");
      ctnt.setAttribute(attrPath + ".pattern", Locale.ENGLISH, "");
      ctnt.setAttribute(attrPath + ".type", Locale.ENGLISH, "");
      cs.updateContent(session, ctnt);
      request.setParameter("attribute", attrPath);
      // action ok
      this.setActionForwardName("ok");
      this.setErrorCode(Integer.toString(ACTION_EXECUTE_RESULT_OK));
      this.setErrorCode("create_attribute", Integer.toString(ACTION_EXECUTE_RESULT_OK));
      
    } else {
      // acion failed - show error
      super.setActionForwardName("nok");
      this.setErrorCode(Integer.toString(ACTION_EXECUTE_FAILED));
      this.setErrorCode("create_attribute", ActionConstants.ACTION_CUSTOM_VALIDATION_ERROR_10);
    }
    return true;
  }

  public boolean validate(CoaSession session, WebletRequest request) {
    log.debug(LOG_CATEGORY, "ActionCreateTemplateDefinitionAttribute - validate: Reached.");
    
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
