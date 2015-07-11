package in.pathri.gaanaextractor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HTTPHelper {
	private final static String USER_AGENT = "Mozilla/5.0";
	static final Logger logger = LogManager.getLogger(MainExtractor.class.getName());

	// HTTP GET request
	public static String sendGet(String endPoint, Map<String, String> params) throws Exception {
		logger.entry();
		String query = params.entrySet().stream().map(param -> {
			try {
				return URLEncoder.encode(param.getKey(), "UTF-8") + "=" + URLEncoder.encode(param.getValue(), "UTF-8");
			} catch (Exception e) {
				e.printStackTrace();
				return "";
			}
		}).collect(Collectors.joining("&"));

		endPoint = endPoint + (endPoint.endsWith("/") ? "?" : "/?");
		endPoint = endPoint + query;
		logger.info(endPoint);
		URL obj = new URL(endPoint);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		// add request header
		con.setRequestProperty("User-Agent", USER_AGENT);
		logger.info(con.getRequestProperties());
		// int responseCode = con.getResponseCode();
		// System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		
		// print result
		// System.out.println(response.toString());
		logger.exit();
		return response.toString();
	}
}
