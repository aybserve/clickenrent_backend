package org.clickenrent.paymentservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO for B2BRevenueSharePayoutItem entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class B2BRevenueSharePayoutItemDTO {
    
    private Long id;
    
    private UUID externalId;
    
    private Long b2bRevenueSharePayoutId;
    
    @NotNull(message = "Bike rental ID is required")
    private Long bikeRentalId;
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;
}

