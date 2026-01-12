package org.clickenrent.supportservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.supportservice.dto.BikeUnitDTO;
import org.clickenrent.supportservice.service.BikeUnitService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing BikeUnit entities.
 */
@RestController
@RequestMapping("/api/v1/bike-units")
@RequiredArgsConstructor
@Tag(name = "Bike Unit", description = "Bike unit management")
@SecurityRequirement(name = "bearerAuth")
public class BikeUnitController {

    private final BikeUnitService bikeUnitService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all bike units")
    public ResponseEntity<List<BikeUnitDTO>> getAll() {
        return ResponseEntity.ok(bikeUnitService.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get bike unit by ID")
    public ResponseEntity<BikeUnitDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(bikeUnitService.getById(id));
    }

    @GetMapping("/external/{externalId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get bike unit by external ID")
    public ResponseEntity<BikeUnitDTO> getByExternalId(@PathVariable String externalId) {
        return ResponseEntity.ok(bikeUnitService.getByExternalId(externalId));
    }

    @GetMapping("/company/{companyExternalId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get bike units by company external ID")
    public ResponseEntity<List<BikeUnitDTO>> getByCompanyExternalId(@PathVariable String companyExternalId) {
        return ResponseEntity.ok(bikeUnitService.getByCompanyExternalId(companyExternalId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Create bike unit")
    public ResponseEntity<BikeUnitDTO> create(@Valid @RequestBody BikeUnitDTO dto) {
        return new ResponseEntity<>(bikeUnitService.create(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Update bike unit")
    public ResponseEntity<BikeUnitDTO> update(@PathVariable Long id, @Valid @RequestBody BikeUnitDTO dto) {
        return ResponseEntity.ok(bikeUnitService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Delete bike unit")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bikeUnitService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
