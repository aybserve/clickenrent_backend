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
 * DTO for Bizum payment requests (Spanish mobile payment)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BizumPaymentRequestDTO {
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "10.00", message = "Bizum minimum amount is €10.00")
    @DecimalMax(value = "1000.00", message = "Bizum maximum amount is €1000.00")
    private BigDecimal amount;
    
    @NotBlank(message = "Currency is required")
    @Pattern(regexp = "EUR", message = "Bizum only supports EUR currency")
    private String currency;
    
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+34[0-9]{9}$", message = "Phone must be Spanish format: +34XXXXXXXXX")
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
