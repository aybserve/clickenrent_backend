package org.clickenrent.supportservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.supportservice.dto.BikeInspectionItemBikeUnitDTO;
import org.clickenrent.supportservice.service.BikeInspectionItemBikeUnitService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing BikeInspectionItemBikeUnit junction entities.
 */
@RestController
@RequestMapping("/api/v1/bike-inspection-item-bike-units")
@RequiredArgsConstructor
@Tag(name = "Bike Inspection Item Bike Unit", description = "Bike inspection item bike unit link management")
@SecurityRequirement(name = "bearerAuth")
public class BikeInspectionItemBikeUnitController {

    private final BikeInspectionItemBikeUnitService bikeInspectionItemBikeUnitService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all bike inspection item bike unit links")
    public ResponseEntity<List<BikeInspectionItemBikeUnitDTO>> getAll() {
        return ResponseEntity.ok(bikeInspectionItemBikeUnitService.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get bike inspection item bike unit link by ID")
    public ResponseEntity<BikeInspectionItemBikeUnitDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(bikeInspectionItemBikeUnitService.getById(id));
    }

    @GetMapping("/external/{externalId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get bike inspection item bike unit link by external ID")
    public ResponseEntity<BikeInspectionItemBikeUnitDTO> getByExternalId(@PathVariable String externalId) {
        return ResponseEntity.ok(bikeInspectionItemBikeUnitService.getByExternalId(externalId));
    }

    @GetMapping("/inspection-item/{bikeInspectionItemId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get bike units by bike inspection item ID")
    public ResponseEntity<List<BikeInspectionItemBikeUnitDTO>> getByBikeInspectionItemId(@PathVariable Long bikeInspectionItemId) {
        return ResponseEntity.ok(bikeInspectionItemBikeUnitService.getByBikeInspectionItemId(bikeInspectionItemId));
    }

    @GetMapping("/bike-unit/{bikeUnitId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get bike inspection items by bike unit ID")
    public ResponseEntity<List<BikeInspectionItemBikeUnitDTO>> getByBikeUnitId(@PathVariable Long bikeUnitId) {
        return ResponseEntity.ok(bikeInspectionItemBikeUnitService.getByBikeUnitId(bikeUnitId));
    }

    @GetMapping("/company/{companyExternalId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get bike inspection item bike unit links by company external ID")
    public ResponseEntity<List<BikeInspectionItemBikeUnitDTO>> getByCompanyExternalId(@PathVariable String companyExternalId) {
        return ResponseEntity.ok(bikeInspectionItemBikeUnitService.getByCompanyExternalId(companyExternalId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Create bike inspection item bike unit link")
    public ResponseEntity<BikeInspectionItemBikeUnitDTO> create(@Valid @RequestBody BikeInspectionItemBikeUnitDTO dto) {
        return new ResponseEntity<>(bikeInspectionItemBikeUnitService.create(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Update bike inspection item bike unit link")
    public ResponseEntity<BikeInspectionItemBikeUnitDTO> update(@PathVariable Long id, @Valid @RequestBody BikeInspectionItemBikeUnitDTO dto) {
        return ResponseEntity.ok(bikeInspectionItemBikeUnitService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Delete bike inspection item bike unit link")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bikeInspectionItemBikeUnitService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
