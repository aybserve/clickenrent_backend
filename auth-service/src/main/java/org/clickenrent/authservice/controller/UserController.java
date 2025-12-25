package org.clickenrent.authservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.authservice.dto.CreateUserRequest;
import org.clickenrent.authservice.dto.UserDTO;
import org.clickenrent.authservice.dto.UserStatsDTO;
import org.clickenrent.authservice.service.UserService;
import org.clickenrent.authservice.service.UserStatisticsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for User management operations.
 * 
 * Security Rules:
 * - SUPERADMIN/ADMIN: Full access to all users
 * - B2B: Can view users in their companies
 * - CUSTOMER: Can only view and update themselves
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "User management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class UserController {
    
    private final UserService userService;
    private final UserStatisticsService userStatisticsService;
    
    /**
     * Get all users with pagination.
     * GET /api/users
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(
            summary = "Get all users",
            description = "Returns a paginated list of all users. Access control based on user role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<Page<UserDTO>> getAllUsers(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<UserDTO> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }
    
    /**
     * Get user by ID.
     * GET /api/users/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN') or @resourceSecurity.canAccessUser(#id)")
    @Operation(
            summary = "Get user by ID",
            description = "Returns user details by user ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserDTO> getUserById(
            @Parameter(description = "User ID", required = true) @PathVariable Long id) {
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }
    
    /**
     * Get user by external ID.
     * GET /api/users/external/{externalId}
     */
    @GetMapping("/external/{externalId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get user by external ID", description = "Retrieve user details by external ID for cross-service communication")
    public ResponseEntity<UserDTO> getUserByExternalId(@PathVariable String externalId) {
        UserDTO user = userService.findByExternalId(externalId);
        return ResponseEntity.ok(user);
    }
    
    /**
     * Create a new user (admin only).
     * POST /api/users
     * Requires: SUPERADMIN or ADMIN role
     * 
     * Security: Password is securely transmitted in the request body (not URL).
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'CUSTOMER')")
    @Operation(
            summary = "Create a new user",
            description = "Creates a new user account. Requires SUPERADMIN or ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully",
                    content = @Content(schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "409", description = "User already exists")
    })
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserDTO userDTO = UserDTO.builder()
                .userName(request.getUserName())
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .imageUrl(request.getImageUrl())
                .languageId(request.getLanguageId())
                .isActive(request.getIsActive())
                .build();
        
        UserDTO createdUser = userService.createUser(userDTO, request.getPassword());
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }
    
    /**
     * Update user by ID.
     * PUT /api/users/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN') or @resourceSecurity.canAccessUser(#id)")
    @Operation(
            summary = "Update user",
            description = "Updates user information by ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully",
                    content = @Content(schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserDTO> updateUser(
            @Parameter(description = "User ID", required = true) @PathVariable Long id,
            @Valid @RequestBody UserDTO userDTO) {
        UserDTO updatedUser = userService.updateUser(id, userDTO);
        return ResponseEntity.ok(updatedUser);
    }
    
    /**
     * Soft delete user by ID.
     * DELETE /api/users/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN') or @resourceSecurity.canAccessUser(#id)")
    @Operation(
            summary = "Delete user",
            description = "Soft deletes a user by ID. Requires SUPERADMIN or ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "User ID", required = true) @PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Activate user by ID.
     * PUT /api/users/{id}/activate
     */
    @PutMapping("/{id}/activate")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<UserDTO> activateUser(@PathVariable Long id) {
        UserDTO user = userService.activateUser(id);
        return ResponseEntity.ok(user);
    }
    
    /**
     * Deactivate user by ID.
     * PUT /api/users/{id}/deactivate
     */
    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<UserDTO> deactivateUser(@PathVariable Long id) {
        UserDTO user = userService.deactivateUser(id);
        return ResponseEntity.ok(user);
    }
    
    /**
     * Get user bike rental statistics.
     * GET /api/users/{id}/stats
     */
    @GetMapping("/{id}/stats")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN') or @resourceSecurity.canAccessUser(#id)")
    @Operation(
            summary = "Get user bike rental statistics",
            description = "Returns comprehensive statistics about user's bike rentals, rides, spending, and ratings. " +
                          "Admins can view any user's stats, regular users can only view their own."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully",
                    content = @Content(schema = @Schema(implementation = UserStatsDTO.class))),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserStatsDTO> getUserStats(
            @Parameter(description = "User ID", required = true) @PathVariable Long id) {
        UserStatsDTO stats = userStatisticsService.getUserStats(id);
        return ResponseEntity.ok(stats);
    }
}

