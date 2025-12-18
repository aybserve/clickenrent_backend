package org.clickenrent.paymentservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO for UserPaymentProfile entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPaymentProfileDTO {
    
    private Long id;
    
    private UUID externalId;
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    private String stripeCustomerId;
    
    @NotNull(message = "Active status is required")
    private Boolean isActive;
}

