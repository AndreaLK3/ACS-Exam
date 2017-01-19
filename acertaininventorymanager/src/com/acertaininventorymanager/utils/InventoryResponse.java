package com.acertaininventorymanager.utils;

import java.util.List;

import com.acertaininventorymanager.business.ItemPurchase;
import com.acertaininventorymanager.business.RegionTotal;

/**
 * {@link InventoryResponse} is the data structure that encapsulates a HTTP
 * response from the Inventory server to the client. The data structure contains
 * error messages from the server if an error occurred.
 */
public class InventoryResponse {

	/** The exception. */
	private InventoryManagerException exception;

	/** The list. */
	private List<?> list;

	/**
	 * Instantiates a new {@link InventoryResponse}.
	 *
	 * @param exception
	 *            the exception
	 * @param list
	 *            the list
	 */
	public InventoryResponse(InventoryManagerException exception, List<RegionTotal> list) {
		this.setException(exception);
		this.setList(list);
	}

	/**
	 * Instantiates a new book store response.
	 */
	public InventoryResponse() {
		this.setException(null);
		this.setList(null);
	}

	/**
	 * Gets the list.
	 *
	 * @return the list
	 */
	public List<?> getList() {
		return list;
	}

	/**
	 * Sets the list.
	 *
	 * @param list
	 *            the new list
	 */
	public void setList(List<?> list) {
		this.list = list;
	}

	/**
	 * Gets the exception.
	 *
	 * @return the exception
	 */
	public InventoryManagerException getException() {
		return exception;
	}

	/**
	 * Sets the exception.
	 *
	 * @param exception
	 *            the new exception
	 */
	public void setException(InventoryManagerException exception) {
		this.exception = exception;
		exception.fillInStackTrace();
		exception.printStackTrace();
		System.out.println(exception.getCause());
	}
}
