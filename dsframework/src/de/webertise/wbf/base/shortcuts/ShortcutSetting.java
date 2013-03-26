package de.webertise.wbf.base.shortcuts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import de.reddot.api.common.content.CoaContent;
import de.reddot.api.common.content.CoaContentData;
import de.reddot.api.common.session.CoaSession;
import de.webertise.ds.services.ContentServices;
import de.webertise.ds.services.LogServices;

public class ShortcutSetting {

  // *********************************************************
  // * properties
  // *********************************************************
  private final static String LOG_CATEGORY = "wbf_settings";
  private LogServices         log          = LogServices.getInstance(LOG_CATEGORY);
  private ArrayList<Shortcut> shortcuts    = new ArrayList<Shortcut>();

  // *********************************************************
  // * constructor
  // *********************************************************
  public ShortcutSetting() {
  }

  // *********************************************************
  // * Getters and Setters
  // *********************************************************

  /**
   * @return the shortCuts
   */
  public ArrayList<Shortcut> getShortcuts() {
    return shortcuts;
  }

  /**
   * @param shortCuts the shortCuts to set
   */
  public void setShortcuts(ArrayList<Shortcut> shortCuts) {
    this.shortcuts = shortCuts;
  }

  // *********************************************************
  // * Methods
  // *********************************************************
  public String checkShortcut(String sourceRequestUri, String sourceUrl) {

    String targetUrl = "";

    Iterator<Shortcut> scIter = this.shortcuts.iterator();
    while(scIter.hasNext()) {

      Shortcut sc = scIter.next();

      // check pattern for type domain
      Pattern p = Pattern.compile(sc.getPattern());
      Matcher m = null;
      if (sc.getType() == Shortcut.SHORTCUT_TYPE_DOMAIN) {
        m = p.matcher(sourceUrl);
        log.debug(LOG_CATEGORY, "ShortCutEngine - checkShortCut: RequestUri='" + sourceUrl + "'");
      }

      // check pattern for type url
      if (sc.getType() == Shortcut.SHORTCUT_TYPE_URL) {
        m = p.matcher(sourceRequestUri);
        log.debug(LOG_CATEGORY, "ShortCutEngine - checkShortCut: Url='" + sourceRequestUri + "'");
      }
      if (m.find()) {
        log.debug(LOG_CATEGORY, "ShortCutEngine - checkShortCut: Pattern '" + sc.getPattern() + "' matches");
        targetUrl = sc.getTargetUrl();
        break;
      } else {
        log.debug(LOG_CATEGORY, "ShortCutEngine - checkShortCut: Pattern '" + sc.getPattern() + "' does not match");
      }
    }
    return targetUrl;
  }

  public boolean readShortcutSettingsFromContent(CoaSession session, String projectName, String contentName) {

    // read content data from shortcuts.xml
    ContentServices ctntServices = new ContentServices();
    CoaContent ctnt = ctntServices.getContent(session, projectName, "", contentName);
    log.debug(LOG_CATEGORY, "ShortCutEngine - readShortCuts: Read content '" + ctnt.getName() + "'");

    // get xml from de content data
    CoaContentData data = null;
    data = ctnt.getCoaContentData(Locale.ENGLISH, CoaContent.LOCALE_POLICY_NONE, false);
    log.debug(LOG_CATEGORY, "ShortCutEngine - readShortCuts: Read content data '" + data.getText() + "'");

    // use jdom for creating a string of the xml
    SAXBuilder sb = new SAXBuilder();
    Document doc = null;
    try {
      doc = sb.build(data.getStream());
      log.debug(LOG_CATEGORY, "Document read from data.getText()");
    } catch (JDOMException e) {
      log.debug(LOG_CATEGORY, "JDOM Exception: " + e.toString());
      return false;
    } catch (IOException e) {
      log.debug(LOG_CATEGORY, "IO Exception: " + e.toString());
      return false;
    }

    // if document could be read as xml
    if (doc != null) {
      @SuppressWarnings("unchecked")
      Iterator<Element> iter = doc.getRootElement().getChildren().iterator();
      log.debug(LOG_CATEGORY, "ShortCutEngine - readShortCuts: Amount of elements: '" + doc.getRootElement().getChildren().size() + "'");
      while(iter.hasNext()) {
        Element elem = iter.next();
        String pattern = elem.getChildText("pattern");
        String target = elem.getChildText("target");
        String type = elem.getChildText("type");
        if (pattern == null || "".equals(pattern) || target == null || "".equals(target)) {
          log.debug(LOG_CATEGORY, "ShortCutEngine - readShortCuts: Shortcut for pattern '" + pattern + "' and target '" + target + "' has no valid type");
        } else {
          if (type != null && type.equals("1")) {
            // create domain shortcut
            Shortcut sc = new Shortcut(pattern, target, Shortcut.SHORTCUT_TYPE_DOMAIN);
            this.shortcuts.add(sc);
            log.debug(LOG_CATEGORY, "ShortCutEngine - readShortCuts: Domain-Shortcut for pattern '" + pattern + "' and target '" + target + "' added");
          } else if (type != null && type.equals("2")) {
            // create url shortcut
            Shortcut sc = new Shortcut(pattern, target, Shortcut.SHORTCUT_TYPE_URL);
            this.shortcuts.add(sc);
            log.debug(LOG_CATEGORY, "ShortCutEngine - readShortCuts: URL-Shortcut for pattern '" + pattern + "' and target '" + target + "' added");
          } else {
            log.debug(LOG_CATEGORY, "ShortCutEngine - readShortCuts: Shortcut for pattern '" + pattern + "' and target '" + target + "' has no valid type");
          }
        }
      }
    } else {
      log.debug(LOG_CATEGORY, "ShortCutEngine - readShortCuts: JDOM Document is null!");
    }
    return true;
  }
}
