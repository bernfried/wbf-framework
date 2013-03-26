package de.webertise.wbf.admin.generator;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.StringTokenizer;

import de.webertise.ds.services.LogServices;
import de.webertise.wbf.base.action.ActionForwardDef;

public class TemplateDefinition {

  protected final static String                    LOG_CATEGORY = "wbf_generator";
  protected LogServices                            log          = LogServices.getInstance(LOG_CATEGORY);

  private String                                   controllerName;
  private Map<String, TemplateDefinitionAttribute> attributes;
  private Map<String, TemplateDefinitionAction>    actions;

  // ****************************************
  // constructor
  // ****************************************
  public TemplateDefinition() {
    this.setAttributes(new Hashtable<String, TemplateDefinitionAttribute>());
    this.setActions(new Hashtable<String, TemplateDefinitionAction>());
  }

  // ****************************************
  // methods
  // ****************************************
  public void addAttribute(TemplateDefinitionAttribute tdAttr) {
    this.attributes.put(tdAttr.getName(), tdAttr);
  }

  public void addAttribute(String path, String value) {
    if (path.startsWith("root.attributes")) {
      extractAttributes(path, value);
    } else if (path.startsWith("root.actions")) {
      extractActions(path, value);
    }
  }

  private void extractActions(String path, String value) {

    // following paths are ignored
    // root
    // root.actions
    
    // these paths are considered
    // root.actions.<action>                                (<= action name)
    // root.actions.<action>.packagePath                    (<= action property)
    // root.actions.<action>.attributes                     (<= action property)
    // root.actions.<action>.forwards                       (<= ignored)
    // root.actions.<action>.forwards.<fwname>              (<= forward name)
    // root.actions.<action>.forwards.<fwname>.<propname>   (<= forward property

    log.trace(LOG_CATEGORY, "TemplateDefinition - extractActions: ***************************** begin *********************************");

    boolean isAction = false;
    boolean isActionProp = false;
    boolean isForward = false;
    boolean isForwardProp = false;
    boolean relevant = false;
    String actionName = "";
    String actionPropName = "";
    String forwardName = "";
    String forwardPropName = "";

    StringTokenizer st = new StringTokenizer(path, ".");
    ArrayList<String> tokens = new ArrayList<String>();
    while (st.hasMoreTokens()) {
      tokens.add(st.nextToken());
    }

    int amountTokens = tokens.size();
    log.trace(LOG_CATEGORY, "TemplateDefinition - extractActions: amount tokens '" + amountTokens + "' for path: '" + path + "' and value '" + value + "'");

    // only consider path strings with 3 or more tokens 
    if (amountTokens >= 3) {
      actionName = tokens.get(2); // get action name
      
      // get property or sub area like forwards
      if (amountTokens == 3) {
        relevant=true;
        isAction = true;
      
      } else if (amountTokens == 4) {
        actionPropName = tokens.get(3);
        if (!actionPropName.equals("forwards")) {
          relevant=true;
          isActionProp = true;
        }
      
      } else if (amountTokens == 5) {
        forwardName = tokens.get(4);
        relevant=true;
        isForward = true;

      } else if (amountTokens == 6) {
          forwardName = tokens.get(4);
          forwardPropName = tokens.get(5);
          relevant=true;
          isForwardProp = true;
      }
    }
    
    if (relevant) {
      
      // create object if not yet done
      TemplateDefinitionAction tda = new TemplateDefinitionAction();
      if (!actionName.equals("") && this.actions.containsKey(actionName)) {
        tda = this.actions.get(actionName);
        log.trace(LOG_CATEGORY, "TemplateDefinition - extractActions: used existing action of Template Definition for action: '" + actionName + "'");
      } else {
        tda = new TemplateDefinitionAction();
        log.trace(LOG_CATEGORY, "TemplateDefinition - extractActions: added new action to Template Definition for action: '" + actionName + "'");
      }

      //******************************************
      //* handle forwards
      //******************************************
      if (isForward || isForwardProp) {

        ActionForwardDef afDef = null;
        if (tda.getForwards().containsKey(forwardName)) {
          afDef = tda.getForwards().get(forwardName);
          log.trace(LOG_CATEGORY, "TemplateDefinition - extractActions: used existing foward of Template Definition for: '" + forwardName + "'");
        } else {
          afDef = new ActionForwardDef();
          log.trace(LOG_CATEGORY, "TemplateDefinition - extractActions: added new forward to Template Definition for: '" + forwardName + "'");
        }
        afDef.setName(forwardName);

        if (isForwardProp) {
          if (forwardPropName.equals("action")) {
            afDef.setAction(value);
            log.trace(LOG_CATEGORY, "TemplateDefinition - extractActions: setAction with: '" + value + "'");
          } else if (forwardPropName.equals("view")) {
            afDef.setView(value);
            log.trace(LOG_CATEGORY, "TemplateDefinition - extractActions: setView with: '" + value + "'");
          } else if (forwardPropName.equals("type")) {
            try {
              afDef.setType(Integer.parseInt(value));
              log.trace(LOG_CATEGORY, "TemplateDefinition - extractActions: setType with: '" + value + "'");
            } catch (NumberFormatException e) {
              log.trace(LOG_CATEGORY, "TemplateDefinition - extractActions: action forward has wrong type '" + actionName + "' type: '" + value + "' (NumberFormatException)");
            }
          } else if (forwardPropName.equals("template")) {
            afDef.setTemplate(value);
            log.trace(LOG_CATEGORY, "TemplateDefinition - extractActions: setTemplate with: '" + value + "'");
          }
        }
        tda.addForword(forwardName, afDef);

        // handle action properties
      } else if (isActionProp) {
        if (actionPropName.equals("packageName")) {
          tda.setPackageName(value);
          log.trace(LOG_CATEGORY, "TemplateDefinition - extractActions: setPackageName with: '" + value + "'");
        } else if (actionPropName.equals("group")) {
          tda.setGroup(value);
          log.trace(LOG_CATEGORY, "TemplateDefinition - extractActions: setGroup with: '" + value + "'");
        } else if (actionPropName.equals("attributes")) {
          tda.setAttributes(value);
          log.trace(LOG_CATEGORY, "TemplateDefinition - extractActions: setAttributes with: '" + value + "'");
        }
        
        // handle action
      } else if (isAction){
        tda.setKey(actionName);
        tda.setName(value);
      }

      // add to map
      this.actions.put(actionName, tda);
    }
    

    log.trace(LOG_CATEGORY, "TemplateDefinition - extractActions: ***************************** end *********************************");

  }

  private void extractAttributes(String path, String value) {

    log.trace(LOG_CATEGORY, "TemplateDefinition - extractAttributes: ***************************** begin *********************************");
    
    String name = path.replace("root.attributes.", "");
    String prop = "";
    int pos = name.indexOf(".");
    if (pos != -1) {
      prop = name.substring(pos + 1);
      name = name.substring(0, pos);
    }
    log.trace(LOG_CATEGORY, "TemplateDefinition - extractAttributes: property = '" + prop + "' for name '" + name + "'");

    TemplateDefinitionAttribute attr = null;
    if (this.attributes.containsKey(name)) {
      attr = this.attributes.get(name);
      log.trace(LOG_CATEGORY, "TemplateDefinition - extractAttributes: used existing attribute of template defintion for attribute '" + name + "'");
    } else {
      attr = new TemplateDefinitionAttribute();
      log.trace(LOG_CATEGORY, "TemplateDefinition - extractAttributes: added new attribute to template defintion for attribute '" + name + "'");
    }
    // name
    attr.setName(name);
    // mandatory field
    if (prop.equals("mandatory")) {
      if (value.equals("1")) {
        attr.setMandatory(true);
      } else {
        attr.setMandatory(false);
      }
    }
    // showInList field
    if (prop.equals("showInList")) {
      if (value.equals("1")) {
        attr.setShowInList(true);
      } else {
        attr.setShowInList(false);
      }
    }
    // key field
    if (prop.equals("key")) {
      if (value.equals("1")) {
        attr.setKey(true);
      } else {
        attr.setKey(false);
      }
    }
    // minLength field
    if (prop.equals("minLength")) {
      try {
        attr.setMinLength(Integer.parseInt(value));
      } catch (NumberFormatException e) {
        attr.setMinLength(-1);
      }
    }
    // maxLength field
    if (prop.equals("maxLength")) {
      try {
        attr.setMaxLength(Integer.parseInt(value));
      } catch (NumberFormatException e) {
        attr.setMaxLength(-1);
      }
    }
    // pattern
    if (prop.equals("pattern")) {
      attr.setPattern(value);
    }
    // type
    if (prop.equals("type")) {
      try {
        attr.setType(Integer.parseInt(value));
      } catch (NumberFormatException e) {
        attr.setType(1);
      }
    }

    // add to map
    this.attributes.put(name, attr);
    log.trace(LOG_CATEGORY, "TemplateDefinition - extractAttributes: added attribute '" + name + "'  with properties (name,mand,min,max,pattern,type): '" + attr.getName() + "/" + attr.getMandatory()
        + "/" + attr.getMinLength() + "/" + attr.getMaxLength() + "/" + attr.getPattern() + "/" + attr.getType() + "'");

    log.trace(LOG_CATEGORY, "TemplateDefinition - extractAttributes: ***************************** end *********************************");

  }

  // ****************************************
  // getter / setter
  // ****************************************

  public String getControllerName() {
    return controllerName;
  }

  public void setControllerName(String controllerName) {
    this.controllerName = controllerName;
  }

  public Map<String, TemplateDefinitionAttribute> getAttributes() {
    return attributes;
  }

  public void setAttributes(Map<String, TemplateDefinitionAttribute> attributes) {
    this.attributes = attributes;
  }

  public Map<String, TemplateDefinitionAction> getActions() {
    return actions;
  }

  public void setActions(Map<String, TemplateDefinitionAction> actions) {
    this.actions = actions;
  }

}
