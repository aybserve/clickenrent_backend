package org.clickenrent.rentalservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.BikeStatusDTO;
import org.clickenrent.rentalservice.service.BikeStatusService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bike-statuses")
@RequiredArgsConstructor
@Tag(name = "BikeStatus", description = "Bike status management (Available, Broken, Disabled, In use, Paused, Reserved)")
@SecurityRequirement(name = "bearerAuth")
public class BikeStatusController {

    private final BikeStatusService bikeStatusService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all bike statuses")
    public ResponseEntity<List<BikeStatusDTO>> getAllBikeStatuses() {
        return ResponseEntity.ok(bikeStatusService.getAllBikeStatuses());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get bike status by ID")
    public ResponseEntity<BikeStatusDTO> getBikeStatusById(@PathVariable Long id) {
        return ResponseEntity.ok(bikeStatusService.getBikeStatusById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Create bike status")
    public ResponseEntity<BikeStatusDTO> createBikeStatus(@Valid @RequestBody BikeStatusDTO dto) {
        return new ResponseEntity<>(bikeStatusService.createBikeStatus(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Update bike status")
    public ResponseEntity<BikeStatusDTO> updateBikeStatus(@PathVariable Long id, @Valid @RequestBody BikeStatusDTO dto) {
        return ResponseEntity.ok(bikeStatusService.updateBikeStatus(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Delete bike status")
    public ResponseEntity<Void> deleteBikeStatus(@PathVariable Long id) {
        bikeStatusService.deleteBikeStatus(id);
        return ResponseEntity.noContent().build();
    }
}







