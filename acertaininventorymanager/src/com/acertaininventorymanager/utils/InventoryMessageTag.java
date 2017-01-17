package com.acertaininventorymanager.utils;

/**
 * {@link InventoryMessageTag} implements the messages supported in the
 * bookstore.
 */
public enum InventoryMessageTag {

	/** The tag for the "process orders" message. */
	PROCESSORDERS,
	
	/** The tag for the "get regions' totals" message. */
	GETREGIONTOTALS, 
	
	/** The tag for the "add customers' totals" message. */
	ADDCUSTOMERS,
	
	/** The tag for the "eliminate all customers' totals" message. */
	CLEARCUSTOMERS,
	
	/** From CTM to IDMs : The tag for the "add item purchase" message. */
	ADDPURCHASE,
	
	/** From CTM to IDMs: The tag for the "remove item purchase" message. */
	REMOVEPURCHASE,

}
