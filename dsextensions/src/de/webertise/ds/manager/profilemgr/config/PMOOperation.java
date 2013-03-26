/**
 * 
 */
package de.webertise.ds.manager.profilemgr.config;

import java.util.Hashtable;

// import de.webertise.ds.services.LogServices;

/**
 * @author bernfried
 *
 */
public class PMOOperation {
	
	// declare log services
	// private LogServices log = LogServices.getInstance();
	
	private String name;
	private String type; 
	private String operationClasspath;
	private String target;
	private Hashtable<String,PMOInputParameter> pmoInputParameter = new Hashtable<String,PMOInputParameter>();
	private Hashtable<String,PMOOutputParameter> pmoOutputParameter = new Hashtable<String,PMOOutputParameter>();

	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @return the pmoInputParameter
	 */
	public Hashtable<String, PMOInputParameter> getPmoInputParameter() {
		return pmoInputParameter;
	}
	/**
	 * @return the pmoOutputParameter
	 */
	public Hashtable<String, PMOOutputParameter> getPmoOutputParameter() {
		return pmoOutputParameter;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @param pmoInputParameter the pmoInputParameter to set
	 */
	public void setPmoInputParameter(Hashtable<String, PMOInputParameter> pmoInputParameter) {
		this.pmoInputParameter = pmoInputParameter;
	}
	/**
	 * @param pmoOutputParameter the pmoOutputParameter to set
	 */
	public void setPmoOutputParameter(Hashtable<String, PMOOutputParameter> pmoOutputParameter) {
		this.pmoOutputParameter = pmoOutputParameter;
	}
	/**
	 * @param operationClasspath the operationClasspath to set
	 */
	public void setOperationClasspath(String operationClasspath) {
		this.operationClasspath = operationClasspath;
	}
	/**
	 * @return the operationClasspath
	 */
	public String getOperationClasspath() {
		return operationClasspath;
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
	 * @param target the target to set
	 */
	public void setTarget(String target) {
		this.target = target;
	}
	/**
	 * @return the target
	 */
	public String getTarget() {
		return target;
	}

}
