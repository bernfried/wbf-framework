package de.webertise.ds.services;

import java.util.Enumeration;
import java.util.Hashtable;

import de.reddot.api.app.io.IoletRequest;
import de.webertise.ds.manager.profilemgr.config.PMOInputParameter;
import de.webertise.ds.manager.profilemgr.config.PMOOperation;
import de.webertise.ds.manager.profilemgr.config.PMOValidation;
import de.webertise.ds.manager.profilemgr.config.ProfileManagerObject;
import de.webertise.ds.manager.profilemgr.service.PMOValidationResult;
import de.webertise.ds.manager.profilemgr.service.PMOValidationResultItem;
import de.webertise.ds.utils.xssfilter.HTMLInputFilter;

public class ValidationService {
	
	// declare log services
	private static LogServices log = LogServices.getInstance("wbtServices");
	

	public static StringBuffer createResultInvalidXml(IoletRequest request, PMOValidationResult valResult, String code) {
		StringBuffer sbResult = new StringBuffer();
		
		sbResult.append("<validation-result code='" + code + "'>");
		
		// iterate through all parameters and return error codes
		Enumeration<String> resItemsKeys = valResult.getValResultItems().keys();
		while (resItemsKeys.hasMoreElements()) {
			String key = resItemsKeys.nextElement();
			PMOValidationResultItem resItem = valResult.getValResultItems().get(key);
			sbResult.append("<validated-parameter name='" + resItem.getName() + "'>");
			sbResult.append("<valid>" + resItem.isValid() + "</valid>");
			sbResult.append("<orig-value><![CDATA[" + resItem.getOrigValue() + "]]></orig-value>");
			sbResult.append("<validated-value><![CDATA[" + resItem.getValidatedValue() + "]]></validated-value>");
			sbResult.append("<mandatory-failed>" + resItem.isMandatoryFailed() + "</mandatory-failed>");
			sbResult.append("<minlength-failed>" + resItem.isMinLengthFailed() + "</minlength-failed>");
			sbResult.append("<maxlength-failed>" + resItem.isMaxLengthFailed() + "</maxlength-failed>");
			sbResult.append("<regex-failed>" + resItem.isRegExFailed() + "</regex-failed>");
			sbResult.append("<boolean-failed>" + resItem.isBooleanCheckFailed() + "</boolean-failed>");
			sbResult.append("<number-failed>" + resItem.isNumberCheckFailed() + "</number-failed>");
			sbResult.append("<date-failed>" + resItem.isDateCheckFailed() + "</date-failed>");
			sbResult.append("</validated-parameter>");
		}
		sbResult.append("</validation-result>");
		
		return sbResult;
	}

	public static PMOValidationResult validateInputParameters(IoletRequest request, ProfileManagerObject pmo, PMOOperation op) {
		
		// get input-parameter list from operation
		Hashtable<String,PMOInputParameter> inputParams = op.getPmoInputParameter();
		
		// create validation result list
		PMOValidationResult valResult = new PMOValidationResult();
		
		// check if input parameters are defined
		if (!inputParams.isEmpty()) {
			
			// get validation rules of pmo object
			Hashtable<String,PMOValidation> validations = pmo.getPmoValidations();
		
			// iterate through all input parameters and validate them
			Enumeration<String> ipKeys = inputParams.keys();
			while (ipKeys.hasMoreElements()) {
				String ipKey = ipKeys.nextElement();
				PMOInputParameter inputParam = inputParams.get(ipKey);
				String paramValue = (String) request.getParameter(inputParam.getName());
				if (paramValue == null) paramValue = "";
				// avoid xss attacks by replacing certain digits
				paramValue = HTMLInputFilter.filter( paramValue );
				log.debug("wbtServices", "ValidationService - validateInputParameters: parameter value for '" + inputParam.getName() + "' is '" + paramValue + "'.");

				PMOValidation validation = null;
				PMOValidationResultItem valResItem = null;
				
				if (validations != null && validations.get(ipKey)!=null && (inputParam.isMandatory() || (!inputParam.isMandatory() && !paramValue.equals("")))) {
					log.debug("wbtServices", "ValidationService - validateInputParameters: validation/formatting is initiated.");
					// validate against validation object
					validation = validations.get(ipKey);
				} else {
					// create simple validation object
					validation = new PMOValidation();
					validation.setMaxLength(0);
					validation.setMaxLength(0);
					validation.setName(ipKey);
					validation.setRegEx("");
				}
				
				// validate input parameter
				valResItem = validation.validate(paramValue, inputParam);
				valResItem.setPersistent(inputParam.isPersistent());

				// add to validated parameters
				valResult.addValResultItem(valResItem);			
				
			}
		} else {
			log.debug("wbtServices", "ValidationService - validateInputParameters: no <input-parameter> elements found for operation '" + op.getName() + "'.");
		}
		
		return valResult;
	}

	
	
}
