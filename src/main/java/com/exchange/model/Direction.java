package com.exchange.model;

public enum Direction {
	BUY, 
	SELL;
	
	public Direction getOpposite(){
		return (this == BUY) ? SELL : BUY ;
	}
}
