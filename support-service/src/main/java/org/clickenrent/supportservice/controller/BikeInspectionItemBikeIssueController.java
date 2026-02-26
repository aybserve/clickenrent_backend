package org.clickenrent.supportservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.supportservice.dto.BikeInspectionItemBikeIssueDTO;
import org.clickenrent.supportservice.service.BikeInspectionItemBikeIssueService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing BikeInspectionItemBikeIssue junction entities.
 */
@RestController
@RequestMapping("/api/v1/bike-inspection-item-bike-issues")
@RequiredArgsConstructor
@Tag(name = "Bike Inspection Item Bike Issue", description = "Bike inspection item bike issue link management")
@SecurityRequirement(name = "bearerAuth")
public class BikeInspectionItemBikeIssueController {

    private final BikeInspectionItemBikeIssueService bikeInspectionItemBikeIssueService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all bike inspection item bike issue links")
    public ResponseEntity<List<BikeInspectionItemBikeIssueDTO>> getAll() {
        return ResponseEntity.ok(bikeInspectionItemBikeIssueService.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get bike inspection item bike issue link by ID")
    public ResponseEntity<BikeInspectionItemBikeIssueDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(bikeInspectionItemBikeIssueService.getById(id));
    }

    @GetMapping("/external/{externalId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get bike inspection item bike issue link by external ID")
    public ResponseEntity<BikeInspectionItemBikeIssueDTO> getByExternalId(@PathVariable String externalId) {
        return ResponseEntity.ok(bikeInspectionItemBikeIssueService.getByExternalId(externalId));
    }

    @GetMapping("/inspection-item/{bikeInspectionItemId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get bike issues by bike inspection item ID")
    public ResponseEntity<List<BikeInspectionItemBikeIssueDTO>> getByBikeInspectionItemId(@PathVariable Long bikeInspectionItemId) {
        return ResponseEntity.ok(bikeInspectionItemBikeIssueService.getByBikeInspectionItemId(bikeInspectionItemId));
    }

    @GetMapping("/bike-issue/{bikeIssueId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get bike inspection items by bike issue ID")
    public ResponseEntity<List<BikeInspectionItemBikeIssueDTO>> getByBikeIssueId(@PathVariable Long bikeIssueId) {
        return ResponseEntity.ok(bikeInspectionItemBikeIssueService.getByBikeIssueId(bikeIssueId));
    }

    @GetMapping("/company/{companyExternalId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get bike inspection item bike issue links by company external ID")
    public ResponseEntity<List<BikeInspectionItemBikeIssueDTO>> getByCompanyExternalId(@PathVariable String companyExternalId) {
        return ResponseEntity.ok(bikeInspectionItemBikeIssueService.getByCompanyExternalId(companyExternalId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Create bike inspection item bike issue link")
    public ResponseEntity<BikeInspectionItemBikeIssueDTO> create(@Valid @RequestBody BikeInspectionItemBikeIssueDTO dto) {
        return new ResponseEntity<>(bikeInspectionItemBikeIssueService.create(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Update bike inspection item bike issue link")
    public ResponseEntity<BikeInspectionItemBikeIssueDTO> update(@PathVariable Long id, @Valid @RequestBody BikeInspectionItemBikeIssueDTO dto) {
        return ResponseEntity.ok(bikeInspectionItemBikeIssueService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Delete bike inspection item bike issue link")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bikeInspectionItemBikeIssueService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
