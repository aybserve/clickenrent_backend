package org.clickenrent.authservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.authservice.dto.CreateUserRequest;
import org.clickenrent.authservice.dto.UserDTO;
import org.clickenrent.authservice.service.UserService;
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
public class UserController {
    
    private final UserService userService;
    
    /**
     * Get all users with pagination.
     * GET /api/users
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
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
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }
    
    /**
     * Get user by external ID.
     * GET /api/users/external/{externalId}
     */
    @GetMapping("/external/{externalId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<UserDTO> getUserByExternalId(@PathVariable String externalId) {
        UserDTO user = userService.getUserByExternalId(externalId);
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
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserDTO userDTO = UserDTO.builder()
                .userName(request.getUserName())
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .city(request.getCity())
                .address(request.getAddress())
                .zipcode(request.getZipcode())
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
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserDTO userDTO) {
        UserDTO updatedUser = userService.updateUser(id, userDTO);
        return ResponseEntity.ok(updatedUser);
    }
    
    /**
     * Soft delete user by ID.
     * DELETE /api/users/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
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
}

