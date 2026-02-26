package org.clickenrent.supportservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.supportservice.dto.BikeInspectionStatusDTO;
import org.clickenrent.supportservice.service.BikeInspectionStatusService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing BikeInspectionStatus entities.
 */
@RestController
@RequestMapping("/api/v1/bike-inspection-statuses")
@RequiredArgsConstructor
@Tag(name = "Bike Inspection Status", description = "Bike inspection status management")
@SecurityRequirement(name = "bearerAuth")
public class BikeInspectionStatusController {

    private final BikeInspectionStatusService bikeInspectionStatusService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all bike inspection statuses")
    public ResponseEntity<List<BikeInspectionStatusDTO>> getAll() {
        return ResponseEntity.ok(bikeInspectionStatusService.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get bike inspection status by ID")
    public ResponseEntity<BikeInspectionStatusDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(bikeInspectionStatusService.getById(id));
    }

    @GetMapping("/external/{externalId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get bike inspection status by external ID")
    public ResponseEntity<BikeInspectionStatusDTO> getByExternalId(@PathVariable String externalId) {
        return ResponseEntity.ok(bikeInspectionStatusService.getByExternalId(externalId));
    }

    @GetMapping("/name/{name}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get bike inspection status by name")
    public ResponseEntity<BikeInspectionStatusDTO> getByName(@PathVariable String name) {
        return ResponseEntity.ok(bikeInspectionStatusService.getByName(name));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Create bike inspection status")
    public ResponseEntity<BikeInspectionStatusDTO> create(@Valid @RequestBody BikeInspectionStatusDTO dto) {
        return new ResponseEntity<>(bikeInspectionStatusService.create(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Update bike inspection status")
    public ResponseEntity<BikeInspectionStatusDTO> update(@PathVariable Long id, @Valid @RequestBody BikeInspectionStatusDTO dto) {
        return ResponseEntity.ok(bikeInspectionStatusService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Delete bike inspection status")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bikeInspectionStatusService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
