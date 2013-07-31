import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;


public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Trader trader=new Trader();//probably going to need some authentication passed here
		iTradeAlgorithm algorithm;//probably going to be a lot of ways to go about this 
		String[] tickerNames={"stxs"};
		String separatedVals=concatonateVals(tickerNames);
		URL url;
		BufferedReader reader = null;
		int shares=760;
		double tradeCost=9.99;
		int maxLoss=-10;
		double buyPrice=6.11;
		while (true){
			try {
				url = new URL("http://download.finance.yahoo.com/d/quotes.csv?s="+separatedVals+"&f=l1&e=.csv");
				reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
				double currentPrice=prettyPrint(separatedVals, reader);
				
				double priceDiff=currentPrice-buyPrice;
				if (priceDiff*shares>tradeCost||priceDiff*shares<maxLoss){
					System.out.println("SELL");
					System.exit(0);
				}
				Thread.sleep(1000);
			} catch (IOException | InterruptedException e) {
				System.err.println(e.getMessage());
				System.err.println(e.getCause());
				e.printStackTrace();
				System.exit(-1);
			}
			finally {
			    if (reader != null) try { reader.close(); } catch (IOException ignore) {}
			}
		}

	}

	private static double prettyPrint(String separatedVals, BufferedReader reader)
			throws IOException {
		System.out.println(separatedVals.replace("+", "     "));
		double currentPrice = -1;
		for (String line; (line = reader.readLine()) != null;) {//currently set up for only one stock
			currentPrice=Double.valueOf(line);
			System.out.printf("%s    ",line);
		}
		System.out.println();
		return currentPrice;
	}

	private static String concatonateVals(String[] tickerNames) {
		String returnVal=tickerNames[0];
		for (int i =1; i < tickerNames.length; i++){
			returnVal+="+"+tickerNames[i];
		}
		System.out.println(returnVal);		
		return returnVal;
	}

}
