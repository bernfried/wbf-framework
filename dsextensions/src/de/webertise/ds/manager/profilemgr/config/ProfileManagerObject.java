package de.webertise.ds.manager.profilemgr.config;

import java.util.Hashtable;

public class ProfileManagerObject {

	private String name;
	private String attributePath;
	private String attributeSeparator;
	private String keyValueSeparator;
	private Hashtable<String,PMOOperation> pmoOperations = new Hashtable<String,PMOOperation>();
	private Hashtable<String,PMOValidation> pmoValidations = new Hashtable<String,PMOValidation>();

	/* ***************************** */
	/* Getter                        */
	/* ***************************** */
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @return the attributePath
	 */
	public String getAttributePath() {
		return attributePath;
	}
	/**
	 * @return the attribute separator
	 */
	public String getAttributeSeparator() {
		return attributeSeparator;
	}
	/**
	 * @return the key-value separator
	 */
	public String getKeyValueSeparator() {
		return keyValueSeparator;
	}
	/**
	 * @return the pmoOperations
	 */
	public Hashtable<String,PMOOperation> getPmoOperations() {
		return pmoOperations;
	}
	/**
	 * @return the pmoValidations
	 */
	public Hashtable<String,PMOValidation> getPmoValidations() {
		return pmoValidations;
	}
	/**
	 * @param operationName the name of the operation
	 * @return the pmoOperation
	 */
	public PMOOperation getOperation(String operation) {
		return this.pmoOperations.get(operation);
	}

	/* ***************************** */
	/* Setter                        */
	/* ***************************** */
	
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @param attributePath the attributePath to set
	 */
	public void setAttributePath(String attributePath) {
		this.attributePath = attributePath;
	}
	/**
	 * @param separator the attribute separator to set
	 */
	public void setAttributeSeparator(String separator) {
		this.attributeSeparator = separator;
	}
	/**
	 * @param separator the key-value separator to set
	 */
	public void setKeyValueSeparator(String separator) {
		this.keyValueSeparator = separator;
	}
	/**
	 * @param pmoOperations the pmoOperations to set
	 */
	public void setPmoOperations(Hashtable<String,PMOOperation> pmoOperations) {
		this.pmoOperations = pmoOperations;
	}
	/**
	 * @param pmoValidations the pmoValidations to set
	 */
	public void setPmoValidations(Hashtable<String,PMOValidation> pmoValidations) {
		this.pmoValidations = pmoValidations;
	}	
	
}
