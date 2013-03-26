/** 
 * 
 * 
 * @author Bernfried Howe, bernfried.howe@webertise.de
 * @version 1.0
 */
package de.webertise.ds.services;

import java.util.ArrayList;
import java.util.Hashtable;

import de.reddot.api.common.util.CoaLogger;
import de.reddot.api.common.util.CoaLoggerFactory;

public class LogServices implements LogServicesConstants {

  // **********************************************************
  // * static properties
  // **********************************************************
  private static LogServices           instance       = null;

  // **********************************************************
  // * properties
  // **********************************************************
  private Hashtable<String, CoaLogger> loggerList;
  private ArrayList<String>            requestLogger  = new ArrayList<String>();
  private boolean                      requestLogging = false;

  // **********************************************************
  // * constructors
  // **********************************************************
  private LogServices(String loggerName) {
    loggerList = new Hashtable<String, CoaLogger>();
    if (!loggerList.containsKey(loggerName)) {
      CoaLogger logger = CoaLoggerFactory.getLogger(loggerName);
      loggerList.put(loggerName, logger);
    }
  }

  // **********************************************************
  // * get instances
  // **********************************************************
  public static LogServices getInstance(String loggerName) {
    if (LogServices.instance == null) {
      LogServices.instance = new LogServices(loggerName);
    }
    return instance;
  }

  // **********************************************************
  // * getter and setter
  // **********************************************************
  public Hashtable<String, CoaLogger> getLoggerList() {
    return loggerList;
  }

  public void setLoggerList(Hashtable<String, CoaLogger> loggerList) {
    this.loggerList = loggerList;
  }

  public void setRequestLogging(boolean flag) {
    this.requestLogging = flag;
    this.requestLogger.clear();
  }

  public ArrayList<String> getRequestLoggerData() {
    return requestLogger;
  }

  // **********************************************************
  // * methods
  // **********************************************************
  private void checkAndAddLogger(String loggerName) {
    if (!this.loggerList.containsKey(loggerName)) {
      CoaLogger logger = CoaLoggerFactory.getLogger(loggerName);
      loggerList.put(loggerName, logger);
    }
  }

  public void warn(String loggerName, String msg) {
    checkAndAddLogger(loggerName);
    if (loggerList.get(loggerName).isWarnEnabled()) {
      loggerList.get(loggerName).warn(LOG_LINE_PREFIX + "(" + loggerName + ") - " + msg + LOG_LINE_SUFFIX);
      if (this.requestLogging)
        this.requestLogger.add("[WARN] - " + loggerName + " - " + msg);
    }
  }

  public void info(String loggerName, String msg) {
    checkAndAddLogger(loggerName);
    if (loggerList.get(loggerName).isInfoEnabled()) {
      loggerList.get(loggerName).info(LOG_LINE_PREFIX + "(" + loggerName + ") - " + msg + LOG_LINE_SUFFIX);
      if (this.requestLogging)
        this.requestLogger.add("[INFO] - " + loggerName + " - " + msg);
    }
  }

  public void debug(String loggerName, String msg) {
    checkAndAddLogger(loggerName);
    if (loggerList.get(loggerName).isDebugEnabled()) {
      loggerList.get(loggerName).debug(LOG_LINE_PREFIX + "(" + loggerName + ") - " + msg + LOG_LINE_SUFFIX);
      if (this.requestLogging)
        this.requestLogger.add("[DEBUG] - " + loggerName + " - " + msg);
    }
  }

  public void trace(String loggerName, String msg) {
    checkAndAddLogger(loggerName);
    if (loggerList.get(loggerName).isTraceEnabled()) {
      loggerList.get(loggerName).trace(LOG_LINE_PREFIX + "(" + loggerName + ") - " + msg + LOG_LINE_SUFFIX);
      if (this.requestLogging)
        this.requestLogger.add("[TRACE] - " + loggerName + " - " + msg);
    }
  }

  public void printLogConfig(String loggerName) {

    CoaLogger logger = loggerList.get(loggerName);

    System.out.println("Is Debug Enabled: " + logger.isDebugEnabled());
    System.out.println("Is Info  Enabled: " + logger.isInfoEnabled());
    System.out.println("Is Trace Enabled: " + logger.isTraceEnabled());
    System.out.println("Is Warn  Enabled: " + logger.isWarnEnabled());

  }

}
