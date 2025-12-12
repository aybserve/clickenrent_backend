package org.clickenrent.paymentservice.controller;

import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.paymentservice.entity.FinancialTransaction;
import org.clickenrent.paymentservice.entity.PaymentStatus;
import org.clickenrent.paymentservice.repository.FinancialTransactionRepository;
import org.clickenrent.paymentservice.repository.PaymentStatusRepository;
import org.clickenrent.paymentservice.service.StripeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling Stripe webhook events
 */
@RestController
@RequestMapping("/api/webhooks/stripe")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Stripe Webhook", description = "Stripe webhook event handling")
public class StripeWebhookController {

    private final StripeService stripeService;
    private final FinancialTransactionRepository financialTransactionRepository;
    private final PaymentStatusRepository paymentStatusRepository;

    @PostMapping
    @Operation(summary = "Handle Stripe webhook events")
    public ResponseEntity<Void> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String signature) {
        
        try {
            // Verify and parse webhook event
            Event event = stripeService.handleWebhookEvent(payload, signature);
            
            log.info("Processing Stripe webhook event: {}", event.getType());
            
            // Handle different event types
            switch (event.getType()) {
                case "payment_intent.succeeded":
                    handlePaymentIntentSucceeded(event);
                    break;
                    
                case "payment_intent.payment_failed":
                    handlePaymentIntentFailed(event);
                    break;
                    
                case "charge.refunded":
                    handleChargeRefunded(event);
                    break;
                    
                case "payment_method.attached":
                    handlePaymentMethodAttached(event);
                    break;
                    
                default:
                    log.info("Unhandled webhook event type: {}", event.getType());
            }
            
            return ResponseEntity.ok().build();
            
        } catch (Exception e) {
            log.error("Error processing webhook", e);
            return ResponseEntity.badRequest().build();
        }
    }

    private void handlePaymentIntentSucceeded(Event event) {
        try {
            PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer()
                    .getObject()
                    .orElse(null);
            
            if (paymentIntent == null) {
                log.warn("PaymentIntent is null in webhook event");
                return;
            }
            
            // Find transaction by payment intent ID
            financialTransactionRepository.findByStripePaymentIntentId(paymentIntent.getId())
                    .ifPresent(transaction -> {
                        // Update transaction status to SUCCEEDED
                        PaymentStatus succeededStatus = paymentStatusRepository.findByCode("SUCCEEDED")
                                .orElseThrow(() -> new RuntimeException("SUCCEEDED status not found"));
                        
                        transaction.setPaymentStatus(succeededStatus);
                        
                        // Update charge ID if available (latest charge)
                        String latestChargeId = paymentIntent.getLatestCharge();
                        if (latestChargeId != null && !latestChargeId.isEmpty()) {
                            transaction.setStripeChargeId(latestChargeId);
                        }
                        
                        financialTransactionRepository.save(transaction);
                        log.info("Updated transaction {} to SUCCEEDED", transaction.getId());
                    });
            
        } catch (Exception e) {
            log.error("Error handling payment_intent.succeeded", e);
        }
    }

    private void handlePaymentIntentFailed(Event event) {
        try {
            PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer()
                    .getObject()
                    .orElse(null);
            
            if (paymentIntent == null) {
                log.warn("PaymentIntent is null in webhook event");
                return;
            }
            
            // Find transaction by payment intent ID
            financialTransactionRepository.findByStripePaymentIntentId(paymentIntent.getId())
                    .ifPresent(transaction -> {
                        // Update transaction status to FAILED
                        PaymentStatus failedStatus = paymentStatusRepository.findByCode("FAILED")
                                .orElseThrow(() -> new RuntimeException("FAILED status not found"));
                        
                        transaction.setPaymentStatus(failedStatus);
                        financialTransactionRepository.save(transaction);
                        log.info("Updated transaction {} to FAILED", transaction.getId());
                    });
            
        } catch (Exception e) {
            log.error("Error handling payment_intent.payment_failed", e);
        }
    }

    private void handleChargeRefunded(Event event) {
        try {
            // Handle charge refunded event
            log.info("Handling charge.refunded event");
            // TODO: Implement refund handling logic
            
        } catch (Exception e) {
            log.error("Error handling charge.refunded", e);
        }
    }

    private void handlePaymentMethodAttached(Event event) {
        try {
            // Handle payment method attached event
            log.info("Handling payment_method.attached event");
            // TODO: Implement payment method attachment logic
            
        } catch (Exception e) {
            log.error("Error handling payment_method.attached", e);
        }
    }
}
