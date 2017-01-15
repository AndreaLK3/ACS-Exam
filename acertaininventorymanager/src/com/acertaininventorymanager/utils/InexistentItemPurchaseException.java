package com.acertaininventorymanager.utils;

import com.acertaininventorymanager.business.ItemPurchase;

/**
 * This class represents an application-level exception to be thrown whenever
 * the attempt to find an {@link ItemPurchase} results in no such record being
 * found.
 * 
 * @author vmarcos
 */
public class InexistentItemPurchaseException extends InventoryManagerException {

	/**
	 * The serial version UID.
	 */
	private static final long serialVersionUID = -8932447090789107149L;

	/**
	 * Instantiates an {@link InexistentItemPurchaseException}
	 */
	public InexistentItemPurchaseException() {
	}

	/**
	 * Instantiates an {@link InexistentItemPurchaseException} with a given
	 * error message.
	 *
	 * @param message
	 */
	public InexistentItemPurchaseException(String message) {
		super(message);
	}

	/**
	 * Instantiates an {@link InexistentItemPurchaseException} with a given root
	 * cause.
	 *
	 * @param cause
	 */
	public InexistentItemPurchaseException(Throwable cause) {
		super(cause);
	}

	/**
	 * Instantiates an {@link InexistentItemPurchaseException} with a given
	 * error message and root cause.
	 *
	 * @param message
	 * @param cause
	 */
	public InexistentItemPurchaseException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates an {@link InexistentItemPurchaseException} with a given
	 * error message, root cause, suppression modifier, and writable stack trace
	 * modifier.
	 *
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public InexistentItemPurchaseException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
