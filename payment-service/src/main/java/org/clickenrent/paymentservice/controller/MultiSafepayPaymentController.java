package org.clickenrent.paymentservice.controller;

import com.google.gson.JsonObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.paymentservice.dto.*;
import org.clickenrent.paymentservice.service.MultiSafepayService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Production controller for MultiSafepay payment operations
 * All endpoints require proper authentication and authorization
 */
@RestController
@RequestMapping("/api/v1/multisafepay")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin Payments - B2B", description = "Administrative operations for B2B partners")
public class MultiSafepayPaymentController {

    private final MultiSafepayService multiSafepayService;

    // === Orders ===

    @GetMapping("/orders/{orderId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN', 'B2B')")
    @Operation(summary = "Get order status")
    public ResponseEntity<Map<String, Object>> getOrder(@PathVariable String orderId) {
        log.info("Getting MultiSafePay order: {}", orderId);
        
        try {
            JsonObject order = multiSafepayService.getOrder(orderId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", order.toString());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to get order: {}", orderId, e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PostMapping("/orders")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN', 'B2B')")
    @Operation(summary = "Create order for web checkout")
    public ResponseEntity<Map<String, Object>> createOrder(
            @Valid @RequestBody WebOrderCreateRequest request) {
        
        log.info("Creating web checkout order: {} {}", request.getAmount(), request.getCurrency());
        
        try {
            // Delegate to service layer for complete order creation
            JsonObject orderResponse = multiSafepayService.createWebOrder(request);
            
            Map<String, Object> response = new HashMap<>();
            
            if (orderResponse != null && orderResponse.has("success") 
                    && orderResponse.get("success").getAsBoolean()) {
                
                JsonObject data = orderResponse.getAsJsonObject("data");
                
                response.put("success", true);
                response.put("orderId", data.get("order_id").getAsString());
                response.put("paymentUrl", data.has("payment_url") 
                    ? data.get("payment_url").getAsString() : null);
                response.put("status", data.has("status") 
                    ? data.get("status").getAsString() : "initialized");
                
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("error", "Failed to create order");
                return ResponseEntity.status(500).body(response);
            }
            
        } catch (Exception e) {
            log.error("Failed to create web order", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PostMapping("/orders/with-splits")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN', 'B2B')")
    @Operation(summary = "Create order with split payments")
    public ResponseEntity<Map<String, Object>> createOrderWithSplits(
            @Valid @RequestBody Map<String, Object> request) {
        
        log.info("Creating MultiSafePay order with splits");
        
        try {
            // Implementation would depend on business requirements
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Split payment creation endpoint ready");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to create order with splits", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/orders/{orderId}/splits")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN', 'B2B')")
    @Operation(summary = "Get split payment details")
    public ResponseEntity<List<SplitPaymentDTO>> getOrderSplits(@PathVariable String orderId) {
        log.info("Getting split payment details for order: {}", orderId);
        
        try {
            // Implementation would query split payments from database or API
            return ResponseEntity.ok(List.of());
            
        } catch (Exception e) {
            log.error("Failed to get split payments for order: {}", orderId, e);
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/orders/{orderId}/capture")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN', 'B2B')")
    @Operation(summary = "Capture authorized payment")
    public ResponseEntity<Map<String, Object>> capturePayment(@PathVariable String orderId) {
        log.info("Capturing payment for order: {}", orderId);
        
        try {
            JsonObject result = multiSafepayService.captureOrder(orderId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("orderId", orderId);
            response.put("data", result.toString());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to capture payment for order: {}", orderId, e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PostMapping("/orders/{orderId}/cancel-authorization")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN', 'B2B')")
    @Operation(summary = "Cancel authorization")
    public ResponseEntity<Map<String, Object>> cancelAuthorization(@PathVariable String orderId) {
        log.info("Cancelling authorization for order: {}", orderId);
        
        try {
            JsonObject result = multiSafepayService.cancelAuthorization(orderId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("orderId", orderId);
            response.put("data", result.toString());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to cancel authorization for order: {}", orderId, e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PostMapping("/orders/{orderId}/extend")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN', 'B2B')")
    @Operation(summary = "Extend order expiration")
    public ResponseEntity<Map<String, Object>> extendExpiration(
            @PathVariable String orderId,
            @RequestParam(defaultValue = "7") int days) {
        
        log.info("Extending expiration for order: {} by {} days", orderId, days);
        
        try {
            JsonObject result = multiSafepayService.extendOrderExpiration(orderId, days);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("orderId", orderId);
            response.put("daysExtended", days);
            response.put("data", result.toString());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to extend expiration for order: {}", orderId, e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    // === Refunds ===

    @PostMapping("/orders/{orderId}/refunds/{refundId}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN', 'B2B')")
    @Operation(summary = "Cancel refund")
    public ResponseEntity<Map<String, Object>> cancelRefund(
            @PathVariable String orderId,
            @PathVariable String refundId) {
        
        log.info("Cancelling refund {} for order: {}", refundId, orderId);
        
        try {
            JsonObject result = multiSafepayService.cancelRefund(orderId, refundId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("orderId", orderId);
            response.put("refundId", refundId);
            response.put("data", result.toString());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to cancel refund for order: {}", orderId, e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PostMapping("/orders/{orderId}/refunds")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN', 'B2B')")
    @Operation(summary = "Create refund for order")
    public ResponseEntity<Map<String, Object>> createRefund(
            @PathVariable String orderId,
            @Valid @RequestBody RefundRequestDTO request) {
        
        log.info("Creating refund for order: {}", orderId);
        
        try {
            String refundId = multiSafepayService.createRefund(
                orderId, 
                request.getAmount(), 
                request.getCurrency(), 
                request.getDescription()
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("orderId", orderId);
            response.put("refundId", refundId);
            response.put("amount", request.getAmount());
            response.put("currency", request.getCurrency());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to create refund for order: {}", orderId, e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    // === Chargebacks ===

    @PostMapping("/orders/{orderId}/chargebacks/challenge")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    @Operation(summary = "Challenge chargeback")
    public ResponseEntity<Map<String, Object>> challengeChargeback(
            @PathVariable String orderId,
            @Valid @RequestBody ChargebackChallengeDTO request) {
        
        log.info("Challenging chargeback for order: {}", orderId);
        
        try {
            JsonObject result = multiSafepayService.challengeChargeback(orderId, request.getReason());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("orderId", orderId);
            response.put("data", result.toString());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to challenge chargeback for order: {}", orderId, e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    // === Tokens ===

    @GetMapping("/tokens")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN', 'B2B', 'CUSTOMER')")
    @Operation(summary = "List payment tokens")
    public ResponseEntity<Map<String, Object>> listTokens(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        
        log.info("Listing payment tokens, page: {}", page);
        
        try {
            JsonObject result = multiSafepayService.listTokens(page, pageSize);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", result.toString());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to list tokens", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/tokens/{tokenId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN', 'B2B', 'CUSTOMER')")
    @Operation(summary = "Get token details")
    public ResponseEntity<Map<String, Object>> getToken(@PathVariable String tokenId) {
        log.info("Getting token details: {}", tokenId);
        
        try {
            JsonObject result = multiSafepayService.getToken(tokenId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", result.toString());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to get token: {}", tokenId, e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PatchMapping("/tokens/{tokenId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN', 'B2B', 'CUSTOMER')")
    @Operation(summary = "Update token")
    public ResponseEntity<Map<String, Object>> updateToken(
            @PathVariable String tokenId,
            @RequestBody Map<String, Object> tokenData) {
        
        log.info("Updating token: {}", tokenId);
        
        try {
            org.clickenrent.paymentservice.client.multisafepay.model.Token token = 
                new org.clickenrent.paymentservice.client.multisafepay.model.Token();
            // Map tokenData to token object
            
            JsonObject result = multiSafepayService.updateToken(tokenId, token);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", result.toString());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to update token: {}", tokenId, e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @DeleteMapping("/tokens/{tokenId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN', 'B2B', 'CUSTOMER')")
    @Operation(summary = "Delete token")
    public ResponseEntity<Void> deleteToken(@PathVariable String tokenId) {
        log.info("Deleting token: {}", tokenId);
        
        try {
            multiSafepayService.deleteToken(tokenId);
            return ResponseEntity.noContent().build();
            
        } catch (Exception e) {
            log.error("Failed to delete token: {}", tokenId, e);
            return ResponseEntity.status(500).build();
        }
    }

    // === Transactions ===

    @GetMapping("/transactions")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN', 'B2B')")
    @Operation(summary = "List transactions")
    public ResponseEntity<Map<String, Object>> listTransactions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        
        log.info("Listing transactions, page: {}", page);
        
        try {
            JsonObject result = multiSafepayService.listTransactions(page, pageSize);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", result.toString());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to list transactions", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/transactions/{transactionId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN', 'B2B')")
    @Operation(summary = "Get transaction")
    public ResponseEntity<Map<String, Object>> getTransaction(@PathVariable String transactionId) {
        log.info("Getting transaction: {}", transactionId);
        
        try {
            JsonObject result = multiSafepayService.getTransaction(transactionId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", result.toString());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to get transaction: {}", transactionId, e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    // === Payment Methods ===

    @GetMapping("/payment-methods")
    @Operation(summary = "List payment methods")
    public ResponseEntity<Map<String, Object>> listPaymentMethods() {
        log.info("Listing payment methods");
        
        try {
            JsonObject result = multiSafepayService.listPaymentMethods();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", result.toString());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to list payment methods", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/payment-methods/{methodCode}")
    @Operation(summary = "Get payment method")
    public ResponseEntity<Map<String, Object>> getPaymentMethod(@PathVariable String methodCode) {
        log.info("Getting payment method: {}", methodCode);
        
        try {
            JsonObject result = multiSafepayService.getPaymentMethod(methodCode);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", result.toString());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to get payment method: {}", methodCode, e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    // === Gateways ===

    @GetMapping("/gateways")
    @Operation(summary = "List gateways")
    public ResponseEntity<Map<String, Object>> listGateways() {
        log.info("Listing gateways");
        
        try {
            JsonObject result = multiSafepayService.listGateways();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", result.toString());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to list gateways", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/gateways/{gatewayId}")
    @Operation(summary = "Get gateway")
    public ResponseEntity<Map<String, Object>> getGateway(@PathVariable String gatewayId) {
        log.info("Getting gateway: {}", gatewayId);
        
        try {
            JsonObject result = multiSafepayService.getGateway(gatewayId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", result.toString());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to get gateway: {}", gatewayId, e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    // === Account Management ===

    @GetMapping("/site-config")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    @Operation(summary = "Get site configuration")
    public ResponseEntity<Map<String, Object>> getSiteConfig() {
        log.info("Getting site configuration");
        
        try {
            JsonObject result = multiSafepayService.getSiteConfiguration();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", result.toString());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to get site configuration", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PatchMapping("/site-config")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    @Operation(summary = "Update site configuration")
    public ResponseEntity<Map<String, Object>> updateSiteConfig(
            @RequestBody Map<String, Object> siteConfigData) {
        
        log.info("Updating site configuration");
        
        try {
            org.clickenrent.paymentservice.client.multisafepay.model.SiteConfig siteConfig = 
                new org.clickenrent.paymentservice.client.multisafepay.model.SiteConfig();
            // Map siteConfigData to siteConfig object
            
            JsonObject result = multiSafepayService.updateSiteConfiguration(siteConfig);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", result.toString());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to update site configuration", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/closing-balances")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    @Operation(summary = "List closing balances")
    public ResponseEntity<Map<String, Object>> getClosingBalances(
            @RequestParam String fromDate,
            @RequestParam String toDate) {
        
        log.info("Getting closing balances from {} to {}", fromDate, toDate);
        
        try {
            JsonObject result = multiSafepayService.getClosingBalances(fromDate, toDate);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", result.toString());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to get closing balances", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    // === Payment Method Specific ===

    @PostMapping("/orders/{orderId}/cancel-bancontact-qr")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN', 'B2B')")
    @Operation(summary = "Cancel Bancontact QR payment")
    public ResponseEntity<Map<String, Object>> cancelBancontactQR(@PathVariable String orderId) {
        log.info("Cancelling Bancontact QR payment for order: {}", orderId);
        
        try {
            JsonObject result = multiSafepayService.cancelBancontactQRPayment(orderId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", result.toString());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to cancel Bancontact QR payment", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PostMapping("/orders/{orderId}/hold")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN', 'B2B')")
    @Operation(summary = "Put PAD order on hold")
    public ResponseEntity<Map<String, Object>> putPADOrderOnHold(@PathVariable String orderId) {
        log.info("Putting PAD order on hold: {}", orderId);
        
        try {
            JsonObject result = multiSafepayService.putPADOrderOnHold(orderId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", result.toString());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to put PAD order on hold", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}
