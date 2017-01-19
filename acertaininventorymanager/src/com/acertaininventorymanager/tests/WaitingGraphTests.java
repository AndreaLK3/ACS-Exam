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

	
	public void testDetectDeadlock() {
		createDeadlockGraph();
		assertTrue(waitGraphManager.isThereADeadlock(waitGraphManager.getMapOfAdjLists()) instanceof Integer);
	}
	
	
	public void testSeeNoDeadlock(){
		createGraphWithoutDeadlocks();
		assertTrue(waitGraphManager.isThereADeadlock(waitGraphManager.getMapOfAdjLists()) == null);
	}
	
	@Test
	public void testFlagTransactions() throws InterruptedException{
			new Thread(){ 
				public void run() {
					try {
						lockManager.tryToAcquireLock(1, 101, LockType.WRITELOCK);
						Thread.sleep(100);
						lockManager.tryToAcquireLock(1, 102, LockType.WRITELOCK);
						lockManager.releaseLock(1, 101);
						lockManager.releaseLock(1, 102);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}.start();
		
			new Thread(){ 
				public void run() {
					try {
						lockManager.tryToAcquireLock(2, 102, LockType.WRITELOCK);
						Thread.sleep(100);
						lockManager.tryToAcquireLock(2, 103, LockType.WRITELOCK);
						lockManager.releaseLock(2, 102);
						lockManager.releaseLock(2, 103);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}.start();
			new Thread(){ 
				public void run() {
					try {
						lockManager.tryToAcquireLock(3, 103, LockType.WRITELOCK);
						Thread.sleep(100);
						lockManager.tryToAcquireLock(3, 101, LockType.WRITELOCK);
						lockManager.releaseLock(3, 103);
						lockManager.releaseLock(3, 101);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}.start();
			Thread.sleep(2000);
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
