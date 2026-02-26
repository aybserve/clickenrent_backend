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
 * DTO for MB WAY payment requests (Portuguese mobile payment)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MBWayPaymentRequestDTO {
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "10.00", message = "MB WAY minimum amount is €10.00")
    @DecimalMax(value = "20.00", message = "MB WAY maximum amount is €20.00 in test mode")
    private BigDecimal amount;
    
    @NotBlank(message = "Currency is required")
    @Pattern(regexp = "EUR", message = "MB WAY only supports EUR currency")
    private String currency;
    
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+351[0-9]{9}$", message = "Phone must be Portuguese format: +351XXXXXXXXX")
    private String phone;
    
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
