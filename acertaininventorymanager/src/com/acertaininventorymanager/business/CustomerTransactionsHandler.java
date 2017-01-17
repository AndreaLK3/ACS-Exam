package com.acertaininventorymanager.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.acertaininventorymanager.client.CtmClientHTTPProxy;
import com.acertaininventorymanager.client.InvManagerClientConstants;
import com.acertaininventorymanager.interfaces.CustomerTransactionManager;
import com.acertaininventorymanager.interfaces.ItemDataManager;
import com.acertaininventorymanager.server.CtmHTTPServer;
import com.acertaininventorymanager.server.IdmHTTPServer;
import com.acertaininventorymanager.utils.EmptyRegionException;
import com.acertaininventorymanager.utils.InexistentCustomerException;
import com.acertaininventorymanager.utils.InexistentItemPurchaseException;
import com.acertaininventorymanager.utils.InventoryManagerException;
import com.acertaininventorymanager.utils.NonPositiveIntegerException;

public class CustomerTransactionsHandler implements CustomerTransactionManager {

	private ConcurrentHashMap<Integer,Customer> customers = new ConcurrentHashMap<>();
	int numOfItemDataManagers;
	ConcurrentHashMap<Integer, ItemDataManager> IDMs = new ConcurrentHashMap<>();
	
	
	/**The constructor. Creates the CtmClientHTTPProxies associated with the IDMs,
	 * and registers the proxies in the ConcurrentHashMap. Note: Does not start any IDM Server.
	 * @param  numOfItemDataManagers
	 * @throws Exception */
	public CustomerTransactionsHandler(int numOfItemDataManagers, Set<Customer> startingCustomers) throws Exception {
		this.numOfItemDataManagers = numOfItemDataManagers;
		
		for (int i=1; i<=numOfItemDataManagers; i++){
		
			int portNumber = InvManagerClientConstants.DEFAULT_PORT + i;
			String portNumberS = String.valueOf(portNumber);
			String serverAddress = InvManagerClientConstants.ADDRESSPART + portNumberS;
			
;			IDMs.put(i, new CtmClientHTTPProxy(serverAddress));
		}
		addCustomers(startingCustomers);
	}
	
	
/** The interface function processOrders. It carries out 3 tasks:  
 *  - validate the set of item purchases, guaranteeing all-or-nothing semantics.
 *  - send each item purchase to its ItemDataManager, determined using the hash function on the itemID.
 *  - update the total value spent by each customer
 * */
	@Override
	public synchronized void processOrders(Set<ItemPurchase> itemPurchases)
			throws NonPositiveIntegerException, InexistentCustomerException, InventoryManagerException {

		for (ItemPurchase itP : itemPurchases){
			validateItemPurchase(itP);
		}
		
		for (ItemPurchase itP : itemPurchases){
			int idmNumber = hashingFunction(itP)+1;
			ItemDataManager theIdm = IDMs.get(idmNumber);
			theIdm.addItemPurchase(itP);
			
			Customer theCustomer = customers.get(itP.getCustomerId());
			long oldTotal = theCustomer.getValueBought();
			theCustomer.setValueBought(oldTotal + itP.getUnitPrice()*itP.getQuantity());
		}

	}

	//TODO: add read/write locks on the CTH
	private synchronized void validateItemPurchase(ItemPurchase itemPurchase)
			throws NonPositiveIntegerException, InexistentCustomerException, InventoryManagerException {

		if (! isPositiveInteger(itemPurchase.getOrderId()) )
			throw new NonPositiveIntegerException();
		
		if (! isPositiveInteger(itemPurchase.getCustomerId()) )
				throw new NonPositiveIntegerException();
		
		if (! (customers.containsKey(itemPurchase.getCustomerId())))
			throw new InexistentCustomerException();
		
		if (! isPositiveInteger(itemPurchase.getQuantity()) )
			throw new NonPositiveIntegerException();
		
		if (! isPositiveInteger(itemPurchase.getUnitPrice()) )
			throw new NonPositiveIntegerException();
	}
	
	
	private boolean isPositiveInteger(Object o){
		boolean valid = true;
		
		try {
			int value = (int) o;
			if (value <= 0 || value != Math.floor(value))
				valid = false;
		} catch (Exception e){
			valid = false;
			e.printStackTrace();
		}
		
		return valid;
	}
	
/**	Item purchase data is distributed across the multiple instances of ItemDataManager,
*   by a hash function h mapping item IDs to ItemDataManager instance numbers.
*	instance numbers are assigned in the domain [0, …, N-1]*/
	private int hashingFunction(ItemPurchase itemPurchase){
		int itemId = itemPurchase.getItemId();
		int associatedIDM = itemId % numOfItemDataManagers;
		return associatedIDM;
	}
	
	
	/**This operation calculates the total value bought for the customers of each of the regions given,
	 * and returns a list of these aggregate values per region
	 * */
	@Override
	public List<RegionTotal> getTotalsForRegions(Set<Integer> regionIds)
			throws NonPositiveIntegerException, EmptyRegionException, InventoryManagerException {
		
		validateRegionIds(regionIds);

		Set<Integer> customerIds = customers.keySet();
		HashMap<Integer,RegionTotal> regionTotals = new HashMap<>();
		List<RegionTotal> outputListRegionTotals = new ArrayList<>();
		
		for (Integer customerId : customerIds){
			Customer c = customers.get(customerId);
			
			int cRegId = c.getRegionId();
			RegionTotal cRegTot = regionTotals.get(cRegId);
			long oldTotal = 0;
			if (cRegTot != null){
				oldTotal = cRegTot.getTotalValueBought();
			}
			RegionTotal updatedCRegTot = new RegionTotal(cRegId, oldTotal+c.getValueBought());
			regionTotals.put(cRegId, updatedCRegTot);

		}
		
		for (Integer regionId : regionIds){
			outputListRegionTotals.add(regionTotals.get(regionId));
		}
		
		return outputListRegionTotals;
	}
	
	
	/**The method performs the following validation checks: 
	 * 1) The region IDs given are positive integers; and 
	 * 2) Each region ID matches at least one customer.	 * */
	private void validateRegionIds(Set<Integer> regionIds) throws NonPositiveIntegerException, EmptyRegionException{

		for (Integer regId : regionIds){
			if ( ! isPositiveInteger(regId))
				throw new NonPositiveIntegerException();
		}
		
		Set<Integer> customerIds = customers.keySet();
		
		for (Integer regId : regionIds){
			
			boolean regionEmpty = true;
			
			for (Integer customerId : customerIds){
				Customer c = customers.get(customerId);
				if (c.getRegionId() == regId)
				regionEmpty = false;	
			}
			if (regionEmpty == true)
				throw new EmptyRegionException();
		}
		
	}
	
	/**Helper function: adds new customers to the CTM.**/
	public synchronized void addCustomers(Set<Customer> newCustomers){
		for (Customer c : newCustomers){
			int key = c.getCustomerId();
			Customer value = c;
			customers.put(key, value);
		}
	}
	
	/**This function eliminates all customers from the CTM. Used for testing purposes.**/
	public synchronized void removeAllCustomers(){
		customers = new ConcurrentHashMap<>();
	}
	
	/**This function removes a specific item purchase from the IDM that contains it, 
	 * and updates the customer's data.
	 * @throws InventoryManagerException 
	 * @throws InexistentItemPurchaseException **/
	public synchronized void removeItemPurchase(int orderId, int customerId, int itemId) 
			throws InexistentItemPurchaseException, InventoryManagerException{
		ItemPurchase temp = new ItemPurchase(orderId, customerId, itemId, 1, 1);
		int idmNumber = hashingFunction(temp)+1;
		ItemDataManager theIdm = IDMs.get(idmNumber);
		
		theIdm.removeItemPurchase(orderId, customerId, itemId);
		
	}

	/**For testing purposes.*/
	public ConcurrentHashMap<Integer, ItemDataManager> getIDMs() {
		return IDMs;
	}
	
	/**For testing purposes (work in progress).
	 * @throws Exception */
	public void causeIDMfailure() {
		CtmClientHTTPProxy failingIDM = (CtmClientHTTPProxy) IDMs.get(1);
		failingIDM.setServerAddress(InvManagerClientConstants.ADDRESSPART + (InvManagerClientConstants.DEFAULT_PORT-1));
	}


	/**For testing purposes> allows us to use removeItemPurchase in the IDMs */
	public void removeOrders(Set<ItemPurchase> itemPurchases) throws NonPositiveIntegerException, InexistentCustomerException, InventoryManagerException {
		for (ItemPurchase itP : itemPurchases){
			validateItemPurchase(itP);
		}
		
		for (ItemPurchase itP : itemPurchases){
			int idmNumber = hashingFunction(itP)+1;
			ItemDataManager theIdm = IDMs.get(idmNumber);
			int orderID = itP.getOrderId();
			int customerID = itP.getCustomerId();
			int itemID = itP.getItemId();			
			
			theIdm.removeItemPurchase(orderID, customerID, itemID);
			
			Customer theCustomer = customers.get(itP.getCustomerId());
			long oldTotal = theCustomer.getValueBought();
			theCustomer.setValueBought(oldTotal - itP.getUnitPrice()*itP.getQuantity());
		}
		
	}

}
