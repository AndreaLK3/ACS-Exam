package com.acertaininventorymanager.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;

import com.acertaininventorymanager.business.Customer;
import com.acertaininventorymanager.business.ItemPurchase;
import com.acertaininventorymanager.client.ClientHTTPProxy;
import com.acertaininventorymanager.client.CtmClientHTTPProxy;
import com.acertaininventorymanager.interfaces.InventorySerializer;
import com.acertaininventorymanager.server.CtmHTTPMessageHandler;
import com.acertaininventorymanager.server.CtmHTTPServer;
import com.acertaininventorymanager.server.IdmHTTPMessageHandler;
import com.acertaininventorymanager.server.IdmHTTPServer;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.minlog.Log;

import org.objenesis.strategy.StdInstantiatorStrategy;

/**
 * {@link InventoryKryoSerializer} serializes objects to arrays of bytes
 * representing strings using the Kryo library.
 * 
 * @see InventorySerializer
 */
public final class InventoryKryoSerializer implements InventorySerializer {

	/** The binary stream. */
	private final Kryo binaryStream;

	/**
	 * Instantiates a new {@link InventoryKryoSerializer}.
	 */
	public InventoryKryoSerializer() {
		binaryStream = new Kryo();
		//binaryStream.setRegistrationRequired(false); //ADD-ON to solve "encountered unregistered class ID" problem
		binaryStream.setInstantiatorStrategy(new Kryo.DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.acertainbookstore.interfaces.BookStoreSerializer#serialize(java.lang.
	 * Object)
	 */
	@Override
	public byte[] serialize(Object object) throws IOException {
		try (OutputStream outStream = new ByteArrayOutputStream(); Output out = new Output(outStream)) {
			binaryStream.writeClassAndObject(out, object);
			return out.toBytes();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.acertainbookstore.interfaces.BookStoreSerializer#deserialize(byte[])
	 */
	@Override
	public Object deserialize(byte[] bytes) throws IOException {
		try (InputStream inStream = new ByteArrayInputStream(bytes); Input in = new Input(inStream)) {
			return binaryStream.readClassAndObject(in);
		}
	}
}
