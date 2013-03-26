/*
 * MasterServletConstants.java
 *
 * Created on 26. Januar 2003, 11:54
 */

package de.webertise.wbf.base.action;

/**
 *
 * @author  bernfried.howe
 */
public interface ActionForwardConstants {
  
  // the callWeblet is called based on the view page set in the actionForward settings
  public final static int ACTION_FORWARD_TYPE_CALL_XCHG_WEBLET = 1;
  
  // the sendRedirect method is called based on the action set in the actionForward settings
  public final static int ACTION_FORWARD_TYPE_ACTION_REDIRECT = 2;
  
  // the sendRedirect method is called based on the actionForward settings
  public final static int ACTION_FORWARD_TYPE_WEBLET_REDIRECT = 3;
  
  // the callWeblet method is called based on a given referer page in url or in request parameter
  public final static int ACTION_FORWARD_TYPE_CALL_XCHG_WEBLET_TO_REFERER = 4;

  // the action is called based on the action set in the actionForward settings
  public final static int ACTION_FORWARD_TYPE_ACTION_FORWARD = 5;
  

  
}
