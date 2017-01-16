package com.acertaininventorymanager.server;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

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
 * {@link InventoryHTTPMessageHandler} implements the message handler class
 * which is invoked to handle messages received by the
 * {@link InventoryHTTPServerUtility}. It decodes the HTTP message and invokes
 * the {@link CertainInventory} server API.
 * 
 * @see AbstractHandler
 * @see InventoryHTTPServerUtility
 * @see CertainInventory
 */
public class InventoryHTTPMessageHandler extends AbstractHandler {
	/** The inventory manager. */
	private CustomerTransactionManager myInvManager = null;

	/** The serializer. */
	private static ThreadLocal<InventorySerializer> serializer;

	/**
	 * Instantiates a new {@link InventoryHTTPMessageHandler}.
	 *
	 * @param bookStore
	 *            the book store
	 */
	public InventoryHTTPMessageHandler(CustomerTransactionManager invManager) {
		myInvManager = invManager;

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
			case PROCESSORDERS:
				processOrders(request, response);
				break;

			case GETREGIONTOTALS:
				getRegionTotals(request, response);
				break;

			default:
				System.err.println("Unsupported message tag.");
				break;
			}
		}

		// Mark the request as handled so that the HTTP response can be sent
		baseRequest.setHandled(true);
	}


	private void processOrders(HttpServletRequest request, HttpServletResponse response) throws IOException {
		byte[] serializedRequestContent = getSerializedRequestContent(request);

		Set<ItemPurchase> purchases = (Set<ItemPurchase>) serializer.get().deserialize(serializedRequestContent);
		InventoryResponse invResponse = new InventoryResponse();

		try {
			myInvManager.processOrders(purchases);
		} catch (InventoryManagerException ex) {
			invResponse.setException(ex);
		}

		byte[] serializedResponseContent = serializer.get().serialize(invResponse);
		response.getOutputStream().write(serializedResponseContent);
		
	}

	private void getRegionTotals(HttpServletRequest request, HttpServletResponse response) throws IOException {
		byte[] serializedRequestContent = getSerializedRequestContent(request);

		Set<Integer> regionIDs = (Set<Integer>) serializer.get().deserialize(serializedRequestContent);
		InventoryResponse invResponse = new InventoryResponse();

		try {
			myInvManager.getTotalsForRegions(regionIDs);
		} catch (InventoryManagerException ex) {
			invResponse.setException(ex);
		}

		byte[] serializedResponseContent = serializer.get().serialize(invResponse);
		response.getOutputStream().write(serializedResponseContent);
	}
	
	/*
	*//**
	 * Gets the stock books by ISBN.
	 *
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 *//*
	@SuppressWarnings("unchecked")
	private void getStockBooksByISBN(HttpServletRequest request, HttpServletResponse response) throws IOException {
		byte[] serializedRequestContent = getSerializedRequestContent(request);

		Set<Integer> isbnSet = (Set<Integer>) serializer.get().deserialize(serializedRequestContent);
		InventoryResponse bookStoreResponse = new InventoryResponse();

		try {
			bookStoreResponse.setList(myInvManager.getBooksByISBN(isbnSet));
		} catch (InventoryException ex) {
			bookStoreResponse.setException(ex);
		}

		byte[] serializedResponseContent = serializer.get().serialize(bookStoreResponse);
		response.getOutputStream().write(serializedResponseContent);
	}

	*//**
	 * Gets the editor picks.
	 *
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 *//*
	private void getEditorPicks(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String numBooksString = URLDecoder.decode(request.getParameter(InventoryConstants.BOOK_NUM_PARAM), "UTF-8");
		InventoryResponse bookStoreResponse = new InventoryResponse();

		try {
			int numBooks = InventoryUtility.convertStringToInt(numBooksString);
			bookStoreResponse.setList(myInvManager.getEditorPicks(numBooks));
		} catch (InventoryException ex) {
			bookStoreResponse.setException(ex);
		}

		byte[] serializedResponseContent = serializer.get().serialize(bookStoreResponse);
		response.getOutputStream().write(serializedResponseContent);
	}

	*//**
	 * Gets the books.
	 *
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 *//*
	@SuppressWarnings("unchecked")
	private void getBooks(HttpServletRequest request, HttpServletResponse response) throws IOException {
		byte[] serializedRequestContent = getSerializedRequestContent(request);

		Set<Integer> isbnSet = (Set<Integer>) serializer.get().deserialize(serializedRequestContent);
		InventoryResponse bookStoreResponse = new InventoryResponse();

		try {
			bookStoreResponse.setList(myInvManager.getBooks(isbnSet));
		} catch (InventoryException ex) {
			bookStoreResponse.setException(ex);
		}

		byte[] serializedResponseContent = serializer.get().serialize(bookStoreResponse);
		response.getOutputStream().write(serializedResponseContent);
	}

	*//**
	 * Buys books.
	 *
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 *//*
	@SuppressWarnings("unchecked")
	private void buyBooks(HttpServletRequest request, HttpServletResponse response) throws IOException {
		byte[] serializedRequestContent = getSerializedRequestContent(request);

		Set<BookCopy> bookCopiesToBuy = (Set<BookCopy>) serializer.get().deserialize(serializedRequestContent);
		InventoryResponse bookStoreResponse = new InventoryResponse();

		try {
			myInvManager.buyBooks(bookCopiesToBuy);
		} catch (InventoryException ex) {
			bookStoreResponse.setException(ex);
		}

		byte[] serializedResponseContent = serializer.get().serialize(bookStoreResponse);
		response.getOutputStream().write(serializedResponseContent);
	}

	*//**
	 * Updates editor picks.
	 *
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 *//*
	@SuppressWarnings("unchecked")
	private void updateEditorPicks(HttpServletRequest request, HttpServletResponse response) throws IOException {
		byte[] serializedRequestContent = getSerializedRequestContent(request);

		Set<BookEditorPick> mapEditorPicksValues = (Set<BookEditorPick>) serializer.get()
				.deserialize(serializedRequestContent);
		InventoryResponse bookStoreResponse = new InventoryResponse();

		try {
			myInvManager.updateEditorPicks(mapEditorPicksValues);
		} catch (InventoryException ex) {
			bookStoreResponse.setException(ex);
		}

		byte[] serializedResponseContent = serializer.get().serialize(bookStoreResponse);
		response.getOutputStream().write(serializedResponseContent);
	}

	*//**
	 * Lists the books.
	 *
	 * @param response
	 *            the response
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 *//*
	private void listBooks(HttpServletResponse response) throws IOException {
		InventoryResponse bookStoreResponse = new InventoryResponse();
		bookStoreResponse.setList(myInvManager.getBooks());

		byte[] serializedResponseContent = serializer.get().serialize(bookStoreResponse);
		response.getOutputStream().write(serializedResponseContent);
	}

	*//**
	 * Adds the copies.
	 *
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 *//*
	@SuppressWarnings("unchecked")
	private void addCopies(HttpServletRequest request, HttpServletResponse response) throws IOException {
		byte[] serializedRequestContent = getSerializedRequestContent(request);

		Set<BookCopy> listBookCopies = (Set<BookCopy>) serializer.get().deserialize(serializedRequestContent);
		InventoryResponse bookStoreResponse = new InventoryResponse();

		try {
			myInvManager.addCopies(listBookCopies);
		} catch (InventoryException ex) {
			bookStoreResponse.setException(ex);
		}

		byte[] serializedResponseContent = serializer.get().serialize(bookStoreResponse);
		response.getOutputStream().write(serializedResponseContent);
	}

	*//**
	 * Adds the books.
	 *
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 *//*
	@SuppressWarnings("unchecked")
	private void addBooks(HttpServletRequest request, HttpServletResponse response) throws IOException {
		byte[] serializedRequestContent = getSerializedRequestContent(request);

		Set<StockBook> newBooks = (Set<StockBook>) serializer.get().deserialize(serializedRequestContent);
		InventoryResponse bookStoreResponse = new InventoryResponse();

		try {
			myInvManager.addBooks(newBooks);
		} catch (InventoryException ex) {
			bookStoreResponse.setException(ex);
		}

		byte[] serializedResponseContent = serializer.get().serialize(bookStoreResponse);
		response.getOutputStream().write(serializedResponseContent);
	}

	*//**
	 * Removes all books.
	 *
	 * @param response
	 *            the response
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 *//*
	private void removeAllBooks(HttpServletResponse response) throws IOException {
		InventoryResponse bookStoreResponse = new InventoryResponse();

		try {
			myInvManager.removeAllBooks();
		} catch (InventoryException ex) {
			bookStoreResponse.setException(ex);
		}

		byte[] serializedResponseContent = serializer.get().serialize(bookStoreResponse);
		response.getOutputStream().write(serializedResponseContent);
	}

	*//**
	 * Removes the books.
	 *
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 *//*
	@SuppressWarnings("unchecked")
	private void removeBooks(HttpServletRequest request, HttpServletResponse response) throws IOException {
		byte[] serializedRequestContent = getSerializedRequestContent(request);

		Set<Integer> bookSet = (Set<Integer>) serializer.get().deserialize(serializedRequestContent);
		InventoryResponse bookStoreResponse = new InventoryResponse();

		try {
			myInvManager.removeBooks(bookSet);
		} catch (InventoryException ex) {
			bookStoreResponse.setException(ex);
		}

		byte[] serializedResponseContent = serializer.get().serialize(bookStoreResponse);
		response.getOutputStream().write(serializedResponseContent);
	}
*/
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
