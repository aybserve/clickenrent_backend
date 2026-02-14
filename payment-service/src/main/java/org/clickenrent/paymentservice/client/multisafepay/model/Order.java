package org.clickenrent.paymentservice.client.multisafepay.model;

public class Order {

	public String order_id = null;
	public String recurring_id = null;
	public String cost = null;
	public String type = null;
	public String currency = null;
	public Integer amount = null;
	public String description = null;
	public String manual = null;
	public String gateway = null;
	public String days_active = null;
	public Integer seconds_active = null;
	public String payment_url = null;
	public String ship_date = null;
	public String tracktrace_code = null;
	public String reason = null;
	public String carrier = null;
	public String invoice_id = null;
	public String items = null;
	public String cart_expiration = null;
	public String status = null;

	public String var1 = null;
	public String var2 = null;
	public String var3 = null;

	public String use_shipping_notification = null;
	public String use_field_notifications = null;

	public Customer customer = null;
	public Delivery delivery = null;
	public Plugin plugin = null;
	public GatewayInfo gateway_info = null;
	public PaymentOptions payment_options = null;
	public ShoppingCart shopping_cart = null;
	public GoogleAnalytics google_analytics = null;
	public CustomFields custom_fields = null;
	public CustomInfo custom_info = null;
	public CheckoutOptions checkout_options = null;
	public Affiliate affiliate = null;

	public Order setRedirect(String order_id, String description,
			Integer amount, String currency, PaymentOptions payment_options) {
		this.type = "redirect";
		this.order_id = order_id;
		this.description = description;
		this.amount = amount;
		this.currency = currency;
		this.payment_options = payment_options;
		return this;
	}

	public Order setFastCheckout(String order_id, String description,
			Integer amount, String currency, PaymentOptions payment_options,
			ShoppingCart shopping_cart, CheckoutOptions checkout_options) {
		this.type = "checkout";
		this.order_id = order_id;
		this.description = description;
		this.amount = amount;
		this.currency = currency;
		this.payment_options = payment_options;
		this.shopping_cart = shopping_cart;
		this.checkout_options = checkout_options;
		return this;
	}

	public Order setDirectPayAfter(String order_id, String description,
			Integer amount, String currency, PaymentOptions payment_options,
			GatewayInfo gateway_info, ShoppingCart shopping_cart,
			CheckoutOptions checkout_options, Customer customer) {
		this.type = "direct";
		this.gateway = "PAYAFTER";
		this.order_id = order_id;
		this.description = description;
		this.amount = amount;
		this.currency = currency;
		this.payment_options = payment_options;
		this.gateway_info = gateway_info;
		this.shopping_cart = shopping_cart;
		this.checkout_options = checkout_options;
		this.customer = customer;
		return this;
	}

	public Order setDirectEinvoice(String order_id, String description,
			Integer amount, String currency, PaymentOptions payment_options,
			GatewayInfo gateway_info, ShoppingCart shopping_cart,
			CheckoutOptions checkout_options, Customer customer) {
		this.type = "direct";
		this.gateway = "EINVOICE";
		this.order_id = order_id;
		this.description = description;
		this.amount = amount;
		this.currency = currency;
		this.payment_options = payment_options;
		this.gateway_info = gateway_info;
		this.shopping_cart = shopping_cart;
		this.checkout_options = checkout_options;
		this.customer = customer;
		return this;
	}

	public Order setDirectKlarna(String order_id, String description,
			Integer amount, String currency, PaymentOptions payment_options,
			GatewayInfo gateway_info, ShoppingCart shopping_cart,
			CheckoutOptions checkout_options, Customer customer,
			Delivery delivery) {
		this.type = "direct";
		this.gateway = "KLARNA";
		this.order_id = order_id;
		this.description = description;
		this.amount = amount;
		this.currency = currency;
		this.payment_options = payment_options;
		this.gateway_info = gateway_info;
		this.shopping_cart = shopping_cart;
		this.checkout_options = checkout_options;
		this.customer = customer;
		this.delivery = delivery;
		return this;
	}

	public Order setDirectBank(String order_id, String description,
			Integer amount, String currency, GatewayInfo gateway_info) {
		this.type = "direct";
		this.gateway = "DIRECTBANK";
		this.order_id = order_id;
		this.description = description;
		this.amount = amount;
		this.currency = currency;
		this.gateway_info = gateway_info;
		return this;
	}

	public Order setDirectIdeal(String order_id, String description,
			Integer amount, String currency, PaymentOptions payment_options,
			GatewayInfo gateway_info) {
		this.type = "direct";
		this.gateway = "IDEAL";
		this.order_id = order_id;
		this.description = description;
		this.amount = amount;
		this.currency = currency;
		this.payment_options = payment_options;
		this.gateway_info = gateway_info;
		return this;
	}

	public Order setDirectIdealQR(String order_id, String description,
			Integer amount, String currency, PaymentOptions payment_options,
			GatewayInfo gateway_info) {
		this.type = "redirect";
		this.gateway = "IDEALQR";
		this.order_id = order_id;
		this.description = description;
		this.amount = amount;
		this.currency = currency;
		this.payment_options = payment_options;
		this.gateway_info = gateway_info;
		return this;
	}

	// Split payment methods

	public Order setRedirectWithSplits(String order_id, String description,
			Integer amount, String currency, PaymentOptions payment_options,
			Affiliate affiliate) {
		this.type = "redirect";
		this.order_id = order_id;
		this.description = description;
		this.amount = amount;
		this.currency = currency;
		this.payment_options = payment_options;
		this.affiliate = affiliate;
		return this;
	}

	public Order setDirectIdealWithSplits(String order_id, String description,
			Integer amount, String currency, PaymentOptions payment_options,
			GatewayInfo gateway_info, Affiliate affiliate) {
		this.type = "direct";
		this.gateway = "IDEAL";
		this.order_id = order_id;
		this.description = description;
		this.amount = amount;
		this.currency = currency;
		this.payment_options = payment_options;
		this.gateway_info = gateway_info;
		this.affiliate = affiliate;
		return this;
	}

	public Order setDirectBankWithSplits(String order_id, String description,
			Integer amount, String currency, GatewayInfo gateway_info,
			Affiliate affiliate) {
		this.type = "direct";
		this.gateway = "DIRECTBANK";
		this.order_id = order_id;
		this.description = description;
		this.amount = amount;
		this.currency = currency;
		this.gateway_info = gateway_info;
		this.affiliate = affiliate;
		return this;
	}

	// ========================================
	// Banking Methods - Direct
	// ========================================

	public Order setDirectBancontact(String order_id, String description,
			Integer amount, String currency, PaymentOptions payment_options,
			GatewayInfo gateway_info) {
		this.type = "direct";
		this.gateway = "BANCONTACT";
		this.order_id = order_id;
		this.description = description;
		this.amount = amount;
		this.currency = currency;
		this.payment_options = payment_options;
		this.gateway_info = gateway_info;
		return this;
	}

	public Order setDirectBelfius(String order_id, String description,
			Integer amount, String currency, PaymentOptions payment_options,
			GatewayInfo gateway_info) {
		this.type = "direct";
		this.gateway = "BELFIUS";
		this.order_id = order_id;
		this.description = description;
		this.amount = amount;
		this.currency = currency;
		this.payment_options = payment_options;
		this.gateway_info = gateway_info;
		return this;
	}

	public Order setDirectBizum(String order_id, String description,
			Integer amount, String currency, PaymentOptions payment_options,
			GatewayInfo gateway_info) {
		this.type = "direct";
		this.gateway = "BIZUM";
		this.order_id = order_id;
		this.description = description;
		this.amount = amount;
		this.currency = currency;
		this.payment_options = payment_options;
		this.gateway_info = gateway_info;
		return this;
	}

	public Order setDirectCBC(String order_id, String description,
			Integer amount, String currency, PaymentOptions payment_options,
			GatewayInfo gateway_info) {
		this.type = "direct";
		this.gateway = "CBC";
		this.order_id = order_id;
		this.description = description;
		this.amount = amount;
		this.currency = currency;
		this.payment_options = payment_options;
		this.gateway_info = gateway_info;
		return this;
	}

	public Order setDirectKBC(String order_id, String description,
			Integer amount, String currency, PaymentOptions payment_options,
			GatewayInfo gateway_info) {
		this.type = "direct";
		this.gateway = "KBC";
		this.order_id = order_id;
		this.description = description;
		this.amount = amount;
		this.currency = currency;
		this.payment_options = payment_options;
		this.gateway_info = gateway_info;
		return this;
	}

	public Order setDirectDebit(String order_id, String description,
			Integer amount, String currency, PaymentOptions payment_options,
			GatewayInfo gateway_info) {
		this.type = "direct";
		this.gateway = "DIRDEB";
		this.order_id = order_id;
		this.description = description;
		this.amount = amount;
		this.currency = currency;
		this.payment_options = payment_options;
		this.gateway_info = gateway_info;
		return this;
	}

	public Order setDirectDotpay(String order_id, String description,
			Integer amount, String currency, PaymentOptions payment_options,
			GatewayInfo gateway_info) {
		this.type = "direct";
		this.gateway = "DOTPAY";
		this.order_id = order_id;
		this.description = description;
		this.amount = amount;
		this.currency = currency;
		this.payment_options = payment_options;
		this.gateway_info = gateway_info;
		return this;
	}

	public Order setDirectEPS(String order_id, String description,
			Integer amount, String currency, PaymentOptions payment_options,
			GatewayInfo gateway_info) {
		this.type = "direct";
		this.gateway = "EPS";
		this.order_id = order_id;
		this.description = description;
		this.amount = amount;
		this.currency = currency;
		this.payment_options = payment_options;
		this.gateway_info = gateway_info;
		return this;
	}

	public Order setDirectGiropay(String order_id, String description,
			Integer amount, String currency, PaymentOptions payment_options,
			GatewayInfo gateway_info) {
		this.type = "direct";
		this.gateway = "GIROPAY";
		this.order_id = order_id;
		this.description = description;
		this.amount = amount;
		this.currency = currency;
		this.payment_options = payment_options;
		this.gateway_info = gateway_info;
		return this;
	}

	public Order setDirectMBWay(String order_id, String description,
			Integer amount, String currency, PaymentOptions payment_options,
			GatewayInfo gateway_info) {
		this.type = "direct";
		this.gateway = "MBWAY";
		this.order_id = order_id;
		this.description = description;
		this.amount = amount;
		this.currency = currency;
		this.payment_options = payment_options;
		this.gateway_info = gateway_info;
		return this;
	}

	public Order setDirectMultibanco(String order_id, String description,
			Integer amount, String currency, PaymentOptions payment_options,
			GatewayInfo gateway_info) {
		this.type = "direct";
		this.gateway = "MULTIBANCO";
		this.order_id = order_id;
		this.description = description;
		this.amount = amount;
		this.currency = currency;
		this.payment_options = payment_options;
		this.gateway_info = gateway_info;
		return this;
	}

	public Order setDirectMyBank(String order_id, String description,
			Integer amount, String currency, PaymentOptions payment_options,
			GatewayInfo gateway_info) {
		this.type = "direct";
		this.gateway = "MYBANK";
		this.order_id = order_id;
		this.description = description;
		this.amount = amount;
		this.currency = currency;
		this.payment_options = payment_options;
		this.gateway_info = gateway_info;
		return this;
	}

	public Order setDirectSofort(String order_id, String description,
			Integer amount, String currency, PaymentOptions payment_options,
			GatewayInfo gateway_info) {
		this.type = "direct";
		this.gateway = "SOFORT";
		this.order_id = order_id;
		this.description = description;
		this.amount = amount;
		this.currency = currency;
		this.payment_options = payment_options;
		this.gateway_info = gateway_info;
		return this;
	}

	public Order setDirectTrustly(String order_id, String description,
			Integer amount, String currency, PaymentOptions payment_options,
			GatewayInfo gateway_info) {
		this.type = "direct";
		this.gateway = "TRUSTLY";
		this.order_id = order_id;
		this.description = description;
		this.amount = amount;
		this.currency = currency;
		this.payment_options = payment_options;
		this.gateway_info = gateway_info;
		return this;
	}

	// ========================================
	// Banking Methods - Redirect
	// ========================================

	public Order setRedirectBancontact(String order_id, String description,
			Integer amount, String currency, PaymentOptions payment_options) {
		this.type = "redirect";
		this.gateway = "BANCONTACT";
		this.order_id = order_id;
		this.description = description;
		this.amount = amount;
		this.currency = currency;
		this.payment_options = payment_options;
		return this;
	}

	public Order setRedirectBancontactQR(String order_id, String description,
			Integer amount, String currency, PaymentOptions payment_options,
			GatewayInfo gateway_info) {
		this.type = "redirect";
		this.gateway = "BANCONTACTQR";
		this.order_id = order_id;
		this.description = description;
		this.amount = amount;
		this.currency = currency;
		this.payment_options = payment_options;
		this.gateway_info = gateway_info;
		return this;
	}

	public Order setRedirectBelfius(String order_id, String description,
			Integer amount, String currency, PaymentOptions payment_options) {
		this.type = "redirect";
		this.gateway = "BELFIUS";
		this.order_id = order_id;
		this.description = description;
		this.amount = amount;
		this.currency = currency;
		this.payment_options = payment_options;
		return this;
	}

	public Order setRedirectBizum(String order_id, String description,
			Integer amount, String currency, PaymentOptions payment_options) {
		this.type = "redirect";
		this.gateway = "BIZUM";
		this.order_id = order_id;
		this.description = description;
		this.amount = amount;
		this.currency = currency;
		this.payment_options = payment_options;
		return this;
	}

	public Order setRedirectCBC(String order_id, String description,
			Integer amount, String currency, PaymentOptions payment_options) {
		this.type = "redirect";
		this.gateway = "CBC";
		this.order_id = order_id;
		this.description = description;
		this.amount = amount;
		this.currency = currency;
		this.payment_options = payment_options;
		return this;
	}

	public Order setRedirectKBC(String order_id, String description,
			Integer amount, String currency, PaymentOptions payment_options) {
		this.type = "redirect";
		this.gateway = "KBC";
		this.order_id = order_id;
		this.description = description;
		this.amount = amount;
		this.currency = currency;
		this.payment_options = payment_options;
		return this;
	}

	public Order setRedirectDotpay(String order_id, String description,
			Integer amount, String currency, PaymentOptions payment_options) {
		this.type = "redirect";
		this.gateway = "DOTPAY";
		this.order_id = order_id;
		this.description = description;
		this.amount = amount;
		this.currency = currency;
		this.payment_options = payment_options;
		return this;
	}

	public Order setRedirectEPS(String order_id, String description,
			Integer amount, String currency, PaymentOptions payment_options) {
		this.type = "redirect";
		this.gateway = "EPS";
		this.order_id = order_id;
		this.description = description;
		this.amount = amount;
		this.currency = currency;
		this.payment_options = payment_options;
		return this;
	}

	public Order setRedirectGiropay(String order_id, String description,
			Integer amount, String currency, PaymentOptions payment_options) {
		this.type = "redirect";
		this.gateway = "GIROPAY";
		this.order_id = order_id;
		this.description = description;
		this.amount = amount;
		this.currency = currency;
		this.payment_options = payment_options;
		return this;
	}

	public Order setRedirectMultibanco(String order_id, String description,
			Integer amount, String currency, PaymentOptions payment_options) {
		this.type = "redirect";
		this.gateway = "MULTIBANCO";
		this.order_id = order_id;
		this.description = description;
		this.amount = amount;
		this.currency = currency;
		this.payment_options = payment_options;
		return this;
	}

	public Order setRedirectMBWay(String order_id, String description,
			Integer amount, String currency, PaymentOptions payment_options) {
		this.type = "redirect";
		this.gateway = "MBWAY";
		this.order_id = order_id;
		this.description = description;
		this.amount = amount;
		this.currency = currency;
		this.payment_options = payment_options;
		return this;
	}

	public Order setRedirectMyBank(String order_id, String description,
			Integer amount, String currency, PaymentOptions payment_options) {
		this.type = "redirect";
		this.gateway = "MYBANK";
		this.order_id = order_id;
		this.description = description;
		this.amount = amount;
		this.currency = currency;
		this.payment_options = payment_options;
		return this;
	}

	public Order setRedirectSofort(String order_id, String description,
			Integer amount, String currency, PaymentOptions payment_options) {
		this.type = "redirect";
		this.gateway = "SOFORT";
		this.order_id = order_id;
		this.description = description;
		this.amount = amount;
		this.currency = currency;
		this.payment_options = payment_options;
		return this;
	}

	public Order setRedirectTrustly(String order_id, String description,
			Integer amount, String currency, PaymentOptions payment_options) {
		this.type = "redirect";
		this.gateway = "TRUSTLY";
		this.order_id = order_id;
		this.description = description;
		this.amount = amount;
		this.currency = currency;
		this.payment_options = payment_options;
		return this;
	}

	// ========================================
	// Card Methods
	// ========================================

	public Order setDirectCreditCard(String order_id, String description,
			Integer amount, String currency, PaymentOptions payment_options,
			GatewayInfo gateway_info) {
		this.type = "direct";
		this.gateway = "CREDITCARD";
		this.order_id = order_id;
		this.description = description;
		this.amount = amount;
		this.currency = currency;
		this.payment_options = payment_options;
		this.gateway_info = gateway_info;
		return this;
	}

	public Order setRedirectCreditCard(String order_id, String description,
			Integer amount, String currency, PaymentOptions payment_options) {
		this.type = "redirect";
		this.gateway = "CREDITCARD";
		this.order_id = order_id;
		this.description = description;
		this.amount = amount;
		this.currency = currency;
		this.payment_options = payment_options;
		return this;
	}

	// ========================================
	// BNPL Methods - Direct
	// ========================================

	public Order setDirectBillink(String order_id, String description,
			Integer amount, String currency, PaymentOptions payment_options,
			GatewayInfo gateway_info, ShoppingCart shopping_cart,
			CheckoutOptions checkout_options, Customer customer) {
		this.type = "direct";
		this.gateway = "BILLINK";
		this.order_id = order_id;
		this.description = description;
		this.amount = amount;
		this.currency = currency;
		this.payment_options = payment_options;
		this.gateway_info = gateway_info;
		this.shopping_cart = shopping_cart;
		this.checkout_options = checkout_options;
		this.customer = customer;
		return this;
	}

	public Order setDirectIn3(String order_id, String description,
			Integer amount, String currency, PaymentOptions payment_options,
			GatewayInfo gateway_info, ShoppingCart shopping_cart,
			CheckoutOptions checkout_options, Customer customer) {
		this.type = "direct";
		this.gateway = "IN3";
		this.order_id = order_id;
		this.description = description;
		this.amount = amount;
		this.currency = currency;
		this.payment_options = payment_options;
		this.gateway_info = gateway_info;
		this.shopping_cart = shopping_cart;
		this.checkout_options = checkout_options;
		this.customer = customer;
		return this;
	}

	public Order setDirectRiverty(String order_id, String description,
			Integer amount, String currency, PaymentOptions payment_options,
			GatewayInfo gateway_info, ShoppingCart shopping_cart,
			CheckoutOptions checkout_options, Customer customer, Delivery delivery) {
		this.type = "direct";
		this.gateway = "AFTERPAY";
		this.order_id = order_id;
		this.description = description;
		this.amount = amount;
		this.currency = currency;
		this.payment_options = payment_options;
		this.gateway_info = gateway_info;
		this.shopping_cart = shopping_cart;
		this.checkout_options = checkout_options;
		this.customer = customer;
		this.delivery = delivery;
		return this;
	}

	// ========================================
	// BNPL Methods - Redirect
	// ========================================

	public Order setRedirectBillink(String order_id, String description,
			Integer amount, String currency, PaymentOptions payment_options,
			ShoppingCart shopping_cart, CheckoutOptions checkout_options) {
		this.type = "redirect";
		this.gateway = "BILLINK";
		this.order_id = order_id;
		this.description = description;
		this.amount = amount;
		this.currency = currency;
		this.payment_options = payment_options;
		this.shopping_cart = shopping_cart;
		this.checkout_options = checkout_options;
		return this;
	}

	public Order setRedirectIn3(String order_id, String description,
			Integer amount, String currency, PaymentOptions payment_options,
			ShoppingCart shopping_cart, CheckoutOptions checkout_options) {
		this.type = "redirect";
		this.gateway = "IN3";
		this.order_id = order_id;
		this.description = description;
		this.amount = amount;
		this.currency = currency;
		this.payment_options = payment_options;
		this.shopping_cart = shopping_cart;
		this.checkout_options = checkout_options;
		return this;
	}

	public Order setRedirectRiverty(String order_id, String description,
			Integer amount, String currency, PaymentOptions payment_options,
			ShoppingCart shopping_cart, CheckoutOptions checkout_options) {
		this.type = "redirect";
		this.gateway = "AFTERPAY";
		this.order_id = order_id;
		this.description = description;
		this.amount = amount;
		this.currency = currency;
		this.payment_options = payment_options;
		this.shopping_cart = shopping_cart;
		this.checkout_options = checkout_options;
		return this;
	}

	// ========================================
	// Prepaid Cards / Gift Cards - Direct
	// ========================================

	public Order setDirectGiftCard(String order_id, String description,
			Integer amount, String currency, PaymentOptions payment_options,
			GatewayInfo gateway_info, String gift_card_type) {
		this.type = "direct";
		this.gateway = gift_card_type;  // Dynamic gateway based on card type
		this.order_id = order_id;
		this.description = description;
		this.amount = amount;
		this.currency = currency;
		this.payment_options = payment_options;
		this.gateway_info = gateway_info;
		return this;
	}

	public Order setDirectEdenred(String order_id, String description,
			Integer amount, String currency, PaymentOptions payment_options,
			GatewayInfo gateway_info) {
		this.type = "direct";
		this.gateway = "EDENRED";
		this.order_id = order_id;
		this.description = description;
		this.amount = amount;
		this.currency = currency;
		this.payment_options = payment_options;
		this.gateway_info = gateway_info;
		return this;
	}

	public Order setDirectMonizze(String order_id, String description,
			Integer amount, String currency, PaymentOptions payment_options,
			GatewayInfo gateway_info) {
		this.type = "direct";
		this.gateway = "MONIZZE";
		this.order_id = order_id;
		this.description = description;
		this.amount = amount;
		this.currency = currency;
		this.payment_options = payment_options;
		this.gateway_info = gateway_info;
		return this;
	}

	public Order setDirectSodexo(String order_id, String description,
			Integer amount, String currency, PaymentOptions payment_options,
			GatewayInfo gateway_info) {
		this.type = "direct";
		this.gateway = "SODEXO";
		this.order_id = order_id;
		this.description = description;
		this.amount = amount;
		this.currency = currency;
		this.payment_options = payment_options;
		this.gateway_info = gateway_info;
		return this;
	}

	public Order setDirectPaysafecard(String order_id, String description,
			Integer amount, String currency, PaymentOptions payment_options,
			GatewayInfo gateway_info) {
		this.type = "direct";
		this.gateway = "PAYSAFECARD";
		this.order_id = order_id;
		this.description = description;
		this.amount = amount;
		this.currency = currency;
		this.payment_options = payment_options;
		this.gateway_info = gateway_info;
		return this;
	}

	// ========================================
	// Prepaid Cards / Gift Cards - Redirect
	// ========================================

	public Order setRedirectGiftCard(String order_id, String description,
			Integer amount, String currency, PaymentOptions payment_options,
			String gift_card_type) {
		this.type = "redirect";
		this.gateway = gift_card_type;  // Dynamic gateway based on card type
		this.order_id = order_id;
		this.description = description;
		this.amount = amount;
		this.currency = currency;
		this.payment_options = payment_options;
		return this;
	}

	public Order setRedirectEdenred(String order_id, String description,
			Integer amount, String currency, PaymentOptions payment_options) {
		this.type = "redirect";
		this.gateway = "EDENRED";
		this.order_id = order_id;
		this.description = description;
		this.amount = amount;
		this.currency = currency;
		this.payment_options = payment_options;
		return this;
	}

	public Order setRedirectMonizze(String order_id, String description,
			Integer amount, String currency, PaymentOptions payment_options) {
		this.type = "redirect";
		this.gateway = "MONIZZE";
		this.order_id = order_id;
		this.description = description;
		this.amount = amount;
		this.currency = currency;
		this.payment_options = payment_options;
		return this;
	}

	public Order setRedirectSodexo(String order_id, String description,
			Integer amount, String currency, PaymentOptions payment_options) {
		this.type = "redirect";
		this.gateway = "SODEXO";
		this.order_id = order_id;
		this.description = description;
		this.amount = amount;
		this.currency = currency;
		this.payment_options = payment_options;
		return this;
	}

	public Order setRedirectPaysafecard(String order_id, String description,
			Integer amount, String currency, PaymentOptions payment_options) {
		this.type = "redirect";
		this.gateway = "PAYSAFECARD";
		this.order_id = order_id;
		this.description = description;
		this.amount = amount;
		this.currency = currency;
		this.payment_options = payment_options;
		return this;
	}

	// ========================================
	// Wallets - Direct
	// ========================================

	public Order setDirectAlipay(String order_id, String description,
			Integer amount, String currency, PaymentOptions payment_options,
			GatewayInfo gateway_info) {
		this.type = "direct";
		this.gateway = "ALIPAY";
		this.order_id = order_id;
		this.description = description;
		this.amount = amount;
		this.currency = currency;
		this.payment_options = payment_options;
		this.gateway_info = gateway_info;
		return this;
	}

	public Order setDirectAlipayPlus(String order_id, String description,
			Integer amount, String currency, PaymentOptions payment_options,
			GatewayInfo gateway_info) {
		this.type = "direct";
		this.gateway = "ALIPAYPLUS";
		this.order_id = order_id;
		this.description = description;
		this.amount = amount;
		this.currency = currency;
		this.payment_options = payment_options;
		this.gateway_info = gateway_info;
		return this;
	}

	public Order setDirectAmazonPay(String order_id, String description,
			Integer amount, String currency, PaymentOptions payment_options,
			GatewayInfo gateway_info) {
		this.type = "direct";
		this.gateway = "AMAZONPAY";
		this.order_id = order_id;
		this.description = description;
		this.amount = amount;
		this.currency = currency;
		this.payment_options = payment_options;
		this.gateway_info = gateway_info;
		return this;
	}

	public Order setDirectApplePay(String order_id, String description,
			Integer amount, String currency, PaymentOptions payment_options,
			GatewayInfo gateway_info) {
		this.type = "direct";
		this.gateway = "APPLEPAY";
		this.order_id = order_id;
		this.description = description;
		this.amount = amount;
		this.currency = currency;
		this.payment_options = payment_options;
		this.gateway_info = gateway_info;
		return this;
	}

	public Order setDirectGooglePay(String order_id, String description,
			Integer amount, String currency, PaymentOptions payment_options,
			GatewayInfo gateway_info) {
		this.type = "direct";
		this.gateway = "GOOGLEPAY";
		this.order_id = order_id;
		this.description = description;
		this.amount = amount;
		this.currency = currency;
		this.payment_options = payment_options;
		this.gateway_info = gateway_info;
		return this;
	}

	public Order setDirectPayPal(String order_id, String description,
			Integer amount, String currency, PaymentOptions payment_options,
			GatewayInfo gateway_info) {
		this.type = "direct";
		this.gateway = "PAYPAL";
		this.order_id = order_id;
		this.description = description;
		this.amount = amount;
		this.currency = currency;
		this.payment_options = payment_options;
		this.gateway_info = gateway_info;
		return this;
	}

	public Order setDirectWeChat(String order_id, String description,
			Integer amount, String currency, PaymentOptions payment_options,
			GatewayInfo gateway_info) {
		this.type = "direct";
		this.gateway = "WECHAT";
		this.order_id = order_id;
		this.description = description;
		this.amount = amount;
		this.currency = currency;
		this.payment_options = payment_options;
		this.gateway_info = gateway_info;
		return this;
	}

	// ========================================
	// Wallets - Redirect
	// ========================================

	public Order setRedirectAlipay(String order_id, String description,
			Integer amount, String currency, PaymentOptions payment_options) {
		this.type = "redirect";
		this.gateway = "ALIPAY";
		this.order_id = order_id;
		this.description = description;
		this.amount = amount;
		this.currency = currency;
		this.payment_options = payment_options;
		return this;
	}

	public Order setRedirectAlipayPlus(String order_id, String description,
			Integer amount, String currency, PaymentOptions payment_options) {
		this.type = "redirect";
		this.gateway = "ALIPAYPLUS";
		this.order_id = order_id;
		this.description = description;
		this.amount = amount;
		this.currency = currency;
		this.payment_options = payment_options;
		return this;
	}

	public Order setRedirectAmazonPay(String order_id, String description,
			Integer amount, String currency, PaymentOptions payment_options) {
		this.type = "redirect";
		this.gateway = "AMAZONPAY";
		this.order_id = order_id;
		this.description = description;
		this.amount = amount;
		this.currency = currency;
		this.payment_options = payment_options;
		return this;
	}

	public Order setRedirectApplePay(String order_id, String description,
			Integer amount, String currency, PaymentOptions payment_options) {
		this.type = "redirect";
		this.gateway = "APPLEPAY";
		this.order_id = order_id;
		this.description = description;
		this.amount = amount;
		this.currency = currency;
		this.payment_options = payment_options;
		return this;
	}

	public Order setRedirectGooglePay(String order_id, String description,
			Integer amount, String currency, PaymentOptions payment_options) {
		this.type = "redirect";
		this.gateway = "GOOGLEPAY";
		this.order_id = order_id;
		this.description = description;
		this.amount = amount;
		this.currency = currency;
		this.payment_options = payment_options;
		return this;
	}

	public Order setRedirectPayPal(String order_id, String description,
			Integer amount, String currency, PaymentOptions payment_options) {
		this.type = "redirect";
		this.gateway = "PAYPAL";
		this.order_id = order_id;
		this.description = description;
		this.amount = amount;
		this.currency = currency;
		this.payment_options = payment_options;
		return this;
	}

	public Order setRedirectWeChat(String order_id, String description,
			Integer amount, String currency, PaymentOptions payment_options) {
		this.type = "redirect";
		this.gateway = "WECHAT";
		this.order_id = order_id;
		this.description = description;
		this.amount = amount;
		this.currency = currency;
		this.payment_options = payment_options;
		return this;
	}
}
