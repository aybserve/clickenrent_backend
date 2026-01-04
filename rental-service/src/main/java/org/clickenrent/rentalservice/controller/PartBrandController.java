package org.clickenrent.rentalservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.PartBrandDTO;
import org.clickenrent.rentalservice.service.PartBrandService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/part-brands")
@RequiredArgsConstructor
@Tag(name = "Part Brands", description = "Part brand management")
@SecurityRequirement(name = "bearerAuth")
public class PartBrandController {

    private final PartBrandService partBrandService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all part brands")
    public ResponseEntity<Page<PartBrandDTO>> getAllBrands(
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(partBrandService.getAllBrands(pageable));
    }

    @GetMapping("/by-company/{companyExternalId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get brands by company external ID")
    public ResponseEntity<List<PartBrandDTO>> getBrandsByCompanyExternalId(@PathVariable String companyExternalId) {
        return ResponseEntity.ok(partBrandService.getBrandsByCompanyExternalId(companyExternalId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get brand by ID")
    public ResponseEntity<PartBrandDTO> getBrandById(@PathVariable Long id) {
        return ResponseEntity.ok(partBrandService.getBrandById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(summary = "Create part brand")
    public ResponseEntity<PartBrandDTO> createBrand(@Valid @RequestBody PartBrandDTO dto) {
        return new ResponseEntity<>(partBrandService.createBrand(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(summary = "Update brand")
    public ResponseEntity<PartBrandDTO> updateBrand(@PathVariable Long id, @Valid @RequestBody PartBrandDTO dto) {
        return ResponseEntity.ok(partBrandService.updateBrand(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Delete brand")
    public ResponseEntity<Void> deleteBrand(@PathVariable Long id) {
        partBrandService.deleteBrand(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/external/{externalId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get part brand by external ID", description = "Retrieve part brand by external ID for cross-service communication")
    public ResponseEntity<PartBrandDTO> getByExternalId(@PathVariable String externalId) {
        return ResponseEntity.ok(partBrandService.getPartBrandByExternalId(externalId));
    }
}




