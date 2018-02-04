package com.exchange.service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.exchange.model.Order;
import com.exchange.model.OrderExecuted;
import com.exchange.model.RicDirQtyKey;
import com.exchange.model.RicDirKey;
import com.exchange.model.RicQtyKey;
import com.exchange.model.RicUserKey;

public interface OrderMatchingService {
	
	/**
	 * Adds order to in-memory collection.  
	 * Collection is grouped by RIC, direction, qty.  
	 * The value is a Set of orders with same RIC, direction, qty.   
	 * @param order
	 */
	void addOrder(Order order);
	
	Map<RicDirQtyKey, Set<Order>> getOpenOrders();
	
	Map<RicQtyKey, Set<OrderExecuted>> getExecutedOrders();
	
	Optional<Map<BigDecimal, Integer>> getOpenInterest(RicDirKey key);
	
	Optional<BigDecimal> getAvgExecutionPrice(String ric);

	Optional<Integer> getExecutiedQty(RicUserKey key);
		
}
