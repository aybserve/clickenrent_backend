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
import org.clickenrent.authservice.dto.CompanyDTO;
import org.clickenrent.authservice.service.CompanyService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for Company management operations.
 * 
 * Security Rules:
 * - SUPERADMIN/ADMIN: Can view and manage all companies
 * - B2B: Can view and update companies they belong to
 * - CUSTOMER: No access to companies
 */
@RestController
@RequestMapping("/api/v1/companies")
@RequiredArgsConstructor
@Tag(name = "Company", description = "Company management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class CompanyController {
    
    private final CompanyService companyService;
    
    /**
     * Get all companies with pagination.
     * GET /api/companies
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(
            summary = "Get all companies",
            description = "Returns a paginated list of all companies. Requires SUPERADMIN or ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Companies retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<Page<CompanyDTO>> getAllCompanies(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<CompanyDTO> companies = companyService.getAllCompanies(pageable);
        return ResponseEntity.ok(companies);
    }
    
    /**
     * Get company by ID.
     * GET /api/companies/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN') or @resourceSecurity.canAccessCompany(#id)")
    @Operation(
            summary = "Get company by ID",
            description = "Returns company details by company ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Company found",
                    content = @Content(schema = @Schema(implementation = CompanyDTO.class))),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Company not found")
    })
    public ResponseEntity<CompanyDTO> getCompanyById(
            @Parameter(description = "Company ID", required = true) @PathVariable Long id) {
        CompanyDTO company = companyService.getCompanyById(id);
        return ResponseEntity.ok(company);
    }
    
    /**
     * Get company by external ID.
     * GET /api/companies/external/{externalId}
     */
    @GetMapping("/external/{externalId}")
    @Operation(summary = "Get company by external ID", description = "Retrieve company details by external ID for cross-service communication (public for service-to-service calls)")
    public ResponseEntity<CompanyDTO> getCompanyByExternalId(@PathVariable String externalId) {
        CompanyDTO company = companyService.findByExternalId(externalId);
        return ResponseEntity.ok(company);
    }
    
    /**
     * Create a new company.
     * POST /api/companies
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(
            summary = "Create a new company",
            description = "Creates a new company. Requires SUPERADMIN or ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Company created successfully",
                    content = @Content(schema = @Schema(implementation = CompanyDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<CompanyDTO> createCompany(@Valid @RequestBody CompanyDTO companyDTO) {
        CompanyDTO createdCompany = companyService.createCompany(companyDTO);
        return new ResponseEntity<>(createdCompany, HttpStatus.CREATED);
    }
    
    /**
     * Update company by ID.
     * PUT /api/companies/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN') or @resourceSecurity.canAccessCompany(#id)")
    @Operation(
            summary = "Update company",
            description = "Updates company information by ID. Requires SUPERADMIN or ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Company updated successfully",
                    content = @Content(schema = @Schema(implementation = CompanyDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Company not found")
    })
    public ResponseEntity<CompanyDTO> updateCompany(
            @Parameter(description = "Company ID", required = true) @PathVariable Long id,
            @Valid @RequestBody CompanyDTO companyDTO) {
        CompanyDTO updatedCompany = companyService.updateCompany(id, companyDTO);
        return ResponseEntity.ok(updatedCompany);
    }
    
    /**
     * Delete company by ID.
     * DELETE /api/companies/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(
            summary = "Delete company",
            description = "Deletes a company by ID. Requires SUPERADMIN or ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Company deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Company not found")
    })
    public ResponseEntity<Void> deleteCompany(
            @Parameter(description = "Company ID", required = true) @PathVariable Long id) {
        companyService.deleteCompany(id);
        return ResponseEntity.noContent().build();
    }
}

