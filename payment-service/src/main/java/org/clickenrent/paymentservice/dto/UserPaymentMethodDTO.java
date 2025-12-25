package org.clickenrent.paymentservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * DTO for UserPaymentMethod entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPaymentMethodDTO {
    
    private Long id;
    
    private String externalId;
    
    @NotNull(message = "User payment profile is required")
    private UserPaymentProfileDTO userPaymentProfile;
    
    @NotNull(message = "Payment method is required")
    private PaymentMethodDTO paymentMethod;
    
    private String stripePaymentMethodId;
    
    @NotNull(message = "Default status is required")
    private Boolean isDefault;
    
    @NotNull(message = "Active status is required")
    private Boolean isActive;
}






