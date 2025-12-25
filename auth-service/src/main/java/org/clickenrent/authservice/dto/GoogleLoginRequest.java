package org.clickenrent.authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for Google OAuth login.
 * Contains the authorization code received from Google OAuth flow.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Google OAuth login request containing authorization code")
public class GoogleLoginRequest {
    
    @NotBlank(message = "Authorization code is required")
    @Schema(description = "Authorization code received from Google OAuth", example = "4/0AY0e-g7...")
    private String code;
    
    @NotBlank(message = "Redirect URI is required")
    @Schema(description = "Redirect URI used in the OAuth flow", example = "http://localhost:3000/auth/google/callback")
    private String redirectUri;
}

