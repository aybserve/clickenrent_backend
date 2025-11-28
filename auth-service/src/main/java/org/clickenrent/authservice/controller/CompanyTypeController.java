package org.clickenrent.authservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.authservice.dto.CompanyTypeDTO;
import org.clickenrent.authservice.service.CompanyTypeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for CompanyType management operations.
 */
@RestController
@RequestMapping("/api/company-types")
@RequiredArgsConstructor
public class CompanyTypeController {
    
    private final CompanyTypeService companyTypeService;
    
    /**
     * Get all company types.
     * GET /api/company-types
     */
    @GetMapping
    public ResponseEntity<List<CompanyTypeDTO>> getAllCompanyTypes() {
        List<CompanyTypeDTO> companyTypes = companyTypeService.getAllCompanyTypes();
        return ResponseEntity.ok(companyTypes);
    }
    
    /**
     * Get company type by ID.
     * GET /api/company-types/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<CompanyTypeDTO> getCompanyTypeById(@PathVariable Long id) {
        CompanyTypeDTO companyType = companyTypeService.getCompanyTypeById(id);
        return ResponseEntity.ok(companyType);
    }
    
    /**
     * Create a new company type.
     * POST /api/company-types
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CompanyTypeDTO> createCompanyType(@Valid @RequestBody CompanyTypeDTO companyTypeDTO) {
        CompanyTypeDTO createdCompanyType = companyTypeService.createCompanyType(companyTypeDTO);
        return new ResponseEntity<>(createdCompanyType, HttpStatus.CREATED);
    }
    
    /**
     * Update company type by ID.
     * PUT /api/company-types/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CompanyTypeDTO> updateCompanyType(
            @PathVariable Long id,
            @Valid @RequestBody CompanyTypeDTO companyTypeDTO) {
        CompanyTypeDTO updatedCompanyType = companyTypeService.updateCompanyType(id, companyTypeDTO);
        return ResponseEntity.ok(updatedCompanyType);
    }
    
    /**
     * Delete company type by ID.
     * DELETE /api/company-types/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCompanyType(@PathVariable Long id) {
        companyTypeService.deleteCompanyType(id);
        return ResponseEntity.noContent().build();
    }
}

