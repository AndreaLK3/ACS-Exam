package com.acertaininventorymanager.client.workloads;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.acertaininventorymanager.business.Customer;
import com.acertaininventorymanager.business.ItemPurchase;

/**
 * Helper class to generate random ItemPurchases and Customers
 */
public class ElementsGenerator {

	public static Random randGen = new Random();

	public final static int RANDOMINT_BOUND = 10000;

	public ElementsGenerator() {
		
	}

	/**
	 * Returns num randomly selected isbns from the input set
	 * 
	 * @param num
	 * @return
	 */
	public Set<Integer> sampleFromSetOfISBNs(Set<Integer> isbns, int num) {
		Integer[] isbn_array = isbns.toArray(new Integer[0]);
		Set<Integer> picked_isbns = new HashSet<Integer>();	
		
		num = Math.min(num, isbns.size());
		
		for (int i = 0; i<num; i++) {
				int index = randGen.nextInt(isbn_array.length);
				picked_isbns.add(isbn_array[index]);
			}
		
		return picked_isbns;
	}

	/**
	 * Return num stock books. For now return an ImmutableStockBook
	 * 
	 * @param num
	 * @return
	 *//*
	public Set<StockBook> nextSetOfStockBooks(int num) {
		Set<StockBook> books = new HashSet<StockBook>();
	
		for (int i=0; i< num; i++){
			int randomIsbn = randGen.nextInt(ISBNRANGE);
			
			int randomNumOfCopies = randGen.nextInt(ANUMBEROFCOPIES);
			Boolean randomEditorPick = randGen.nextBoolean();
			
			StockBook book = new ImmutableStockBook(randomIsbn, "Pride and Prejudice" + randomIsbn,"Jane Austen", 
													10, randomNumOfCopies, 0,0,0, randomEditorPick); 
			books.add(book);
		}
		
		return books;
	}*/
	
	/**Creates a random item purchase, owned by the specified registered customer.*/
	public static ItemPurchase createRandomItemPurchase(Customer aCustomer){
		int cID = aCustomer.getCustomerId();
		int cReg = aCustomer.getRegionId();
		int orderID = randGen.nextInt(RANDOMINT_BOUND)+1, itemID = randGen.nextInt(RANDOMINT_BOUND)+1;
		int quantity =  randGen.nextInt(RANDOMINT_BOUND)+1, unitPrice = randGen.nextInt(RANDOMINT_BOUND)+1;
		ItemPurchase aPurchase = new ItemPurchase(orderID, cID, itemID, quantity, unitPrice);
		return aPurchase;
	}

	/**Creates a set of customers, with cID in [1,RANDOMINT_BOUND] and 
	 * belonging to one region chosen at random.
	 * @param numOfCustomers 
	 * @param regions */
	public static Set<Customer> createSetOfCustomers(int numOfCustomers, Set<Integer> regions){
		Set<Customer> setOfCustomers = new HashSet<>();
		
		for (int i=1; i<=numOfCustomers; i++){
			int cId = randGen.nextInt(RANDOMINT_BOUND)+1;
			int cReg = randGen.nextInt(regions.size())+1;
			Customer c = new Customer(cId, cReg);
			setOfCustomers.add(c);
		}
		return setOfCustomers;
	}
	
	/**Given a set of customers, creates a set of item purchases.
	 * The customerID belongs to one of the customers, while the purchase features
	 * (orderID, itemID, quantity, unit price) are chosen at random.*/
	public static Set<ItemPurchase> createSetOfItemPurchases(Set<Customer> customers, Integer itemsPerCustomer){
		Set<ItemPurchase> setOfPurchases = new HashSet<>();
		
		for (Customer c : customers){
			for (int i=1; i<=itemsPerCustomer; i++){
				int orderID = randGen.nextInt(RANDOMINT_BOUND)+1;
				int itemID = randGen.nextInt(RANDOMINT_BOUND)+1;
				int quantity = randGen.nextInt(RANDOMINT_BOUND)+1;
				int price = randGen.nextInt(RANDOMINT_BOUND)+1;
				
				setOfPurchases.add(new ItemPurchase(orderID, c.getCustomerId(), itemID, quantity, price));
			}
		}
		return setOfPurchases;
	}
	
	

	/**Overloaded method: 
	 * Given a set customer ids (Integer), creates a set of item purchases.
	 * The customerID belongs to one of the customers, while the purchase features
	 * (orderID, itemID, quantity, unit price) are chosen at random.*/
	public static Set<ItemPurchase> createSetOfItemPurchases(Set<Integer> customerIDs, Integer numOfCustomers, Integer itemsPerCustomer){
		Set<ItemPurchase> setOfPurchases = new HashSet<>();
		List<Integer> customerIDsToUse;
		
		List<Integer> listOfCustomerIDs = new ArrayList<>();
		listOfCustomerIDs.addAll(customerIDs);
		Collections.shuffle(listOfCustomerIDs);
		customerIDsToUse = listOfCustomerIDs.subList(0, numOfCustomers-1);
		
		
		for (Integer customerID : customerIDsToUse){
			for (int i=1; i<=itemsPerCustomer; i++){
				int orderID = randGen.nextInt(RANDOMINT_BOUND)+1;
				int itemID = randGen.nextInt(RANDOMINT_BOUND)+1;
				int quantity = randGen.nextInt(RANDOMINT_BOUND)+1;
				int price = randGen.nextInt(RANDOMINT_BOUND)+1;
				
				setOfPurchases.add(new ItemPurchase(orderID, customerID, itemID, quantity, price));
			}
		}
		return setOfPurchases;
	}
	
	
	
}
