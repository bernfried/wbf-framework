/*
 * Action.java
 */

package de.webertise.wbf.base.action;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.reddot.api.common.session.CoaSession;
import de.reddot.api.web.io.WebletRequest;
import de.webertise.ds.services.LogServices;

/**
 * @author bernfried howe
 * 
 *         To change the template for this generated type comment go to Window>Preferences>Java>Code Generation>Code and Comments
 */
public abstract class AbstractAction {

  // constants
  public final static int           ACTION_VALIDATION_RESULT_NOK              = -1;
  public final static int           ACTION_VALIDATION_RESULT_OK               = 0;
  public final static int           ACTION_VALIDATION_RESULT_PATTERN_MISMATCH = 1;
  public final static int           ACTION_VALIDATION_RESULT_MIN_LENGTH_ERROR = 2;
  public final static int           ACTION_VALIDATION_RESULT_MAX_LENGTH_ERROR = 3;
  public final static int           ACTION_VALIDATION_RESULT_MANDATORY_FAILED = 4;
  public final static int           ACTION_VALIDATION_RESULT_NULL             = 99;

  public final static int           ACTION_EXECUTE_RESULT_OK                  = 0;
  public final static int           ACTION_EXECUTE_FAILED                     = 21;
  public final static int           ACTION_EXECUTE_OBJECT_EXISTS              = 22;

  public static final String        ERROR_CODE_POSTFIX                        = "_errorCode";
  public static final String        ERROR_CLASS_POSTFIX                       = "_errorClass";
  public static final String        ERROR_CLASS                               = "error";

  protected final static String     LOG_CATEGORY                              = "wbf_action";
  protected LogServices             log                                       = LogServices.getInstance(LOG_CATEGORY);
  protected String                  logMsg                                    = "";

  private Hashtable<String, Object> actionRequest                             = new Hashtable<String, Object>();
  private Hashtable<String, String> actionErrors                              = new Hashtable<String, String>();
  private ActionResponse            actionResponse                            = new ActionResponse();
  private String                    actionForwardName                         = "";
  private ActionMapping             actionMapping;

  public AbstractAction() {
  }

  public abstract boolean validate(CoaSession session, WebletRequest request);

  public abstract boolean execute(CoaSession session, WebletRequest request);

  public boolean init(CoaSession session, WebletRequest request, ActionMapping actionMapping) {

    logMsg = "ActionMapping - Action: " + actionMapping.getAction();
    log.debug(LOG_CATEGORY, "AbstractAction.java - init: " + logMsg);
    this.setResponseParameter(ActionResponseItem.TARGET_SESSION, "currentAction", actionMapping.getAction(), false, false);
    @SuppressWarnings("unchecked")
    Enumeration<String> enumKeys = session.getAttributeKeysEnumeration();
    while(enumKeys.hasMoreElements()) {
      String key = enumKeys.nextElement();
      if (key.startsWith("transient"))
        session.removeAttribute(key);
    }

    // set ActionMapping
    this.actionMapping = actionMapping;

    return true;
  }

  public void finalize(CoaSession session, WebletRequest request) {

    // write all request parameters and found errors to action response
    this.actionErrors.put("action" + ERROR_CODE_POSTFIX, this.actionResponse.getErrorCode());
    this.setResponseParameter(ActionResponseItem.TARGET_SESSION_TRANSIENT, "action_request", this.actionRequest, false, false);
    this.setResponseParameter(ActionResponseItem.TARGET_SESSION_TRANSIENT, "action_errors", this.actionErrors, false, false);

  }

  protected boolean validateAllRequestParameters(CoaSession session, WebletRequest request) {

    log.debug(LOG_CATEGORY, "AbstractAction - validateAllRequestParameters: for action '" + this.actionMapping.getAction() + "'");

    // get all validation rule keys
    Enumeration<String> ruleKeys = this.actionMapping.getValidationRuleKeys();
    boolean retValue = true;
    while(ruleKeys.hasMoreElements()) {
      String key = (String) ruleKeys.nextElement();
      String value = (String) request.getParameter(key);

      // get validation rule from ActionMapping
      ActionValidationRule rule = this.actionMapping.getValidationRule(key);

      // if value is empty => set to default if available and if default values is multi-value, all values are semicolon-separated
      if (value == null || value.equals("")) {
        String[] values = rule.getDefaultValues();
        if (values != null && values.length > 0) {
          boolean isFirst = true;
          for (int i = 0; i < values.length; i++) {
            if (isFirst) {
              value = values[i];
              isFirst = false;
            } else {
              value = value + ";" + values[i];
            }
          }
        }
      }

      logMsg = "Key/Value: '" + key + "' / '" + value + "'";
      log.debug(LOG_CATEGORY, "AbstractAction - validateAllRequestParameters: " + logMsg);

      int result = AbstractAction.ACTION_VALIDATION_RESULT_OK;
      if (value != null) {
        // log key/value pairs of request parameters except password
        if (!key.equals("password")) {
          logMsg = "Value not null - Validation Rule Key: '" + key + "' and value: '" + value + "'";
          log.debug(LOG_CATEGORY, "AbstractAction - validateAllRequestParameters: " + logMsg);
        }

        // validate request parameter against rule
        result = validateRequestParameter(key, value, rule);
        if (result > AbstractAction.ACTION_VALIDATION_RESULT_OK) {
          retValue = false;
          this.setErrorCode(Integer.toString(AbstractAction.ACTION_VALIDATION_RESULT_NOK));
          this.setErrorCode(key, Integer.toString(result));
          if (result == AbstractAction.ACTION_VALIDATION_RESULT_MIN_LENGTH_ERROR) {
            this.setErrorCode(key, "_minLength", Integer.toString(rule.getMinLength()));
          }
          if (result == AbstractAction.ACTION_VALIDATION_RESULT_MAX_LENGTH_ERROR) {
            this.setErrorCode(key, "_maxLength", Integer.toString(rule.getMaxLength()));
          }
        } else {
          this.setErrorCode(key, Integer.toString(AbstractAction.ACTION_VALIDATION_RESULT_OK));
        }
      } else {
        logMsg = "Value null - Validation Rule Key: '" + key + "' and value: '" + value + "'";
        log.debug(LOG_CATEGORY, "AbstractAction - validateAllRequestParameters" + logMsg);
        this.setErrorCode(Integer.toString(AbstractAction.ACTION_VALIDATION_RESULT_NOK));
        this.setErrorCode(key, Integer.toString(AbstractAction.ACTION_VALIDATION_RESULT_NULL));
        retValue = false;
      }
    }
    return retValue;
  }

  /**
   * @param request
   * @return
   */
  protected boolean getAllRequestParameters(WebletRequest request) {
    Enumeration<String> ruleKeys = this.actionMapping.getValidationRuleKeys();
    boolean retValue = true;
    while(ruleKeys.hasMoreElements()) {
      String key = (String) ruleKeys.nextElement();
      String value = (String) request.getParameter(key);
      if (value != null) {
        this.setRequestParameter(key, value);
      } else {
        this.setRequestParameter(key, "");
        retValue = false;
      }
    }
    return retValue;
  }

  protected int validateRequestParameter(String name, String value, ActionValidationRule rule) {

    // set request and response values
    if (value == null)
      value = "";
    this.setRequestParameter(name, value);

    // get rule properties
    if (rule == null) {
      return ACTION_VALIDATION_RESULT_OK;
    }

    // check value and mandatory
    if ((value == null) || (value.equals(""))) {
      if (rule.isMandatory()) {
        String logMsg = "Mandortory rule failed for: " + name + " with value: " + value;
        log.debug(LOG_CATEGORY, "AbstractAction.java - validateRequestParameter" + logMsg);
        return ACTION_VALIDATION_RESULT_MANDATORY_FAILED;
      } else {
        log.debug(LOG_CATEGORY, "AbstractAction.java - validateRequestParameter: Field '" + name + "' is empty but not mandortory. Further validations skipped.");
        return ACTION_VALIDATION_RESULT_OK;
      }
    }

    // check min and max length
    if (rule.getMinLength() != -1) {
      if (value.length() < rule.getMinLength()) {
        String logMsg = "MinLength rule failed for: " + name + " with value: " + value + "(Min: " + rule.getMinLength() + ").";
        log.debug(LOG_CATEGORY, "AbstractAction.java - validateRequestParameter" + logMsg);
        return ACTION_VALIDATION_RESULT_MIN_LENGTH_ERROR;
      }
    }
    if (rule.getMaxLength() != -1) {
      if (value.length() > rule.getMaxLength()) {
        String logMsg = "MaxLength rule failed for: " + name + " with value: " + value + "(Max: " + rule.getMaxLength() + ").";
        log.debug(LOG_CATEGORY, "AbstractAction.java - validateRequestParameter" + logMsg);
        return ACTION_VALIDATION_RESULT_MAX_LENGTH_ERROR;
      }
    }

    // check pattern
    if ((rule.getPattern() != null) && (!rule.getPattern().equals(""))) {
      Pattern p = Pattern.compile(rule.getPattern());
      Matcher m = p.matcher(value);
      if (!m.matches()) {
        String logMsg = "Pattern rule failed for: " + name + " with value: " + value + "(Pattern: " + rule.getPattern() + ").";
        log.debug(LOG_CATEGORY, "AbstractAction.java - validateRequestParameter" + logMsg);
        return ACTION_VALIDATION_RESULT_PATTERN_MISMATCH;
      }
    }

    return ACTION_VALIDATION_RESULT_OK;
  }

  public Hashtable<String, Object> getRequest() {
    return this.actionRequest;
  }

  public Object getRequestParameter(String key) {
    return this.actionRequest.get(key);
  }

  public void setRequestParameter(String key, Object value) {
    this.actionRequest.put(key, value);
  }

  public ActionResponse getResponse() {
    return this.actionResponse;
  }

  public void setResponseParameter(int type, String name, Object value, boolean cdata, boolean htmlEncoded) {
    ActionResponseItem item = new ActionResponseItem(type, name, value, cdata, htmlEncoded);
    this.actionResponse.setResponseParameter(name, item);
  }

  public void setErrorCode(String code) {
    this.actionResponse.setErrorCode(code);
  }

  public String getErrorCode() {
    return this.actionResponse.getErrorCode();
  }

  public void setErrorAction(String action) {
    this.actionResponse.setErrorAction(action);
  }

  public String getActionForwardName() {
    return this.actionForwardName;
  }

  public void setActionForwardName(String actionForwardName) {
    this.actionForwardName = actionForwardName;
  }

  /**
   * @return the actionErrors
   */
  public Hashtable<String, String> getActionErrors() {
    return actionErrors;
  }

  /**
   * @param actionErrors the actionErrors to set
   */
  public void setActionErrors(Hashtable<String, String> actionErrors) {
    this.actionErrors = actionErrors;
  }

  public void setErrorCode(String key, String value) {
    this.actionErrors.put(key + ERROR_CODE_POSTFIX, value);
    if (value != null && !value.equals("0")) {
      this.actionErrors.put(key + ERROR_CLASS_POSTFIX, ERROR_CLASS);
    } else {
      this.actionErrors.put(key + ERROR_CLASS_POSTFIX, "");
    }
  }

  private void setErrorCode(String key, String postfix, String value) {
    this.actionErrors.put(key + postfix, value);
  }

  public void removeRequestParameter(String key) {
    this.actionRequest.remove(key);
  }

  public void removeResponseParameter(String key) {
    this.actionResponse.removeResponseParameter(key);
  }

  protected void debugRequestParameters(WebletRequest request) {
    @SuppressWarnings("unchecked")
    Enumeration<String> keys = request.getParameterNames();
    while(keys.hasMoreElements()) {
      String key = (String) keys.nextElement();
      String value = (String) request.getParameter(key);
      String logMsg = "Attribute-Key: >>>" + key + "<<< >>>" + value + "<<<";
      log.debug(LOG_CATEGORY, "AbstractAction.java - debugRequestParameter" + logMsg);
    }
  }
}
