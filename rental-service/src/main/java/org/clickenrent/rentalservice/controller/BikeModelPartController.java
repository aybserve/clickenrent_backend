package org.clickenrent.rentalservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.BikeModelPartDTO;
import org.clickenrent.rentalservice.service.BikeModelPartService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bike-model-parts")
@RequiredArgsConstructor
@Tag(name = "Bike Model Part", description = "Bike model part relationship management")
@SecurityRequirement(name = "bearerAuth")
public class BikeModelPartController {

    private final BikeModelPartService bikeModelPartService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Get all bike model parts")
    public ResponseEntity<Page<BikeModelPartDTO>> getAllBikeModelParts(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(bikeModelPartService.getAllBikeModelParts(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Get bike model part by ID")
    public ResponseEntity<BikeModelPartDTO> getBikeModelPartById(@PathVariable Long id) {
        return ResponseEntity.ok(bikeModelPartService.getBikeModelPartById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Create bike model part")
    public ResponseEntity<BikeModelPartDTO> createBikeModelPart(@Valid @RequestBody BikeModelPartDTO dto) {
        return new ResponseEntity<>(bikeModelPartService.createBikeModelPart(dto), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Delete bike model part")
    public ResponseEntity<Void> deleteBikeModelPart(@PathVariable Long id) {
        bikeModelPartService.deleteBikeModelPart(id);
        return ResponseEntity.noContent().build();
    }
}





