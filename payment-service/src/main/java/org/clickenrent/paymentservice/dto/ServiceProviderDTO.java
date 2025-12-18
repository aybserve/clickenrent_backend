package org.clickenrent.paymentservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO for ServiceProvider entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceProviderDTO {
    
    private Long id;
    
    private UUID externalId;
    
    @NotBlank(message = "Service provider code is required")
    private String code;
    
    @NotBlank(message = "Service provider name is required")
    private String name;
}


