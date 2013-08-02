import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		eTrader trader = new eTrader();// something like etrade that'll do our
										// trading for us
		SQLDBConnection conn = null;
		try {
			conn = new SQLDBConnection();
		} catch (ClassNotFoundException | SQLException e1) {
			
			e1.printStackTrace();
		}
		iTradeAlgorithm algorithm = new Algorithm1();// our algorithm for buying/selling
		ArrayList<String> tickerNames = conn.getFollowedStocks();
		conn.createViews(tickerNames);
		String separatedVals = concatonateVals(tickerNames);
		URL url;
		BufferedReader reader = null;

		while (true) {
			try {
				url = new URL(
						"http://download.finance.yahoo.com/d/quotes.csv?s="
								+ separatedVals + "&f=l1&e=.csv");
				reader = new BufferedReader(new InputStreamReader(
						url.openStream(), "UTF-8"));
				ArrayList<Stock> currentPrices = getPricesFromURI(tickerNames,
						reader);
				Stock.pushPricesToDB(currentPrices);
				trader.buy(algorithm.stocksToBuy());
				trader.sell(algorithm.stocksToSell());
				Thread.sleep(1000);
			} catch (IOException | InterruptedException
					| ClassNotFoundException | SQLException e) {
				System.err.println(e.getMessage());
				System.err.println(e.getCause());
				e.printStackTrace();
				System.exit(-1);
			} finally {
				if (reader != null)
					try {
						reader.close();
					} catch (IOException ignore) {
					}
			}
		}
	}

	/*
	 * Keeps us from inserting the same data into the table repeatedly
	 */
	private static ArrayList<Stock> getPricesFromURI(
			ArrayList<String> tickerNames, BufferedReader reader)
			throws IOException {
		int i = 0;
		ArrayList<Stock> toRet = new ArrayList<Stock>();
		double currentPrice = -1;
		for (String line; (line = reader.readLine()) != null;) {
			currentPrice = Double.valueOf(line);
			Stock stock = new Stock(tickerNames.get(i), currentPrice, -1, -1);
			i++;
			toRet.add(stock);
		}
		return toRet;
	}

	private static String concatonateVals(ArrayList<String> tickerNames) {
		if (tickerNames.size()==0){
			tickerNames=Stock.getStocksFromUser();
		}
		String returnVal = tickerNames.get(0);
		for (int i = 1; i < tickerNames.size(); i++) {
			returnVal += "+" + tickerNames.get(i);
		}
		System.out.println(returnVal);
		return returnVal;
	}

	
}
