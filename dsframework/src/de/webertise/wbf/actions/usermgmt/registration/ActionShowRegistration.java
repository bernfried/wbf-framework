/*
 * ActionShowRegistration.java
 *
 * Created on 2st of January 2013, 15:26
 */
package de.webertise.wbf.actions.usermgmt.registration;

import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

import de.reddot.api.common.session.CoaSession;
import de.reddot.api.web.io.WebletRequest;
import de.webertise.wbf.base.action.ActionResponseItem;

/**
 * 
 * @author bernfried howe
 */
public class ActionShowRegistration extends de.webertise.wbf.base.action.AbstractAction {

  /** Creates a new instance of ActionInit */
  public ActionShowRegistration() {
  }

  public boolean execute(CoaSession session, WebletRequest request) {

    log.debug(LOG_CATEGORY, "ActionShowRegistration - execute: Reached.");

    // get timezones
    List<String> tmList = Arrays.asList(TimeZone.getAvailableIDs());
    this.setResponseParameter(ActionResponseItem.TARGET_SESSION_TRANSIENT, "registration.timezones", tmList, false, false);

    // action ok
    this.setErrorCode(Integer.toString(ACTION_EXECUTE_RESULT_OK));
    this.setActionForwardName("ok");
    return true;

  }

  public boolean validate(CoaSession session, WebletRequest request) {
    log.debug(LOG_CATEGORY, "ActionShowRegistration - validate: Reached.");
    return true;
  }
}
