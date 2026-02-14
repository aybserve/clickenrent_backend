package org.clickenrent.supportservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.supportservice.dto.BikeInspectionItemPhotoDTO;
import org.clickenrent.supportservice.service.BikeInspectionItemPhotoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing BikeInspectionItemPhoto entities.
 */
@RestController
@RequestMapping("/api/v1/bike-inspection-item-photos")
@RequiredArgsConstructor
@Tag(name = "Bike Inspection Item Photo", description = "Bike inspection item photo management")
@SecurityRequirement(name = "bearerAuth")
public class BikeInspectionItemPhotoController {

    private final BikeInspectionItemPhotoService bikeInspectionItemPhotoService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all bike inspection item photos")
    public ResponseEntity<List<BikeInspectionItemPhotoDTO>> getAll() {
        return ResponseEntity.ok(bikeInspectionItemPhotoService.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get bike inspection item photo by ID")
    public ResponseEntity<BikeInspectionItemPhotoDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(bikeInspectionItemPhotoService.getById(id));
    }

    @GetMapping("/inspection-item/{bikeInspectionItemId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get bike inspection item photos by bike inspection item ID")
    public ResponseEntity<List<BikeInspectionItemPhotoDTO>> getByBikeInspectionItemId(@PathVariable Long bikeInspectionItemId) {
        return ResponseEntity.ok(bikeInspectionItemPhotoService.getByBikeInspectionItemId(bikeInspectionItemId));
    }

    @GetMapping("/company/{companyExternalId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get bike inspection item photos by company external ID")
    public ResponseEntity<List<BikeInspectionItemPhotoDTO>> getByCompanyExternalId(@PathVariable String companyExternalId) {
        return ResponseEntity.ok(bikeInspectionItemPhotoService.getByCompanyExternalId(companyExternalId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Create bike inspection item photo")
    public ResponseEntity<BikeInspectionItemPhotoDTO> create(@Valid @RequestBody BikeInspectionItemPhotoDTO dto) {
        return new ResponseEntity<>(bikeInspectionItemPhotoService.create(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Update bike inspection item photo")
    public ResponseEntity<BikeInspectionItemPhotoDTO> update(@PathVariable Long id, @Valid @RequestBody BikeInspectionItemPhotoDTO dto) {
        return ResponseEntity.ok(bikeInspectionItemPhotoService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Delete bike inspection item photo")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bikeInspectionItemPhotoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
