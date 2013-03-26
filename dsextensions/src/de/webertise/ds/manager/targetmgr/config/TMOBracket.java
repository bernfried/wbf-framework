package de.webertise.ds.manager.targetmgr.config;

import java.util.StringTokenizer;

public class TMOBracket {
	
	private String id;
	private String parent;
	private String key;
	private String attributeName;
	private String inputParameterName;
	private String type;
	private String compareOperator;
	private String valueSeparator;
	private String logicalOperatorBracket;
	private String logicalOperatorValue;
	private String variableType;
	private boolean validSubQuery;
	private boolean bracketOnly;

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(String parent) {
		this.parent = parent;
	}

	/**
	 * @return the parent
	 */
	public String getParent() {
		return parent;
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param attributeName the attributeName to set
	 */
	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	/**
	 * @return the attributeName
	 */
	public String getAttributeName() {
		return attributeName;
	}

	/**
	 * @param inputParameterName the inputParameterName to set
	 */
	public void setInputParameterName(String inputParameterName) {
		this.inputParameterName = inputParameterName;
	}

	/**
	 * @return the inputParameterName
	 */
	public String getInputParameterName() {
		return inputParameterName;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param compareOperator the compareOperator to set
	 */
	public void setCompareOperator(String compareOperator) {
		this.compareOperator = compareOperator;
	}

	/**
	 * @return the compareOperator
	 */
	public String getCompareOperator() {
		return compareOperator;
	}

	/**
	 * @param valueSeparator the valueSeparator to set
	 */
	public void setValueSeparator(String valueSeparator) {
		this.valueSeparator = valueSeparator;
	}

	/**
	 * @return the valueSeparator
	 */
	public String getValueSeparator() {
		return valueSeparator;
	}

	/**
	 * @param logicalOperatorBracket the logicalOperatorBracket to set
	 */
	public void setLogicalOperatorBracket(String logicalOperatorBracket) {
		this.logicalOperatorBracket = logicalOperatorBracket;
	}

	/**
	 * @return the logicalOperatorBracket
	 */
	public String getLogicalOperatorBracket() {
		return logicalOperatorBracket;
	}

	/**
	 * @param logicalOperatorValue the logicalOperatorValue to set
	 */
	public void setLogicalOperatorValue(String logicalOperatorValue) {
		this.logicalOperatorValue = logicalOperatorValue;
	}

	/**
	 * @return the logicalOperatorValue
	 */
	public String getLogicalOperatorValue() {
		return logicalOperatorValue;
	}

	/**
	 * @param variableType the variableType to set
	 */
	public void setVariableType(String variableType) {
		this.variableType = variableType;
	}

	/**
	 * @return the variableType
	 */
	public String getVariableType() {
		return variableType;
	}

	/**
	 * @param validSubQuery the validSubQuery to set
	 */
	public void setValidSubQuery(boolean validSubQuery) {
		this.validSubQuery = validSubQuery;
	}

	/**
	 * @return the validSubQuery
	 */
	public boolean isValidSubQuery() {
		return validSubQuery;
	}

	/**
	 * @param bracketOnly the bracketOnly to set
	 */
	public void setBracketOnly(boolean bracketOnly) {
		this.bracketOnly = bracketOnly;
	}

	/**
	 * @return the bracketOnly
	 */
	public boolean isBracketOnly() {
		return bracketOnly;
	}
	
	
	
	public String getQueryPart(String validatedValue) {
		String result = "";
		
		if (this.type.equals("")) {
			// do nothing
			this.setValidSubQuery(true);
			this.setBracketOnly(true);
			
		} else if (this.type.equals("single")) {
			// build subquery string
			if (this.variableType.equals("string")) {
				result = "(content." + this.attributeName + " " + this.compareOperator + " '" + validatedValue + "')";  
			} else {
				result = "(content." + this.attributeName + " " + this.compareOperator + " " + validatedValue + ")";
			}
			this.setValidSubQuery(true);
			this.setBracketOnly(false);
		} else {
			StringTokenizer values = new StringTokenizer(validatedValue,this.valueSeparator);
			StringBuffer resultBuffer = new StringBuffer();
			int i=1;
			while (values.hasMoreTokens()) {
				String value = values.nextToken();
				if (i>1) {
					resultBuffer.append(" " + this.logicalOperatorValue + " ");
				}
				i++;
				resultBuffer.append("(content." + this.attributeName + " " + this.compareOperator + " '" + value + "')");
			}
			this.setValidSubQuery(true);
			this.setBracketOnly(false);
			result = resultBuffer.toString();
		}
		
		
		return result;
	}


}
