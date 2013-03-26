package de.webertise.ds.manager.profilemgr.config;

public class PMOOutputParameter {
	
	private String name;
	private String property;
	private String urlEncoding;
	private boolean asCData;
	private String numberFormat;
	private String dateFormat;
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @return the property
	 */
	public String getProperty() {
		return property;
	}
	/**
	 * @return the urlEncoding
	 */
	public String getUrlEncoding() {
		return urlEncoding;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @param property the property to set
	 */
	public void setProperty(String property) {
		this.property = property;
	}
	/**
	 * @param urlEncoding the urlEncoding to set
	 */
	public void setUrlEncoding(String urlEncoding) {
		this.urlEncoding = urlEncoding;
	}
	/**
	 * @param asCData the asCData to set
	 */
	public void setAsCData(boolean asCData) {
		this.asCData = asCData;
	}
	/**
	 * @return the asCData
	 */
	public boolean isAsCData() {
		return asCData;
	}
	/**
	 * @param numberFormat the numberFormat to set
	 */
	public void setNumberFormat(String numberFormat) {
		this.numberFormat = numberFormat;
	}
	/**
	 * @return the numberFormat
	 */
	public String getNumberFormat() {
		return numberFormat;
	}
	/**
	 * @param dateFormat the dateFormat to set
	 */
	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}
	/**
	 * @return the dateFormat
	 */
	public String getDateFormat() {
		return dateFormat;
	}

}
