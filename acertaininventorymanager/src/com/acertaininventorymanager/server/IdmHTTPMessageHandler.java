package com.acertaininventorymanager.server;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.acertaininventorymanager.business.Customer;
import com.acertaininventorymanager.business.CustomerTransactionsHandler;
import com.acertaininventorymanager.business.ItemDataHandler;
import com.acertaininventorymanager.business.ItemPurchase;
import com.acertaininventorymanager.interfaces.CustomerTransactionManager;
import com.acertaininventorymanager.interfaces.InventorySerializer;
import com.acertaininventorymanager.utils.InventoryConstants;
import com.acertaininventorymanager.utils.InventoryKryoSerializer;
import com.acertaininventorymanager.utils.InventoryManagerException;
import com.acertaininventorymanager.utils.InventoryMessageTag;
import com.acertaininventorymanager.utils.InventoryResponse;
import com.acertaininventorymanager.utils.InventoryUtility;
import com.acertaininventorymanager.utils.InventoryXStreamSerializer;
import com.esotericsoftware.kryo.io.Input;

/**
 * Implements the message handler class server-side,
 * which is invoked to handle messages received by the
 * HTTPServerUtility. It decodes the HTTP message and invokes
 * the server API.
 */
public class IdmHTTPMessageHandler extends AbstractHandler {
	/** The inventory manager. */
	private ItemDataHandler myItemDataManager = null;

	/** The serializer. */
	private static ThreadLocal<InventorySerializer> serializer;

	/**
	 * Instantiates a new {@link IdmHTTPMessageHandler}.
	 */
	public IdmHTTPMessageHandler(ItemDataHandler itemDataManager) {
		myItemDataManager = itemDataManager;

		// Setup the type of serializer.
		if (InventoryConstants.BINARY_SERIALIZATION) {
			serializer = ThreadLocal.withInitial(InventoryKryoSerializer::new);
		} else {
			serializer = ThreadLocal.withInitial(InventoryXStreamSerializer::new);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jetty.server.Handler#handle(java.lang.String,
	 * org.eclipse.jetty.server.Request, javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		InventoryMessageTag messageTag;
		String requestURI;

		response.setStatus(HttpServletResponse.SC_OK);
		requestURI = request.getRequestURI();

		// Need to do request multiplexing
		if (!InventoryUtility.isEmpty(requestURI) && requestURI.toLowerCase().startsWith("/stock")) {
			// The request is from the store manager; more sophisticated.
			// security features could be added here.
			messageTag = InventoryUtility.convertURItoMessageTag(requestURI.substring(6));
		} else {
			messageTag = InventoryUtility.convertURItoMessageTag(requestURI);
		}

		// The RequestURI before the switch.
		if (messageTag == null) {
			System.err.println("No message tag.");
		} else {
			switch (messageTag) {
			case ADDPURCHASE:
				addPurchase(request, response);
				break;

			case REMOVEPURCHASE:
				removePurchase(request, response);
				break;
				
			default:
				System.err.println("Unsupported message tag. This is the : " + this.getClass().getSimpleName() + " . The tag was:" + messageTag);
				break;
			}
		}

		// Mark the request as handled so that the HTTP response can be sent
		baseRequest.setHandled(true);
	}

private void addPurchase(HttpServletRequest request, HttpServletResponse response) throws IOException {
	byte[] serializedRequestContent = getSerializedRequestContent(request);

	ItemPurchase purchase = (ItemPurchase) serializer.get().deserialize(serializedRequestContent);
	InventoryResponse invResponse = new InventoryResponse();

	try {
		myItemDataManager.addItemPurchase(purchase);
	} catch (InventoryManagerException ex) {
		invResponse.setException(ex);
	}

	byte[] serializedResponseContent = serializer.get().serialize(invResponse);
	response.getOutputStream().write(serializedResponseContent);
		
	}

private void removePurchase(HttpServletRequest request, HttpServletResponse response) throws IOException {
	byte[] serializedRequestContent = getSerializedRequestContent(request);

	int[] parametersArray = (int []) serializer.get().deserialize(serializedRequestContent);
	InventoryResponse invResponse = new InventoryResponse();

	int orderId = parametersArray[0]; int customerId = parametersArray[1]; int itemId = parametersArray[2];
	try {
		myItemDataManager.removeItemPurchase(orderId, customerId, itemId);
	} catch (InventoryManagerException ex) {
		invResponse.setException(ex);
	}

	byte[] serializedResponseContent = serializer.get().serialize(invResponse);
	response.getOutputStream().write(serializedResponseContent);
		
	}
		
	/**
	 * Gets the serialized request content.
	 *
	 * @param request the request
	 * @return the serialized request content
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private byte[] getSerializedRequestContent(HttpServletRequest request) throws IOException {
		Input in = new Input(request.getInputStream());
		byte[] serializedRequestContent = in.readBytes(request.getContentLength());
		in.close();
		return serializedRequestContent;
	}
}

