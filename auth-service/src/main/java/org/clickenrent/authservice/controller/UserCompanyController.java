package org.clickenrent.authservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.authservice.dto.AssignUserToCompanyRequest;
import org.clickenrent.authservice.dto.UpdateUserCompanyRoleRequest;
import org.clickenrent.authservice.dto.UserCompanyDTO;
import org.clickenrent.authservice.dto.UserCompanyDetailDTO;
import org.clickenrent.authservice.service.UserCompanyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for UserCompany relationship management.
 * 
 * Security Rules:
 * - SUPERADMIN/ADMIN: Full access to all operations
 * - B2B: Can view users in their companies only
 * - CUSTOMER: Can only view their own company associations
 */
@RestController
@RequestMapping("/api/user-companies")
@RequiredArgsConstructor
public class UserCompanyController {
    
    private final UserCompanyService userCompanyService;
    
    /**
     * Assign a user to a company with a specific role.
     * POST /api/user-companies
     * Requires: SUPERADMIN or ADMIN role
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<UserCompanyDTO> assignUserToCompany(
            @Valid @RequestBody AssignUserToCompanyRequest request) {
        UserCompanyDTO userCompany = userCompanyService.assignUserToCompany(
                request.getUserId(), 
                request.getCompanyId(), 
                request.getCompanyRoleId()
        );
        return new ResponseEntity<>(userCompany, HttpStatus.CREATED);
    }
    
    /**
     * Get all company associations for a specific user with full details.
     * GET /api/user-companies/user/{userId}
     * Requires: Authentication
     * - SUPERADMIN/ADMIN: Can view any user's company associations
     * - B2B/CUSTOMER: Can only view their own associations
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<UserCompanyDetailDTO>> getUserCompanies(@PathVariable Long userId) {
        List<UserCompanyDetailDTO> userCompanies = userCompanyService.getUserCompanies(userId);
        return ResponseEntity.ok(userCompanies);
    }
    
    /**
     * Get all users in a specific company with full details.
     * GET /api/user-companies/company/{companyId}
     * Requires: Authentication
     * - SUPERADMIN/ADMIN: Can view users in any company
     * - B2B: Can only view users in their own companies
     * - CUSTOMER: Access denied
     */
    @GetMapping("/company/{companyId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<UserCompanyDetailDTO>> getCompanyUsers(@PathVariable Long companyId) {
        List<UserCompanyDetailDTO> companyUsers = userCompanyService.getCompanyUsers(companyId);
        return ResponseEntity.ok(companyUsers);
    }
    
    /**
     * Update user's role in a company.
     * PUT /api/user-companies/{id}/role
     * Requires: SUPERADMIN or ADMIN role
     */
    @PutMapping("/{id}/role")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<UserCompanyDTO> updateUserCompanyRole(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserCompanyRoleRequest request) {
        UserCompanyDTO userCompany = userCompanyService.updateUserCompanyRole(id, request.getCompanyRoleId());
        return ResponseEntity.ok(userCompany);
    }
    
    /**
     * Remove user from company.
     * DELETE /api/user-companies/{id}
     * Requires: SUPERADMIN or ADMIN role
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<Void> removeUserFromCompany(@PathVariable Long id) {
        userCompanyService.removeUserFromCompany(id);
        return ResponseEntity.noContent().build();
    }
}

