package com.acertaininventorymanager.tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import com.acertaininventorymanager.utils.InexistentCustomerException;
import com.acertaininventorymanager.utils.InventoryManagerException;
import com.acertaininventorymanager.utils.NonPositiveIntegerException;

//TODO: either find a way to cause timeouts while communicating with IDMs, or remove this entirely.
public class IdmFailureTests {
	
	public final static int NUM_OF_IDM=1;
	
	private static ClientHTTPProxy client;
	private static CustomerTransactionManager ctm;
	private static Set<Customer> customers;

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
	}


	/**Initialization before every test: create a random set of customers, 
	 * create a random set of purchases, and process the orders in the system.*/
	@Before
	public void setUp() throws Exception {
		client.removeAllCustomers();
		customers = RPCtests.createSetOfCustomers();
		client.addCustomers(customers);
		Set<ItemPurchase> purchases = RPCtests.createSetOfItemPurchases(customers);
		client.processOrders(purchases);
	}

	@Test
	public void testIDMfailure() throws InventoryManagerException {
	
		client.causeIDMfailure();
		Set<ItemPurchase> purchases = RPCtests.createSetOfItemPurchases(customers);
		client.processOrders(purchases);
		
		Customer aCustomer = customers.stream().findAny().get();
		Integer regionId = aCustomer.getRegionId();
		Set<Integer> regionIds = new HashSet<>(); regionIds.add(regionId);
		
		client.getTotalsForRegions(regionIds);
		assertTrue(true);

	}

}
