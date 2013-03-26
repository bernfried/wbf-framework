package de.webertise.wbf.base.project;

public class Project {

  private String   name = "";
  private String[] locales;

  public Project(String name) {
    this.setName(name);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String[] getLocales() {
    return locales;
  }

  public void setLocales(String[] locales) {
    this.locales = locales;
  }

  public boolean exists(String locale) {
    for (int i = 0; i < this.locales.length; i++) {
      if (this.locales[i].equals(locale))
        return true;
    }
    return false;
  }

  public String getLocalesAsString() {
    String result = "";
    for (int i = 0; i < this.locales.length; i++) {
      result = result + this.locales[i] + "|";
    }
    result = result.substring(0, result.length() - 1);
    return result;
  }

}
