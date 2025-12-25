package org.clickenrent.authservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.authservice.dto.AuthResponse;
import org.clickenrent.authservice.dto.GoogleLoginRequest;
import org.clickenrent.authservice.service.GoogleOAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for Google OAuth authentication.
 * Handles Google social login via authorization code exchange.
 */
@RestController
@RequestMapping("/api/auth/google")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Google Authentication", description = "Google OAuth social login endpoints")
public class GoogleAuthController {
    
    private final GoogleOAuthService googleOAuthService;
    
    /**
     * Authenticate user with Google OAuth (Public endpoint).
     * POST /api/auth/google/login
     * 
     * Frontend should:
     * 1. Use Google Sign-In SDK to get authorization code
     * 2. Send code and redirectUri to this endpoint
     * 3. Receive JWT tokens in response
     * 
     * Security: Public access - no authentication required.
     * Note: New users automatically get CUSTOMER role.
     * Existing users with matching email are auto-linked to Google account.
     */
    @PostMapping("/login")
    @Operation(
            summary = "Login with Google OAuth",
            description = "Authenticates user with Google authorization code and returns JWT tokens. " +
                         "Creates new user if email doesn't exist, or links Google account to existing user. " +
                         "Public endpoint - no authentication required."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully authenticated with Google",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request - missing or invalid authorization code"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication failed - invalid Google authorization code or token"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error during Google OAuth process"
            )
    })
    @SecurityRequirement(name = "") // No authentication required
    public ResponseEntity<AuthResponse> loginWithGoogle(@Valid @RequestBody GoogleLoginRequest request) {
        log.info("Google OAuth login request received");
        
        AuthResponse response = googleOAuthService.authenticateWithGoogle(
                request.getCode(),
                request.getRedirectUri()
        );
        
        log.info("Google OAuth login successful");
        return ResponseEntity.ok(response);
    }
}

