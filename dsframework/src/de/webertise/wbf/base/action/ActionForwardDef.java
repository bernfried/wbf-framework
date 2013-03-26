/*
 * ActionForward.java
 *
 * Created on 18. März 2003, 21:36
 */

package de.webertise.wbf.base.action;

/**
 *
 * @author  bernfried howe
 */
public class ActionForwardDef implements ActionForwardConstants{
    
    private String name = "";
    private String view = "";   
    private String template = "";
    private String action = "";
    private int type = 1;
    
    /** Creates a new instance of ActionForward */
    public ActionForwardDef() {
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getView() {
        return this.view;
    }
    
    public String getTemplate() {
        return this.template;
    }
    
    public int getType() {
        return this.type;
    }

	public String getAction() {
		return this.action;
	}
    
    public void setName(String name ) {
        this.name = name;
    }
    
    public void setView(String view ) {
        this.view = view;
    }
    
    public void setTemplate(String template ) {
        this.template = template;
    }
    
    public void setType( int type ) {
        this.type = type;
    }

    public void setAction(String action) {
    	this.action = action;
    }
    
    public String getAsXml() {
      StringBuffer sb = new StringBuffer();
      
      sb.append("<actionforward>");
      sb.append("<name>" + this.name + "</name>");
      sb.append("<view>" + this.view + "</view>");
      sb.append("<template>" + this.template + "</template>");
      sb.append("<action>" + this.action + "</action>");
      sb.append("<type>" + this.type + "</type>");
      sb.append("</actionforward>");

      return sb.toString();
    }
}
