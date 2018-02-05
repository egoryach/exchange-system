/**
 * 
 */
package com.exchange.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents two Orders that have been matched.  It contains both BUY and SELL matched orders.
 * 
 * @author Elliot G
 */
public class OrderExecuted {

	private final String ric;
	private final Integer qty;
	private final BigDecimal execPrice;
	private final Set<String> users;
	private final Order buy;
	private final Order sell;
	private final LocalDateTime execTime;
	
	public OrderExecuted(String ric, Integer qty, BigDecimal execPrice, Order buy, Order sell,LocalDateTime execTime) {
		super();
		this.ric = ric;
		this.qty = qty;
		this.execPrice = execPrice;
		this.buy = buy;
		this.sell = sell;
		this.execTime = execTime;
		
		Set<String> s = new HashSet<>();
		s.add(buy.getUser());
		s.add(sell.getUser());
		users = Collections.unmodifiableSet(s);
	}

	public Set<String> getUsers() {
		return users;
	}

	public BigDecimal getExecPrice() {
		return execPrice;
	}

	public String getRic() {
		return ric;
	}

	public Integer getQty() {
		return qty;
	}

	public Order getBuy() {
		return buy;
	}

	public Order getSell() {
		return sell;
	}

	public LocalDateTime getExecTime() {
		return execTime;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((buy == null) ? 0 : buy.hashCode());
		result = prime * result + ((execTime == null) ? 0 : execTime.hashCode());
		result = prime * result + ((qty == null) ? 0 : qty.hashCode());
		result = prime * result + ((ric == null) ? 0 : ric.hashCode());
		result = prime * result + ((sell == null) ? 0 : sell.hashCode());
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
		OrderExecuted other = (OrderExecuted) obj;
		if (buy == null) {
			if (other.buy != null)
				return false;
		} else if (!buy.equals(other.buy))
			return false;
		if (execTime == null) {
			if (other.execTime != null)
				return false;
		} else if (!execTime.equals(other.execTime))
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
		if (sell == null) {
			if (other.sell != null)
				return false;
		} else if (!sell.equals(other.sell))
			return false;
		return true;
	}	
}
