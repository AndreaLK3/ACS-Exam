package com.acertaininventorymanager.utils;

import com.acertaininventorymanager.interfaces.InventorySerializer;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

/**
 * {@link InventoryXStreamSerializer} serializes objects to arrays of bytes
 * representing XML trees using the XStream library.
 * 
 * @see InventorySerializer
 */
public final class InventoryXStreamSerializer implements InventorySerializer {

	/** The XML stream. */
	private final XStream xmlStream = new XStream(new StaxDriver());

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.acertainbookstore.interfaces.BookStoreSerializer#serialize(java.lang.
	 * Object)
	 */
	@Override
	public byte[] serialize(Object object) {
		String xml = xmlStream.toXML(object);
		return xml.getBytes();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.acertainbookstore.interfaces.BookStoreSerializer#deserialize(byte[])
	 */
	@Override
	public Object deserialize(byte[] bytes) {
		String xml = new String(bytes);
		return xmlStream.fromXML(xml);
	}
}
