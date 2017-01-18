package com.acertaininventorymanager.tests;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.acertaininventorymanager.business.Strict2PLManager;
import com.acertaininventorymanager.business.WaitingGraphManager;

public class WaitingGraphTests {

	public static WaitingGraphManager waitGraphManager;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		waitGraphManager = new WaitingGraphManager(new Strict2PLManager());
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
