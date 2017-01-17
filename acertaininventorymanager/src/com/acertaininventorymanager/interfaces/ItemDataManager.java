package com.acertaininventorymanager.interfaces;

import java.io.IOException;

import com.acertaininventorymanager.business.Customer;
import com.acertaininventorymanager.business.ItemPurchase;
import com.acertaininventorymanager.utils.InexistentItemPurchaseException;
import com.acertaininventorymanager.utils.InventoryManagerException;

/**
 * This interface declares the methods supported by the item data manager layer
 * of the inventory management system. The item data manager layer keeps the
 * detailed information for items purchased, and only provides single-element
 * atomic operations.
 * 
 * For more details on the methods of this interface, please refer to the
 * programming task description.
 * 
 * @author vmarcos
 */
public interface ItemDataManager {

	/**
	 * Adds an item purchase to this {@link ItemDataManager} instance.
	 * 
	 * @param itemPurchase
	 *            - an instance of {@link ItemPurchase}
	 * @throws InventoryManagerException
	 */
	public void addItemPurchase(ItemPurchase itemPurchase) throws InventoryManagerException;

	/**
	 * Removes an existing {@link ItemPurchase} from this
	 * {@link ItemDataManager} instance.
	 * 
	 * @param orderId
	 *            - the identifier of the order.
	 * @param customerId
	 *            - the identifier of the {@link Customer}.
	 * @param itemId
	 *            - the identifier of the item.
	 * @throws InexistentItemPurchaseException
	 * @throws InventoryManagerException 
	 */
	public void removeItemPurchase(int orderId, int customerId, int itemId)
			throws InexistentItemPurchaseException, InventoryManagerException, IOException;

}
