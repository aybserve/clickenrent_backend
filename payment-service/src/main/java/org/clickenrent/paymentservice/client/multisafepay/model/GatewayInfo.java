package org.clickenrent.paymentservice.client.multisafepay.model;

public class GatewayInfo {

	public String issuer_id = "";
	public Integer qr_size = null;
	public String birthday = "";
	public String gender = "";
	public String bank_account = "";
	public String phone = "";
	public String email = "";
	public String referrer = "";
	public String user_agent = "";
	public String account_id = "";
	public String account_holder_name = "";
	public String account_holder_city = "";
	public String account_holder_country = "";
	public String account_holder_iban = "";
	public String account_holder_bic = "";
	public String card_number = "";
	public String card_holder_name = "";
	public String card_expiry_date = "";
	public String card_cvc = "";
	public String emandate = "";
	public String company = "";
	public String po_number = "";
	public String coc = "";
	public String vat = "";
	public String collecting_flow = "";
	public String action_on_declined = "";
	public String company_type = "";
	public String term_url = "";

	public static GatewayInfo DirectBank(String account_holder_name,
			String account_holder_city, String account_holder_country,
			String account_holder_iban, String account_holder_bic) {
		GatewayInfo info = new GatewayInfo();
		info.account_holder_name = account_holder_name;
		info.account_holder_city = account_holder_city;
		info.account_holder_country = account_holder_country;
		info.account_holder_iban = account_holder_iban;
		info.account_holder_bic = account_holder_bic;
		return info;
	}

	public static GatewayInfo PayAfterDelivery(String birthday,
			String bank_account, String phone, String email, String referrer,
			String user_agent) {
		GatewayInfo info = new GatewayInfo();
		info.birthday = birthday;
		info.bank_account = bank_account;
		info.phone = phone;
		info.email = email;
		info.referrer = referrer;
		info.user_agent = user_agent;
		return info;
	}

	public static GatewayInfo Einvoice(String birthday, String bank_account,
			String phone, String email, String referrer, String user_agent) {
		GatewayInfo info = new GatewayInfo();
		info.birthday = birthday;
		info.bank_account = bank_account;
		info.phone = phone;
		info.email = email;
		info.referrer = referrer;
		info.user_agent = user_agent;
		return info;
	}

	public static GatewayInfo Klarna(String birthday, String gender,
			String phone, String email) {
		GatewayInfo info = new GatewayInfo();
		info.birthday = birthday;
		info.gender = gender;
		info.phone = phone;
		info.email = email;
		return info;
	}

	public static GatewayInfo Ideal(String issuer_id) {
		GatewayInfo info = new GatewayInfo();
		info.issuer_id = issuer_id;
		return info;
	}

	public static GatewayInfo IdealQR(Integer qr_size) {
		GatewayInfo info = new GatewayInfo();
		info.qr_size = qr_size;
		return info;
	}

	// ========================================
	// Banking Methods
	// ========================================

	public static GatewayInfo Bancontact() {
		return new GatewayInfo();
	}

	public static GatewayInfo BancontactQR(Integer qr_size) {
		GatewayInfo info = new GatewayInfo();
		info.qr_size = qr_size;
		return info;
	}

	public static GatewayInfo Belfius() {
		return new GatewayInfo();
	}

	public static GatewayInfo Bizum(String phone) {
		GatewayInfo info = new GatewayInfo();
		info.phone = phone;
		return info;
	}

	public static GatewayInfo CBC() {
		return new GatewayInfo();
	}

	public static GatewayInfo KBC() {
		return new GatewayInfo();
	}

	public static GatewayInfo DirectDebit(String account_holder_name, String account_holder_iban, String emandate) {
		GatewayInfo info = new GatewayInfo();
		info.account_holder_name = account_holder_name;
		info.account_holder_iban = account_holder_iban;
		info.emandate = emandate;
		return info;
	}

	public static GatewayInfo Dotpay() {
		return new GatewayInfo();
	}

	public static GatewayInfo EPS(String account_holder_bic) {
		GatewayInfo info = new GatewayInfo();
		info.account_holder_bic = account_holder_bic;
		return info;
	}

	public static GatewayInfo Giropay(String account_holder_bic) {
		GatewayInfo info = new GatewayInfo();
		info.account_holder_bic = account_holder_bic;
		return info;
	}

	public static GatewayInfo MBWay(String phone) {
		GatewayInfo info = new GatewayInfo();
		info.phone = phone;
		return info;
	}

	public static GatewayInfo Multibanco() {
		return new GatewayInfo();
	}

	public static GatewayInfo MyBank() {
		return new GatewayInfo();
	}

	public static GatewayInfo Sofort() {
		return new GatewayInfo();
	}

	public static GatewayInfo Trustly() {
		return new GatewayInfo();
	}

	// ========================================
	// Card Methods
	// ========================================

	public static GatewayInfo CreditCard(String card_number, String card_cvc, 
			String card_expiry_date, String card_holder_name) {
		GatewayInfo info = new GatewayInfo();
		info.card_number = card_number;
		info.card_cvc = card_cvc;
		info.card_expiry_date = card_expiry_date;
		info.card_holder_name = card_holder_name;
		return info;
	}

	// ========================================
	// BNPL (Buy Now Pay Later) Methods
	// ========================================

	public static GatewayInfo Billink(String birthday, String gender, String company_type) {
		GatewayInfo info = new GatewayInfo();
		info.birthday = birthday;
		info.gender = gender;
		info.company_type = company_type;
		return info;
	}

	public static GatewayInfo In3(String birthday, String phone) {
		GatewayInfo info = new GatewayInfo();
		info.birthday = birthday;
		info.phone = phone;
		return info;
	}

	public static GatewayInfo Riverty(String birthday, String gender, String phone, String email) {
		GatewayInfo info = new GatewayInfo();
		info.birthday = birthday;
		info.gender = gender;
		info.phone = phone;
		info.email = email;
		return info;
	}

	// ========================================
	// Prepaid Cards (Gift Cards)
	// ========================================

	public static GatewayInfo Edenred(String card_number) {
		GatewayInfo info = new GatewayInfo();
		info.card_number = card_number;
		return info;
	}

	public static GatewayInfo GiftCard(String card_number) {
		GatewayInfo info = new GatewayInfo();
		info.card_number = card_number;
		return info;
	}

	public static GatewayInfo Monizze(String card_number) {
		GatewayInfo info = new GatewayInfo();
		info.card_number = card_number;
		return info;
	}

	public static GatewayInfo Paysafecard(String card_number) {
		GatewayInfo info = new GatewayInfo();
		info.card_number = card_number;
		return info;
	}

	public static GatewayInfo Sodexo(String card_number) {
		GatewayInfo info = new GatewayInfo();
		info.card_number = card_number;
		return info;
	}

	// ========================================
	// Wallets
	// ========================================

	public static GatewayInfo Alipay() {
		return new GatewayInfo();
	}

	public static GatewayInfo AlipayPlus() {
		return new GatewayInfo();
	}

	public static GatewayInfo AmazonPay() {
		return new GatewayInfo();
	}

	public static GatewayInfo ApplePay(String payment_token) {
		GatewayInfo info = new GatewayInfo();
		// Apple Pay token would be handled in a separate field or via payment_token parameter
		return info;
	}

	public static GatewayInfo GooglePay(String payment_token) {
		GatewayInfo info = new GatewayInfo();
		// Google Pay token would be handled in a separate field or via payment_token parameter
		return info;
	}

	public static GatewayInfo PayPal() {
		return new GatewayInfo();
	}

	public static GatewayInfo WeChat() {
		return new GatewayInfo();
	}
}