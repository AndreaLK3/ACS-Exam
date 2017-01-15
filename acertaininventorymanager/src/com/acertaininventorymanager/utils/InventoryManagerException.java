package com.acertaininventorymanager.utils;

/**
 * This class is the root exception type for the acertaininventorymanager
 * service. The exception can be thrown directly in case of system-level errors,
 * as opposed to specialized exception classes for application-level errors.
 * 
 * @author vmarcos
 */
public class InventoryManagerException extends Exception {

	/**
	 * The serial version UID.
	 */
	private static final long serialVersionUID = -4654687394509401800L;

	/**
	 * Instantiates an {@link InventoryManagerException}
	 */
	public InventoryManagerException() {
	}

	/**
	 * Instantiates an {@link InventoryManagerException} with a given error
	 * message.
	 *
	 * @param message
	 */
	public InventoryManagerException(String message) {
		super(message);
	}

	/**
	 * Instantiates an {@link InventoryManagerException} with a given root
	 * cause.
	 *
	 * @param cause
	 */
	public InventoryManagerException(Throwable cause) {
		super(cause);
	}

	/**
	 * Instantiates an {@link InventoryManagerException} with a given error
	 * message and root cause.
	 *
	 * @param message
	 * @param cause
	 */
	public InventoryManagerException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates an {@link InventoryManagerException} with a given error
	 * message, root cause, suppression modifier, and writable stack trace
	 * modifier.
	 *
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public InventoryManagerException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
