package simpleStock;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.logging.Logger;


public class Stock {
	private static final Logger LOG = Logger.getGlobal();

	/**Period (in seconds) used to calculate price based on trades.
	 * Calculate for last 15'.*/
	private final int CALCULATION_PERIOD = 15 * 60;

	/**
	 * Last dividend issued for this particular stock. 
	 */
	private int lastDividend;
	
	/**
	 * Par value for this particular stock.
	 * */
	protected final int parValue;
	
	private final String symbol;
	
	private ArrayList<Trade> tradeRegister;

	/**
	 * Create a stock.
	 * */
	public Stock(String stockSymbol, int par, int dividend){
		symbol = stockSymbol;
		parValue = par;
		lastDividend = dividend;
	}
	
	/**Create a stock with no known last dividend*/
	public Stock(String stockSymbol, int par){
		this (stockSymbol, par, 0);
	}
		
	public String getSymbol(){
		return symbol;
	}
	/**
	 * Calculate the dividend yield for this stock given the current ticker price
	 * @param tickerPrice current ticker price for this stock
	 * @return dividend yield.
	 * */
	public double calcDividendYield(int tickerPrice){
		//TODO: provide financial grade operator; do not trust PC arithmetic
		return ((double)lastDividend / (double)tickerPrice);
	}

	/**
	 * Calculate Price-Earnings ratio for this stock, given the ticker price.
	 * */
	public double calcPERatio(int tickerPrice){
		//TODO: provide financial grade operator; do not trust PC arithmetic
		if (0 < lastDividend){
			return ((double)tickerPrice / (double)lastDividend);
		}else{
			return(Double.MAX_VALUE);
		}
	}

	
	/**
	 * Record a trade on this stock.
	 * */
	public void recordTrade (Trade trade){
		if (null == tradeRegister){
			tradeRegister  = new ArrayList<Trade>();
		}
		tradeRegister.add(trade);
	}

	/**
	 * Calculate the price of a stock based on the recorded trade operations of
	 * the last 15' (sum of quantities x prices divided by sum of quantities)
	 * @return the freshly calculated price or -1 if it couldn't find trade
	 * operations on that stock.
	 * */
	public double calcPrice (){
		if (null == tradeRegister) return -1;
		
		ListIterator<Trade> allTrades = tradeRegister.listIterator();
		int priceQtty = 0;
		int quantity = 0;
		
		final Instant when = Instant.now().minusSeconds(CALCULATION_PERIOD);
		while (allTrades.hasNext()){
			Trade trade = allTrades.next();
			if (0 > when.compareTo(trade.getTimeStamp())){
				priceQtty += trade.getPrice()*trade.getQuantity();
				quantity += trade.getQuantity();
			}
		}
		if (0 < quantity){
			LOG.warning(symbol +" PriceQ: " + priceQtty + " Qtty: " + quantity);
			LOG.warning(symbol + ": " +(double)priceQtty / (double)quantity);
			//TODO: provide financial grade operator; do not trust PC arithmetic
			return ((double)priceQtty / (double)quantity);
		}else{
			LOG.severe(symbol + " not traded within period of calculation.");
			return -1.0;
		}
	}
	
	
	/**
	 * Get the list of trade operations for this stock
	 * @return Iterator on the register of trade operations or null if there are no operations
	 * registered.
	 * */
	public Iterator<Trade> getTradeRecord(){
		if (null == tradeRegister) return null;
		else return tradeRegister.iterator();
	}
	
	public void discard(){
		tradeRegister = null;
	}
}
