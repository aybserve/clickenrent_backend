package org.clickenrent.paymentservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for UserPaymentMethod entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPaymentMethodDTO {
    
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String externalId;
    
    @NotNull(message = "User payment profile is required")
    private UserPaymentProfileDTO userPaymentProfile;
    
    @NotNull(message = "Payment method is required")
    private PaymentMethodDTO paymentMethod;
    
    private String stripePaymentMethodId;
    
    private String multiSafepayTokenId;
    
    @NotNull(message = "Default status is required")
    private Boolean isDefault;
    
    @NotNull(message = "Active status is required")
    private Boolean isActive;
    
    // Audit fields
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime dateCreated;
    
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime lastDateModified;
    
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String createdBy;
    
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String lastModifiedBy;
}








