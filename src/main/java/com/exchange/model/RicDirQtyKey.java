package com.exchange.model;

/**
 * Direction, RIC, qty
 * @author HP
 *
 */
public class RicDirQtyKey {

	final private Direction direction;
	final private String ric;
	final private Integer qty;

	public RicDirQtyKey(Direction direction, String ric, Integer qty) {
		super();
		this.direction = direction;
		this.ric = ric;
		this.qty = qty;
	}

	public Direction getDirection() {
		return direction;
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
		result = prime * result + ((direction == null) ? 0 : direction.hashCode());
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
		RicDirQtyKey other = (RicDirQtyKey) obj;
		if (direction != other.direction)
			return false;
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
		return "OrderKey [direction=" + direction + ", ric=" + ric + ", qty=" + qty + "]";
	}

	
	
}
