package de.webertise.wbf.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexTester {

  /**
   * @param args
   */
  public static void main(String[] args) {

    String test = "[aaa][bbb][ccc]";
    String regex = "\\[(.*?)\\]";

    Pattern p = Pattern.compile(regex);
    Matcher m = p.matcher(test);

    while(m.find()) {
      System.out.println(m.group(1));
    }

  }

}
