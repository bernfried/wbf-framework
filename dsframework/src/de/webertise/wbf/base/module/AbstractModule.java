/*
 * AbstractModule.java
 *
 * Created on 31. Januar 2003, 18:30
 */

package de.webertise.wbf.base.module;

import de.reddot.api.common.session.CoaSession;
import de.reddot.api.web.io.WebletRequest;
import de.reddot.api.web.io.WebletResponse;
import de.webertise.wbf.base.action.ActionResponse;
import de.webertise.wbf.base.application.ApplicationSetting;
import de.webertise.wbf.base.webmodule.WebModuleSetting;
import de.webertise.wbf.weblet.RequestControllerData;

/**
 * 
 * @author bernfried.howe
 */
public abstract class AbstractModule {

  /** Creates a new instance of AbstractModule */
  public AbstractModule() {
  }

  public abstract ActionResponse handleRequest(WebletRequest request, WebletResponse response, CoaSession session, WebModuleSetting webModuleSetting, ApplicationSetting applicationSetting,
      RequestControllerData reqCtrlData);

}
