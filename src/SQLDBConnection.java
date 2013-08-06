import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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
		try {
			if (!hasOpenStatementAndConnection())
				reopenConnectionAndStatement();
			return this.stmt.executeQuery(sql);
		} catch (SQLException exception) {
			System.err.println("SQLException: sql = "+sql);
			exception.printStackTrace();
			System.exit(-1);
		}
		finally{
			System.err.println("An uncaught error passed through executeQuery");
			return null;
		}
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

	protected static ArrayList<String> getOwnedStocks() {
		ArrayList<String> toRet = null;
			SQLDBConnection conn = new SQLDBConnection();
			String sql = "Select Ticker_name from stocks";
			ResultSet names = conn.executeQuery(sql);
			toRet = convertOneDimensionResultSetToArrayList(names);
		return toRet;
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

	public void createViews(ArrayList<String> tickerNames) {
		String sql;
		for (String viewName : tickerNames) {
			sql = "create or replace view " + viewName + "_prices as "
					+ "select * from ticker_prices "
					+ "where ticker_prices.Ticker_name='"+viewName+"' "
					+ "order by ticker_prices.Time desc;";
			executeUpdate(sql);
			
		}
	}

}
