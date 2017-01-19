/**
 * 
 */
package com.acertaininventorymanager.client.workloads;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.acertaininventorymanager.business.Customer;
import com.acertaininventorymanager.client.ClientHTTPProxy;
import com.acertaininventorymanager.client.InvManagerClientConstants;
import com.acertaininventorymanager.interfaces.CustomerTransactionManager;
import com.acertaininventorymanager.server.CtmHTTPServer;
import com.acertaininventorymanager.server.IdmHTTPServer;
import com.acertaininventorymanager.utils.EmptyRegionException;
import com.acertaininventorymanager.utils.InventoryManagerException;
import com.acertaininventorymanager.utils.NonPositiveIntegerException;

import jdk.nashorn.internal.runtime.regexp.joni.Config;

/**
 * 
 * CertainWorkload class runs the workloads by different workers concurrently.
 * It configures the environment for the workers using WorkloadConfiguration
 * objects and reports the metrics
 * 
 */
public class CertainWorkload {

	private static final int numOfRegisteredCustomers = 100;
	private static final int NUM_OF_IDM = 5;
	private static final int NUM_OF_REGIONS = 10;
	private static Set<Customer> customers ;
	
	public static Random ourRandGen = new Random();
	public static List<Double> listOfThroughputs = new ArrayList<>();
	public static List<Double> listOfLatencies = new ArrayList<>();
	public static List<Integer> numberOfOpsCompleted = new ArrayList<>();

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		int numConcurrentWorkloadThreads = 1;
		cleanLogFile();

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
		//while (numConcurrentWorkloadThreads <= 10) {
			String serverAddress = "http://localhost:8081";
			List<WorkerRunResult> workerRunResults = new ArrayList<WorkerRunResult>();
			List<Future<WorkerRunResult>> runResults = new ArrayList<Future<WorkerRunResult>>();
			
			ClientHTTPProxy ctm;
			
			ctm = new ClientHTTPProxy(serverAddress);
			
			// Initialize the registered customers, so that no InexistentCustomerException are thrown
			Set<Integer> customerIDs = initializeCustomers(ctm);

			ExecutorService exec = Executors.newFixedThreadPool(numConcurrentWorkloadThreads);

			for (int i = 0; i < numConcurrentWorkloadThreads; i++) {
				WorkloadConfiguration config = new WorkloadConfiguration(ctm);
				Worker workerTask = new Worker(config, ctm, customerIDs, NUM_OF_REGIONS);
				// Keep the futures to wait for the result from the thread
				runResults.add(exec.submit(workerTask));
			}

			// Get the results from the threads using the futures returned
			for (Future<WorkerRunResult> futureRunResult : runResults) {
				WorkerRunResult runResult = futureRunResult.get(); // blocking
																	// call
				workerRunResults.add(runResult);
			}

			//System.out.println("numConcurrentWorkloadThreads: " + numConcurrentWorkloadThreads);
			
			//writeOnFile("\n"+numberOfOpsCompleted.toString()+"\n");

			exec.shutdownNow(); // shutdown the executor

			// Finished initialization, stop the client
			((ClientHTTPProxy) ctm).stop();

			reportMetric(workerRunResults);
			//numConcurrentWorkloadThreads++;
		//whileEnd}
		System.out.println("done");
	}

	/**
	 * Computes the metrics and prints them
	 * 
	 * @param workerRunResults
	 */
	public static void reportMetric(List<WorkerRunResult> workerRunResults) {
		double aggregateThroughput;
		double averageLatency;

		aggregateThroughput = computeAggregateThroughput(workerRunResults);
		averageLatency = computeAverageLatency(workerRunResults);

		numberOfOpsCompleted.add(-1);
		writeOnFile(workerRunResults.size()+","+aggregateThroughput+","+averageLatency+"\n");
		
		// System.out.println("The aggregate throughput is: " +
		// approxNumber(aggregateThroughput, 3)
		// + " interactions/second");
		// System.out.println("The average latency is: " +
		// approxNumber(averageLatency, 6) + " seconds");

	}

	private static void addToListOfResults(double aggregateThroughput, double averageLatency) {
		listOfThroughputs.add(aggregateThroughput);
		listOfLatencies.add(averageLatency);
	}
	
	private static void writeResultsToFile(int numOfConcurrentClients){
			int i = numOfConcurrentClients-1;
			Double t = listOfThroughputs.get(i);
			Double l = listOfLatencies.get(i);
			writeOnFile(i+","+t+","+l+"\n");

	}

	public static double computeAggregateThroughput(List<WorkerRunResult> workerRunResults) {
		double totTimeInS = 0;
		double aggregateThroughput = 0;

		for (WorkerRunResult wResult : workerRunResults) {
			int succCustomerInts = wResult.getSuccessfulInteractions();
			long timeInNs = wResult.getElapsedTimeInNanoSecs();
			double timeInS = timeInNs / Math.pow(10, 9);
			totTimeInS += timeInS;
			double workerAvgThroughput = succCustomerInts / timeInS;
			aggregateThroughput += workerAvgThroughput;
		}

		return approxNumber(aggregateThroughput, 5);
	}

	public static double computeAverageLatency(List<WorkerRunResult> workerRunResults) {
		double totTimeInS = 0;
		double averageLatency = 0;
		double timeInS;
		long timeInNs=0;
		int succCustomerInts=0;
		double workerLatency;

		for (WorkerRunResult wResult : workerRunResults) {
			succCustomerInts = wResult.getSuccessfulFrequentBookStoreInteractionRuns();
			timeInNs = wResult.getElapsedTimeInNanoSecs();
			timeInS = timeInNs / Math.pow(10, 9);
			
			workerLatency = timeInS / succCustomerInts;
			averageLatency += workerLatency;
		}
		averageLatency = averageLatency / workerRunResults.size();
		//writeOnDebugFile("succCustomerInts: "+ succCustomerInts + " . timeInNs: " + timeInNs + "\n");

		return approxNumber(averageLatency, 5);
	}

	
	public static Set<Integer> initializeCustomers(ClientHTTPProxy ctm) throws NonPositiveIntegerException, EmptyRegionException, InventoryManagerException{
	
		Set<Integer> regions = new HashSet<Integer>();
		for (int i = 1; i<=NUM_OF_REGIONS; i++){
			regions.add(i);
		}
		customers = ElementsGenerator.createSetOfCustomers(numOfRegisteredCustomers, regions);
		ctm.addCustomers(customers);
		Set<Integer> customerIDs = new HashSet<Integer>();
		for(Customer c : customers){
			customerIDs.add(c.getCustomerId());
		}		
		return customerIDs;
	}

	/**
	 * Returns a Stockbook with the given ISBN and number of copies; the title
	 * is picked at random in a set of possible titles; the editor pick
	 * (true/false) is random.
	 * 
	 * @param isbn
	 * @param copies
	 * @return
	 *//*
	private static StockBook getABook(int isbn, int copies) {
		Boolean randomEditorPick = ourRandGen.nextBoolean();

		List<String> possibleTitles = new ArrayList<>();
		possibleTitles.add("War and Peace");
		possibleTitles.add("This is London");
		possibleTitles.add("The way we live now");
		possibleTitles.add("Dissertations over the different fables in different regions of the world");
		possibleTitles.add("Leaf and stream");

		String randomTitle = possibleTitles.get(ourRandGen.nextInt(possibleTitles.size()));

		return new ImmutableStockBook(isbn, randomTitle, "Jonathan Livingstone", (float) 10, copies, 0, 0, 0,
				randomEditorPick);
	}*/

	private static double approxNumber(Double num, int numOfDecimals) {
		String numAsString = num.toString();
		int decimalsStart = numAsString.indexOf('.');
		String approxNumAsString = numAsString.substring(0,
				Math.min(decimalsStart + numOfDecimals + 1, numAsString.length()));
		double approxNum = Double.parseDouble(approxNumAsString);
		return approxNum;
	}

	private static void writeOnFile(String msg) {
		Charset charset = Charset.forName("US-ASCII");
		StandardOpenOption[] opts = { StandardOpenOption.CREATE, StandardOpenOption.APPEND };
		Path filePath = Paths.get("testfile.csv");
		try (BufferedWriter writer = Files.newBufferedWriter(filePath, charset, opts)) {
			writer.write(msg, 0, msg.length());
		} catch (IOException x) {
			System.err.format("IOException: %s%n", x);
		}
	}
	
	private static void writeOnDebugFile(String msg) {
		Charset charset = Charset.forName("US-ASCII");
		StandardOpenOption[] opts = { StandardOpenOption.CREATE, StandardOpenOption.APPEND };
		Path filePath = Paths.get("ourDebugging.csv");
		try (BufferedWriter writer = Files.newBufferedWriter(filePath, charset, opts)) {
			writer.write(msg, 0, msg.length());
		} catch (IOException x) {
			System.err.format("IOException: %s%n", x);
		}
	}

	private static void cleanLogFile() {
		Path fp = Paths.get("testFile.csv");
		if (Files.exists(fp)) {
			try {
				Files.delete(fp);
				new File("testFile.csv");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public int getNumOfRegisteredCustomers() {
		return numOfRegisteredCustomers;
	}


}
