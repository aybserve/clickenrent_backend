package org.clickenrent.supportservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.supportservice.dto.BikeTypeBikeIssueDTO;
import org.clickenrent.supportservice.service.BikeTypeBikeIssueService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing BikeTypeBikeIssue junction entities.
 */
@RestController
@RequestMapping("/api/bike-type-bike-issues")
@RequiredArgsConstructor
@Tag(name = "Bike Type Bike Issue", description = "Bike type bike issue link management")
@SecurityRequirement(name = "bearerAuth")
public class BikeTypeBikeIssueController {

    private final BikeTypeBikeIssueService bikeTypeBikeIssueService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all bike type bike issue links")
    public ResponseEntity<List<BikeTypeBikeIssueDTO>> getAll() {
        return ResponseEntity.ok(bikeTypeBikeIssueService.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get bike type bike issue link by ID")
    public ResponseEntity<BikeTypeBikeIssueDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(bikeTypeBikeIssueService.getById(id));
    }

    @GetMapping("/bike-type/{bikeTypeExternalId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get bike issues by bike type external ID")
    public ResponseEntity<List<BikeTypeBikeIssueDTO>> getByBikeTypeExternalId(@PathVariable String bikeTypeExternalId) {
        return ResponseEntity.ok(bikeTypeBikeIssueService.getByBikeTypeExternalId(bikeTypeExternalId));
    }

    @GetMapping("/bike-issue/{bikeIssueId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get bike types by bike issue ID")
    public ResponseEntity<List<BikeTypeBikeIssueDTO>> getByBikeIssueId(@PathVariable Long bikeIssueId) {
        return ResponseEntity.ok(bikeTypeBikeIssueService.getByBikeIssueId(bikeIssueId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Create bike type bike issue link")
    public ResponseEntity<BikeTypeBikeIssueDTO> create(@Valid @RequestBody BikeTypeBikeIssueDTO dto) {
        return new ResponseEntity<>(bikeTypeBikeIssueService.create(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Update bike type bike issue link")
    public ResponseEntity<BikeTypeBikeIssueDTO> update(@PathVariable Long id, @Valid @RequestBody BikeTypeBikeIssueDTO dto) {
        return ResponseEntity.ok(bikeTypeBikeIssueService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Delete bike type bike issue link")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bikeTypeBikeIssueService.delete(id);
        return ResponseEntity.noContent().build();
    }
}








