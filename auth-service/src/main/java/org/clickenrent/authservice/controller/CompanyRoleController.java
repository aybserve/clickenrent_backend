package org.clickenrent.authservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.authservice.dto.CompanyRoleDTO;
import org.clickenrent.authservice.service.CompanyRoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for CompanyRole management operations.
 */
@RestController
@RequestMapping("/api/company-roles")
@RequiredArgsConstructor
public class CompanyRoleController {
    
    private final CompanyRoleService companyRoleService;
    
    /**
     * Get all company roles.
     * GET /api/company-roles
     */
    @GetMapping
    public ResponseEntity<List<CompanyRoleDTO>> getAllCompanyRoles() {
        List<CompanyRoleDTO> companyRoles = companyRoleService.getAllCompanyRoles();
        return ResponseEntity.ok(companyRoles);
    }
    
    /**
     * Get company role by ID.
     * GET /api/company-roles/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<CompanyRoleDTO> getCompanyRoleById(@PathVariable Long id) {
        CompanyRoleDTO companyRole = companyRoleService.getCompanyRoleById(id);
        return ResponseEntity.ok(companyRole);
    }
    
    /**
     * Create a new company role.
     * POST /api/company-roles
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CompanyRoleDTO> createCompanyRole(@Valid @RequestBody CompanyRoleDTO companyRoleDTO) {
        CompanyRoleDTO createdCompanyRole = companyRoleService.createCompanyRole(companyRoleDTO);
        return new ResponseEntity<>(createdCompanyRole, HttpStatus.CREATED);
    }
    
    /**
     * Update company role by ID.
     * PUT /api/company-roles/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CompanyRoleDTO> updateCompanyRole(
            @PathVariable Long id,
            @Valid @RequestBody CompanyRoleDTO companyRoleDTO) {
        CompanyRoleDTO updatedCompanyRole = companyRoleService.updateCompanyRole(id, companyRoleDTO);
        return ResponseEntity.ok(updatedCompanyRole);
    }
    
    /**
     * Delete company role by ID.
     * DELETE /api/company-roles/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCompanyRole(@PathVariable Long id) {
        companyRoleService.deleteCompanyRole(id);
        return ResponseEntity.noContent().build();
    }
}

