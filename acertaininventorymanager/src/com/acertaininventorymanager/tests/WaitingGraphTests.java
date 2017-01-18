package com.acertaininventorymanager.tests;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.acertaininventorymanager.business.LockType;
import com.acertaininventorymanager.business.Strict2PLManager;
import com.acertaininventorymanager.business.WaitingGraphManager;
import com.acertaininventorymanager.utils.AbortedTransactionException;

public class WaitingGraphTests {

	public static WaitingGraphManager waitGraphManager;
	public static Strict2PLManager lockManager;

	@Before
	public void setUp() throws Exception {
		lockManager = new Strict2PLManager();
		waitGraphManager = new WaitingGraphManager(lockManager);
	}

	@Test
	public void testDetectDeadlock() {
		createDeadlockGraph();
		assertTrue(waitGraphManager.isThereADeadlock(waitGraphManager.getMapOfAdjLists()) instanceof Integer);
	}
	
	@Test
	public void testSeeNoDeadlock(){
		createGraphWithoutDeadlocks();
		assertTrue(waitGraphManager.isThereADeadlock(waitGraphManager.getMapOfAdjLists()) == null);
	}
	
	@Test
	public void testFlagTransactions() throws InterruptedException{
			new Thread(){ 
				public void run() {
					try {
						lockManager.tryToAcquireLock(1, 100, LockType.WRITELOCK);
						Thread.sleep(3000);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}.start();
			new Thread(){ 
				public void run() {
					try {
						lockManager.tryToAcquireLock(2, 101, LockType.WRITELOCK);
						Thread.sleep(3000);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}.start();
			new Thread(){ 
				public void run() {
					try {
						lockManager.tryToAcquireLock(3, 100, LockType.WRITELOCK);
						Thread.sleep(3000);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}.start();
		assertTrue(checkFlaggedTransactions());
	
	}
		
	private boolean checkFlaggedTransactions(){
		 ConcurrentLinkedQueue<Integer> stoppedTransactions = lockManager.getAbortedXactIds();
		 return (stoppedTransactions.contains(1) || 
				    stoppedTransactions.contains(2) ||
				    stoppedTransactions.contains(3));
	}
	
	private void createDeadlockGraph(){
		waitGraphManager.addEdges(1, new HashSet<Integer>(Arrays.asList(2, 3)));
		waitGraphManager.addEdges(2, new HashSet<Integer>(Arrays.asList(4)));
		waitGraphManager.addEdges(4, new HashSet<Integer>(Arrays.asList(5)));
		waitGraphManager.addEdges(4, new HashSet<Integer>(Arrays.asList(2)));
	}
	
	private void createGraphWithoutDeadlocks(){
		waitGraphManager.addEdges(1, new HashSet<Integer>(Arrays.asList(2, 3)));
		waitGraphManager.addEdges(2, new HashSet<Integer>(Arrays.asList(4,5,6)));
		waitGraphManager.addEdges(5, new HashSet<Integer>(Arrays.asList(10)));
	}

}
