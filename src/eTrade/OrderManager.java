import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;

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
	
	public void createOrder(){
		
	}
	
	private static List<OrderDetails> getOrderList(Account a){
		OrderClient client = new OrderClient(request);
		OrderListRequest olr = new OrderListRequest();
		olr.setAccountId(a.getAccountId()); 
		try {
			return client.getOrderList(olr).getOrderListResponse().getOrderDetails();
		} catch (IOException | ETWSException e) {
			Main.handleError(e);
		}
		return null;
	}
	
	/**
	 *  returns a list of orders that are currently open or have had activity during the current day
	 */
	public static void viewCurrentOrders(Account a ){
		List<OrderDetails> details=getOrderList(a);
		Iterator<OrderDetails> it =details.iterator();
		log("=============Orders=========");
		while (it.hasNext()){
			OrderDetails detail= it.next();
			log(detail.getOrder().getLegDetails().get(0).getSymbolDescription());
		}
		log("There are now "+details.size()+" orders");
	}
	
	public static  PlaceEquityOrderResponse placeOrder(Account a){
		OrderClient client = new OrderClient(request);
		PlaceEquityOrder orderRequest = new PlaceEquityOrder(); 
		EquityOrderRequest eor = new EquityOrderRequest(); 
		eor.setAccountId(a.getAccountId()); // sample values
		eor.setSymbol("AAPL");
		eor.setAllOrNone("FALSE"); 
		eor.setClientOrderId("asdf1234"); 
		eor.setOrderTerm(EquityOrderTerm.GOOD_FOR_DAY); 
		eor.setOrderAction(EquityOrderAction.BUY); 
		eor.setMarketSession(MarketSession.REGULAR); 
		eor.setPriceType(EquityPriceType.MARKET); 
		eor.setQuantity(new BigInteger("40")); 
		eor.setRoutingDestination(EquityOrderRoutingDestination.AUTO.value()); 
		eor.setReserveOrder("FALSE"); 
		orderRequest.setEquityOrderRequest(eor); 
		try {
			return client.placeEquityOrder(orderRequest);
		} catch (IOException | ETWSException e) {
			Main.handleError(e);
		}
		return null;
		
	}
	
	private static void log(String message){
		Main.writer.println(message);
	}
	
}
