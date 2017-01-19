/**
 * 
 */
package com.acertaininventorymanager.client.workloads;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import com.acertaininventorymanager.business.Customer;
import com.acertaininventorymanager.business.ItemPurchase;
import com.acertaininventorymanager.client.ClientHTTPProxy;
import com.acertaininventorymanager.interfaces.CustomerTransactionManager;
import com.acertaininventorymanager.utils.EmptyRegionException;
import com.acertaininventorymanager.utils.InexistentCustomerException;
import com.acertaininventorymanager.utils.InventoryManagerException;
import com.acertaininventorymanager.utils.NonPositiveIntegerException;

/**
 * 
 * Worker represents the workload runner which runs the workloads with
 * parameters using WorkloadConfiguration and then reports the results
 * 
 */
public class Worker implements Callable<WorkerRunResult> {
    private WorkloadConfiguration configuration = null;
    private int numSuccessfulFrequentBookStoreInteraction = 0;
    private int numTotalFrequentBookStoreInteraction = 0;
    
	private ClientHTTPProxy ctm;
	private Set<Integer> customerIDs;
	private Integer numOfRegions;
    
    // for now useless
    //private Comparator<StockBook> comparator;
    
    public Worker(WorkloadConfiguration config, ClientHTTPProxy theCtm, Set<Integer> registeredCustomersIDs
    				, Integer numOfRegions) {
    	configuration = config;
    	/*comparator = new Comparator<StockBook>() {
			@Override
			public int compare(StockBook b1, StockBook b2) {
				return b1.getNumCopies() - b2.getNumCopies();
			}
			
		};*/
		ctm = theCtm; 
		customerIDs = registeredCustomersIDs;
		this.numOfRegions = numOfRegions;
    }

    /**
     * Run the appropriate interaction while trying to maintain the configured
     * distributions
     * 
     * Updates the counts of total runs and successful runs for customer
     * interaction
     * 
     * @param chooseInteraction
     * @return
     */
    private boolean runInteraction(float chooseInteraction) {
	try {
	    float percentRareStockManagerInteraction = configuration.getPercentRareStockManagerInteraction();
	    float percentFrequentStockManagerInteraction = configuration.getPercentFrequentStockManagerInteraction();

	    if (chooseInteraction < percentRareStockManagerInteraction) {
					runRarePurchaseInteraction();
	    } else if (chooseInteraction < percentRareStockManagerInteraction
		    + percentFrequentStockManagerInteraction) {
		runFrequentPurchaseInteraction();
	    } else {
		numTotalFrequentBookStoreInteraction++;
		runRegionsLookupInteraction();
		numSuccessfulFrequentBookStoreInteraction++;
	    }
	} catch (Exception ex) {
	    return false;
	}
	return true;
    }


	private void runRegionsLookupInteraction() throws NonPositiveIntegerException, EmptyRegionException, InventoryManagerException {
		Set<Integer> regionIDs = new HashSet<>();
		for (Integer regId = 1; regId<= numOfRegions; regId++){
			regionIDs.add(regId);
		}
		ctm.getTotalsForRegions(regionIDs);
	}

	private void runFrequentPurchaseInteraction() throws NonPositiveIntegerException, InexistentCustomerException, InventoryManagerException {
		Set<ItemPurchase> ps = ElementsGenerator.createSetOfItemPurchases(customerIDs, configuration.getNumOfCustomersFrequent(),
													configuration.getNumOfItemsFrequent());
		ctm.processOrders(ps);		
	}

	private void runRarePurchaseInteraction() throws NonPositiveIntegerException, InexistentCustomerException, InventoryManagerException {
		Set<ItemPurchase> ps = ElementsGenerator.createSetOfItemPurchases(customerIDs, 
										configuration.getNumOfCustomersRare(),
										configuration.getNumOfItemsRare());
		ctm.processOrders(ps);		
	}

	/**
     * Run the workloads trying to respect the distributions of the interactions
     * and return result in the end
     */
    public WorkerRunResult call() throws Exception {
	int count = 1;
	long startTimeInNanoSecs = 0;
	long endTimeInNanoSecs = 0;
	int successfulInteractions = 0;
	long timeForRunsInNanoSecs = 0;

	Random rand = new Random();
	float chooseInteraction;

	// Perform the warmup runs
	while (count++ <= configuration.getWarmUpRuns()) {
	    chooseInteraction = rand.nextFloat() * 100f;
	    runInteraction(chooseInteraction);
	}

	count = 1;
	numTotalFrequentBookStoreInteraction = 0;
	numSuccessfulFrequentBookStoreInteraction = 0;

	// Perform the actual runs
	startTimeInNanoSecs = System.nanoTime();
	while (count++ <= configuration.getNumActualRuns()) {
	    chooseInteraction = rand.nextFloat() * 100f;
	    if (runInteraction(chooseInteraction)) {
		successfulInteractions++;
	    }
	}
	endTimeInNanoSecs = System.nanoTime();
	timeForRunsInNanoSecs += (endTimeInNanoSecs - startTimeInNanoSecs);
	return new WorkerRunResult(successfulInteractions, timeForRunsInNanoSecs, configuration.getNumActualRuns(),
		numSuccessfulFrequentBookStoreInteraction, numTotalFrequentBookStoreInteraction);
    }

    /**
     * Runs the new stock acquisition interaction
     * 
     * @throws BookStoreException
     *//*
    private void runRareStockManagerInteraction(int num) throws BookStoreException {

    	
    	List<StockBook> bookList = new ArrayList<StockBook>();
    	Set<StockBook> booksThatCanBeAdded = new HashSet<StockBook>();
    	bookList = storeManager.getBooks();
    	
    	BookSetGenerator bookGen = new BookSetGenerator();
    	Set<StockBook> newBooks = bookGen.nextSetOfStockBooks(num);
    	
    	for(StockBook book : newBooks){
    		Boolean alreadyPresent = false;
    		for (StockBook storeBook : bookList){
    			if (book.getISBN()==storeBook.getISBN()){
    				alreadyPresent = true;
    			}
    		}
    		if (!alreadyPresent){
    			booksThatCanBeAdded.add(book);
    		}
    	}
    	
    	storeManager.addBooks(booksThatCanBeAdded);
    }

    *//**
     * Runs the stock replenishment interaction
     * 
     * @throws BookStoreException
     *//*
    private void runFrequentStockManagerInteraction(int k, int numCopies) throws BookStoreException {
    	
    	List<StockBook> bookList = storeManager.getBooks();
    	Set<BookCopy> copySet = new HashSet<BookCopy>();
    	
    	k = Math.min(k, bookList.size());
    	bookList.sort(comparator);
    	List<StockBook> klist = bookList.stream().
    		sorted(comparator).
    		limit(k).
    		collect(Collectors.toList());
    	
    	for(StockBook b : klist) {
    		copySet.add(new BookCopy(b.getISBN(), numCopies));
    	}
    	
    	storeManager.addCopies(copySet);
    }

    *//**
     * Runs the customer interaction
     * 
     * @throws BookStoreException
     *//*
    private void runFrequentBookStoreInteraction(int numBook, int numCopies) throws BookStoreException {
    	
    	List<Book> retrievedBooksList = client.getEditorPicks(numBook);
    	BookSetGenerator bookGen = new BookSetGenerator();
    	Set<Integer> isbns = new HashSet<>();
    	
    	for(Book book:retrievedBooksList){
    		isbns.add(book.getISBN());
    	}
    	Set<Integer> newBooks = bookGen.sampleFromSetOfISBNs(isbns, configuration.getNumBooksToBuy());
    	
    	Set<BookCopy> copySet = new HashSet<BookCopy>();
    	for (int isbn:newBooks){
    		copySet.add(new BookCopy(isbn, numCopies));
    	}
    	
    	client.buyBooks(copySet);
    
    }*/

}
