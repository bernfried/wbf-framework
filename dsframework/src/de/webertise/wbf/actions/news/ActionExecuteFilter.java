package de.webertise.wbf.actions.news;

import de.reddot.api.common.session.CoaSession;
import de.reddot.api.web.io.WebletRequest;

/**
 * 
 * @author bernfried howe
 */
public class ActionExecuteFilter extends de.webertise.wbf.base.action.AbstractAction {

  /** Creates a new instance of ActionExecuteFilter */
  public ActionExecuteFilter() {
  }

  public boolean execute(CoaSession session, WebletRequest request) {

    log.debug(LOG_CATEGORY, "ActionExecuteFilter - execute: Reached.");

    // get current session values
    session.setAttribute("community.selCategory", request.getParameter("selCategory"));
    session.setAttribute("community.selFilterCategories", request.getParameter("selFilterCategories"));
    session.setAttribute("community.selTags", request.getParameter("selTags"));
    session.setAttribute("community.selTagsSemiSep", request.getParameter("selTagsSemiSep"));
    session.setAttribute("community.selTagsAndOrFlag", request.getParameter("selTagsAndOrFlag"));

    // return constraint
    this.setErrorCode(Integer.toString(ACTION_EXECUTE_RESULT_OK));
    this.setActionForwardName("ok");
    return true;
  }

  public boolean validate(CoaSession session, WebletRequest request) {
    log.debug(LOG_CATEGORY, "ActionExecuteFilter - validate: Reached.");
    return true;
  }
  
}
