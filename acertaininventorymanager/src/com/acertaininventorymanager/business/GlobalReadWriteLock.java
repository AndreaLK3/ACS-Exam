package com.acertaininventorymanager.business;

public class GlobalReadWriteLock {

	/* keeps track of the amount of readers on the IDM list */
	private int readers = 0;

	/**
	 * Tries to acquire a shared lock.
	 * @throws InterruptedException
	 */
	public synchronized void readLock() throws InterruptedException {
		while (readers < 0) {
			wait();
		}
		readers += 1;
	}

	/**
	 * Tries to acquire the exclusive lock.
	 * @throws InterruptedException
	 */
	public synchronized void writeLock() throws InterruptedException {
		while (readers != 0) {
			wait();
		}
		readers = -1;
	}
	
	/**
	 * Releases the read lock.
	 */
	public synchronized void releaseReadLock() {
		readers -= 1;
		notifyAll();
	}

	/**
	 * Releases the write lock.
	 */
	public synchronized void releaseWriteLock() {
		readers = 0;
		notifyAll();
	}
}

