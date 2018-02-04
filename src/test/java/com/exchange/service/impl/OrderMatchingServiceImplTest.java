package com.exchange.service.impl;

import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.exchange.model.Direction;
import com.exchange.model.Order;
import com.exchange.model.OrderExecuted;
import com.exchange.model.RicDirQtyKey;
import com.exchange.model.RicDirKey;
import com.exchange.model.RicQtyKey;
import com.exchange.model.RicUserKey;
import com.exchange.service.OrderMatchingService;


@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderMatchingServiceImplTest {

	private static final Logger logger = LoggerFactory.getLogger(OrderMatchingServiceImplTest.class.getName());
	
	@Autowired
	private OrderMatchingService matchingService;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		generateOrders();
		
		
	}

	private void generateOrders() {
		
		// IBM.N 159.03
		// GOOGL.OQ 1,119.20
		// AMZN.OQ  1,429.95
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testAddOrder() {

		init(Direction.BUY);
		
		Map<RicDirQtyKey, Set<Order>> map = matchingService.getOpenOrders();
		Assert.assertNotNull(map);

	}
	
	/**
	 * Sell order against existing Buy orders
	 * Buy order price is greater than Sell orders.
	 */
	@Test
	public void testAddSellOrderWithSamePriceDiffTime() {

		initWithSamePriceDiffTime(Direction.BUY);
		
		matchingService.addOrder( new Order(Direction.SELL,"GOOGL.OQ",100,new BigDecimal("1119.20"),"User2",LocalDateTime.now()) );
		
		Map<RicDirQtyKey, Set<Order>> openOrders = matchingService.getOpenOrders();
		Assert.assertNotNull(openOrders);
		
		Map<RicQtyKey, Set<OrderExecuted>> execOrders =  matchingService.getExecutedOrders();
		Assert.assertTrue(execOrders.size()==1);

	}
	
	/**
	 * Buy order against existing Sell orders
	 * Buy order price is greater than Sell orders.
	 * Should execute.
	 */
	@Test
	public void testAddBuyOrderWithSamePriceDiffTime() {

		initWithSamePriceDiffTime(Direction.SELL);
		
		matchingService.addOrder( new Order(Direction.BUY,"GOOGL.OQ",100,new BigDecimal("1150.20"),"User2",LocalDateTime.now()) );
		
		Map<RicDirQtyKey, Set<Order>> openOrders = matchingService.getOpenOrders();
		Assert.assertNotNull(openOrders);
		
		Map<RicQtyKey, Set<OrderExecuted>> execOrders =  matchingService.getExecutedOrders();
		Assert.assertTrue(execOrders.size()==1);
	}
	
	
	/**
	 * Buy order against existing Sell orders.  
	 * Buy order is lower price than Sell orders.
	 * Should not execute.
	 */
	@Test
	public void testAddBuyOrderLowerPriceThanSell() {

		initWithSamePriceDiffTime(Direction.SELL);
		
		matchingService.addOrder( new Order(Direction.BUY,"GOOGL.OQ",100,new BigDecimal("1100.20"),"User2",LocalDateTime.now()) );
		
		Map<RicDirQtyKey, Set<Order>> openOrders = matchingService.getOpenOrders();
		Assert.assertNotNull(openOrders);
		
		Map<RicQtyKey, Set<OrderExecuted>> execOrders =  matchingService.getExecutedOrders();
		Assert.assertTrue(execOrders.size()==0);

	}
	/**
	 * Sell order against existing Buy orders
	 * User same.
	 * Should not execute.
	 */
	@Test
	public void testAddSellOrderWithSameUser() {

		initWithSamePriceDiffTime(Direction.BUY);
		
		matchingService.addOrder( new Order(Direction.SELL,"GOOGL.OQ",100,new BigDecimal("1119.20"),"User1",LocalDateTime.now()) );
		
		Map<RicDirQtyKey, Set<Order>> openOrders = matchingService.getOpenOrders();
		Assert.assertNotNull(openOrders);
		
		Map<RicQtyKey, Set<OrderExecuted>> execOrders =  matchingService.getExecutedOrders();
		Assert.assertTrue(execOrders.size()==0);

	}
	
	/**
	 * Buy order against existing Sell orders.
	 * Should pick lowest price and latest date.
	 * Should execute.
	 */
	@Test
	public void testAddBuyOrderWithDiffPriceAndTime() {

		initWithDiffPriceAndTime(Direction.SELL);
		
		matchingService.addOrder( new Order(Direction.BUY,"GOOGL.OQ",100,new BigDecimal("1119.20"),"User2",LocalDateTime.now()) );
		
		Map<RicDirQtyKey, Set<Order>> map = matchingService.getOpenOrders();
		Assert.assertNotNull(map);

		Map<RicQtyKey, Set<OrderExecuted>> execOrders =  matchingService.getExecutedOrders();		
		Set<OrderExecuted> execSet =  execOrders.get(new RicQtyKey("GOOGL.OQ", 100));
		Assert.assertTrue(execSet.size()==1);
		
		OrderExecuted execOrder = execSet.stream().findFirst().get();
		Assert.assertTrue(execOrder.getSell().getPrice().equals(new BigDecimal("1119.20")));
		Assert.assertTrue(execOrder.getSell().getOrderTime().equals(LocalDateTime.parse("2018-02-02T12:10:30")));
	}
	
	/**
	 * Sell order against existing Buy orders.
	 * Should pick highest price and latest date.
	 */
	@Test
	public void testAddSellOrderWithDiffPriceAndTime() {

		initWithDiffPriceAndTime(Direction.BUY);
		
		matchingService.addOrder( new Order(Direction.SELL,"GOOGL.OQ",100,new BigDecimal("1119.20"),"User2",LocalDateTime.now()) );
		
		Map<RicDirQtyKey, Set<Order>> map = matchingService.getOpenOrders();
		Assert.assertNotNull(map);

		Map<RicQtyKey, Set<OrderExecuted>> execOrders =  matchingService.getExecutedOrders();		
		Set<OrderExecuted> execSet =  execOrders.get(new RicQtyKey("GOOGL.OQ", 100));
		Assert.assertTrue(execSet.size()==1);
		
		OrderExecuted execOrder = execSet.stream().findFirst().get();
		Assert.assertTrue(execOrder.getBuy().getPrice().equals(new BigDecimal("1125.20")));
		Assert.assertTrue(execOrder.getBuy().getOrderTime().equals(LocalDateTime.parse("2018-02-02T12:10:30")));
	}

	@Test
	public void testAddOrderWithDiffPriceAndTime2Orders() {

		initWithDiffPriceAndTime(Direction.BUY);
		
		matchingService.addOrder( new Order(Direction.SELL,"GOOGL.OQ",100,new BigDecimal("1119.20"),"User2",LocalDateTime.now()) );
		matchingService.addOrder( new Order(Direction.SELL,"GOOGL.OQ",100,new BigDecimal("1119.20"),"User2",LocalDateTime.now()) );
		
		Map<RicDirQtyKey, Set<Order>> map = matchingService.getOpenOrders();
		Assert.assertNotNull(map);

		Map<RicQtyKey, Set<OrderExecuted>> execOrders =  matchingService.getExecutedOrders();
		Assert.assertTrue(execOrders.size()==1);
		
		Set<OrderExecuted> execSet =  execOrders.get(new RicQtyKey("GOOGL.OQ", 100));
		Assert.assertTrue(execSet.size()==2);
	}
	
	@Test
	public void testGetOpenInterest() {
		initWithDiffPriceAndTime(Direction.SELL);
		
		RicDirKey key = new RicDirKey("GOOGL.OQ", Direction.SELL);
		Optional<Map<BigDecimal, Integer>> optMap = matchingService.getOpenInterest(key);
		if(!optMap.isPresent())
			fail("Unable to return Open interest");
		
		Map<BigDecimal, Integer> map =optMap.get();
		map.forEach( (k, v) -> logger.info("Open {} {} Interest: {} @ {}",key.getRic(),key.getDirection(),v,k));
		
		Assert.assertTrue(map.get(new BigDecimal("1119.20")).equals(200));
		Assert.assertTrue(map.get(new BigDecimal("1125.20")).equals(200));
	}

	@Test
	public void testGetAvgExecutionPrice() {
		initWithDiffPriceAndTime(Direction.SELL);
		
		matchingService.addOrder( new Order(Direction.BUY,"GOOGL.OQ",100,new BigDecimal("1122.20"),"User2",LocalDateTime.now()) );
		matchingService.addOrder( new Order(Direction.BUY,"GOOGL.OQ",100,new BigDecimal("1160.20"),"User2",LocalDateTime.now()) );		
		
		String ric = "GOOGL.OQ";
		Optional<BigDecimal> avgPrice = matchingService.getAvgExecutionPrice(ric);
		if(avgPrice.isPresent()){
			BigDecimal price = avgPrice.get();
			logger.info("Average execution price for {} is {}",ric,price);
			Assert.assertTrue(price.compareTo(new BigDecimal("1141.20"))==0);
		}else
			fail(String.format("Avg price not computed for %s",ric));
		
	}

	@Test
	public void testGetAvgExecutionPrice2() {
		
		initWithDiffPriceAndTime(Direction.SELL);
		
		matchingService.addOrder( new Order(Direction.BUY,"GOOGL.OQ",100,new BigDecimal("1122.20"),"User2",LocalDateTime.now()) );
		matchingService.addOrder( new Order(Direction.BUY,"GOOGL.OQ",100,new BigDecimal("1160.20"),"User2",LocalDateTime.now()) );		
		matchingService.addOrder( new Order(Direction.BUY,"GOOGL.OQ",100,new BigDecimal("1130.20"),"User2",LocalDateTime.now()) );		

		
		String ric = "GOOGL.OQ";
		Optional<BigDecimal> avgPrice = matchingService.getAvgExecutionPrice(ric);
		if(avgPrice.isPresent()){
			BigDecimal price = avgPrice.get();
			logger.info("Average execution price for {} is {}",ric,price);
			Assert.assertTrue(price.compareTo(new BigDecimal("1137.54"))==0);
		}else
			fail(String.format("Avg price not computed for %s",ric));
	}
	
	@Test
	public void testGetExecutiedQtyBuy() {
		
		initWithDiffPriceAndTime(Direction.SELL);
		
		matchingService.addOrder( new Order(Direction.BUY,"GOOGL.OQ",100,new BigDecimal("1122.20"),"User2",LocalDateTime.now()) );
		matchingService.addOrder( new Order(Direction.BUY,"GOOGL.OQ",100,new BigDecimal("1160.20"),"User2",LocalDateTime.now()) );		
		matchingService.addOrder( new Order(Direction.BUY,"GOOGL.OQ",100,new BigDecimal("1130.20"),"User2",LocalDateTime.now()) );		
		
		String ric = "GOOGL.OQ";
		
		String user = "User2";
		RicUserKey key = new RicUserKey(ric, user);			
		Optional<Integer> execQty = matchingService.getExecutiedQty(key);
		if(execQty.isPresent()){
			int qty = execQty.get();
			logger.info("Executed quantity for {} {} is {}",ric,user,qty);
			Assert.assertTrue(qty == 300);
		}else
			fail(String.format("Unable to get executed qty for {} {} %s",ric,user ));
		

		user = "User1";
		key = new RicUserKey(ric, user);			
		execQty = matchingService.getExecutiedQty(key);
		if(execQty.isPresent()){
			int qty = execQty.get();
			logger.info("Executed quantity for {} {} is {}",ric,user,qty);
			Assert.assertTrue(qty == -300);
		}else
			fail(String.format("Unable to get executed qty for {} {} %s",ric,user ));
	}
	
	@Test
	public void testGetExecutiedQtySell() {
		
		initWithDiffPriceAndTime(Direction.BUY);
		
		matchingService.addOrder( new Order(Direction.SELL,"GOOGL.OQ",100,new BigDecimal("1122.20"),"User2",LocalDateTime.now()) );
		matchingService.addOrder( new Order(Direction.SELL,"GOOGL.OQ",100,new BigDecimal("1160.20"),"User2",LocalDateTime.now()) );		
		matchingService.addOrder( new Order(Direction.SELL,"GOOGL.OQ",100,new BigDecimal("1130.20"),"User2",LocalDateTime.now()) );		

		
		String ric = "GOOGL.OQ";
		String user = "User2";
		RicUserKey key = new RicUserKey(ric, user);			
		Optional<Integer> execQty = matchingService.getExecutiedQty(key);
		if(execQty.isPresent()){
			int qty = execQty.get();
			logger.info("Executed quantity for {} {} is {}",ric,user,qty);
			Assert.assertTrue(qty == -100);
		}else
			fail(String.format("Unable to get executed qty for {} {} %s",ric,user ));

		user = "User1";
		key = new RicUserKey(ric, user);			
		execQty = matchingService.getExecutiedQty(key);
		if(execQty.isPresent()){
			int qty = execQty.get();
			logger.info("Executed quantity for {} {} is {}",ric,user,qty);
			Assert.assertTrue(qty == 100);
		}else
			fail(String.format("Unable to get executed qty for {} {} %s",ric,user ));
		
	}
	
	//should pick latest timestamp, 1 remaining
	private void initWithDiffPrices(Direction dir) {
		matchingService.addOrder( new Order(dir,"GOOGL.OQ",100,new BigDecimal("1119.20"),"User1",LocalDateTime.parse("2018-02-02T10:15:30")) );
		matchingService.addOrder( new Order(dir,"GOOGL.OQ",100,new BigDecimal("1125.20"),"User1",LocalDateTime.parse("2018-02-02T12:10:30")) );		
	}

	//should pick latest timestamp, 1 remaining
	private void initWithSamePriceDiffTime(Direction dir) {
		matchingService.addOrder( new Order(dir,"GOOGL.OQ",100,new BigDecimal("1119.20"),"User1",LocalDateTime.parse("2018-02-02T10:15:30")) );
		matchingService.addOrder( new Order(dir,"GOOGL.OQ",100,new BigDecimal("1119.20"),"User1",LocalDateTime.parse("2018-02-02T12:10:30")) );		
	}
	
	//should pick higher price if sell, 2 remaining
	//should pick lower price if buy, 2 remaining
	private void initWithDiffPriceAndTime(Direction dir) {
		matchingService.addOrder( new Order(dir,"GOOGL.OQ",100,new BigDecimal("1119.20"),"User1",LocalDateTime.parse("2018-02-02T10:15:30")) );
		matchingService.addOrder( new Order(dir,"GOOGL.OQ",100,new BigDecimal("1119.20"),"User1",LocalDateTime.parse("2018-02-02T12:10:30")) );
		matchingService.addOrder( new Order(dir,"GOOGL.OQ",100,new BigDecimal("1125.20"),"User1",LocalDateTime.parse("2018-02-02T10:15:30")) );	
		matchingService.addOrder( new Order(dir,"GOOGL.OQ",100,new BigDecimal("1125.20"),"User1",LocalDateTime.parse("2018-02-02T12:10:30")) );		
	}
	
	//should pick proper ric, 1 remaining
	private void initWithDiffRicCodes(Direction dir) {
		matchingService.addOrder( new Order(dir,"IBM.N",100,new BigDecimal("159.03"),"User1",LocalDateTime.parse("2018-02-02T10:15:30")) );
		matchingService.addOrder( new Order(dir,"GOOGL.OQ",100,new BigDecimal("1119.20"),"User1",LocalDateTime.parse("2018-02-02T10:15:30")) );		
	}
	
	private void init(Direction dir) {
		matchingService.addOrder( new Order(dir,"IBM.N",100,new BigDecimal("159.03"),"User1",LocalDateTime.parse("2018-02-02T10:15:30")) );
		matchingService.addOrder( new Order(dir,"GOOGL.OQ",100,new BigDecimal("1119.20"),"User1",LocalDateTime.parse("2018-02-02T10:15:30")) );
		matchingService.addOrder( new Order(dir,"GOOGL.OQ",100,new BigDecimal("1119.20"),"User1",LocalDateTime.parse("2018-02-02T12:10:30")) );		
	}
}
