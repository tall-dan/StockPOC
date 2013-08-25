import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.etrade.etws.market.DetailFlag;
import com.etrade.etws.market.QuoteData;
import com.etrade.etws.market.QuoteResponse;
import com.etrade.etws.sdk.client.ClientRequest;
import com.etrade.etws.sdk.client.MarketClient;
import com.etrade.etws.sdk.common.ETWSException;


public class MarketManager {
	private static ClientRequest request;
	public MarketManager(){
		request= Login.getRequest();
	}
	
	public static QuoteResponse getQuotes(ArrayList<String> tickerNames){
		if (request==null)
			request=Login.getRequest();
		MarketClient client = new MarketClient(request);
		QuoteResponse response=null;
		try {

			response = client.getQuote(tickerNames, false, 
					DetailFlag.ALL);
		} catch (IOException | ETWSException e) {
			Main.handleError(e);
		}
		return response;
	}
	
	public static void getQuotes(){
		ArrayList<String> tickerNames= new ArrayList<String>();
		tickerNames.add("aapl");
		tickerNames.add("abt");
		QuoteResponse response =MarketManager.getQuotes(tickerNames);
		List<QuoteData> qd= response.getQuoteData();
		Iterator<QuoteData> it = qd.iterator();
		while (it.hasNext()){
			QuoteData quote= it.next();
			log("Current asking price: "+ quote.getAll().getAsk());
		}
	}
	
	private static void log(String s){
		Main.writer.println("s");
	}
	
	
}
