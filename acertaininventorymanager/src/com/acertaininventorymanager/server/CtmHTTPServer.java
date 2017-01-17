package com.acertaininventorymanager.server;

import java.util.HashSet;
import java.util.Set;

import com.acertaininventorymanager.utils.InventoryConstants;
import com.acertaininventorymanager.business.Customer;
import com.acertaininventorymanager.business.CustomerTransactionsHandler;
import com.acertaininventorymanager.client.InvManagerClientConstants;

/**
 * Starts the {@link CtmHTTPServer} that the clients will communicate
 * with.
 */
public class CtmHTTPServer {

	/**
	 * Prevents the instantiation of a new {@link CtmHTTPServer}.
	 */
	private CtmHTTPServer() {
		// Prevent instances from being created.
	}

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments; 
	 *            args[0]=number of IDMS associated with a CTM. 
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		int listenOnPort = InvManagerClientConstants.DEFAULT_PORT;
		int numOfAssociatedIDMs = Integer.parseInt(args[0]);
		Set<Customer> emptyCustomersSet = new HashSet<Customer>();
		CustomerTransactionsHandler theCth = new CustomerTransactionsHandler(numOfAssociatedIDMs, emptyCustomersSet);
		
		
		CtmHTTPMessageHandler handler = new CtmHTTPMessageHandler(theCth);
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
