package com.jesper.redis;

public class TradeKey extends BasePrefix {

	private TradeKey(int expireSeconds, String prefix) {
		super(expireSeconds, prefix);
		// TODO Auto-generated constructor stub
	}

	public static final int TN_EXPIRE = 3600*24 *1;//一天
	
	public static TradeKey trade = new TradeKey(TN_EXPIRE,"trade");
}
