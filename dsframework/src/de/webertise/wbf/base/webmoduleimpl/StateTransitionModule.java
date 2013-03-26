/*
 * ActionModule.java
 *
 * Created on 31. Januar 2003, 18:38
 */

package de.webertise.wbf.base.webmoduleimpl;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;

import de.reddot.api.common.session.CoaSession;
import de.reddot.api.common.user.CoaUser;
import de.reddot.api.common.user.CoaUserGroup;
import de.reddot.api.web.io.WebletRequest;
import de.reddot.api.web.io.WebletResponse;
import de.webertise.ds.services.LogServices;
import de.webertise.wbf.base.action.AbstractAction;
import de.webertise.wbf.base.action.ActionForwardDef;
import de.webertise.wbf.base.action.ActionMapping;
import de.webertise.wbf.base.action.ActionResponse;
import de.webertise.wbf.base.action.ActionResponseItem;
import de.webertise.wbf.base.application.Application;
import de.webertise.wbf.base.application.ApplicationSetting;
import de.webertise.wbf.base.webmodule.WebModuleSetting;
import de.webertise.wbf.weblet.RequestControllerData;

/**
 * 
 * @author bernfried.howe
 */
public class StateTransitionModule extends de.webertise.wbf.base.module.AbstractModule implements de.webertise.wbf.base.webmoduleimpl.StateTransitionModuleConstants {

  // constants
  private final static String                LOG_CATEGORY        = "wbf_webmodule";
  private LogServices                        log                 = LogServices.getInstance(LOG_CATEGORY);

  // variables
  private Map<String, Class<AbstractAction>> loadedActionClasses = new Hashtable<String, Class<AbstractAction>>();

  /** Creates a new instance of StateTransitionModule */
  public StateTransitionModule() {
  }

  @SuppressWarnings("unchecked")
  public ActionResponse handleRequest(WebletRequest request, WebletResponse response, CoaSession session, WebModuleSetting webModuleSetting, ApplicationSetting applicationSetting,
      RequestControllerData reqCtrlData) {

    // ************************************************************************
    // * get general application settings
    // ************************************************************************
    String initAction = applicationSetting.getInitAction();
    String loginAction = applicationSetting.getLoginAction();
    String actionToExecute = reqCtrlData.getActionName();

    // ************************************************************************
    // * get application settings
    // ************************************************************************
    Application application = applicationSetting.getApplication(reqCtrlData.getApplicationName());

    // ************************************************************************
    // * The block within the do while loop will be repeated as long as an
    // * action forward type 2 (redirect to action) is returned.
    // ************************************************************************
    ActionResponse actionResponse = null;
    ActionResponse actionResponseLast = null;
    ActionForwardDef actionForward = null;
    int redirectCounter = 0;
    boolean leave = false;

    do {
      // ************************************************************************
      // * get action mapping
      // ************************************************************************
      ActionMapping actionMapping = application.getActionMapping(actionToExecute);

      // *************************************************************************
      // * check authorization of session based on user roles / groups
      // *************************************************************************
      // check application role and action user group (except for init / login action)
      if (!actionToExecute.equals(initAction) && !actionToExecute.equals(loginAction)) {
        if (session.getAttribute("wbf.currentuser.usergroups") == null) {
          CoaUser user = session.getCoaUser();
          String groups = "";
          String roles = "";
          Enumeration<CoaUserGroup> groupEnum = user.getGroupsEnumeration();
          while(groupEnum.hasMoreElements()) {
            CoaUserGroup group = (CoaUserGroup) groupEnum.nextElement();
            groups = groups + "[" + group.getPath() + "]";
          }
          Enumeration<String> roleEnum = user.getRolesEnumeration();
          while(roleEnum.hasMoreElements()) {
            String role = roleEnum.nextElement();
            roles = roles + "[" + role + "]";
          }
          session.setAttribute("wbf.currentuser.roles", roles);
          session.setAttribute("wbf.currentuser.groups", groups);
        }
        // check application role against roles of current user
        if (!application.checkUserRoles((String) session.getAttribute("wbf.currentuser.roles"))) {
          log.debug(LOG_CATEGORY, "StateTransitionModule - handleRequest: user '" + session.getLogin() + "' needs one of the following roles: '" + application.getRolesAsString() + "'.");
          actionMapping = application.getActionMapping(initAction);
        }
        // check action user group against groups of current user
        if (!actionMapping.checkUserGroups((String) session.getAttribute("wbf.currentuser.groups"))) {
          log.debug(LOG_CATEGORY, "StateTransitionModule - handleRequest: user '" + session.getLogin() + "' needs one of the following groups: '" + actionMapping.getGroupsAsString() + "' for action '"
              + actionToExecute + "'.");
          actionMapping = application.getActionMapping(initAction);
        }
      }

      // ************************************************************************
      // * Get Action Class and Instanciate
      // ************************************************************************
      @SuppressWarnings("rawtypes")
      Class abstractActionClass = null;
      String actionClassPath = "";
      try {
        // get action class
        actionClassPath = application.getActionPath() + "." + actionMapping.getActionClass();
        log.debug(LOG_CATEGORY, "StateTransitionModule - handleRequest: Action Classpath: '" + actionClassPath + "'.");
        abstractActionClass = loadedActionClasses.get(actionClassPath);
        if (abstractActionClass == null) {
          abstractActionClass = (Class<AbstractAction>) Class.forName(actionClassPath);
          loadedActionClasses.put(actionClassPath, abstractActionClass);
        }
      } catch (ClassNotFoundException e) {
        log.debug(LOG_CATEGORY, "StateTransitionModule - handleRequest: ClassNotFoundException - Action Classpath '" + actionClassPath + "' could not be found.");
        return null;
      }
      AbstractAction abstractActionObject = null;
      try {
        abstractActionObject = (AbstractAction) abstractActionClass.newInstance();
      } catch (InstantiationException e) {
        log.debug(LOG_CATEGORY, "StateTransitionModule - handleRequest: InstantiationException - Action '" + actionClassPath + "' could not be instanciated.");
        return null;
      } catch (IllegalAccessException e) {
        log.debug(LOG_CATEGORY, "StateTransitionModule - handleRequest: IllegalAccessException - Action '" + actionClassPath + "' could not be instanciated.");
        return null;
      }

      // init action error code
      abstractActionObject.setErrorCode(Integer.toString(AbstractAction.ACTION_EXECUTE_RESULT_OK)); // initialize errorCode for all actions

      // write request controller data to response for view
      reqCtrlData.writeToActionResponse(abstractActionObject);

      // ************************************************************************
      // * Init Action
      // ************************************************************************
      boolean result = abstractActionObject.init(session, request, actionMapping);
      if (!result) {
        log.debug(LOG_CATEGORY, "StateTransitionModule - handleRequest: Init of Action '" + actionToExecute + "' failed.");
        return null;
      }

      // ************************************************************************
      // * Validate Action
      // ************************************************************************
      result = abstractActionObject.validate(session, request);

      // ************************************************************************
      // * Execute Action
      // ************************************************************************
      if (result) {
        abstractActionObject.execute(session, request);
      } else {
        log.debug(LOG_CATEGORY, "StateTransitionModule - handleRequest: Validation of Action '" + actionToExecute + "' failed.");
      }

      // ************************************************************************
      // * Init Action
      // ************************************************************************
      abstractActionObject.finalize(session, request);

      // ************************************************************************
      // * Get ActionForward
      // ************************************************************************
      String actionForwardName = abstractActionObject.getActionForwardName();
      actionForward = actionMapping.getActionForward(actionForwardName);
      if (actionForward == null) {
        log.debug(LOG_CATEGORY, "StateTransitionModule - handleRequest: No action forward found in actionMapping for forward name'" + actionForwardName + "'.");
        return null;
      } else {
        if (actionForward.getType() == 1) {
          log.debug(LOG_CATEGORY,
              "StateTransitionModule - handleRequest: ActionForward '" + actionForward.getName() + "' with view '" + actionForward.getView() + "' and type '" + actionForward.getType()
                  + "' and template '" + actionForward.getTemplate() + "' is called.");
        } else {
          log.debug(LOG_CATEGORY,
              "StateTransitionModule - handleRequest: ActionForward '" + actionForward.getName() + "' with action '" + actionForward.getAction() + "' and type '" + actionForward.getType()
                  + "' is called.");
        }
      }

      // ************************************************************************
      // * get final action response and fill it with last response if necessary
      // ************************************************************************
      actionResponse = abstractActionObject.getResponse();
      actionResponse.setActionForward(actionForward);

      // ************************************************************************
      // * if action was formerly set to default action, an global message is created.
      // * (only if no shortcut matched)
      // ************************************************************************
      if (actionResponse.getResponse().get(ActionResponseItem.KEY_SHORTCUT_TARGET_URL) == null && 
          reqCtrlData.getActionNameFound() == RequestControllerData.PARAMETER_VALUE_WRONG_AND_SET_TO_DEFAULT && 
          (reqCtrlData.getRefererPageName() == null || reqCtrlData.getRefererPageName().equals(""))) {
        abstractActionObject.setResponseParameter(ActionResponseItem.TARGET_GLOBAL_MESSAGE, ActionResponseItem.GLOBAL_MESSAGE_SUCCESS_KEY, "action.set_to_default", false, false);
      }

      // ***********************************************************************************************************
      // * Check type of ActionForward, if redirect to another action is requested and this is the first redirect
      // * Only one redirect to another action is allowed otherwise an error is raised
      // ***********************************************************************************************************
      if (actionForward.getType() == ActionForwardDef.ACTION_FORWARD_TYPE_ACTION_FORWARD && redirectCounter == 0) {

        // set action for do while loop and save existing response parameters
        actionToExecute = actionForward.getAction();

        // save former action Response
        actionResponseLast = abstractActionObject.getResponse();

        // change requestControllerData action in order to write the response parameters with correct prefix.
        reqCtrlData.setActionName(actionToExecute);

        // log this
        log.debug(LOG_CATEGORY, "StateTransitionModule - handleRequest: Redirect to Action '" + actionForward.getAction() + "'.");

        // increase redirect counter
        redirectCounter++;

      } else if (actionForward.getType() == ActionForwardDef.ACTION_FORWARD_TYPE_ACTION_FORWARD && redirectCounter == 1) {
        leave = true;
      }

    } while((actionForward.getType() == ActionForwardDef.ACTION_FORWARD_TYPE_ACTION_FORWARD) || (leave == true));
    // end of do while

    // a second action redirect was requested, which is not allowed. Therefore the stm is left with false as return code.
    if (leave == true) {
      log.debug(LOG_CATEGORY, "StateTransitionModule - handleRequest: Second action redirect to Action '" + actionForward.getAction() + "' was requested, which is not allowed.");
      return null;
    }

    // transfer actionResponse of previous action if an action forward was done
    if (actionResponseLast != null) {
      String prefix = reqCtrlData.getWebModuleName() + "." + reqCtrlData.getApplicationName() + "." + reqCtrlData.getActionName() + ".last.";
      actionResponseLast.transferResponse(session, request, prefix);
    }

    return actionResponse;
  }

}
