package org.clickenrent.authservice.controller;

import lombok.RequiredArgsConstructor;
import org.clickenrent.authservice.dto.UserCompanyDTO;
import org.clickenrent.authservice.service.UserCompanyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for UserCompany relationship management.
 */
@RestController
@RequestMapping("/api/user-companies")
@RequiredArgsConstructor
public class UserCompanyController {
    
    private final UserCompanyService userCompanyService;
    
    /**
     * Assign a user to a company with a specific role.
     * POST /api/user-companies
     */
    @PostMapping
    public ResponseEntity<UserCompanyDTO> assignUserToCompany(
            @RequestParam Long userId,
            @RequestParam Long companyId,
            @RequestParam Long companyRoleId) {
        UserCompanyDTO userCompany = userCompanyService.assignUserToCompany(userId, companyId, companyRoleId);
        return new ResponseEntity<>(userCompany, HttpStatus.CREATED);
    }
    
    /**
     * Get all companies for a specific user.
     * GET /api/user-companies/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserCompanyDTO>> getUserCompanies(@PathVariable Long userId) {
        List<UserCompanyDTO> userCompanies = userCompanyService.getUserCompanies(userId);
        return ResponseEntity.ok(userCompanies);
    }
    
    /**
     * Get all users in a specific company.
     * GET /api/user-companies/company/{companyId}
     */
    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<UserCompanyDTO>> getCompanyUsers(@PathVariable Long companyId) {
        List<UserCompanyDTO> companyUsers = userCompanyService.getCompanyUsers(companyId);
        return ResponseEntity.ok(companyUsers);
    }
    
    /**
     * Update user's role in a company.
     * PUT /api/user-companies/{id}/role
     */
    @PutMapping("/{id}/role")
    public ResponseEntity<UserCompanyDTO> updateUserCompanyRole(
            @PathVariable Long id,
            @RequestParam Long companyRoleId) {
        UserCompanyDTO userCompany = userCompanyService.updateUserCompanyRole(id, companyRoleId);
        return ResponseEntity.ok(userCompany);
    }
    
    /**
     * Remove user from company.
     * DELETE /api/user-companies/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeUserFromCompany(@PathVariable Long id) {
        userCompanyService.removeUserFromCompany(id);
        return ResponseEntity.noContent().build();
    }
}

