package org.clickenrent.authservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for validating Swagger access credentials.
 * Used by Gateway to validate user credentials and check for required roles.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SwaggerAccessRequest {
    
    @NotBlank(message = "Username or email is required")
    private String usernameOrEmail;
    
    @NotBlank(message = "Password is required")
    private String password;
}
