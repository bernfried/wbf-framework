package de.webertise.wbf.admin.generator;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import de.webertise.wbf.base.action.ActionForwardDef;

public class TemplateDefinitionAction {

  private String                        key;
  private String                        name;
  private String                        attributes;
  private String                        packageName;
  private String                        group;
  private Map<String, ActionForwardDef> forwards;
  private List<String>                  attrList;

  public TemplateDefinitionAction() {
    forwards = new Hashtable<String, ActionForwardDef>();
    attrList = new ArrayList<String>();
  }

  /**
   * @return the key
   */
  public String getKey() {
    return key;
  }

  /**
   * @param key the key to set
   */
  public void setKey(String key) {
    this.key = key;
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
   * @return the attributes
   */
  public String getAttributes() {
    return attributes;
  }

  /**
   * @param attributes the attributes to set
   */
  public void setAttributes(String attributes) {
    this.attributes = attributes;
    StringTokenizer st = new StringTokenizer(attributes, ";");
    while(st.hasMoreTokens()) {
      this.attrList.add(st.nextToken());
    }
  }

  public Map<String, ActionForwardDef> getForwards() {
    return forwards;
  }

  public void setForwards(Map<String, ActionForwardDef> forwards) {
    this.forwards = forwards;
  }

  public void addForword(String name, ActionForwardDef afDef) {
    this.forwards.put(name, afDef);
  }

  public String getPackageName() {
    return packageName;
  }

  public void setPackageName(String packageName) {
    this.packageName = packageName;
  }

  public String getNameWithUpperCaseLetter() {
    if (this.getName() != null && this.getName().length() > 1) {
      return this.getName().substring(0, 1).toUpperCase() + this.getName().substring(1);
    } else {
      return this.getName();
    }
  }

  public boolean hasAttribute(String name) {
    if (this.attrList != null && this.attrList.contains(name)) {
      return true;
    } else {
      return false;
    }
  }

  public String getGroup() {
    return group;
  }

  public void setGroup(String group) {
    this.group = group;
  }

}
