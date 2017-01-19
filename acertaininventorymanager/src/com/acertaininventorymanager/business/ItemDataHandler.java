package com.acertaininventorymanager.business;

import java.util.ArrayList;
import java.util.List;

import com.acertaininventorymanager.client.InvManagerClientConstants;
import com.acertaininventorymanager.interfaces.ItemDataManager;
import com.acertaininventorymanager.utils.InexistentItemPurchaseException;
import com.acertaininventorymanager.utils.InventoryManagerException;

public class ItemDataHandler implements ItemDataManager {

	private List<ItemPurchase> listOfItemPurchases = new ArrayList<ItemPurchase>();
	private GlobalReadWriteLock lockManager = new GlobalReadWriteLock();
	
	public ItemDataHandler() {
	}

	@Override
	public synchronized void addItemPurchase(ItemPurchase itemPurchase) throws InventoryManagerException {
		try {
			lockManager.writeLock();
			listOfItemPurchases.add(itemPurchase);
			lockManager.releaseWriteLock();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		

	}

	@Override
	public synchronized void removeItemPurchase(int orderId, int customerId, int itemId)
			throws InexistentItemPurchaseException, InventoryManagerException {
		
		ItemPurchase itemPurchase = findItemPurchase(orderId, customerId, itemId);
		if (itemPurchase==null){
			System.out.println("INEXISTENT ITEM PURCHASE");
			throw new InexistentItemPurchaseException();
		}
		else {
			try {
				lockManager.writeLock();
				listOfItemPurchases.remove(itemPurchase);
				lockManager.releaseWriteLock();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		

	}

	/**Searches an ItemPurchase in this ItemDataManager. If the element is not found, it returns null 
	 * (the higher layer may throw an exception).
	 * **/
	public ItemPurchase findItemPurchase(int orderId, int customerId, int itemId){
		ItemPurchase foundItemPurchase = null;
		try {
			lockManager.readLock();
			
			for (ItemPurchase itemPur : listOfItemPurchases){
				if (itemPur.getOrderId() == orderId &&
					itemPur.getCustomerId() == customerId && 
					itemPur.getItemId() == itemId) {
						foundItemPurchase = itemPur;
				}
			}
			lockManager.releaseReadLock();
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return foundItemPurchase;
	}

	/**Only for testing purposes: 
	 * Getter method to check the list of purchases registered in this IDM.*/
	public List<ItemPurchase> getListOfItemPurchases() {
		return listOfItemPurchases;
	}
	
}
