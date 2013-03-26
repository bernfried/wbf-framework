package de.webertise.wbf.base.shortcuts;

public class Shortcut {

  public final static int SHORTCUT_TYPE_DOMAIN = 1;
  public final static int SHORTCUT_TYPE_URL    = 2;

  private String          pattern;
  private String          targetUrl;
  private int             type;

  public Shortcut(String pattern, String targetUrl, int type) {
    this.setPattern(pattern);
    this.setTargetUrl(targetUrl);
    this.setType(type);
  }

  /**
   * @return the pattern
   */
  public String getPattern() {
    return pattern;
  }

  /**
   * @param pattern the pattern to set
   */
  public void setPattern(String pattern) {
    this.pattern = pattern;
  }

  /**
   * @return the targetUrl
   */
  public String getTargetUrl() {
    return targetUrl;
  }

  /**
   * @param targetUrl the targetUrl to set
   */
  public void setTargetUrl(String targetUrl) {
    this.targetUrl = targetUrl;
  }

  /**
   * @return the type
   */
  public int getType() {
    return type;
  }

  /**
   * @param type the type to set
   */
  public void setType(int type) {
    this.type = type;
  }

}
