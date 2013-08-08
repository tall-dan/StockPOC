import java.util.ArrayList;


public interface iTradeAlgorithm {

	abstract ArrayList<Stock> stocksToBuy();
	ArrayList<Stock> stocksToSell();
	
}
