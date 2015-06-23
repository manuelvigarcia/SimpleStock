package simpleStock;

import java.time.Instant;

public class Trade {
	/**Stock Symbol*/
	private final String stockSymbol;
	
	/**number of shares that changed owner in this trade*/
	private final int quantity;
	
	/**amount of pennies paid for {@link #quantity} shares*/
	private final int price;
	
	/**indicator of the nature of the operation: buy or sell*/
	private final GBCExchange.Operation operation;
	
	/**timestamp of the operation*/
	private final Instant timestamp;
	
	public Trade (String symbol, int nofShares, int amount,
			GBCExchange.Operation buyOrSell, Instant time) {
		stockSymbol = symbol;
		quantity = nofShares;
		price = amount;
		operation = buyOrSell;
		timestamp = time;
	}
	public Trade(String symbol, int nofShares, int amount,
			GBCExchange.Operation buyOrSell) {
		this (symbol, nofShares, amount, buyOrSell, Instant.now());
	}
	
	public Instant getTimeStamp(){
		return timestamp;
	}
	
	public String getStockSymbol(){
		return stockSymbol;
	}
	public int getPrice(){
		return price;
	}
	public int getQuantity(){
		return quantity;
	}
	public GBCExchange.Operation getOperation(){
		return operation;
	}
}
