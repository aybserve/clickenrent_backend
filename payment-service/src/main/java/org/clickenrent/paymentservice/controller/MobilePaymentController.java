package org.clickenrent.paymentservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.paymentservice.dto.mobile.*;
import org.clickenrent.paymentservice.service.MobilePaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Mobile-optimized payment controller
 * Provides endpoints for mobile app payment flows
 */
@RestController
@RequestMapping("/api/v1/payments/mobile")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Mobile Payments", description = "Mobile-optimized payment APIs")
public class MobilePaymentController {

    private final MobilePaymentService mobilePaymentService;

    @GetMapping("/methods")
    @Operation(
        summary = "List available payment methods for mobile",
        description = "Returns a list of payment methods optimized for mobile display, " +
                     "including icons, flow types, and requirements. Popular methods like iDEAL " +
                     "are flagged and sorted first."
    )
    public ResponseEntity<List<MobilePaymentMethodDTO>> getPaymentMethods() {
        log.info("Fetching available payment methods for mobile");
        try {
            List<MobilePaymentMethodDTO> methods = mobilePaymentService.getAvailablePaymentMethods();
            log.info("Successfully retrieved {} payment methods", methods.size());
            return ResponseEntity.ok(methods);
        } catch (Exception e) {
            log.error("Failed to get payment methods", e);
            throw e;
        }
    }

    @GetMapping("/ideal/banks")
    @Operation(
        summary = "Get list of banks for iDEAL payment",
        description = "Returns a list of banks (issuers) available for iDEAL payments in the Netherlands. " +
                     "Each bank includes an issuer ID that must be provided when creating a direct iDEAL payment."
    )
    public ResponseEntity<List<MobileBankDTO>> getIdealBanks() {
        log.info("Fetching iDEAL bank list for mobile");
        try {
            List<MobileBankDTO> banks = mobilePaymentService.getIdealBanks();
            log.info("Successfully retrieved {} iDEAL banks", banks.size());
            return ResponseEntity.ok(banks);
        } catch (Exception e) {
            log.error("Failed to get iDEAL banks", e);
            throw e;
        }
    }

    @PostMapping("/direct")
    @Operation(
        summary = "Create direct payment (iDEAL, DirectBank)",
        description = "Creates a direct payment order for payment methods that support direct integration. " +
                     "For iDEAL payments, requires bank issuer ID. " +
                     "Supports split payments for revenue sharing with partners. " +
                     "Returns a transaction URL that should be opened in a minimal WebView for bank authentication. " +
                     "The mobile app should detect the redirect back to the redirectUrl and close the WebView. " +
                     "Payment completion is confirmed via webhook. " +
                     "To use split payments, include the 'splits' array in the request body with partner merchant IDs and percentages."
    )
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN', 'B2B_CLIENT')")
    public ResponseEntity<MobilePaymentResponseDTO> createDirectPayment(
            @Valid @RequestBody MobilePaymentRequestDTO request,
            @AuthenticationPrincipal Jwt jwt) {
        
        String userExternalId = jwt.getSubject();
        boolean hasSplits = request.getSplits() != null && !request.getSplits().isEmpty();
        
        log.info("Creating direct payment for user: {} with method: {}{}", 
            userExternalId, 
            request.getPaymentMethodCode(),
            hasSplits ? " (with " + request.getSplits().size() + " splits)" : "");
        
        try {
            MobilePaymentResponseDTO response = mobilePaymentService.createDirectPayment(request, userExternalId);
            log.info("Successfully created direct payment. Order ID: {}", response.getOrderId());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("Invalid direct payment request: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to create direct payment", e);
            throw e;
        }
    }

    @PostMapping("/redirect")
    @Operation(
        summary = "Create redirect payment (generic)",
        description = "Creates a redirect payment order for payment methods that require full payment page. " +
                     "Supports split payments for revenue sharing with partners. " +
                     "Returns a payment URL that should be opened in a full WebView. " +
                     "The mobile app should detect the redirect back to the redirectUrl and close the WebView. " +
                     "Payment completion is confirmed via webhook. " +
                     "To use split payments, include the 'splits' array in the request body with partner merchant IDs and percentages."
    )
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN', 'B2B_CLIENT')")
    public ResponseEntity<MobilePaymentResponseDTO> createRedirectPayment(
            @Valid @RequestBody MobilePaymentRequestDTO request,
            @AuthenticationPrincipal Jwt jwt) {
        
        String userExternalId = jwt.getSubject();
        boolean hasSplits = request.getSplits() != null && !request.getSplits().isEmpty();
        
        log.info("Creating redirect payment for user: {}{}", 
            userExternalId,
            hasSplits ? " (with " + request.getSplits().size() + " splits)" : "");
        
        try {
            MobilePaymentResponseDTO response = mobilePaymentService.createRedirectPayment(request, userExternalId);
            log.info("Successfully created redirect payment. Order ID: {}", response.getOrderId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to create redirect payment", e);
            throw e;
        }
    }

    @GetMapping("/status/{orderId}")
    @Operation(
        summary = "Get payment status",
        description = "Retrieves the current status of a payment order. " +
                     "Returns both the order status (initialized, completed, cancelled, etc.) " +
                     "and financial status (initialized, completed, uncleared, reserved). " +
                     "Mobile apps should call this endpoint after the user returns from the payment WebView " +
                     "or in response to a push notification."
    )
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN', 'B2B_CLIENT')")
    public ResponseEntity<MobilePaymentResponseDTO> getPaymentStatus(
            @PathVariable String orderId,
            @AuthenticationPrincipal Jwt jwt) {
        
        String userExternalId = jwt.getSubject();
        log.info("Getting payment status for order: {} by user: {}", orderId, userExternalId);
        
        try {
            MobilePaymentResponseDTO response = mobilePaymentService.getPaymentStatus(orderId);
            log.info("Successfully retrieved payment status. Status: {}", response.getStatus());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get payment status for order: {}", orderId, e);
            throw e;
        }
    }

    @PostMapping("/direct/ideal")
    @Operation(
        summary = "Quick iDEAL payment",
        description = "Simplified endpoint for iDEAL payment with default values. " +
                     "Provide only amount and issuer ID via query parameters."
    )
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN', 'B2B_CLIENT')")
    public ResponseEntity<MobilePaymentResponseDTO> createQuickIdealPayment(
            @RequestParam String amount,
            @RequestParam String issuerId,
            @RequestParam(defaultValue = "EUR") String currency,
            @AuthenticationPrincipal Jwt jwt) {
        
        String userExternalId = jwt.getSubject();
        log.info("Creating quick iDEAL payment for user: {} - Amount: {} {}, Issuer: {}", 
            userExternalId, amount, currency, issuerId);
        
        try {
            MobilePaymentRequestDTO request = MobilePaymentRequestDTO.builder()
                .amount(new java.math.BigDecimal(amount))
                .currency(currency)
                .customerEmail(jwt.getClaim("email"))
                .description("iDEAL Payment")
                .paymentMethodCode("IDEAL")
                .issuerId(issuerId)
                .build();
            
            MobilePaymentResponseDTO response = mobilePaymentService.createDirectPayment(request, userExternalId);
            log.info("Successfully created quick iDEAL payment. Order ID: {}", response.getOrderId());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to create quick iDEAL payment", e);
            throw e;
        }
    }

    @PostMapping("/redirect/quick")
    @Operation(
        summary = "Quick redirect payment",
        description = "Simplified endpoint for redirect payment with default values. " +
                     "Provide only amount via query parameter."
    )
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN', 'B2B_CLIENT')")
    public ResponseEntity<MobilePaymentResponseDTO> createQuickRedirectPayment(
            @RequestParam String amount,
            @RequestParam(defaultValue = "EUR") String currency,
            @AuthenticationPrincipal Jwt jwt) {
        
        String userExternalId = jwt.getSubject();
        log.info("Creating quick redirect payment for user: {} - Amount: {} {}", 
            userExternalId, amount, currency);
        
        try {
            MobilePaymentRequestDTO request = MobilePaymentRequestDTO.builder()
                .amount(new java.math.BigDecimal(amount))
                .currency(currency)
                .customerEmail(jwt.getClaim("email"))
                .description("Payment")
                .build();
            
            MobilePaymentResponseDTO response = mobilePaymentService.createRedirectPayment(request, userExternalId);
            log.info("Successfully created quick redirect payment. Order ID: {}", response.getOrderId());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to create quick redirect payment", e);
            throw e;
        }
    }

    @PostMapping("/direct/splits")
    @Operation(
        summary = "Direct payment with split payments",
        description = "Creates a direct payment (iDEAL, DirectBank) with revenue sharing (split payments). " +
                     "This endpoint is explicitly for payments WITH splits. " +
                     "Split configuration must be provided in the request body. " +
                     "Returns a transaction URL for minimal WebView authentication."
    )
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN', 'B2B_CLIENT')")
    public ResponseEntity<MobilePaymentResponseDTO> createDirectPaymentWithSplits(
            @Valid @RequestBody MobilePaymentRequestDTO request,
            @AuthenticationPrincipal Jwt jwt) {
        
        String userExternalId = jwt.getSubject();
        int splitCount = request.getSplits() != null ? request.getSplits().size() : 0;
        
        log.info("Creating direct payment with splits for user: {} - Method: {}, Splits: {}", 
            userExternalId, request.getPaymentMethodCode(), splitCount);
        
        if (splitCount == 0) {
            log.warn("No splits provided to /direct/splits endpoint");
        }
        
        try {
            MobilePaymentResponseDTO response = mobilePaymentService.createDirectPayment(request, userExternalId);
            log.info("Successfully created direct payment with {} splits. Order ID: {}", splitCount, response.getOrderId());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to create direct payment with splits", e);
            throw e;
        }
    }

    @PostMapping("/redirect/splits")
    @Operation(
        summary = "Redirect payment with split payments",
        description = "Creates a redirect payment with revenue sharing (split payments). " +
                     "This endpoint is explicitly for payments WITH splits. " +
                     "Split configuration must be provided in the request body. " +
                     "Returns a payment URL for full WebView."
    )
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN', 'B2B_CLIENT')")
    public ResponseEntity<MobilePaymentResponseDTO> createRedirectPaymentWithSplits(
            @Valid @RequestBody MobilePaymentRequestDTO request,
            @AuthenticationPrincipal Jwt jwt) {
        
        String userExternalId = jwt.getSubject();
        int splitCount = request.getSplits() != null ? request.getSplits().size() : 0;
        
        log.info("Creating redirect payment with splits for user: {} - Splits: {}", 
            userExternalId, splitCount);
        
        if (splitCount == 0) {
            log.warn("No splits provided to /redirect/splits endpoint");
        }
        
        try {
            MobilePaymentResponseDTO response = mobilePaymentService.createRedirectPayment(request, userExternalId);
            log.info("Successfully created redirect payment with {} splits. Order ID: {}", splitCount, response.getOrderId());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to create redirect payment with splits", e);
            throw e;
        }
    }

    @PostMapping("/direct/ideal/splits")
    @Operation(
        summary = "Quick iDEAL payment with splits",
        description = "Quick endpoint for iDEAL payment with revenue sharing. " +
                     "Provide amount, issuer ID, partner merchant ID, and percentage via query parameters."
    )
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN', 'B2B_CLIENT')")
    public ResponseEntity<MobilePaymentResponseDTO> createQuickIdealWithSplits(
            @RequestParam String amount,
            @RequestParam String issuerId,
            @RequestParam String partnerMerchantId,
            @RequestParam(defaultValue = "30") String partnerPercentage,
            @RequestParam(defaultValue = "EUR") String currency,
            @AuthenticationPrincipal Jwt jwt) {
        
        String userExternalId = jwt.getSubject();
        log.info("Creating quick iDEAL with splits for user: {} - Amount: {} {}, Partner: {} ({}%)", 
            userExternalId, amount, currency, partnerMerchantId, partnerPercentage);
        
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
                .customerEmail(jwt.getClaim("email"))
                .description("iDEAL Payment with Split")
                .paymentMethodCode("IDEAL")
                .issuerId(issuerId)
                .splits(splits)
                .build();
            
            MobilePaymentResponseDTO response = mobilePaymentService.createDirectPayment(request, userExternalId);
            log.info("Successfully created quick iDEAL payment with splits. Order ID: {}", response.getOrderId());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to create quick iDEAL payment with splits", e);
            throw e;
        }
    }

    @PostMapping("/redirect/quick/splits")
    @Operation(
        summary = "Quick redirect payment with splits",
        description = "Quick endpoint for redirect payment with revenue sharing. " +
                     "Provide amount, partner merchant ID, and percentage via query parameters."
    )
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN', 'B2B_CLIENT')")
    public ResponseEntity<MobilePaymentResponseDTO> createQuickRedirectWithSplits(
            @RequestParam String amount,
            @RequestParam String partnerMerchantId,
            @RequestParam(defaultValue = "30") String partnerPercentage,
            @RequestParam(defaultValue = "EUR") String currency,
            @AuthenticationPrincipal Jwt jwt) {
        
        String userExternalId = jwt.getSubject();
        log.info("Creating quick redirect with splits for user: {} - Amount: {} {}, Partner: {} ({}%)", 
            userExternalId, amount, currency, partnerMerchantId, partnerPercentage);
        
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
                .customerEmail(jwt.getClaim("email"))
                .description("Payment with Split")
                .splits(splits)
                .build();
            
            MobilePaymentResponseDTO response = mobilePaymentService.createRedirectPayment(request, userExternalId);
            log.info("Successfully created quick redirect payment with splits. Order ID: {}", response.getOrderId());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to create quick redirect payment with splits", e);
            throw e;
        }
    }

    @GetMapping("/health")
    @Operation(
        summary = "Health check for mobile payment service",
        description = "Simple health check endpoint to verify mobile payment service availability"
    )
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Mobile payment service is healthy");
    }
}
