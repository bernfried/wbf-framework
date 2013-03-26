/*
 * ValidationRule.java
 *
 * Created on 29. Mai 2003, 11:35
 */

package de.webertise.wbf.base.action;

/**
 *
 * @author  bernfried howe
 */
public class ActionValidationRule {
    
    private String name = "";
    private String pattern = "";    
    private boolean mandatory;
    private int minLength = -1;    
    private int maxLength = -1;
    private String[] defaultValues = null;
    
    /** Creates a new instance of ValidationRule */
    public ActionValidationRule() {
    }
    
    /** Getter for property name.
     * @return Value of property name.
     */
    public java.lang.String getName() {
        return name;
    }
    
    /** Setter for property name.
     * @param name New value of property name.
     */
    public void setName(java.lang.String name) {
        this.name = name;
    }
    
    /** Getter for property pattern.
     * @return Value of property pattern.
     */
    public java.lang.String getPattern() {
        return pattern;
    }
    
    /** Setter for property pattern.
     * @param pattern New value of property pattern.
     */
    public void setPattern(java.lang.String pattern) {
        this.pattern = pattern;
    }
    
    /** Getter for property minLength.
     * @return Value of property minLength.
     */
    public int getMinLength() {
        return minLength;
    }
    
    /** Setter for property minLength.
     * @param minLength New value of property minLength.
     */
    public void setMinLength(int minLength) {
        this.minLength = minLength;
    }
    
    /** Getter for property maxLength.
     * @return Value of property maxLength.
     */
    public int getMaxLength() {
        return maxLength;
    }
    
    /** Setter for property maxLength.
     * @param maxLength New value of property maxLength.
     */
    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }
    
    /** Getter for property mandatory.
     * @return Value of property mandatory.
     */
    public boolean isMandatory() {
        return mandatory;
    }
    
    /** Setter for property mandatory.
     * @param mandatory New value of property mandatory.
     */
    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public String[] getDefaultValues() {
      return defaultValues;
    }

    public void setDefaultValues(String[] defaultValues) {
      this.defaultValues = defaultValues;
    }
    
}
