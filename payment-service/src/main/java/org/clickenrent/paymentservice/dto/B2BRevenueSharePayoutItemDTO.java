package org.clickenrent.paymentservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for B2BRevenueSharePayoutItem entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class B2BRevenueSharePayoutItemDTO {
    
    private Long id;
    
    private String externalId;
    
    private Long b2bRevenueSharePayoutId;
    
    @NotNull(message = "Bike rental external ID is required")
    private String bikeRentalExternalId;
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;
    
    // Audit fields
    private LocalDateTime dateCreated;
    private LocalDateTime lastDateModified;
    private String createdBy;
    private String lastModifiedBy;
}




