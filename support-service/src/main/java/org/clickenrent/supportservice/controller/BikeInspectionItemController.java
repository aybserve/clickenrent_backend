package org.clickenrent.supportservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.supportservice.dto.BikeInspectionItemDTO;
import org.clickenrent.supportservice.service.BikeInspectionItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing BikeInspectionItem entities.
 */
@RestController
@RequestMapping("/api/v1/bike-inspection-items")
@RequiredArgsConstructor
@Tag(name = "Bike Inspection Item", description = "Bike inspection item management")
@SecurityRequirement(name = "bearerAuth")
public class BikeInspectionItemController {

    private final BikeInspectionItemService bikeInspectionItemService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all bike inspection items")
    public ResponseEntity<List<BikeInspectionItemDTO>> getAll() {
        return ResponseEntity.ok(bikeInspectionItemService.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get bike inspection item by ID")
    public ResponseEntity<BikeInspectionItemDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(bikeInspectionItemService.getById(id));
    }

    @GetMapping("/external/{externalId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get bike inspection item by external ID")
    public ResponseEntity<BikeInspectionItemDTO> getByExternalId(@PathVariable String externalId) {
        return ResponseEntity.ok(bikeInspectionItemService.getByExternalId(externalId));
    }

    @GetMapping("/inspection/{bikeInspectionId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get bike inspection items by bike inspection ID")
    public ResponseEntity<List<BikeInspectionItemDTO>> getByBikeInspectionId(@PathVariable Long bikeInspectionId) {
        return ResponseEntity.ok(bikeInspectionItemService.getByBikeInspectionId(bikeInspectionId));
    }

    @GetMapping("/bike/{bikeExternalId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get bike inspection items by bike external ID")
    public ResponseEntity<List<BikeInspectionItemDTO>> getByBikeExternalId(@PathVariable String bikeExternalId) {
        return ResponseEntity.ok(bikeInspectionItemService.getByBikeExternalId(bikeExternalId));
    }

    @GetMapping("/company/{companyExternalId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get bike inspection items by company external ID")
    public ResponseEntity<List<BikeInspectionItemDTO>> getByCompanyExternalId(@PathVariable String companyExternalId) {
        return ResponseEntity.ok(bikeInspectionItemService.getByCompanyExternalId(companyExternalId));
    }

    @GetMapping("/status/{statusId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get bike inspection items by status ID")
    public ResponseEntity<List<BikeInspectionItemDTO>> getByStatusId(@PathVariable Long statusId) {
        return ResponseEntity.ok(bikeInspectionItemService.getByStatusId(statusId));
    }

    @GetMapping("/error-code/{errorCodeId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get bike inspection items by error code ID")
    public ResponseEntity<List<BikeInspectionItemDTO>> getByErrorCodeId(@PathVariable Long errorCodeId) {
        return ResponseEntity.ok(bikeInspectionItemService.getByErrorCodeId(errorCodeId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Create bike inspection item")
    public ResponseEntity<BikeInspectionItemDTO> create(@Valid @RequestBody BikeInspectionItemDTO dto) {
        return new ResponseEntity<>(bikeInspectionItemService.create(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Update bike inspection item")
    public ResponseEntity<BikeInspectionItemDTO> update(@PathVariable Long id, @Valid @RequestBody BikeInspectionItemDTO dto) {
        return ResponseEntity.ok(bikeInspectionItemService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Delete bike inspection item")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bikeInspectionItemService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
