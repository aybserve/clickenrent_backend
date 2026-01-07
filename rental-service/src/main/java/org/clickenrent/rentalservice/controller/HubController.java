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
import org.clickenrent.rentalservice.dto.HubDTO;
import org.clickenrent.rentalservice.service.HubService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for Hub management operations.
 */
@RestController
@RequestMapping("/api/v1/hubs")
@RequiredArgsConstructor
@Tag(name = "Hub", description = "Hub management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class HubController {

    private final HubService hubService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all hubs", description = "Returns a paginated list of all hubs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hubs retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<Page<HubDTO>> getAllHubs(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<HubDTO> hubs = hubService.getAllHubs(pageable);
        return ResponseEntity.ok(hubs);
    }

    @GetMapping("/by-location/{locationId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get hubs by location", description = "Returns all hubs for a specific location")
    public ResponseEntity<List<HubDTO>> getHubsByLocation(@PathVariable Long locationId) {
        List<HubDTO> hubs = hubService.getHubsByLocation(locationId);
        return ResponseEntity.ok(hubs);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get hub by ID", description = "Returns hub details by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hub found",
                    content = @Content(schema = @Schema(implementation = HubDTO.class))),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "404", description = "Hub not found")
    })
    public ResponseEntity<HubDTO> getHubById(
            @Parameter(description = "Hub ID", required = true) @PathVariable Long id) {
        HubDTO hub = hubService.getHubById(id);
        return ResponseEntity.ok(hub);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(summary = "Create a new hub", description = "Creates a new hub for a location")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Hub created successfully",
                    content = @Content(schema = @Schema(implementation = HubDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<HubDTO> createHub(@Valid @RequestBody HubDTO hubDTO) {
        HubDTO createdHub = hubService.createHub(hubDTO);
        return new ResponseEntity<>(createdHub, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(summary = "Update hub", description = "Updates hub information by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hub updated successfully",
                    content = @Content(schema = @Schema(implementation = HubDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Hub not found")
    })
    public ResponseEntity<HubDTO> updateHub(
            @Parameter(description = "Hub ID", required = true) @PathVariable Long id,
            @Valid @RequestBody HubDTO hubDTO) {
        HubDTO updatedHub = hubService.updateHub(id, hubDTO);
        return ResponseEntity.ok(updatedHub);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Delete hub", description = "Deletes a hub by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Hub deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Hub not found")
    })
    public ResponseEntity<Void> deleteHub(
            @Parameter(description = "Hub ID", required = true) @PathVariable Long id) {
        hubService.deleteHub(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/external/{externalId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get hub by external ID", description = "Retrieve hub by external ID for cross-service communication")
    public ResponseEntity<HubDTO> getByExternalId(@PathVariable String externalId) {
        return ResponseEntity.ok(hubService.getHubByExternalId(externalId));
    }
}








