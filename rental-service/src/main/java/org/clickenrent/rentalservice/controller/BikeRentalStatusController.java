package org.clickenrent.rentalservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.BikeRentalStatusDTO;
import org.clickenrent.rentalservice.service.BikeRentalStatusService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bike-rental-statuses")
@RequiredArgsConstructor
@Tag(name = "Bike Rental Status", description = "Bike rental status management")
@SecurityRequirement(name = "bearerAuth")
public class BikeRentalStatusController {

    private final BikeRentalStatusService bikeRentalStatusService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all bike rental statuses")
    public ResponseEntity<List<BikeRentalStatusDTO>> getAllStatuses() {
        return ResponseEntity.ok(bikeRentalStatusService.getAllStatuses());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get status by ID")
    public ResponseEntity<BikeRentalStatusDTO> getStatusById(@PathVariable Long id) {
        return ResponseEntity.ok(bikeRentalStatusService.getStatusById(id));
    }

    @GetMapping("/external/{externalId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get bike rental status by external ID", description = "Retrieve status by external ID for cross-service communication")
    public ResponseEntity<BikeRentalStatusDTO> getByExternalId(@PathVariable String externalId) {
        return ResponseEntity.ok(bikeRentalStatusService.findByExternalId(externalId));
    }
}








