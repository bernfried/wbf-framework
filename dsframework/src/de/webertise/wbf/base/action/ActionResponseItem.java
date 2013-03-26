package de.webertise.wbf.base.action;

import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.reddot.api.common.attribute.CoaAttribute;
import de.webertise.ds.services.LogServices;

public class ActionResponseItem {

  // constants
  public static final int TARGET_SESSION = 1;
  public static final int TARGET_REQUEST = 2;
  public static final int TARGET_SESSION_TRANSIENT = 3;
  public static final int TARGET_GLOBAL_MESSAGE = 4;
  
  // standard response item keys
  public static final String KEY_SHORTCUT_TARGET_URL = "shortcutTargetUrl";

  public static final String GLOBAL_MESSAGE_ERROR_KEY = "global.message.error";
  public static final String GLOBAL_MESSAGE_SUCCESS_KEY = "global.message.success";
  public static final String GLOBAL_MESSAGE_WARNING_KEY = "global.message.warning";
  
  public static final String LOG_CATEGORY = "wbf_response";

  // variables
  private LogServices         log                = LogServices.getInstance(LOG_CATEGORY);

  // properties
  private int             target;
  private String          name;
  private Object          value;
  private boolean         cdata          = false;
  private boolean         htmlEncoded    = false;

  public ActionResponseItem(int target, String name, Object value, boolean cdata, boolean htmlEncoded) {
    this.target = target;
    this.name = name;
    this.value = value;
    this.cdata = cdata;
    this.htmlEncoded = htmlEncoded;
  }

  /**
   * @return the target
   */
  public int getTarget() {
    return target;
  }

  /**
   * @param target the target to set
   */
  public void setTarget(int target) {
    this.target = target;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return the value
   */
  public Object getValue() {
    return value;
  }

  /**
   * @param value the value to set
   */
  public void setValue(Object value) {
    this.value = value;
  }

  public boolean isCdata() {
    return cdata;
  }

  public void setCdata(boolean cdata) {
    this.cdata = cdata;
  }

  public boolean isHtmlEncoded() {
    return htmlEncoded;
  }

  public void setHtmlEncoded(boolean htmlEncoded) {
    this.htmlEncoded = htmlEncoded;
  }

  @SuppressWarnings({ "unchecked" })
  public String getAsXml() {

    // buffer for xml string
    StringBuffer sb = new StringBuffer();

    log.debug(LOG_CATEGORY, "ActionResponseItem - getAsXml: execute value with name '" + this.name + "'");
    
    // check object type "Map"
    if (this.value instanceof CoaAttribute) {
      
      log.debug(LOG_CATEGORY, "ActionResponseItem - getAsXml: value is CoaAttribute");
      // get name and values from attribute and create an xml
      getXmlForCoaAttribute((CoaAttribute) this.value, sb);
      
    } else if (this.value instanceof Map) {
      
      log.debug(LOG_CATEGORY, "ActionResponseItem - getAsXml: value is Map");

      // ******************************************************************************
      // * build xml for Map values (recursive if ActionResponseItem objects are found)
      // ******************************************************************************
      Iterator<String> keys = ((Map<String, Object>) this.value).keySet().iterator();
      sb.append("<" + this.name + ">");
      while(keys.hasNext()) {
        String key = keys.next();
        Object value = ((Map<String, Object>) this.value).get(key);
 
        // check recursive if type is ActionResponseItem
        if (value instanceof String) {
          
          sb.append("<" + key + ">");
          sb.append(prepareValue(value));
          sb.append("</" + key + ">");
          
        } else if (value instanceof CoaAttribute) { 
          
          getXmlForCoaAttribute((CoaAttribute) value, sb);
          
        } else if (value instanceof ActionResponseItem) {
          
          sb.append("<" + key + ">");
          sb.append(((ActionResponseItem) value).getAsXml());
          sb.append("</" + key + ">");
          
        } else if (value instanceof List) {

          log.debug(LOG_CATEGORY, "ActionResponseItem - getAsXml: value is List");
          getXmlForList((List<Object>) value, key, sb);
          
        }
      }
      sb.append("</" + this.name + ">");

    } else if (this.value instanceof List) {

      log.debug(LOG_CATEGORY, "ActionResponseItem - getAsXml: value is List");
      List<Object> list = (List<Object>) this.value;
      getXmlForList(list, this.name, sb);

    } else if (this.value instanceof Enumeration) {

      log.debug(LOG_CATEGORY, "ActionResponseItem - getAsXml: value is Enumeration");
      Enumeration<String> items = (Enumeration<String>) this.value;  
      getXmlForEnumeration(items, this.name, sb);

    } else if (this.value instanceof String) {
      // just write string to request parameter
      // always prefix with web module and application name
      sb.append(prepareValue(this.value));
    }

    // return xml string
    return sb.toString();
  }

  private void getXmlForEnumeration(Enumeration<String> items, String elementName, StringBuffer sb) {
    // ******************************************************************************
    // * build xml for List values
    // ******************************************************************************
    sb.append("<" + elementName + ">");
    while (items.hasMoreElements()) {
      String item = items.nextElement();
      sb.append("<value>" + prepareValue(item) + "</value>");
    }
    sb.append("</" + elementName + ">");
  }

  private void getXmlForList(List<Object> list, String elementName, StringBuffer sb) {
    // ******************************************************************************
    // * build xml for List values
    // ******************************************************************************
    sb.append("<" + elementName + ">");
    for (int i = 0; i < list.size(); i++) {
      Object item = list.get(i);
      
      if (item instanceof String) {
        sb.append("<value>" + prepareValue(item) + "</value>");
        
      } else if (item instanceof CoaAttribute) {
        getXmlForCoaAttribute((CoaAttribute) item, sb);

      } else if (item instanceof ActionResponseItem) {
        sb.append("<listitem>");
        sb.append(((ActionResponseItem) item).getAsXml());
        sb.append("</listitem>");
      }
    }
    sb.append("</" + elementName + ">");
  }

  private void getXmlForCoaAttribute(CoaAttribute value, StringBuffer sb) {
    CoaAttribute attr = (CoaAttribute) value;
    String[] strValues = attr.getValues();
    sb.append("<attribute path='" + attr.getPath() + "'>");
    if (strValues != null) {
      for (int i = 0; i < strValues.length; i++) {
        String strValue = strValues[i];
        if (strValue == null)
          strValue = "";
        sb.append("<value index='" + i + "'>" + strValue + "</value>");
      }
    }
    sb.append("</attribute>");
  }

  @SuppressWarnings("deprecation")
  private String prepareValue(Object value) {

    String preparedValue = (String) value;
    if (this.htmlEncoded) {
      value = URLEncoder.encode((String) value);
    }
    if (this.cdata) {
      value = "<![CDATA[" + value + "]]>";
    }
    return preparedValue;
  }

}
