package org.clickenrent.rentalservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.RideDTO;
import org.clickenrent.rentalservice.service.RideService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rides")
@RequiredArgsConstructor
@Tag(name = "Ride", description = "Ride tracking and management")
@SecurityRequirement(name = "bearerAuth")
public class RideController {

    private final RideService rideService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Get all rides")
    public ResponseEntity<Page<RideDTO>> getAllRides(
            @PageableDefault(size = 20, sort = "startDateTime", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(rideService.getAllRides(pageable));
    }

    @GetMapping("/by-bike-rental/{bikeRentalId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get rides by bike rental")
    public ResponseEntity<List<RideDTO>> getRidesByBikeRental(@PathVariable Long bikeRentalId) {
        return ResponseEntity.ok(rideService.getRidesByBikeRental(bikeRentalId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get ride by ID")
    public ResponseEntity<RideDTO> getRideById(@PathVariable Long id) {
        return ResponseEntity.ok(rideService.getRideById(id));
    }

    @PostMapping("/start")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Start a new ride", description = "Starts a ride for a bike rental. Sets status to Active.")
    public ResponseEntity<RideDTO> startRide(@Valid @RequestBody RideDTO dto) {
        return new ResponseEntity<>(rideService.startRide(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}/end")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "End a ride", description = "Ends a ride and sets status to Finished")
    public ResponseEntity<RideDTO> endRide(@PathVariable Long id, @Valid @RequestBody RideDTO dto) {
        return ResponseEntity.ok(rideService.endRide(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Delete ride")
    public ResponseEntity<Void> deleteRide(@PathVariable Long id) {
        rideService.deleteRide(id);
        return ResponseEntity.noContent().build();
    }
}

