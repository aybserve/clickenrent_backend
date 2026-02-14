package org.clickenrent.paymentservice.service;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Payment Provider Service - Facade/Strategy pattern for payment provider abstraction
 * Routes payment operations to the configured provider (Stripe or MultiSafePay)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentProviderService {

    @Value("${payment.provider:multisafepay}")
    private String paymentProvider;

    private final StripeService stripeService;
    private final MultiSafepayService multiSafepayService;

    /**
     * Create a customer in the active payment provider
     * 
     * @param userExternalId User external ID
     * @param email Customer email
     * @return Customer ID from the provider
     */
    public String createCustomer(String userExternalId, String email) {
        log.info("Creating customer with provider: {}", paymentProvider);
        
        if ("stripe".equalsIgnoreCase(paymentProvider)) {
            return stripeService.createCustomer(userExternalId, email);
        } else if ("multisafepay".equalsIgnoreCase(paymentProvider)) {
            return multiSafepayService.createCustomer(userExternalId, email);
        } else {
            throw new IllegalStateException("Unknown payment provider: " + paymentProvider);
        }
    }

    /**
     * Create a payment intent/order
     * 
     * @param amount Amount to charge
     * @param currency Currency code
     * @param customerId Customer ID
     * @param description Payment description
     * @return Payment intent/order ID
     */
    public String createPayment(BigDecimal amount, String currency, String customerId, String description) {
        log.info("Creating payment with provider: {} for amount: {} {}", paymentProvider, amount, currency);
        
        if ("stripe".equalsIgnoreCase(paymentProvider)) {
            return stripeService.createPaymentIntent(amount, currency, customerId);
        } else if ("multisafepay".equalsIgnoreCase(paymentProvider)) {
            return multiSafepayService.createOrder(amount, currency, customerId, description);
        } else {
            throw new IllegalStateException("Unknown payment provider: " + paymentProvider);
        }
    }

    /**
     * Confirm/capture a payment
     * 
     * @param paymentId Payment intent/order ID
     * @return Charge/transaction ID
     */
    public String confirmPayment(String paymentId) {
        log.info("Confirming payment with provider: {} for payment: {}", paymentProvider, paymentId);
        
        if ("stripe".equalsIgnoreCase(paymentProvider)) {
            return stripeService.confirmPaymentIntent(paymentId);
        } else if ("multisafepay".equalsIgnoreCase(paymentProvider)) {
            return multiSafepayService.capturePayment(paymentId);
        } else {
            throw new IllegalStateException("Unknown payment provider: " + paymentProvider);
        }
    }

    /**
     * Create a refund
     * 
     * @param chargeId Charge/order ID
     * @param amount Amount to refund (null for full refund)
     * @param currency Currency code (required for MultiSafePay)
     * @param description Refund description
     * @return Refund ID
     */
    public String createRefund(String chargeId, BigDecimal amount, String currency, String description) {
        log.info("Creating refund with provider: {} for charge: {}", paymentProvider, chargeId);
        
        if ("stripe".equalsIgnoreCase(paymentProvider)) {
            return stripeService.createRefund(chargeId, amount);
        } else if ("multisafepay".equalsIgnoreCase(paymentProvider)) {
            return multiSafepayService.createRefund(chargeId, amount, currency, description);
        } else {
            throw new IllegalStateException("Unknown payment provider: " + paymentProvider);
        }
    }

    /**
     * Attach a payment method to a customer (Stripe-specific, not applicable for MultiSafePay)
     * 
     * @param paymentMethodId Payment method ID
     * @param customerId Customer ID
     * @return Payment method ID
     */
    public String attachPaymentMethod(String paymentMethodId, String customerId) {
        log.info("Attaching payment method with provider: {}", paymentProvider);
        
        if ("stripe".equalsIgnoreCase(paymentProvider)) {
            return stripeService.attachPaymentMethod(paymentMethodId, customerId);
        } else if ("multisafepay".equalsIgnoreCase(paymentProvider)) {
            log.warn("MultiSafePay doesn't support separate payment method attachment");
            return paymentMethodId; // Return as-is for compatibility
        } else {
            throw new IllegalStateException("Unknown payment provider: " + paymentProvider);
        }
    }

    /**
     * Get payment details
     * 
     * @param paymentId Payment intent/order ID
     * @return Payment details (provider-specific format)
     */
    public Object getPaymentDetails(String paymentId) {
        log.info("Getting payment details with provider: {} for payment: {}", paymentProvider, paymentId);
        
        if ("stripe".equalsIgnoreCase(paymentProvider)) {
            return stripeService.retrievePaymentIntent(paymentId);
        } else if ("multisafepay".equalsIgnoreCase(paymentProvider)) {
            return multiSafepayService.getOrder(paymentId);
        } else {
            throw new IllegalStateException("Unknown payment provider: " + paymentProvider);
        }
    }

    /**
     * Get customer details
     * 
     * @param customerId Customer ID
     * @return Customer details (provider-specific format)
     */
    public Object getCustomerDetails(String customerId) {
        log.info("Getting customer details with provider: {} for customer: {}", paymentProvider, customerId);
        
        if ("stripe".equalsIgnoreCase(paymentProvider)) {
            return stripeService.retrieveCustomer(customerId);
        } else if ("multisafepay".equalsIgnoreCase(paymentProvider)) {
            log.info("MultiSafePay doesn't have separate customer objects. Customer ID is email: {}", customerId);
            return customerId; // Return email as customer identifier
        } else {
            throw new IllegalStateException("Unknown payment provider: " + paymentProvider);
        }
    }

    /**
     * Get available payment methods/gateways
     * 
     * @return Available payment methods (provider-specific format)
     */
    public Object getAvailablePaymentMethods() {
        log.info("Getting available payment methods with provider: {}", paymentProvider);
        
        if ("stripe".equalsIgnoreCase(paymentProvider)) {
            log.info("Stripe payment methods retrieval not implemented yet");
            return null;
        } else if ("multisafepay".equalsIgnoreCase(paymentProvider)) {
            return multiSafepayService.listGateways();
        } else {
            throw new IllegalStateException("Unknown payment provider: " + paymentProvider);
        }
    }

    /**
     * Get the currently configured payment provider
     * 
     * @return Payment provider name (stripe or multisafepay)
     */
    public String getActiveProvider() {
        return paymentProvider;
    }

    /**
     * Check if the active provider is Stripe
     * 
     * @return true if Stripe is active
     */
    public boolean isStripeActive() {
        return "stripe".equalsIgnoreCase(paymentProvider);
    }

    /**
     * Check if the active provider is MultiSafePay
     * 
     * @return true if MultiSafePay is active
     */
    public boolean isMultiSafepayActive() {
        return "multisafepay".equalsIgnoreCase(paymentProvider);
    }

    /**
     * Get payment URL (for redirect-based payments like MultiSafePay)
     * 
     * @param paymentResponse Payment creation response
     * @return Payment URL or null if not applicable
     */
    public String getPaymentUrl(Object paymentResponse) {
        if ("multisafepay".equalsIgnoreCase(paymentProvider) && paymentResponse instanceof JsonObject) {
            return multiSafepayService.getPaymentUrl((JsonObject) paymentResponse);
        }
        return null;
    }
}
