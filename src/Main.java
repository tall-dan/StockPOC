import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;

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
		ArrayList<Stock> tickerNames = conn.getFollowedStocks();
		/*
		 * Make sure you're following your owned stocks
		 */
		conn.createViews(tickerNames);
		String separatedVals = concatonateVals(tickerNames);
		URL url;
		BufferedReader reader = null;

		
		/*
		 * Runtime r = Runtime.getRuntime(); long memBefore=0; long memAfter=0;
		 */
		while (true) {
			try {
				
				/*memAfter = r.totalMemory();
				 * if (memBefore!=memAfter){
				 * System.out.println("memory used by the JVM is "+memAfter);
				 * memBefore=memAfter; }
				 */
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
				algorithm.updateOwnedStocks();
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

	private static ArrayList<Stock> getPricesFromURI(
			ArrayList<Stock> tickerNames, BufferedReader reader)
			throws IOException {
		int i = 0;
		ArrayList<Stock> toRet = new ArrayList<Stock>();
		double currentPrice = -1;
		for (String line; (line = reader.readLine()) != null;) {
			currentPrice = Double.valueOf(line);
			Stock stock = new Stock(tickerNames.get(i).getName(), currentPrice,
					-1, -1, null);
			i++;
			toRet.add(stock);
		}
		return toRet;
	}

	private static String concatonateVals(ArrayList<Stock> tickerNames) {
		if (tickerNames.size() == 0) {
			tickerNames = Stock.getStocksFromUser();
		}
		String returnVal = tickerNames.get(0).getName();
		for (int i = 1; i < tickerNames.size(); i++) {
			returnVal += "+" + tickerNames.get(i).getName();
		}
		System.out.println(returnVal);
		return returnVal;
	}

}
