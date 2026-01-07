package org.clickenrent.rentalservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.LocationRoleDTO;
import org.clickenrent.rentalservice.service.LocationRoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/location-roles")
@RequiredArgsConstructor
@Tag(name = "Location Role", description = "Location role management (Admin, Manager, Staff)")
@SecurityRequirement(name = "bearerAuth")
public class LocationRoleController {

    private final LocationRoleService locationRoleService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all location roles")
    public ResponseEntity<List<LocationRoleDTO>> getAllRoles() {
        return ResponseEntity.ok(locationRoleService.getAllRoles());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get role by ID")
    public ResponseEntity<LocationRoleDTO> getRoleById(@PathVariable Long id) {
        return ResponseEntity.ok(locationRoleService.getRoleById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new location role")
    public ResponseEntity<LocationRoleDTO> createLocationRole(@Valid @RequestBody LocationRoleDTO locationRoleDTO) {
        return ResponseEntity.status(201).body(locationRoleService.createLocationRole(locationRoleDTO));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update location role")
    public ResponseEntity<LocationRoleDTO> updateLocationRole(@PathVariable Long id, @Valid @RequestBody LocationRoleDTO locationRoleDTO) {
        return ResponseEntity.ok(locationRoleService.updateLocationRole(id, locationRoleDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete location role")
    public ResponseEntity<Void> deleteLocationRole(@PathVariable Long id) {
        locationRoleService.deleteLocationRole(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/external/{externalId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get location role by external ID", description = "Retrieve location role by external ID for cross-service communication")
    public ResponseEntity<LocationRoleDTO> getByExternalId(@PathVariable String externalId) {
        return ResponseEntity.ok(locationRoleService.findByExternalId(externalId));
    }
}
