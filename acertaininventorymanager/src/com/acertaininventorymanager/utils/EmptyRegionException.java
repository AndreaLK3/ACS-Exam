package com.acertaininventorymanager.utils;

import com.acertaininventorymanager.business.Customer;

/**
 * This class represents an application-level exception to be raised whenever a
 * region identifier is given to a function that does not match the region
 * identifier of any {@link Customer} in the service.
 * 
 * @author vmarcos
 */
public class EmptyRegionException extends InventoryManagerException {

	/**
	 * The serial version UID.
	 */
	private static final long serialVersionUID = 4524410391560882041L;

	/**
	 * Instantiates an {@link EmptyRegionException}
	 */
	public EmptyRegionException() {
	}

	/**
	 * Instantiates an {@link EmptyRegionException} with a given error message.
	 *
	 * @param message
	 */
	public EmptyRegionException(String message) {
		super(message);
	}

	/**
	 * Instantiates an {@link EmptyRegionException} with a given root cause.
	 *
	 * @param cause
	 */
	public EmptyRegionException(Throwable cause) {
		super(cause);
	}

	/**
	 * Instantiates an {@link EmptyRegionException} with a given error message
	 * and root cause.
	 *
	 * @param message
	 * @param cause
	 */
	public EmptyRegionException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates an {@link EmptyRegionException} with a given error message,
	 * root cause, suppression modifier, and writable stack trace modifier.
	 *
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public EmptyRegionException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
