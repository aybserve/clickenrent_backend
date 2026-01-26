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
import org.clickenrent.rentalservice.dto.*;
import org.clickenrent.rentalservice.service.MapboxService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for location-based operations including geocoding and directions.
 */
@RestController
@RequestMapping("/api/v1/location")
@RequiredArgsConstructor
@Tag(name = "Location", description = "Location services including geocoding and directions")
@SecurityRequirement(name = "bearerAuth")
public class LocationController {

    private final MapboxService mapboxService;
    private final org.clickenrent.rentalservice.service.LocationService locationService;

    /**
     * Geocode an address to coordinates.
     * POST /api/location/geocode
     */
    @PostMapping("/geocode")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Geocode address",
            description = "Convert an address to geographic coordinates using Mapbox Geocoding API"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Geocoding successful",
                    content = @Content(schema = @Schema(implementation = GeocodingResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "500", description = "Geocoding service error")
    })
    public ResponseEntity<GeocodingResponseDTO> geocode(
            @Valid @RequestBody GeocodingRequestDTO request) {
        GeocodingResponseDTO response = mapboxService.geocode(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Reverse geocode coordinates to address.
     * POST /api/location/reverse-geocode
     */
    @PostMapping("/reverse-geocode")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Reverse geocode coordinates",
            description = "Convert geographic coordinates to an address using Mapbox Geocoding API"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reverse geocoding successful",
                    content = @Content(schema = @Schema(implementation = GeocodingResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid coordinates"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "500", description = "Geocoding service error")
    })
    public ResponseEntity<GeocodingResponseDTO> reverseGeocode(
            @Valid @RequestBody ReverseGeocodingRequestDTO request) {
        GeocodingResponseDTO response = mapboxService.reverseGeocode(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Get directions between two points.
     * POST /api/location/directions
     */
    @PostMapping("/directions")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get directions",
            description = "Get route directions between two points using Mapbox Directions API"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Directions retrieved successfully",
                    content = @Content(schema = @Schema(implementation = DirectionsResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid coordinates"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "500", description = "Directions service error")
    })
    public ResponseEntity<DirectionsResponseDTO> getDirections(
            @Valid @RequestBody DirectionsRequestDTO request) {
        DirectionsResponseDTO response = mapboxService.getDirections(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/external/{externalId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get location by external ID", description = "Retrieve location by external ID for cross-service communication")
    public ResponseEntity<LocationDTO> getByExternalId(@PathVariable String externalId) {
        return ResponseEntity.ok(locationService.getLocationByExternalId(externalId));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all locations (paginated)", description = "Retrieve paginated list of locations for bulk indexing")
    public ResponseEntity<org.springframework.data.domain.Page<LocationDTO>> getAllLocations(
            @RequestParam(required = false) String companyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {
        return ResponseEntity.ok(locationService.getAllLocations(companyId, page, size));
    }

    /**
     * Get location by ID.
     * GET /api/v1/location/{id}
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
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Location not found")
    })
    public ResponseEntity<LocationDTO> getLocationById(
            @Parameter(description = "Location ID", required = true) @PathVariable Long id) {
        LocationDTO location = locationService.getLocationById(id);
        return ResponseEntity.ok(location);
    }

    /**
     * Create a new location.
     * POST /api/v1/location
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(
            summary = "Create a new location",
            description = "Creates a new location with automatic 'Main' hub creation. Requires SUPERADMIN, ADMIN or B2B role."
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
     * PUT /api/v1/location/{id}
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
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
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
     * DELETE /api/v1/location/{id}
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
