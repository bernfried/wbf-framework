package de.webertise.wbf.admin.utils;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.webertise.ds.services.LogServices;
import de.webertise.wbf.base.action.ActionResponseItem;

public class Substitutor {

  // constants
  private final static String LOG_CATEGORY         = "wbf_weblet";

  // variables
  private static LogServices  log                  = LogServices.getInstance(LOG_CATEGORY);

  private static final int    TEXT_MODE            = 0;
  private static final int    VAR_MODE             = 1;
  private static final int    IDENTIFY_PREFIX_MODE = 2;
  private static final int    IDENTIFY_SUFFIX_MODE = 3;
  private static final int    CANCEL_PREFIX_MODE   = 4;
  private static final int    CANCEL_SUFFIX_MODE   = 5;

  /**
   * This method replaces all placeholders beginning with prefix and ending with suffix by an value which can be found in valuesMap based on the string between prefix and suffix. e.g. <#test#>
   * searches for a key "test" in valuesMap and replaces the whole placeholder with the corresponding value.
   * 
   * @param inputString
   * @param valuesMap
   * @param prefix
   * @param suffix
   * @return
   * @throws IOException
   */
  public static String substituteVariables(String inputString, Map<String, ActionResponseItem> valuesMap, String prefix, String suffix) throws IOException {

    StringReader inputStream = new StringReader(inputString);
    StringWriter outputStream = new StringWriter();
    StringWriter varStream = new StringWriter();
    StringWriter prefixStream = new StringWriter();
    StringWriter suffixStream = new StringWriter();

    ActionResponseItem value;
    int c;

    byte[] prefixChars = prefix.getBytes();
    byte[] suffixChars = suffix.getBytes();

    int mode = TEXT_MODE;
    int iPrefixChar = 0;
    int iSuffixChar = 0;
    boolean hasPrefix = false;

    while((c = inputStream.read()) != -1) {

      if (c == prefixChars[0]) { // Beginn eines Prefix
        mode = IDENTIFY_PREFIX_MODE;

      } else if (iPrefixChar > 0 && iPrefixChar < prefixChars.length && c == prefixChars[iPrefixChar]) {
        // Fortsetzen eines Prefix
        mode = IDENTIFY_PREFIX_MODE;

      } else if (iPrefixChar > 0 && iPrefixChar < prefixChars.length && c != prefixChars[iPrefixChar]) {
        // Abbrechen der Prefix Identifikation
        mode = CANCEL_PREFIX_MODE;

      } else if (iPrefixChar == prefixChars.length) {
        // Abschlie§en der Prefix Identifikation
        iPrefixChar = 0;
        hasPrefix = true;
        prefixStream = new StringWriter();
        mode = VAR_MODE;

      } else if (hasPrefix && c == suffixChars[0]) { // Beginn eines Suffix
        mode = IDENTIFY_SUFFIX_MODE;

      } else if (hasPrefix && iSuffixChar > 0 && iSuffixChar < suffixChars.length && c == suffixChars[iSuffixChar]) {
        // Fortsetzen der Suffix
        mode = IDENTIFY_SUFFIX_MODE;

      } else if (hasPrefix && iSuffixChar > 0 && iSuffixChar < suffixChars.length && c != suffixChars[iSuffixChar]) {
        // Abbrechen der Suffix Identifikation
        mode = CANCEL_SUFFIX_MODE;

      }

      if (iSuffixChar == suffixChars.length) {
        // Abschlie§en der Suffix Identifikation
        hasPrefix = false;

        if (c == prefixChars[0]) { // Beginn eines Prefix
          mode = IDENTIFY_PREFIX_MODE;
        } else {
          mode = TEXT_MODE;
        }
        value = valuesMap.get(varStream.toString());
        if (value != null) {
          if (value.getValue() instanceof String) {
            outputStream.write((String) value.getValue());
          } else {
            outputStream.write("");
          }
        }
        suffixStream = new StringWriter();
        varStream = new StringWriter();
        iSuffixChar = 0;
      }

      switch (mode) {
        case TEXT_MODE:
          outputStream.write(c);
          break;
        case VAR_MODE:
          varStream.write(c);
          break;
        case IDENTIFY_PREFIX_MODE:
          prefixStream.write(c);
          iPrefixChar++;
          break;
        case IDENTIFY_SUFFIX_MODE:
          suffixStream.write(c);
          iSuffixChar++;
          break;
        case CANCEL_PREFIX_MODE:
          outputStream.write(prefixStream.toString());
          outputStream.write(c);
          iPrefixChar = 0;
          prefixStream = new StringWriter();
          mode = TEXT_MODE;
          break;
        case CANCEL_SUFFIX_MODE:
          varStream.write(suffixStream.toString());
          varStream.write(c);
          iSuffixChar = 0;
          suffixStream = new StringWriter();
          mode = VAR_MODE;
          hasPrefix = false;
          break;
      }

      // log
      log.trace(LOG_CATEGORY, "Substitutor - substituteVariables: Mode: '" + mode + "' Char: '" + c + "' Output: '" + outputStream.toString() + "' Var: '" + varStream.toString() + "'");

    }

    // if a placeholder suffix is at the end of the text string
    if (iSuffixChar == suffixChars.length) {
      value = valuesMap.get(varStream.toString());
      if (value != null) {
        if (value.getValue() instanceof String) {
          outputStream.write((String) value.getValue());
          log.trace(LOG_CATEGORY, "Substitutor - substituteVariables: placeholder name is '" + varStream.toString() + "' with value: '" + (String) value.getValue() + "'");
        } else {
          outputStream.write("");
          log.trace(LOG_CATEGORY, "Substitutor - substituteVariables: placeholder name is '" + varStream.toString() + "' but value: is not a string '" + (String) value.getValue() + "'");
        }
      }
    }

    return outputStream.toString();
  }

  public static String[] find(String text, String pattern) {

    if (text != null) {
      List<String> lst = new ArrayList<String>();

      // regex: \[\#[\w\s]*\#\]
      Pattern patt = Pattern.compile(pattern);
      Matcher matcher = patt.matcher(text);
      while(matcher.find()) {

        // get next match
        String match = matcher.group();

        // match: e.g. <#placehoderName#>
        // strip tags
        String phName = match.substring(2, match.length() - 2);

        // store in list
        lst.add(phName);
      }

      String[] ret = new String[lst.size()];
      lst.toArray(ret);
      return ret;
    } else {
      return null;
    }
  }

}
