package org.clickenrent.rentalservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.LocationRoleDTO;
import org.clickenrent.rentalservice.service.LocationRoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/location-roles")
@RequiredArgsConstructor
@Tag(name = "LocationRole", description = "Location role management (Admin, Manager, Staff)")
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
}
