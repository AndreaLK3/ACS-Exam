package com.acertaininventorymanager.interfaces;

import java.util.List;
import java.util.Set;

import com.acertaininventorymanager.business.ItemPurchase;
import com.acertaininventorymanager.business.RegionTotal;
import com.acertaininventorymanager.utils.EmptyRegionException;
import com.acertaininventorymanager.utils.InexistentCustomerException;
import com.acertaininventorymanager.utils.InventoryManagerException;
import com.acertaininventorymanager.utils.NonPositiveIntegerException;

/**
 * This interface declares the methods supported by the customer transaction
 * manager layer of the inventory management system. The customer transaction
 * manager layer provides higher-level atomic operations for processing orders
 * from customers and aggregating sales region monetary totals, stores customer
 * information, and is centralized into a single component.
 * 
 * For more details on the methods of this interface, please refer to the
 * programming task description.
 * 
 * @author vmarcos
 */
public interface CustomerTransactionManager {

	/**
	 * Processes orders from various customers according to conditions agreed
	 * with sales managers for a variety of items.
	 * 
	 * @param itemPurchases
	 *            - the set of {@link ItemPurchase} instances.
	 * @throws NonPositiveIntegerException
	 * @throws InexistentCustomerException
	 * @throws InventoryManagerException
	 */
	public void processOrders(Set<ItemPurchase> itemPurchases)
			throws NonPositiveIntegerException, InexistentCustomerException, InventoryManagerException;

	/**
	 * Returns the value bought in total per region for the requested set of
	 * region identifiers.
	 * 
	 * @param regionIds
	 *            - the set of region identifiers for which {@link RegionTotal}s
	 *            will be produced.
	 * @return the list of {@link RegionTotal}s.
	 * @throws NonPositiveIntegerException
	 * @throws EmptyRegionException
	 * @throws InventoryManagerException
	 */
	public List<RegionTotal> getTotalsForRegions(Set<Integer> regionIds)
			throws NonPositiveIntegerException, EmptyRegionException, InventoryManagerException;

}
