package org.clickenrent.paymentservice.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.paymentservice.service.MultiSafepayService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

/**
 * Test controller for MultiSafePay integration testing
 * Use this to test MultiSafePay API without full payment flow
 */
@RestController
@RequestMapping("/api/v1/multisafepay/test")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin Payments - B2B (Test)", description = "Testing endpoints for B2B payment operations (NO AUTH)")
public class MultiSafepayTestController {

    private final MultiSafepayService multiSafepayService;
    private final Gson gson = new Gson();

    @PostMapping("/create-order")
    @Operation(summary = "Test: Create a MultiSafePay order and get payment URL")
    public ResponseEntity<Map<String, Object>> testCreateOrder(
            @RequestParam(defaultValue = "10.00") BigDecimal amount,
            @RequestParam(defaultValue = "EUR") String currency,
            @RequestParam(defaultValue = "test@example.com") String customerEmail,
            @RequestParam(defaultValue = "Test Order") String description) {
        
        log.info("Testing MultiSafePay order creation: {} {} for {}", amount, currency, customerEmail);
        
        try {
            // Create order and get full response (includes payment URL)
            JsonObject createResponse = multiSafepayService.createOrderWithResponse(amount, currency, customerEmail, description);
            
            // Extract order ID from create response
            String orderId = null;
            if (createResponse != null && createResponse.has("data")) {
                JsonObject createData = createResponse.getAsJsonObject("data");
                if (createData.has("order_id") && !createData.get("order_id").isJsonNull()) {
                    orderId = createData.get("order_id").getAsString();
                }
            }
            
            // Use create response for payment URL (it's in the create response, not get response)
            JsonObject orderDetails = createResponse;
            
            // Extract payment URL and other details safely
            String paymentUrl = null;
            String qrUrl = null;
            String status = null;
            
            log.debug("Order details response: {}", orderDetails != null ? orderDetails.toString() : "null");
            
            if (orderDetails != null && orderDetails.has("data")) {
                JsonObject data = orderDetails.getAsJsonObject("data");
                
                // Safely extract payment_url
                if (data.has("payment_url") && !data.get("payment_url").isJsonNull()) {
                    paymentUrl = data.get("payment_url").getAsString();
                }
                
                // Safely extract status
                if (data.has("status") && !data.get("status").isJsonNull()) {
                    status = data.get("status").getAsString();
                }
                
                // Try to get QR URL if available (safely)
                try {
                    if (data.has("qr_url") && !data.get("qr_url").isJsonNull()) {
                        qrUrl = data.get("qr_url").getAsString();
                    }
                } catch (Exception e) {
                    log.debug("QR URL not available: {}", e.getMessage());
                }
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("orderId", orderId);
            response.put("paymentUrl", paymentUrl);
            response.put("qrUrl", qrUrl);
            response.put("status", status);
            response.put("amount", amount);
            response.put("currency", currency);
            
            if (paymentUrl != null) {
                response.put("message", "Order created successfully. Open the paymentUrl to complete payment.");
            } else {
                response.put("message", "Order created successfully. Payment URL not available - check fullResponse for details.");
            }
            
            response.put("fullResponse", orderDetails != null ? gson.fromJson(orderDetails.toString(), Object.class) : null);
            
            log.info("✅ Order created: {} | Payment URL: {}", orderId, paymentUrl);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Failed to create MultiSafePay order", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            errorResponse.put("message", "Failed to create order. Check logs for details.");
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Test: Get MultiSafePay order status")
    public ResponseEntity<Map<String, Object>> testGetOrder(@PathVariable String orderId) {
        log.info("Testing MultiSafePay order retrieval: {}", orderId);
        
        try {
            JsonObject orderDetails = multiSafepayService.getOrder(orderId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("orderId", orderId);
            
            if (orderDetails != null && orderDetails.has("data")) {
                JsonObject data = orderDetails.getAsJsonObject("data");
                
                // Safely extract each field
                response.put("status", 
                    data.has("status") && !data.get("status").isJsonNull() 
                        ? data.get("status").getAsString() 
                        : "unknown");
                
                response.put("amount", 
                    data.has("amount") && !data.get("amount").isJsonNull() 
                        ? data.get("amount").getAsInt() / 100.0 
                        : 0);
                
                response.put("currency", 
                    data.has("currency") && !data.get("currency").isJsonNull() 
                        ? data.get("currency").getAsString() 
                        : "");
                
                response.put("paymentUrl", 
                    data.has("payment_url") && !data.get("payment_url").isJsonNull() 
                        ? data.get("payment_url").getAsString() 
                        : null);
            }
            
            response.put("fullResponse", orderDetails != null ? gson.fromJson(orderDetails.toString(), Object.class) : null);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Failed to retrieve MultiSafePay order: {}", orderId, e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("orderId", orderId);
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/gateways")
    @Operation(summary = "Test: List available MultiSafePay payment gateways")
    public ResponseEntity<Map<String, Object>> testListGateways() {
        log.info("Testing MultiSafePay gateways listing");
        
        try {
            JsonObject gateways = multiSafepayService.listGateways();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Available payment gateways retrieved successfully");
            response.put("gateways", gson.fromJson(gateways.toString(), Object.class));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Failed to list MultiSafePay gateways", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PostMapping("/refund/{orderId}")
    @Operation(summary = "Test: Create a refund for an order")
    public ResponseEntity<Map<String, Object>> testCreateRefund(
            @PathVariable String orderId,
            @RequestParam(required = false) BigDecimal amount,
            @RequestParam(defaultValue = "EUR") String currency,
            @RequestParam(defaultValue = "Test refund") String description) {
        
        log.info("Testing MultiSafePay refund creation for order: {}", orderId);
        
        try {
            String refundId = multiSafepayService.createRefund(orderId, amount, currency, description);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("orderId", orderId);
            response.put("refundId", refundId);
            response.put("amount", amount);
            response.put("message", "Refund created successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Failed to create refund for order: {}", orderId, e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("orderId", orderId);
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/raw-order/{orderId}")
    @Operation(summary = "Test: Get raw MultiSafePay order response (for debugging)")
    public ResponseEntity<String> testGetRawOrder(@PathVariable String orderId) {
        log.info("Testing MultiSafePay raw order retrieval: {}", orderId);
        
        try {
            JsonObject orderDetails = multiSafepayService.getOrder(orderId);
            
            // Return raw JSON response for debugging
            return ResponseEntity.ok()
                    .header("Content-Type", "application/json")
                    .body(orderDetails != null ? orderDetails.toString() : "null");
            
        } catch (Exception e) {
            log.error("❌ Failed to retrieve raw MultiSafePay order: {}", orderId, e);
            
            return ResponseEntity.status(500)
                    .header("Content-Type", "application/json")
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/verify-connection")
    @Operation(summary = "Test: Verify MultiSafePay API connection and account")
    public ResponseEntity<Map<String, Object>> testVerifyConnection() {
        log.info("Testing MultiSafePay API connection");
        
        try {
            Map<String, Object> result = multiSafepayService.verifyConnection();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", (Boolean) result.get("connected"));
            response.put("message", (Boolean) result.get("connected") 
                ? "✅ Connected to MultiSafePay API successfully!" 
                : "❌ Failed to connect");
            response.putAll(result);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Failed to connect to MultiSafePay API", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            errorResponse.put("message", "❌ Failed to connect. Check your API key!");
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PostMapping("/create-order-with-splits")
    @Operation(summary = "Test: Create order with split payments")
    public ResponseEntity<Map<String, Object>> testCreateOrderWithSplits(
            @RequestParam(defaultValue = "100.00") BigDecimal amount,
            @RequestParam(defaultValue = "EUR") String currency,
            @RequestParam(defaultValue = "test@example.com") String customerEmail,
            @RequestParam(defaultValue = "Test Order with Splits") String description,
            @RequestBody java.util.List<org.clickenrent.paymentservice.dto.SplitPaymentRequest> splits) {
        
        log.info("Testing MultiSafePay order with splits creation: {} {} with {} splits", amount, currency, splits.size());
        
        try {
            // Validate splits
            if (splits == null || splits.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "At least one split payment is required");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            // Build Affiliate object with split payments
            org.clickenrent.paymentservice.client.multisafepay.model.Affiliate affiliate = 
                new org.clickenrent.paymentservice.client.multisafepay.model.Affiliate();
            affiliate.split_payments = new java.util.ArrayList<>();
            
            int amountInCents = amount.multiply(new BigDecimal(100)).intValue();
            int totalSplitAmount = 0;
            
            for (org.clickenrent.paymentservice.dto.SplitPaymentRequest split : splits) {
                org.clickenrent.paymentservice.client.multisafepay.model.SplitPayments sp = 
                    new org.clickenrent.paymentservice.client.multisafepay.model.SplitPayments();
                sp.merchant = split.getMerchantId();
                
                // Calculate split amount (either percentage or fixed amount)
                if (split.getFixedAmountCents() != null && split.getFixedAmountCents() > 0) {
                    sp.fixed = split.getFixedAmountCents();
                    totalSplitAmount += split.getFixedAmountCents();
                } else if (split.getPercentage() != null && split.getPercentage().compareTo(BigDecimal.ZERO) > 0) {
                    int splitAmount = split.getPercentage()
                        .multiply(new BigDecimal(amountInCents))
                        .divide(new BigDecimal(100), RoundingMode.HALF_UP)
                        .intValue();
                    sp.fixed = splitAmount;
                    totalSplitAmount += splitAmount;
                } else {
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("success", false);
                    errorResponse.put("error", "Each split must have either percentage or fixedAmountCents specified");
                    return ResponseEntity.badRequest().body(errorResponse);
                }
                
                sp.description = split.getDescription() != null ? split.getDescription() : "Split payment";
                affiliate.split_payments.add(sp);
            }
            
            // Validate total split amount doesn't exceed order amount
            if (totalSplitAmount > amountInCents) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "Total split amount (" + totalSplitAmount + " cents) exceeds order amount (" + amountInCents + " cents)");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            // Create order with splits using the service
            JsonObject createResponse = multiSafepayService.createRedirectOrderWithSplits(
                amount, currency, customerEmail, description, affiliate);
            
            // Extract order details
            String orderId = null;
            String paymentUrl = null;
            String status = null;
            
            if (createResponse != null && createResponse.has("data")) {
                JsonObject data = createResponse.getAsJsonObject("data");
                
                if (data.has("order_id") && !data.get("order_id").isJsonNull()) {
                    orderId = data.get("order_id").getAsString();
                }
                
                if (data.has("payment_url") && !data.get("payment_url").isJsonNull()) {
                    paymentUrl = data.get("payment_url").getAsString();
                }
                
                if (data.has("status") && !data.get("status").isJsonNull()) {
                    status = data.get("status").getAsString();
                }
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("orderId", orderId);
            response.put("paymentUrl", paymentUrl);
            response.put("status", status);
            response.put("amount", amount);
            response.put("currency", currency);
            response.put("splitsCount", splits.size());
            response.put("totalSplitAmount", totalSplitAmount / 100.0);
            response.put("remainingAmount", (amountInCents - totalSplitAmount) / 100.0);
            response.put("message", "Order with split payments created successfully. Open the paymentUrl to complete payment.");
            response.put("fullResponse", createResponse != null ? gson.fromJson(createResponse.toString(), Object.class) : null);
            
            log.info("✅ Order with splits created: {} | Payment URL: {} | Splits: {}", orderId, paymentUrl, splits.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Failed to create order with splits", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            errorResponse.put("message", "Failed to create order with splits. Check logs for details.");
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PostMapping("/capture/{orderId}")
    @Operation(summary = "Test: Capture authorized payment")
    public ResponseEntity<Map<String, Object>> testCapture(@PathVariable String orderId) {
        log.info("Testing MultiSafePay capture for order: {}", orderId);
        
        try {
            JsonObject result = multiSafepayService.captureOrder(orderId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("orderId", orderId);
            response.put("message", "Payment captured successfully");
            response.put("fullResponse", gson.fromJson(result.toString(), Object.class));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Failed to capture payment", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PostMapping("/cancel-authorization/{orderId}")
    @Operation(summary = "Test: Cancel authorized payment")
    public ResponseEntity<Map<String, Object>> testCancelAuth(@PathVariable String orderId) {
        log.info("Testing MultiSafePay cancel authorization for order: {}", orderId);
        
        try {
            JsonObject result = multiSafepayService.cancelAuthorization(orderId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("orderId", orderId);
            response.put("message", "Authorization cancelled successfully");
            response.put("fullResponse", gson.fromJson(result.toString(), Object.class));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Failed to cancel authorization", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PostMapping("/extend-expiration/{orderId}")
    @Operation(summary = "Test: Extend order expiration")
    public ResponseEntity<Map<String, Object>> testExtendExpiration(
            @PathVariable String orderId,
            @RequestParam(defaultValue = "7") int days) {
        
        log.info("Testing MultiSafePay extend expiration for order: {} by {} days", orderId, days);
        
        try {
            JsonObject result = multiSafepayService.extendOrderExpiration(orderId, days);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("orderId", orderId);
            response.put("daysExtended", days);
            response.put("message", "Expiration extended successfully");
            response.put("fullResponse", gson.fromJson(result.toString(), Object.class));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Failed to extend expiration", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PostMapping("/cancel-refund/{orderId}/{refundId}")
    @Operation(summary = "Test: Cancel a pending refund")
    public ResponseEntity<Map<String, Object>> testCancelRefund(
            @PathVariable String orderId,
            @PathVariable String refundId) {
        
        log.info("Testing MultiSafePay cancel refund: {} for order: {}", refundId, orderId);
        
        try {
            JsonObject result = multiSafepayService.cancelRefund(orderId, refundId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("orderId", orderId);
            response.put("refundId", refundId);
            response.put("message", "Refund cancelled successfully");
            response.put("fullResponse", gson.fromJson(result.toString(), Object.class));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Failed to cancel refund", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PostMapping("/challenge-chargeback/{orderId}")
    @Operation(summary = "Test: Challenge a chargeback")
    public ResponseEntity<Map<String, Object>> testChallengeChargeback(
            @PathVariable String orderId,
            @RequestParam(defaultValue = "Customer provided proof of delivery") String reason) {
        
        log.info("Testing MultiSafePay challenge chargeback for order: {}", orderId);
        
        try {
            JsonObject result = multiSafepayService.challengeChargeback(orderId, reason);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("orderId", orderId);
            response.put("reason", reason);
            response.put("message", "Chargeback challenged successfully");
            response.put("fullResponse", gson.fromJson(result.toString(), Object.class));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Failed to challenge chargeback", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/tokens")
    @Operation(summary = "Test: List payment tokens")
    public ResponseEntity<Map<String, Object>> testListTokens(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        
        log.info("Testing MultiSafePay list tokens");
        
        try {
            JsonObject result = multiSafepayService.listTokens(page, pageSize);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("page", page);
            response.put("pageSize", pageSize);
            response.put("fullResponse", gson.fromJson(result.toString(), Object.class));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Failed to list tokens", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/tokens/{tokenId}")
    @Operation(summary = "Test: Get token details")
    public ResponseEntity<Map<String, Object>> testGetToken(@PathVariable String tokenId) {
        log.info("Testing MultiSafePay get token: {}", tokenId);
        
        try {
            JsonObject result = multiSafepayService.getToken(tokenId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("tokenId", tokenId);
            response.put("fullResponse", gson.fromJson(result.toString(), Object.class));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Failed to get token", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PatchMapping("/tokens/{tokenId}")
    @Operation(summary = "Test: Update a token")
    public ResponseEntity<Map<String, Object>> testUpdateToken(
            @PathVariable String tokenId,
            @RequestBody Map<String, Object> tokenData) {
        
        log.info("Testing MultiSafePay update token: {}", tokenId);
        
        try {
            org.clickenrent.paymentservice.client.multisafepay.model.Token token = 
                new org.clickenrent.paymentservice.client.multisafepay.model.Token();
            // Map the tokenData to token object as needed
            
            JsonObject result = multiSafepayService.updateToken(tokenId, token);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("tokenId", tokenId);
            response.put("message", "Token updated successfully");
            response.put("fullResponse", gson.fromJson(result.toString(), Object.class));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Failed to update token", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @DeleteMapping("/tokens/{tokenId}")
    @Operation(summary = "Test: Delete a token")
    public ResponseEntity<Map<String, Object>> testDeleteToken(@PathVariable String tokenId) {
        log.info("Testing MultiSafePay delete token: {}", tokenId);
        
        try {
            JsonObject result = multiSafepayService.deleteToken(tokenId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("tokenId", tokenId);
            response.put("message", "Token deleted successfully");
            response.put("fullResponse", gson.fromJson(result.toString(), Object.class));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Failed to delete token", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/transactions")
    @Operation(summary = "Test: List transactions with pagination")
    public ResponseEntity<Map<String, Object>> testListTransactions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        
        log.info("Testing MultiSafePay list transactions");
        
        try {
            JsonObject result = multiSafepayService.listTransactions(page, pageSize);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("page", page);
            response.put("pageSize", pageSize);
            response.put("fullResponse", gson.fromJson(result.toString(), Object.class));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Failed to list transactions", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/payment-methods")
    @Operation(summary = "Test: List available payment methods")
    public ResponseEntity<Map<String, Object>> testListPaymentMethods() {
        log.info("Testing MultiSafePay list payment methods");
        
        try {
            JsonObject result = multiSafepayService.listPaymentMethods();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Payment methods retrieved successfully");
            response.put("fullResponse", gson.fromJson(result.toString(), Object.class));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Failed to list payment methods", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/payment-methods/{methodCode}")
    @Operation(summary = "Test: Get payment method details")
    public ResponseEntity<Map<String, Object>> testGetPaymentMethod(@PathVariable String methodCode) {
        log.info("Testing MultiSafePay get payment method: {}", methodCode);
        
        try {
            JsonObject result = multiSafepayService.getPaymentMethod(methodCode);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("methodCode", methodCode);
            response.put("fullResponse", gson.fromJson(result.toString(), Object.class));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Failed to get payment method", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/site-config")
    @Operation(summary = "Test: Get site configuration")
    public ResponseEntity<Map<String, Object>> testGetSiteConfig() {
        log.info("Testing MultiSafePay get site configuration");
        
        try {
            JsonObject result = multiSafepayService.getSiteConfiguration();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Site configuration retrieved successfully");
            response.put("fullResponse", gson.fromJson(result.toString(), Object.class));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Failed to get site configuration", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PatchMapping("/site-config")
    @Operation(summary = "Test: Update site configuration")
    public ResponseEntity<Map<String, Object>> testUpdateSiteConfig(
            @RequestBody Map<String, Object> siteConfigData) {
        
        log.info("Testing MultiSafePay update site configuration");
        
        try {
            org.clickenrent.paymentservice.client.multisafepay.model.SiteConfig siteConfig = 
                new org.clickenrent.paymentservice.client.multisafepay.model.SiteConfig();
            // Map siteConfigData to siteConfig object
            
            JsonObject result = multiSafepayService.updateSiteConfiguration(siteConfig);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Site configuration updated successfully");
            response.put("fullResponse", gson.fromJson(result.toString(), Object.class));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Failed to update site configuration", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PostMapping("/cancel-bancontact-qr/{orderId}")
    @Operation(summary = "Test: Cancel Bancontact QR payment")
    public ResponseEntity<Map<String, Object>> testCancelBancontactQR(@PathVariable String orderId) {
        log.info("Testing MultiSafePay cancel Bancontact QR: {}", orderId);
        
        try {
            JsonObject result = multiSafepayService.cancelBancontactQRPayment(orderId);
            
            Map<String, Object> response = new HashMap<>();
            
            if (result != null) {
                response.put("success", true);
                response.put("orderId", orderId);
                response.put("message", "Bancontact QR payment cancelled successfully");
                response.put("fullResponse", gson.fromJson(result.toString(), Object.class));
            } else {
                response.put("success", false);
                response.put("orderId", orderId);
                response.put("message", "No response from MultiSafePay API");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Failed to cancel Bancontact QR", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PostMapping("/pad-hold/{orderId}")
    @Operation(summary = "Test: Put PAD order on hold")
    public ResponseEntity<Map<String, Object>> testPutPADOnHold(@PathVariable String orderId) {
        log.info("Testing MultiSafePay put PAD order on hold: {}", orderId);
        
        try {
            JsonObject result = multiSafepayService.putPADOrderOnHold(orderId);
            
            Map<String, Object> response = new HashMap<>();
            
            if (result != null) {
                response.put("success", true);
                response.put("orderId", orderId);
                response.put("message", "PAD order put on hold successfully");
                response.put("fullResponse", gson.fromJson(result.toString(), Object.class));
            } else {
                response.put("success", false);
                response.put("orderId", orderId);
                response.put("message", "No response from MultiSafePay API");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Failed to put PAD order on hold", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/closing-balances")
    @Operation(summary = "Test: List closing balances for reconciliation")
    public ResponseEntity<Map<String, Object>> testGetClosingBalances(
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate) {
        
        log.info("Testing MultiSafePay get closing balances");
        
        // Default to last 30 days if not specified
        if (fromDate == null || toDate == null) {
            java.time.LocalDate now = java.time.LocalDate.now();
            java.time.LocalDate monthAgo = now.minusDays(30);
            fromDate = monthAgo.toString();
            toDate = now.toString();
        }
        
        try {
            JsonObject result = multiSafepayService.getClosingBalances(fromDate, toDate);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("fromDate", fromDate);
            response.put("toDate", toDate);
            response.put("fullResponse", gson.fromJson(result.toString(), Object.class));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Failed to get closing balances", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }


    @PostMapping("/verify-webhook-signature")
    @Operation(summary = "Test: Verify webhook signature")
    public ResponseEntity<Map<String, Object>> testVerifySignature(
            @RequestBody String payload,
            @RequestParam String signature) {
        
        log.info("Testing MultiSafePay webhook signature verification");
        
        try {
            boolean valid = multiSafepayService.verifyWebhookSignature(payload, signature);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("signatureValid", valid);
            response.put("message", valid ? "Signature is valid" : "Signature is invalid");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Failed to verify signature", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}
