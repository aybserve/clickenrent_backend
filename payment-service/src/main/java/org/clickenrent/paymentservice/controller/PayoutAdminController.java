package org.clickenrent.paymentservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.paymentservice.entity.B2BRevenueSharePayout;
import org.clickenrent.paymentservice.exception.MultiSafepayIntegrationException;
import org.clickenrent.paymentservice.exception.ResourceNotFoundException;
import org.clickenrent.paymentservice.scheduler.MonthlyPayoutScheduler;
import org.clickenrent.paymentservice.service.PayoutProcessingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Admin controller for payout management
 * Provides administrative functions for payout processing
 */
@RestController
@RequestMapping("/api/v1/admin/payouts")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Payout Admin", description = "Administrative functions for payout management")
@SecurityRequirement(name = "bearerAuth")
public class PayoutAdminController {
    
    private final MonthlyPayoutScheduler monthlyPayoutScheduler;
    private final PayoutProcessingService payoutProcessingService;
    
    @PostMapping("/process")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN', 'SYSTEM')")
    @Operation(
        summary = "Manually trigger payout processing",
        description = "Manually trigger the monthly payout process. " +
                     "This will process all unpaid bike rentals from the previous month. " +
                     "Use for testing or emergency processing outside the scheduled time."
    )
    public ResponseEntity<Map<String, Object>> manuallyProcessPayouts() {
        log.info("Manual payout processing triggered by admin");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            monthlyPayoutScheduler.triggerManualPayout();
            
            response.put("success", true);
            response.put("message", "Payout processing completed successfully");
            
            log.info("Manual payout processing completed successfully");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Manual payout processing failed", e);
            
            response.put("success", false);
            response.put("error", e.getClass().getSimpleName());
            response.put("message", "Payout processing failed: " + e.getMessage());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @GetMapping("/preview")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN', 'SYSTEM')")
    @Operation(
        summary = "Preview what would be paid (dry run)",
        description = "Preview the payout amounts without actually executing payouts. " +
                     "Shows which locations would receive payouts and how much."
    )
    public ResponseEntity<Map<String, Object>> previewPayouts() {
        log.info("Payout preview requested by admin");
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Payout preview not yet implemented");
        response.put("note", "This feature will show payout amounts without executing them");
        
        // TODO: Implement preview functionality
        // This would call a variant of processMonthlyPayouts that doesn't actually send money
        // but returns a summary of what would be paid
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN', 'SYSTEM')")
    @Operation(
        summary = "Get payout system status",
        description = "Get current status of the payout system including scheduling configuration"
    )
    public ResponseEntity<Map<String, Object>> getPayoutSystemStatus() {
        log.debug("Payout system status requested");
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("schedulingEnabled", true); // Could read from config
        response.put("nextScheduledRun", "5th of next month at 02:00 AM");
        response.put("timezone", "Europe/Amsterdam");
        response.put("minPayoutAmount", "10.00 EUR");
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{payoutExternalId}/retry")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN', 'SYSTEM')")
    @Operation(
        summary = "Retry a failed payout",
        description = "Retry a payout that previously failed. " +
                     "This will attempt to send the payout again to MultiSafepay."
    )
    public ResponseEntity<Map<String, Object>> retryFailedPayout(
            @PathVariable String payoutExternalId) {
        log.info("Retry payout requested for: {}", payoutExternalId);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Call service to retry the payout
            B2BRevenueSharePayout payout = payoutProcessingService.retryPayout(payoutExternalId);
            
            // Build success response
            response.put("success", true);
            response.put("message", "Payout retry successful");
            response.put("payoutExternalId", payout.getExternalId());
            response.put("newStatus", payout.getStatus());
            response.put("multiSafepayPayoutId", payout.getMultiSafepayPayoutId());
            response.put("totalAmount", payout.getTotalAmount());
            response.put("currency", payout.getCurrency());
            
            log.info("Payout retry successful for: {}", payoutExternalId);
            return ResponseEntity.ok(response);
            
        } catch (ResourceNotFoundException e) {
            log.warn("Payout not found: {}", payoutExternalId);
            response.put("success", false);
            response.put("error", "NotFound");
            response.put("message", "Payout not found: " + e.getMessage());
            return ResponseEntity.status(404).body(response);
            
        } catch (IllegalStateException e) {
            log.warn("Invalid payout state for retry: {}", e.getMessage());
            response.put("success", false);
            response.put("error", "InvalidState");
            response.put("message", e.getMessage());
            return ResponseEntity.status(400).body(response);
            
        } catch (MultiSafepayIntegrationException e) {
            log.error("MultiSafepay API error during payout retry for: {}", payoutExternalId, e);
            response.put("success", false);
            response.put("error", "MultiSafepayError");
            response.put("message", "MultiSafepay API error: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
            
        } catch (Exception e) {
            log.error("Payout retry failed for: {}", payoutExternalId, e);
            response.put("success", false);
            response.put("error", e.getClass().getSimpleName());
            response.put("message", "Payout retry failed: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @GetMapping("/history")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN', 'SYSTEM')")
    @Operation(
        summary = "Get payout history",
        description = "Get history of all payouts with filtering options"
    )
    public ResponseEntity<Map<String, Object>> getPayoutHistory(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String locationExternalId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.debug("Payout history requested - Status: {}, Location: {}, Page: {}", status, locationExternalId, page);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Payout history not yet implemented");
        response.put("filters", Map.of(
            "status", status != null ? status : "all",
            "locationExternalId", locationExternalId != null ? locationExternalId : "all"
        ));
        response.put("pagination", Map.of(
            "page", page,
            "size", size,
            "totalElements", 0,
            "totalPages", 0
        ));
        response.put("payouts", new java.util.ArrayList<>());
        
        // TODO: Implement payout history retrieval
        // This would query B2BRevenueSharePayout repository with filters and pagination
        
        return ResponseEntity.ok(response);
    }
}
