package org.clickenrent.paymentservice.dto.mobile;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.clickenrent.paymentservice.dto.SplitPaymentDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * DTO for mobile payment request
 * Supports both direct and redirect payment flows
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MobilePaymentRequestDTO {
    
    /**
     * Payment amount
     */
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;
    
    /**
     * Currency code (e.g., EUR, USD)
     */
    @NotBlank(message = "Currency is required")
    private String currency;
    
    /**
     * Customer email address
     */
    private String customerEmail;
    
    /**
     * Payment description
     */
    private String description;
    
    // Fields for direct payments
    
    /**
     * Payment method code (e.g., IDEAL, DIRECTBANK) - required for direct payments
     */
    private String paymentMethodCode;
    
    /**
     * Bank issuer ID - required for iDEAL payments
     */
    private String issuerId;
    
    /**
     * Account holder name - required for DirectBank payments
     */
    private String accountHolderName;
    
    /**
     * Account holder city - for DirectBank payments
     */
    private String accountHolderCity;
    
    /**
     * Account holder country - for DirectBank payments
     */
    private String accountHolderCountry;
    
    /**
     * Account holder IBAN - required for DirectBank payments
     */
    private String accountHolderIban;
    
    /**
     * Account holder BIC - for DirectBank payments
     */
    private String accountHolderBic;
    
    // ========================================
    // Banking Methods Fields
    // ========================================
    
    /**
     * Phone number - required for Bizum, MB WAY
     */
    private String phone;
    
    /**
     * BIC code - required for Giropay, EPS
     */
    private String bic;
    
    // ========================================
    // Card Payment Fields
    // ========================================
    
    /**
     * Card number - required for card payments
     */
    private String cardNumber;
    
    /**
     * Card holder name - required for card payments
     */
    private String cardHolderName;
    
    /**
     * Card expiry date (MM/YY format) - required for card payments
     */
    private String expiryDate;
    
    /**
     * Card CVV/CVC - required for card payments
     */
    private String cvv;
    
    // ========================================
    // BNPL (Buy Now Pay Later) Fields
    // ========================================
    
    /**
     * Customer birthday (YYYY-MM-DD) - required for BNPL methods (Klarna, Billink, in3, Riverty)
     */
    private String birthday;
    
    /**
     * Customer gender (male/female/other) - required for some BNPL methods
     */
    private String gender;
    
    /**
     * Company type (private/business) - required for Billink
     */
    private String companyType;
    
    // ========================================
    // Prepaid Cards / Gift Cards Fields
    // ========================================
    
    /**
     * Gift card PIN / security code
     */
    private String pin;
    
    // ========================================
    // Wallet Payment Fields
    // ========================================
    
    /**
     * Apple Pay payment token - required for Apple Pay
     */
    private String applePayToken;
    
    /**
     * Google Pay payment token - required for Google Pay
     */
    private String googlePayToken;
    
    // ========================================
    // Metadata
    // ========================================
    
    /**
     * Optional: Rental external ID to link payment to rental
     */
    private String rentalExternalId;
    
    /**
     * Optional: Additional metadata
     */
    private Map<String, String> metadata;
    
    // ========================================
    // Split Payments
    // ========================================
    
    /**
     * Optional: Split payment configuration for revenue sharing
     * Each split defines a merchant ID and their share (percentage or fixed amount)
     */
    private List<SplitPaymentDTO> splits;
}
