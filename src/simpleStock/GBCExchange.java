package simpleStock;

import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GBCExchange {
	private static final Logger LOG = Logger.getGlobal();
	public static final Level globalLogLevel = Level.SEVERE;

	public enum Operation {BUY, SELL}
//	private ArrayList<Stock> allGBEStocks;
	private HashMap<String, Stock> allGBEStocks;
	
	public GBCExchange() {
//		allGBEStocks = new ArrayList<Stock>();
		allGBEStocks = new HashMap<String, Stock>();
		LOG.setLevel(globalLogLevel);
	}

	public void addStock (Stock stock){
//		allGBEStocks.add(stock);
		if (!allGBEStocks.containsKey(stock.getSymbol())){
			allGBEStocks.put(stock.getSymbol(), stock);			
		}
	}
	
	/**
	 * Return the current value for the GBCE All Share Index.
	 * 
	 * */
	public double allShareIndex(Iterator<Stock> stocks){
		double product = 1.0;    /*Idempotente*/
		int count = 0;
		while (stocks.hasNext()){
			Stock stock = stocks.next();
			LOG.warning(stock.getSymbol());
			double onePrice = stock.calcPrice(); 
			if (-1.0 != onePrice) product *= onePrice;
			count ++;
			LOG.warning("product: "+product+" count: "+count);
		}
		/*assuming all prices are positive*/
		LOG.warning("product: "+product+" count: "+count);
		return Math.pow(product, (1/(double)count));
	}
	
	public Iterator<Stock> listAllStocks(){
//		return allGBEStocks.iterator();
		return allGBEStocks.values().iterator();
	}
	
	public boolean registerTrade(Trade trade){
		if (allGBEStocks.containsKey(trade.getStockSymbol())) {
			allGBEStocks.get(trade.getStockSymbol()).recordTrade(trade);
			LOG.warning("Trading: " + trade.getStockSymbol());
			return true;
		}else {
			return false;
		}
	}
	
	public void discard(){
		Iterator<Stock> stocks = allGBEStocks.values().iterator();
		while (stocks.hasNext()){
			Stock stock = stocks.next();
			stock.discard();
			stocks.remove();
		}
	}
}
