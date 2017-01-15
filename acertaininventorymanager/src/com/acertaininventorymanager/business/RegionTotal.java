package com.acertaininventorymanager.business;

/**
 * Represents the total monetary value calculated for a sales region.
 * 
 * @author vmarcos
 */
public class RegionTotal {

	/**
	 * The identifier of the sales region, as obtained from {@link Customer}
	 * instances.
	 */
	private final int regionId;

	/**
	 * The total value bought by all customers in the given region.
	 */
	private final long totalValueBought;

	/**
	 * Instantiates a {@link RegionTotal} to return the total value bought for a
	 * given region.
	 * 
	 * @param regionId
	 * @param totalValueBought
	 */
	public RegionTotal(int regionId, long totalValueBought) {
		this.regionId = regionId;
		this.totalValueBought = totalValueBought;
	}

	/**
	 * @return the regionId
	 */
	public int getRegionId() {
		return regionId;
	}

	/**
	 * @return the totalValueBought
	 */
	public long getTotalValueBought() {
		return totalValueBought;
	}

}
