package com.acertaininventorymanager.tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.acertaininventorymanager.business.Customer;
import com.acertaininventorymanager.business.CustomerTransactionsHandler;
import com.acertaininventorymanager.interfaces.CustomerTransactionManager;
import com.acertaininventorymanager.server.CtmHTTPServer;
import com.acertaininventorymanager.server.IdmHTTPServer;
import com.acertaininventorymanager.utils.EmptyRegionException;
import com.acertaininventorymanager.utils.InventoryManagerException;
import com.acertaininventorymanager.utils.NonPositiveIntegerException;
import com.acertaininventorymanager.business.ItemPurchase;
import com.acertaininventorymanager.business.RegionTotal;
import com.acertaininventorymanager.client.ClientHTTPProxy;
import com.acertaininventorymanager.client.InvManagerClientConstants;

public class RPCtests {
	
	public static Random randGen = new Random();
	public final static int NUM_OF_IDM=5;
	public final static int NUM_OF_CUSTOMERS = 20;
	public final static int ITEMS_PER_CUSTOMER = 10;
	public final static Set<Integer> REGIONS = new HashSet<Integer>(Arrays.asList(1, 2, 3));
	public final static int RANDOMINT_BOUND = 1000;
	
	private static boolean localTest = false;
	
	private static ClientHTTPProxy client;
	private static CustomerTransactionManager ctm;
	private static Set<Customer> customers;

	/**Before-class pre-initialization:
	 * If we are in the remote Setting, we need to create&start the Client Proxy and the Server.*/
	@BeforeClass
	public static void setUpBeforeClass() throws Exception{
		if (localTest == false){
			try {
				client = new ClientHTTPProxy("http://localhost:8081");
				ctm = client;
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			//////////The CTM Server thread /////////
			Thread t = new Thread() {
			    public void run() {
			        try {
			        	String[] args = {String.valueOf(NUM_OF_IDM)};
						CtmHTTPServer.main(args);
					} catch (Exception e) {
						e.printStackTrace();
					}
			    }
			};
			t.start();
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
	
		}
	}
	
	
	/**Initialization before every test: 
	 * in the Local Setting: we create an instance of the CustomerTransactionManager,
	 * we create a random set of customers (n: we have a specified, limited number of regions),
	 * and we execute a random set of purchases. \n
	 * in the Remote setting: we send a request to the Server to removeAllCustomers,
	 * and then we add a new set of customers.*/
	@Before
	public void setUp() throws Exception {
		if (localTest == true){
			customers = createSetOfCustomers();
			ctm = new CustomerTransactionsHandler(NUM_OF_IDM, customers);
		}
		else {
			client.removeAllCustomers();
			customers = createSetOfCustomers();
			client.addCustomers(customers);
		}
		Set<ItemPurchase> purchases = createSetOfItemPurchases(customers);
		client.processOrders(purchases);
	}

	/**Helper function: creates a set of customers, with cID in [0,999] and 
	 * belonging to one region chosen at random.*/
	public static Set<Customer> createSetOfCustomers(){
		Set<Customer> setOfCustomers = new HashSet<>();
		
		for (int i=1; i<=NUM_OF_CUSTOMERS; i++){
			int cId = randGen.nextInt(1000)+1;
			int cReg = randGen.nextInt(REGIONS.size())+1;
			Customer c = new Customer(cId, cReg);
			setOfCustomers.add(c);
		}
		return setOfCustomers;
	}
	
	/**Helper function: given a set of customers, creates a set of item purchases.
	 * The customerID belongs to one of the customers, while the purchase data
	 * (orderID, itemID, quantity, unit price) are chosen at random.*/
	public static Set<ItemPurchase> createSetOfItemPurchases(Set<Customer> customers){
		Set<ItemPurchase> setOfPurchases = new HashSet<>();
		
		for (Customer c : customers){
			for (int i=1; i<=ITEMS_PER_CUSTOMER; i++){
				int orderID = randGen.nextInt(RANDOMINT_BOUND)+1;
				int itemID = randGen.nextInt(RANDOMINT_BOUND)+1;
				int quantity = randGen.nextInt(RANDOMINT_BOUND)+1;
				int price = randGen.nextInt(RANDOMINT_BOUND)+1;
				
				setOfPurchases.add(new ItemPurchase(orderID, c.getCustomerId(), itemID, quantity, price));
			}
		}
		return setOfPurchases;
	}
	
	

	/**This test checks the core functionality of CustomerTransactionManager.
	 * We pick a customer at random, and we create a purchase of nOfUnits * unitPrice.
	 * After the purchase, the totalValueBought for the region of the customer
	 * must have increased of nOfUnits * unitPrice.**/
	@Test
	public void testProcessOrders() {
		List<RegionTotal> oldRegionTotals = new ArrayList<>();
		try {
			oldRegionTotals = ctm.getTotalsForRegions(REGIONS);
		} catch (InventoryManagerException e) {
			e.printStackTrace();
			fail();
		}
		
		Customer aCustomer = customers.stream().findAny().get();
		int cRegID = aCustomer.getRegionId();
		ItemPurchase purchase1 = createRandomItemPurchase(aCustomer);
		int pricePerUnit = purchase1.getUnitPrice();
		int numberOfUnits = purchase1.getQuantity();
		Set<ItemPurchase> itPurchases = new HashSet<>(Arrays.asList(purchase1));
		
		try {
			ctm.processOrders(itPurchases);
			List<RegionTotal> newRegionTotals = ctm.getTotalsForRegions(REGIONS);
			RegionTotal oldTotal = getRegionTotal(cRegID, oldRegionTotals);
			RegionTotal newTotal = getRegionTotal(cRegID, newRegionTotals);
			
			assert(newTotal.getTotalValueBought() - oldTotal.getTotalValueBought() == pricePerUnit * numberOfUnits);
			
		} catch (InventoryManagerException e) {
			e.printStackTrace();
			fail();
		}
		
	}
	
	/**This negative test checks the validation phase of the CTM.
	 * If an integer < 0 is given as parameter, the NonPositiveIntegerException should occur.**/
	@Test
	public void testProcessInvalidOrders() {
				
		Customer aCustomer = customers.stream().findAny().get();
		ItemPurchase purchase1 = new ItemPurchase(2,-45, 2, 2, 2);

		Set<ItemPurchase> itPurchases = new HashSet<>(Arrays.asList(purchase1));
		
		try {
			ctm.processOrders(itPurchases);
			fail();
		} catch (InventoryManagerException e) {
			assertTrue(e instanceof NonPositiveIntegerException);
		}
		
	}
	
	
	/**Helper function.*/
	private ItemPurchase createRandomItemPurchase(Customer aCustomer){
		int cID = aCustomer.getCustomerId();
		int cReg = aCustomer.getRegionId();
		int orderID = randGen.nextInt(RANDOMINT_BOUND)+1, itemID = randGen.nextInt(RANDOMINT_BOUND)+1;
		int quantity =  randGen.nextInt(RANDOMINT_BOUND)+1, unitPrice = randGen.nextInt(RANDOMINT_BOUND)+1;
		ItemPurchase aPurchase = new ItemPurchase(orderID, cID, itemID, quantity, unitPrice);
		return aPurchase;
	}
	
	/**Helper function.*/
	public static RegionTotal getRegionTotal(Integer regID, List<RegionTotal> regionTotals){
		RegionTotal result = null;
		for (RegionTotal regTot : regionTotals){
			if (regTot.getRegionId()==regID)
				result = regTot;
		}
		return result;
	}
	
	
	

}
