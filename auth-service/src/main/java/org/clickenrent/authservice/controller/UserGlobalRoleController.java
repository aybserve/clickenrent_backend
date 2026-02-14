package org.clickenrent.authservice.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.authservice.dto.AssignGlobalRoleRequest;
import org.clickenrent.authservice.dto.UserGlobalRoleDTO;
import org.clickenrent.authservice.service.UserGlobalRoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for UserGlobalRole relationship management.
 * 
 * Security Rules:
 * - SUPERADMIN/ADMIN: Full access to assign and manage global roles
 * - Other roles: No access
 */
@RestController
@RequestMapping("/api/v1/user-global-roles")
@RequiredArgsConstructor
@Tag(name = "User Global Role", description = "User Global Role management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class UserGlobalRoleController {
    
    private final UserGlobalRoleService userGlobalRoleService;
    
    /**
     * Assign a global role to a user.
     * POST /api/user-global-roles
     * Requires: SUPERADMIN or ADMIN role
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<UserGlobalRoleDTO> assignGlobalRoleToUser(
            @Valid @RequestBody AssignGlobalRoleRequest request) {
        UserGlobalRoleDTO userGlobalRole = userGlobalRoleService.assignGlobalRoleToUser(
                request.getUserId(), 
                request.getGlobalRoleId()
        );
        return new ResponseEntity<>(userGlobalRole, HttpStatus.CREATED);
    }
    
    /**
     * Get user-global role link by external ID.
     * GET /api/user-global-roles/external/{externalId}
     * Requires: SUPERADMIN or ADMIN role
     */
    @GetMapping("/external/{externalId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<UserGlobalRoleDTO> getUserGlobalRoleByExternalId(@PathVariable String externalId) {
        UserGlobalRoleDTO userGlobalRole = userGlobalRoleService.getUserGlobalRoleByExternalId(externalId);
        return ResponseEntity.ok(userGlobalRole);
    }
    
    /**
     * Get all global roles for a specific user.
     * GET /api/user-global-roles/user/{userId}
     * Requires: SUPERADMIN or ADMIN role
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<List<UserGlobalRoleDTO>> getUserGlobalRoles(@PathVariable Long userId) {
        List<UserGlobalRoleDTO> userGlobalRoles = userGlobalRoleService.getUserGlobalRoles(userId);
        return ResponseEntity.ok(userGlobalRoles);
    }
    
    /**
     * Remove global role from user.
     * DELETE /api/user-global-roles/{id}
     * Requires: SUPERADMIN or ADMIN role
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<Void> removeGlobalRoleFromUser(@PathVariable Long id) {
        userGlobalRoleService.removeGlobalRoleFromUser(id);
        return ResponseEntity.noContent().build();
    }
}

