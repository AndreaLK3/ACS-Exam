package com.acertaininventorymanager.business;

/**
 * This class represents the purchase of an item by a customer interacting with
 * a sales manager.
 * 
 * @author vmarcos
 */
public class ItemPurchase {

	/**
	 * The ID of the order for this item purchase.
	 */
	private final int orderId;

	/**
	 * The ID of the customer for this item purchase.
	 */
	private final int customerId;

	/**
	 * The ID of the item for this item purchase.
	 */
	private final int itemId;

	/**
	 * The quantity ordered of the item in this item purchase.
	 */
	private final int quantity;

	/**
	 * The unit price agreed for the item in this item purchase.
	 */
	private final int unitPrice;

	/**
	 * Instantiates a new ItemPurchase with the given order, customer, and item
	 * IDs, along with a quantity ordered and unit price.
	 * 
	 * @param orderId
	 * @param customerId
	 * @param itemId
	 * @param quantity
	 * @param unitPrice
	 */
	public ItemPurchase(int orderId, int customerId, int itemId, int quantity, int unitPrice) {
		this.orderId = orderId;
		this.customerId = customerId;
		this.itemId = itemId;
		this.quantity = quantity;
		this.unitPrice = unitPrice;
	}

	/**
	 * @return the orderId
	 */
	public int getOrderId() {
		return orderId;
	}

	/**
	 * @return the customerId
	 */
	public int getCustomerId() {
		return customerId;
	}

	/**
	 * @return the itemId
	 */
	public int getItemId() {
		return itemId;
	}

	/**
	 * @return the quantity
	 */
	public int getQuantity() {
		return quantity;
	}

	/**
	 * @return the unitPrice
	 */
	public int getUnitPrice() {
		return unitPrice;
	}

	@Override
	public boolean equals(Object obj){
		if (! (obj instanceof ItemPurchase) ){
			return false;
		}
		ItemPurchase p2 = (ItemPurchase)obj;
		if (this.orderId == p2.getOrderId() &&
			this.customerId == p2.getCustomerId() &&
			this.itemId == p2.getItemId() ){
				return true;
		} else{
			return false;
		}
	}
	
	@Override
	public int hashCode(){
		return (this.orderId + this.customerId + this.itemId);
	}
	
	@Override
	public String toString(){
		return "Purchase: (oId= "+orderId+", cId="+customerId+", iId="+itemId+" )";
	}
	
}
