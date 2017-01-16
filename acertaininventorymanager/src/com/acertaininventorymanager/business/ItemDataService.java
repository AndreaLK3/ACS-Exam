package com.acertaininventorymanager.business;

import java.util.ArrayList;
import java.util.List;

import com.acertaininventorymanager.interfaces.ItemDataManager;
import com.acertaininventorymanager.utils.InexistentItemPurchaseException;
import com.acertaininventorymanager.utils.InventoryManagerException;

public class ItemDataService implements ItemDataManager {

	List<ItemPurchase> listOfItemPurchases = new ArrayList<ItemPurchase>();
	
	public ItemDataService() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void addItemPurchase(ItemPurchase itemPurchase) throws InventoryManagerException {
		listOfItemPurchases.add(itemPurchase);

	}

	@Override
	public void removeItemPurchase(int orderId, int customerId, int itemId)
			throws InexistentItemPurchaseException, InventoryManagerException {
		ItemPurchase itemPurchase = findItemPurchase(orderId, customerId, itemId);
		if (itemPurchase==null){
			throw new InexistentItemPurchaseException();
		}
		else {
			listOfItemPurchases.remove(itemPurchase);
		}
		

	}

	/**Searches an ItemPurchase in this ItemDataManager. If the element is not found, it returns null 
	 * (the higher layer may throw an exception).
	 * **/
	private ItemPurchase findItemPurchase(int orderId, int customerId, int itemId){
		ItemPurchase foundItemPurchase = null;
		
		for (ItemPurchase itemPur : listOfItemPurchases){
			if (itemPur.getOrderId() == orderId &&
				itemPur.getCustomerId() == customerId && 
				itemPur.getItemId() == itemId) {
					foundItemPurchase = itemPur;
			}
		}
		return foundItemPurchase;
	}
}
