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
	private final static String APP_VERSION = "V7";
	private final static String DEVICE_TYPE = "GaanaAndroidApp";	
	
	static final Logger logger = LogManager.getLogger();

	// HTTP GET request
	public static String sendGet(String endPoint, Map<String, String> params) throws Exception {
		logger.entry(endPoint,params);
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
		logger.debug("EndPoint:: {}",endPoint);
		URL obj = new URL(endPoint);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		// add request header
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("appVersion", APP_VERSION);
		con.setRequestProperty("deviceType", DEVICE_TYPE);
		logger.debug("Connection Req Properties:: {}",con.getRequestProperties());
		// int responseCode = con.getResponseCode();
		// System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		
		logger.traceExit(response);
		return response.toString();
	}
}
