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

	public SQLDBConnection()  {
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

	// Insert, update, or delete
	public int executeUpdate(String sql) {
		int toRet=-1;
		try {
			if (!hasOpenStatementAndConnection())
				reopenConnectionAndStatement();
			return this.stmt.executeUpdate(sql);
		} catch (SQLException exception) {
			System.err.println("SQLException: sql = "+sql);
			exception.printStackTrace();
			System.exit(-1);
		}
		return toRet;
	}

	// Select
	public ResultSet executeQuery(String sql) {
		ResultSet result=null;
		try {
			if (!hasOpenStatementAndConnection())
				reopenConnectionAndStatement();
			result= this.stmt.executeQuery(sql);
		} catch (SQLException exception) {
			System.err.println("SQLException: sql = "+sql);
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
				String name = rows.getString("Ticker_name");
				double buyPrice = Double.valueOf(rows.getString("Buy_Price"));
				int shares = Integer.valueOf(rows.getString("Shares"));
				double maxLoss = Double.valueOf(rows.getString("Max_loss"));
				DateFormat dateFormat = new SimpleDateFormat(
						"yyyy/MM/dd HH:mm:ss");
				Date buyDate = null;
				buyDate = dateFormat.parse(rows.getString("Buy_date"));
				stocks.add(new Stock(name, buyPrice, shares, maxLoss,
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
			System.err.println("exception in convertOneDimensionResultSetToArrayList");
			e.printStackTrace();
			System.exit(-1);
		}
		return result;
	}

	public void createViews(ArrayList<Stock> tickerNames) {
		String sql;
		for (Stock viewName : tickerNames) {
			sql = "create or replace view " + viewName.getName() + "_prices as "
					+ "select * from ticker_prices "
					+ "where ticker_prices.Ticker_name='"+viewName.getName()+"' "
					+ "order by ticker_prices.Time desc;";
			executeUpdate(sql);
			
		}
	}

}
