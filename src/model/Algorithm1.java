package model;

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
	private int minimumInvestment=2500;//Play around with this
	private double maxWaste=100;
	ArrayList<Stock> stocks;
	private double minimumProfitPerSale;
	public Algorithm1() {
		this.conn = new SQLDBConnection();
		updateOwnedStocks();
	}
	
	@Override
	public void updateOwnedStocks(){
		this.stocks=this.conn.getOwnedStocks();
	}
	@Override
	public ArrayList<Stock> stocksToBuy(ArrayList<Stock> stocksToConsider)  {
		ArrayList<Stock> stocksToBuy=new ArrayList<Stock>();
		double currentCash=this.conn.getCurrentCash();
		if (currentCash>minimumInvestment){
			for (Stock stock: stocksToConsider){
				double currentPrice=stock.getBuyPrice();//buy price is currently market price
				int shares=(int) Math.floor((currentCash-tradeFee)/currentPrice);
				if (currentCash-shares*currentPrice<maxWaste&&currentPrice<stock.getMostRecentSalePrice()){
					stock.setShares(shares);
					stock.setBuyPrice(currentPrice);
					stock.setBuyTime(new Date());
					stocksToBuy.add(stock);
				}
			}
		}
		/*
		 * logTransaction(); addStockToTable(stock);
		 */
		return stocksToBuy;
	}

	@Override
	public ArrayList<Stock> stocksToSell() {
		ArrayList<Stock> stocksToSell = new ArrayList<Stock>();
		for (Stock stock : this.stocks) {
			double currentPrice = stock.getCurrentPrice();
			if ((currentPrice - stock.getBuyPrice()) * stock.getShares() > 2*tradeFee+minimumProfitPerSale) {
				stocksToSell.add(stock);
			}
			else if ((currentPrice-stock.getBuyPrice())*stock.getShares()*-1 > stock.getMaxLoss()){
				stocksToSell.add(stock);
			}
		}
		return stocksToSell;
	}






}
