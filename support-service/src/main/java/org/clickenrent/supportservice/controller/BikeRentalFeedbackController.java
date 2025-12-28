package org.clickenrent.supportservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.supportservice.dto.BikeRentalFeedbackDTO;
import org.clickenrent.supportservice.service.BikeRentalFeedbackService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing BikeRentalFeedback entities.
 */
@RestController
@RequestMapping("/api/bike-rental-feedbacks")
@RequiredArgsConstructor
@Tag(name = "Bike Rental Feedback", description = "Bike rental feedback management")
@SecurityRequirement(name = "bearerAuth")
public class BikeRentalFeedbackController {

    private final BikeRentalFeedbackService bikeRentalFeedbackService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all bike rental feedbacks (or user's own feedbacks)")
    public ResponseEntity<List<BikeRentalFeedbackDTO>> getAll() {
        return ResponseEntity.ok(bikeRentalFeedbackService.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get bike rental feedback by ID")
    public ResponseEntity<BikeRentalFeedbackDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(bikeRentalFeedbackService.getById(id));
    }

    @GetMapping("/bike-rental/{bikeRentalExternalId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get bike rental feedback by bike rental external ID")
    public ResponseEntity<BikeRentalFeedbackDTO> getByBikeRentalExternalId(@PathVariable String bikeRentalExternalId) {
        return ResponseEntity.ok(bikeRentalFeedbackService.getByBikeRentalExternalId(bikeRentalExternalId));
    }

    @GetMapping("/user/{userExternalId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get bike rental feedbacks by user external ID")
    public ResponseEntity<List<BikeRentalFeedbackDTO>> getByUserExternalId(@PathVariable String userExternalId) {
        return ResponseEntity.ok(bikeRentalFeedbackService.getByUserExternalId(userExternalId));
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Create bike rental feedback")
    public ResponseEntity<BikeRentalFeedbackDTO> create(@Valid @RequestBody BikeRentalFeedbackDTO dto) {
        return new ResponseEntity<>(bikeRentalFeedbackService.create(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Update bike rental feedback")
    public ResponseEntity<BikeRentalFeedbackDTO> update(@PathVariable Long id, @Valid @RequestBody BikeRentalFeedbackDTO dto) {
        return ResponseEntity.ok(bikeRentalFeedbackService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Delete bike rental feedback")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bikeRentalFeedbackService.delete(id);
        return ResponseEntity.noContent().build();
    }
}








