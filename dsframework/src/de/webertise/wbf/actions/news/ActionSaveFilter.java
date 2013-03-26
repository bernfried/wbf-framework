package de.webertise.wbf.actions.news;

import de.reddot.api.common.attribute.CoaAttribute;
import de.reddot.api.common.session.CoaSession;
import de.reddot.api.common.user.CoaUser;
import de.reddot.api.common.user.CoaUserController;
import de.reddot.api.web.io.WebletRequest;
import de.webertise.wbf.base.action.ActionResponseItem;

/**
 * 
 * @author bernfried howe
 */
public class ActionSaveFilter extends de.webertise.wbf.base.action.AbstractAction {

  /** Creates a new instance of ActionSaveFilter */
  public ActionSaveFilter() {
  }

  public boolean execute(CoaSession session, WebletRequest request) {

    log.debug(LOG_CATEGORY, "ActionSaveFilter - execute: Reached.");

    // result string buffer
    StringBuffer result = new StringBuffer();

    // get current session values
    String selCategory = (String) session.getAttribute("community.selCategory");
    String selFilterCategories = (String) session.getAttribute("community.selFilterCategories");
    String selTags = (String) session.getAttribute("community.selTags");
    String selTagsSemiSep = (String) session.getAttribute("community.selTagsSemiSep");
    String selTagsAndOrFlag = (String) session.getAttribute("community.selTagsAndOrFlag");
    String name = (String) this.getRequestParameter("name");

    // build query string which represents the current filter settings
    result.append("selCategory=" + selCategory + "&");
    result.append("selFilterCategories=" + selFilterCategories + "&");
    result.append("selTags=" + selTags + "&");
    result.append("selTagsSemiSep=" + selTagsSemiSep + "&");
    result.append("selTagsAndOrFlag=" + selTagsAndOrFlag);

    CoaUser user = session.getCoaUser();
    if (user != null) {
      CoaAttribute attr = user.getCoaAttribute("profile.community.savedSearches");
      attr.addValue(name + "|" + result.toString());
      user.setCoaAttribute(attr);
      CoaUserController.updateUser(user);
    }

    // return constraint
    this.setResponseParameter(ActionResponseItem.TARGET_GLOBAL_MESSAGE, ActionResponseItem.GLOBAL_MESSAGE_SUCCESS_KEY, "savefilter.succeeded", false, false);
    this.setErrorCode(Integer.toString(ACTION_EXECUTE_RESULT_OK));
    this.setActionForwardName("ok");
    return true;
  }

  public boolean validate(CoaSession session, WebletRequest request) {
    log.debug(LOG_CATEGORY, "ActionSaveFilter - validate: Reached.");

    // get request parameter
    boolean valid = false;
    valid = validateAllRequestParameters(session, request);

    // check if user already exists
    if (valid) {
      return true;
    } else {
      this.setErrorCode(Integer.toString(ACTION_VALIDATION_RESULT_NOK));
      this.setActionForwardName("nok");
      return false;
    }
  }

}
