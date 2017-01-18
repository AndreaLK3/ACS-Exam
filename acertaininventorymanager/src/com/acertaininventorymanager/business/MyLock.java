package com.acertaininventorymanager.business;

import java.util.Set;

public class MyLock {
	
	private int objectId;
	private LockType lockStatus;
	private Set<Integer> holders;

	public MyLock(Integer theObjectId, LockType theLockStatus, Integer tIdOfHolder) {
		objectId = theObjectId;
		lockStatus = theLockStatus;
		holders.add(tIdOfHolder);
	}

	public int getObjectId() {
		return objectId;
	}

	public LockType getLockStatus() {
		return lockStatus;
	}
	
	public void setLockStatus(LockType newLockStatus) {
		lockStatus = newLockStatus;
	}

	public void addHolder(Integer newHolderXactId){
		holders.add(newHolderXactId);
	}
	
	public void removeHolder(Integer holderXactId){
		holders.remove(holderXactId);
	}
	
	public Set<Integer> getHolders(){
		return holders;
	}

}
