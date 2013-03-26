/*
 * ActionMessage.java
 *
 * Created on 24. Mai 2003, 21:04
 */

package de.webertise.wbf.base.action;

import java.util.Hashtable;

/**
 * 
 * @author bernfried howe
 */
public class ActionMessage {

  private String                    code      = "";
  private String                    actionOK  = "";
  private String                    actionYes = "";
  private String                    actionNo  = "";
  private Hashtable<String, String> request;

  /** Creates a new instance of ActionMessage */
  public ActionMessage() {
  }

  /**
   * Getter for property actionOK.
   * 
   * @return Value of property actionOK.
   */
  public java.lang.String getActionOK() {
    return actionOK;
  }

  /**
   * Setter for property actionOK.
   * 
   * @param actionOK New value of property actionOK.
   */
  public void setActionOK(java.lang.String actionOK) {
    this.actionOK = actionOK;
  }

  /**
   * Getter for property actionYes.
   * 
   * @return Value of property actionYes.
   */
  public java.lang.String getActionYes() {
    return actionYes;
  }

  /**
   * Setter for property actionYes.
   * 
   * @param actionYes New value of property actionYes.
   */
  public void setActionYes(java.lang.String actionYes) {
    this.actionYes = actionYes;
  }

  /**
   * Getter for property actionNo.
   * 
   * @return Value of property actionNo.
   */
  public java.lang.String getActionNo() {
    return actionNo;
  }

  /**
   * Setter for property actionNo.
   * 
   * @param actionNo New value of property actionNo.
   */
  public void setActionNo(java.lang.String actionNo) {
    this.actionNo = actionNo;
  }

  /**
   * Getter for property code.
   * 
   * @return Value of property code.
   */
  public java.lang.String getCode() {
    return code;
  }

  /**
   * Setter for property code.
   * 
   * @param code New value of property code.
   */
  public void setCode(java.lang.String code) {
    this.code = code;
  }

  /**
   * Getter for property request.
   * 
   * @return Value of property request.
   */
  public Hashtable<String, String> getRequest() {
    return request;
  }

  /**
   * Setter for property request.
   * 
   * @param request New value of property request.
   */
  public void setRequest(Hashtable<String, String> request) {
    this.request = request;
  }

}
