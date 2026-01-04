package org.clickenrent.rentalservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.ChargingStationBrandDTO;
import org.clickenrent.rentalservice.service.ChargingStationBrandService;
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
@RequestMapping("/api/charging-station-brands")
@RequiredArgsConstructor
@Tag(name = "ChargingStationBrand", description = "Charging station brand management")
@SecurityRequirement(name = "bearerAuth")
public class ChargingStationBrandController {

    private final ChargingStationBrandService chargingStationBrandService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all charging station brands")
    public ResponseEntity<Page<ChargingStationBrandDTO>> getAllBrands(
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(chargingStationBrandService.getAllBrands(pageable));
    }

    @GetMapping("/by-company/{companyExternalId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get brands by company external ID")
    public ResponseEntity<List<ChargingStationBrandDTO>> getBrandsByCompanyExternalId(@PathVariable String companyExternalId) {
        return ResponseEntity.ok(chargingStationBrandService.getBrandsByCompanyExternalId(companyExternalId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get brand by ID")
    public ResponseEntity<ChargingStationBrandDTO> getBrandById(@PathVariable Long id) {
        return ResponseEntity.ok(chargingStationBrandService.getBrandById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(summary = "Create charging station brand")
    public ResponseEntity<ChargingStationBrandDTO> createBrand(@Valid @RequestBody ChargingStationBrandDTO dto) {
        return new ResponseEntity<>(chargingStationBrandService.createBrand(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(summary = "Update brand")
    public ResponseEntity<ChargingStationBrandDTO> updateBrand(@PathVariable Long id, @Valid @RequestBody ChargingStationBrandDTO dto) {
        return ResponseEntity.ok(chargingStationBrandService.updateBrand(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Delete brand")
    public ResponseEntity<Void> deleteBrand(@PathVariable Long id) {
        chargingStationBrandService.deleteBrand(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/external/{externalId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get charging station brand by external ID", description = "Retrieve brand by external ID for cross-service communication")
    public ResponseEntity<ChargingStationBrandDTO> getByExternalId(@PathVariable String externalId) {
        return ResponseEntity.ok(chargingStationBrandService.getChargingStationBrandByExternalId(externalId));
    }
}




