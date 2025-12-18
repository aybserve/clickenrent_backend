package org.clickenrent.authservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating a new B2B user invitation.
 * Used by SUPERADMIN, ADMIN, or B2B users to invite new users to a company.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateInvitationRequest {
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;
    
    @NotNull(message = "Company ID is required")
    private Long companyId;
}


