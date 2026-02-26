package org.clickenrent.paymentservice.controller;

import com.google.gson.JsonObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.paymentservice.entity.PaymentStatus;
import org.clickenrent.paymentservice.repository.FinancialTransactionRepository;
import org.clickenrent.paymentservice.repository.PaymentStatusRepository;
import org.clickenrent.paymentservice.service.MultiSafepayService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling MultiSafePay webhook events
 */
@RestController
@RequestMapping("/api/v1/webhooks/multisafepay")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "MultiSafePay Webhook", description = "MultiSafePay webhook event handling")
public class MultiSafepayWebhookController {

    private final MultiSafepayService multiSafepayService;
    private final FinancialTransactionRepository financialTransactionRepository;
    private final PaymentStatusRepository paymentStatusRepository;

    @PostMapping
    @Operation(
        summary = "Handle MultiSafePay webhook events",
        description = "Receives notifications from MultiSafePay when order status changes. " +
                     "The 'transactionid' parameter contains your order_id (e.g., 'order_1234567890'). " +
                     "Example: POST /api/v1/webhooks/multisafepay?transactionid=order_msp_test_001"
    )
    public ResponseEntity<String> handleWebhook(
            @RequestParam(required = false) String transactionid,  // Despite the name, this is your order_id
            @RequestBody(required = false) String payload,
            @RequestHeader(value = "X-Signature", required = false) String signature) {
        
        try {
            log.info("Received MultiSafePay POST webhook notification");
            
            // MultiSafePay sends 'transactionid' parameter containing your order_id
            if (transactionid != null && !transactionid.isEmpty()) {
                log.info("Processing webhook for order: {}", transactionid);
                
                // Retrieve order details from MultiSafePay
                JsonObject orderResponse = multiSafepayService.getOrder(transactionid);
                
                if (orderResponse != null && orderResponse.has("success") 
                        && orderResponse.get("success").getAsBoolean()) {
                    
                    JsonObject data = orderResponse.getAsJsonObject("data");
                    String orderId = data.get("order_id").getAsString();
                    String status = data.get("status").getAsString();
                    
                    log.info("Order {} status: {}", orderId, status);
                    
                    // Handle different order statuses
                    handleOrderStatus(orderId, status, data);
                    
                    return ResponseEntity.ok("OK");
                } else {
                    log.warn("Failed to retrieve order details for transaction: {}", transactionid);
                    return ResponseEntity.ok("OK"); // Return OK to prevent retries
                }
            }
            
            // If no transactionid parameter, try to process payload
            if (payload != null && !payload.isEmpty()) {
                log.debug("Processing webhook payload: {}", payload);
                multiSafepayService.handleWebhookEvent(payload, signature);
            }
            
            return ResponseEntity.ok("OK");
            
        } catch (Exception e) {
            log.error("Error processing MultiSafePay webhook", e);
            // Return OK to prevent MultiSafePay from retrying
            return ResponseEntity.ok("OK");
        }
    }

    @GetMapping
    @Operation(
        summary = "Handle MultiSafePay webhook GET notifications",
        description = "Receives GET notifications from MultiSafePay when order status changes. " +
                     "The 'transactionid' parameter contains your order_id (e.g., 'order_1234567890'). " +
                     "Example: GET /api/v1/webhooks/multisafepay?transactionid=order_msp_test_001"
    )
    public ResponseEntity<String> handleWebhookGet(
            @RequestParam(required = false) String transactionid) {  // Despite the name, this is your order_id
        
        try {
            log.info("Received MultiSafePay GET webhook notification");
            
            if (transactionid != null && !transactionid.isEmpty()) {
                log.info("Processing GET webhook for order: {}", transactionid);
                
                // Retrieve order details from MultiSafePay
                JsonObject orderResponse = multiSafepayService.getOrder(transactionid);
                
                if (orderResponse != null && orderResponse.has("success") 
                        && orderResponse.get("success").getAsBoolean()) {
                    
                    JsonObject data = orderResponse.getAsJsonObject("data");
                    String orderId = data.get("order_id").getAsString();
                    String status = data.get("status").getAsString();
                    
                    log.info("Order {} status: {}", orderId, status);
                    
                    // Handle different order statuses
                    handleOrderStatus(orderId, status, data);
                }
            }
            
            return ResponseEntity.ok("OK");
            
        } catch (Exception e) {
            log.error("Error processing MultiSafePay GET webhook", e);
            return ResponseEntity.ok("OK");
        }
    }

    /**
     * Handle order status updates
     * 
     * @param orderId Order ID
     * @param status Order status
     * @param data Order data from MultiSafePay
     */
    private void handleOrderStatus(String orderId, String status, JsonObject data) {
        try {
            // Find transaction by MultiSafePay order ID
            financialTransactionRepository.findByMultiSafepayOrderId(orderId)
                    .ifPresent(transaction -> {
                        log.info("Found transaction {} for order {}", transaction.getId(), orderId);
                        
                        // Update transaction based on status
                        switch (status.toLowerCase()) {
                            case "completed":
                                handleOrderCompleted(transaction, data);
                                break;
                                
                            case "expired":
                                handleOrderExpired(transaction);
                                break;
                                
                            case "cancelled":
                            case "void":
                                handleOrderCancelled(transaction);
                                break;
                                
                            case "refunded":
                                handleOrderRefunded(transaction);
                                break;
                                
                            case "partial_refunded":
                                handleOrderPartiallyRefunded(transaction);
                                break;
                                
                            case "initialized":
                            case "uncleared":
                                log.info("Order {} is in pending state: {}", orderId, status);
                                break;
                                
                            default:
                                log.info("Unhandled order status: {} for order: {}", status, orderId);
                        }
                    });
            
            if (financialTransactionRepository.findByMultiSafepayOrderId(orderId).isEmpty()) {
                log.warn("No transaction found for MultiSafePay order: {}", orderId);
            }
            
        } catch (Exception e) {
            log.error("Error handling order status for order: {}", orderId, e);
        }
    }

    private void handleOrderCompleted(org.clickenrent.paymentservice.entity.FinancialTransaction transaction, JsonObject data) {
        try {
            PaymentStatus succeededStatus = paymentStatusRepository.findByCode("SUCCEEDED")
                    .orElseThrow(() -> new RuntimeException("SUCCEEDED status not found"));
            
            transaction.setPaymentStatus(succeededStatus);
            
            // Update transaction ID if available
            if (data.has("transaction_id")) {
                String transactionId = data.get("transaction_id").getAsString();
                transaction.setMultiSafepayTransactionId(transactionId);
            }
            
            financialTransactionRepository.save(transaction);
            log.info("Updated transaction {} to SUCCEEDED", transaction.getId());
            
        } catch (Exception e) {
            log.error("Error handling completed order", e);
        }
    }

    private void handleOrderExpired(org.clickenrent.paymentservice.entity.FinancialTransaction transaction) {
        try {
            PaymentStatus failedStatus = paymentStatusRepository.findByCode("FAILED")
                    .orElseThrow(() -> new RuntimeException("FAILED status not found"));
            
            transaction.setPaymentStatus(failedStatus);
            financialTransactionRepository.save(transaction);
            log.info("Updated transaction {} to FAILED (expired)", transaction.getId());
            
        } catch (Exception e) {
            log.error("Error handling expired order", e);
        }
    }

    private void handleOrderCancelled(org.clickenrent.paymentservice.entity.FinancialTransaction transaction) {
        try {
            PaymentStatus canceledStatus = paymentStatusRepository.findByCode("CANCELED")
                    .orElseThrow(() -> new RuntimeException("CANCELED status not found"));
            
            transaction.setPaymentStatus(canceledStatus);
            financialTransactionRepository.save(transaction);
            log.info("Updated transaction {} to CANCELED", transaction.getId());
            
        } catch (Exception e) {
            log.error("Error handling cancelled order", e);
        }
    }

    private void handleOrderRefunded(org.clickenrent.paymentservice.entity.FinancialTransaction transaction) {
        try {
            PaymentStatus refundedStatus = paymentStatusRepository.findByCode("REFUNDED")
                    .orElseThrow(() -> new RuntimeException("REFUNDED status not found"));
            
            transaction.setPaymentStatus(refundedStatus);
            financialTransactionRepository.save(transaction);
            log.info("Updated transaction {} to REFUNDED", transaction.getId());
            
        } catch (Exception e) {
            log.error("Error handling refunded order", e);
        }
    }

    private void handleOrderPartiallyRefunded(org.clickenrent.paymentservice.entity.FinancialTransaction transaction) {
        try {
            PaymentStatus partiallyRefundedStatus = paymentStatusRepository.findByCode("PARTIALLY_REFUNDED")
                    .orElseThrow(() -> new RuntimeException("PARTIALLY_REFUNDED status not found"));
            
            transaction.setPaymentStatus(partiallyRefundedStatus);
            financialTransactionRepository.save(transaction);
            log.info("Updated transaction {} to PARTIALLY_REFUNDED", transaction.getId());
            
        } catch (Exception e) {
            log.error("Error handling partially refunded order", e);
        }
    }
}
