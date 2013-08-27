package model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

/**
 * TODO Put here a description of what this class does.
 * 
 * @author schepedw. Created Apr 30, 2013. Modified July 31, 2013
 */
public class SQLDBConnection {
	private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";

	private Connection conn;
	private Statement stmt;
	private String db_url;
	private String user;
	private String password;

	public SQLDBConnection() {
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream("local.properties"));
			String DBname = prop.getProperty("dbname");
			this.user = prop.getProperty("user");
			this.password = prop.getProperty("password");
			this.db_url = "jdbc:mysql://localhost:3306/" + DBname;
			Class.forName(JDBC_DRIVER);
			if (!user.equals("none")) {
				this.conn = DriverManager.getConnection(db_url, user, password);
			} else {
				this.conn = DriverManager.getConnection(db_url);
			}
			this.stmt = this.conn.createStatement();
		} catch (ClassNotFoundException | SQLException | IOException e) {
			System.err.println("Exception in constructor: " + e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
	}

	protected double getCurrentCash() {
		ResultSet moneySet = executeQuery("select * from Current_Cash");
		try {
			moneySet.next();
			return moneySet.getDouble(1);
		} catch (SQLException e) {
			System.err.println("Failed to get cash from DB");
			System.err.println(e.getStackTrace());
			System.err.println(e.getMessage());
			System.exit(-1);
		}
		return -1;
	}

	// Insert, update, or delete
	public int executeUpdate(String sql) {
		int toRet = -1;
		try {
			if (!hasOpenStatementAndConnection())
				reopenConnectionAndStatement();
			return this.stmt.executeUpdate(sql);
		} catch (SQLException exception) {
			System.err.println("SQLException: sql = " + sql);
			exception.printStackTrace();
			System.exit(-1);
		}
		return toRet;
	}

	// Select
	public ResultSet executeQuery(String sql) {
		ResultSet result = null;
		try {
			if (!hasOpenStatementAndConnection())
				reopenConnectionAndStatement();
			result = this.stmt.executeQuery(sql);
		} catch (SQLException exception) {
			System.err.println("SQLException: sql = " + sql);
			exception.printStackTrace();
			System.exit(-1);
		}
		return result;
	}

	private boolean hasOpenStatementAndConnection() throws SQLException {
		return !this.conn.isClosed() && !this.stmt.isClosed();
	}

	private void reopenConnectionAndStatement() {
		try {
			if (this.conn == null || this.conn.isClosed())
				this.conn = DriverManager.getConnection(this.db_url, this.user,
						this.password);
			if (this.stmt == null || this.stmt.isClosed())
				this.stmt = this.conn.createStatement();
		} catch (SQLException exception) {
			exception.printStackTrace();
			System.exit(-1);
		}
	}

	protected ArrayList<Stock> getOwnedStocks() {
		ArrayList<Stock> stocks = new ArrayList<Stock>();
		ResultSet rows = executeQuery("Select * from Owned_Stocks");
		try {
			while (rows.next()) {
				String name = rows.getString("tickerName");
				double buyPrice = Double.valueOf(rows.getString("buyPrice"));
				int shares = Integer.valueOf(rows.getString("shares"));
				DateFormat dateFormat = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				Date buyDate = null;
				String stringBuyDate = rows.getString("buyDate").substring(0,
						19);
				buyDate = dateFormat.parse(stringBuyDate);
				stocks.add(new Stock(name, buyPrice, shares, buyDate));
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
		return stocks;
	}

	/**
	 * This returns an array of stock names. Each stock has
	 * mostRecentlySoldPrice as its buy price
	 * 
	 * @return ArrayList<Stock>
	 */

	public ArrayList<String> getFollowedStocks() {
		ArrayList<String> stocks = new ArrayList<String>();
		ResultSet rows = executeQuery("Select * from Followed_Stocks");
		try {
			while (rows.next()) {

				String name = rows.getString("tickerName");
				/*
				 * Double mostRecentSellPrice=
				 * rows.getDouble("mostRecentSellPrice"); mostRecentSellPrice =
				 * (mostRecentSellPrice==0? 1000000: mostRecentSellPrice); Stock
				 * stock=new Stock(name,mostRecentSellPrice,0,0,null);
				 * stocks.add(stock);
				 */
				stocks.add(name);

			}
			rows.close();
		} catch (SQLException e) {
			System.err.println("SQL exception caused by getString()");
			e.printStackTrace();
			System.exit(-1);
		}
		return stocks;
	}

	protected static ArrayList<String> convertOneDimensionResultSetToArrayList(
			ResultSet names) {
		ArrayList<String> result = new ArrayList<String>();
		try {
			while (names.next()) {
				result.add(names.getString(1));
			}
			names.close();
		} catch (SQLException e) {
			System.err
					.println("exception in convertOneDimensionResultSetToArrayList");
			e.printStackTrace();
			System.exit(-1);
		}
		return result;
	}

	public void createViews(ArrayList<Stock> tickerNames) {
		String sql;
		for (Stock viewName : tickerNames) {
			sql = "create or replace view " + viewName.getName()
					+ "_Prices as " + "select * from Ticker_Prices "
					+ "where Ticker_Prices.tickerName='" + viewName.getName()
					+ "' " + "order by Ticker_Prices.time desc;";
			executeUpdate(sql);

		}
	}

}
