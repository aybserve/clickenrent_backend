package org.clickenrent.paymentservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for PaymentStatus entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentStatusDTO {
    
    private Long id;
    
    private String externalId;
    
    @NotBlank(message = "Payment status code is required")
    private String code;
    
    @NotBlank(message = "Payment status name is required")
    private String name;
    
    // Audit fields
    private LocalDateTime dateCreated;
    private LocalDateTime lastDateModified;
    private String createdBy;
    private String lastModifiedBy;
}








