package com.exchange.model;

/**
 * Represents Order direction.  Either BUY or SELL.
 * 
 * @author Elliot G
 */
public enum Direction {
	BUY, 
	SELL;
	
	public Direction getOpposite(){
		return (this == BUY) ? SELL : BUY ;
	}
}
