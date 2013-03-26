/** 
 * 
 * 
 * @author Bernfried Howe, bernfried.howe@webertise.de
 * @version 1.0
 */
package de.webertise.ds.services;

public interface LogServicesConstants {

  // general constants
  public final static String LOG_LINE_PREFIX = "*** LogServices - ";
  public final static String LOG_LINE_SUFFIX = " ***";

  // log modes
  public final static int    LOG_MODE_INFO   = 1;
  public final static int    LOG_MODE_WARN   = 2;
  public final static int    LOG_MODE_ERROR  = 3;
  public final static int    LOG_MODE_DEBUG  = 4;
  public final static int    LOG_MODE_TRACE  = 5;

}
