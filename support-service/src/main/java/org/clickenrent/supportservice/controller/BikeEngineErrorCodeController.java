package org.clickenrent.supportservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.supportservice.dto.BikeEngineErrorCodeDTO;
import org.clickenrent.supportservice.service.BikeEngineErrorCodeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing BikeEngineErrorCode junction entities.
 */
@RestController
@RequestMapping("/api/v1/bike-engine-error-codes")
@RequiredArgsConstructor
@Tag(name = "Bike Engine Error Code", description = "Bike engine error code link management")
@SecurityRequirement(name = "bearerAuth")
public class BikeEngineErrorCodeController {

    private final BikeEngineErrorCodeService bikeEngineErrorCodeService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all bike engine error code links")
    public ResponseEntity<List<BikeEngineErrorCodeDTO>> getAll() {
        return ResponseEntity.ok(bikeEngineErrorCodeService.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get bike engine error code link by ID")
    public ResponseEntity<BikeEngineErrorCodeDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(bikeEngineErrorCodeService.getById(id));
    }

    @GetMapping("/external/{externalId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get bike engine error code link by external ID")
    public ResponseEntity<BikeEngineErrorCodeDTO> getByExternalId(@PathVariable String externalId) {
        return ResponseEntity.ok(bikeEngineErrorCodeService.getByExternalId(externalId));
    }

    @GetMapping("/bike-engine/{bikeEngineExternalId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get error codes by bike engine external ID")
    public ResponseEntity<List<BikeEngineErrorCodeDTO>> getByBikeEngineExternalId(@PathVariable String bikeEngineExternalId) {
        return ResponseEntity.ok(bikeEngineErrorCodeService.getByBikeEngineExternalId(bikeEngineExternalId));
    }

    @GetMapping("/error-code/{errorCodeId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get bike engines by error code ID")
    public ResponseEntity<List<BikeEngineErrorCodeDTO>> getByErrorCodeId(@PathVariable Long errorCodeId) {
        return ResponseEntity.ok(bikeEngineErrorCodeService.getByErrorCodeId(errorCodeId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Create bike engine error code link")
    public ResponseEntity<BikeEngineErrorCodeDTO> create(@Valid @RequestBody BikeEngineErrorCodeDTO dto) {
        return new ResponseEntity<>(bikeEngineErrorCodeService.create(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Update bike engine error code link")
    public ResponseEntity<BikeEngineErrorCodeDTO> update(@PathVariable Long id, @Valid @RequestBody BikeEngineErrorCodeDTO dto) {
        return ResponseEntity.ok(bikeEngineErrorCodeService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Delete bike engine error code link")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bikeEngineErrorCodeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
