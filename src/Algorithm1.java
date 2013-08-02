import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class Algorithm1 implements iTradeAlgorithm {
	SQLDBConnection conn;
	private double tradeFee=9.99;
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
		this.stocks=new ArrayList<Stock>();
		ResultSet rows=conn.executeQuery("Select * from stocks");
		while (rows.next()) {
			String name=rows.getString("Ticker_name");
			double buyPrice=Double.valueOf(rows.getString("Buy_Price"));
			int shares=Integer.valueOf(rows.getString("Shares"));
			double maxLoss=Double.valueOf(rows.getString("Max_loss"));
			this.stocks.add(new Stock(name, buyPrice,shares, maxLoss));
		}
		rows.close();
		
	}

	@Override
	public ArrayList<Stock> stocksToBuy() {
		/*
		logTransaction();
		addStockToTable(stock);
		*/
		return null;
	}

	@Override
	public ArrayList<Stock> stocksToSell() {
		ArrayList<Stock> stocksToSell=new ArrayList<Stock>();
		for (Stock stock: this.stocks){
			double currentPrice=getCurrentPrice(stock);
			if ((currentPrice-stock.getBuyPrice())*stock.getShares()>tradeFee){
				stocksToSell.add(stock);
				logTransaction(stock,currentPrice, "sell");
				removeStockFromTable(stock);
			}
		}
		
		return stocksToSell;
	}
	
	private void removeStockFromTable(Stock stock) {
		String sql="Delete from stocks where Ticker_Name='"+stock.getName()+"';";
		try {
			this.conn.executeUpdate(sql);
		} catch (ClassNotFoundException | SQLException e) {
			System.err.println("SQL statment="+sql);
			e.printStackTrace();
		}
	}

	private double getCurrentPrice(Stock stock) {
		
		String sql="Select Price from "+stock.getName()+"_prices order by Time desc limit 1";
		ResultSet price=conn.executeQuery(sql);
		double currentPrice=-1;
		try {
			price.next();
			currentPrice=Double.valueOf(price.getString(1));
		} catch (SQLException e) {
			System.err.println("no price entry for" +stock.getName());
			e.printStackTrace();
			System.exit(-1);
		}
		return currentPrice;
	}


	@Override
	public void logTransaction(Stock stock, double currentPrice, String buyOrSell) {
		DateFormat dateFormat = new SimpleDateFormat(
				"yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		String sql="Insert into Transaction_History (Ticker_name, Price, Shares, BUY_SELL, Profit, Time) Values ('"+stock.getName()+"', "+currentPrice+ ", "+stock.getShares()+
				", '"+buyOrSell+"', "+((currentPrice-stock.getBuyPrice())*stock.getShares())+", '"+dateFormat.format(date)+ "');";
		try {
			this.conn.executeUpdate(sql);
		} catch (ClassNotFoundException | SQLException e) {
			System.err.println("SQL = "+sql);
			e.printStackTrace();
		}
	}

	
	

}
