package org.clickenrent.authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for Apple OAuth login.
 * Contains the authorization code received from Apple OAuth flow.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Apple OAuth login request containing authorization code")
public class AppleLoginRequest {
    
    @NotBlank(message = "Authorization code is required")
    @Schema(description = "Authorization code received from Apple OAuth", example = "c1a2b3c4d5...")
    private String code;
    
    @NotBlank(message = "Redirect URI is required")
    @Schema(description = "Redirect URI used in the OAuth flow", example = "http://localhost:3000/auth/apple/callback")
    private String redirectUri;
}
