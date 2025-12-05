package org.clickenrent.rentalservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.BikeRentalDTO;
import org.clickenrent.rentalservice.service.BikeRentalService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bike-rentals")
@RequiredArgsConstructor
@Tag(name = "BikeRental", description = "Bike rental management with revenue sharing")
@SecurityRequirement(name = "bearerAuth")
public class BikeRentalController {

    private final BikeRentalService bikeRentalService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all bike rentals")
    public ResponseEntity<Page<BikeRentalDTO>> getAllBikeRentals(
            @PageableDefault(size = 20, sort = "startDateTime", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(bikeRentalService.getAllBikeRentals(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get bike rental by ID")
    public ResponseEntity<BikeRentalDTO> getBikeRentalById(@PathVariable Long id) {
        return ResponseEntity.ok(bikeRentalService.getBikeRentalById(id));
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Create bike rental", description = "Creates bike rental and calculates revenue share if applicable")
    public ResponseEntity<BikeRentalDTO> createBikeRental(@Valid @RequestBody BikeRentalDTO dto) {
        return new ResponseEntity<>(bikeRentalService.createBikeRental(dto), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Delete bike rental")
    public ResponseEntity<Void> deleteBikeRental(@PathVariable Long id) {
        bikeRentalService.deleteBikeRental(id);
        return ResponseEntity.noContent().build();
    }
}
