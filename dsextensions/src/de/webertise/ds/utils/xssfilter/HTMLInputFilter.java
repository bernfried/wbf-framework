package de.webertise.ds.utils.xssfilter;

public class HTMLInputFilter
{   
    
    public HTMLInputFilter() {
      
    }
    
    public static String filter(String value) {
      value = value.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
      value = value.replaceAll("\\(", "&#40;").replaceAll("\\)", "&#41;");
      value = value.replaceAll("'", "&#39;");
      value = value.replaceAll("eval\\((.*)\\)", "");
      value = value.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");
      value = value.replaceAll("script", "");
      return value;
    }
    
}