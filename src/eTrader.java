import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class eTrader {
	private SQLDBConnection conn = new SQLDBConnection();

	public void buy(ArrayList<Stock> stocksToBuy) {
	}

	public void sell(ArrayList<Stock> stocksToSell) {
		for (Stock stock : stocksToSell) {
			if (allowTrade(stock)) {
				logTransaction(stock, "sell");
				removeStockFromTable(stock);
				updateCash(stock);
				System.out.println("sold some stocks, yo");
			}
		}
	}

	private void updateCash(Stock stock) {
		String sql="Update Current_Cash Set CurrentCash=CurrentCash+"+(stock.getProfit()*stock.getShares())+";";
		this.conn.executeUpdate(sql);
	}

	private boolean allowTrade(Stock s) {
		if (s.isDayTrade()) {
			if (maxNumDayTradesReached())
				return false;
			incrementDayTradesTable(s);
		}
		return true;
	}

	/**
	 * @return
	 */
	private boolean maxNumDayTradesReached() {
		cleanUpDayTradesTable();
		String sql = "Select Buy_time from Day_Trades;";
		ResultSet results = this.conn.executeQuery(sql);
		int resultCount = 0;
		try {
			while (results.next())
				resultCount++;
		} catch (SQLException exception) {
			System.err.println("SQLException: sql = " + sql);
			exception.printStackTrace();
			System.exit(-1);
		}
		return resultCount > 3;
	}

	private void incrementDayTradesTable(Stock s) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		String sql = "Insert into day_trades(Ticker_name, Buy_time, Sell_time) values ('"
				+ s.getName()
				+ "', "
				+ s.getBuyTime()
				+ ", "
				+ dateFormat.format(date) + "');";
		this.conn.executeUpdate(sql);
	}

	private void cleanUpDayTradesTable() {// ideally, we only need to run this
											// 1x a day.
		boolean oldTradesInTable = true;
		String sql = null;
		ResultSet oldest;
		while (oldTradesInTable) {
			try {
				sql = "Select Buy_time from Day_Trades order by Buy_time Limit 1";// get
																					// the
																					// oldest
				oldest = this.conn.executeQuery(sql);
				oldest.next();
				int businessDays = countBusinessDays(
						getDateFromString(oldest.getString(1)), new Date());
				if (businessDays > 5) {
					sql = "Delete from Day_Trades where Buy_time='"
							+ oldest.getString(1) + "';";
					this.conn.executeUpdate(sql);
				} else
					oldTradesInTable = false;
			} catch (SQLException exception) {
				System.err.println("Exception caused by next()");
				exception.printStackTrace();
				System.exit(-1);
			}

		}
	}

	public void logTransaction(Stock stock, String buyOrSell) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		String sql = "Insert into Transaction_History (Ticker_name, Price, Shares, BUY_SELL, Profit, Time) Values ('"
				+ stock.getName()
				+ "', "
				+ stock.getCurrentPrice()
				+ ", "
				+ stock.getShares()
				+ ", '"
				+ buyOrSell
				+ "', "
				+ ((stock.getCurrentPrice() - stock.getBuyPrice()) * stock
						.getShares()) + ", '" + dateFormat.format(date) + "');";
		this.conn.executeUpdate(sql);
	}

	private void removeStockFromTable(Stock stock) {
		String sql = "Delete from stocks where Ticker_Name='" + stock.getName()
				+ "';";
		this.conn.executeUpdate(sql);
	}

	private Date getDateFromString(String s) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = null;
		try {
			date = dateFormat.parse(s);
		} catch (ParseException exception) {
			System.err.println("Error parsing date from DB");
			exception.printStackTrace();
			System.exit(-1);
		}
		return date;
	}

	public static int countBusinessDays(Date start, Date end) {
		// Ignore argument check

		Calendar c1 = Calendar.getInstance();
		c1.setTime(start);
		int w1 = c1.get(Calendar.DAY_OF_WEEK);
		c1.add(Calendar.DAY_OF_WEEK, -w1);

		Calendar c2 = Calendar.getInstance();
		c2.setTime(end);
		int w2 = c2.get(Calendar.DAY_OF_WEEK);
		c2.add(Calendar.DAY_OF_WEEK, -w2);

		// end Saturday to start Saturday
		long days = (c2.getTimeInMillis() - c1.getTimeInMillis())
				/ (1000 * 60 * 60 * 24);
		long daysWithoutSunday = days - (days * 2 / 7);

		return (int) (daysWithoutSunday - w1 + w2);
	}
}
