package org.clickenrent.paymentservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.paymentservice.dto.mobile.*;
import org.clickenrent.paymentservice.service.MobilePaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test controller for mobile payment integration
 * NO AUTHENTICATION REQUIRED - For development and testing only
 * Should be disabled in production environments
 */
@RestController
@RequestMapping("/api/v1/payments/mobile/test")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Mobile Payments - Customer (Test)", description = "Testing endpoints for mobile payment integration (NO AUTH)")
public class MobilePaymentTestController {

    private final MobilePaymentService mobilePaymentService;

    @GetMapping("/methods")
    @Operation(
        summary = "Test: List available payment methods",
        description = "Returns a list of payment methods optimized for mobile display. " +
                     "No authentication required for testing."
    )
    public ResponseEntity<Map<String, Object>> testGetPaymentMethods() {
        log.info("🧪 TEST: Fetching available payment methods for mobile");
        
        try {
            List<MobilePaymentMethodDTO> methods = mobilePaymentService.getAvailablePaymentMethods();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", methods.size());
            response.put("methods", methods);
            response.put("message", "✅ Successfully retrieved " + methods.size() + " payment methods");
            
            log.info("✅ TEST: Successfully retrieved {} payment methods", methods.size());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ TEST: Failed to get payment methods", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getClass().getSimpleName());
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/ideal/banks")
    @Operation(
        summary = "Test: Get iDEAL bank list",
        description = "Returns list of banks available for iDEAL payments. " +
                     "No authentication required for testing."
    )
    public ResponseEntity<Map<String, Object>> testGetIdealBanks() {
        log.info("🧪 TEST: Fetching iDEAL bank list");
        
        try {
            List<MobileBankDTO> banks = mobilePaymentService.getIdealBanks();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", banks.size());
            response.put("banks", banks);
            response.put("message", "✅ Successfully retrieved " + banks.size() + " iDEAL banks");
            
            log.info("✅ TEST: Successfully retrieved {} iDEAL banks", banks.size());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ TEST: Failed to get iDEAL banks", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getClass().getSimpleName());
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PostMapping("/direct")
    @Operation(
        summary = "Test: Create direct payment",
        description = "Creates a direct payment order (iDEAL, DirectBank). " +
                     "No authentication required for testing. " +
                     "Use test user ID: 'test-user-mobile-123'"
    )
    public ResponseEntity<Map<String, Object>> testCreateDirectPayment(
            @Valid @RequestBody MobilePaymentRequestDTO request) {
        
        String testUserId = "test-user-mobile-123";
        log.info("🧪 TEST: Creating direct payment for test user with method: {}", request.getPaymentMethodCode());
        log.info("🧪 TEST: Request details - Amount: {} {}, Email: {}", 
            request.getAmount(), request.getCurrency(), request.getCustomerEmail());
        
        try {
            MobilePaymentResponseDTO paymentResponse = mobilePaymentService.createDirectPayment(request, testUserId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("payment", paymentResponse);
            response.put("testUserId", testUserId);
            response.put("message", "✅ Direct payment created successfully");
            response.put("instructions", 
                "Open the transactionUrl in a WebView. " +
                "The user will authenticate with their bank. " +
                "After authentication, the bank will redirect back to the redirectUrl. " +
                "Detect the redirect and close the WebView. " +
                "The backend will receive a webhook notification when payment is complete.");
            
            log.info("✅ TEST: Direct payment created successfully");
            log.info("📱 Order ID: {}", paymentResponse.getOrderId());
            log.info("🔗 Transaction URL: {}", paymentResponse.getTransactionUrl());
            log.info("🎯 Flow Type: {}", paymentResponse.getFlowType());
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.error("❌ TEST: Invalid direct payment request: {}", e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "ValidationError");
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(400).body(errorResponse);
            
        } catch (Exception e) {
            log.error("❌ TEST: Failed to create direct payment", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getClass().getSimpleName());
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PostMapping("/redirect")
    @Operation(
        summary = "Test: Create redirect payment",
        description = "Creates a redirect payment order (PayPal, Credit Card, etc.). " +
                     "No authentication required for testing. " +
                     "Use test user ID: 'test-user-mobile-123'"
    )
    public ResponseEntity<Map<String, Object>> testCreateRedirectPayment(
            @Valid @RequestBody MobilePaymentRequestDTO request) {
        
        String testUserId = "test-user-mobile-123";
        log.info("🧪 TEST: Creating redirect payment for test user");
        log.info("🧪 TEST: Request details - Amount: {} {}, Email: {}", 
            request.getAmount(), request.getCurrency(), request.getCustomerEmail());
        
        try {
            MobilePaymentResponseDTO paymentResponse = mobilePaymentService.createRedirectPayment(request, testUserId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("payment", paymentResponse);
            response.put("testUserId", testUserId);
            response.put("message", "✅ Redirect payment created successfully");
            response.put("instructions", 
                "Open the paymentUrl in a full WebView. " +
                "The user will see the MultiSafePay payment page and select their payment method. " +
                "After completing payment, MultiSafePay will redirect to the redirectUrl. " +
                "Detect the redirect and close the WebView. " +
                "The backend will receive a webhook notification when payment is complete.");
            
            log.info("✅ TEST: Redirect payment created successfully");
            log.info("📱 Order ID: {}", paymentResponse.getOrderId());
            log.info("🔗 Payment URL: {}", paymentResponse.getPaymentUrl());
            log.info("🎯 Flow Type: {}", paymentResponse.getFlowType());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ TEST: Failed to create redirect payment", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getClass().getSimpleName());
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/status/{orderId}")
    @Operation(
        summary = "Test: Get payment status",
        description = "Retrieves the current status of a payment order. " +
                     "No authentication required for testing."
    )
    public ResponseEntity<Map<String, Object>> testGetPaymentStatus(@PathVariable String orderId) {
        log.info("🧪 TEST: Getting payment status for order: {}", orderId);
        
        try {
            MobilePaymentResponseDTO statusResponse = mobilePaymentService.getPaymentStatus(orderId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("status", statusResponse);
            response.put("message", "✅ Payment status retrieved successfully");
            
            // Add status explanation
            String statusExplanation = getStatusExplanation(statusResponse.getStatus());
            response.put("statusExplanation", statusExplanation);
            
            log.info("✅ TEST: Payment status retrieved successfully");
            log.info("📊 Status: {}", statusResponse.getStatus());
            log.info("💰 Financial Status: {}", statusResponse.getFinancialStatus());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ TEST: Failed to get payment status for order: {}", orderId, e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getClass().getSimpleName());
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PostMapping("/direct/ideal")
    @Operation(
        summary = "Test: Quick iDEAL payment",
        description = "Quick test endpoint for iDEAL payment with default test data. " +
                     "Provide only amount and issuer ID."
    )
    public ResponseEntity<Map<String, Object>> testQuickIdealPayment(
            @RequestParam(defaultValue = "10.00") String amount,
            @RequestParam(defaultValue = "0031") String issuerId,
            @RequestParam(defaultValue = "EUR") String currency) {
        
        log.info("🧪 TEST: Quick iDEAL payment - Amount: {} {}, Issuer: {}", amount, currency, issuerId);
        
        try {
            MobilePaymentRequestDTO request = MobilePaymentRequestDTO.builder()
                .amount(new java.math.BigDecimal(amount))
                .currency(currency)
                .customerEmail("test@example.com")
                .description("Test iDEAL Payment")
                .paymentMethodCode("IDEAL")
                .issuerId(issuerId)
                .build();
            
            return testCreateDirectPayment(request);
            
        } catch (Exception e) {
            log.error("❌ TEST: Failed to create quick iDEAL payment", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getClass().getSimpleName());
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PostMapping("/redirect/quick")
    @Operation(
        summary = "Test: Quick redirect payment",
        description = "Quick test endpoint for redirect payment with default test data. " +
                     "Provide only amount."
    )
    public ResponseEntity<Map<String, Object>> testQuickRedirectPayment(
            @RequestParam(defaultValue = "10.00") String amount,
            @RequestParam(defaultValue = "EUR") String currency) {
        
        log.info("🧪 TEST: Quick redirect payment - Amount: {} {}", amount, currency);
        
        try {
            MobilePaymentRequestDTO request = MobilePaymentRequestDTO.builder()
                .amount(new java.math.BigDecimal(amount))
                .currency(currency)
                .customerEmail("test@example.com")
                .description("Test Redirect Payment")
                .build();
            
            return testCreateRedirectPayment(request);
            
        } catch (Exception e) {
            log.error("❌ TEST: Failed to create quick redirect payment", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getClass().getSimpleName());
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PostMapping("/direct/splits")
    @Operation(
        summary = "Test: Direct payment with split payments",
        description = "Test direct iDEAL payment with revenue sharing (split payments). " +
                     "Requires merchant IDs from your MultiSafePay account configuration."
    )
    public ResponseEntity<Map<String, Object>> testDirectPaymentWithSplits(
            @Valid @RequestBody MobilePaymentRequestDTO request) {
        
        log.info("🧪 TEST: Creating direct payment with splits");
        log.info("🧪 TEST: Amount: {} {}, Method: {}, Splits: {}", 
            request.getAmount(), request.getCurrency(), 
            request.getPaymentMethodCode(),
            request.getSplits() != null ? request.getSplits().size() : 0);
        
        return testCreateDirectPayment(request);
    }

    @PostMapping("/redirect/splits")
    @Operation(
        summary = "Test: Redirect payment with split payments",
        description = "Test redirect payment with revenue sharing (split payments). " +
                     "Requires merchant IDs from your MultiSafePay account configuration."
    )
    public ResponseEntity<Map<String, Object>> testRedirectPaymentWithSplits(
            @Valid @RequestBody MobilePaymentRequestDTO request) {
        
        log.info("🧪 TEST: Creating redirect payment with splits");
        log.info("🧪 TEST: Amount: {} {}, Splits: {}", 
            request.getAmount(), request.getCurrency(),
            request.getSplits() != null ? request.getSplits().size() : 0);
        
        return testCreateRedirectPayment(request);
    }

    @PostMapping("/direct/ideal/splits")
    @Operation(
        summary = "Test: Quick iDEAL payment with splits",
        description = "Quick test for iDEAL payment with 70/30 split. " +
                     "Provide amount, issuer, and partner merchant ID."
    )
    public ResponseEntity<Map<String, Object>> testQuickIdealWithSplits(
            @RequestParam(defaultValue = "25.00") String amount,
            @RequestParam(defaultValue = "0031") String issuerId,
            @RequestParam(required = true) String partnerMerchantId,
            @RequestParam(defaultValue = "30") String partnerPercentage,
            @RequestParam(defaultValue = "EUR") String currency) {
        
        log.info("🧪 TEST: Quick iDEAL with splits - Amount: {} {}, Partner: {} ({}%)", 
            amount, currency, partnerMerchantId, partnerPercentage);
        
        try {
            java.util.List<org.clickenrent.paymentservice.dto.SplitPaymentDTO> splits = new java.util.ArrayList<>();
            splits.add(org.clickenrent.paymentservice.dto.SplitPaymentDTO.builder()
                .merchantId(partnerMerchantId)
                .percentage(new java.math.BigDecimal(partnerPercentage))
                .description("Partner commission - " + partnerPercentage + "%")
                .build());
            
            MobilePaymentRequestDTO request = MobilePaymentRequestDTO.builder()
                .amount(new java.math.BigDecimal(amount))
                .currency(currency)
                .customerEmail("test@example.com")
                .description("Test iDEAL Payment with Split")
                .paymentMethodCode("IDEAL")
                .issuerId(issuerId)
                .splits(splits)
                .build();
            
            return testCreateDirectPayment(request);
            
        } catch (Exception e) {
            log.error("❌ TEST: Failed to create quick iDEAL payment with splits", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getClass().getSimpleName());
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PostMapping("/redirect/quick/splits")
    @Operation(
        summary = "Test: Quick redirect payment with splits",
        description = "Quick test for redirect payment with 70/30 split. " +
                     "Provide amount and partner merchant ID."
    )
    public ResponseEntity<Map<String, Object>> testQuickRedirectWithSplits(
            @RequestParam(defaultValue = "25.00") String amount,
            @RequestParam(required = true) String partnerMerchantId,
            @RequestParam(defaultValue = "30") String partnerPercentage,
            @RequestParam(defaultValue = "EUR") String currency) {
        
        log.info("🧪 TEST: Quick redirect with splits - Amount: {} {}, Partner: {} ({}%)", 
            amount, currency, partnerMerchantId, partnerPercentage);
        
        try {
            java.util.List<org.clickenrent.paymentservice.dto.SplitPaymentDTO> splits = new java.util.ArrayList<>();
            splits.add(org.clickenrent.paymentservice.dto.SplitPaymentDTO.builder()
                .merchantId(partnerMerchantId)
                .percentage(new java.math.BigDecimal(partnerPercentage))
                .description("Partner commission - " + partnerPercentage + "%")
                .build());
            
            MobilePaymentRequestDTO request = MobilePaymentRequestDTO.builder()
                .amount(new java.math.BigDecimal(amount))
                .currency(currency)
                .customerEmail("test@example.com")
                .description("Test Redirect Payment with Split")
                .splits(splits)
                .build();
            
            return testCreateRedirectPayment(request);
            
        } catch (Exception e) {
            log.error("❌ TEST: Failed to create quick redirect payment with splits", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getClass().getSimpleName());
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/health")
    @Operation(
        summary = "Test: Health check",
        description = "Simple health check for mobile payment test endpoints"
    )
    public ResponseEntity<Map<String, Object>> testHealthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("service", "Mobile Payment Test Controller");
        response.put("status", "healthy");
        response.put("message", "✅ Mobile payment test endpoints are operational");
        response.put("endpoints", Map.of(
            "methods", "GET /api/v1/payments/mobile/test/methods",
            "idealBanks", "GET /api/v1/payments/mobile/test/ideal/banks",
            "directPayment", "POST /api/v1/payments/mobile/test/direct",
            "redirectPayment", "POST /api/v1/payments/mobile/test/redirect",
            "directSplits", "POST /api/v1/payments/mobile/test/direct/splits",
            "redirectSplits", "POST /api/v1/payments/mobile/test/redirect/splits",
            "quickIdealSplits", "POST /api/v1/payments/mobile/test/direct/ideal/splits?amount=25&issuerId=0031&partnerMerchantId=YOUR_MERCHANT_ID",
            "paymentStatus", "GET /api/v1/payments/mobile/test/status/{orderId}"
        ));
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/scenarios")
    @Operation(
        summary = "Test: Get MultiSafepay test scenarios",
        description = "Returns comprehensive test scenarios for all payment methods including test card numbers, " +
                     "IBANs, phone numbers, and expected outcomes based on MultiSafepay official testing documentation."
    )
    public ResponseEntity<Map<String, Object>> getTestScenarios() {
        log.info("🧪 TEST: Fetching test scenarios");
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "✅ MultiSafepay test scenarios - See PAYMENT_METHODS_TESTING.md for complete guide");
        
        // Banking Methods
        Map<String, Object> bankingScenarios = new HashMap<>();
        bankingScenarios.put("IDEAL", Map.of(
            "banks", "Use any bank from GET /test/ideal/banks",
            "scenarios", "Select Success/Failure/Cancelled/Expired on test platform"
        ));
        bankingScenarios.put("BANCONTACT", Map.of(
            "testCards", Map.of(
                "67034500054620008", "Completed (3D enrolled)",
                "67039902990000045", "Declined (3D failed)",
                "67039902990000011", "Declined (insufficient funds)"
            ),
            "expiry", "Any future date",
            "cvv", "Any 3 digits"
        ));
        bankingScenarios.put("BIZUM", Map.of(
            "phone", "+34612345678 (Spanish format)",
            "amountScenarios", Map.of(
                "€10.00-€20.00", "Completed",
                "< €9.99", "Declined",
                "> €20.00", "Expired (after 84 hours)"
            )
        ));
        bankingScenarios.put("GIROPAY", Map.of(
            "bic", "NOLADE22XXX (any valid German BIC)",
            "scenarios", "Select 'Completed' on test platform"
        ));
        bankingScenarios.put("EPS", Map.of(
            "bic", "RZOOAT2L420 (any valid Austrian BIC)",
            "scenarios", "Select 'Completed' on test platform"
        ));
        bankingScenarios.put("MBWAY", Map.of(
            "phone", "+351912345678 (Portuguese format)",
            "amountScenarios", Map.of(
                "€10.00-€20.00", "Completed",
                "< €9.99", "Declined",
                "> €20.00", "Expired"
            )
        ));
        bankingScenarios.put("MULTIBANCO", Map.of(
            "amountScenarios", Map.of(
                "€10.00-€20.00", "Completed",
                "< €9.99", "Declined",
                "> €20.00", "Expired"
            )
        ));
        bankingScenarios.put("DIRECTDEBIT", Map.of(
            "testIBANs", Map.of(
                "NL87ABNA0000000001", "Initiated → Completed (2 min)",
                "NL87ABNA0000000002", "Initiated → Declined (2 min)",
                "NL87ABNA0000000003", "Initiated → Uncleared → Completed",
                "NL87ABNA0000000004", "Initiated → Uncleared → Declined"
            )
        ));
        response.put("bankingMethods", bankingScenarios);
        
        // Card Methods
        Map<String, Object> cardScenarios = new HashMap<>();
        cardScenarios.put("VISA", Map.of(
            "cards", Map.of(
                "4111111111111111", "Completed (3D enrolled)",
                "4761340000000019", "Completed (3D enrolled)",
                "4917300000000008", "Uncleared → Void (3 min)",
                "4462000000000003", "Uncleared → Completed (3 min)",
                "4012001037461114", "Declined (3D failed)",
                "4012001038488884", "Declined (insufficient funds)"
            ),
            "cvv", "123",
            "expiry", "Any future date"
        ));
        cardScenarios.put("MASTERCARD", Map.of(
            "cards", Map.of("5500000000000004", "Completed (3D enrolled)"),
            "cvv", "123",
            "expiry", "Any future date"
        ));
        cardScenarios.put("MAESTRO", Map.of(
            "cards", Map.of("6799990000000000011", "Completed (3D enrolled)"),
            "cvv", "123",
            "expiry", "Any future date"
        ));
        cardScenarios.put("AMEX", Map.of(
            "cards", Map.of(
                "374500000000015", "Completed (3D enrolled)",
                "378734493671000", "Uncleared → Void (3 min)",
                "374200000000004", "Declined (3D failed)"
            ),
            "cvv", "1234 (4 digits for Amex)",
            "expiry", "Any future date"
        ));
        response.put("cardMethods", cardScenarios);
        
        // BNPL Methods
        Map<String, Object> bnplScenarios = new HashMap<>();
        bnplScenarios.put("KLARNA", Map.of(
            "scenarios", "Click 'Kopen', enter any mobile, any 6-digit verification code",
            "result", "Uncleared (change to Shipped to test invoice)"
        ));
        bnplScenarios.put("IN3", Map.of(
            "successScenario", Map.of(
                "birthday", "01-01-1999",
                "postalCode", "1234AB",
                "houseNumber", "1"
            ),
            "declineScenario", Map.of(
                "birthday", "01-01-2000",
                "postalCode", "1111AB",
                "houseNumber", "1"
            )
        ));
        bnplScenarios.put("BILLINK", Map.of(
            "scenarios", Map.of(
                "Success", "Order Completed, transaction Uncleared",
                "Failure", "Order Declined, transaction Declined",
                "Cancelled", "Order Void, transaction Void"
            )
        ));
        bnplScenarios.put("AFTERPAY", Map.of(
            "rejectTest", "Use email test-reject@afterpay.nl to test rejection"
        ));
        response.put("bnplMethods", bnplScenarios);
        
        // Gift Cards
        Map<String, Object> giftCardScenarios = new HashMap<>();
        giftCardScenarios.put("testCards", Map.of(
            "111115", "€100 balance → Completed",
            "111112", "€5 balance → Completed",
            "111110", "€0 balance → Declined (no balance)"
        ));
        giftCardScenarios.put("pin", "Any 4-digit security code");
        giftCardScenarios.put("types", "VVV, Beauty & Wellness, Fashioncheque, etc.");
        response.put("giftCards", giftCardScenarios);
        
        // Wallets
        Map<String, Object> walletScenarios = new HashMap<>();
        walletScenarios.put("PAYPAL", Map.of(
            "scenarios", "Select Approved/Cancelled/Closed on test platform",
            "note", "Transaction remains 'Initialized' (MultiSafepay doesn't collect for PayPal)"
        ));
        walletScenarios.put("ALIPAY", Map.of(
            "scenarios", "Select 'Completed' on test platform",
            "note", "Cannot test declined transactions"
        ));
        walletScenarios.put("AMAZONPAY", Map.of(
            "scenarios", "Wait 5 seconds or click 'Amazon Pay', select 'Completed'"
        ));
        walletScenarios.put("WECHAT", Map.of(
            "scenarios", "Scan QR code with general QR reader (NOT WeChat app), select 'Completed'"
        ));
        walletScenarios.put("APPLEPAY", Map.of(
            "requirement", "Apple device with Touch ID/Face ID or Appetize.io emulator",
            "note", "Requires Apple Developer account with test cards"
        ));
        walletScenarios.put("GOOGLEPAY", Map.of(
            "requirement", "Google account with at least one chargeable card",
            "note", "May redirect to 3D Secure based on card type"
        ));
        response.put("walletMethods", walletScenarios);
        
        response.put("documentation", "For complete testing guide, see PAYMENT_METHODS_TESTING.md");
        
        return ResponseEntity.ok(response);
    }

    // Helper method to explain payment statuses
    private String getStatusExplanation(String status) {
        if (status == null) return "Status unknown";
        
        return switch (status.toLowerCase()) {
            case "initialized" -> "Payment has been created and is waiting for the customer to complete it";
            case "completed" -> "Payment has been successfully completed and funds are received";
            case "cancelled" -> "Payment was cancelled by the customer or merchant";
            case "expired" -> "Payment link has expired without being completed";
            case "void" -> "Payment authorization was cancelled";
            case "declined" -> "Payment was declined by the payment provider";
            case "refunded" -> "Payment was completed but has been refunded";
            case "partial_refunded" -> "Payment was completed and partially refunded";
            case "reserved" -> "Payment amount is reserved but not yet captured";
            case "uncleared" -> "Payment is received but not yet cleared (may still be reversed)";
            default -> "Status: " + status;
        };
    }
}
