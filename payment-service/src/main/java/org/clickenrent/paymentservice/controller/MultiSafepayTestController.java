package org.clickenrent.paymentservice.controller;

import com.google.gson.JsonObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.paymentservice.service.MultiSafepayService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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
@Tag(name = "MultiSafePay Testing", description = "Testing endpoints for MultiSafePay integration")
public class MultiSafepayTestController {

    private final MultiSafepayService multiSafepayService;

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
            
            response.put("fullResponse", orderDetails != null ? orderDetails.toString() : "null");
            
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
            
            response.put("fullResponse", orderDetails != null ? orderDetails.toString() : "null");
            
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
            response.put("gateways", gateways.toString());
            
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
            // Try to list gateways - this will verify API key is valid
            JsonObject gateways = multiSafepayService.listGateways();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "✅ Connected to MultiSafePay API successfully!");
            response.put("apiEndpoint", "https://testapi.multisafepay.com");
            response.put("testMode", true);
            
            // Count available gateways
            if (gateways != null && gateways.has("data")) {
                response.put("availableGateways", gateways.getAsJsonArray("data").size());
            }
            
            response.put("note", "If you see this, your API key is valid and working!");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Failed to connect to MultiSafePay API", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            errorResponse.put("message", "❌ Failed to connect. Check your API key!");
            errorResponse.put("troubleshooting", "1. Verify API key in application.properties, 2. Check you're using TEST API key, 3. Verify test.mode=true");
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}
