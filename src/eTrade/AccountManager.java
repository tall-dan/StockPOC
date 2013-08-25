import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import com.etrade.etws.account.Account;
import com.etrade.etws.account.AccountBalanceResponse;
import com.etrade.etws.account.AccountListResponse;
import com.etrade.etws.account.AccountPositionsRequest;
import com.etrade.etws.account.AccountPositionsResponse;
import com.etrade.etws.sdk.client.AccountsClient;
import com.etrade.etws.sdk.client.ClientRequest;
import com.etrade.etws.sdk.common.ETWSException;


public class AccountManager {

	private final static Logger LOGGER = Logger.getLogger(AccountManager.class .getName());
	public static List<Account> getAccounts() {
		AccountsClient account_client=getAccountsClient();
		try {
			AccountListResponse response = account_client.getAccountList();
			List<Account> alist = response.getResponse();
			return alist;
		} catch (Exception e) {
			Main.handleError(e);
		}
		return null;//can't get here - either we return from the try, or die in the catch
	}
	
	public static AccountsClient getAccountsClient(){
		ClientRequest r = Login.getRequest();
		return new AccountsClient(r);
	}
	public static AccountBalanceResponse getAccountBalance(Account a){
		return getAccountBalance(a.getAccountId());
	} 
	
	public static AccountBalanceResponse getAccountBalance(String accountID){
		try {
			return getAccountsClient().getAccountBalance(accountID);
		} catch (IOException | ETWSException e) {
			Main.handleError(e);
		}
		return null;//can't get here  
	}
	
	public static AccountPositionsResponse getAcctPosition(String acct){
		AccountsClient client=getAccountsClient();
		AccountPositionsResponse aprs = null;
		AccountPositionsRequest apr = new AccountPositionsRequest();
		/*apr.setCount("10"); // number of positions to return
		apr.setMarker("Your marker value"); // insert marker
		*/
		apr.setSymbol("Your symbol value"); // insert desired symbol
		
		apr.setTypeCode("EQ"); // set type code to EQ, OPTN, MF, or BOND
		try {
			aprs = client.getAccountPositions(acct, apr);
		} catch (IOException | ETWSException e) {
			Main.handleError(e);
		}
		return aprs;
	}
	public static AccountPositionsResponse getAcctPosition(Account a){
		return getAcctPosition(a.getAccountId());
	}
	
	
}
