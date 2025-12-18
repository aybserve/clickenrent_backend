package org.clickenrent.authservice.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.authservice.dto.GlobalRoleDTO;
import org.clickenrent.authservice.service.GlobalRoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for GlobalRole management operations.
 */
@RestController
@RequestMapping("/api/global-roles")
@RequiredArgsConstructor
@Tag(name = "Global Role", description = "Global role management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class GlobalRoleController {
    
    private final GlobalRoleService globalRoleService;
    
    /**
     * Get all global roles.
     * GET /api/global-roles
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<List<GlobalRoleDTO>> getAllGlobalRoles() {
        List<GlobalRoleDTO> globalRoles = globalRoleService.getAllGlobalRoles();
        return ResponseEntity.ok(globalRoles);
    }
    
    /**
     * Get global role by ID.
     * GET /api/global-roles/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<GlobalRoleDTO> getGlobalRoleById(@PathVariable Long id) {
        GlobalRoleDTO globalRole = globalRoleService.getGlobalRoleById(id);
        return ResponseEntity.ok(globalRole);
    }
    
    /**
     * Create a new global role.
     * POST /api/global-roles
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<GlobalRoleDTO> createGlobalRole(@Valid @RequestBody GlobalRoleDTO globalRoleDTO) {
        GlobalRoleDTO createdGlobalRole = globalRoleService.createGlobalRole(globalRoleDTO);
        return new ResponseEntity<>(createdGlobalRole, HttpStatus.CREATED);
    }
    
    /**
     * Update global role by ID.
     * PUT /api/global-roles/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<GlobalRoleDTO> updateGlobalRole(
            @PathVariable Long id,
            @Valid @RequestBody GlobalRoleDTO globalRoleDTO) {
        GlobalRoleDTO updatedGlobalRole = globalRoleService.updateGlobalRole(id, globalRoleDTO);
        return ResponseEntity.ok(updatedGlobalRole);
    }
    
    /**
     * Delete global role by ID.
     * DELETE /api/global-roles/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<Void> deleteGlobalRole(@PathVariable Long id) {
        globalRoleService.deleteGlobalRole(id);
        return ResponseEntity.noContent().build();
    }
}




