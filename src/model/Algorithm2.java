package model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import eTrade.eTradeLog;

public class Algorithm2 implements iTradeAlgorithm {

	@Override
	/**
	 * StocksToConsider contains stocks with current price & symbol
	 */
	public ArrayList<Stock> stocksToBuy(ArrayList<Stock> stocksToConsider) {
		try {
		ArrayList<Stock> stocksToBuy = new ArrayList<Stock>();
			for (Stock stock : stocksToConsider) {
				double vals[] = getStats(stock.getName());
				double avgPrice = vals[0];
				double stdDev = vals[1];
				double currentPrice= stock.getBuyPrice();
				double normalizedStandardDev= stdDev/avgPrice; //gives a measure of how volatile this stock is
				if (normalizedStandardDev>.02&&avgPrice-currentPrice>stdDev){
					stocksToBuy.add(stock);
				}
			}
			return stocksToBuy;
		} catch (SQLException e) {
			eTradeLog.handleError(e);
		}
		return null;
	}

	/**
	 * Get average & stddev for the given stock
	 * 
	 * @param name
	 * @return {avgPrice, stddev}
	 * @throws SQLException
	 */
	private double[] getStats(String name) throws SQLException {
		double vals[]={0,0}; 
		String sql = "Select avgPrice, stddev from " + name + "_Stats where tickerName='"
				+ name + "'l";
		ResultSet StdDev = new SQLDBConnection().executeQuery(sql);
		if (StdDev.next()){
			vals[0]=StdDev.getDouble(1);
			vals[1]=StdDev.getDouble(2);
			return vals;
		}
		StdDev.close();
		throw new SQLException("Could not get StdDev from view");
	}

	@Override
	public ArrayList<Stock> stocksToSell() {
		ArrayList<Stock> stocksToSell = new ArrayList<Stock>();
		ArrayList<Stock> owned= new SQLDBConnection().getOwnedStocks();
		for (Stock stock: owned){
			double currentPrice = stock.getCurrentPrice();
			double tradeFee=9.99;
			double minimumProfitPerSale= 5;
			if ((currentPrice - stock.getBuyPrice()) * stock.getShares() > 2*tradeFee+minimumProfitPerSale) {
				stocksToSell.add(stock);
			}
			else if ((currentPrice-stock.getBuyPrice())*stock.getShares()*-1 > stock.getMaxLoss()){
				stocksToSell.add(stock);
			}
			
		}
		return null;
	}

	@Override
	public void updateOwnedStocks() {
		//Shouldn't be in the interface.

	}

}
