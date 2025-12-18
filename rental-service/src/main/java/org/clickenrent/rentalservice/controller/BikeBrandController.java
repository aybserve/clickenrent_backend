package org.clickenrent.rentalservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.BikeBrandDTO;
import org.clickenrent.rentalservice.service.BikeBrandService;
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
@RequestMapping("/api/bike-brands")
@RequiredArgsConstructor
@Tag(name = "BikeBrand", description = "Bike brand management")
@SecurityRequirement(name = "bearerAuth")
public class BikeBrandController {

    private final BikeBrandService bikeBrandService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all bike brands")
    public ResponseEntity<Page<BikeBrandDTO>> getAllBikeBrands(
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(bikeBrandService.getAllBikeBrands(pageable));
    }

    @GetMapping("/by-company/{companyId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get bike brands by company")
    public ResponseEntity<List<BikeBrandDTO>> getBikeBrandsByCompany(@PathVariable Long companyId) {
        return ResponseEntity.ok(bikeBrandService.getBikeBrandsByCompany(companyId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get bike brand by ID")
    public ResponseEntity<BikeBrandDTO> getBikeBrandById(@PathVariable Long id) {
        return ResponseEntity.ok(bikeBrandService.getBikeBrandById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(summary = "Create bike brand")
    public ResponseEntity<BikeBrandDTO> createBikeBrand(@Valid @RequestBody BikeBrandDTO dto) {
        return new ResponseEntity<>(bikeBrandService.createBikeBrand(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(summary = "Update bike brand")
    public ResponseEntity<BikeBrandDTO> updateBikeBrand(@PathVariable Long id, @Valid @RequestBody BikeBrandDTO dto) {
        return ResponseEntity.ok(bikeBrandService.updateBikeBrand(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Delete bike brand")
    public ResponseEntity<Void> deleteBikeBrand(@PathVariable Long id) {
        bikeBrandService.deleteBikeBrand(id);
        return ResponseEntity.noContent().build();
    }
}


