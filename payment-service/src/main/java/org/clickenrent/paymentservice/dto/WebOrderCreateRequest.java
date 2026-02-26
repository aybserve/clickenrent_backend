package org.clickenrent.paymentservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for creating web checkout orders
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebOrderCreateRequest {
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;
    
    @Builder.Default
    private String currency = "EUR";
    
    @NotBlank(message = "Customer email is required")
    @Email(message = "Invalid email format")
    private String customerEmail;
    
    private String customerFirstName;
    
    private String customerLastName;
    
    private String description;
    
    private String paymentMethodCode;
    
    private String issuerId;
    
    @NotBlank(message = "Redirect URL is required")
    private String redirectUrl;
    
    private String cancelUrl;
    
    private String notificationUrl;
    
    private List<SplitPaymentDTO> splits;
}
