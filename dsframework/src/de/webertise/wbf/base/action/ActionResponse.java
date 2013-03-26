/*
 * ActionResponse.java
 *
 * Created on 25. März 2003, 23:00
 */

package de.webertise.wbf.base.action;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.reddot.api.common.session.CoaSession;
import de.reddot.api.web.io.WebletRequest;

/**
 * 
 * @author bernfried howe
 */
public class ActionResponse {

  private Map<String, ActionResponseItem> response      = new HashMap<String, ActionResponseItem>();
  private String                          errorCode;
  private String                          errorAction;
  private ActionForwardDef                actionForward = null;

  /** Creates a new instance of ActionResponse */
  public ActionResponse() {
  }

  public void setResponse(HashMap<String, ActionResponseItem> response) {
    this.response = response;
  }

  public void setResponseParameter(String key, ActionResponseItem item) {
    this.response.put(key, item);
  }

  public void setResponseParameter(int type, String name, Object value, boolean cdata, boolean htmlEncoded) {
    ActionResponseItem item = new ActionResponseItem(type, name, value, cdata, htmlEncoded);
    this.response.put(name, item);
  }

  public void setErrorCode(String errorCode) {
    this.errorCode = errorCode;
  }

  public void setErrorAction(String errorAction) {
    this.errorAction = errorAction;
  }

  public ActionForwardDef getActionForward() {
    return actionForward;
  }

  public void setActionForward(ActionForwardDef actionForward) {
    this.actionForward = actionForward;
  }

  public String getErrorCode() {
    return this.errorCode;
  }

  public String getErrorAction() {
    return this.errorAction;
  }

  public Map<String, ActionResponseItem> getResponse() {
    return this.response;
  }

  public Map<String, ActionResponseItem> getResponseRoot() {
    return this.response;
  }

  public void removeResponseParameter(String key) {
    this.response.remove(key);
  }

  public void transferResponse(CoaSession session, WebletRequest request, String prefix) {
    Iterator<String> keys = this.response.keySet().iterator();
    while(keys.hasNext()) {
      String key = keys.next();
      ActionResponseItem item = this.response.get(key);
      String xml = item.getAsXml();
      if (item.getTarget() == ActionResponseItem.TARGET_REQUEST) {
        request.setParameter(prefix + key, xml);
      } else if (item.getTarget() == ActionResponseItem.TARGET_SESSION) {
        session.setAttribute(key, xml);
      } else if (item.getTarget() == ActionResponseItem.TARGET_SESSION_TRANSIENT) {
        session.setAttribute("transient." + prefix + key, xml);
      } else if (item.getTarget() == ActionResponseItem.TARGET_GLOBAL_MESSAGE) {
        session.setAttribute("transient." + key, xml);
      }
    }
    session.setAttribute(prefix + "errorCode", this.errorCode);
  }

}
