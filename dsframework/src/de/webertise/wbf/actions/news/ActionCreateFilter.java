package de.webertise.wbf.actions.news;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.reddot.api.common.session.CoaSession;
import de.reddot.api.web.io.WebletRequest;
import de.webertise.wbf.base.action.ActionResponseItem;

/**
 * 
 * @author bernfried howe
 */
public class ActionCreateFilter extends de.webertise.wbf.base.action.AbstractAction {

  /** Creates a new instance of ActionCreateFilter */
  public ActionCreateFilter() {
  }

  public boolean execute(CoaSession session, WebletRequest request) {

    log.debug(LOG_CATEGORY, "ActionCreateFilter - execute: Reached.");

    // result string buffer
    StringBuffer result = new StringBuffer();
    boolean isSelCategoryAll = false;

    // ***********************************************************************
    // get tags (and/or) flag if action is set to
    // ***********************************************************************
    String action = request.getParameter("action");
    if (action != null && action.equals("setTagsAndOrFlag")) {
      String tagsOp = request.getParameter("tagsOp");
      if (tagsOp != null) {
        session.setAttribute("community.selTagsAndOrFlag", tagsOp);
      }
    }
    String selTagsAndOrFlag = (String) session.getAttribute("community.selTagsAndOrFlag");
    if (selTagsAndOrFlag == null || selTagsAndOrFlag.equals("")) {
      session.setAttribute("community.selTagsAndOrFlag", "and");
    }

    // ***********************************************************************
    // check, if init is called
    // ***********************************************************************
    if (action != null && action.equals("init")) {
      session.setAttribute("community.selCategory", "all");
      session.setAttribute("community.selFilterCategories", "");
      session.setAttribute("community.selTags", "");
      session.setAttribute("community.selTagsSemiSep", "");
      session.setAttribute("community.selTagsAndOrFlag", "or");
    }

    // ***********************************************************************
    // get category
    // ***********************************************************************
    String category = request.getParameter("ctg");
    String selCategory = (String) session.getAttribute("community.selCategory");
    if (category != null) {
      result.append("(content.category EQ '" + category + "')");
      session.setAttribute("community.selCategory", category);
      if (category.equals("all"))
        isSelCategoryAll = true;
    } else {
      if (selCategory != null && !selCategory.equals("")) {
        result.append("(content.category EQ '" + selCategory + "')");
        if (selCategory.equals("all"))
          isSelCategoryAll = true;
      } else {
        result.append("(content.category EQ 'all')");
        session.setAttribute("community.selCategory", "all");
        isSelCategoryAll = true;
      }
    }

    // ***********************************************************************
    // check if selCategory is "all" - if yes, check selFilterCategories
    // ***********************************************************************
    if (isSelCategoryAll) {
      // get filtered category and add/remove from selFilteredCategories
      String filterCategory = request.getParameter("fctg");
      String selFilterCategories = (String) session.getAttribute("community.selFilterCategories");
      if (selFilterCategories == null || selFilterCategories.equals("")) {
        if (filterCategory != null && !filterCategory.equals("")) {
          selFilterCategories = "[" + filterCategory + "]";
          session.setAttribute("community.selFilterCategories", selFilterCategories);
          result.append(" AND (content.category EQ '" + filterCategory + "')");
        }
      } else {
        if (filterCategory != null && !filterCategory.equals("")) {
          if (selFilterCategories.contains("[" + filterCategory + "]")) {
            selFilterCategories = selFilterCategories.replace("[" + filterCategory + "]", "");
          } else {
            selFilterCategories += "[" + filterCategory + "]";
          }
          session.setAttribute("community.selFilterCategories", selFilterCategories);
          if (!selFilterCategories.equals(""))
            result.append(getConstraint(selFilterCategories, "category"));
        } else {
          result.append(getConstraint(selFilterCategories, "category"));
        }
      }
    } else {
      session.setAttribute("community.selFilterCategories", "");
    }

    // ***********************************************************************
    // get tag and add or remove from selTags
    // ***********************************************************************
    String tag = request.getParameter("tag");
    String selTags = (String) session.getAttribute("community.selTags");
    log.debug(LOG_CATEGORY, "ActionCreateFilter - execute: tag='" + tag + "' / selTags='" + selTags + "'");
    if (selTags == null || selTags.equals("")) {
      if (tag != null && !tag.equals("")) {
        selTags = "[" + tag + "]";
        session.setAttribute("community.selTags", selTags);
        session.setAttribute("community.selTagsSemiSep", selTags.replace("][", ";").replace("[", "").replace("]", ""));
        // result.append(" AND (content.tag EQ '" + tag + "')");
      }
    } else {
      if (tag != null && !tag.equals("")) {
        if (selTags.contains("[" + tag + "]")) {
          selTags = selTags.replace("[" + tag + "]", "");
          log.debug(LOG_CATEGORY, "ActionCreateFilter - execute: removed tag='" + tag + "' from selTags='" + selTags + "'");
        } else {
          selTags += "[" + tag + "]";
        }
        session.setAttribute("community.selTags", selTags);
        session.setAttribute("community.selTagsSemiSep", selTags.replace("][", ";").replace("[", "").replace("]", ""));
        // if (!selTags.equals("")) result.append(getConstraint(selTags, "tag"));
      } else {
        // result.append(getConstraint(selTags, "tag"));
      }
    }

    // return constraint
    this.setResponseParameter(ActionResponseItem.TARGET_SESSION_TRANSIENT, "constraint", result.toString(), true, false);
    this.setActionForwardName("ok");
    return true;
  }

  public boolean validate(CoaSession session, WebletRequest request) {
    log.debug(LOG_CATEGORY, "ActionCreateFilter - validate: Reached.");
    return true;
  }

  private String getConstraint(String selection, String attrName) {
    StringBuffer constraint = new StringBuffer();
    String regex = "\\[(.*?)\\]";
    Pattern p = Pattern.compile(regex);
    Matcher m = p.matcher(selection);
    while(m.find()) {
      constraint.append(" AND (content." + attrName + " EQ '" + m.group(1) + "')");
    }
    return constraint.toString();
  }

}
