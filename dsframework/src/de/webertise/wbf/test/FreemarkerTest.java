package de.webertise.wbf.test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class FreemarkerTest {

  /**
   * @param args
   */
  public static void main(String[] args) {

    // get a string template loader
    StringTemplateLoader stringLoader = new StringTemplateLoader();

    // get the template and put it to the stringLoader
    String freemarkerTemplate = "FreeMarker Template example: ${message} ";
    stringLoader.putTemplate("myFirstTemplate", freemarkerTemplate);

    // get a template configuration
    Configuration cfg = new Configuration();
    cfg.setTemplateLoader(stringLoader);

    // get template
    Template template = null;
    try {
      template = cfg.getTemplate("myFirstTemplate");
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    // process template
    StringWriter out = new StringWriter();
    Map<String, Object> data = new HashMap<String, Object>();
    data.put("message", "Bernfried");

    try {
      template.process(data, out);
    } catch (TemplateException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    System.out.print(out);

  }

}
