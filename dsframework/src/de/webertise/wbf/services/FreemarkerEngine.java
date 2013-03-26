package de.webertise.wbf.services;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import de.webertise.ds.services.LogServices;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class FreemarkerEngine {

  private final static String      LOG_CATEGORY = "wbf_generator";
  private final static LogServices log          = LogServices.getInstance(LOG_CATEGORY);

  public static final String executeTemplate(String fmTemplate, Map<String, String> data) {

    // get a string template loader
    StringTemplateLoader stringLoader = new StringTemplateLoader();

    // get the template and put it to the stringLoader
    stringLoader.putTemplate("template", fmTemplate);

    // get a template configuration
    Configuration cfg = new Configuration();
    cfg.setTemplateLoader(stringLoader);

    // get template
    Template template = null;
    try {
      template = cfg.getTemplate("template");
    } catch (IOException e) {
      log.debug(LOG_CATEGORY, "FreemarkerEngine - executeTemplate: ERROR - getTemplate failed (IOException).");
      log.trace(LOG_CATEGORY, "FreemarkerEngine - executeTemplate: Exception = " + e.getMessage());
    }

    // process template
    StringWriter out = new StringWriter();

    try {
      template.process(data, out);
    } catch (TemplateException e) {
      log.debug(LOG_CATEGORY, "FreemarkerEngine - executeTemplate: ERROR - process failed (TemplateException).");
      log.trace(LOG_CATEGORY, "FreemarkerEngine - executeTemplate: Exception = " + e.getMessage());
    } catch (IOException e) {
      log.debug(LOG_CATEGORY, "FreemarkerEngine - executeTemplate: ERROR - process failed (IOException).");
      log.trace(LOG_CATEGORY, "FreemarkerEngine - executeTemplate: Exception = " + e.getMessage());
    }

    return out.toString();
  }
}
