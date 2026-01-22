package org.clickenrent.authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.clickenrent.authservice.validator.ValidGoogleLoginRequest;

/**
 * Request DTO for Google OAuth login.
 * Supports two authentication flows:
 * 1. Web flow: authorization code exchange (code + redirectUri)
 * 2. Mobile flow: ID token verification (idToken)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ValidGoogleLoginRequest
@Schema(description = "Google OAuth login request supporting both web (code + redirectUri) and mobile (idToken) flows")
public class GoogleLoginRequest {
    
    @Schema(description = "Authorization code from Google OAuth (Web flow)", example = "4/0AY0e-g7...")
    private String code;
    
    @Schema(description = "Redirect URI used in OAuth flow (Web flow)", example = "http://localhost:3000/auth/google/callback")
    private String redirectUri;
    
    @Schema(description = "Google ID token from mobile app (Mobile flow)", example = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String idToken;
}



