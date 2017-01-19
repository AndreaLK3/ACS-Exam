package com.acertaininventorymanager.tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.naming.InitialContext;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.acertaininventorymanager.business.Customer;
import com.acertaininventorymanager.business.ItemPurchase;
import com.acertaininventorymanager.business.RegionTotal;
import com.acertaininventorymanager.client.ClientHTTPProxy;
import com.acertaininventorymanager.client.InvManagerClientConstants;
import com.acertaininventorymanager.interfaces.CustomerTransactionManager;
import com.acertaininventorymanager.server.CtmHTTPServer;
import com.acertaininventorymanager.server.IdmHTTPServer;
import com.acertaininventorymanager.tests.RPCtests;
import com.acertaininventorymanager.utils.InventoryManagerException;

public class AtomicityTests {

	public final static Random randGen = new Random();
	public final static int NUM_OF_IDM=5;
	public final static int RANDOMINT_BOUND = 1000;
	public final static int NUM_OF_CUSTOMERS = 20;
	public final static Set<Integer> REGIONS = new HashSet<Integer>(Arrays.asList(1, 2, 3));
	public final static int ITERATIONS = 100;
	
	private static ClientHTTPProxy client;
	private static CustomerTransactionManager ctm;
	private static Set<Customer> customers;
	private static int[] itemIds1 = {2026,2027,2028,2029};
	private static int[] itemIds2 = {1890,1891,1892,1893};
	private static int fixedUnitPrice = 15, fixedQuantity = 7;
	private static Set<ItemPurchase> fixedPurchases1, fixedPurchases2;

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
		//////////Start the IDM Servers /////////
			for (int i=1; i<=NUM_OF_IDM; i++){
				int portNumber = InvManagerClientConstants.DEFAULT_PORT + i;
	        	String portNumberS = String.valueOf(portNumber);
				new Thread() {
				    public void run() {
				        try {
				        	String[] args = {portNumberS};
							IdmHTTPServer.main(args);
						} catch (Exception e) {
							e.printStackTrace();
						}
				    }
				}.start();
		}
		///////// 
		customers = RPCtests.createSetOfCustomers(NUM_OF_CUSTOMERS, REGIONS);
		client.addCustomers(customers);
		initializePurchasesForConcurrentAddRemoval();

	}

	/**Helper function.
	 * The sets of purchases '1' and '2' have a fixed quantity and unit price for the given itemids.*/
	private static void initializePurchasesForConcurrentAddRemoval(){
		fixedPurchases1 = new HashSet<>();
		fixedPurchases2 = new HashSet<>();
		for (int itemId : itemIds1){
			int orderId = RPCtests.randGen.nextInt(RANDOMINT_BOUND)+1;
			Customer[] theCustomers = customers.toArray(new Customer[0]);
			int customerId =  theCustomers[randGen.nextInt(theCustomers.length)].getCustomerId();
			int quantity = fixedQuantity;
			int unitPrice = fixedUnitPrice;
			fixedPurchases1.add(new ItemPurchase(orderId, customerId, itemId, quantity, unitPrice));
		}
		for (int itemId : itemIds2){
			int orderId = RPCtests.randGen.nextInt(RANDOMINT_BOUND)+1;
			Customer[] theCustomers = customers.toArray(new Customer[0]);
			int customerId =  theCustomers[randGen.nextInt(theCustomers.length)].getCustomerId();
			int quantity = fixedQuantity;
			int unitPrice = fixedUnitPrice;
			fixedPurchases2.add(new ItemPurchase(orderId, customerId, itemId, quantity, unitPrice));

		}
	}

	/**Initialization before every test: Add the fixed purchases a number of times >= the number of thread iterations.*/
	@Before
	public void setUp() throws Exception {

		for (int i=0; i<ITERATIONS; i++){
			client.processOrders(fixedPurchases1);
			client.processOrders(fixedPurchases2);
		}
		return;
	}


	/**In this test: 
	 * Thread1 and Thread2 add concurrently the sets of fixed purchases '1' and '2'.
	 * The total value bought across all regions will be either == (the sumValue of all the items that were bought), 
	 * or == sumValue - integer*transactionValue.
	 * (Taking into account that some transactions may need to be rolled back due to deadlocks,
	 * the total will not be necessarily equal to the sum of all the prices*quantity).
	 * */
	@Test
	public void testConcurrentAdd1Add2() {

		List<RegionTotal> oldRegionTotals = new ArrayList<>();
		long oldSum=0, newSum=0;
		try {
			oldRegionTotals = ctm.getTotalsForRegions(REGIONS);
			oldSum = getTotalOfRegionTotals(oldRegionTotals);
		} catch (InventoryManagerException e) {
			e.printStackTrace();
			fail();
		}
		
		Thread adder1 = new Thread(){
			public void run() {
			for (int i=0; i<ITERATIONS; i++){
				try {
					System.out.println("THREAD n:1 , iteration:" + i);
					client.processOrders(fixedPurchases1);
				} catch (InventoryManagerException e) {
					e.printStackTrace();
				}
			}
				
			}
		};
		
		
		Thread adder2 = new Thread(){
			public void run() {
				for (int i=0; i<ITERATIONS; i++){
					try {
						System.out.println("THREAD n:2 , iteration:" + i);
						client.processOrders(fixedPurchases2);
					} catch (InventoryManagerException e) {
						e.printStackTrace();
					}
				}	
			}
		};
		
		
		adder1.start();
		adder2.start();
		
		try {
			adder1.join();
			adder2.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		List<RegionTotal> newRegionTotals = new ArrayList<>();
		try {
			newRegionTotals = ctm.getTotalsForRegions(REGIONS);
			newSum = getTotalOfRegionTotals(newRegionTotals);
		} catch (InventoryManagerException e) {
			e.printStackTrace();
			fail();
		}
				
		int dueDifference = itemIds1.length * fixedQuantity * fixedUnitPrice;
		
		assertTrue ( (newSum - oldSum) % dueDifference == 0 );
			
	}
	
	
	private long getTotalOfRegionTotals(List<RegionTotal> regionTotals){
		long sum=0;
		for (RegionTotal regTot : regionTotals){
			sum = sum + regTot.getTotalValueBought();
		}
		return sum;
	}

}
