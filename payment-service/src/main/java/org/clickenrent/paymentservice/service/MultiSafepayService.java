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
}
