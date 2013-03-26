/**
 * 
 */
package de.webertise.ds.manager.profilemgr.config;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;

import de.webertise.ds.manager.profilemgr.service.PMOValidationResultItem;
import de.webertise.ds.services.LogServices;

/**
 * @author bernfried
 *
 */
public class PMOValidation {

	// declare log services
	private LogServices log = LogServices.getInstance("wbtServices");

	// class properties
	private String name;
	private int minLength;
	private int maxLength;
	private String regEx;
	
	public PMOValidation() {}
	
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @return the minLength
	 */
	public int getMinLength() {
		return minLength;
	}
	/**
	 * @return the maxLength
	 */
	public int getMaxLength() {
		return maxLength;
	}
	/**
	 * @return the regEx
	 */
	public String getRegEx() {
		return regEx;
	}
	
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @param minLength the minLength to set
	 */
	public void setMinLength(int minLength) {
		this.minLength = minLength;
	}
	/**
	 * @param maxLength the maxLength to set
	 */
	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}
	/**
	 * @param regEx the regEx to set
	 */
	public void setRegEx(String regEx) {
		this.regEx = regEx;
	}
	
	/**
	 * @param parameter the parameter to validate
	 */
	@SuppressWarnings("deprecation")
	public PMOValidationResultItem validate(String parameter, PMOInputParameter inputParam) {
		
		// init
		PMOValidationResultItem valResItem = new PMOValidationResultItem();
		valResItem.setName(name);
		valResItem.setOrigValue(parameter);
		
		log.debug("wbtServices", "PMOValidation - validate: reached with " + parameter);
		
		// check if parameter was found in request at all
		if ((parameter==null || parameter.equals("")) && inputParam.isMandatory()) {

			//************************************************************
			//* parameter not found, but mandatory
			//************************************************************
			parameter = "";
			valResItem.setValid(false);
			valResItem.setMandatoryFailed(true);
			log.debug("wbtServices", "PMOValidation - validate: mandatory failed");

		} else if ((parameter==null || parameter.equals("")) && inputParam.isMandatory()) {
			
			//************************************************************
			//* parameter not found and is not mandatory. it is assumed
			//* that the standard value is always valid.
			//************************************************************
			parameter = inputParam.getStandardValue(); 
			log.debug("wbtServices", "PMOValidation - validate: value set to standard value '" + inputParam.getStandardValue() + "'.");

		} else {

			//************************************************************
			//* check if standard value is set and parameter is empty
			//************************************************************
			if (inputParam.getStandardValue() != null && !inputParam.getStandardValue().equals("") && parameter.equals("")) {
				// set to standard value
				parameter = inputParam.getStandardValue();
			}
			//************************************************************
			//* check minLength
			//************************************************************
			if (this.minLength > 0 && parameter.length() < this.minLength) {
				valResItem.setValid(false);
				valResItem.setMinLengthFailed(true);
				log.debug("wbtServices", "PMOValidation - validate: minLength failed (Min-Length: " + this.minLength + " / Cur-Length: " + parameter.length() + ")");
			}
			//************************************************************
			//* check maxLength
			//************************************************************
			if (this.maxLength > 0 && parameter.length() > this.maxLength) {
				valResItem.setValid(false);
				valResItem.setMaxLengthFailed(true);
				log.debug("wbtServices", "PMOValidation - validate: maxLength failed (Max-Length: " + this.maxLength + " / Cur-Length: " + parameter.length() + ")");
			}
			//************************************************************
			//* check regEx
			//************************************************************
			if (!this.regEx.equals("")) {
				// check pattern
				Pattern p = Pattern.compile(this.regEx);
				Matcher m = p.matcher(parameter);
				if (!m.find()) {
					// parameter check failed
					valResItem.setValid(false);
					valResItem.setRegExFailed(true);
					log.debug("wbtServices", "PMOValidation - validate: regEx failed");
				}
			}
			
			//*********************************************************************
			//* check if value is valid: formatting rules based on parameter type
			//*********************************************************************
			if (inputParam.getType() == ProfileManagerConstants.INPUT_PARAMETER_TYPE_BOOLEAN) {
				parameter = checkBoolean(valResItem, parameter);
			}
			if (inputParam.getType() == ProfileManagerConstants.INPUT_PARAMETER_TYPE_DATE) {
				parameter = checkDate(valResItem, parameter, inputParam);
			}
			if (inputParam.getType() == ProfileManagerConstants.INPUT_PARAMETER_TYPE_NUMBER) {
				parameter = checkNumber(valResItem, parameter, inputParam);
			}
			
			
			//*********************************************************************
			//* check if value needs to be url encoded/decoded
			//*********************************************************************
			if (inputParam.getUrlEncoding().equals(ProfileManagerConstants.INPUT_PARAMETER_URL_ENCODE)) {
				parameter = URLEncoder.encode(parameter);
			} else if (inputParam.getUrlEncoding().equals(ProfileManagerConstants.INPUT_PARAMETER_URL_DECODE)) {
				parameter = URLDecoder.decode(parameter);
			}
			//*********************************************************************
			//* check if value needs to be url encoded/decoded
			//*********************************************************************
			if (inputParam.getHtmlEncoding().equals(ProfileManagerConstants.INPUT_PARAMETER_HTML_ENCODE)) {
				parameter = StringEscapeUtils.escapeHtml(parameter);
			} else if (inputParam.getHtmlEncoding().equals(ProfileManagerConstants.INPUT_PARAMETER_HTML_DECODE)) {
				parameter = StringEscapeUtils.unescapeHtml(parameter);
			}
			
			//************************************************************
			// set validated and formated value
			//************************************************************
			valResItem.setValidatedValue(parameter);
		}
				
		return valResItem;
	}


	private String checkNumber(PMOValidationResultItem valResItem, String parameter, PMOInputParameter inputParam) {
		
		try {
			Integer.parseInt(parameter);
		} catch (NumberFormatException e) {
			valResItem.setValid(false);
			valResItem.setNumberCheckFailed(true);
			parameter = "0";
		}
		return parameter;
	}


	private String checkDate(PMOValidationResultItem valResItem, String parameter, PMOInputParameter inputParam) {
		
		// check date 
		if (inputParam.getInputFormat()!=null) {
			
			// build date based in input format
		    DateFormat inputFormat = new SimpleDateFormat(inputParam.getInputFormat());
		    Date inputDate = null;
		    try {
		    	inputDate = inputFormat.parse(parameter);
		    	log.debug("wbtServices", "PMOValidation - checkDate: inputDate based on date '" + parameter + "' : '" + inputDate.toString() + "'");
			    DateFormat persistFormat = new SimpleDateFormat(inputParam.getPersistFormat());
		    	parameter = persistFormat.format(inputDate);
		    	log.debug("wbtServices", "PMOValidation - checkDate: persistDate based on date '" + inputDate.toString() + "' : '" + parameter + "'");
		    } catch (ParseException e) {
		    	valResItem.setValid(false);
		    	valResItem.setDateCheckFailed(true);
		    	log.debug("wbtServices", "PMOValidation - checkDate: inputDate based on parameter '" + parameter + "' with input-format '" + inputParam.getInputFormat() + "' caused parse exception.'");
		    }
		}
		return parameter;
	}


	private String checkBoolean(PMOValidationResultItem valResItem, String parameter) {
		
		if (!parameter.equals("true") && !parameter.equals("false")) {
			valResItem.setValid(false);
			valResItem.setBooleanCheckFailed(true);
			parameter = "false";
		}
		return parameter;
	}
	
}
