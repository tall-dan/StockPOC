import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


public class Algorithm1 implements iTradeAlgorithm {
	SQLDBConnection conn;
	ArrayList<Stock> stocks;
	public Algorithm1(){
		try {
			this.conn=new SQLDBConnection();
			getStocks();
		} catch (ClassNotFoundException | SQLException e) {
			System.err.println("Error in Algorithm1 constuctor");
			e.printStackTrace();
			System.exit(-1);
		}
	}

	private void getStocks() throws SQLException {
		ResultSet rows=conn.executeQuery("Select * from stocks");
		
		while (rows.next()) {
			String name=rows.getString("Ticker_name");
			double buyPrice=Double.valueOf(rows.getString("Buy_Price"));
			int shares=Integer.valueOf(rows.getString("Shares"));
			double maxLoss=Integer.valueOf(rows.getString("Max_loss"));
			this.stocks.add(new Stock(name, buyPrice,shares, maxLoss));
		}
		rows.close();
		
	}

	@Override
	public Boolean buy() {
		return null;
	}

	@Override
	public Boolean sell() {
		for (Stock stock: this.stocks){
			
		}
		return false;
	}
	
	public double getCurrentPrices(){//will need to be extended
		return 0;
	}

}
