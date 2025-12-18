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
import org.clickenrent.rentalservice.dto.RentalDTO;
import org.clickenrent.rentalservice.service.RentalService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for Rental management operations.
 */
@RestController
@RequestMapping("/api/rentals")
@RequiredArgsConstructor
@Tag(name = "Rental", description = "Rental management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class RentalController {

    private final RentalService rentalService;

    /**
     * Get all rentals with pagination.
     * GET /api/rentals
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get all rentals",
            description = "Returns a paginated list of rentals. Access control: Admin sees all, B2B sees company rentals, Customer sees own rentals."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rentals retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<Page<RentalDTO>> getAllRentals(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<RentalDTO> rentals = rentalService.getAllRentals(pageable);
        return ResponseEntity.ok(rentals);
    }

    /**
     * Get rental by ID.
     * GET /api/rentals/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get rental by ID",
            description = "Returns rental details by rental ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rental found",
                    content = @Content(schema = @Schema(implementation = RentalDTO.class))),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Rental not found")
    })
    public ResponseEntity<RentalDTO> getRentalById(
            @Parameter(description = "Rental ID", required = true) @PathVariable Long id) {
        RentalDTO rental = rentalService.getRentalById(id);
        return ResponseEntity.ok(rental);
    }

    /**
     * Create a new rental.
     * POST /api/rentals
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Create a new rental",
            description = "Creates a new rental order. Users can create rentals for themselves, admins can create for anyone."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Rental created successfully",
                    content = @Content(schema = @Schema(implementation = RentalDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<RentalDTO> createRental(@Valid @RequestBody RentalDTO rentalDTO) {
        RentalDTO createdRental = rentalService.createRental(rentalDTO);
        return new ResponseEntity<>(createdRental, HttpStatus.CREATED);
    }

    /**
     * Update rental by ID.
     * PUT /api/rentals/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Update rental",
            description = "Updates rental information by ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rental updated successfully",
                    content = @Content(schema = @Schema(implementation = RentalDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Rental not found")
    })
    public ResponseEntity<RentalDTO> updateRental(
            @Parameter(description = "Rental ID", required = true) @PathVariable Long id,
            @Valid @RequestBody RentalDTO rentalDTO) {
        RentalDTO updatedRental = rentalService.updateRental(id, rentalDTO);
        return ResponseEntity.ok(updatedRental);
    }

    /**
     * Delete rental by ID.
     * DELETE /api/rentals/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(
            summary = "Delete rental",
            description = "Deletes a rental by ID. Requires SUPERADMIN or ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Rental deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Rental not found")
    })
    public ResponseEntity<Void> deleteRental(
            @Parameter(description = "Rental ID", required = true) @PathVariable Long id) {
        rentalService.deleteRental(id);
        return ResponseEntity.noContent().build();
    }
}


