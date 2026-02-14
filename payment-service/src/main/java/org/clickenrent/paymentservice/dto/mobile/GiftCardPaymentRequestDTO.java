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
 * DTO for gift card payment requests
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GiftCardPaymentRequestDTO {
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be positive")
    private BigDecimal amount;
    
    @NotBlank(message = "Currency is required")
    private String currency;
    
    @NotBlank(message = "Gift card type is required")
    @Pattern(regexp = "VVVGIFTCARD|BEAUTYANDWELLNESS|BOEKENBON|FASHIONCHEQUE|FASHIONGIFTCARD|WEBSHOPGIFTCARD|EDENRED|MONIZZE|SODEXO", 
        message = "Invalid gift card type")
    private String giftCardType;
    
    @NotBlank(message = "Card number is required")
    @Size(min = 6, max = 20, message = "Card number must be between 6 and 20 characters")
    private String cardNumber;
    
    /**
     * PIN / security code (required for most gift cards)
     */
    @Pattern(regexp = "^[0-9]{4,8}$", message = "PIN must be 4-8 digits")
    private String pin;
    
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
