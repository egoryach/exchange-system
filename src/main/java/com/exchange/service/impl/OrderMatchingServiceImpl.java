/**
 * 
 */
package com.exchange.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.exchange.model.Direction;
import com.exchange.model.Order;
import com.exchange.model.OrderExecuted;
import com.exchange.model.RicDirQtyKey;
import com.exchange.model.RicDirKey;
import com.exchange.model.RicQtyKey;
import com.exchange.model.RicUserKey;
import com.exchange.service.OrderMatchingService;

/**
 * 
 * 
 * @author Elliot Goryachkovsky
 */
@Service
public class OrderMatchingServiceImpl implements OrderMatchingService {
	private static final Logger logger = LoggerFactory.getLogger(OrderMatchingServiceImpl.class.getName());
	
	// keyed by RIC, direction, quantity
	private final Map<RicDirQtyKey, Set<Order>> openOrders = new ConcurrentHashMap<>();
	
	// keyed by RIC, direction
	private final Map<RicDirKey, Set<Order>> openOrdersByRicDir = new ConcurrentHashMap<>();
	
	// keyed by RIC, quantity
	private final Map<RicQtyKey, Set<OrderExecuted>> execOrdersByRicQty = new ConcurrentHashMap<>();
	
	// keyed by RIC, user
	private final Map<RicUserKey, Set<OrderExecuted>> execOrdersByRicUser = new ConcurrentHashMap<>();
	
	// keyed by prices by RIC
	private final Map<String, Set<BigDecimal>> executedPrices = new ConcurrentHashMap<>();

	/**
	 * 
	 */
	public OrderMatchingServiceImpl() {

	}


	/* (non-Javadoc)
	 * @see com.exchange.service.OrderMatchingService#addOrder(com.exchange.model.Order)
	 */
	@Override
	public void addOrder(Order order) {

		Optional<Order> matchingOrder = findMatchingOrder(order);

		//if match, we have executed pair / executed price
		if(matchingOrder.isPresent()){					
			processExecution(order, matchingOrder.get());
		}else{
			processOrder(order);			
		}
	}


	private void processOrder(Order order) {
		RicDirQtyKey orderKey = new RicDirQtyKey(order.getDirection(), order.getRic(), order.getQuantity());
		openOrders.putIfAbsent(orderKey, ConcurrentHashMap.newKeySet());
		openOrders.get(orderKey).add(order);
		
		RicDirKey key = new RicDirKey(order.getRic(), order.getDirection());
		openOrdersByRicDir.putIfAbsent(key, ConcurrentHashMap.newKeySet());
		openOrdersByRicDir.get(key).add(order);

	}


	private void processExecution(Order order, Order matchingOrder) {

		executedPrices.putIfAbsent(order.getRic(), ConcurrentHashMap.newKeySet());
		Set<BigDecimal> prices = executedPrices.get(order.getRic());
		prices.add(order.getPrice());
		
		// orderExecuted
		OrderExecuted orderExec = new OrderExecuted(
				order.getRic(), 
				order.getQuantity(), 
				order.getPrice(),
				order.getDirection()==Direction.BUY ? order : matchingOrder, 
				order.getDirection()==Direction.SELL ? order : matchingOrder,
				LocalDateTime.now());
		
		//map RIC, QTY
		RicQtyKey ricQtyKey = new RicQtyKey(order.getRic(), order.getQuantity());			
		execOrdersByRicQty.putIfAbsent(ricQtyKey, ConcurrentHashMap.newKeySet());
	    execOrdersByRicQty.get(ricQtyKey).add(orderExec);

	    //map RIC, USER
		RicUserKey ricUserKey1 = new RicUserKey(order.getRic(), order.getUser());	
		RicUserKey ricUserKey2 = new RicUserKey(matchingOrder.getRic(), matchingOrder.getUser());	
		execOrdersByRicUser.putIfAbsent(ricUserKey1, ConcurrentHashMap.newKeySet());
		execOrdersByRicUser.putIfAbsent(ricUserKey2, ConcurrentHashMap.newKeySet());
		execOrdersByRicUser.get(ricUserKey1).add(orderExec);	
		execOrdersByRicUser.get(ricUserKey2).add(orderExec);	

		//remove matching order from set
		RicDirQtyKey orderMatchKey = new RicDirQtyKey(order.getDirection().getOpposite(), order.getRic(), order.getQuantity());
		openOrders.get(orderMatchKey).remove(matchingOrder);
		
		RicDirKey key = new RicDirKey(order.getRic(), order.getDirection().getOpposite());
		openOrdersByRicDir.get(key).remove(matchingOrder);
	}

	private Optional<Order> findMatchingOrder(Order order) {
		
		logger.info("Looking to match: {}", order);
		
		Optional<Order> orderOptional = Optional.empty(); 
		
		// get Orders matching on RIC, direction(opposite), qty
		RicDirQtyKey key = new RicDirQtyKey(order.getDirection().getOpposite(), order.getRic(), order.getQuantity());
		Set<Order> matchingOrders = openOrders.get(key);
		
		if(matchingOrders == null){
			logger.info("...match not found");
			return orderOptional;
		}
		//filter out same user
		//matchingOrders = matchingOrders.stream().filter(o -> !o.getUser().equalsIgnoreCase(order.getUser())).collect(Collectors.toCollection(ConcurrentHashMap::newKeySet));
				
		// is BUY or SELL
		// if order buy, make sure collection sell prices <= order price
		// if sell order, make sure collecion buy prices >= order price
		
		if (order.getDirection() == Direction.BUY) {
			orderOptional = matchingOrders.stream()
					.filter(o -> !o.getUser().equalsIgnoreCase(order.getUser()))
					.filter(o -> o.getPrice().compareTo(order.getPrice()) <= 0)
					.max((o1, o2) -> o2.getPrice().compareTo(o1.getPrice()) == 0
							? o1.getOrderTime().compareTo(o2.getOrderTime()) : o2.getPrice().compareTo(o1.getPrice()));
		}else{
			orderOptional = matchingOrders.stream()				
					.filter(o -> !o.getUser().equalsIgnoreCase(order.getUser()))
					.filter(o -> o.getPrice().compareTo(order.getPrice()) >= 0)
					.max((o1, o2) -> o1.getPrice().compareTo(o2.getPrice()) == 0
							? o1.getOrderTime().compareTo(o2.getOrderTime()) : o1.getPrice().compareTo(o2.getPrice()));
		}
		
		if(orderOptional.isPresent())
			logger.info("...match found!!: {}", orderOptional.get());
		else
			logger.info("...match not found");
		return orderOptional;
	}

	@Override
	public Optional<Map<BigDecimal, Integer>> getOpenInterest(RicDirKey key) {

		Set<Order> orders = openOrdersByRicDir.get(key);
		if(orders == null)
			return Optional.empty();
			
		Map<BigDecimal, Integer> map = orders
				.stream()
				.collect(Collectors.groupingBy(Order::getPrice, Collectors.summingInt(Order::getQuantity)));
		
		return Optional.of(map);
	}

	@Override
	public Optional<BigDecimal> getAvgExecutionPrice(String ric) {

		Set<BigDecimal> prices = executedPrices.get(ric);
		if (prices == null)
			return Optional.empty();

		BigDecimal avgPrice = prices.stream().reduce(BigDecimal.ZERO, BigDecimal::add)
				.divide(BigDecimal.valueOf(prices.size()), 2);

		return Optional.of(avgPrice);
	}

	@Override
	public Optional<Integer> getExecutiedQty(RicUserKey key) {
		//for given RIC, User
		Set<OrderExecuted> set = execOrdersByRicUser.get(key);
		
		//buy
		int buyQty = set.stream()
				.filter(e -> e.getBuy().getUser().equals(key.getUser()))
				.mapToInt(e -> e.getBuy().getQuantity()).sum();
		//sell
		int sellQty = set.stream()
				.filter(e -> e.getSell().getUser().equals(key.getUser()))
				.mapToInt(e -> e.getSell().getQuantity()).sum();
		
		int result = buyQty - sellQty;
		
		return Optional.of(result);
	}

	@Override
	public Map<RicDirQtyKey, Set<Order>> getOpenOrders() {		
		return Collections.unmodifiableMap(openOrders);
	}
	
	@Override
	public Map<RicQtyKey, Set<OrderExecuted>> getExecutedOrders() {		 
		return Collections.unmodifiableMap(execOrdersByRicQty);
	}

}
