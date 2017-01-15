package com.acertaininventorymanager.utils;

/**
 * This class represents an application-level exception that is raised whenever
 * a parameter is supposed to be a positive integer, but a non-positive integer
 * is given by the calling code instead.
 * 
 * @author vmarcos
 */
public class NonPositiveIntegerException extends InventoryManagerException {

	/**
	 * The serial version UID.
	 */
	private static final long serialVersionUID = -7230235346123892641L;

	/**
	 * Instantiates a {@link NonPositiveIntegerException}
	 */
	public NonPositiveIntegerException() {
	}

	/**
	 * Instantiates a {@link NonPositiveIntegerException} with a given error
	 * message.
	 *
	 * @param message
	 */
	public NonPositiveIntegerException(String message) {
		super(message);
	}

	/**
	 * Instantiates a {@link NonPositiveIntegerException} with a given root
	 * cause.
	 *
	 * @param cause
	 */
	public NonPositiveIntegerException(Throwable cause) {
		super(cause);
	}

	/**
	 * Instantiates a {@link NonPositiveIntegerException} with a given error
	 * message and root cause.
	 *
	 * @param message
	 * @param cause
	 */
	public NonPositiveIntegerException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a {@link NonPositiveIntegerException} with a given error
	 * message, root cause, suppression modifier, and writable stack trace
	 * modifier.
	 *
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public NonPositiveIntegerException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
