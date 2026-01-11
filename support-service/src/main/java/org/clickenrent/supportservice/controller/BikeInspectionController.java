package org.clickenrent.supportservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.supportservice.dto.BikeInspectionDTO;
import org.clickenrent.supportservice.service.BikeInspectionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing BikeInspection entities.
 */
@RestController
@RequestMapping("/api/v1/bike-inspections")
@RequiredArgsConstructor
@Tag(name = "Bike Inspection", description = "Bike inspection management")
@SecurityRequirement(name = "bearerAuth")
public class BikeInspectionController {

    private final BikeInspectionService bikeInspectionService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all bike inspections")
    public ResponseEntity<List<BikeInspectionDTO>> getAll() {
        return ResponseEntity.ok(bikeInspectionService.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get bike inspection by ID")
    public ResponseEntity<BikeInspectionDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(bikeInspectionService.getById(id));
    }

    @GetMapping("/external/{externalId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get bike inspection by external ID")
    public ResponseEntity<BikeInspectionDTO> getByExternalId(@PathVariable String externalId) {
        return ResponseEntity.ok(bikeInspectionService.getByExternalId(externalId));
    }

    @GetMapping("/user/{userExternalId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get bike inspections by user external ID")
    public ResponseEntity<List<BikeInspectionDTO>> getByUserExternalId(@PathVariable String userExternalId) {
        return ResponseEntity.ok(bikeInspectionService.getByUserExternalId(userExternalId));
    }

    @GetMapping("/company/{companyExternalId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get bike inspections by company external ID")
    public ResponseEntity<List<BikeInspectionDTO>> getByCompanyExternalId(@PathVariable String companyExternalId) {
        return ResponseEntity.ok(bikeInspectionService.getByCompanyExternalId(companyExternalId));
    }

    @GetMapping("/status/{statusId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get bike inspections by status ID")
    public ResponseEntity<List<BikeInspectionDTO>> getByStatusId(@PathVariable Long statusId) {
        return ResponseEntity.ok(bikeInspectionService.getByStatusId(statusId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Create bike inspection")
    public ResponseEntity<BikeInspectionDTO> create(@Valid @RequestBody BikeInspectionDTO dto) {
        return new ResponseEntity<>(bikeInspectionService.create(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Update bike inspection")
    public ResponseEntity<BikeInspectionDTO> update(@PathVariable Long id, @Valid @RequestBody BikeInspectionDTO dto) {
        return ResponseEntity.ok(bikeInspectionService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Delete bike inspection")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bikeInspectionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
