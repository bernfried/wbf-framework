package de.webertise.wbf.actions.administration.generator;

import java.util.Hashtable;
import java.util.Locale;

import de.reddot.api.common.content.CoaContent;
import de.reddot.api.common.content.CoaContentControllerFactory;
import de.reddot.api.common.content.CoaContentGroup;
import de.reddot.api.common.content.CoaContentGroupController;
import de.reddot.api.common.session.CoaSession;
import de.reddot.api.web.io.WebletRequest;
import de.webertise.ds.services.ContentServices;
import de.webertise.wbf.actions.ActionConstants;
import de.webertise.wbf.weblet.MasterWebletConstants;

/**
 * 
 * @author bernfried howe
 */
public class ActionCreateTemplateDefinitionInstance extends de.webertise.wbf.base.action.AbstractAction {

  /** Creates a new instance of ActionCreateTemplateDefinitionInstance */
  public ActionCreateTemplateDefinitionInstance() {
  }

  public boolean execute(CoaSession session, WebletRequest request) {

    log.debug(LOG_CATEGORY, "ActionCreateTemplateDefinitionInstance - execute: Reached.");

    // get parameter
    String instanceName = (String) this.getRequestParameter("instanceName");

    // get content
    ContentServices cs = new ContentServices();

    String ctntGroup = "gen-templates/" + this.getRequestParameter("definition") + "/instances/" + instanceName;
    log.debug(LOG_CATEGORY, "ActionCreateTemplateDefinitionInstance - execute: create content group with path '" + ctntGroup + "'");

    // create coaContentGroup
    CoaContentGroupController cgCtrl = CoaContentControllerFactory.getCoaContentGroupController();
    CoaContentGroup cg = cgCtrl.createCoaContentGroup(session, MasterWebletConstants.WBF_PROJECT_NAME, ctntGroup);
    cg.setDescription(instanceName);
    log.debug(LOG_CATEGORY, "ActionCreateTemplateDefinitionInstance - execute: created content group '" + cg.getName() + "' with description '" + cg.getDescription() + "'");

    // set description
    if (cg != null) {

      // create new content
      Hashtable<String, String> attributes = new Hashtable<String, String>();
      attributes.put("root", "");
      attributes.put("root.controllerName", instanceName);
      attributes.put("root.attributes", "");
      int resultOk = cs.createContent(session, MasterWebletConstants.WBF_PROJECT_NAME, ctntGroup + "/", "definition.xml", Locale.ENGLISH, CoaContent.TYPE_XML, "", attributes, "|");

      if (resultOk == 0) {

        // make new instance the current one
        request.setParameter("instance", instanceName);

        // action ok
        this.setActionForwardName("ok");
        this.setErrorCode("create_instance", Integer.toString(ACTION_EXECUTE_RESULT_OK));
        this.setErrorCode(Integer.toString(ACTION_EXECUTE_RESULT_OK));

      } else {
        // action failed - show error
        this.setActionForwardName("nok");
        this.setErrorCode("create_instance", ActionConstants.ACTION_CUSTOM_VALIDATION_ERROR_10);
        this.setErrorCode(Integer.toString(ACTION_EXECUTE_FAILED));
      }
    }

    return true;
  }

  public boolean validate(CoaSession session, WebletRequest request) {
    log.debug(LOG_CATEGORY, "ActionCreateTemplateDefinitionInstance - validate: Reached.");

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
