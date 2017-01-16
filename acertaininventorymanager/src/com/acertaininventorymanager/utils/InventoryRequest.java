package com.acertaininventorymanager.utils;

import org.eclipse.jetty.http.HttpMethod;

/**
 * {@link InventoryRequest} is the data structure that encapsulates a HTTP
 * request from the bookstore client to the server.
 */
public final class InventoryRequest {

	/** The method. */
	private final HttpMethod method;

	/** The URL string. */
	private final String urlString;

	/** The input value. */
	private final Object inputValue;

	/**
	 * Instantiates a new {@link InventoryRequest}.
	 *
	 * @param method
	 *            the method
	 * @param urlString
	 *            the URL string
	 * @param inputValue
	 *            the input value
	 */
	private InventoryRequest(HttpMethod method, String urlString, Object inputValue) {
		this.method = method;
		this.urlString = urlString;
		this.inputValue = inputValue;
	}

	/**
	 * Gets the method.
	 *
	 * @return the method
	 */
	public HttpMethod getMethod() {
		return method;
	}

	/**
	 * Gets the URL string.
	 *
	 * @return the URL string
	 */
	public String getURLString() {
		return urlString;
	}

	/**
	 * Gets the input value.
	 *
	 * @return the input value
	 */
	public Object getInputValue() {
		return inputValue;
	}

	/**
	 * Gets a new GET request.
	 *
	 * @param urlString
	 *            the URL string
	 * @return the book store request
	 */
	public static InventoryRequest newGetRequest(String urlString) {
		return new InventoryRequest(HttpMethod.GET, urlString, null);
	}

	/**
	 * Gets a new POST request.
	 *
	 * @param urlString
	 *            the URL string
	 * @param inputValue
	 *            the input value
	 * @return the book store request
	 */
	public static InventoryRequest newPostRequest(String urlString, Object inputValue) {
		return new InventoryRequest(HttpMethod.POST, urlString, inputValue);
	}
}
