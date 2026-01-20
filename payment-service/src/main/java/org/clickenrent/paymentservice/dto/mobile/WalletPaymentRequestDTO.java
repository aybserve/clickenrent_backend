package org.clickenrent.paymentservice.dto.mobile;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.clickenrent.paymentservice.dto.SplitPaymentDTO;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for digital wallet payment requests
 * Used for PayPal, Apple Pay, Google Pay, Alipay, Amazon Pay, WeChat Pay
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletPaymentRequestDTO {
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be positive")
    private BigDecimal amount;
    
    @NotBlank(message = "Currency is required")
    private String currency;
    
    @NotBlank(message = "Payment method code is required")
    @Pattern(regexp = "PAYPAL|APPLEPAY|GOOGLEPAY|ALIPAY|ALIPAYPLUS|AMAZONPAY|WECHAT", 
        message = "Invalid wallet payment method")
    private String paymentMethodCode;
    
    /**
     * Apple Pay payment token (required for APPLEPAY)
     */
    private String applePayToken;
    
    /**
     * Google Pay payment token (required for GOOGLEPAY)
     */
    private String googlePayToken;
    
    @NotBlank(message = "Description is required")
    @Size(max = 200, message = "Description must not exceed 200 characters")
    private String description;
    
    @Email(message = "Valid email is required")
    private String customerEmail;
    
    /**
     * External rental ID for tracking
     */
    private String rentalExternalId;
    
    /**
     * Split payment configuration (optional)
     */
    private List<SplitPaymentDTO> splits;
}
