package org.clickenrent.rentalservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.*;
import org.clickenrent.rentalservice.service.BikeRentalService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/bike-rentals")
@RequiredArgsConstructor
@Tag(name = "Bike Rental", description = "Bike rental management with revenue sharing")
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

    @GetMapping("/{id}/exists")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Check if bike rental exists by ID")
    public ResponseEntity<Boolean> checkBikeRentalExists(@PathVariable Long id) {
        boolean exists = bikeRentalService.getBikeRentalById(id) != null;
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/external/{externalId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get bike rental by external ID")
    public ResponseEntity<BikeRentalDTO> getByExternalId(@PathVariable String externalId) {
        return ResponseEntity.ok(bikeRentalService.findByExternalId(externalId));
    }

    @PutMapping("/external/{externalId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Update bike rental by external ID")
    public ResponseEntity<BikeRentalDTO> updateByExternalId(
            @PathVariable String externalId,
            @Valid @RequestBody BikeRentalDTO dto) {
        return ResponseEntity.ok(bikeRentalService.updateByExternalId(externalId, dto));
    }

    @DeleteMapping("/external/{externalId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Delete bike rental by external ID")
    public ResponseEntity<Void> deleteByExternalId(@PathVariable String externalId) {
        bikeRentalService.deleteByExternalId(externalId);
        return ResponseEntity.noContent().build();
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

    @PostMapping("/{id}/unlock")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Unlock bike",
            description = "Generate unlock token for BLE communication. User must have an active rental for this bike."
    )
    public ResponseEntity<UnlockResponseDTO> unlockBike(
            @PathVariable Long id,
            @Valid @RequestBody UnlockRequestDTO request) {
        UnlockResponseDTO response = bikeRentalService.unlockBike(id, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/lock")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Lock bike",
            description = "Confirm bike is locked and update bike rental status. Updates bike location if coordinates provided."
    )
    public ResponseEntity<LockResponseDTO> lockBike(
            @PathVariable Long id,
            @Valid @RequestBody LockRequestDTO request) {
        LockResponseDTO response = bikeRentalService.lockBike(id, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/photo")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Upload photo for bike rental",
            description = "Upload a photo for a completed bike rental. Only one photo per rental allowed. " +
                         "Photo must be JPEG or PNG format and not exceed 5 MB."
    )
    public ResponseEntity<PhotoUploadResponseDTO> uploadPhoto(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        PhotoUploadResponseDTO response = bikeRentalService.uploadPhoto(id, file);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/rental/{rentalId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get bike rentals by rental ID", description = "Retrieve all bike rentals for a specific rental")
    public ResponseEntity<List<BikeRentalDTO>> getBikeRentalsByRentalId(@PathVariable Long rentalId) {
        return ResponseEntity.ok(bikeRentalService.getBikeRentalsByRentalId(rentalId));
    }

    @GetMapping("/rental/external/{rentalExternalId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get bike rentals by rental external ID", description = "Retrieve all bike rentals for a specific rental by external ID")
    public ResponseEntity<List<BikeRentalDTO>> getBikeRentalsByRentalExternalId(@PathVariable String rentalExternalId) {
        return ResponseEntity.ok(bikeRentalService.getBikeRentalsByRentalExternalId(rentalExternalId));
    }
}
