package com.acertaininventorymanager.utils;

/**
 * {@link InventoryMessageTag} implements the messages supported in the
 * bookstore.
 */
public enum InventoryMessageTag {

	/** The tag for the "process orders" message. */
	PROCESSORDERS,
	REMOVEORDERS,
	
	/** The tag for the "get regions' totals" message. */
	GETREGIONTOTALS, 
	
	/** The tag for the "add customers' totals" message. */
	ADDCUSTOMERS,
	
	/** The tag for the "eliminate all customers' totals" message. */
	CLEARCUSTOMERS,
	
	/** From CTM to IDMs : The tag for the "add item purchase" message. */
	ADDPURCHASE,
	REMOVEPURCHASE, 
	
	/** The tag to tell the CTM to make one of its IDMs fail. */
	CAUSEIDMFAILURE,

}
