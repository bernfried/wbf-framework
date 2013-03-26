/*
 * ActionInit.java
 *
 * Created on 15. März 2003, 15:26
 */

package de.webertise.wbf.actions.usermgmt.profile;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import de.reddot.api.common.attribute.CoaAttribute;
import de.reddot.api.common.session.CoaSession;
import de.reddot.api.common.user.CoaUser;
import de.reddot.api.common.user.CoaUserGroup;
import de.reddot.api.web.io.WebletRequest;
import de.webertise.wbf.base.action.ActionResponseItem;
import edu.emory.mathcs.backport.java.util.Arrays;

/**
 * 
 * @author bernfried howe
 */
public class ActionShowProfile extends de.webertise.wbf.base.action.AbstractAction {

  /** Creates a new instance of ActionInit */
  public ActionShowProfile() {
  }

  public boolean execute(CoaSession session, WebletRequest request) {

    log.debug(LOG_CATEGORY, "ActionShowProfile - execute: Reached.");

    // get user groups for authorization of dialog
    CoaUser user = session.getCoaUser();
    
    // get user groups as List
    @SuppressWarnings("unchecked")
    Enumeration<CoaUserGroup> userEnum = user.getGroupsEnumeration();
    List<String> groups = new ArrayList<String>();
    while(userEnum.hasMoreElements()) {
      CoaUserGroup group = (CoaUserGroup) userEnum.nextElement();
      groups.add(group.getPath());
    }
    this.setResponseParameter(ActionResponseItem.TARGET_SESSION_TRANSIENT, "myprofile.usergroups", groups, false, false);

    // get user attributes as Map
    @SuppressWarnings("unchecked")
    Enumeration<CoaAttribute> attrEnum = user.getCoaAttributeEnumeration(true);
    Map<String,String> userAttr = new Hashtable<String,String>();
    while(attrEnum.hasMoreElements()) {
      CoaAttribute attr = (CoaAttribute) attrEnum.nextElement();
      if (attr.getPath().startsWith("profile.")) {
        log.debug(LOG_CATEGORY, "ActionShowProfile - execute: attr path: '" + attr.getPath() + "' added to list.");
        String path = attr.getPath();
        int lastIndex = path.lastIndexOf(".");
        String key = path.substring(lastIndex+1);
        String value = attr.getValue();
        if (value==null) value="";
        userAttr.put(key, value);
      }
    }
    this.setResponseParameter(ActionResponseItem.TARGET_SESSION_TRANSIENT, "action_request_initial", userAttr, false, false);

    // get timezones
    @SuppressWarnings("unchecked")
    List<String> tmList = Arrays.asList(TimeZone.getAvailableIDs());
    this.setResponseParameter(ActionResponseItem.TARGET_SESSION_TRANSIENT, "myprofile.timezones", tmList, false, false);
    
    // action ok
    this.setErrorCode(Integer.toString(ACTION_EXECUTE_RESULT_OK));
    this.setActionForwardName("ok");
    return true;

  }

  public boolean validate(CoaSession session, WebletRequest request) {
    log.debug(LOG_CATEGORY, "ActionShowProfile - validate: Reached.");
    return true;
  }
}
