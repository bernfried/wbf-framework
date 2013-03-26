package de.webertise.ds.services;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.Formatter;
import java.util.Hashtable;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

public class StringServices {
	
	// declare log services
	private static LogServices log = LogServices.getInstance("wbtServices");

	public static Hashtable<String,String> getKeyValuePairs(String input, String pairSeparator, String keyValueSeparator) {
		
		// result Hashtable
		Hashtable<String,String> result = new Hashtable<String,String>();
		
		if (input!=null && !input.equals("")) {
			// check separator strings
			if (keyValueSeparator==null || keyValueSeparator.equals("")) {
				keyValueSeparator = "::";
			}
			if (pairSeparator==null || pairSeparator.equals("")) {
				pairSeparator = "|";
			}
			// tokenize input string
			StringTokenizer pairs = new StringTokenizer(input, pairSeparator);
			// tokenize all key/value pairs
			if (pairs.countTokens() == 0) {
				log.debug("wbtServices", "StringServices - getKeyValuePairs: pairs could not be build for '" + input + "' with separator '" + pairSeparator + "'");
			} else {
				while(pairs.hasMoreTokens()) {
					// there is only one key/value pair; tokenize key/value pair
					String keyValueInput = pairs.nextToken();
					StringTokenizer keyValue = new StringTokenizer(keyValueInput, keyValueSeparator);
					// check if more than one token was found
					if (keyValue.hasMoreTokens() && keyValue.countTokens() == 2) {
						String key = keyValue.nextToken();
						String value = keyValue.nextToken();
						result.put(key, value);
					} else {
						log.debug("wbtServices", "StringServices - getKeyValuePairs: key/value pair could not be build for '" + keyValueInput + "' with separator '" + keyValueSeparator + "'");
					}
				}
			}							
		} else {
			result = null;
		}
		return result;
	}
	
	public static TreeMap<String,String> getKeyValuePairsSorted(String input, String pairSeparator, String keyValueSeparator) {
		
		// result Hashtable
		TreeMap<String,String> result = new TreeMap<String,String>();
		
		if (input!=null && !input.equals("")) {
			// check separator strings
			if (keyValueSeparator==null || keyValueSeparator.equals("")) {
				keyValueSeparator = "::";
			}
			if (pairSeparator==null || pairSeparator.equals("")) {
				pairSeparator = "|";
			}
			// tokenize input string
			StringTokenizer pairs = new StringTokenizer(input, pairSeparator);
			// tokenize all key/value pairs
			if (pairs.countTokens() == 0) {
				log.debug("wbtServices", "StringServices - getKeyValuePairsSorted: pairs could not be build for '" + input + "' with separator '" + pairSeparator + "'");
			} else {
				while(pairs.hasMoreTokens()) {
					// there is only one key/value pair; tokenize key/value pair
					String keyValueInput = pairs.nextToken();
					StringTokenizer keyValue = new StringTokenizer(keyValueInput, keyValueSeparator);
					// check if more than one token was found
					if (keyValue.hasMoreTokens() && keyValue.countTokens() == 2) {
						String key = keyValue.nextToken();
						String value = keyValue.nextToken();
						result.put(key, value);				
					} else {
						log.debug("wbtServices", "StringServices - getKeyValuePairsSorted: key/value pair could not be build for '" + keyValueInput + "' with separator '" + keyValueSeparator + "'");
					}
				}
			}
		} else {
			result = null;
		}
		return result;
	}

	
	public static Hashtable<String,String> getKeys(String input, String separator) {
		
		// result Hashtable
		Hashtable<String,String> result = new Hashtable<String,String>();
		
		if (input!=null && !input.equals("")) {
			// check separator strings
			if (separator==null || separator.equals("")) {
				separator = "|";
			}
			// tokenize input string
			StringTokenizer keys = new StringTokenizer(input, separator);
			// tokenize all key/value pairs
			if (keys.countTokens() == 0) {
				log.debug("wbtServices", "StringServices - getKeys: keys could not be build for '" + input + "' with separator '" + separator + "'");
			} else {
				while(keys.hasMoreTokens()) {
					// there is only one key/value pair; tokenize key/value pair
					result.put(keys.nextToken(), "true");
				}
			}
		} else {
			result = null;
		}
		return result;
	}
	
	public static String addLeadingZeros(int number, int digit) {
	    String buffer = String.valueOf(number);
	    while(buffer.length() != digit)
	        buffer="0" + buffer;
	    return buffer;
	}

	
	/**
	* generates a md5 hash from a string
	*
	* @param input Ð the string the md5 has to generate from
	* @return Ð a md5 hash as a a string
	*/
	public static String getMD5Hash(String input) {
		StringBuffer stringBuffer = new StringBuffer();
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(input.getBytes());
			Formatter f = new Formatter(stringBuffer);
			for (byte b : md5.digest()) {
				f.format("%02x", b);
			}
		} catch (NoSuchAlgorithmException e) {
			log.debug("wbtServices", e.getMessage());
		}		
		return stringBuffer.toString();
	}

	
	@SuppressWarnings("rawtypes")
	static class ValueComparator implements Comparator {

		public int compare(Object obj1, Object obj2){
			int result=0;
			Map.Entry e1 = (Map.Entry) obj1 ;
			Map.Entry e2 = (Map.Entry) obj2 ; //Sort based on values.
	
			Integer value1 = (Integer)e1.getValue();
			Integer value2 = (Integer)e2.getValue();
	
			if(value1.compareTo(value2)==0){
				String word1=(String)e1.getKey();
				String word2=(String)e2.getKey();
				//Sort String in an alphabetical order
				result=word1.compareToIgnoreCase(word2);
			} else{
				//Sort values in a descending order
				result=value2.compareTo( value1 );
			}
			return result;
		}
	}	
	
}
