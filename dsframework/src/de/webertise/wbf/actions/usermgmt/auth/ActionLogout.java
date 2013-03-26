package de.webertise.wbf.actions.usermgmt.auth;

import de.reddot.api.common.session.CoaSession;
import de.reddot.api.web.io.WebletRequest;
import de.webertise.wbf.base.action.AbstractAction;

/**
 * @author bernfried howe
 * 
 */
public class ActionLogout extends AbstractAction {

  public boolean validate(CoaSession session, WebletRequest request) {
    log.debug(LOG_CATEGORY, "ActionLogout.java - validate: Reached.");
    return true;
  }

  public boolean execute(CoaSession session, WebletRequest request) {

    log.debug(LOG_CATEGORY, "ActionLogout.java - execute: Reached.");

    super.setActionForwardName("ok");
    session.invalidate();
    return true;
  }

}
