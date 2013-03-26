/*
 * WebModuleIpSecurityItem.java
 *
 * Created on 7. Dezember 2002, 18:57
 */

package de.webertise.wbf.base.webmodule;

import java.util.StringTokenizer;
import java.lang.NumberFormatException;
import de.webertise.ds.services.LogServices;

/**
 * 
 * @author bernfried.howe
 */
public class WebModuleIpSecurityItem {

  // constants
  private final static String LOG_CATEGORY = "wbf_framework";

  private int                 ipBlockMin[] = { 0, 0, 0, 0 };
  private int                 ipBlockMax[] = { 255, 255, 255, 255 };
  private String              ipAddress    = "";
  private String              logMsg       = "";

  private LogServices         log          = LogServices.getInstance(LOG_CATEGORY);

  public WebModuleIpSecurityItem() {
  }

  public int getIpBlockMin(int index) {
    return this.ipBlockMin[index];
  }

  public int getIpBlockMax(int index) {
    return this.ipBlockMax[index];
  }

  public void setIpAddress(String ipAddress) {
    this.ipAddress = ipAddress;
  }

  public String getIpAddress() {
    return this.ipAddress;
  }

  /** Creates a new instance of WebModuleIpSecurityItem */
  public WebModuleIpSecurityItem(String ipAddress) {

    StringTokenizer ipBlockRangeTokens = null;
    String token = "";
    String min = "";
    String max = "";

    if ((ipAddress == null) || (ipAddress.equals(""))) {
      logMsg = "IP Address is null or empty.";
      log.debug(LOG_CATEGORY, "WebModuleIpSecurityItem. - WebModuleIpSecurityItem" + logMsg);
      return;
    }

    StringTokenizer ipBlockTokens = new StringTokenizer(ipAddress, ".");
    if (ipBlockTokens.countTokens() == 8) {
      // nothing

    } else if (ipBlockTokens.countTokens() != 4) {
      logMsg = "IP Address '" + ipAddress + "' has an invalid format.";
      log.debug(LOG_CATEGORY, "WebModuleIpSecurityItem. - WebModuleIpSecurityItem" + logMsg);
      return;
    }

    int block = 0;
    while(ipBlockTokens.hasMoreTokens()) {
      token = ipBlockTokens.nextToken();

      // if * , initial values can be used
      if (token.equals("*"))
        continue;

      // if range, identify first and last value
      ipBlockRangeTokens = new StringTokenizer(token, "-");
      if (ipBlockRangeTokens.countTokens() == 2) {
        min = ipBlockRangeTokens.nextToken();
        max = ipBlockRangeTokens.nextToken();
        try {
          this.ipBlockMin[block] = Integer.parseInt(min);
          this.ipBlockMax[block] = Integer.parseInt(max);
        } catch (NumberFormatException nfe) {
          logMsg = "NumberFormatException: IP Address Min Block '" + this.ipBlockMin[block] + " and/or Max Block '" + this.ipBlockMax[block] + "' is not a number.";
          log.debug(LOG_CATEGORY, "WebModuleIpSecurityItem. - WebModuleIpSecurityItem" + logMsg);
        }
      } else if (ipBlockRangeTokens.countTokens() == 1) {
        min = ipBlockRangeTokens.nextToken();
        try {
          this.ipBlockMin[block] = Integer.parseInt(min);
          this.ipBlockMax[block] = Integer.parseInt(min);
        } catch (NumberFormatException nfe) {
          logMsg = "NumberFormatException: IP Address Min Block '" + this.ipBlockMin[block] + " and/or Max Block '" + this.ipBlockMax[block] + "' is not a number.";
          log.debug(LOG_CATEGORY, "WebModuleIpSecurityItem. - WebModuleIpSecurityItem" + logMsg);
        }
      } else {
        logMsg = "IP Address count tokens invalid.";
        log.debug(LOG_CATEGORY, "WebModuleIpSecurityItem. - WebModuleIpSecurityItem" + logMsg);
      }
      block++;
    }
  }
}
