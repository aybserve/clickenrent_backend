package org.clickenrent.rentalservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.RideStatusDTO;
import org.clickenrent.rentalservice.service.RideStatusService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ride-statuses")
@RequiredArgsConstructor
@Tag(name = "Ride Status", description = "Ride status management (Active, Finished)")
@SecurityRequirement(name = "bearerAuth")
public class RideStatusController {

    private final RideStatusService rideStatusService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all ride statuses")
    public ResponseEntity<List<RideStatusDTO>> getAllStatuses() {
        return ResponseEntity.ok(rideStatusService.getAllStatuses());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get status by ID")
    public ResponseEntity<RideStatusDTO> getStatusById(@PathVariable Long id) {
        return ResponseEntity.ok(rideStatusService.getStatusById(id));
    }

    @GetMapping("/external/{externalId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get ride status by external ID", description = "Retrieve status by external ID for cross-service communication")
    public ResponseEntity<RideStatusDTO> getByExternalId(@PathVariable String externalId) {
        return ResponseEntity.ok(rideStatusService.findByExternalId(externalId));
    }
}








