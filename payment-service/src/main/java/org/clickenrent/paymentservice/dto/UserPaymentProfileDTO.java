package org.clickenrent.paymentservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
    
    // Cross-service externalId reference
    @NotNull(message = "User external ID is required")
    private String userExternalId;
    
    private String stripeCustomerId;
    
    @NotNull(message = "Active status is required")
    private Boolean isActive;
    
    // Audit fields
    private LocalDateTime dateCreated;
    private LocalDateTime lastDateModified;
    private String createdBy;
    private String lastModifiedBy;
}




