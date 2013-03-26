/** 
 * Constants for Delivery Server Events
 * 
 * @author Bernfried Howe, bernfried.howe@webertise.de
 * @version 1.0
 */
package de.webertise.ds.events;

/**
 * Interface for Event Constants used in each DS Event implementation	
 */
public interface EventConstants {
	
	/**
	 * attribute name/path of content attribute	which is used to identify
	 * if a content has been updated via an event in order to avoid a 
	 * endless loop of the update event.
	 */
	static final String CONTENT_ATTRIBUTE_NAME_UPDATE_FLAG = "updateFlag";



}
