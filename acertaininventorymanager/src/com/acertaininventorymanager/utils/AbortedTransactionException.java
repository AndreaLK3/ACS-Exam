package com.acertaininventorymanager.utils;

public class AbortedTransactionException extends Exception {

	public AbortedTransactionException() {
		
	}

	public AbortedTransactionException(String message) {
		super(message);
		
	}

	public AbortedTransactionException(Throwable cause) {
		super(cause);
		
	}

	public AbortedTransactionException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public AbortedTransactionException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		
	}

}
