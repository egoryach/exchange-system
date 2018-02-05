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

/**
 * Service for matching Orders on RIC, opposite direction, quantity and price.  
 * 
 * @author Elliot G
 */
public interface OrderMatchingService {
	
	/**
	 * Adds Order to in-memory collection.  
	 * Collection is keyed by RIC, direction, qty.  
	 * Collection value is a Set of Orders with same RIC, direction, qty and varying price.   
	 * @param order
	 */
	void addOrder(Order order);
	
	/**
	 * Retrieves a map of open Orders keyed by RIC, direction and quantity.
	 * 
	 * @return Map
	 */
	Map<RicDirQtyKey, Set<Order>> getOpenOrders();
	
	/**
	 * Retrieves a map of executed Orders keyed by RIC and quantity.
	 * 
	 * @return Map
	 */
	Map<RicQtyKey, Set<OrderExecuted>> getExecutedOrders();
	
	/**
	 * Retrieves a map of open interest(open Order qty) for given RIC and direction grouped by price.
	 * 
	 * @return Map
	 */
	Optional<Map<BigDecimal, Integer>> getOpenInterest(RicDirKey key);
	
	/**
	 * Retrieves average execution price for a given RIC.
	 * 
	 * @return Map
	 */
	Optional<BigDecimal> getAvgExecutionPrice(String ric);

	/**
	 * Retrieves executed quantity for a given RIC and user.
	 * 
	 * @return Map
	 */
	Optional<Integer> getExecutedQty(RicUserKey key);
		
}
