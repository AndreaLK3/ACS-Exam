package com.acertaininventorymanager.client;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import com.acertaininventorymanager.business.Customer;
import com.acertaininventorymanager.business.ItemPurchase;
import com.acertaininventorymanager.business.RegionTotal;
import com.acertaininventorymanager.interfaces.InventorySerializer;
import com.acertaininventorymanager.interfaces.ItemDataManager;
import com.acertaininventorymanager.utils.EmptyRegionException;
import com.acertaininventorymanager.utils.InexistentCustomerException;
import com.acertaininventorymanager.utils.InexistentItemPurchaseException;
import com.acertaininventorymanager.utils.InventoryConstants;
import com.acertaininventorymanager.utils.InventoryKryoSerializer;
import com.acertaininventorymanager.utils.InventoryManagerException;
import com.acertaininventorymanager.utils.InventoryMessageTag;
import com.acertaininventorymanager.utils.InventoryRequest;
import com.acertaininventorymanager.utils.InventoryResponse;
import com.acertaininventorymanager.utils.InventoryUtility;
import com.acertaininventorymanager.utils.InventoryXStreamSerializer;
import com.acertaininventorymanager.utils.NonPositiveIntegerException;

public class CtmClientHTTPProxy implements ItemDataManager{

	/** The client. */
	protected HttpClient client;

	/** The server address. */
	protected String serverAddress;

	/** The serializer. */
	private static ThreadLocal<InventorySerializer> serializer;

	/**
	 * Initializes a new CtmClientHTTPProxy.
	 */
	public CtmClientHTTPProxy(String serverAddress) throws Exception {

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
	public void addItemPurchase(ItemPurchase itemPurchase) throws InventoryManagerException {
		//TODO: remove debug System.out.println("This is a "+this.getClass().getSimpleName() + " . My Server address is: " + serverAddress);
		String urlString = serverAddress + "/" + InventoryMessageTag.ADDPURCHASE;
		InventoryRequest invReq = InventoryRequest.newPostRequest(urlString, itemPurchase);
		InventoryUtility.performHttpExchange(client, invReq, serializer.get());
		return;
		
	}

	@Override
	public void removeItemPurchase(int orderId, int customerId, int itemId) throws InventoryManagerException
			{
		String urlString = serverAddress + "/" + InventoryMessageTag.REMOVEPURCHASE;
		int[] paramsArray = {orderId, customerId, itemId};
		InventoryRequest invReq = InventoryRequest.newPostRequest(urlString, paramsArray);
		InventoryResponse invResp = InventoryUtility.performHttpExchange(client, invReq, serializer.get());
		return;
		
	}

}
