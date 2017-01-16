package com.acertaininventorymanager.client;

import java.util.List;
import java.util.Set;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import com.acertaininventorymanager.utils.InventoryKryoSerializer;
import com.acertaininventorymanager.utils.InventoryXStreamSerializer;
import com.acertaininventorymanager.business.ItemPurchase;
import com.acertaininventorymanager.business.RegionTotal;
import com.acertaininventorymanager.interfaces.CustomerTransactionManager;
import com.acertaininventorymanager.interfaces.InventorySerializer;
import com.acertaininventorymanager.utils.EmptyRegionException;
import com.acertaininventorymanager.utils.InexistentCustomerException;
import com.acertaininventorymanager.utils.InventoryConstants;
import com.acertaininventorymanager.utils.InventoryManagerException;
import com.acertaininventorymanager.utils.NonPositiveIntegerException;
import com.acertaininventorymanager.utils.InventoryMessageTag;
import com.acertaininventorymanager.utils.InventoryRequest;
import com.acertaininventorymanager.utils.InventoryResponse;
import com.acertaininventorymanager.utils.InventoryUtility;


/**
 * Implements the client level synchronous API.
 * Uses the HTTP protocol for communication with the server.
 */
public class ClientHTTPProxy implements CustomerTransactionManager {

	/** The client. */
	protected HttpClient client;

	/** The server address. */
	protected String serverAddress;

	/** The serializer. */
	private static ThreadLocal<InventorySerializer> serializer;

	/**
	 * Initializes a new ClientHTTPProxy.
	 * @param serverAddress
	 *            the server address
	 * @throws Exception
	 *             the exception
	 */
	public ClientHTTPProxy(String serverAddress) throws Exception {

		// Setup the type of serializer.
		if (InventoryConstants.BINARY_SERIALIZATION) {
			serializer = ThreadLocal.withInitial(InventoryKryoSerializer::new);
		} else {
			serializer = ThreadLocal.withInitial(InventoryXStreamSerializer::new);
		}

		setServerAddress(serverAddress);
		client = new HttpClient();

		// Max concurrent connections to every address.
		client.setMaxConnectionsPerDestination(InvManagerClientConstants.CLIENT_MAX_CONNECTION_ADDRESS);

		// Max number of threads.
		client.setExecutor(new QueuedThreadPool(InvManagerClientConstants.CLIENT_MAX_THREADSPOOL_THREADS));

		// Seconds timeout; if no server reply, the request expires.
		client.setConnectTimeout(InvManagerClientConstants.CLIENT_MAX_TIMEOUT_MILLISECS);

		client.start();
	}

	/**
	 * Gets the server address.
	 *
	 * @return the server address
	 */
	public String getServerAddress() {
		return serverAddress;
	}

	/**
	 * Sets the server address.
	 *
	 * @param serverAddress
	 *            the new server address
	 */
	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}

/*	
	 * (non-Javadoc)
	 * 
	 * @see com.acertaininventorymanager.interfaces.StockManager#getBooks()
	 
	@SuppressWarnings("unchecked")
	public List<StockBook> getBooks() throws BookStoreException {
		String urlString = serverAddress + "/" + InventoryMessageTag.LISTBOOKS;
		InventoryRequest bookStoreRequest = InventoryRequest.newGetRequest(urlString);
		BookStoreResponse bookStoreResponse = InventoryUtility.performHttpExchange(client, bookStoreRequest,
				serializer.get());
		return (List<StockBook>) bookStoreResponse.getList();
	}

	
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.acertaininventorymanager.interfaces.StockManager#updateEditorPicks(java.util
	 * .Set)
	 
	public void updateEditorPicks(Set<BookEditorPick> editorPicksValues) throws BookStoreException {
		String urlString = serverAddress + "/" + InventoryMessageTag.UPDATEEDITORPICKS + "?";
		InventoryRequest bookStoreRequest = InventoryRequest.newPostRequest(urlString, editorPicksValues);
		InventoryUtility.performHttpExchange(client, bookStoreRequest, serializer.get());
	}

*/
	/**
	 * Stops the proxy.
	 */
	public void stop() {
		try {
			client.stop();
		} catch (Exception ex) {
			System.err.println(ex.getStackTrace());
		}
	}

	@Override
	public void processOrders(Set<ItemPurchase> itemPurchases)
			throws NonPositiveIntegerException, InexistentCustomerException, InventoryManagerException {
		
		String urlString = serverAddress + "/" + InventoryMessageTag.PROCESSORDERS;
		InventoryRequest invReq = InventoryRequest.newPostRequest(urlString, itemPurchases);
		InventoryUtility.performHttpExchange(client, invReq, serializer.get());
		
	}

	@Override
	public List<RegionTotal> getTotalsForRegions(Set<Integer> regionIds)
			throws NonPositiveIntegerException, EmptyRegionException, InventoryManagerException {

		String urlString = serverAddress + "/" + InventoryMessageTag.GETREGIONTOTALS;
		InventoryRequest invRequest = InventoryRequest.newGetRequest(urlString);
		InventoryResponse invResponse = InventoryUtility.performHttpExchange(client, invRequest,
				serializer.get());
		return (List<RegionTotal>) invResponse.getList();
	}
}
