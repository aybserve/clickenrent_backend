package org.clickenrent.paymentservice.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.net.Webhook;
import com.stripe.param.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.paymentservice.exception.StripeIntegrationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Service for Stripe API integration
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StripeService {

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    /**
     * Create a Stripe customer
     * 
     * @param userId User ID for metadata
     * @param email Customer email
     * @return Stripe customer ID
     */
    public String createCustomer(Long userId, String email) {
        try {
            CustomerCreateParams params = CustomerCreateParams.builder()
                    .setEmail(email)
                    .putMetadata("userId", userId.toString())
                    .build();

            Customer customer = Customer.create(params);
            log.info("Created Stripe customer: {} for user: {}", customer.getId(), userId);
            return customer.getId();
        } catch (StripeException e) {
            log.error("Failed to create Stripe customer for user: {}", userId, e);
            throw new StripeIntegrationException("Failed to create Stripe customer: " + e.getMessage(), e);
        }
    }

    /**
     * Create a payment intent
     * 
     * @param amount Amount to charge
     * @param currency Currency code (e.g., "usd", "eur")
     * @param customerId Stripe customer ID
     * @return Payment intent ID
     */
    public String createPaymentIntent(BigDecimal amount, String currency, String customerId) {
        try {
            // Convert amount to cents (Stripe uses smallest currency unit)
            long amountInCents = amount.multiply(new BigDecimal(100)).longValue();

            PaymentIntentCreateParams.Builder paramsBuilder = PaymentIntentCreateParams.builder()
                    .setAmount(amountInCents)
                    .setCurrency(currency.toLowerCase())
                    .setConfirm(false); // Don't auto-confirm

            if (customerId != null) {
                paramsBuilder.setCustomer(customerId);
            }

            PaymentIntent paymentIntent = PaymentIntent.create(paramsBuilder.build());
            log.info("Created payment intent: {} for amount: {} {}", 
                    paymentIntent.getId(), amount, currency);
            return paymentIntent.getId();
        } catch (StripeException e) {
            log.error("Failed to create payment intent for amount: {} {}", amount, currency, e);
            throw new StripeIntegrationException("Failed to create payment intent: " + e.getMessage(), e);
        }
    }

    /**
     * Confirm a payment intent
     * 
     * @param paymentIntentId Payment intent ID to confirm
     * @return Charge ID from the confirmed payment intent
     */
    public String confirmPaymentIntent(String paymentIntentId) {
        try {
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
            
            if (!"succeeded".equals(paymentIntent.getStatus())) {
                PaymentIntentConfirmParams params = PaymentIntentConfirmParams.builder().build();
                paymentIntent = paymentIntent.confirm(params);
            }

            log.info("Confirmed payment intent: {}, status: {}", 
                    paymentIntentId, paymentIntent.getStatus());
            
            // Get the charge ID if available (latest charge)
            String latestChargeId = paymentIntent.getLatestCharge();
            if (latestChargeId != null && !latestChargeId.isEmpty()) {
                return latestChargeId;
            }
            
            return null;
        } catch (StripeException e) {
            log.error("Failed to confirm payment intent: {}", paymentIntentId, e);
            throw new StripeIntegrationException("Failed to confirm payment intent: " + e.getMessage(), e);
        }
    }

    /**
     * Attach a payment method to a customer
     * 
     * @param paymentMethodId Payment method ID
     * @param customerId Customer ID
     * @return Payment method ID
     */
    public String attachPaymentMethod(String paymentMethodId, String customerId) {
        try {
            PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodId);
            
            PaymentMethodAttachParams params = PaymentMethodAttachParams.builder()
                    .setCustomer(customerId)
                    .build();

            paymentMethod.attach(params);
            log.info("Attached payment method: {} to customer: {}", paymentMethodId, customerId);
            return paymentMethodId;
        } catch (StripeException e) {
            log.error("Failed to attach payment method: {} to customer: {}", 
                    paymentMethodId, customerId, e);
            throw new StripeIntegrationException("Failed to attach payment method: " + e.getMessage(), e);
        }
    }

    /**
     * Create a refund for a charge
     * 
     * @param chargeId Charge ID to refund
     * @param amount Amount to refund (null for full refund)
     * @return Refund ID
     */
    public String createRefund(String chargeId, BigDecimal amount) {
        try {
            RefundCreateParams.Builder paramsBuilder = RefundCreateParams.builder()
                    .setCharge(chargeId);

            if (amount != null) {
                // Convert amount to cents
                long amountInCents = amount.multiply(new BigDecimal(100)).longValue();
                paramsBuilder.setAmount(amountInCents);
            }

            Refund refund = Refund.create(paramsBuilder.build());
            log.info("Created refund: {} for charge: {}, amount: {}", 
                    refund.getId(), chargeId, amount);
            return refund.getId();
        } catch (StripeException e) {
            log.error("Failed to create refund for charge: {}", chargeId, e);
            throw new StripeIntegrationException("Failed to create refund: " + e.getMessage(), e);
        }
    }

    /**
     * Handle Stripe webhook event
     * 
     * @param payload Webhook payload
     * @param signature Webhook signature header
     * @return Parsed event
     */
    public Event handleWebhookEvent(String payload, String signature) {
        try {
            Event event = Webhook.constructEvent(payload, signature, webhookSecret);
            log.info("Received Stripe webhook event: {}", event.getType());
            return event;
        } catch (Exception e) {
            log.error("Failed to verify webhook signature", e);
            throw new StripeIntegrationException("Invalid webhook signature: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieve a payment intent
     * 
     * @param paymentIntentId Payment intent ID
     * @return PaymentIntent object
     */
    public PaymentIntent retrievePaymentIntent(String paymentIntentId) {
        try {
            return PaymentIntent.retrieve(paymentIntentId);
        } catch (StripeException e) {
            log.error("Failed to retrieve payment intent: {}", paymentIntentId, e);
            throw new StripeIntegrationException("Failed to retrieve payment intent: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieve a customer
     * 
     * @param customerId Customer ID
     * @return Customer object
     */
    public Customer retrieveCustomer(String customerId) {
        try {
            return Customer.retrieve(customerId);
        } catch (StripeException e) {
            log.error("Failed to retrieve customer: {}", customerId, e);
            throw new StripeIntegrationException("Failed to retrieve customer: " + e.getMessage(), e);
        }
    }
}

