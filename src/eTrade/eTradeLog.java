package eTrade;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;

public class eTradeLog {
	private static PrintWriter writer;

	public static void handleError(Exception e) {
		System.err.println(e.getMessage());
		e.printStackTrace();
		if (writer != null) {
			writer.flush();
			writer.close();
		}
		System.exit(-1);
	}

	private static void setupLog() {
		try {
			writer = new PrintWriter("../logs/output.log", "UTF-8");
			writer.println(new Date().toString());
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			handleError(e);
		}
	}

	public static void log(String l) {
		if (writer == null)
			setupLog();
		writer.println(l);
		writer.flush();
	}

}
