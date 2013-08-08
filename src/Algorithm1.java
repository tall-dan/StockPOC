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
		this.stocks=this.conn.getOwnedStocks();
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
			double currentPrice = stock.getCurrentPrice();
			if ((currentPrice - stock.getBuyPrice()) * stock.getShares() > tradeFee+minimumProfitPerSale) {
				stocksToSell.add(stock);
				
				
			}
		}

		return stocksToSell;
	}






}
