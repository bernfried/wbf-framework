package de.webertise.wbf.admin.generator;

public class TemplateDefinitionAttribute {

  // constants
  public final static int TYPE_TEXTFIELD = 1;
  public final static int TYPE_TEXTAREA = 2;
  public final static int TYPE_PASSWORD = 3;
  public final static int TYPE_SELECT = 4;
  public final static int TYPE_CHECKBOX = 5;
  public final static int TYPE_RADIOBUTTONS = 6;
  
  // properties
  private String name;
  private int type;
  private String pattern;
  private int minLength;
  private int maxLength;
  private boolean mandatory = false;
  private boolean key = false;
  private boolean showInList = false;

  // constructor
  public TemplateDefinitionAttribute() {
    
  }
  
  //*******************************************************
  //* getter/setter
  //*******************************************************

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
   * @return the mandatory
   */
  public boolean isMandatory() {
    return mandatory;
  }
  /**
   * @param mandatory the mandatory to set
   */
  public void setMandatory(boolean mandatory) {
    this.mandatory = mandatory;
  }
  /**
   * @return the minLength
   */
  public int getMinLength() {
    return minLength;
  }
  /**
   * @param minLength the minLength to set
   */
  public void setMinLength(int minLength) {
    this.minLength = minLength;
  }
  /**
   * @return the maxLength
   */
  public int getMaxLength() {
    return maxLength;
  }
  /**
   * @param maxLength the maxLength to set
   */
  public void setMaxLength(int maxLength) {
    this.maxLength = maxLength;
  }
  /**
   * @return the pattern
   */
  public String getPattern() {
    return pattern;
  }
  /**
   * @param pattern the pattern to set
   */
  public void setPattern(String pattern) {
    this.pattern = pattern;
  }
  /**
   * @return the type
   */
  public int getType() {
    return type;
  }
  /**
   * @param type the type to set
   */
  public void setType(int type) {
    this.type = type;
  }

  public String getMandatory() {
    if (isMandatory()) {
      return "1";
    } else {
      return "0";
    }
  }

  public boolean isKey() {
    return key;
  }

  public void setKey(boolean key) {
    this.key = key;
  }

  public String getKey() {
    if (isKey()) {
      return "1";
    } else {
      return "0";
    }
  }

  public boolean isShowInList() {
    return showInList;
  }

  public void setShowInList(boolean showInList) {
    this.showInList = showInList;
  }

  public String getShowInList() {
    if (isShowInList()) {
      return "1";
    } else {
      return "0";
    }
  }

}
