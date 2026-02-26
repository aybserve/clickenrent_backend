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
import org.clickenrent.authservice.dto.AppleLoginRequest;
import org.clickenrent.authservice.dto.AuthResponse;
import org.clickenrent.authservice.service.AppleOAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for Apple OAuth authentication.
 * Handles Apple Sign In via authorization code exchange.
 */
@RestController
@RequestMapping("/api/v1/auth/apple")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Apple Authentication", description = "Apple OAuth social login endpoints")
public class AppleAuthController {
    
    private final AppleOAuthService appleOAuthService;
    
    /**
     * Authenticate user with Apple OAuth (Public endpoint).
     * POST /api/v1/auth/apple/login
     * 
     * Frontend should:
     * 1. Use Apple Sign In SDK to get authorization code
     * 2. Send code and redirectUri to this endpoint
     * 3. Receive JWT tokens in response
     * 
     * Security: Public access - no authentication required.
     * Note: New users automatically get CUSTOMER role.
     * Existing users with matching email are auto-linked to Apple account.
     */
    @PostMapping("/login")
    @Operation(
            summary = "Login with Apple OAuth",
            description = "Authenticates user with Apple authorization code and returns JWT tokens. " +
                         "Creates new user if email doesn't exist, or links Apple account to existing user. " +
                         "Supports Apple's private relay emails. " +
                         "Public endpoint - no authentication required."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully authenticated with Apple",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request - missing or invalid authorization code"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication failed - invalid Apple authorization code or token"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error during Apple OAuth process"
            )
    })
    @SecurityRequirement(name = "") // No authentication required
    public ResponseEntity<AuthResponse> loginWithApple(@Valid @RequestBody AppleLoginRequest request) {
        log.info("Apple OAuth login request received");
        
        AuthResponse response = appleOAuthService.authenticateWithApple(
                request.getCode(),
                request.getRedirectUri()
        );
        
        log.info("Apple OAuth login successful");
        return ResponseEntity.ok(response);
    }
}
