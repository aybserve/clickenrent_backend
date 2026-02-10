package org.clickenrent.paymentservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationBankAccountDTO {
    
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String externalId;
    
    private String companyExternalId;
    
    @NotBlank(message = "Location external ID is required")
    private String locationExternalId;
    
    @NotBlank(message = "Account holder name is required")
    @Size(max = 255, message = "Account holder name must not exceed 255 characters")
    private String accountHolderName;
    
    @NotBlank(message = "IBAN is required")
    @Size(max = 34, message = "IBAN must not exceed 34 characters")
    @Pattern(regexp = "^[A-Z]{2}[0-9]{2}[A-Z0-9]+$", message = "Invalid IBAN format")
    private String iban;
    
    @Size(max = 11, message = "BIC must not exceed 11 characters")
    @Pattern(regexp = "^[A-Z]{6}[A-Z0-9]{2}([A-Z0-9]{3})?$", message = "Invalid BIC format")
    private String bic;
    
    @NotBlank(message = "Currency is required")
    @Size(min = 3, max = 3, message = "Currency must be 3 characters (e.g., EUR)")
    private String currency;
    
    private Boolean isVerified;
    
    private Boolean isActive;
    
    private String verificationNotes;
    
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;
    
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime updatedAt;
    
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String createdBy;
    
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String updatedBy;
}
