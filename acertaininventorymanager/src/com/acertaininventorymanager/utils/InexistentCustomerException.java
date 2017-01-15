package com.acertaininventorymanager.utils;

import com.acertaininventorymanager.business.Customer;

/**
 * This class represents an application-level exception to be raised whenever a
 * customer is to be found by identifier, but the calling code provides an
 * identifier that does not match any {@link Customer} recorded in the service.
 * 
 * @author vmarcos
 */
public class InexistentCustomerException extends InventoryManagerException {

	/**
	 * The serial version UID.
	 */
	private static final long serialVersionUID = -9022170746344025516L;

	/**
	 * Instantiates an {@link InexistentCustomerException}
	 */
	public InexistentCustomerException() {
	}

	/**
	 * Instantiates an {@link InexistentCustomerException} with a given error
	 * message.
	 *
	 * @param message
	 */
	public InexistentCustomerException(String message) {
		super(message);
	}

	/**
	 * Instantiates an {@link InexistentCustomerException} with a given root
	 * cause.
	 *
	 * @param cause
	 */
	public InexistentCustomerException(Throwable cause) {
		super(cause);
	}

	/**
	 * Instantiates an {@link InexistentCustomerException} with a given error
	 * message and root cause.
	 *
	 * @param message
	 * @param cause
	 */
	public InexistentCustomerException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates an {@link InexistentCustomerException} with a given error
	 * message, root cause, suppression modifier, and writable stack trace
	 * modifier.
	 *
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public InexistentCustomerException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
