package eTrade;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;

import model.Stock;

import com.etrade.etws.account.Account;
import com.etrade.etws.order.EquityOrderAction;
import com.etrade.etws.order.EquityOrderRequest;
import com.etrade.etws.order.EquityOrderRoutingDestination;
import com.etrade.etws.order.EquityOrderTerm;
import com.etrade.etws.order.EquityPriceType;
import com.etrade.etws.order.MarketSession;
import com.etrade.etws.order.OrderDetails;
import com.etrade.etws.order.OrderListRequest;
import com.etrade.etws.order.PlaceEquityOrder;
import com.etrade.etws.order.PlaceEquityOrderResponse;
import com.etrade.etws.sdk.client.ClientRequest;
import com.etrade.etws.sdk.client.OrderClient;
import com.etrade.etws.sdk.common.ETWSException;


public class OrderManager {
	private static ClientRequest request= Login.getRequest();
	
	public void createOrder(){}
	
	private static List<OrderDetails> getOrderList(Account a){
		OrderClient client = new OrderClient(request);
		OrderListRequest olr = new OrderListRequest();
		olr.setAccountId(a.getAccountId()); 
		try {
			return client.getOrderList(olr).getOrderListResponse().getOrderDetails();
		} catch (IOException | ETWSException e) {
			eTradeLog.handleError(e);
		}
		return null;
	}
	
	/**
	 *  returns a list of orders that are currently open or have had activity during the current day
	 */
	public static void viewCurrentOrders(Account a ){
		List<OrderDetails> details=getOrderList(a);
		Iterator<OrderDetails> it =details.iterator();
		eTradeLog.log("=============Orders=========");
		while (it.hasNext()){
			OrderDetails detail= it.next();
			eTradeLog.log(detail.getOrder().getLegDetails().get(0).getSymbolDescription());
		}
		eTradeLog.log("There are now "+details.size()+" orders");
	}
	
	private static PlaceEquityOrderResponse placeOrder(Account a, EquityOrderRequest eor){
		OrderClient client = new OrderClient(request);
		PlaceEquityOrder orderRequest = new PlaceEquityOrder(); 
		eor.setAccountId(a.getAccountId()); // sample values
		eor.setAllOrNone("FALSE");
		eor.setOrderTerm(EquityOrderTerm.GOOD_FOR_DAY); 
		eor.setMarketSession(MarketSession.REGULAR); 
		eor.setPriceType(EquityPriceType.MARKET); 
		eor.setRoutingDestination(EquityOrderRoutingDestination.AUTO.value()); 
		eor.setReserveOrder("FALSE"); 
		String cid= createClientOrderId(eor);
		eor.setClientOrderId(cid); 
		orderRequest.setEquityOrderRequest(eor); 
		try {
			return client.placeEquityOrder(orderRequest);
		} catch (IOException | ETWSException e) {
			eTradeLog.handleError(e);
		}
		return null;
		
	}

	/**
	 * TODO Put here a description of what this method does.
	 *
	 * @param eor
	 * @return
	 */
	private static String createClientOrderId(EquityOrderRequest eor) {
		return String.format("%s%s%s%s",eor.getOrderAction().toString(),eor.getQuantity().toString(),eor.getSymbol());
	}
	

	/**
	 * BUY THAT STOCK THO
	 *
	 * @param stock
	 * @param acct
	 * @return 
	 */
	public static PlaceEquityOrderResponse buy(Stock stock, Account acct) {
		EquityOrderRequest eor = new EquityOrderRequest(); 
		eor.setSymbol(stock.getName());
		eor.setOrderAction(EquityOrderAction.BUY);
		eor.setQuantity(new BigInteger(String.valueOf(stock.getShares())));
		System.err.println("Figure out what setAllOrNone should be.");
		System.err.println("Figure out what setPriceType should be.");
		return placeOrder(acct, eor);
		
	}

	/**
	 * TODO Put here a description of what this method does.
	 *
	 * @param stock
	 * @param acct
	 * @return 
	 */
	public static PlaceEquityOrderResponse sell(Stock stock, Account acct) {
		EquityOrderRequest eor = new EquityOrderRequest(); 
		eor.setSymbol(stock.getName());
		eor.setOrderAction(EquityOrderAction.SELL);
		eor.setQuantity(new BigInteger(String.valueOf(stock.getShares())));
		System.err.println("Figure out what setAllOrNone should be.");
		System.err.println("Figure out what setPriceType should be.");
		return placeOrder(acct, eor);
		
	}
	
	
	
}
