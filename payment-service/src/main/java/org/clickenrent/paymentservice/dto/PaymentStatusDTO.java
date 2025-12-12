package org.clickenrent.paymentservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO for PaymentStatus entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentStatusDTO {
    
    private Long id;
    
    private UUID externalId;
    
    @NotBlank(message = "Payment status code is required")
    private String code;
    
    @NotBlank(message = "Payment status name is required")
    private String name;
}
