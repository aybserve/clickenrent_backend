package org.clickenrent.paymentservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * DTO for Currency entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyDTO {
    
    private Long id;
    
    private String externalId;
    
    @NotBlank(message = "Currency code is required")
    @Size(min = 3, max = 3, message = "Currency code must be exactly 3 characters")
    private String code;
    
    @NotBlank(message = "Currency name is required")
    private String name;
}







