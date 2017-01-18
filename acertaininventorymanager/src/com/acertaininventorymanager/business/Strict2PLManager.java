package com.acertaininventorymanager.business;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


public class Strict2PLManager {
	
	/**The Integer key may be:
	 * the customerId, stored as it is, an Integer >0;
	 * the IDM number; it is stored with the opposite sign, so it is <0.
	 * The MyLock object stores the object key, the LockStatus, and the transactionIDs of the Xacts holding this object.*/
	private ConcurrentHashMap<Integer, MyLock> locksTable = new ConcurrentHashMap<>();;
	
		
	public void tryToAcquireLock(Integer xactId, Integer objectId, LockType lockType){
				
		MyLock objectLock = locksTable.get(objectId);
		
		if (objectLock == null || objectLock.getLockStatus()==LockType.FREE){
			MyLock lockToAdd = new MyLock(objectId, lockType, xactId);
			locksTable.put(objectId, lockToAdd);
		}
		
		if (objectLock.getLockStatus()==LockType.READLOCK && lockType==LockType.READLOCK){
			objectLock.addHolder(xactId);
		}
		
		if (objectLock.getLockStatus()==LockType.WRITELOCK ||
				lockType==LockType.WRITELOCK && objectLock.getLockStatus()!=LockType.FREE){
			 
			Set<Integer> holdersOfThisObject = objectLock.getHolders();
			//updateWaitingGraph(add)(xactId, holdersOfThisObject)
			//now must wait.
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			tryToAcquireLock(xactId,objectId,lockType);
			
		}
	}
		
	
	public void releaseLock(Integer xactId, Integer objectId){
	
		MyLock objectLock = locksTable.get(objectId);
		
		objectLock.removeHolder(xactId);
		//updateWaitingGraph(removal)
		
		if (objectLock.getHolders().size()==0){
			objectLock.setLockStatus(LockType.FREE);
		}//otherwise, we don't change anything
		notifyAll();
		
	}
		
		

}
