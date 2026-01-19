package org.clickenrent.paymentservice.client.multisafepay;

import java.lang.reflect.Type;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.clickenrent.paymentservice.client.multisafepay.model.*;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

public class MultiSafepayClient {

	// Configuration - set via init() method from application.properties
	private static String api_key;
	private static String USER_AGENT = "ClickEnRent-Payment-Service/1.0";

	private static boolean testMode = true;

	private static String testApiUrl = "https://testapi.multisafepay.com/v1/json/";
	private static String apiUrl = "https://api.multisafepay.com/v1/json/";

	private static String endPoint = "";

	/**
	 * Initializes MultiSafePay client with configuration from application.properties
	 * 
	 * @param testMode true for test environment, false for production
	 * @param apiKey API key from MultiSafePay
	 */
	public static void init(Boolean testMode, String apiKey) {
		if (apiKey == null || apiKey.isEmpty()) {
			throw new IllegalArgumentException("MultiSafePay API key is required");
		}
		
		MultiSafepayClient.testMode = testMode;
		MultiSafepayClient.api_key = apiKey;
		MultiSafepayClient.endPoint = testMode ? testApiUrl : apiUrl;
	}

	public static JsonObject GetGateways() {
		return MultiSafepayClient.sendRequest("gateways");
	}

	public static JsonObject GetGateway(String name) {
		return MultiSafepayClient.sendRequest("gateways/" + name);
	}

	public static JsonObject GetIdealIssuers() {
		return MultiSafepayClient.sendRequest("issuers/ideal");
	}

	public static JsonObject GetIssuer(String name) {
		return MultiSafepayClient.sendRequest("issuers/" + name);
	}

	public static JsonObject GetOrder(String order_id) {
		return MultiSafepayClient.sendRequest("orders/" + order_id);
	}

	public static JsonObject GetTransaction(String transaction_id) {
		return MultiSafepayClient.sendRequest("transactions/" + transaction_id);
	}

	public static JsonObject GetOrderTransactions(String order_id) {
		return MultiSafepayClient.sendRequest("orders/" + order_id
				+ "/transactions");
	}

	public static JsonObject SetOrderRefund(String order_id, Integer amount,
			String currency, String description) {
		Order order = new Order();
		order.currency = currency;
		order.amount = amount;
		order.description = description;

		return MultiSafepayClient.sendRequest(
				"orders/" + order_id + "/refunds", "POST", order);
	}

	public static JsonObject SetOrderInvoice(String order_id, String invoice_id) {
		Order order = new Order();
		order.invoice_id = invoice_id;

		return MultiSafepayClient.sendRequest("orders/" + order_id, "PATCH",
				order);
	}

	public static JsonObject SetOrderShipping(String order_id,
			String ship_date, String carrier, String tracktrace_code) {
		Order order = new Order();
		order.ship_date = ship_date;
		order.carrier = carrier;
		order.tracktrace_code = tracktrace_code;

		return MultiSafepayClient.sendRequest("orders/" + order_id, "PATCH",
				order);
	}

	public static JsonObject createOrder(Order order) {
		return MultiSafepayClient.sendRequest("orders", "POST", order);
	}

	// === Orders Management ===
	
	public static JsonObject updateOrder(String order_id, Order order) {
		return MultiSafepayClient.sendRequest("orders/" + order_id, "PATCH", order);
	}
	
	public static JsonObject captureOrder(String order_id) {
		return MultiSafepayClient.sendRequest("orders/" + order_id + "/capture", "POST", null);
	}
	
	public static JsonObject cancelAuthorization(String order_id) {
		Order order = new Order();
		order.status = "void";
		return MultiSafepayClient.sendRequest("orders/" + order_id, "PATCH", order);
	}
	
	public static JsonObject extendExpiration(String order_id, int days) {
		Order order = new Order();
		order.days_active = String.valueOf(days);
		return MultiSafepayClient.sendRequest("orders/" + order_id, "PATCH", order);
	}
	
	public static JsonObject cancelBancontactQR(String order_id) {
		return MultiSafepayClient.sendRequest("orders/" + order_id + "/cancel-bancontact-qr", "POST", null);
	}
	
	public static JsonObject putPADOrderOnHold(String order_id) {
		return MultiSafepayClient.sendRequest("orders/" + order_id + "/hold", "POST", null);
	}

	// === Refunds ===
	
	public static JsonObject createRefund(String order_id, Refund refund) {
		return MultiSafepayClient.sendRequest("orders/" + order_id + "/refunds", "POST", refund);
	}
	
	public static JsonObject cancelRefund(String order_id, String refund_id) {
		return MultiSafepayClient.sendRequest("orders/" + order_id + "/refunds/" + refund_id, "PATCH", null);
	}

	// === Chargebacks ===
	
	public static JsonObject challengeChargeback(String order_id, String reason) {
		Chargeback chargeback = new Chargeback();
		chargeback.reason = reason;
		return MultiSafepayClient.sendRequest("orders/" + order_id + "/chargebacks/challenge", "POST", chargeback);
	}

	// === Tokens ===
	
	public static JsonObject listTokens(int page, int pageSize) {
		return MultiSafepayClient.sendRequest("recurring/" + api_key + "/tokens?page=" + page + "&per_page=" + pageSize);
	}
	
	public static JsonObject getToken(String token_id) {
		return MultiSafepayClient.sendRequest("recurring/" + api_key + "/tokens/" + token_id);
	}
	
	public static JsonObject updateToken(String token_id, Token token) {
		return MultiSafepayClient.sendRequest("recurring/" + api_key + "/tokens/" + token_id, "PATCH", token);
	}
	
	public static JsonObject deleteToken(String token_id) {
		return MultiSafepayClient.sendRequest("recurring/" + api_key + "/tokens/" + token_id, "DELETE", null);
	}

	// === Transactions ===
	
	public static JsonObject listTransactions(int page, int pageSize) {
		return MultiSafepayClient.sendRequest("transactions?page=" + page + "&per_page=" + pageSize);
	}

	// === Payment Methods ===
	
	public static JsonObject listPaymentMethods() {
		return MultiSafepayClient.sendRequest("payment-methods");
	}
	
	public static JsonObject getPaymentMethod(String method_code) {
		return MultiSafepayClient.sendRequest("payment-methods/" + method_code);
	}

	// === Account Management ===
	
	public static JsonObject getSiteConfig() {
		return MultiSafepayClient.sendRequest("sites/" + api_key);
	}
	
	public static JsonObject updateSiteConfig(SiteConfig siteConfig) {
		return MultiSafepayClient.sendRequest("sites/" + api_key, "PATCH", siteConfig);
	}
	
	public static JsonObject listClosingBalances(String from_date, String to_date) {
		return MultiSafepayClient.sendRequest("balances/closing?from=" + from_date + "&to=" + to_date);
	}

	// === POS Terminals ===
	
	public static JsonObject listTerminals() {
		return MultiSafepayClient.sendRequest("terminals");
	}
	
	public static JsonObject listTerminalsByGroup(String group_id) {
		return MultiSafepayClient.sendRequest("terminals?group=" + group_id);
	}
	
	public static JsonObject getReceipt(String terminal_id, String transaction_id) {
		return MultiSafepayClient.sendRequest("terminals/" + terminal_id + "/receipt/" + transaction_id);
	}
	
	public static JsonObject cancelTransaction(String terminal_id, String transaction_id) {
		return MultiSafepayClient.sendRequest("terminals/" + terminal_id + "/transactions/" + transaction_id, "POST", null);
	}
	
	public static JsonObject createTerminal(Terminal terminal) {
		return MultiSafepayClient.sendRequest("terminals", "POST", terminal);
	}

	// === Webhook Signature Verification ===
	
	public static boolean verifySignature(String payload, String signature, String apiKey) {
		try {
			javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA512");
			javax.crypto.spec.SecretKeySpec secret_key = new javax.crypto.spec.SecretKeySpec(
				apiKey.getBytes("UTF-8"), "HmacSHA512");
			mac.init(secret_key);
			byte[] hash = mac.doFinal(payload.getBytes("UTF-8"));
			
			StringBuilder hexString = new StringBuilder();
			for (byte b : hash) {
				String hex = Integer.toHexString(0xff & b);
				if (hex.length() == 1) hexString.append('0');
				hexString.append(hex);
			}
			
			return hexString.toString().equalsIgnoreCase(signature);
		} catch (Exception e) {
			System.out.println("Error verifying signature: " + e.toString());
			return false;
		}
	}

	// Private Methods

	/**
	 * Send Http request to Multisafepay
	 * 
	 * @param url
	 * @param method
	 * @param jsonString
	 * @return
	 */
	public static JsonObject sendRequest(String url, String method,
			Object mspObject) {

		JsonObject jsonResponse = null;
		String _overrideMethod = null;
		String jsonString = null;
		if (mspObject != null) {
			jsonString = JsonHandler(mspObject);
		}
		if (method == null || method.isEmpty()) {
			method = "GET";
		}

		if ("PATCH".equals(method)) { // Workaround HttpURLConnection does not support all modern methods like PATCH
			_overrideMethod = "PATCH";
			method = "POST";
		}

		method = method.toUpperCase();
		try {

			System.out.println("Send Api Request: "
					+ MultiSafepayClient.endPoint + url);

			URL obj = new URL(MultiSafepayClient.endPoint + url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setDoOutput(true);
			con.setInstanceFollowRedirects(false);
			con.setRequestMethod(method);
			con.setRequestProperty("User-Agent", USER_AGENT);

			if (_overrideMethod != null) {
				con.setRequestProperty("X-HTTP-Method-Override",
						_overrideMethod);
			}

			con.setRequestProperty("api_key", MultiSafepayClient.api_key);
			con.setRequestProperty("charset", "utf-8");
			con.setUseCaches(false);

	if ("POST".equals(method) || "PUT".equals(method) || "PATCH".equals(method)) {
		con.setDoInput(true);
		con.setRequestProperty("Content-Type", "application/json");
		
		// Handle null jsonString by sending empty JSON object
		String requestBody = (jsonString != null) ? jsonString : "{}";
		
		con.setRequestProperty("Content-Length",
				"" + Integer.toString(requestBody.getBytes().length));
		DataOutputStream wr = new DataOutputStream(
				con.getOutputStream());
		wr.writeBytes(requestBody);
		wr.flush();
		wr.close();
		System.out.println(method + " Data:");
		System.out.println(requestBody);
	}

			int status = con.getResponseCode();
			System.out.println("Http response code:");
			System.out.println(status);

			String inputLine;
			BufferedReader reader = null;
			if (status == 200) {
				reader = new BufferedReader(new InputStreamReader(
						con.getInputStream()));
			} else {
				reader = new BufferedReader(new InputStreamReader(
						con.getErrorStream()));
			}

			StringBuffer response = new StringBuffer();

			while ((inputLine = reader.readLine()) != null) {
				response.append(inputLine);
			}

			reader.close();

			// Use JsonParser.parseString() instead of deprecated parse()
			jsonResponse = JsonParser.parseString(response.toString()).getAsJsonObject();

			con.disconnect();

		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return jsonResponse;
	}

	public static JsonObject sendRequest(String url, String method) {

		return MultiSafepayClient.sendRequest(url, method, null);
	}

	public static JsonObject sendRequest(String url) {

		return MultiSafepayClient.sendRequest(url, "GET", null);
	}

	/**
	 * Helper
	 * 
	 * @param jsonString
	 * @return
	 */
	private static String JsonHandler(Object _object) {
		Gson gson = new Gson();
		String jsonString = gson.toJson(_object);

		Type type = new TypeToken<Map<String, Object>>() {
		}.getType();
		Map<String, Object> data = new Gson().fromJson(jsonString, type);

		for (Iterator<Map.Entry<String, Object>> it = data.entrySet()
				.iterator(); it.hasNext();) {
			Map.Entry<String, Object> entry = it.next();
			if (entry.getValue() == null) {
				it.remove();
			} else if (entry.getValue().getClass().equals(ArrayList.class)) {
				if (((ArrayList<?>) entry.getValue()).size() == 0) {
					it.remove();
				}
			}
		}
		String json = new GsonBuilder().create().toJson(data);
		return json;
	}

	/**
	 * Parse payment_url from response for transactions with redirection or
	 * payment_url
	 * 
	 * @param response
	 * @return
	 */
	public static String getPaymenUrl(JsonObject response) {
		String payment_url = null;
		try {
			JsonObject data = response.getAsJsonObject("data");
			payment_url = data.get("payment_url").getAsString();
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return payment_url;
	}

	/**
	 * Parse qr_url from response for transactions with redirection or
	 * payment_url
	 * 
	 * @param response
	 * @return
	 */
	public static String getQrUrl(JsonObject response) {
		String qr_url = null;
		try {
			JsonObject data = response.getAsJsonObject("data");
			qr_url = data.get("qr_url").getAsString();
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return qr_url;
	}

	// ===== PAYOUT API METHODS =====

	/**
	 * Create a payout to a bank account
	 * Sends money from merchant MultiSafepay account to specified IBAN
	 * 
	 * @param payout Payout object with amount, currency, bank account details
	 * @return JsonObject response from MultiSafepay
	 */
	public static JsonObject createPayout(Payout payout) {
		return MultiSafepayClient.sendRequest("payouts", "POST", payout);
	}

	/**
	 * Get payout status by payout ID
	 * 
	 * @param payoutId Payout ID from MultiSafepay
	 * @return JsonObject with payout details and status
	 */
	public static JsonObject getPayoutStatus(String payoutId) {
		return MultiSafepayClient.sendRequest("payouts/" + payoutId);
	}

	/**
	 * List all payouts with pagination
	 * 
	 * @param page Page number (1-based)
	 * @param limit Number of results per page
	 * @return JsonObject with list of payouts
	 */
	public static JsonObject listPayouts(int page, int limit) {
		return MultiSafepayClient.sendRequest("payouts?page=" + page + "&limit=" + limit);
	}

	/**
	 * List payouts with date range filter
	 * 
	 * @param page Page number (1-based)
	 * @param limit Number of results per page
	 * @param fromDate Date from in format YYYY-MM-DD
	 * @param toDate Date to in format YYYY-MM-DD
	 * @return JsonObject with list of payouts
	 */
	public static JsonObject listPayoutsWithDateRange(int page, int limit, String fromDate, String toDate) {
		return MultiSafepayClient.sendRequest(
			"payouts?page=" + page + "&limit=" + limit + "&from=" + fromDate + "&to=" + toDate
		);
	}
}
