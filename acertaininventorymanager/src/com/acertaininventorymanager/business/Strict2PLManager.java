package com.acertaininventorymanager.business;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.acertaininventorymanager.utils.AbortedTransactionException;


public class Strict2PLManager {
	
	private WaitingGraphManager waitGraphManager;
	
	private ConcurrentLinkedQueue<Integer> abortedXactIds;
	
	/**The Integer key / objectID is either:
	 *  - the customerId, stored as it is, an Integer >0;
	 *  - the IDM number; it is stored with the opposite sign, so it is <0.
	 * The MyLock object stores the object key, the LockStatus, and the transactionIDs of the Xacts holding this object.*/
	private ConcurrentHashMap<Integer, MyLock> locksTable;
	
	/**The constructor*/
	public Strict2PLManager() {
		this.locksTable = new ConcurrentHashMap<>();
		waitGraphManager = new WaitingGraphManager(this);
		abortedXactIds = new ConcurrentLinkedQueue<>();
	}
	
		
	public void tryToAcquireLock(Integer xactId, Integer objectId, LockType lockType) throws AbortedTransactionException{
				
		MyLock objectLock = locksTable.get(objectId);
		
		if (objectLock == null || objectLock.getLockStatus()==LockType.FREE){
			MyLock lockToAdd = new MyLock(objectId, lockType, xactId);
			locksTable.put(objectId, lockToAdd);
		}
		else
			if (objectLock.getLockStatus()==LockType.READLOCK && lockType==LockType.READLOCK){
			objectLock.addHolder(xactId);
			}
			else
				if (objectLock.getLockStatus()==LockType.WRITELOCK ||
						lockType==LockType.WRITELOCK && objectLock.getLockStatus()!=LockType.FREE){
			 
						Set<Integer> holdersOfThisObject = objectLock.getHolders();
						
						//if the transaction already holds this object, it must not wait for itself
						if (! holdersOfThisObject.contains(xactId)){
							
							waitGraphManager.addEdges(xactId, holdersOfThisObject);
							
							//now, we wait for 0.05 seconds and then we try again
							if (! abortedXactIds.contains(xactId)) {
								try {
									Thread.sleep(50);
									System.out.println("Locks table:");
									for (Integer objID : locksTable.keySet()){
										MyLock objLock = locksTable.get(objID);
										if (objLock.getLockStatus()==LockType.WRITELOCK)
											System.out.print(objLock);
									}
									System.out.println("\n");
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								tryToAcquireLock(xactId,objectId,lockType);
							}
							else
								throw new AbortedTransactionException();
						}			
		}
	}
		
	
	public void releaseLock(Integer xactId, Integer objectId){
	
		MyLock objectLock = locksTable.get(objectId);
		
		if (objectLock!=null) {
			objectLock.removeHolder(xactId);
			waitGraphManager.removeXactFromGraph(xactId);
			
			
			if (objectLock.getHolders().size()==0){
				objectLock.setLockStatus(LockType.FREE);
			}//otherwise, we don't change the status	
			
			System.out.println("Successfully released lock of " + xactId + " on Object: " + objectId);
		}
	}
		
	
	public void flagTransactionAsAborted(int xactId){
		if (! abortedXactIds.contains(xactId))
			abortedXactIds.add(xactId);
	}
	
	public void removeTransactionFromAbortedList(int xactId){
		abortedXactIds.remove(xactId);
	}


	/**Returns the list of aborted transactions. For testing purposes.*/
	public ConcurrentLinkedQueue<Integer> getAbortedXactIds() {
		return abortedXactIds;
	}

	
	/**Looks up the locksTable to reconstruct the information of the transaction table:
	 * which transactions hold the object. Then, we check if the current transaction
	 * is waiting for an object that is already held by someone else, and we add the edge to the
	 * WaitingGraphManager. */
	private void lookUpTransactionTable(Integer xactID, Integer objectID){
		Set<Integer> tIDs = locksTable.keySet();
		Set<Integer> tIdsHoldingSameObject = new HashSet<>();
		
		
	}

}
