package com.acertaininventorymanager.server;


import java.util.HashSet;
import java.util.Set;

import com.acertaininventorymanager.utils.InventoryConstants;
import com.acertaininventorymanager.business.Customer;
import com.acertaininventorymanager.business.CustomerTransactionsHandler;
import com.acertaininventorymanager.business.ItemDataHandler;
import com.acertaininventorymanager.client.InvManagerClientConstants;

/**
 * Starts the {@link CtmHTTPServer} that the clients will communicate
 * with.
 */
public class IdmHTTPServer {


	/**
	 * Prevents the instantiation of a new {@link CtmHTTPServer}.
	 */
	private IdmHTTPServer() {
		// Prevent instances from being created.
	}

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments; 
	 *            args[0]=IMPORTANT: the port the server listens on. 
	 */
	public static void main(String[] args) {
		
		int listenOnPort = Integer.parseInt(args[0]);
		
		ItemDataHandler theIdm = new ItemDataHandler();
		
		
		IdmHTTPMessageHandler handler = new IdmHTTPMessageHandler(theIdm);
		String serverPortString = System.getProperty(InventoryConstants.PROPERTY_KEY_SERVER_PORT);

		if (serverPortString != null) {
			try {
				listenOnPort = Integer.parseInt(serverPortString);
			} catch (NumberFormatException ex) {
				System.err.println("Unsupported message tag");
			}
		}

		if (InvManagerHTTPServerUtility.createServer(listenOnPort, handler)) {
			System.out.println("Server started.");
		}
	}
}