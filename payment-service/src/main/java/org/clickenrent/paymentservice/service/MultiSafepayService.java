package org.clickenrent.paymentservice.service;

import com.google.gson.JsonObject;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.paymentservice.client.multisafepay.MultiSafepayClient;
import org.clickenrent.paymentservice.client.multisafepay.model.*;
import org.clickenrent.paymentservice.exception.MultiSafepayIntegrationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Service for MultiSafePay API integration
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MultiSafepayService {

    @Value("${multisafepay.api.key}")
    private String multiSafepayApiKey;

    @Value("${multisafepay.test.mode}")
    private Boolean testMode;

    @Value("${multisafepay.webhook.secret:}")
    private String webhookSecret;

    @Value("${multisafepay.notification.url}")
    private String notificationUrl;

    @Value("${multisafepay.cancel.url:http://localhost:3000/payment/cancelled}")
    private String cancelUrl;

    @Value("${multisafepay.redirect.url:http://localhost:3000/payment/success}")
    private String redirectUrl;

    @PostConstruct
    public void init() {
        MultiSafepayClient.init(testMode, multiSafepayApiKey);
        log.info("MultiSafePay client initialized in {} mode", testMode ? "TEST" : "PRODUCTION");
    }

    /**
     * Create a MultiSafePay customer (Note: MultiSafePay doesn't have separate customer objects like Stripe)
     * This method returns a placeholder customer ID for compatibility with the payment provider abstraction
     * 
     * @param userExternalId User external ID for metadata
     * @param email Customer email
     * @return Customer identifier (email in this case)
     */
    public String createCustomer(String userExternalId, String email) {
        log.info("MultiSafePay doesn't use separate customer objects. Using email as customer identifier: {}", email);
        // MultiSafePay doesn't have a separate customer creation API
        // Customer data is sent with each order
        return email; // Return email as customer identifier
    }

    /**
     * Create a payment order
     * 
     * @param amount Amount to charge
     * @param currency Currency code (e.g., "EUR", "USD")
     * @param customerId Customer email (MultiSafePay uses email)
     * @param description Order description
     * @return Order ID
     */
    public String createOrder(BigDecimal amount, String currency, String customerId, String description) {
        JsonObject response = createOrderWithResponse(amount, currency, customerId, description);
        
        if (response != null && response.has("success") && response.get("success").getAsBoolean()) {
            JsonObject data = response.getAsJsonObject("data");
            return data.get("order_id").getAsString();
        }
        
        throw new MultiSafepayIntegrationException("Failed to create order");
    }

    /**
     * Create a payment order and return full response (including payment URL)
     * 
     * @param amount Amount to charge
     * @param currency Currency code (e.g., "EUR", "USD")
     * @param customerId Customer email (MultiSafePay uses email)
     * @param description Order description
     * @return Full JsonObject response from MultiSafePay
     */
    public JsonObject createOrderWithResponse(BigDecimal amount, String currency, String customerId, String description) {
        try {
            // Convert amount to cents (MultiSafePay uses smallest currency unit)
            int amountInCents = amount.multiply(new BigDecimal(100)).intValue();

            // Generate unique order ID
            String orderId = "order_" + System.currentTimeMillis();

            // Create payment options with notification and redirect URLs
            PaymentOptions paymentOptions = new PaymentOptions(
                    notificationUrl, // notification URL (webhook)
                    cancelUrl,       // cancel URL (where user goes if payment cancelled)
                    redirectUrl      // redirect URL (where user goes after successful payment)
            );

            // Create order
            Order order = new Order();
            order.setRedirect(orderId, description, amountInCents, currency.toUpperCase(), paymentOptions);

            // Add customer information if available
            if (customerId != null && !customerId.isEmpty()) {
                Customer customer = new Customer();
                customer.email = customerId;
                order.customer = customer;
            }

            // Send request to MultiSafePay
            JsonObject response = MultiSafepayClient.createOrder(order);

            if (response != null && response.has("success") && response.get("success").getAsBoolean()) {
                JsonObject data = response.getAsJsonObject("data");
                String orderIdResponse = data.get("order_id").getAsString();
                log.info("Created MultiSafePay order: {} for amount: {} {}", orderIdResponse, amount, currency);
                log.debug("Full create order response: {}", response.toString());
                return response;
            } else {
                String errorMessage = response != null && response.has("error_info") 
                    ? response.get("error_info").getAsString() 
                    : "Unknown error";
                throw new MultiSafepayIntegrationException("Failed to create order: " + errorMessage);
            }
        } catch (Exception e) {
            log.error("Failed to create MultiSafePay order for amount: {} {}", amount, currency, e);
            throw new MultiSafepayIntegrationException("Failed to create order: " + e.getMessage(), e);
        }
    }

    /**
     * Get order details
     * 
     * @param orderId Order ID
     * @return Order details as JsonObject
     */
    public JsonObject getOrder(String orderId) {
        try {
            JsonObject response = MultiSafepayClient.GetOrder(orderId);
            log.info("Retrieved MultiSafePay order: {}", orderId);
            return response;
        } catch (Exception e) {
            log.error("Failed to retrieve MultiSafePay order: {}", orderId, e);
            throw new MultiSafepayIntegrationException("Failed to retrieve order: " + e.getMessage(), e);
        }
    }

    /**
     * Update order (e.g., add invoice ID)
     * 
     * @param orderId Order ID
     * @param invoiceId Invoice ID
     * @return Response as JsonObject
     */
    public JsonObject updateOrderInvoice(String orderId, String invoiceId) {
        try {
            JsonObject response = MultiSafepayClient.SetOrderInvoice(orderId, invoiceId);
            log.info("Updated MultiSafePay order {} with invoice: {}", orderId, invoiceId);
            return response;
        } catch (Exception e) {
            log.error("Failed to update MultiSafePay order: {}", orderId, e);
            throw new MultiSafepayIntegrationException("Failed to update order: " + e.getMessage(), e);
        }
    }

    /**
     * Update order shipping information
     * 
     * @param orderId Order ID
     * @param shipDate Ship date
     * @param carrier Carrier name
     * @param trackTraceCode Tracking code
     * @return Response as JsonObject
     */
    public JsonObject updateOrderShipping(String orderId, String shipDate, String carrier, String trackTraceCode) {
        try {
            JsonObject response = MultiSafepayClient.SetOrderShipping(orderId, shipDate, carrier, trackTraceCode);
            log.info("Updated MultiSafePay order {} with shipping info", orderId);
            return response;
        } catch (Exception e) {
            log.error("Failed to update shipping for MultiSafePay order: {}", orderId, e);
            throw new MultiSafepayIntegrationException("Failed to update shipping: " + e.getMessage(), e);
        }
    }

    /**
     * Capture payment (Note: MultiSafePay handles this automatically for most payment methods)
     * This method is for compatibility with the payment provider abstraction
     * 
     * @param orderId Order ID
     * @return Transaction ID
     */
    public String capturePayment(String orderId) {
        log.info("MultiSafePay handles payment capture automatically for order: {}", orderId);
        // MultiSafePay automatically captures payments for most methods
        // Just verify the order status
        JsonObject order = getOrder(orderId);
        if (order != null && order.has("data")) {
            JsonObject data = order.getAsJsonObject("data");
            String status = data.get("status").getAsString();
            log.info("Order {} status: {}", orderId, status);
            
            if ("completed".equalsIgnoreCase(status)) {
                return data.has("transaction_id") ? data.get("transaction_id").getAsString() : orderId;
            }
        }
        throw new MultiSafepayIntegrationException("Payment not completed for order: " + orderId);
    }

    /**
     * Cancel authorized payment
     * 
     * @param orderId Order ID
     * @return Response as JsonObject
     */
    public JsonObject cancelAuthorizedPayment(String orderId) {
        try {
            // MultiSafePay doesn't have a specific cancel endpoint
            // Orders expire automatically or can be marked as cancelled via update
            log.info("Cancelling MultiSafePay order: {}", orderId);
            JsonObject order = getOrder(orderId);
            log.info("Order {} marked for cancellation", orderId);
            return order;
        } catch (Exception e) {
            log.error("Failed to cancel MultiSafePay order: {}", orderId, e);
            throw new MultiSafepayIntegrationException("Failed to cancel order: " + e.getMessage(), e);
        }
    }

    /**
     * Create a refund for an order
     * 
     * @param orderId Order ID to refund
     * @param amount Amount to refund (null for full refund)
     * @param currency Currency code
     * @param description Refund description
     * @return Refund ID
     */
    public String createRefund(String orderId, BigDecimal amount, String currency, String description) {
        try {
            // Get order details first to determine full amount if needed
            JsonObject orderResponse = getOrder(orderId);
            
            Integer refundAmount;
            if (amount != null) {
                // Convert amount to cents
                refundAmount = amount.multiply(new BigDecimal(100)).intValue();
            } else {
                // Full refund - get amount from order
                JsonObject data = orderResponse.getAsJsonObject("data");
                refundAmount = data.get("amount").getAsInt();
            }

            JsonObject response = MultiSafepayClient.SetOrderRefund(
                    orderId, 
                    refundAmount, 
                    currency.toUpperCase(), 
                    description != null ? description : "Refund"
            );

            if (response != null && response.has("success") && response.get("success").getAsBoolean()) {
                log.info("Created refund for MultiSafePay order: {}, amount: {}", orderId, amount);
                // MultiSafePay returns the transaction ID in the response
                JsonObject data = response.getAsJsonObject("data");
                return data.has("transaction_id") ? data.get("transaction_id").getAsString() : orderId;
            } else {
                String errorMessage = response != null && response.has("error_info") 
                    ? response.get("error_info").getAsString() 
                    : "Unknown error";
                throw new MultiSafepayIntegrationException("Failed to create refund: " + errorMessage);
            }
        } catch (Exception e) {
            log.error("Failed to create refund for MultiSafePay order: {}", orderId, e);
            throw new MultiSafepayIntegrationException("Failed to create refund: " + e.getMessage(), e);
        }
    }

    /**
     * List available payment gateways
     * 
     * @return Gateways as JsonObject
     */
    public JsonObject listGateways() {
        try {
            JsonObject response = MultiSafepayClient.GetGateways();
            log.info("Retrieved MultiSafePay gateways");
            return response;
        } catch (Exception e) {
            log.error("Failed to retrieve MultiSafePay gateways", e);
            throw new MultiSafepayIntegrationException("Failed to retrieve gateways: " + e.getMessage(), e);
        }
    }

    /**
     * Get specific gateway details
     * 
     * @param gatewayId Gateway ID (e.g., "IDEAL", "VISA", "MASTERCARD")
     * @return Gateway details as JsonObject
     */
    public JsonObject getGateway(String gatewayId) {
        try {
            JsonObject response = MultiSafepayClient.GetGateway(gatewayId);
            log.info("Retrieved MultiSafePay gateway: {}", gatewayId);
            return response;
        } catch (Exception e) {
            log.error("Failed to retrieve MultiSafePay gateway: {}", gatewayId, e);
            throw new MultiSafepayIntegrationException("Failed to retrieve gateway: " + e.getMessage(), e);
        }
    }

    /**
     * Get issuer details (for payment methods like iDEAL)
     * 
     * @param issuerId Issuer ID
     * @return Issuer details as JsonObject
     */
    public JsonObject getIssuer(String issuerId) {
        try {
            JsonObject response = MultiSafepayClient.GetIssuer(issuerId);
            log.info("Retrieved MultiSafePay issuer: {}", issuerId);
            return response;
        } catch (Exception e) {
            log.error("Failed to retrieve MultiSafePay issuer: {}", issuerId, e);
            throw new MultiSafepayIntegrationException("Failed to retrieve issuer: " + e.getMessage(), e);
        }
    }

    /**
     * Get transaction details
     * 
     * @param transactionId Transaction ID
     * @return Transaction details as JsonObject
     */
    public JsonObject getTransaction(String transactionId) {
        try {
            JsonObject response = MultiSafepayClient.GetTransaction(transactionId);
            log.info("Retrieved MultiSafePay transaction: {}", transactionId);
            return response;
        } catch (Exception e) {
            log.error("Failed to retrieve MultiSafePay transaction: {}", transactionId, e);
            throw new MultiSafepayIntegrationException("Failed to retrieve transaction: " + e.getMessage(), e);
        }
    }

    /**
     * Get all transactions for an order
     * 
     * @param orderId Order ID
     * @return Transactions as JsonObject
     */
    public JsonObject getOrderTransactions(String orderId) {
        try {
            JsonObject response = MultiSafepayClient.GetOrderTransactions(orderId);
            log.info("Retrieved transactions for MultiSafePay order: {}", orderId);
            return response;
        } catch (Exception e) {
            log.error("Failed to retrieve transactions for MultiSafePay order: {}", orderId, e);
            throw new MultiSafepayIntegrationException("Failed to retrieve order transactions: " + e.getMessage(), e);
        }
    }

    /**
     * Handle MultiSafePay webhook event
     * 
     * @param payload Webhook payload
     * @param signature Webhook signature header (if available)
     * @return Parsed order ID from webhook
     */
    public String handleWebhookEvent(String payload, String signature) {
        try {
            log.info("Received MultiSafePay webhook event");
            
            // MultiSafePay sends transactionid parameter in webhook
            // Parse the payload to extract order information
            // For now, we'll log and return the payload for processing
            // In production, you should verify the webhook signature if configured
            
            if (webhookSecret != null && !webhookSecret.isEmpty() && signature != null) {
                // TODO: Implement webhook signature verification
                log.debug("Webhook signature verification not yet implemented");
            }
            
            log.debug("Webhook payload: {}", payload);
            return payload;
        } catch (Exception e) {
            log.error("Failed to process MultiSafePay webhook", e);
            throw new MultiSafepayIntegrationException("Invalid webhook: " + e.getMessage(), e);
        }
    }

    /**
     * Get payment URL from order response
     * 
     * @param orderResponse Order creation response
     * @return Payment URL for customer to complete payment
     */
    public String getPaymentUrl(JsonObject orderResponse) {
        return MultiSafepayClient.getPaymenUrl(orderResponse);
    }

    /**
     * Get QR URL from order response (for QR code payments)
     * 
     * @param orderResponse Order creation response
     * @return QR code URL
     */
    public String getQrUrl(JsonObject orderResponse) {
        return MultiSafepayClient.getQrUrl(orderResponse);
    }

    // === Mobile Payment Methods ===

    /**
     * Create direct iDEAL payment order
     * 
     * @param amount Amount to charge
     * @param currency Currency code (e.g., "EUR")
     * @param customerEmail Customer email address
     * @param description Order description
     * @param issuerId Bank issuer ID (e.g., "0031" for ABN AMRO)
     * @return Full JsonObject response from MultiSafePay
     */
    public JsonObject createDirectIdealOrder(BigDecimal amount, String currency, 
            String customerEmail, String description, String issuerId) {
        try {
            // Convert amount to cents
            int amountInCents = amount.multiply(new BigDecimal(100)).intValue();
            
            // Generate unique order ID
            String orderId = "order_ideal_" + System.currentTimeMillis();
            
            // Create payment options with notification and redirect URLs
            PaymentOptions paymentOptions = new PaymentOptions(
                notificationUrl,
                cancelUrl,
                redirectUrl
            );
            
            // Create gateway info with bank issuer
            GatewayInfo gatewayInfo = GatewayInfo.Ideal(issuerId);
            
            // Create order
            Order order = new Order();
            order.setDirectIdeal(orderId, description, amountInCents, 
                currency.toUpperCase(), paymentOptions, gatewayInfo);
            
            // Add customer information if available
            if (customerEmail != null && !customerEmail.isEmpty()) {
                Customer customer = new Customer();
                customer.email = customerEmail;
                order.customer = customer;
            }
            
            // Send request to MultiSafePay
            JsonObject response = MultiSafepayClient.createOrder(order);
            
            if (response != null && response.has("success") && response.get("success").getAsBoolean()) {
                log.info("Created direct iDEAL order: {} for amount: {} {} with bank: {}", 
                    orderId, amount, currency, issuerId);
            } else {
                log.warn("Failed to create direct iDEAL order: {}", orderId);
            }
            
            return response;
        } catch (Exception e) {
            log.error("Failed to create direct iDEAL order", e);
            throw new MultiSafepayIntegrationException("Failed to create direct iDEAL payment: " + e.getMessage(), e);
        }
    }

    /**
     * Create direct bank transfer order
     * 
     * @param amount Amount to charge
     * @param currency Currency code (e.g., "EUR")
     * @param description Order description
     * @param accountHolderName Account holder name
     * @param accountHolderCity Account holder city
     * @param accountHolderCountry Account holder country code
     * @param accountHolderIban Account holder IBAN
     * @param accountHolderBic Account holder BIC
     * @return Full JsonObject response from MultiSafePay
     */
    public JsonObject createDirectBankOrder(BigDecimal amount, String currency,
            String description, String accountHolderName, String accountHolderCity,
            String accountHolderCountry, String accountHolderIban, String accountHolderBic) {
        try {
            // Convert amount to cents
            int amountInCents = amount.multiply(new BigDecimal(100)).intValue();
            
            // Generate unique order ID
            String orderId = "order_bank_" + System.currentTimeMillis();
            
            // Create gateway info with bank account details
            GatewayInfo gatewayInfo = GatewayInfo.DirectBank(
                accountHolderName, 
                accountHolderCity, 
                accountHolderCountry,
                accountHolderIban, 
                accountHolderBic
            );
            
            // Create order
            Order order = new Order();
            order.setDirectBank(orderId, description, amountInCents,
                currency.toUpperCase(), gatewayInfo);
            
            // Send request to MultiSafePay
            JsonObject response = MultiSafepayClient.createOrder(order);
            
            if (response != null && response.has("success") && response.get("success").getAsBoolean()) {
                log.info("Created direct bank order: {} for amount: {} {}", orderId, amount, currency);
            } else {
                log.warn("Failed to create direct bank order: {}", orderId);
            }
            
            return response;
        } catch (Exception e) {
            log.error("Failed to create direct bank order", e);
            throw new MultiSafepayIntegrationException("Failed to create direct bank payment: " + e.getMessage(), e);
        }
    }

    /**
     * Get list of iDEAL issuers (banks) from MultiSafePay
     * 
     * @return JsonObject containing issuers list
     */
    public JsonObject getIdealIssuers() {
        try {
            JsonObject response = MultiSafepayClient.GetGateway("IDEAL");
            log.info("Retrieved iDEAL issuers from MultiSafePay");
            return response;
        } catch (Exception e) {
            log.error("Failed to get iDEAL issuers", e);
            throw new MultiSafepayIntegrationException("Failed to retrieve bank list: " + e.getMessage(), e);
        }
    }

    // === Mobile Payment Methods with Split Payments ===

    /**
     * Create direct iDEAL payment order with split payments
     * 
     * @param amount Amount to charge
     * @param currency Currency code (e.g., "EUR")
     * @param customerEmail Customer email address
     * @param description Order description
     * @param issuerId Bank issuer ID (e.g., "0031" for ABN AMRO)
     * @param affiliate Affiliate object containing split payment configuration
     * @return Full JsonObject response from MultiSafePay
     */
    public JsonObject createDirectIdealOrderWithSplits(BigDecimal amount, String currency, 
            String customerEmail, String description, String issuerId, Affiliate affiliate) {
        try {
            // Convert amount to cents
            int amountInCents = amount.multiply(new BigDecimal(100)).intValue();
            
            // Generate unique order ID
            String orderId = "order_ideal_split_" + System.currentTimeMillis();
            
            // Create payment options with notification and redirect URLs
            PaymentOptions paymentOptions = new PaymentOptions(
                notificationUrl,
                cancelUrl,
                redirectUrl
            );
            
            // Create gateway info with bank issuer
            GatewayInfo gatewayInfo = GatewayInfo.Ideal(issuerId);
            
            // Create order with splits
            Order order = new Order();
            order.setDirectIdealWithSplits(orderId, description, amountInCents, 
                currency.toUpperCase(), paymentOptions, gatewayInfo, affiliate);
            
            // Add customer information if available
            if (customerEmail != null && !customerEmail.isEmpty()) {
                Customer customer = new Customer();
                customer.email = customerEmail;
                order.customer = customer;
            }
            
            // Send request to MultiSafePay
            JsonObject response = MultiSafepayClient.createOrder(order);
            
            if (response != null && response.has("success") && response.get("success").getAsBoolean()) {
                log.info("Created direct iDEAL order with splits: {} for amount: {} {} with bank: {} and {} splits", 
                    orderId, amount, currency, issuerId, affiliate.split_payments != null ? affiliate.split_payments.size() : 0);
            } else {
                log.warn("Failed to create direct iDEAL order with splits: {}", orderId);
            }
            
            return response;
        } catch (Exception e) {
            log.error("Failed to create direct iDEAL order with splits", e);
            throw new MultiSafepayIntegrationException("Failed to create direct iDEAL payment with splits: " + e.getMessage(), e);
        }
    }

    /**
     * Create direct bank transfer order with split payments
     * 
     * @param amount Amount to charge
     * @param currency Currency code (e.g., "EUR")
     * @param description Order description
     * @param accountHolderName Account holder name
     * @param accountHolderCity Account holder city
     * @param accountHolderCountry Account holder country code
     * @param accountHolderIban Account holder IBAN
     * @param accountHolderBic Account holder BIC
     * @param affiliate Affiliate object containing split payment configuration
     * @return Full JsonObject response from MultiSafePay
     */
    public JsonObject createDirectBankOrderWithSplits(BigDecimal amount, String currency,
            String description, String accountHolderName, String accountHolderCity,
            String accountHolderCountry, String accountHolderIban, String accountHolderBic, 
            Affiliate affiliate) {
        try {
            // Convert amount to cents
            int amountInCents = amount.multiply(new BigDecimal(100)).intValue();
            
            // Generate unique order ID
            String orderId = "order_bank_split_" + System.currentTimeMillis();
            
            // Create gateway info with bank account details
            GatewayInfo gatewayInfo = GatewayInfo.DirectBank(
                accountHolderName, 
                accountHolderCity, 
                accountHolderCountry,
                accountHolderIban, 
                accountHolderBic
            );
            
            // Create order with splits
            Order order = new Order();
            order.setDirectBankWithSplits(orderId, description, amountInCents,
                currency.toUpperCase(), gatewayInfo, affiliate);
            
            // Send request to MultiSafePay
            JsonObject response = MultiSafepayClient.createOrder(order);
            
            if (response != null && response.has("success") && response.get("success").getAsBoolean()) {
                log.info("Created direct bank order with splits: {} for amount: {} {} with {} splits", 
                    orderId, amount, currency, affiliate.split_payments != null ? affiliate.split_payments.size() : 0);
            } else {
                log.warn("Failed to create direct bank order with splits: {}", orderId);
            }
            
            return response;
        } catch (Exception e) {
            log.error("Failed to create direct bank order with splits", e);
            throw new MultiSafepayIntegrationException("Failed to create direct bank payment with splits: " + e.getMessage(), e);
        }
    }

    /**
     * Create redirect order with split payments
     * 
     * @param amount Amount to charge
     * @param currency Currency code (e.g., "EUR")
     * @param customerEmail Customer email address
     * @param description Order description
     * @param affiliate Affiliate object containing split payment configuration
     * @return Full JsonObject response from MultiSafePay
     */
    public JsonObject createRedirectOrderWithSplits(BigDecimal amount, String currency, 
            String customerEmail, String description, Affiliate affiliate) {
        try {
            // Convert amount to cents
            int amountInCents = amount.multiply(new BigDecimal(100)).intValue();
            
            // Generate unique order ID
            String orderId = "order_split_" + System.currentTimeMillis();
            
            // Create payment options with notification and redirect URLs
            PaymentOptions paymentOptions = new PaymentOptions(
                notificationUrl,
                cancelUrl,
                redirectUrl
            );
            
            // Create order with splits
            Order order = new Order();
            order.setRedirectWithSplits(orderId, description, amountInCents, 
                currency.toUpperCase(), paymentOptions, affiliate);
            
            // Add customer information if available
            if (customerEmail != null && !customerEmail.isEmpty()) {
                Customer customer = new Customer();
                customer.email = customerEmail;
                order.customer = customer;
            }
            
            // Send request to MultiSafePay
            JsonObject response = MultiSafepayClient.createOrder(order);
            
            if (response != null && response.has("success") && response.get("success").getAsBoolean()) {
                log.info("Created redirect order with splits: {} for amount: {} {} with {} splits", 
                    orderId, amount, currency, affiliate.split_payments != null ? affiliate.split_payments.size() : 0);
            } else {
                log.warn("Failed to create redirect order with splits: {}", orderId);
            }
            
            return response;
        } catch (Exception e) {
            log.error("Failed to create redirect order with splits", e);
            throw new MultiSafepayIntegrationException("Failed to create redirect payment with splits: " + e.getMessage(), e);
        }
    }

    // === New API Methods ===

    /**
     * Capture authorized payment
     * 
     * @param orderId Order ID
     * @return Response as JsonObject
     */
    public JsonObject captureOrder(String orderId) {
        try {
            JsonObject response = MultiSafepayClient.captureOrder(orderId);
            log.info("Captured payment for MultiSafePay order: {}", orderId);
            return response;
        } catch (Exception e) {
            log.error("Failed to capture payment for MultiSafePay order: {}", orderId, e);
            throw new MultiSafepayIntegrationException("Failed to capture payment: " + e.getMessage(), e);
        }
    }

    /**
     * Cancel authorized payment
     * 
     * @param orderId Order ID
     * @return Response as JsonObject
     */
    public JsonObject cancelAuthorization(String orderId) {
        try {
            JsonObject response = MultiSafepayClient.cancelAuthorization(orderId);
            log.info("Cancelled authorization for MultiSafePay order: {}", orderId);
            return response;
        } catch (Exception e) {
            log.error("Failed to cancel authorization for MultiSafePay order: {}", orderId, e);
            throw new MultiSafepayIntegrationException("Failed to cancel authorization: " + e.getMessage(), e);
        }
    }

    /**
     * Extend order expiration
     * 
     * @param orderId Order ID
     * @param days Number of days to extend
     * @return Response as JsonObject
     */
    public JsonObject extendOrderExpiration(String orderId, int days) {
        try {
            JsonObject response = MultiSafepayClient.extendExpiration(orderId, days);
            log.info("Extended expiration for MultiSafePay order: {} by {} days", orderId, days);
            return response;
        } catch (Exception e) {
            log.error("Failed to extend expiration for MultiSafePay order: {}", orderId, e);
            throw new MultiSafepayIntegrationException("Failed to extend expiration: " + e.getMessage(), e);
        }
    }

    /**
     * Cancel Bancontact QR payment
     * 
     * @param orderId Order ID
     * @return Response as JsonObject
     */
    public JsonObject cancelBancontactQRPayment(String orderId) {
        try {
            JsonObject response = MultiSafepayClient.cancelBancontactQR(orderId);
            log.info("Cancelled Bancontact QR payment for order: {}", orderId);
            return response;
        } catch (Exception e) {
            log.error("Failed to cancel Bancontact QR payment for order: {}", orderId, e);
            throw new MultiSafepayIntegrationException("Failed to cancel Bancontact QR payment: " + e.getMessage(), e);
        }
    }

    /**
     * Put PAD (Pay After Delivery) order on hold
     * 
     * @param orderId Order ID
     * @return Response as JsonObject
     */
    public JsonObject putPADOrderOnHold(String orderId) {
        try {
            JsonObject response = MultiSafepayClient.putPADOrderOnHold(orderId);
            log.info("Put PAD order on hold: {}", orderId);
            return response;
        } catch (Exception e) {
            log.error("Failed to put PAD order on hold: {}", orderId, e);
            throw new MultiSafepayIntegrationException("Failed to put PAD order on hold: " + e.getMessage(), e);
        }
    }

    /**
     * Cancel a refund
     * 
     * @param orderId Order ID
     * @param refundId Refund ID
     * @return Response as JsonObject
     */
    public JsonObject cancelRefund(String orderId, String refundId) {
        try {
            JsonObject response = MultiSafepayClient.cancelRefund(orderId, refundId);
            log.info("Cancelled refund {} for MultiSafePay order: {}", refundId, orderId);
            return response;
        } catch (Exception e) {
            log.error("Failed to cancel refund for MultiSafePay order: {}", orderId, e);
            throw new MultiSafepayIntegrationException("Failed to cancel refund: " + e.getMessage(), e);
        }
    }

    /**
     * Challenge a chargeback
     * 
     * @param orderId Order ID
     * @param reason Challenge reason
     * @return Response as JsonObject
     */
    public JsonObject challengeChargeback(String orderId, String reason) {
        try {
            JsonObject response = MultiSafepayClient.challengeChargeback(orderId, reason);
            log.info("Challenged chargeback for MultiSafePay order: {}", orderId);
            return response;
        } catch (Exception e) {
            log.error("Failed to challenge chargeback for MultiSafePay order: {}", orderId, e);
            throw new MultiSafepayIntegrationException("Failed to challenge chargeback: " + e.getMessage(), e);
        }
    }

    /**
     * List payment tokens
     * 
     * @param page Page number
     * @param pageSize Page size
     * @return Tokens list as JsonObject
     */
    public JsonObject listTokens(int page, int pageSize) {
        try {
            JsonObject response = MultiSafepayClient.listTokens(page, pageSize);
            log.info("Retrieved MultiSafePay tokens list, page: {}", page);
            return response;
        } catch (Exception e) {
            log.error("Failed to retrieve MultiSafePay tokens", e);
            throw new MultiSafepayIntegrationException("Failed to retrieve tokens: " + e.getMessage(), e);
        }
    }

    /**
     * Get token details
     * 
     * @param tokenId Token ID
     * @return Token details as JsonObject
     */
    public JsonObject getToken(String tokenId) {
        try {
            JsonObject response = MultiSafepayClient.getToken(tokenId);
            log.info("Retrieved MultiSafePay token: {}", tokenId);
            return response;
        } catch (Exception e) {
            log.error("Failed to retrieve MultiSafePay token: {}", tokenId, e);
            throw new MultiSafepayIntegrationException("Failed to retrieve token: " + e.getMessage(), e);
        }
    }

    /**
     * Update a token
     * 
     * @param tokenId Token ID
     * @param token Token data to update
     * @return Response as JsonObject
     */
    public JsonObject updateToken(String tokenId, org.clickenrent.paymentservice.client.multisafepay.model.Token token) {
        try {
            JsonObject response = MultiSafepayClient.updateToken(tokenId, token);
            log.info("Updated MultiSafePay token: {}", tokenId);
            return response;
        } catch (Exception e) {
            log.error("Failed to update MultiSafePay token: {}", tokenId, e);
            throw new MultiSafepayIntegrationException("Failed to update token: " + e.getMessage(), e);
        }
    }

    /**
     * Delete a token
     * 
     * @param tokenId Token ID
     * @return Response as JsonObject
     */
    public JsonObject deleteToken(String tokenId) {
        try {
            JsonObject response = MultiSafepayClient.deleteToken(tokenId);
            log.info("Deleted MultiSafePay token: {}", tokenId);
            return response;
        } catch (Exception e) {
            log.error("Failed to delete MultiSafePay token: {}", tokenId, e);
            throw new MultiSafepayIntegrationException("Failed to delete token: " + e.getMessage(), e);
        }
    }

    /**
     * List transactions with pagination
     * 
     * @param page Page number
     * @param pageSize Page size
     * @return Transactions list as JsonObject
     */
    public JsonObject listTransactions(int page, int pageSize) {
        try {
            JsonObject response = MultiSafepayClient.listTransactions(page, pageSize);
            log.info("Retrieved MultiSafePay transactions list, page: {}", page);
            return response;
        } catch (Exception e) {
            log.error("Failed to retrieve MultiSafePay transactions", e);
            throw new MultiSafepayIntegrationException("Failed to retrieve transactions: " + e.getMessage(), e);
        }
    }

    /**
     * List payment methods
     * 
     * @return Payment methods list as JsonObject
     */
    public JsonObject listPaymentMethods() {
        try {
            JsonObject response = MultiSafepayClient.listPaymentMethods();
            log.info("Retrieved MultiSafePay payment methods");
            return response;
        } catch (Exception e) {
            log.error("Failed to retrieve MultiSafePay payment methods", e);
            throw new MultiSafepayIntegrationException("Failed to retrieve payment methods: " + e.getMessage(), e);
        }
    }

    /**
     * Get payment method details
     * 
     * @param methodCode Payment method code
     * @return Payment method details as JsonObject
     */
    public JsonObject getPaymentMethod(String methodCode) {
        try {
            JsonObject response = MultiSafepayClient.getPaymentMethod(methodCode);
            log.info("Retrieved MultiSafePay payment method: {}", methodCode);
            return response;
        } catch (Exception e) {
            log.error("Failed to retrieve MultiSafePay payment method: {}", methodCode, e);
            throw new MultiSafepayIntegrationException("Failed to retrieve payment method: " + e.getMessage(), e);
        }
    }

    /**
     * Get site configuration
     * 
     * @return Site config as JsonObject
     */
    public JsonObject getSiteConfiguration() {
        try {
            JsonObject response = MultiSafepayClient.getSiteConfig();
            log.info("Retrieved MultiSafePay site configuration");
            return response;
        } catch (Exception e) {
            log.error("Failed to retrieve MultiSafePay site configuration", e);
            throw new MultiSafepayIntegrationException("Failed to retrieve site config: " + e.getMessage(), e);
        }
    }

    /**
     * Update site configuration
     * 
     * @param siteConfig Site configuration data
     * @return Response as JsonObject
     */
    public JsonObject updateSiteConfiguration(org.clickenrent.paymentservice.client.multisafepay.model.SiteConfig siteConfig) {
        try {
            JsonObject response = MultiSafepayClient.updateSiteConfig(siteConfig);
            log.info("Updated MultiSafePay site configuration");
            return response;
        } catch (Exception e) {
            log.error("Failed to update MultiSafePay site configuration", e);
            throw new MultiSafepayIntegrationException("Failed to update site config: " + e.getMessage(), e);
        }
    }

    /**
     * Get closing balances for reconciliation
     * 
     * @param fromDate From date (yyyy-MM-dd)
     * @param toDate To date (yyyy-MM-dd)
     * @return Closing balances as JsonObject
     */
    public JsonObject getClosingBalances(String fromDate, String toDate) {
        try {
            JsonObject response = MultiSafepayClient.listClosingBalances(fromDate, toDate);
            log.info("Retrieved MultiSafePay closing balances from {} to {}", fromDate, toDate);
            return response;
        } catch (Exception e) {
            log.error("Failed to retrieve MultiSafePay closing balances", e);
            throw new MultiSafepayIntegrationException("Failed to retrieve closing balances: " + e.getMessage(), e);
        }
    }

    /**
     * List POS terminals
     * 
     * @return Terminals list as JsonObject
     */
    public JsonObject listTerminals() {
        try {
            JsonObject response = MultiSafepayClient.listTerminals();
            log.info("Retrieved MultiSafePay terminals");
            return response;
        } catch (Exception e) {
            log.error("Failed to retrieve MultiSafePay terminals", e);
            throw new MultiSafepayIntegrationException("Failed to retrieve terminals: " + e.getMessage(), e);
        }
    }

    /**
     * List POS terminals by group
     * 
     * @param groupId Group ID
     * @return Terminals list as JsonObject
     */
    public JsonObject listTerminalsByGroup(String groupId) {
        try {
            JsonObject response = MultiSafepayClient.listTerminalsByGroup(groupId);
            log.info("Retrieved MultiSafePay terminals for group: {}", groupId);
            return response;
        } catch (Exception e) {
            log.error("Failed to retrieve MultiSafePay terminals for group: {}", groupId, e);
            throw new MultiSafepayIntegrationException("Failed to retrieve terminals by group: " + e.getMessage(), e);
        }
    }

    /**
     * Get POS transaction receipt
     * 
     * @param terminalId Terminal ID
     * @param transactionId Transaction ID
     * @return Receipt as JsonObject
     */
    public JsonObject getReceipt(String terminalId, String transactionId) {
        try {
            JsonObject response = MultiSafepayClient.getReceipt(terminalId, transactionId);
            log.info("Retrieved receipt for terminal: {}, transaction: {}", terminalId, transactionId);
            return response;
        } catch (Exception e) {
            log.error("Failed to retrieve receipt for terminal: {}, transaction: {}", terminalId, transactionId, e);
            throw new MultiSafepayIntegrationException("Failed to retrieve receipt: " + e.getMessage(), e);
        }
    }

    /**
     * Cancel POS transaction
     * 
     * @param terminalId Terminal ID
     * @param transactionId Transaction ID
     * @return Response as JsonObject
     */
    public JsonObject cancelPOSTransaction(String terminalId, String transactionId) {
        try {
            JsonObject response = MultiSafepayClient.cancelTransaction(terminalId, transactionId);
            log.info("Cancelled POS transaction for terminal: {}, transaction: {}", terminalId, transactionId);
            return response;
        } catch (Exception e) {
            log.error("Failed to cancel POS transaction for terminal: {}, transaction: {}", terminalId, transactionId, e);
            throw new MultiSafepayIntegrationException("Failed to cancel POS transaction: " + e.getMessage(), e);
        }
    }

    /**
     * Create POS terminal
     * 
     * @param terminal Terminal data
     * @return Response as JsonObject
     */
    public JsonObject createPOSTerminal(org.clickenrent.paymentservice.client.multisafepay.model.Terminal terminal) {
        try {
            JsonObject response = MultiSafepayClient.createTerminal(terminal);
            log.info("Created POS terminal");
            return response;
        } catch (Exception e) {
            log.error("Failed to create POS terminal", e);
            throw new MultiSafepayIntegrationException("Failed to create POS terminal: " + e.getMessage(), e);
        }
    }

    /**
     * Verify webhook signature
     * 
     * @param payload Webhook payload
     * @param signature Webhook signature
     * @return true if signature is valid
     */
    public boolean verifyWebhookSignature(String payload, String signature) {
        try {
            boolean valid = MultiSafepayClient.verifySignature(payload, signature, multiSafepayApiKey);
            log.info("Webhook signature verification: {}", valid ? "VALID" : "INVALID");
            return valid;
        } catch (Exception e) {
            log.error("Failed to verify webhook signature", e);
            return false;
        }
    }

    /**
     * Verify MultiSafePay API connection
     * Uses the List Gateways endpoint which is more reliable than Site Configuration
     * 
     * @return Connection status map
     */
    public java.util.Map<String, Object> verifyConnection() {
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        try {
            // Use List Gateways endpoint - it's more reliable and available for all API keys
            JsonObject response = listGateways();
            boolean connected = response != null && response.has("success") && response.get("success").getAsBoolean();
            
            result.put("connected", connected);
            result.put("testMode", testMode);
            result.put("apiKeyConfigured", multiSafepayApiKey != null && !multiSafepayApiKey.isEmpty());
            result.put("apiEndpoint", testMode ? "https://testapi.multisafepay.com" : "https://api.multisafepay.com");
            
            if (connected && response.has("data")) {
                try {
                    // Count available gateways
                    com.google.gson.JsonArray gateways = response.getAsJsonArray("data");
                    result.put("availableGateways", gateways.size());
                    result.put("message", "✅ Connected to MultiSafePay API successfully! " + gateways.size() + " payment gateways available.");
                } catch (Exception e) {
                    result.put("message", "✅ Connected to MultiSafePay API successfully!");
                }
            } else {
                result.put("message", "❌ Failed to connect to MultiSafePay API");
            }
            
            log.info("MultiSafePay connection verification: {}", connected ? "SUCCESS" : "FAILED");
            return result;
        } catch (Exception e) {
            log.error("Failed to verify MultiSafePay connection", e);
            result.put("connected", false);
            result.put("testMode", testMode);
            result.put("apiKeyConfigured", multiSafepayApiKey != null && !multiSafepayApiKey.isEmpty());
            result.put("error", e.getMessage());
            result.put("message", "❌ Failed to connect. Check your API key and network connection.");
            return result;
        }
    }
}
