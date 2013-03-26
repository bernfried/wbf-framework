/*
 * ActionMapping.java
 *
 * Created on 15. März 2003, 10:00
 */

package de.webertise.wbf.base.action;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * 
 * @author bernfried howe
 */
public class ActionMapping {

  private String                                  action          = "";
  private String                                  actionClass     = "";
  private String[]                                groups          = null;
  private Hashtable<String, ActionForwardDef>     actionForwards  = new Hashtable<String, ActionForwardDef>();
  private Hashtable<String, ActionValidationRule> validationRules = new Hashtable<String, ActionValidationRule>();

  /** Creates a new instance of ActionMapping */
  public ActionMapping() {
  }

  /**************************************************************************
   * Getter
   *************************************************************************/

  public String getAction() {
    return this.action;
  }

  public String getActionClass() {
    return this.actionClass;
  }

  public String[] getGroups() {
    return this.groups;
  }

  public ActionForwardDef getActionForward(String name) {
    return (ActionForwardDef) this.actionForwards.get(name);
  }

  public ActionValidationRule getValidationRule(String name) {
    return (ActionValidationRule) this.validationRules.get(name);
  }

  public Enumeration<String> getValidationRuleKeys() {
    return this.validationRules.keys();
  }

  /**************************************************************************
   * Setter
   *************************************************************************/

  public void setAction(String action) {
    this.action = action;
  }

  public void setActionClass(String actionClass) {
    this.actionClass = actionClass;
  }

  public void setGroups(String[] groups) {
    this.groups = groups;
  }

  public void setActionForward(ActionForwardDef actionForward) {
    this.actionForwards.put(actionForward.getName(), actionForward);
  }

  public void setValidationRule(ActionValidationRule validationRule) {
    this.validationRules.put(validationRule.getName(), validationRule);
  }

  /**
   * @return
   */
  public Enumeration<String> getActionForwardKeys() {
    return this.actionForwards.keys();
  }

  public boolean checkUserGroups(String groups) {
    // find the first corresponding group
    if (this.groups != null) {
      for (int i = 0; i < this.groups.length; i++) {
        if (groups.contains("[" + this.groups[i] + "]")) {
          return true;
        }
      }
      return false;
    } else {
      return true;
    }
  }

  public String getGroupsAsString() {
    String result = "";
    if (this.groups != null) {
      for (int i = 0; i < this.groups.length; i++) {
        result = result + "[" + this.groups[i] + "]";
      }
    }
    return result;
  }

}
