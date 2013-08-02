import java.util.ArrayList;


public interface iTradeAlgorithm {

	abstract ArrayList<Stock> stocksToBuy();
	ArrayList<Stock> stocksToSell();
	void logTransaction(Stock stock, double currentPrice, String buyOrSell);
	
}
