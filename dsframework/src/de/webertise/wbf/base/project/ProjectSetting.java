package de.webertise.wbf.base.project;

import java.util.Enumeration;
import java.util.Hashtable;

import de.reddot.api.common.attribute.CoaAttribute;
import de.reddot.api.common.util.Registry;
import de.webertise.ds.services.LogServices;

public class ProjectSetting {

  // constants
  private final static String        REGISTRY_PATH = "reddot.xmaps.projects";
  private final static String        LOG_CATEGORY = "wbf_settings";

  // variables
  private LogServices                log          = LogServices.getInstance(LOG_CATEGORY);

  // properties
  private Hashtable<String, Project> projects     = new Hashtable<String, Project>();

  public ProjectSetting() {
  }

  public boolean readProjectsFromRegistry() {

    log.debug(LOG_CATEGORY, "ProjectSetting - readProjectsFromRegistry: reached");

    Registry registry = Registry.getInstance();
    if (registry != null) {
      CoaAttribute attr = registry.getCoaAttribute(REGISTRY_PATH);
      // if attr could be read
      if (attr != null) {
        // get all child attributes
        log.debug(LOG_CATEGORY,"ProjectSetting - readProjectsFromRegistry: projects found '" + attr.getChildEnumeration().toString() + "'");
        @SuppressWarnings("unchecked")       
        Enumeration<CoaAttribute> enumProjects = attr.getChildEnumeration();
        while (enumProjects.hasMoreElements()) {
          CoaAttribute attrProject = enumProjects.nextElement();
          // get name from path
          String path = attrProject.getPath();
          String pName = path.replace(REGISTRY_PATH+".", "");
          // create project object
          Project p = new Project(pName);
          log.debug(LOG_CATEGORY, "ProjectSetting - readProjectsFromRegistry: found project '" + p.getName() + "'");
          // get locales
          String localePath = REGISTRY_PATH + "." + p.getName() + ".locales";
          CoaAttribute attrLocale = registry.getCoaAttribute(localePath);
          if (attrLocale!=null) {
            p.setLocales(attrLocale.getValues());
            log.debug(LOG_CATEGORY, "ProjectSetting - readProjectsFromRegistry: found locales '" + p.getLocalesAsString() + "'");
          }
          // put project to projects list
          projects.put(p.getName(), p);
        }        
      }

    } else {
      log.debug(LOG_CATEGORY, "ProjectSetting - readProjectsFromRegistry: can't get registry instance");
    }
    
    // check if projects could be read
    if (projects.isEmpty()) {
      return false;
    } else {
      return true;
    }
  }

  public void addProject(Project project) {
    this.projects.put(project.getName(), project);
  }

  public Project getProject(String name) {
    return this.projects.get(name);
  }

  public Hashtable<String, Project> getProjects() {
    return this.projects;
  }

  public Enumeration<String> getProjectNames() {
    return this.projects.keys();
  }

  public String getProjectNames(String separator) {
    String result = "";
    Enumeration<String> enumProjects = this.projects.keys();
    while (enumProjects.hasMoreElements()) {
      result += enumProjects.nextElement() + separator;
    }
    if (!result.equals("")) result = result.substring(0, result.length()-1);
    return result;
  }
  
}
