package eTrade;

import java.awt.Desktop;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.Properties;
import java.util.Scanner;
import java.util.List;

import org.apache.log4j.Logger;

import com.etrade.etws.account.Account;
import com.etrade.etws.account.AccountBalanceResponse;
import com.etrade.etws.account.AccountListResponse;
import com.etrade.etws.oauth.sdk.client.IOAuthClient;
import com.etrade.etws.oauth.sdk.client.OAuthClientImpl;
import com.etrade.etws.oauth.sdk.common.Token;
import com.etrade.etws.sdk.client.AccountsClient;
import com.etrade.etws.sdk.client.ClientRequest;
import com.etrade.etws.sdk.client.Environment;
import com.etrade.etws.sdk.common.ETWSException;

public class Login {
	// Variables
	public static IOAuthClient client = null;
	public static ClientRequest request = null;
	public static Token token = null;
	public static String oauth_consumer_key = null; // Your consumer key
	public static String oauth_consumer_secret = null; // Your consumer secret
	public static String oauth_request_token = null; // Request token
	public static String oauth_request_token_secret = null; // Request token secret
	public static String oauth_access_token = null; // Variable to store access token
	public static String oauth_access_token_secret = null; // Variable to store access
	public static String oauth_verify_code = null;

	public static void login() {
		getProps();
		client = OAuthClientImpl.getInstance(); // Instantiate IOAUthClient
		request = new ClientRequest(); // Instantiate ClientRequest
		request.setEnv(Environment.SANDBOX); // Use sandbox environment
		request.setConsumerKey(oauth_consumer_key); // Set consumer key
		request.setConsumerSecret(oauth_consumer_secret); // Set consumer secret
		try {
			token = client.getRequestToken(request);
			oauth_request_token = token.getToken(); // Get token string
			oauth_request_token_secret = token.getSecret(); // Get token secret
		} catch (IOException | ETWSException e) {
			eTradeLog.handleError(e);
		}
		request.setToken(token.getToken());
		request.setTokenSecret(token.getSecret());
		oauth_verify_code = getVerficationCode();
		getAccessToken();
	}

	private static String getVerficationCode() {
		String authorizeURL = null;
		try {
			authorizeURL = client.getAuthorizeUrl(request); // E*TRADE
															// authorization URL
			URI uri = new java.net.URI(authorizeURL);
			Desktop desktop = Desktop.getDesktop();
			desktop.browse(uri);
		} catch (IOException | ETWSException | URISyntaxException e) {
			eTradeLog.handleError(e);
		}
		Scanner scanner = new Scanner(System.in);
		System.out.println("please input the verification code");
		String authCode = scanner.next();
		scanner.close();
		return authCode;
	}

	// Should contain the Verification Code received from the authorization step
	private static void getAccessToken() {
		request = new ClientRequest(); // Instantiate ClientRequest
		request.setEnv(Environment.SANDBOX); // Use sandbox environment
		// Prepare request
		request.setConsumerKey(oauth_consumer_key); 
		request.setConsumerSecret(oauth_consumer_secret); 
		request.setToken(oauth_request_token); // Set request token
		request.setTokenSecret(oauth_request_token_secret); 
		request.setVerifierCode(oauth_verify_code); // Set verification code

		// Get access token
		try {
			token = client.getAccessToken(request);
		} catch (IOException | ETWSException e) {
			eTradeLog.handleError(e);
		}
		oauth_access_token = token.getToken(); // Access token string
		oauth_access_token_secret = token.getSecret(); // Access token secret
	}

	private static void getProps() {
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream("local.properties"));
		} catch (IOException e) {
			eTradeLog.handleError(e);
		}
		oauth_consumer_secret=prop.getProperty("consumer_secret");
		oauth_consumer_key=prop.getProperty("consumer_key");
	}


	
	public static ClientRequest getRequest(){
		if (request==null){
			System.out.println("Logging in.  You will have to enter the verification code");
			login();
			request = new ClientRequest(); // Instantiate ClientRequest
			// Prepare request
			request.setEnv(Environment.SANDBOX);
			request.setConsumerKey(oauth_consumer_key);
			request.setConsumerSecret(oauth_consumer_secret);
			request.setToken(oauth_access_token);
			request.setTokenSecret(oauth_access_token_secret);
			return request;
		}
		return request;
		
	}

}
