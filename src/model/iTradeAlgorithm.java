package model;

import java.util.ArrayList;


public interface iTradeAlgorithm {

	abstract ArrayList<Stock> stocksToBuy() ;
	
	/**
	 * 
	 * @return a list of stocks. Returns an empty list if you don't have the money or there are no 'good' purchases
	 */
	ArrayList<Stock> stocksToSell();

	void updateOwnedStocks();
	
}
