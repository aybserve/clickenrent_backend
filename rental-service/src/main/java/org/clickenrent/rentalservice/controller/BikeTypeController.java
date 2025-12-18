package org.clickenrent.rentalservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.BikeTypeDTO;
import org.clickenrent.rentalservice.service.BikeTypeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bike-types")
@RequiredArgsConstructor
@Tag(name = "BikeType", description = "Bike type management (Electric bike, Non-electric bike)")
@SecurityRequirement(name = "bearerAuth")
public class BikeTypeController {

    private final BikeTypeService bikeTypeService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all bike types")
    public ResponseEntity<List<BikeTypeDTO>> getAllBikeTypes() {
        return ResponseEntity.ok(bikeTypeService.getAllBikeTypes());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get bike type by ID")
    public ResponseEntity<BikeTypeDTO> getBikeTypeById(@PathVariable Long id) {
        return ResponseEntity.ok(bikeTypeService.getBikeTypeById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Create bike type")
    public ResponseEntity<BikeTypeDTO> createBikeType(@Valid @RequestBody BikeTypeDTO dto) {
        return new ResponseEntity<>(bikeTypeService.createBikeType(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Update bike type")
    public ResponseEntity<BikeTypeDTO> updateBikeType(@PathVariable Long id, @Valid @RequestBody BikeTypeDTO dto) {
        return ResponseEntity.ok(bikeTypeService.updateBikeType(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Delete bike type")
    public ResponseEntity<Void> deleteBikeType(@PathVariable Long id) {
        bikeTypeService.deleteBikeType(id);
        return ResponseEntity.noContent().build();
    }
}


