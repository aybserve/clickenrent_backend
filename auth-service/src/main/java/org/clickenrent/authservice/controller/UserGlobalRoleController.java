package org.clickenrent.authservice.controller;

import lombok.RequiredArgsConstructor;
import org.clickenrent.authservice.dto.UserGlobalRoleDTO;
import org.clickenrent.authservice.service.UserGlobalRoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for UserGlobalRole relationship management.
 */
@RestController
@RequestMapping("/api/user-global-roles")
@RequiredArgsConstructor
public class UserGlobalRoleController {
    
    private final UserGlobalRoleService userGlobalRoleService;
    
    /**
     * Assign a global role to a user.
     * POST /api/user-global-roles
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserGlobalRoleDTO> assignGlobalRoleToUser(
            @RequestParam Long userId,
            @RequestParam Long globalRoleId) {
        UserGlobalRoleDTO userGlobalRole = userGlobalRoleService.assignGlobalRoleToUser(userId, globalRoleId);
        return new ResponseEntity<>(userGlobalRole, HttpStatus.CREATED);
    }
    
    /**
     * Get all global roles for a specific user.
     * GET /api/user-global-roles/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserGlobalRoleDTO>> getUserGlobalRoles(@PathVariable Long userId) {
        List<UserGlobalRoleDTO> userGlobalRoles = userGlobalRoleService.getUserGlobalRoles(userId);
        return ResponseEntity.ok(userGlobalRoles);
    }
    
    /**
     * Remove global role from user.
     * DELETE /api/user-global-roles/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> removeGlobalRoleFromUser(@PathVariable Long id) {
        userGlobalRoleService.removeGlobalRoleFromUser(id);
        return ResponseEntity.noContent().build();
    }
}

