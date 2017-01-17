package com.acertaininventorymanager.tests;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import javax.naming.InitialContext;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.acertaininventorymanager.business.Customer;
import com.acertaininventorymanager.business.ItemPurchase;
import com.acertaininventorymanager.client.ClientHTTPProxy;
import com.acertaininventorymanager.client.InvManagerClientConstants;
import com.acertaininventorymanager.interfaces.CustomerTransactionManager;
import com.acertaininventorymanager.server.CtmHTTPServer;
import com.acertaininventorymanager.server.IdmHTTPServer;
import com.acertaininventorymanager.tests.RPCtests;
import com.acertaininventorymanager.utils.InventoryManagerException;

public class AtomicityTests {

	public final static int NUM_OF_IDM=5;
	public final static int RANDOMINT_BOUND = 1000;
	public final static int ITERATIONS = 100;
	
	private static ClientHTTPProxy client;
	private static CustomerTransactionManager ctm;
	private static Set<Customer> customers;
	private static int[] itemIds = {2026,2027,2028,2029};
	private static Set<ItemPurchase> fixedPurchases;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		client = new ClientHTTPProxy("http://localhost:8081");
		ctm = client;
		
		//////////The CTM Server thread /////////
		new Thread() {
		    public void run() {
		        try {
		        	String[] args = {String.valueOf(NUM_OF_IDM)};
					CtmHTTPServer.main(args);
				} catch (Exception e) {
					e.printStackTrace();
				}
		    }
		}.start();
		////////// 
		//////////Start the IDM Servers (and register the threads)/////////
		for (int i=1; i<=NUM_OF_IDM; i++){
			int portNumber = InvManagerClientConstants.DEFAULT_PORT + i;
	       	String portNumberS = String.valueOf(portNumber);
			Thread t = new Thread() {
			    public void run() {
			        try {
			        	String[] args = {portNumberS};
						IdmHTTPServer.main(args);
					} catch (Exception e) {
						e.printStackTrace();
					}
			    }
			};
			t.start();
		}
		///////// 
		initializePurchasesForConcurrentAddRemoval();

	}

	/**Helper function*/
	private static void initializePurchasesForConcurrentAddRemoval(){

		for (int itemId : itemIds){
			int orderId = itemId*2;
			int customerId = itemId*3;
			int quantity = RPCtests.randGen.nextInt(RANDOMINT_BOUND)+1;
			int unitPrice = RPCtests.randGen.nextInt(RANDOMINT_BOUND)+1;
			fixedPurchases.add(new ItemPurchase(orderId, customerId, itemId, quantity, unitPrice));
		}
	}

	/**Initialization before every test: create a random set of customers, 
	 * create a random set of purchases, and process the orders in the system.
	 * Then, add the fixed purchases a number of times >= the number of thread iterations.*/
	@Before
	public void setUp() throws Exception {
		client.removeAllCustomers();
		customers = RPCtests.createSetOfCustomers();
		client.addCustomers(customers);
		Set<ItemPurchase> randomPurchases = RPCtests.createSetOfItemPurchases(customers);
		client.processOrders(randomPurchases);
		
		for (int i=0; i<ITERATIONS; i++){
			client.processOrders(fixedPurchases);
		}
	}


	/**In this test: 
	 * Thread1 adds a number */
	@Test
	public void testConcurrentAddRemove() {

		Thread adder = new Thread(){
			public void run() {
			for (int i=0; i<ITERATIONS; i++){
				try {
					client.processOrders(fixedPurchases);
				} catch (InventoryManagerException e) {
					e.printStackTrace();
				}
			}
				
			}
		};
		
		
		Thread remover = new Thread(){
			public void run() {
				for (int i=0; i<ITERATIONS; i++){
					try {
						client.removeOrders(fixedPurchases);
					} catch (InventoryManagerException e) {
						e.printStackTrace();
					}
				}	
			}
		};
		
	}

}
