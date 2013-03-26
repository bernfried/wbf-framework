/*
 * WebModuleSecurity.java
 *
 * Created on 7. Dezember 2002, 18:53
 */

package de.webertise.wbf.base.webmodule;

import de.webertise.ds.services.LogServices;
import de.webertise.wbf.base.webmodule.WebModuleIpSecurityItem;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * 
 * @author bernfried.howe
 */
public class WebModuleSecurity {
  // constants
  private final static String                LOG_CATEGORY    = "wbf_framework";

  private LogServices                        log             = LogServices.getInstance(LOG_CATEGORY);
  private String                             logMsg          = "";

  private ArrayList<WebModuleIpSecurityItem> ipSecurityItems = new ArrayList<WebModuleIpSecurityItem>();

  /** Creates a new instance of WebModuleSecurity */
  public WebModuleSecurity() {
  }

  public void addIpSecurityItem(String ipAddress) {

    if ((ipAddress != null) && (!ipAddress.equals(""))) {
      WebModuleIpSecurityItem ipSecurityItem = new WebModuleIpSecurityItem(ipAddress);
      ipSecurityItem.setIpAddress(ipAddress);
      this.ipSecurityItems.add(ipSecurityItem);
    } else {
      logMsg = "IP Address is null or empty.";
      log.debug(LOG_CATEGORY, "WebModuleSecurity.java - addIpSecurityItem" + logMsg);
    }
  }

  public boolean checkIpSecurity(String ipAddress) {

    // if nothing is restricted, allow any ip.
    if (this.ipSecurityItems.isEmpty())
      return true;

    // check ip address format
    if ((ipAddress == null) || (ipAddress.equals(""))) {
      logMsg = "IP Address is null or empty.";
      log.debug(LOG_CATEGORY, "WebModuleSecurity.java - checkIpSecurity" + logMsg);
      return false;
    }

    StringTokenizer ipBlockTokens = new StringTokenizer(ipAddress, ".");
    if (ipBlockTokens.countTokens() != 4) {
      logMsg = "IP Block Tokens not equal 4 - Invalid IP Address.";
      log.debug(LOG_CATEGORY, "WebModuleSecurity.java - checkIpSecurity" + logMsg);
      return false;
    }

    // get blocks of ip address to be checked
    int block = 0;
    int token[] = { 0, 0, 0, 0 };
    boolean check = true;
    String strToken = "";
    while(ipBlockTokens.hasMoreTokens()) {
      strToken = ipBlockTokens.nextToken();
      try {
        token[block] = Integer.parseInt(strToken);
      } catch (NumberFormatException nfe) {
        logMsg = "NumberFormatException - Block '" + token[block] + "' not valid.";
        log.debug(LOG_CATEGORY, "WebModuleSecurity.java - checkIpSecurity" + logMsg);
        return false;
      }
      block++;
    }

    // check ip address against all security items
    WebModuleIpSecurityItem webModuleIpSecurityItem = null;
    int i, j;
    for (i = 0; i < ipSecurityItems.size(); i++) {
      webModuleIpSecurityItem = (WebModuleIpSecurityItem) this.ipSecurityItems.get(i);
      // check block by block
      check = true;
      for (j = 0; j < 4; j++) {
        logMsg = "Compare: " + token[j] + " <=> " + webModuleIpSecurityItem.getIpBlockMin(j) + " - " + webModuleIpSecurityItem.getIpBlockMax(j);
        log.debug(LOG_CATEGORY, "WebModuleSecurity.java - checkIpSecurity" + logMsg);
        if ((token[j] < webModuleIpSecurityItem.getIpBlockMin(j)) || (token[j] > webModuleIpSecurityItem.getIpBlockMax(j))) {
          check = false;
          break;
        }
      }
      if (check)
        break;
    }
    return check;
  }

  public String[] getIpAddresses() {

    String ipAddrList[] = new String[ipSecurityItems.size()];
    for (int i = 0; i < this.ipSecurityItems.size(); i++) {
      WebModuleIpSecurityItem wmSI = (WebModuleIpSecurityItem) this.ipSecurityItems.get(i);
      ipAddrList[i] = wmSI.getIpAddress();
    }
    return ipAddrList;
  }

}
