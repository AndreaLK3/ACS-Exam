package com.acertaininventorymanager.tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.naming.InitialContext;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.acertaininventorymanager.business.Customer;
import com.acertaininventorymanager.business.CustomerTransactionsHandler;
import com.acertaininventorymanager.business.ItemDataHandler;
import com.acertaininventorymanager.business.ItemPurchase;
import com.acertaininventorymanager.business.RegionTotal;
import com.acertaininventorymanager.client.ClientHTTPProxy;
import com.acertaininventorymanager.client.InvManagerClientConstants;
import com.acertaininventorymanager.interfaces.CustomerTransactionManager;
import com.acertaininventorymanager.server.CtmHTTPServer;
import com.acertaininventorymanager.server.IdmHTTPServer;
import com.acertaininventorymanager.tests.RPCtests;
import com.acertaininventorymanager.utils.InventoryManagerException;

public class AtomicityIDMtests {

	public final static int NUM_OF_IDM=5;
	public final static int RANDOMINT_BOUND = 1000;
	public final static int ITERATIONS = 1000;
	public final static int NUM_OF_CUSTOMERS = 20;
	public final static Set<Integer> REGIONS = new HashSet<Integer>(Arrays.asList(1, 2, 3));
	
	private static ItemDataHandler theIdm;
	private static Set<Customer> customers;
	private static int itemId = 2027;
	private static ItemPurchase thePurchase;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		theIdm = new ItemDataHandler();

	}


	/**Initialization before every test: create a random set of customers, 
	 * create a random set of purchases, and process the orders in the system.
	 * Then, add the fixed purchases, in case the consumer thread acts before the producer thread.*/
	@Before
	public void setUp() throws Exception {

		customers = RPCtests.createSetOfCustomers(NUM_OF_CUSTOMERS, REGIONS);

		Set<ItemPurchase> randomPurchases = RPCtests.createSetOfItemPurchases(customers);
		
		for (ItemPurchase itP : randomPurchases){
			theIdm.addItemPurchase(itP);
		}
		
		for (int i=0; i<ITERATIONS; i++){
			int orderId = itemId*2;
			int customerId = i;
			int quantity = RPCtests.randGen.nextInt(RANDOMINT_BOUND)+1;
			int unitPrice = RPCtests.randGen.nextInt(RANDOMINT_BOUND)+1;
			thePurchase = new ItemPurchase(orderId, customerId, itemId, quantity, unitPrice);
			
			try {
				theIdm.addItemPurchase(thePurchase);
			} catch (InventoryManagerException e) {
				e.printStackTrace();
			}
		}

	}


	/**In this test: 
	 * Thread1 adds a purchase for a number of times. */
	@Test
	public void testConcurrentAddRemove() {
		
		List<ItemPurchase> oldListOfItemPurchases = theIdm.getListOfItemPurchases();

		Thread adder = new Thread(){
			public void run() {
			for (int i=0; i<ITERATIONS; i++){
				int orderId = itemId*2;
				int customerId = i;
				int quantity = RPCtests.randGen.nextInt(RANDOMINT_BOUND)+1;
				int unitPrice = RPCtests.randGen.nextInt(RANDOMINT_BOUND)+1;
				thePurchase = new ItemPurchase(orderId, customerId, itemId, quantity, unitPrice);
				
				try {
					theIdm.addItemPurchase(thePurchase);
				} catch (InventoryManagerException e) {
					e.printStackTrace();
				}
			}
				
			}
		};
		
		Thread remover = new Thread(){
			public void run() {
				for (int i=0; i<ITERATIONS; i++){
					int orderId = itemId*2;
					int customerId = i;
					int quantity = RPCtests.randGen.nextInt(RANDOMINT_BOUND)+1;
					int unitPrice = RPCtests.randGen.nextInt(RANDOMINT_BOUND)+1;
					thePurchase = new ItemPurchase(orderId, customerId, itemId, quantity, unitPrice);
					
					try {
						theIdm.removeItemPurchase(orderId,customerId,itemId);
					} catch (InventoryManagerException e) {
						e.printStackTrace();
					}
				}
			}
		};

		adder.start();
		remover.start();
		
		try {
			adder.join();
			remover.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		List<ItemPurchase> newListOfItemPurchases = theIdm.getListOfItemPurchases();
		
		boolean valid = true;
		
		for (ItemPurchase oldItP : oldListOfItemPurchases){
			if (! (newListOfItemPurchases.contains(oldItP))){
				valid = false;
			}
		}
		
		assertTrue(valid);		
	}
	
	
	
	
	
	
}
