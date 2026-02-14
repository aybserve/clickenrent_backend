package org.clickenrent.supportservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.supportservice.dto.BikeInspectionItemStatusDTO;
import org.clickenrent.supportservice.service.BikeInspectionItemStatusService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing BikeInspectionItemStatus entities.
 */
@RestController
@RequestMapping("/api/v1/bike-inspection-item-statuses")
@RequiredArgsConstructor
@Tag(name = "Bike Inspection Item Status", description = "Bike inspection item status management")
@SecurityRequirement(name = "bearerAuth")
public class BikeInspectionItemStatusController {

    private final BikeInspectionItemStatusService bikeInspectionItemStatusService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all bike inspection item statuses")
    public ResponseEntity<List<BikeInspectionItemStatusDTO>> getAll() {
        return ResponseEntity.ok(bikeInspectionItemStatusService.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get bike inspection item status by ID")
    public ResponseEntity<BikeInspectionItemStatusDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(bikeInspectionItemStatusService.getById(id));
    }

    @GetMapping("/external/{externalId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get bike inspection item status by external ID")
    public ResponseEntity<BikeInspectionItemStatusDTO> getByExternalId(@PathVariable String externalId) {
        return ResponseEntity.ok(bikeInspectionItemStatusService.getByExternalId(externalId));
    }

    @GetMapping("/name/{name}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get bike inspection item status by name")
    public ResponseEntity<BikeInspectionItemStatusDTO> getByName(@PathVariable String name) {
        return ResponseEntity.ok(bikeInspectionItemStatusService.getByName(name));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Create bike inspection item status")
    public ResponseEntity<BikeInspectionItemStatusDTO> create(@Valid @RequestBody BikeInspectionItemStatusDTO dto) {
        return new ResponseEntity<>(bikeInspectionItemStatusService.create(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Update bike inspection item status")
    public ResponseEntity<BikeInspectionItemStatusDTO> update(@PathVariable Long id, @Valid @RequestBody BikeInspectionItemStatusDTO dto) {
        return ResponseEntity.ok(bikeInspectionItemStatusService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Delete bike inspection item status")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bikeInspectionItemStatusService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
