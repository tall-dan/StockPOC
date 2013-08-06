import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Algorithm1 implements iTradeAlgorithm {
	SQLDBConnection conn;
	private double tradeFee = 9.99;
	ArrayList<Stock> stocks;
	private double minimumProfitPerSale;
	public Algorithm1() {
		this.conn = new SQLDBConnection();
		getOwnedStocks();
	}

	private void getOwnedStocks() {
		this.stocks = new ArrayList<Stock>();
		ResultSet rows = conn.executeQuery("Select * from stocks");
		try {
			while (rows.next()) {
				String name = rows.getString("Ticker_name");
				double buyPrice = Double.valueOf(rows.getString("Buy_Price"));
				int shares = Integer.valueOf(rows.getString("Shares"));
				double maxLoss = Double.valueOf(rows.getString("Max_loss"));
				DateFormat dateFormat = new SimpleDateFormat(
						"yyyy/MM/dd HH:mm:ss");
				Date buyDate = null;
				buyDate = dateFormat.parse(rows.getString("Buy_date"));
				this.stocks.add(new Stock(name, buyPrice, shares, maxLoss,
						buyDate));
			}
			rows.close();
		} catch (ParseException exception) {
			System.err.println("Error parsing date from DB");
			exception.printStackTrace();
			System.exit(-1);
		} catch (SQLException e) {
			System.err.println("SQL exception caused by getString()");
			e.printStackTrace();
			System.exit(-1);
		}
	}

	@Override
	public ArrayList<Stock> stocksToBuy() {
		/*
		 * logTransaction(); addStockToTable(stock);
		 */
		return null;
	}

	@Override
	public ArrayList<Stock> stocksToSell() {
		ArrayList<Stock> stocksToSell = new ArrayList<Stock>();
		for (Stock stock : this.stocks) {
			double currentPrice = getCurrentPrice(stock);
			if ((currentPrice - stock.getBuyPrice()) * stock.getShares() > tradeFee+minimumProfitPerSale) {
				stocksToSell.add(stock);
				logTransaction(stock, currentPrice, "sell");
				removeStockFromTable(stock);
			}
		}

		return stocksToSell;
	}

	private void removeStockFromTable(Stock stock) {
		String sql = "Delete from stocks where Ticker_Name='" + stock.getName()
				+ "';";
		this.conn.executeUpdate(sql);
	}

	private double getCurrentPrice(Stock stock) {

		String sql = "Select Price from " + stock.getName()
				+ "_prices order by Time desc limit 1";
		ResultSet price = conn.executeQuery(sql);
		double currentPrice = -1;
		try {
			price.next();
			currentPrice = Double.valueOf(price.getString(1));
		} catch (SQLException e) {
			System.err.println("no price entry for" + stock.getName());
			e.printStackTrace();
			System.exit(-1);
		}
		return currentPrice;
	}

	@Override
	public void logTransaction(Stock stock, double currentPrice,
			String buyOrSell) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		String sql = "Insert into Transaction_History (Ticker_name, Price, Shares, BUY_SELL, Profit, Time) Values ('"
				+ stock.getName()
				+ "', "
				+ currentPrice
				+ ", "
				+ stock.getShares()
				+ ", '"
				+ buyOrSell
				+ "', "
				+ ((currentPrice - stock.getBuyPrice()) * stock.getShares())
				+ ", '" + dateFormat.format(date) + "');";
			this.conn.executeUpdate(sql);
	}

}
