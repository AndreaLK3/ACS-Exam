package com.acertaininventorymanager.tests;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.acertaininventorymanager.business.Customer;
import com.acertaininventorymanager.business.CustomerTransactionsService;
import com.acertaininventorymanager.interfaces.CustomerTransactionManager;
import com.acertaininventorymanager.business.ItemPurchase;

public class CTMbasicTests {
	
	public final static int NUM_OF_IDM=5;
	public final static int NUM_OF_CUSTOMERS = 20;
	public final static int ITEMS_PER_CUSTOMER = 10;
	public final static int[] REGIONS = {0,1,2} ;
	public final static int RANDOMINT_BOUND = 1000;
	
	Random randGen = new Random();
	
	private CustomerTransactionManager ctm;
	private boolean localTest = true;


	@Before
	public void setUp() throws Exception {
		ctm = new CustomerTransactionsService(NUM_OF_IDM);
		Set<Customer> customers = createSetOfCustomers();
		Set<ItemPurchase> purchases = createSetOfItemPurchases(customers);
		ctm.processOrders(purchases);
		
	}

	/**Helper function: creates a set of customers, with cID in [0,999] and 
	 * belonging to one region chosen at random.*/
	private Set<Customer> createSetOfCustomers(){
		Set<Customer> setOfCustomers = new HashSet<>();
		
		for (int i=1; i<=NUM_OF_CUSTOMERS; i++){
			int cId = randGen.nextInt(1000);
			int cReg = randGen.nextInt(REGIONS.length);
			Customer c = new Customer(cId, cReg);
			setOfCustomers.add(c);
		}
		return setOfCustomers;
	}
	
	/**Helper function: given a set of customers, creates a set of item purchases.
	 * The customerID belongs to one of the customers, while the purchase data
	 * (orderID, itemID, quantity, unit price) are chosen at random.*/
	private Set<ItemPurchase> createSetOfItemPurchases(Set<Customer> customers){
		Set<ItemPurchase> setOfPurchases = new HashSet<>();
		
		for (Customer c : customers){
			for (int i=1; i<=ITEMS_PER_CUSTOMER; i++){
				int orderID = randGen.nextInt(RANDOMINT_BOUND);
				int itemID = randGen.nextInt(RANDOMINT_BOUND);
				int quantity = randGen.nextInt(RANDOMINT_BOUND);
				int price = randGen.nextInt(RANDOMINT_BOUND);
				
				setOfPurchases.add(new ItemPurchase(orderID, c.getCustomerId(), itemID, quantity, price));
			}
		}
		return setOfPurchases;
	}
	
	

	@Test
	public void test() {
		fail("Not yet implemented");
	}

}
