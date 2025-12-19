package org.clickenrent.paymentservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * DTO for UserPaymentProfile entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPaymentProfileDTO {
    
    private Long id;
    
    private String externalId;
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    // Cross-service externalId reference
    private String userExternalId;
    
    private String stripeCustomerId;
    
    @NotNull(message = "Active status is required")
    private Boolean isActive;
}


