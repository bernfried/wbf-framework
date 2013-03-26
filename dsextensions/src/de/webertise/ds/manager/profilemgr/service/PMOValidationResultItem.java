package de.webertise.ds.manager.profilemgr.service;

public class PMOValidationResultItem {

	private String name;
	private String origValue;
	private String validatedValue;
	private boolean valid;
	private boolean minLengthFailed;
	private boolean maxLengthFailed;
	private boolean regExFailed;
	private boolean mandatoryFailed;
	private boolean booleanCheckFailed;
	private boolean dateCheckFailed;
	private boolean numberCheckFailed;
	private boolean persistent;
	
	public PMOValidationResultItem() {
		this.valid = true;
		this.origValue = "";
		this.validatedValue = "";
		this.maxLengthFailed = false;
		this.minLengthFailed = false;
		this.regExFailed = false;
		this.mandatoryFailed = false;
		this.persistent = false;
	}
	
	public PMOValidationResultItem(String name, String origValue, String validatedValue) {
		this.name = name;
		this.origValue = origValue;
		this.validatedValue = validatedValue;
		this.valid = true;
		this.maxLengthFailed = false;
		this.minLengthFailed = false;
		this.regExFailed = false;
		this.mandatoryFailed = false;
		this.persistent = true;
	}
	
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @return the origValue
	 */
	public String getOrigValue() {
		return origValue;
	}
	/**
	 * @return the validatedValue
	 */
	public String getValidatedValue() {
		return validatedValue;
	}
	/**
	 * @return the valid
	 */
	public boolean isValid() {
		return valid;
	}
	/**
	 * @return the minLengthFailed
	 */
	public boolean isMinLengthFailed() {
		return minLengthFailed;
	}
	/**
	 * @return the maxLengthFailed
	 */
	public boolean isMaxLengthFailed() {
		return maxLengthFailed;
	}
	/**
	 * @return the regExFailed
	 */
	public boolean isRegExFailed() {
		return regExFailed;
	}
	/**
	 * @return the mandatoryFailed
	 */
	public boolean isMandatoryFailed() {
		return mandatoryFailed;
	}

	
	
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @param origValue the origValue to set
	 */
	public void setOrigValue(String origValue) {
		this.origValue = origValue;
	}
	/**
	 * @param validatedValue the validatedValue to set
	 */
	public void setValidatedValue(String validatedValue) {
		this.validatedValue = validatedValue;
	}
	/**
	 * @param valid the valid to set
	 */
	public void setValid(boolean valid) {
		this.valid = valid;
	}
	/**
	 * @param minLengthFailed the minLengthFailed to set
	 */
	public void setMinLengthFailed(boolean minLengthFailed) {
		this.minLengthFailed = minLengthFailed;
	}
	/**
	 * @param maxLengthFailed the maxLengthFailed to set
	 */
	public void setMaxLengthFailed(boolean maxLengthFailed) {
		this.maxLengthFailed = maxLengthFailed;
	}
	/**
	 * @param regExFailed the regExFailed to set
	 */
	public void setRegExFailed(boolean regExFailed) {
		this.regExFailed = regExFailed;
	}

	/**
	 * @param mandatoryFailed the mandatoryFailed to set
	 */
	public void setMandatoryFailed(boolean mandatoryFailed) {
		this.mandatoryFailed = mandatoryFailed;
	}
	
	/**
	 * @param persistent the persistent to set
	 */
	public void setPersistent(boolean persistent) {
		this.persistent = persistent;
	}

	/**
	 * @return the persistent
	 */
	public boolean isPersistent() {
		return persistent;
	}


	/**
	 * @param booleanCheckFailed the booleanCheckFailed to set
	 */
	public void setBooleanCheckFailed(boolean booleanCheckFailed) {
		this.booleanCheckFailed = booleanCheckFailed;
	}



	/**
	 * @return the booleanCheckFailed
	 */
	public boolean isBooleanCheckFailed() {
		return booleanCheckFailed;
	}



	/**
	 * @param dateCheckFailed the dateCheckFailed to set
	 */
	public void setDateCheckFailed(boolean dateCheckFailed) {
		this.dateCheckFailed = dateCheckFailed;
	}



	/**
	 * @return the dateCheckFailed
	 */
	public boolean isDateCheckFailed() {
		return dateCheckFailed;
	}



	/**
	 * @param numberCheckFailed the numberCheckFailed to set
	 */
	public void setNumberCheckFailed(boolean numberCheckFailed) {
		this.numberCheckFailed = numberCheckFailed;
	}



	/**
	 * @return the numberCheckFailed
	 */
	public boolean isNumberCheckFailed() {
		return numberCheckFailed;
	}
	
}
