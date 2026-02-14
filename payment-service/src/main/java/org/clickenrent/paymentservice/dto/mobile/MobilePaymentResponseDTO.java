package org.clickenrent.paymentservice.dto.mobile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for mobile payment response
 * Contains payment URL and instructions for mobile app
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MobilePaymentResponseDTO {
    
    /**
     * MultiSafePay order ID
     */
    private String orderId;
    
    /**
     * Flow type indicator for mobile app:
     * - "direct_minimal_webview": Open minimal WebView for bank authentication
     * - "redirect_full_webview": Open full WebView with payment page
     */
    private String flowType;
    
    /**
     * Payment URL for redirect flow (full payment page)
     */
    private String paymentUrl;
    
    /**
     * Transaction URL for direct flow (bank authentication only)
     */
    private String transactionUrl;
    
    /**
     * QR code URL (if available for the payment method)
     */
    private String qrUrl;
    
    /**
     * Payment status (initialized, completed, cancelled, etc.)
     */
    private String status;
    
    /**
     * Financial status (initialized, completed, uncleared, reserved)
     */
    private String financialStatus;
    
    /**
     * Payment amount
     */
    private BigDecimal amount;
    
    /**
     * Currency code
     */
    private String currency;
    
    /**
     * Mobile-friendly instructions for the user
     */
    private String instructions;
    
    /**
     * Financial transaction external ID (internal reference)
     */
    private String transactionExternalId;
}
