package org.clickenrent.rentalservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.BikePartDTO;
import org.clickenrent.rentalservice.service.BikePartService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bike-parts")
@RequiredArgsConstructor
@Tag(name = "BikePart", description = "Bike part relationship management")
@SecurityRequirement(name = "bearerAuth")
public class BikePartController {

    private final BikePartService bikePartService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Get all bike parts")
    public ResponseEntity<Page<BikePartDTO>> getAllBikeParts(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(bikePartService.getAllBikeParts(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Get bike part by ID")
    public ResponseEntity<BikePartDTO> getBikePartById(@PathVariable Long id) {
        return ResponseEntity.ok(bikePartService.getBikePartById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Create bike part")
    public ResponseEntity<BikePartDTO> createBikePart(@Valid @RequestBody BikePartDTO dto) {
        return new ResponseEntity<>(bikePartService.createBikePart(dto), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Delete bike part")
    public ResponseEntity<Void> deleteBikePart(@PathVariable Long id) {
        bikePartService.deleteBikePart(id);
        return ResponseEntity.noContent().build();
    }
}


