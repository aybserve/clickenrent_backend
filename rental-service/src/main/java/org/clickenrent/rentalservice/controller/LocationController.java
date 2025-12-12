package org.clickenrent.rentalservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.LocationDTO;
import org.clickenrent.rentalservice.service.LocationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for Location management operations.
 * Auto-creates "Main" hub when creating a location.
 */
@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
@Tag(name = "Location", description = "Location management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class LocationController {

    private final LocationService locationService;

    /**
     * Get all locations with pagination.
     * GET /api/locations
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get all locations",
            description = "Returns a paginated list of all locations. Access control based on user role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Locations retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<Page<LocationDTO>> getAllLocations(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<LocationDTO> locations = locationService.getAllLocations(pageable);
        return ResponseEntity.ok(locations);
    }

    /**
     * Get location by ID.
     * GET /api/locations/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get location by ID",
            description = "Returns location details by location ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Location found",
                    content = @Content(schema = @Schema(implementation = LocationDTO.class))),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Location not found")
    })
    public ResponseEntity<LocationDTO> getLocationById(
            @Parameter(description = "Location ID", required = true) @PathVariable Long id) {
        LocationDTO location = locationService.getLocationById(id);
        return ResponseEntity.ok(location);
    }

    /**
     * Get location by external ID.
     * GET /api/locations/external/{externalId}
     */
    @GetMapping("/external/{externalId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(
            summary = "Get location by external ID",
            description = "Returns location details by external ID"
    )
    public ResponseEntity<LocationDTO> getLocationByExternalId(@PathVariable String externalId) {
        LocationDTO location = locationService.getLocationByExternalId(externalId);
        return ResponseEntity.ok(location);
    }

    /**
     * Create a new location.
     * POST /api/locations
     * Note: Auto-creates a "Main" hub for the location.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(
            summary = "Create a new location",
            description = "Creates a new location and automatically creates a 'Main' hub for it."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Location created successfully",
                    content = @Content(schema = @Schema(implementation = LocationDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<LocationDTO> createLocation(@Valid @RequestBody LocationDTO locationDTO) {
        LocationDTO createdLocation = locationService.createLocation(locationDTO);
        return new ResponseEntity<>(createdLocation, HttpStatus.CREATED);
    }

    /**
     * Update location by ID.
     * PUT /api/locations/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(
            summary = "Update location",
            description = "Updates location information by ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Location updated successfully",
                    content = @Content(schema = @Schema(implementation = LocationDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Location not found")
    })
    public ResponseEntity<LocationDTO> updateLocation(
            @Parameter(description = "Location ID", required = true) @PathVariable Long id,
            @Valid @RequestBody LocationDTO locationDTO) {
        LocationDTO updatedLocation = locationService.updateLocation(id, locationDTO);
        return ResponseEntity.ok(updatedLocation);
    }

    /**
     * Delete location by ID.
     * DELETE /api/locations/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(
            summary = "Delete location",
            description = "Deletes a location by ID. Requires SUPERADMIN or ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Location deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Location not found")
    })
    public ResponseEntity<Void> deleteLocation(
            @Parameter(description = "Location ID", required = true) @PathVariable Long id) {
        locationService.deleteLocation(id);
        return ResponseEntity.noContent().build();
    }
}
