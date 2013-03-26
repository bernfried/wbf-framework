/** 
 * 
 * 
 * @author Bernfried Howe, bernfried.howe@webertise.de
 * @version 1.0
 */
package de.webertise.ds.services;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;

import de.reddot.api.common.content.CoaContent;
import de.reddot.api.common.content.CoaContentController;
import de.reddot.api.common.content.CoaContentControllerFactory;
import de.reddot.api.common.content.CoaContentData;
import de.reddot.api.common.content.CoaContentGroup;
import de.reddot.api.common.content.CoaContentGroupController;
import de.reddot.api.common.content.CoaLock;
import de.reddot.api.common.session.CoaSession;
import de.reddot.api.exception.AccessNotGrantedException;
import de.reddot.api.exception.LockNotGrantedException;
import de.reddot.api.exception.ObjectExistsException;
import de.reddot.api.exception.ObjectNotFoundException;

/**
 * 
 * 
 * @author bernfried
 * 
 */
public class ContentServices {

  // declare log services
  private static LogServices log = LogServices.getInstance("wbtServices");

  /**
   * Updates a CoaContent object for a given locale variant in the DS repository.
   * 
   * @param session CoaSession of iolet
   * @param coaContent CoaContent object, which should be updated
   * @param contentData Updated CoaContentData object of the given locale,
   * @param attributes Hashtable with those attributes (key/value pairs) which should be written to the CoaContent object. There is no delete of attributes.
   * @param multiValueSeparator Value separator string, which separates multiple values of an attribute
   * @param locale Locale which defines the locale variant to be updated.
   * @return int Return code (0 = ok, smaller than 0 = exception)
   */
  public int updateContent(CoaSession session, CoaContent coaContent, String contentData, Hashtable<String, String> attributes, String multiValueSeparator, Locale locale) {

    // return code of this method
    int rtCode = 0;

    // write attributes
    if (attributes != null) {
      Enumeration<String> keys = attributes.keys();
      while(keys.hasMoreElements()) {
        String key = keys.nextElement();
        if (attributes.get(key).contains(multiValueSeparator)) {
          String mvList[] = attributes.get(key).split("\\" + multiValueSeparator);
          coaContent.setAttribute(key, locale, mvList);
          log.debug("wbtServices", "ContentServices - createContent: multi value list for ga relationship - length: " + mvList.length + " value: " + mvList[0] + "/" + mvList[1]);
        } else {
          coaContent.setAttribute(key, locale, attributes.get(key));
        }
      }
    }

    // update content
    CoaContentController ctntCtrl = CoaContentControllerFactory.getCoaContentController();
    CoaLock lock = null;
    try {
      // lock content
      lock = ctntCtrl.lockCoaContent(session, coaContent);

      // write content data
      boolean createIfNotExists = true;
      CoaContentData newData = coaContent.getCoaContentData(locale, CoaContent.LOCALE_POLICY_NONE, createIfNotExists);
      newData.setText(contentData);

      // set timestamp
      DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
      Date date = new Date();
      coaContent.setAttribute("lastUpdated", locale, dateFormat.format(date));

      // update content
      ctntCtrl.updateCoaContent(session, coaContent);
      log.debug("wbtServices", "ContentServices - updateContent: '" + coaContent.getName() + "' has been updated.");

    } catch (AccessNotGrantedException e) {
      rtCode = -1;
      log.debug("wbtServices", "ContentServices - updateContent: AccessNotGrantedException - Content '" + coaContent.getName() + "' is not accessable.");
    } catch (ObjectExistsException e) {
      rtCode = -2;
      log.debug("wbtServices", "ContentServices - updateContent: ObjectExistsException - Content '" + coaContent.getName() + "' does not exist.");
    } catch (ObjectNotFoundException e) {
      rtCode = -1;
      log.debug("wbtServices", "ContentServices - updateContent: ObjectNotFoundException - Content '" + coaContent.getName() + "' could not be found.");
    } catch (LockNotGrantedException e) {
      rtCode = -1;
      log.debug("wbtServices", "ContentServices - updateContent: LockNotGrantedException - Content '" + coaContent.getName() + "' is locked.");
    } finally {
      // unlock content
      ctntCtrl.unlockCoaContent(session, lock);
    }

    return rtCode;
  }

  /**
   * Updates a CoaContent object in DS repository.
   * 
   * @param session CoaSession of iolet
   * @param coaContent CoaContent object, which should be updated
   * @return int Return code (0 = ok, smaller than 0 = exception)
   */
  public int updateContent(CoaSession session, CoaContent coaContent) {

    // return code of this method
    int rtCode = 0;

    // update content
    CoaContentController ctntCtrl = CoaContentControllerFactory.getCoaContentController();
    CoaLock lock = null;
    try {
      // lock content
      lock = ctntCtrl.lockCoaContent(session, coaContent);

      // update content
      ctntCtrl.updateCoaContent(session, coaContent);
      log.debug("wbtServices", "ContentServices - updateContent: '" + coaContent.getName() + "' has been updated.");

    } catch (AccessNotGrantedException e) {
      rtCode = -1;
      log.debug("wbtServices", "ContentServices - updateContent: AccessNotGrantedException - Content '" + coaContent.getName() + "' is not accessable.");
    } catch (ObjectExistsException e) {
      rtCode = -2;
      log.debug("wbtServices", "ContentServices - updateContent: ObjectExistsException - Content '" + coaContent.getName() + "' does not exist.");
    } catch (ObjectNotFoundException e) {
      rtCode = -1;
      log.debug("wbtServices", "ContentServices - updateContent: ObjectNotFoundException - Content '" + coaContent.getName() + "' could not be found.");
    } catch (LockNotGrantedException e) {
      rtCode = -1;
      log.debug("wbtServices", "ContentServices - updateContent: LockNotGrantedException - Content '" + coaContent.getName() + "' is locked.");
    } finally {
      // unlock content
      ctntCtrl.unlockCoaContent(session, lock);
    }

    return rtCode;
  }

  /**
   * Deletes a CoaContent object in DS repository.
   * 
   * @param session CoaSession of iolet
   * @param contentProject Project name
   * @param contentPath Full path to content group e.g. aaa/bbb/ (must end with an "/")
   * @param contentName Name of the content e.g. aaa.xml
   * @return int Return code (0 = ok, smaller than 0 = exception)
   */
  public int deleteContent(CoaSession session, String contentProject, String contentPath, String contentName) {

    // return code of this method
    int rtCode = 0;

    // get content controller
    CoaContentController ctntCtrl = CoaContentControllerFactory.getCoaContentController();

    // lock and delete content
    CoaLock lock = null;
    try {
      // Read the content which should be deleted
      CoaContent coaContent = ctntCtrl.readCoaContent(session, contentProject, contentPath + contentName, CoaContent.STATUS_FINAL_PRIORITY);

      // Lock the content
      lock = ctntCtrl.lockCoaContent(session, coaContent);

      // delete content
      ctntCtrl.deleteCoaContent(session, contentProject, contentPath + contentName);

      log.debug("wbtServices", "ContentServices - deleteContent: '" + contentPath + contentName + "' has been deleted.");

    } catch (AccessNotGrantedException e) {
      rtCode = -1;
      log.debug("wbtServices", "ContentServices - deleteContent: AccessNotGrantedException - Content '" + contentPath + contentName + "' is not accessable.");
    } catch (LockNotGrantedException e) {
      rtCode = -2;
      log.debug("wbtServices", "ContentServices - deleteContent: LockNotGrantedException - Content '" + contentPath + contentName + "' is locked.");
    } finally {
      // finally unlock the content
      ctntCtrl.unlockCoaContent(session, lock);
    }

    return rtCode;
  }

  /**
   * Deletes a CoaContentGroup object in DS repository.
   * 
   * @param session CoaSession of iolet
   * @param project Project name
   * @param groupPath Full path to content group e.g. aaa/bbb
   * @return int Return code (0 = ok, smaller than 0 = exception)
   */
  public int deleteGroup(CoaSession session, String project, String groupPath) {

    // return code of this method
    int rtCode = 0;

    // get content controller
    CoaContentGroupController grpCtrl = CoaContentControllerFactory.getCoaContentGroupController();

    // lock and delete content
    CoaLock lock = null;
    try {
      // Read the content which should be deleted
      CoaContentGroup coaContentGroup = grpCtrl.readCoaContentGroup(session, project, groupPath);

      // Lock the content
      lock = grpCtrl.lockCoaContentGroup(session, coaContentGroup);

      // delete content
      grpCtrl.deleteCoaContentGroup(session, project, groupPath);

      log.debug("wbtServices", "ContentServices - deleteGroup: '" + groupPath + "' has been deleted.");

    } catch (AccessNotGrantedException e) {
      rtCode = -1;
      log.debug("wbtServices", "ContentServices - deleteGroup: AccessNotGrantedException - Content '" + groupPath + "' is not accessable.");
    } catch (LockNotGrantedException e) {
      rtCode = -1;
      log.debug("wbtServices", "ContentServices - deleteGroup: LockNotGrantedException - Content '" + groupPath + "' is locked.");
    } finally {
      // finally unlock the content
      grpCtrl.unlockCoaContentGroup(session, lock);
    }

    return rtCode;
  }

  /**
   * Check existence of a content in the DS repository.
   * 
   * @param session CoaSession of the iolet
   * @param contentProject Project name
   * @param contentPath Full path to content group e.g. aaa/bbb/ (must end with an "/")
   * @param contentName Name of the content e.g. aaa.xml
   * @return boolean true, if content exists in repository
   */
  public boolean existsContent(CoaSession session, String contentProject, String contentPath, String contentName) {

    // update content
    CoaContentController ctntCtrl = CoaContentControllerFactory.getCoaContentController();

    // check existence
    boolean exists = false;
    try {
      exists = ctntCtrl.coaContentExists(session, contentProject, contentPath + contentName, CoaContent.STATUS_FINAL_PRIORITY);
      if (exists)
        log.debug("wbtServices", "ContentServices - existsContent: Content '" + contentPath + contentName + "' exists.");
    } catch (AccessNotGrantedException e) {
      log.debug("wbtServices", "ContentServices - existsContent: AccessNotGrantedException - Existence of Content '" + contentPath + contentName + "' cannot be checked.");
    }

    return exists;

  }

  /**
   * Get coaContent object from DS repository.
   * 
   * @param session CoaSession of the iolet
   * @param contentProject Project name
   * @param contentPath Full path to content group e.g. aaa/bbb/ (must end with an "/")
   * @param contentName Name of the content e.g. aaa.xml
   * @return CoaContent CoaContent object or null, if not found.
   */
  public CoaContent getContent(CoaSession session, String contentProject, String contentPath, String contentName) {

    // update content
    CoaContentController ctntCtrl = CoaContentControllerFactory.getCoaContentController();

    // check existence
    CoaContent ctnt = null;
    try {
      ctnt = ctntCtrl.readCoaContent(session, contentProject, contentPath + contentName, CoaContent.STATUS_FINAL_PRIORITY);
      if (ctnt == null) {
        log.debug("wbtServices", "ContentServices - getContent: Content '" + contentPath + contentName + "' cannot be read.");
      }
    } catch (AccessNotGrantedException e) {
      log.debug("wbtServices", "ContentServices - getContent: AccessNotGrantedException - Existence of Content '" + contentPath + contentName + "' cannot be checked.");
    }

    return ctnt;

  }

  /**
   * Create a coaContent object in the DS repository.
   * 
   * @param session CoaSession of iolet
   * @param contentProject Project name
   * @param contentPath Full path to content group e.g. aaa/bbb/ (must end with an "/")
   * @param contentName Name of the content e.g. aaa.xml
   * @param locale Locale which defines the locale variant to be updated.
   * @param type CoaContent type e.g. XML (use type constants of CoaContent)
   * @param contentData Updated CoaContentData object of the given locale,
   * @param attributes Hashtable with those attributes (key/value pairs) which should be written to the CoaContent object. There is no delete of attributes.
   * @param multiValueSeparator Value separator string, which separates multiple values of an attribute
   * @return int Return code (0 = ok, smaller than 0 = exception)
   */
  public int createContent(CoaSession session, String contentProject, String contentPath, String contentName, Locale locale, int type, String contentData, Hashtable<String, String> attributes,
      String multiValueSeparator) {

    // return code of this method
    int rtCode = 0;

    // update content
    CoaContentController ctntCtrl = CoaContentControllerFactory.getCoaContentController();
    CoaContent coaContent = null;
    try {
      coaContent = ctntCtrl.createCoaContent(session, contentProject, contentPath + contentName, type);
      log.debug("wbtServices", "ContentServices - createContent: '" + coaContent.getName() + "' has been created.");
    } catch (AccessNotGrantedException e) {
      rtCode = -1;
      log.debug("wbtServices", "ContentServices - createContent: AccessNotGrantedException - Content '" + contentPath + contentName + "' is not creatable.");
      return rtCode;
    }

    // coaContent was created and can be changed know
    coaContent.setName(contentName);
    coaContent.setDescription(contentName);

    // write attributes
    if (attributes != null) {
      Enumeration<String> keys = attributes.keys();
      while(keys.hasMoreElements()) {
        String key = keys.nextElement();
        String value = attributes.get(key);
        if (value.contains(multiValueSeparator)) {
          String mvList[] = value.split("\\" + multiValueSeparator);
          log.debug("wbtServices", "ContentServices - createContent: multi value list for ga relationship - length: " + mvList.length + " value: " + mvList[0] + "/" + mvList[1]);
          coaContent.setAttribute(key, locale, mvList);
        } else {
          coaContent.setAttribute(key, locale, attributes.get(key));
        }
      }
    }

    // write content data
    boolean createIfNotExists = true;
    CoaContentData newData = coaContent.getCoaContentData(locale, CoaContent.LOCALE_POLICY_ANY, createIfNotExists);
    newData.setText(contentData);

    // set timestamp
    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    Date date = new Date();
    coaContent.setAttribute("lastUpdated", locale, dateFormat.format(date));

    // set status to final
    coaContent.setStatus(CoaContent.STATUS_FINAL);

    // insert content to repository
    boolean overwriteExisting = false;
    boolean createGroups = true;
    try {
      ctntCtrl.insertCoaContent(session, contentProject, coaContent, overwriteExisting, createGroups);
    } catch (AccessNotGrantedException e) {
      rtCode = -1;
      log.debug("wbtServices", "ContentServices - createContent: AccessNotGrantedException - Content '" + coaContent.getName() + "' is not accessable.");
    } catch (ObjectExistsException e) {
      rtCode = -2;
      log.debug("wbtServices", "ContentServices - createContent: ObjectExistsException - Content '" + coaContent.getName() + "' exists.");
    } catch (ObjectNotFoundException e) {
      rtCode = -3;
      log.debug("wbtServices", "ContentServices - createContent: ObjectNotFoundException - Content '" + coaContent.getName() + "' could not be found.");
    } catch (LockNotGrantedException e) {
      rtCode = -4;
      log.debug("wbtServices", "ContentServices - createContent: LockNotGrantedException - Content '" + coaContent.getName() + "' is locked.");
    }

    return rtCode;

  }

  /**
   * Read content attributes beginning from a given root attribute and return them as a hashtable.
   * 
   * @param session CoaSession from iolet
   * @param attrRootPath Path of the attribute which is the root element of the wished attribute structure
   * @param project Project name
   * @param fileName Name of the content in DS repository
   * @return Hashtable Key/value list of attributes starting from root attribute path
   */
  public static Hashtable<String, String> getContentAttributesAsHashtable(CoaSession session, String attrRootPath, String project, String fileName) {

    Hashtable<String, String> attrValues = new Hashtable<String, String>();
    CoaContent content = null;

    // get a CoaContenController
    CoaContentController contentController = CoaContentControllerFactory.getCoaContentController();

    // get registry.xml content
    try {
      if (contentController.coaContentExists(session, project, fileName, CoaContent.STATUS_FINAL_ONLY)) {
        content = contentController.readCoaContent(session, project, fileName, CoaContent.STATUS_FINAL_ONLY);
      } else {
        log.debug("wbtServices", "Content '" + project + "/" + fileName + "' not found.");
      }
    } catch (AccessNotGrantedException e) {
      log.debug("wbtServices", "AccessNotGrantedException for Content '" + project + "/" + fileName + "'.");
    }

    if (content != null) {
      @SuppressWarnings("unchecked")
      Iterator<String> attrIter = content.getAttributePaths(attrRootPath, true);
      while(attrIter.hasNext()) {
        String attrPath = attrIter.next();
        String attrValueDe = content.getAttributeValue(attrRootPath + "." + attrPath, Locale.GERMAN);
        if (attrValueDe != null) {
          attrValues.put(attrRootPath + "." + attrPath + ".de", attrValueDe);
          // log.debug("wbtServices", "Attribute '" + attrRootPath + "." + attrPath + "' (de) has value: '" + attrValueDe + "'");
        } else {
          // log.debug("wbtServices", "Attribute '" + attrRootPath + "." + attrPath + "' (de) is not defined or empty.");
        }
        String attrValueFr = content.getAttributeValue(attrRootPath + "." + attrPath, Locale.FRENCH);
        if (attrValueFr != null) {
          attrValues.put(attrRootPath + "." + attrPath + ".fr", attrValueFr);
          // log.debug("wbtServices", "Attribute '" + attrRootPath + "." + attrPath + "' (fr) has value: '" + attrValueFr + "'");
        } else {
          // log.debug("wbtServices", "Attribute '" + attrRootPath + "." + attrPath + "' (fr) is not defined or empty.");
        }
        String attrValueIt = content.getAttributeValue(attrRootPath + "." + attrPath, Locale.ITALIAN);
        if (attrValueIt != null) {
          attrValues.put(attrRootPath + "." + attrPath + ".it", attrValueIt);
          // log.debug("wbtServices", "Attribute '" + attrRootPath + "." + attrPath + "' (it) has value: '" + attrValueIt + "'");
        } else {
          // log.debug("wbtServices", "Attribute '" + attrRootPath + "." + attrPath + "' (it) is not defined or empty.");
        }
      }
    }
    return attrValues;
  }

  /**
   * Read content attributes beginning from a given root attribute and return them as a hashtable.
   * 
   * @param session CoaSession from iolet
   * @param attrRootPath Path of the attribute which is the root element of the wished attribute structure
   * @param project Project name
   * @param fileName Name of the content in DS repository
   * @param locale Locale to be read
   * @return Hashtable Key/value list of attributes starting from root attribute path
   */
  public static Hashtable<String, String> getContentAttributesAsHashtable(CoaSession session, String attrRootPath, String project, String fileName, Locale locale) {

    Hashtable<String, String> attrValues = new Hashtable<String, String>();
    CoaContent content = null;

    // get a CoaContenController
    CoaContentController contentController = CoaContentControllerFactory.getCoaContentController();

    // get registry.xml content
    try {
      if (contentController.coaContentExists(session, project, fileName, CoaContent.STATUS_FINAL_ONLY)) {
        content = contentController.readCoaContent(session, project, fileName, CoaContent.STATUS_FINAL_ONLY);
      } else {
        log.debug("wbtServices", "ContentServices - getContentAttributesAsHashtable: Content '" + project + "/" + fileName + "' not found.");
      }
    } catch (AccessNotGrantedException e) {
      log.debug("wbtServices", "ContentServices - getContentAttributesAsHashtable:AccessNotGrantedException for Content '" + project + "/" + fileName + "'.");
    }

    // BHO: added multi-value capabilities 4.1.2013
    if (content != null) {
      @SuppressWarnings("unchecked")
      Iterator<String> attrIter = content.getAttributePaths(attrRootPath, true);
      while(attrIter.hasNext()) {
        String attrPath = attrIter.next();
        String[] attrValueList = content.getAttributeValues(attrRootPath + "." + attrPath, locale);
        if (attrValueList != null && attrValueList.length > 1) {
          String sepList = "";
          for (int i = 0; i < attrValueList.length; i++) {
            sepList = sepList + attrValueList[i] + ";";
          }
          attrValues.put(attrRootPath + "." + attrPath, sepList.substring(0, sepList.length() - 1));
          log.debug("wbtServices", "ContentServices - getContentAttributesAsHashtable: found multi-value attribute '" + attrPath + "' with values '" + sepList.substring(0, sepList.length() - 1) + "'");
        } else {
          String attrValue = content.getAttributeValue(attrRootPath + "." + attrPath, locale);
          if (attrValue == null)
            attrValue = "";
          attrValues.put(attrRootPath + "." + attrPath, attrValue);
          log.debug("wbtServices", "ContentServices - getContentAttributesAsHashtable: found single-value attribute '" + attrPath + "' with values '" + attrValue + "'");
        }
      }
    }
    return attrValues;
  }

}
