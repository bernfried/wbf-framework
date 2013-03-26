package de.webertise.wbf.actions;

import de.reddot.api.common.session.CoaSession;
import de.reddot.api.web.io.WebletRequest;
import de.webertise.wbf.base.action.ActionResponseItem;
import de.webertise.wbf.weblet.MasterWeblet;

/**
 * 
 * @author bernfried howe
 */
public class ActionInit extends de.webertise.wbf.base.action.AbstractAction {

  /** Creates a new instance of ActionInit */
  public ActionInit() {
  }

  public boolean execute(CoaSession session, WebletRequest request) {

    log.debug(LOG_CATEGORY, "ActionInit - execute: Reached.");

    // first check, if a shortcut matches
    String sourceRequestUri = request.getHttpServletRequest().getRequestURI();
    String forwardedUrl = request.getHttpServletRequest().getHeader("Forwarded-URL");
    String targetUrl = MasterWeblet.shortcutSetting.checkShortcut(sourceRequestUri, forwardedUrl);
    log.debug(LOG_CATEGORY, "ActionInit - execute: Shortcut targetUrl: '" + targetUrl + "'");
    if (targetUrl != null && !targetUrl.equals("")) {
      this.setResponseParameter(ActionResponseItem.TARGET_SESSION_TRANSIENT, ActionResponseItem.KEY_SHORTCUT_TARGET_URL, targetUrl, false, false);
      this.setActionForwardName("shortcutPage");
      return true;
    }

    // check for referer
    String referer = request.getParameter("wbf.requestctrldata.referer");
    if (referer != null && !referer.equals("")) {
      // forward to refered page
      this.setActionForwardName("refererPage");
      log.debug(LOG_CATEGORY, "ActionCreateFilter - execute: Current referer is: '" + referer + "'");
    } else {
      // get current session user to check authorization status
      String login = session.getCoaUser().getLogin();
      log.debug(LOG_CATEGORY, "ActionInit - execute: Username: '" + login + "'");

      // check, if user is already authenticated
      if (login.equals("anonymous")) {
        this.setActionForwardName("loginPage");
      } else {
        this.setActionForwardName("startPage");
      }
    }

    return true;
  }

  public boolean validate(CoaSession session, WebletRequest request) {
    log.debug(LOG_CATEGORY, "ActionInit - validate: Reached.");
    return true;
  }

}
