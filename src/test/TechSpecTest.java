package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.time.Instant;
import java.util.Iterator;
import java.util.logging.Logger;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import simpleStock.Stock;
import simpleStock.PreferredStock;
import simpleStock.GBCExchange;
import simpleStock.GBCExchange.Operation;
import simpleStock.Trade;

/**Test the requirements from the Technical Test specification (the .docx)*/
public class TechSpecTest {

	private static final Logger LOG = Logger.getGlobal();

	private static GBCExchange gbce;

	private static  Object [][] sampleStocks = {
		   /*symbol, Pref?, div, Fix, par*/
			 {"TEA", false,  0,   0,  100}
			,{"POP", false,  8,   0,  100}
			,{"ALE", false, 23,   0,   60}
			,{"GIN", true,   8,   2,  100}
			,{"JOE", false, 13,   0,  250}
		};
		
	public TechSpecTest() {
	}

	@BeforeClass
	public static void fillStocks(){
		gbce = new GBCExchange();
		for (Object[] i : sampleStocks) {
			if ((boolean) i[1]){
				gbce.addStock((Stock)(new PreferredStock((String)i[0],
						(int)i[4], (int)i[2], (int)i[3])));
			}else{
				gbce.addStock(new Stock((String)i[0], (int)i[4], (int)i[2]));
			}
		}
	}
	
	@AfterClass
	public static void tearDown() {
		gbce.discard();
		gbce = null;
	}
	
	@Test
	public void dividendYieldTest() {
		final String[] symbols = {      "TEA", "POP", "ALE", "GIN", "JOE"};
		                /*Last dividends    0,     8,    23,    2%,   13*/
		final int []    tickerPrices   = {100,   100,   100,   100,   100};
		final double [] dividendYields = {0.0,  0.08,  0.23,   2.0,  0.13};
		boolean [] visited             = {false,false,false, false, false};
		Iterator<Stock> allStocks = gbce.listAllStocks();
		while (allStocks.hasNext()){
			Stock stock = allStocks.next();
			for (int j = 0; j < symbols.length; j++){
				if (symbols[j].equals(stock.getSymbol())){
					if (visited[j]) {
						fail("visited twice " + symbols[j]);
					}
					visited[j] = true;
					double dividendYield = stock.calcDividendYield(tickerPrices[j]);
					assertEquals(dividendYields[j], dividendYield, 0.0);
					break;
				}
			}
		}
		for (boolean i : visited){
			if (!i) fail("One stock was not visited.");
		}
	}
	
	@Test
	public void calcPERatioTest() {
		final String[] symbols =   {        "TEA","POP",      "ALE","GIN",      "JOE"};
						  /* Last dividends     0,    8,         23,   2%,        13*/
		final int[] tickerPrices = {          100,  100,        100,  100,       100 };
		final double[] ratios = {Double.MAX_VALUE, 12.5, 100.0/23.0, 12.5, 100.0/13.0};
		boolean [] visited               = {false,false,      false,false,      false};
		Iterator<Stock> allStocks = gbce.listAllStocks();
		while (allStocks.hasNext()) {
			Stock stock = allStocks.next();
			for (int j = 0; j < symbols.length; j++){
				if (symbols[j].equals(stock.getSymbol())){
					if (visited[j]) {
						fail("visited twice " + symbols[j]);
					}
					visited[j] = true;
					double ratio = stock.calcPERatio(tickerPrices[j]);
					assertEquals(ratios[j], ratio, 0.0);
					break;
				}
			}
		}
		for (boolean i : visited){
			if (!i) fail("One stock was not visited.");
		}
	}
	
	@Test
	public void tradeRecordingTest(){
		final String[] symbols = {"TEA", "POP", "ALE", "GIN", "JOE","TEA", "POP", "ALE", "GIN", "JOE"};
		final int [] nofShares = {    1,     2,     3,     4,     5,    6,     7,     8,     9,    10}; 
		final int [] prices =    {    3,     6,     9,    12,    15,   18,    21,    24,    27,    30};
		final GBCExchange.Operation [] buySell = {Operation.BUY, Operation.SELL,
				Operation.BUY, Operation.SELL, Operation.BUY, Operation.SELL, Operation.BUY,
				Operation.SELL, Operation.BUY, Operation.SELL};
		final Instant[] timestamps = {Instant.ofEpochSecond(100)
									 ,Instant.ofEpochSecond(200)
									 ,Instant.ofEpochSecond(300)
									 ,Instant.ofEpochSecond(400)
									 ,Instant.ofEpochSecond(500)
									 ,Instant.ofEpochSecond(600)
									 ,Instant.ofEpochSecond(700)
									 ,Instant.ofEpochSecond(800)
									 ,Instant.ofEpochSecond(900)
									 ,Instant.ofEpochSecond(1000)
								 };
		boolean [] visited = {false, false, false, false, false};

		LOG.warning("tradeRecordingTest.");

		/*clear all trades, if any.*/
		
		Iterator<Stock> allStocks = gbce.listAllStocks();
		while (allStocks.hasNext()){
			Stock stock = allStocks.next();
			stock.discard();
			Iterator<Trade> tradeRecord = stock.getTradeRecord();
			assertNull(tradeRecord);
		}
		
		for (int i = 0; i < symbols.length; i++) {
			Trade trade = new Trade(symbols[i], nofShares[i], prices[i],
					buySell[i], timestamps[i]);
			if (!gbce.registerTrade(trade)){
				fail("trade not registered " + trade.getStockSymbol());
			}		
		}
		allStocks = gbce.listAllStocks();
		while (allStocks.hasNext()){
			Stock stock = allStocks.next();
			for (int i = 0; i < symbols.length/2; i++){
				if (symbols[i].equals(stock.getSymbol())){
					visited[i]= true;
					Iterator<Trade> tradeRecord = stock.getTradeRecord();
					assertNotNull(tradeRecord);
					Trade trade = tradeRecord.next();
					assertEquals(nofShares[i], trade.getQuantity());
					assertEquals(prices[i], trade.getPrice());
					assertEquals(buySell[i], trade.getOperation());
					assertEquals(timestamps[i], trade.getTimeStamp());
					trade = tradeRecord.next();
					i += 5;
					assertEquals(nofShares[i], trade.getQuantity());
					assertEquals(prices[i], trade.getPrice());
					assertEquals(buySell[i], trade.getOperation());
					assertEquals(timestamps[i], trade.getTimeStamp());
					assertFalse(tradeRecord.hasNext());
					break;
				}
			}
		}
	}
	
	@Test
	public void priceCalculationTest(){
		final String[] symbols = {"TEA", "POP", "ALE", "GIN", "JOE","TEA", "POP", "ALE", "GIN", "JOE"};
		final int [] nofShares = {    1,     2,     3,     4,     5,    6,     7,     8,     9,    10}; 
		final int [] prices =    {    3,     6,     9,    12,    15,   18,    21,    24,    27,    30};
		final GBCExchange.Operation [] buySell = {Operation.BUY, Operation.SELL,
				Operation.BUY, Operation.SELL, Operation.BUY, Operation.SELL, Operation.BUY,
				Operation.SELL, Operation.BUY, Operation.SELL};
		final double[] stockPrices = {111.0/7.0, 159.0/9.0, 219.0/11.0, 291.0/13.0, 25.0};
		boolean [] visited = {false, false, false, false, false};

		LOG.warning("priceCalculation.");
		for (int i = 0; i < symbols.length; i++) {
			Trade trade = new Trade(symbols[i], nofShares[i], prices[i],
					buySell[i], Instant.now());
			if (!gbce.registerTrade(trade)){
				fail("trade not registered " + trade.getStockSymbol());
			}		
		}
		Iterator<Stock> allStocks = gbce.listAllStocks();
		while (allStocks.hasNext()){
			Stock stock = allStocks.next();
			for (int i = 0; i < symbols.length/2; i++){
				if (symbols[i].equals(stock.getSymbol())){
					visited[i]= true;
					assertEquals(stockPrices[i], stock.calcPrice(), 0.0);
				}
			}
		}
		for (boolean i : visited){
			if (!i) fail("One stock was not visited.");
		}
	}
	
	@Test
	public void indexCalculationTest(){
		final String[] symbols = {"TEA", "POP", "ALE", "GIN", "JOE","TEA", "POP", "ALE", "GIN", "JOE"};
		final int [] nofShares = {    1,     2,     3,     4,     5,    2,     3,     5,     3,    10}; 
		final int [] prices =    {    4,     2,     5,    10,    15,   10,     7,    13,     10,    30};
		final GBCExchange.Operation [] buySell = {Operation.BUY, Operation.SELL,
				Operation.BUY, Operation.SELL, Operation.BUY, Operation.SELL, Operation.BUY,
				Operation.SELL, Operation.BUY, Operation.SELL};
		
		LOG.warning("IndexCalculation.");
		for (int i = 0; i < symbols.length; i++) {
			Trade trade = new Trade(symbols[i], nofShares[i], prices[i],
					buySell[i], Instant.now());
			if (!gbce.registerTrade(trade)){
				fail("trade not registered " + trade.getStockSymbol());
			}		
		}
		double index = gbce.allShareIndex(gbce.listAllStocks());
		assertEquals(10.0, index, 2.0e-15d);

		/*clear all trades, if any.*/
		Iterator<Stock> allStocks = gbce.listAllStocks();
		while (allStocks.hasNext()){
			Stock stock = allStocks.next();
			stock.discard();
			Iterator<Trade> tradeRecord = stock.getTradeRecord();
			assertNull(tradeRecord);
		}
		
	}
}
