package com.acertaininventorymanager.server;

import java.util.HashSet;
import java.util.Set;

import com.acertaininventorymanager.utils.InventoryConstants;
import com.acertaininventorymanager.business.Customer;
import com.acertaininventorymanager.business.CustomerTransactionsHandler;

/**
 * Starts the {@link InvManagerHTTPServer} that the clients will communicate
 * with.
 */
public class InvManagerHTTPServer {

	/** The Constant defaultListenOnPort. */
	private static final int DEFAULT_PORT = 8081;

	/**
	 * Prevents the instantiation of a new {@link InvManagerHTTPServer}.
	 */
	private InvManagerHTTPServer() {
		// Prevent instances from being created.
	}

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments; 
	 *            args[0]=number of IDMS associated with a CTM. 
	 */
	public static void main(String[] args) {
		
		int listenOnPort = DEFAULT_PORT;
		int numOfAssociatedIDMs = Integer.parseInt(args[0]);
		Set<Customer> emptyCustomersSet = new HashSet<Customer>();
		CustomerTransactionsHandler theCth = new CustomerTransactionsHandler(numOfAssociatedIDMs, emptyCustomersSet);
		
		
		InventoryHTTPMessageHandler handler = new InventoryHTTPMessageHandler(theCth);
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
