/**
 * 
 */
package com.exchange.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents an Order to be matched.
 * 
 * @author Elliot G
 */
public class Order {

	private final Direction direction;
	private final String ric;
	private final Integer quantity;
	private final BigDecimal price;
	private final String user;
	private final LocalDateTime orderTime;
	
	
	public Order(Direction direction, String ric, Integer quantity, BigDecimal price, String user,
			LocalDateTime orderTime) {
		super();
		this.direction = direction;
		this.ric = ric;
		this.quantity = quantity;
		this.price = price;
		this.user = user;
		this.orderTime = orderTime;
	}



	public Direction getDirection() {
		return direction;
	}

	public String getRic() {
		return ric;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public String getUser() {
		return user;
	}

	public LocalDateTime getOrderTime() {
		return orderTime;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((direction == null) ? 0 : direction.hashCode());
		result = prime * result + ((orderTime == null) ? 0 : orderTime.hashCode());
		result = prime * result + ((price == null) ? 0 : price.hashCode());
		result = prime * result + ((quantity == null) ? 0 : quantity.hashCode());
		result = prime * result + ((ric == null) ? 0 : ric.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
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
		Order other = (Order) obj;
		if (direction != other.direction)
			return false;
		if (orderTime == null) {
			if (other.orderTime != null)
				return false;
		} else if (!orderTime.equals(other.orderTime))
			return false;
		if (price == null) {
			if (other.price != null)
				return false;
		} else if (!price.equals(other.price))
			return false;
		if (quantity == null) {
			if (other.quantity != null)
				return false;
		} else if (!quantity.equals(other.quantity))
			return false;
		if (ric == null) {
			if (other.ric != null)
				return false;
		} else if (!ric.equals(other.ric))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Order [direction=" + direction + ", ric=" + ric + ", quantity=" + quantity + ", price=" + price
				+ ", user=" + user + ", orderTime=" + orderTime + "]";
	}	
}
