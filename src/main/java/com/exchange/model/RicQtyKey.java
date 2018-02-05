/**
 * 
 */
package com.exchange.model;

/**
 * Used as a key to retrieve executed Orders by RIC and quantity
 * 
 * @author Elliot G
 */
public class RicQtyKey {

	private final String ric;
	private final Integer qty;
	
	/**
	 * @param ric
	 * @param qty
	 */
	public RicQtyKey(String ric, Integer qty) {
		super();
		this.ric = ric;
		this.qty = qty;
	}
	
	public String getRic() {
		return ric;
	}
	public Integer getQty() {
		return qty;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((qty == null) ? 0 : qty.hashCode());
		result = prime * result + ((ric == null) ? 0 : ric.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RicQtyKey other = (RicQtyKey) obj;
		if (qty == null) {
			if (other.qty != null)
				return false;
		} else if (!qty.equals(other.qty))
			return false;
		if (ric == null) {
			if (other.ric != null)
				return false;
		} else if (!ric.equals(other.ric))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "RicQtyKey [ric=" + ric + ", qty=" + qty + "]";
	}	
}
