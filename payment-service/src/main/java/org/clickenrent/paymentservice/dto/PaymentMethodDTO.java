package org.clickenrent.paymentservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * DTO for PaymentMethod entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethodDTO {
    
    private Long id;
    
    private String externalId;
    
    @NotBlank(message = "Payment method code is required")
    private String code;
    
    @NotBlank(message = "Payment method name is required")
    private String name;
    
    @NotNull(message = "Active status is required")
    private Boolean isActive;
}








