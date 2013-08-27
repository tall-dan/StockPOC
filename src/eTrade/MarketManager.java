package eTrade;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import model.Stock;

import com.etrade.etws.market.DetailFlag;
import com.etrade.etws.market.IntradayQuote;
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
	
	private static Iterator<QuoteData> getQuotes(ArrayList<String> tickerNames){
		if (request==null)
			request=Login.getRequest();
		MarketClient client = new MarketClient(request);
		QuoteResponse response=null;
		try {
			response = client.getQuote(tickerNames, false, 
					DetailFlag.INTRADAY);
		} catch (IOException | ETWSException e) {
			eTradeLog.handleError(e);
		}
		List<QuoteData> qd= response.getQuoteData();
		Iterator<QuoteData> it = qd.iterator();
		return it;
	}

	public static ArrayList<Stock> getStockData(ArrayList<String> tickerNames) {
		Iterator<QuoteData> it = getQuotes(tickerNames);
		ArrayList<Stock> toRet= new ArrayList<Stock>();
		while (it.hasNext()){
			QuoteData quote= it.next();
			double marketPrice=quote.getIntraday().getLastTrade();
			Stock s= new Stock(quote.getProduct().getSymbol(),marketPrice,0,null);
			toRet.add(s);
		}
		return toRet;
	}
	
	
}
