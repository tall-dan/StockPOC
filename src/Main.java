import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.ArrayList;

import model.Algorithm1;
import model.Algorithm2;
import model.SQLDBConnection;
import model.Stock;
import model.eTrader;
import model.iTradeAlgorithm;
import eTrade.MarketManager;

public class Main {

	/**
	 * @param args
	 */

	public static void main(String[] args) {
		eTrader trader = new eTrader();// something like etrade that'll do our
										// trading for us
		SQLDBConnection conn = new SQLDBConnection();
		
		iTradeAlgorithm algorithm = new Algorithm1();// our algorithm for
														// buying/selling
		iTradeAlgorithm algorithm2= new Algorithm2();
		ArrayList<String> tickerNames = conn.getFollowedStocks();
		/*
		 * Make sure you're following your owned stocks
		 */


		try {
			ArrayList<Stock> stocks = MarketManager.getStockData(tickerNames);//returns stock with current price & symbol
			conn.createViews(stocks);
			while (true) {
				Stock.pushPricesToDB(stocks);//allows our algorithm to do some stat analysis
				trader.buy(algorithm.stocksToBuy(stocks));//pass stocks. Probably better way to do this.
				trader.sell(algorithm.stocksToSell());
				Thread.sleep(1000);
				stocks=MarketManager.getStockData(tickerNames);
			}
		} catch (InterruptedException e) {
			System.err.println(e.getMessage());
			System.err.println(e.getCause());
			e.printStackTrace();
			System.exit(-1);
		} 
	}



}
