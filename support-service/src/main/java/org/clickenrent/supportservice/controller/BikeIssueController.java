package org.clickenrent.supportservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.supportservice.dto.BikeIssueDTO;
import org.clickenrent.supportservice.service.BikeIssueService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing BikeIssue entities.
 */
@RestController
@RequestMapping("/api/bike-issues")
@RequiredArgsConstructor
@Tag(name = "Bike Issue", description = "Bike issue management")
@SecurityRequirement(name = "bearerAuth")
public class BikeIssueController {

    private final BikeIssueService bikeIssueService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all bike issues")
    public ResponseEntity<List<BikeIssueDTO>> getAll() {
        return ResponseEntity.ok(bikeIssueService.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get bike issue by ID")
    public ResponseEntity<BikeIssueDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(bikeIssueService.getById(id));
    }

    @GetMapping("/external/{externalId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get bike issue by external ID")
    public ResponseEntity<BikeIssueDTO> getByExternalId(@PathVariable String externalId) {
        return ResponseEntity.ok(bikeIssueService.getByExternalId(externalId));
    }

    @GetMapping("/root")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get root bike issues (no parent)")
    public ResponseEntity<List<BikeIssueDTO>> getRootIssues() {
        return ResponseEntity.ok(bikeIssueService.getRootIssues());
    }

    @GetMapping("/parent/{parentId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get sub-issues by parent ID")
    public ResponseEntity<List<BikeIssueDTO>> getSubIssues(@PathVariable Long parentId) {
        return ResponseEntity.ok(bikeIssueService.getSubIssues(parentId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Create bike issue")
    public ResponseEntity<BikeIssueDTO> create(@Valid @RequestBody BikeIssueDTO dto) {
        return new ResponseEntity<>(bikeIssueService.create(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Update bike issue")
    public ResponseEntity<BikeIssueDTO> update(@PathVariable Long id, @Valid @RequestBody BikeIssueDTO dto) {
        return ResponseEntity.ok(bikeIssueService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Delete bike issue")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bikeIssueService.delete(id);
        return ResponseEntity.noContent().build();
    }
}








