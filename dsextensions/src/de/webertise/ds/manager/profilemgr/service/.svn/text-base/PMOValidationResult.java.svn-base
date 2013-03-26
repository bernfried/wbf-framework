package de.webertise.ds.manager.profilemgr.service;


import java.util.Enumeration;
import java.util.Hashtable;

// import de.webertise.ds.services.LogServices;

public class PMOValidationResult {
	
	// declare log services
	// private LogServices log = LogServices.getInstance();
	

	private Hashtable<String,PMOValidationResultItem> valResultItems = new Hashtable<String,PMOValidationResultItem>();
	private boolean valid = true;

	/**
	 * @param valResultItems the valResultItems to set
	 */
	public void setValResultItems(Hashtable<String,PMOValidationResultItem> valResultItems) {
		this.valResultItems = valResultItems;
	}

	/**
	 * @return the valResultItems
	 */
	public Hashtable<String,PMOValidationResultItem> getValResultItems() {
		return valResultItems;
	}
	
	/**
	 * @param boolean only persistent items
	 * @return the valResultItems
	 */
	public Hashtable<String,PMOValidationResultItem> getValResultItems(boolean onlyPersistentItems) {
		
		// result hashtable
		Hashtable<String,PMOValidationResultItem> valResItems = new Hashtable<String,PMOValidationResultItem>();
		
		// iterate through all items
		Enumeration<String> valResultItemsKeys = valResultItems.keys();
		while (valResultItemsKeys.hasMoreElements()) {
			String key = valResultItemsKeys.nextElement();
			PMOValidationResultItem item = valResultItems.get(key);
			// log.debug("PMOValidationResult - getValResultItems - Item '" + key + "' / Persistent: '" + item.isPersistent() + "'");
			if (!onlyPersistentItems || item.isPersistent()) {
				valResItems.put(key,item);
				// log.debug("PMOValidationResult - getValResultItems - Added item '" + key + "'");
			}
		}
		return valResItems;
	}
	
	/**
	 * @param the valResultItem to add
	 */
	public void addValResultItem(PMOValidationResultItem item) {
		// result is invalid if one item is invalid
		if (!item.isValid()) this.setValid(false);
		// add item
		this.valResultItems.put(item.getName(), item);
	}
	
	public PMOValidationResultItem getValResultItem(String id) {
		return this.valResultItems.get(id);
	}

	/**
	 * @param valid the valid to set
	 */
	public void setValid(boolean valid) {
		this.valid = valid;
	}

	/**
	 * @return the valid
	 */
	public boolean isValid() {
		return valid;
	}


	
}
