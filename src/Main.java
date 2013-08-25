import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.UnknownHostException;
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
		InputStream in = null;
		try {
			url = new URL("http://download.finance.yahoo.com/d/quotes.csv?s="
					+ separatedVals + "&f=l1&e=.csv");
			while (true) {
				in = url.openStream();

				/*
				 * memAfter = r.totalMemory(); if (memBefore!=memAfter){
				 * System.out.println("memory used by the JVM is "+memAfter);
				 * memBefore=memAfter; }
				 */

				reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
				ArrayList<Stock> currentPrices = getPricesFromURI(tickerNames,
						reader);
				Stock.pushPricesToDB(currentPrices);
				trader.buy(algorithm.stocksToBuy());
				trader.sell(algorithm.stocksToSell());
				algorithm.updateOwnedStocks();
				Thread.sleep(1000);
			}
		} catch (InterruptedException | ClassNotFoundException | SQLException e) {
			System.err.println(e.getMessage());
			System.err.println(e.getCause());
			e.printStackTrace();
			System.exit(-1);
		} catch (IOException e) {
			if (e.getClass().equals(UnknownHostException.class)) {
				System.err.println("Yahoo is experiencing difficulties");
				try {
					in.close();
				} catch (IOException e1) {
					System.err
							.println("Could not close input stream.  Exiting");
					System.exit(-1);
				}
			} else {
				System.err.println(e.getMessage());
				System.err.println(e.getCause());
				e.printStackTrace();
				System.exit(-1);
			}

		} finally {
			if (reader != null)
				try {
					reader.close();
				} catch (IOException ignore) {
				}
		}
	}

	private static ArrayList<Stock> getPricesFromURI(
			ArrayList<Stock> tickerNames, BufferedReader reader)
			throws IOException {
		int i = 0;
		ArrayList<Stock> toRet = new ArrayList<Stock>();
		double currentPrice;
		//String printString="";
		for (String line; (line = reader.readLine()) != null;) {
			currentPrice = Double.valueOf(line);
			Stock stock = new Stock(tickerNames.get(i).getName(), currentPrice,
					-1, -1, null);
			i++;
			toRet.add(stock);
			//printString+=stock.getName()+" "+currentPrice+"\t";
		}
		//System.out.println(printString);
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
