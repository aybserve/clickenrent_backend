package org.clickenrent.authservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.authservice.dto.*;
import org.clickenrent.authservice.service.AuthService;
import org.clickenrent.authservice.service.EmailVerificationService;
import org.clickenrent.authservice.service.PasswordResetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for authentication operations.
 * Handles user registration, login, token refresh, and profile management.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "User authentication and registration endpoints")
public class AuthController {
    
    private final AuthService authService;
    private final EmailVerificationService emailVerificationService;
    private final PasswordResetService passwordResetService;
    
    /**
     * Register a new user (Public endpoint).
     * POST /api/auth/register
     * 
     * Security: Public access - no authentication required.
     * Note: Automatically assigns CUSTOMER role during registration.
     * Returns JWT tokens that include CUSTOMER role in authorities.
     */
    @PostMapping("/register")
    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account with CUSTOMER role and returns JWT tokens for authentication. Public endpoint."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "User already exists")
    })
    @SecurityRequirement(name = "") // No authentication required
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    /**
     * Register a new user with pre-assigned roles (SUPERADMIN only).
     * POST /api/auth/register-admin
     * 
     * Security: Requires SUPERADMIN role.
     * Used to create privileged users (ADMIN, B2B) with roles assigned during registration.
     */
    @PostMapping("/register-admin")
    @PreAuthorize("hasRole('SUPERADMIN')")
    @Operation(
            summary = "Register a privileged user (SUPERADMIN only)",
            description = "Creates a new user account with pre-assigned global roles. Restricted to SUPERADMIN. Used for creating ADMIN and B2B users."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully",
                    content = @Content(schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions - SUPERADMIN required"),
            @ApiResponse(responseCode = "404", description = "One or more role IDs not found"),
            @ApiResponse(responseCode = "409", description = "User already exists")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UserDTO> registerAdmin(@Valid @RequestBody AdminRegisterRequest request) {
        UserDTO createdUser = authService.registerAdmin(request);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }
    
    /**
     * Authenticate user and generate JWT tokens.
     * POST /api/auth/login
     * 
     * Security: Public access - no authentication required.
     * Available to all users regardless of role.
     */
    @PostMapping("/login")
    @Operation(
            summary = "Login user",
            description = "Authenticates user credentials and returns JWT access and refresh tokens. Public endpoint."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @SecurityRequirement(name = "") // No authentication required
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Refresh access token using refresh token.
     * POST /api/auth/refresh
     * 
     * Security: Public access - no authentication required (uses refresh token in request body).
     * Available to all users regardless of role.
     */
    @PostMapping("/refresh")
    @Operation(
            summary = "Refresh access token",
            description = "Generates a new access token using a valid refresh token. Public endpoint."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token refreshed successfully",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token")
    })
    @SecurityRequirement(name = "") // No authentication required
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get current authenticated user's profile.
     * GET /api/auth/me
     * 
     * Security: Requires authentication (any authenticated user).
     * Available to all roles: SUPERADMIN, ADMIN, B2B, CUSTOMER.
     */
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get current user profile",
            description = "Returns the profile information of the currently authenticated user. Requires authentication."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User profile retrieved successfully",
                    content = @Content(schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UserDTO> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UserDTO user = authService.getCurrentUser(username);
        return ResponseEntity.ok(user);
    }
    
    /**
     * Logout user by blacklisting the current access token.
     * POST /api/auth/logout
     * 
     * Security: Requires authentication (any authenticated user).
     * Available to all roles: SUPERADMIN, ADMIN, B2B, CUSTOMER.
     */
    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Logout user",
            description = "Invalidates the current access token by blacklisting it. Requires authentication."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Logout successful"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            authService.logout(token);
        }
        
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Verify email address with 6-digit code.
     * POST /api/auth/verify-email
     * 
     * Security: Public access - no authentication required.
     * Returns new JWT tokens with updated user info (isEmailVerified=true).
     */
    @PostMapping("/verify-email")
    @Operation(
            summary = "Verify email address",
            description = "Verifies user email with 6-digit code. Returns new tokens with updated user info. Public endpoint."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email verified successfully",
                    content = @Content(schema = @Schema(implementation = VerifyEmailResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid or expired code"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @SecurityRequirement(name = "") // No authentication required
    public ResponseEntity<VerifyEmailResponse> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
        System.out.println("===========================================");
        System.out.println("=== AuthController.verifyEmail() CALLED");
        System.out.println("=== Email: " + request.getEmail());
        System.out.println("=== Code: " + request.getCode());
        System.out.println("===========================================");
        
        VerifyEmailResponse response = authService.verifyEmailAndGenerateTokens(
                request.getEmail(), 
                request.getCode()
        );
        return ResponseEntity.ok(response);
    }
    
    /**
     * Send or resend verification code to user's email.
     * POST /api/auth/send-verification-code
     * 
     * Security: Public access - no authentication required.
     * Invalidates any existing unused codes and generates a new one.
     */
    @PostMapping("/send-verification-code")
    @Operation(
            summary = "Send/resend verification code",
            description = "Sends a new 6-digit verification code to user's email. Invalidates previous unused codes. Public endpoint."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Verification code sent successfully"),
            @ApiResponse(responseCode = "400", description = "Email already verified"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @SecurityRequirement(name = "") // No authentication required
    public ResponseEntity<Void> sendVerificationCode(@Valid @RequestBody SendVerificationCodeRequest request) {
        emailVerificationService.resendVerificationCode(request.getEmail());
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Initiate password reset by sending reset token to email.
     * POST /api/auth/forgot-password
     * 
     * Security: Public access - no authentication required.
     * Invalidates any existing unused reset tokens and generates a new one.
     * For security, always returns success even if email doesn't exist.
     */
    @PostMapping("/forgot-password")
    @Operation(
            summary = "Initiate password reset",
            description = "Sends a 6-digit reset token to user's email. Invalidates previous unused tokens. Public endpoint."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "If email exists, reset token has been sent"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @SecurityRequirement(name = "") // No authentication required
    public ResponseEntity<Void> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request,
            HttpServletRequest httpRequest) {
        try {
            passwordResetService.initiatePasswordReset(request.getEmail(), httpRequest);
        } catch (Exception e) {
            // For security, don't reveal if email exists or not
            // Always return success to prevent email enumeration
            log.warn("Password reset attempt for email: {}, error: {}", request.getEmail(), e.getMessage());
        }
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Reset password using reset token.
     * POST /api/auth/reset-password
     * 
     * Security: Public access - no authentication required (uses reset token for validation).
     * Validates token, updates password, and sends confirmation email.
     */
    @PostMapping("/reset-password")
    @Operation(
            summary = "Reset password with token",
            description = "Resets user password using the 6-digit reset token. Public endpoint."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset successful",
                    content = @Content(schema = @Schema(implementation = PasswordResetResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid or expired token"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @SecurityRequirement(name = "") // No authentication required
    public ResponseEntity<PasswordResetResponse> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request,
            HttpServletRequest httpRequest) {
        passwordResetService.resetPassword(
                request.getEmail(), 
                request.getToken(), 
                request.getNewPassword(),
                httpRequest
        );
        
        PasswordResetResponse response = PasswordResetResponse.builder()
                .success(true)
                .message("Password has been reset successfully")
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Validate password reset token.
     * GET /api/auth/validate-reset-token
     * 
     * Security: Public access - no authentication required.
     * Checks if a reset token is valid without consuming attempts.
     */
    @GetMapping("/validate-reset-token")
    @Operation(
            summary = "Validate password reset token",
            description = "Checks if a reset token is valid and returns expiration info. Public endpoint."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token validation result",
                    content = @Content(schema = @Schema(implementation = TokenValidationResponse.class)))
    })
    @SecurityRequirement(name = "") // No authentication required
    public ResponseEntity<TokenValidationResponse> validateResetToken(
            @RequestParam String email,
            @RequestParam(required = false) String token) {
        TokenValidationResponse response = passwordResetService.validateToken(token, email);
        return ResponseEntity.ok(response);
    }
}

