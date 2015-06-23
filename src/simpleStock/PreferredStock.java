package simpleStock;

import java.util.logging.Level;
//import java.util.logging.Logger;

public class PreferredStock extends Stock{
//	private static final Logger LOG = Logger.getGlobal();
	public static final Level globalLogLevel = Level.SEVERE;

	/**
	 * Percentage that is paid as fixed dividend for this preferred stock.
	 * a fixedDividend of X pays X * Ticker Price / 100
	 * */
	private int fixedDividend;

	/**
	 * Create a preferred stock, with fixed dividend.
	 * */
	public PreferredStock(String stockSymbol, int par, int dividend, int fixDiv) {
		super(stockSymbol, par, dividend);
		fixedDividend = fixDiv;
	}

	/**
	 * Calculate the dividend yield for this preferred stock given the ticker price.
	 * @param ticker price for this stock.
	 * @return dividend yield
	 * */
	@Override
	public double calcDividendYield(int tickerPrice){
		//TODO: provide financial grade operators; do not trust PC arithmetic
		return ((double)(fixedDividend * parValue) / (double)tickerPrice);
	}

}
