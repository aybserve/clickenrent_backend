package org.clickenrent.paymentservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO for B2BRevenueSharePayout entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class B2BRevenueSharePayoutDTO {
    
    private Long id;
    
    private String externalId;
    
    @NotNull(message = "Company ID is required")
    private Long companyId;
    
    // Cross-service externalId reference
    private String companyExternalId;
    
    @NotNull(message = "Payment status is required")
    private PaymentStatusDTO paymentStatus;
    
    @NotNull(message = "Due date is required")
    private LocalDate dueDate;
    
    @NotNull(message = "Total amount is required")
    @PositiveOrZero(message = "Total amount must be positive or zero")
    private BigDecimal totalAmount;
    
    @NotNull(message = "Paid amount is required")
    @PositiveOrZero(message = "Paid amount must be positive or zero")
    private BigDecimal paidAmount;
    
    @NotNull(message = "Remaining amount is required")
    @PositiveOrZero(message = "Remaining amount must be positive or zero")
    private BigDecimal remainingAmount;
    
    @Builder.Default
    private List<B2BRevenueSharePayoutItemDTO> payoutItems = new ArrayList<>();
}


