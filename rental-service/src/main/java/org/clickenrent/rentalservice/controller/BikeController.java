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
import org.clickenrent.rentalservice.dto.BikeDTO;
import org.clickenrent.rentalservice.dto.NearbyBikesResponseDTO;
import org.clickenrent.rentalservice.service.BikeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for Bike management operations.
 */
@RestController
@RequestMapping("/api/v1/bikes")
@RequiredArgsConstructor
@Tag(name = "Bike", description = "Bike management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class BikeController {

    private final BikeService bikeService;

    /**
     * Get all bikes with pagination.
     * GET /api/bikes
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get all bikes",
            description = "Returns a paginated list of all bikes. Access control based on user role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bikes retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<Page<BikeDTO>> getAllBikes(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<BikeDTO> bikes = bikeService.getAllBikes(pageable);
        return ResponseEntity.ok(bikes);
    }

    /**
     * Get bike by ID.
     * GET /api/bikes/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get bike by ID",
            description = "Returns bike details by bike ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bike found",
                    content = @Content(schema = @Schema(implementation = BikeDTO.class))),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "404", description = "Bike not found")
    })
    public ResponseEntity<BikeDTO> getBikeById(
            @Parameter(description = "Bike ID", required = true) @PathVariable Long id) {
        BikeDTO bike = bikeService.getBikeById(id);
        return ResponseEntity.ok(bike);
    }

    /**
     * Get bike by external ID.
     * GET /api/bikes/external/{externalId}
     */
    @GetMapping("/external/{externalId}")
    @Operation(summary = "Get bike by external ID", description = "Retrieve bike details by external ID for cross-service communication (public for service-to-service calls)")
    public ResponseEntity<BikeDTO> getByExternalId(@PathVariable String externalId) {
        BikeDTO bike = bikeService.findByExternalId(externalId);
        return ResponseEntity.ok(bike);
    }

    /**
     * Update bike by external ID.
     * PUT /api/bikes/external/{externalId}
     */
    @PutMapping("/external/{externalId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Update bike by external ID", description = "Used for cross-service updates (e.g., support-service reporting issues)")
    public ResponseEntity<BikeDTO> updateByExternalId(
            @PathVariable String externalId,
            @Valid @RequestBody BikeDTO dto) {
        return ResponseEntity.ok(bikeService.updateByExternalId(externalId, dto));
    }

    /**
     * Delete bike by external ID.
     * DELETE /api/bikes/external/{externalId}
     */
    @DeleteMapping("/external/{externalId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Delete bike by external ID")
    public ResponseEntity<Void> deleteByExternalId(@PathVariable String externalId) {
        bikeService.deleteByExternalId(externalId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get bike by code.
     * GET /api/bikes/code/{code}
     */
    @GetMapping("/code/{code}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get bike by code",
            description = "Returns bike details by bike code"
    )
    public ResponseEntity<BikeDTO> getBikeByCode(@PathVariable String code) {
        BikeDTO bike = bikeService.getBikeByCode(code);
        return ResponseEntity.ok(bike);
    }

    /**
     * Create a new bike.
     * POST /api/bikes
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(
            summary = "Create a new bike",
            description = "Creates a new bike. Requires SUPERADMIN or ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Bike created successfully",
                    content = @Content(schema = @Schema(implementation = BikeDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<BikeDTO> createBike(@Valid @RequestBody BikeDTO bikeDTO) {
        BikeDTO createdBike = bikeService.createBike(bikeDTO);
        return new ResponseEntity<>(createdBike, HttpStatus.CREATED);
    }

    /**
     * Update bike by ID.
     * PUT /api/bikes/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(
            summary = "Update bike",
            description = "Updates bike information by ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bike updated successfully",
                    content = @Content(schema = @Schema(implementation = BikeDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Bike not found")
    })
    public ResponseEntity<BikeDTO> updateBike(
            @Parameter(description = "Bike ID", required = true) @PathVariable Long id,
            @Valid @RequestBody BikeDTO bikeDTO) {
        BikeDTO updatedBike = bikeService.updateBike(id, bikeDTO);
        return ResponseEntity.ok(updatedBike);
    }

    /**
     * Delete bike by ID.
     * DELETE /api/bikes/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(
            summary = "Delete bike",
            description = "Deletes a bike by ID. Requires SUPERADMIN or ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Bike deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Bike not found")
    })
    public ResponseEntity<Void> deleteBike(
            @Parameter(description = "Bike ID", required = true) @PathVariable Long id) {
        bikeService.deleteBike(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Find bikes nearby a given location.
     * GET /api/bikes/nearby?lat=52.374&lng=4.9&radius=5&limit=50
     */
    @GetMapping("/nearby")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Find nearby bikes",
            description = "Returns bikes within a specified radius of a given location, sorted by distance"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nearby bikes retrieved successfully",
                    content = @Content(schema = @Schema(implementation = NearbyBikesResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid parameters (coordinates or radius)"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public ResponseEntity<NearbyBikesResponseDTO> getNearbyBikes(
            @Parameter(description = "Latitude of center point", required = true, example = "52.374")
            @RequestParam("lat") Double latitude,
            
            @Parameter(description = "Longitude of center point", required = true, example = "4.9")
            @RequestParam("lng") Double longitude,
            
            @Parameter(description = "Search radius in kilometers", required = true, example = "5")
            @RequestParam("radius") Double radius,
            
            @Parameter(description = "Maximum number of results (default: 50, max: 200)", example = "50")
            @RequestParam(value = "limit", required = false, defaultValue = "50") Integer limit,
            
            @Parameter(description = "Filter by bike status ID (optional)")
            @RequestParam(value = "status", required = false) Long bikeStatusId) {
        
        NearbyBikesResponseDTO response = bikeService.findNearbyBikes(
                latitude, longitude, radius, limit, bikeStatusId);
        return ResponseEntity.ok(response);
    }
}








