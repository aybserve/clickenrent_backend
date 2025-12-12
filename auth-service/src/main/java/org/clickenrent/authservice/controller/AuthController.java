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
import org.clickenrent.authservice.dto.*;
import org.clickenrent.authservice.service.AuthService;
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
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User authentication and registration endpoints")
public class AuthController {
    
    private final AuthService authService;
    
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
}

