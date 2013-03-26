/*
 * WebModule.java
 *
 * Created on 7. Dezember 2002, 18:41
 */

package de.webertise.wbf.base.webmodule;

import de.webertise.ds.services.LogServices;

/**
 * 
 * @author bernfried.howe
 */
public class WebModule {

  // constants
  private final static String LOG_CATEGORY = "wbf_webmodule";

  private String              name;
  private String              classpath;
  private WebModuleSecurity   webModuleSecurity;
  private LogServices         log                   = LogServices.getInstance(LOG_CATEGORY);

  /** Creates a new instance of WebModule */
  public WebModule() {
    this.name = "";
    this.classpath = "";
    this.webModuleSecurity = new WebModuleSecurity();
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setClasspath(String classpath) {
    this.classpath = classpath;
  }

  public String getName() {
    return this.name;
  }

  public String getClasspath() {
    return this.classpath;
  }

  public void addIpSecurityItem(String ipAddress) {
    if ((ipAddress != null) && (!ipAddress.equals(""))) {
      this.webModuleSecurity.addIpSecurityItem(ipAddress);
    } else {
      String logMsg = "IP Address is empty.";
      log.debug(LOG_CATEGORY, "WebModule.java - addIpSecurityItem" + logMsg);
    }
  }

  public boolean checkIpSecurity(String ipAddress) {
    return this.webModuleSecurity.checkIpSecurity(ipAddress);
  }

  public String[] getIpAddressList() {
    return webModuleSecurity.getIpAddresses();
  }

}
