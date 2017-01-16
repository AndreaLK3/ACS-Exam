package com.acertaininventorymanager.tests;

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.acertaininventorymanager.business.Customer;
import com.acertaininventorymanager.client.ClientHTTPProxy;
import com.acertaininventorymanager.server.InvManagerHTTPServer;
import com.acertaininventorymanager.utils.EmptyRegionException;
import com.acertaininventorymanager.utils.InventoryManagerException;
import com.acertaininventorymanager.utils.NonPositiveIntegerException;
import com.acertaininventorymanager.tests.CTMbasicTests;

public class RPCtests {

	private static final int NUM_OF_IDMS = 4;
	
	private static ClientHTTPProxy client;
	private static Set<Customer> customers;
	
	@BeforeClass
	public static void setUpBeforeClass() throws InventoryManagerException {
		try {

			client = new ClientHTTPProxy("http://localhost:8081");

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//////////The Server thread /////////
		Thread t = new Thread() {
		    public void run() {
		        try {
		        	String[] args = {String.valueOf(NUM_OF_IDMS)};
					InvManagerHTTPServer.main(args);
				} catch (Exception e) {
					e.printStackTrace();
				}
		    }
		};
		t.start();
		////////// 
		
		customers = CTMbasicTests.createSetOfCustomers();
		
		client.addCustomers(customers);
		
		
	}
	
	@Test
	public void test(){
		
	}
	
	@AfterClass
	public static void tearDownAfterClass() {
		client.stop();
	}

}
