import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * TODO Put here a description of what this class does.
 * 
 * @author schepedw. Created Apr 30, 2013. Modified July 31, 2013
 */
public class SQLDBConnection {
	private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";

	private Connection conn;
	private Statement stmt;

	private String user;
	private String password;
	private String db_url;

	public SQLDBConnection(String user, String password, String DBname)
			throws SQLException, ClassNotFoundException {
		this.user = user;
		this.password = password;
		this.db_url = "jdbc:mysql://localhost:3306/" + DBname;
		try {
			Class.forName(JDBC_DRIVER);
			if (!user.equals("none")){
				this.conn = DriverManager.getConnection(this.db_url, user,
						password);
			}
			else{
				this.conn = DriverManager.getConnection(this.db_url);
			}
			this.stmt = this.conn.createStatement();
			
		} catch (ClassNotFoundException | SQLException e) {
			System.err.println("Exception in constructor: "+ e.getMessage());
		
		} 

	}

	// Insert, update, or delete
	public int executeUpdate(String sql) throws ClassNotFoundException,
			SQLException {
		if (!hasOpenStatementAndConnection())
			reopenConnectionAndStatement();
		return this.stmt.executeUpdate(sql);

	}

	// Select
	public ResultSet executeQuery(String sql) {
		try {
			if (!hasOpenStatementAndConnection())
				reopenConnectionAndStatement();
			return this.stmt.executeQuery(sql);
		} catch (Exception exception) {
			exception.printStackTrace();
			return null;
		}

	}

	private boolean hasOpenStatementAndConnection() throws SQLException {
		return !this.conn.isClosed() && !this.stmt.isClosed();
	}

	private void reopenConnectionAndStatement() throws SQLException,
			ClassNotFoundException {
		if (this.conn == null || this.conn.isClosed())
			this.conn = DriverManager.getConnection(this.db_url, this.user,
					this.password);
		if (this.stmt == null || this.stmt.isClosed())
			this.stmt = this.conn.createStatement();
	}

}
