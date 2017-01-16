package com.acertaininventorymanager.business;

/**
 * This class represents the information of a customer in the inventory
 * management system.
 * 
 * @author vmarcos
 */
public class Customer {

	/**
	 * The unique identifier of the customer.
	 */
	private final int customerId;

	/**
	 * The identifier for the sales region that the customer belongs to.
	 */
	private final int regionId;

	/**
	 * The running sum of the monetary value of all {@link ItemPurchase}s made
	 * by the customer.
	 */
	private long valueBought;

	/**
	 * Instantiates a {@link Customer} with given identifiers.
	 * 
	 * @param customerId
	 * @param regionId
	 */
	public Customer(int customerId, int regionId) {
		this.customerId = customerId;
		this.regionId = regionId;
	}

	/**
	 * @return the customerId
	 */
	public int getCustomerId() {
		return customerId;
	}

	/**
	 * @return the regionId
	 */
	public int getRegionId() {
		return regionId;
	}	
	
	/**
	 * @return the valueBought
	 */
	public long getValueBought() {
		return valueBought;
	}

	/**
	 * @param valueBought the valueBought to set
	 */
	public void setValueBought(long valueBought) {
		this.valueBought = valueBought;
	}
	

	@Override
	public boolean equals(Object obj){
		if (! (obj instanceof Customer) ){
			return false;
		}
		Customer c2 = (Customer)obj;
		if (this.customerId == c2.getCustomerId()){
			return true;
		} else{
			return false;
		}
	}
	
	
	@Override
	public int hashCode(){
		return this.customerId;
	}
	
}
