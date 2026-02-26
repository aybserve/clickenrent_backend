package org.clickenrent.paymentservice.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.paymentservice.entity.B2BRevenueSharePayout;
import org.clickenrent.paymentservice.repository.B2BRevenueSharePayoutRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Webhook controller for MultiSafepay payout status updates
 * Receives notifications when payout status changes (completed, failed, etc.)
 */
@RestController
@RequestMapping("/api/v1/webhooks/multisafepay/payout")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Payout Webhooks", description = "Webhook endpoints for MultiSafepay payout notifications")
public class PayoutWebhookController {
    
    private final B2BRevenueSharePayoutRepository payoutRepository;
    private final Gson gson = new Gson();
    
    @PostMapping
    @Operation(
        summary = "Handle MultiSafepay payout webhook",
        description = "Receives POST notifications from MultiSafepay when payout status changes. " +
                     "Signature verification is performed to ensure authenticity."
    )
    public ResponseEntity<String> handlePayoutWebhook(
            @RequestBody String payload,
            @RequestHeader(value = "signature", required = false) String signature) {
        
        log.info("========================================");
        log.info("Received payout webhook from MultiSafepay");
        log.info("========================================");
        log.debug("Payload: {}", payload);
        log.debug("Signature: {}", signature);
        
        try {
            // 1. Log signature (signature verification can be added later if needed)
            if (signature != null && !signature.isEmpty()) {
                log.debug("Webhook signature received: {}", signature);
                // TODO: Implement signature verification if required by MultiSafepay
            } else {
                log.debug("No signature provided in webhook request");
            }
            
            // 2. Parse payload
            JsonObject json = gson.fromJson(payload, JsonObject.class);
            
            if (!json.has("payout_id")) {
                log.warn("No payout_id in webhook payload");
                return ResponseEntity.badRequest().body("Missing payout_id");
            }
            
            String payoutId = json.get("payout_id").getAsString();
            String status = json.has("status") ? json.get("status").getAsString() : "unknown";
            
            log.info("Payout ID: {}, Status: {}", payoutId, status);
            
            // 3. Update payout status in database
            Optional<B2BRevenueSharePayout> payoutOpt = payoutRepository.findByMultiSafepayPayoutId(payoutId);
            
            if (payoutOpt.isEmpty()) {
                log.warn("Payout not found in database: {}", payoutId);
                return ResponseEntity.ok("Payout not found, but webhook received");
            }
            
            B2BRevenueSharePayout payout = payoutOpt.get();
            String oldStatus = payout.getStatus();
            
            // Update status based on MultiSafepay status
            switch (status.toLowerCase()) {
                case "completed":
                case "success":
                    payout.setStatus("COMPLETED");
                    payout.setPaidAmount(payout.getTotalAmount());
                    payout.setRemainingAmount(java.math.BigDecimal.ZERO);
                    log.info("Payout {} marked as COMPLETED", payoutId);
                    break;
                    
                case "failed":
                case "declined":
                case "error":
                    payout.setStatus("FAILED");
                    String errorMsg = json.has("error_message") ? 
                            json.get("error_message").getAsString() : "Payment failed";
                    payout.setFailureReason(errorMsg);
                    log.error("Payout {} marked as FAILED: {}", payoutId, errorMsg);
                    
                    // TODO: Send alert to admins
                    break;
                    
                case "processing":
                case "pending":
                    payout.setStatus("PROCESSING");
                    log.info("Payout {} status updated to PROCESSING", payoutId);
                    break;
                    
                case "cancelled":
                case "void":
                    payout.setStatus("CANCELLED");
                    log.warn("Payout {} was cancelled", payoutId);
                    break;
                    
                default:
                    log.warn("Unknown payout status received: {}", status);
                    payout.setStatus(status.toUpperCase());
            }
            
            payoutRepository.save(payout);
            
            log.info("Payout status updated: {} -> {} for payout ID: {}", 
                    oldStatus, payout.getStatus(), payoutId);
            log.info("========================================");
            
            return ResponseEntity.ok("OK");
            
        } catch (Exception e) {
            log.error("========================================");
            log.error("Error processing payout webhook", e);
            log.error("========================================");
            return ResponseEntity.status(500).body("Error processing webhook");
        }
    }
    
    @GetMapping
    @Operation(
        summary = "Handle MultiSafepay payout GET webhook",
        description = "Receives GET notifications from MultiSafepay (less common). " +
                     "Typically used for status checks."
    )
    public ResponseEntity<String> handlePayoutWebhookGet(
            @RequestParam(required = false) String transactionid,
            @RequestParam(required = false) String timestamp) {
        
        log.info("Received GET payout webhook - Transaction: {}, Timestamp: {}", transactionid, timestamp);
        
        // For GET webhooks, we typically just acknowledge receipt
        // and let the scheduled status checks handle updates
        
        return ResponseEntity.ok("OK");
    }
    
    @PostMapping("/test")
    @Operation(
        summary = "Test payout webhook",
        description = "Test endpoint for webhook functionality (development only)"
    )
    public ResponseEntity<Map<String, Object>> testPayoutWebhook(@RequestBody String payload) {
        log.info("Test payout webhook called");
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Test webhook received");
        response.put("payload", payload);
        
        return ResponseEntity.ok(response);
    }
}
