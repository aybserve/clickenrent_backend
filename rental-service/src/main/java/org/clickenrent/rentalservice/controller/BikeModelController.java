package org.clickenrent.rentalservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.BikeModelDTO;
import org.clickenrent.rentalservice.service.BikeModelService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bike-models")
@RequiredArgsConstructor
@Tag(name = "BikeModel", description = "Bike model management")
@SecurityRequirement(name = "bearerAuth")
public class BikeModelController {

    private final BikeModelService bikeModelService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all bike models")
    public ResponseEntity<Page<BikeModelDTO>> getAllBikeModels(
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(bikeModelService.getAllBikeModels(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get bike model by ID")
    public ResponseEntity<BikeModelDTO> getBikeModelById(@PathVariable Long id) {
        return ResponseEntity.ok(bikeModelService.getBikeModelById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Create bike model")
    public ResponseEntity<BikeModelDTO> createBikeModel(@Valid @RequestBody BikeModelDTO dto) {
        return new ResponseEntity<>(bikeModelService.createBikeModel(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Update bike model")
    public ResponseEntity<BikeModelDTO> updateBikeModel(@PathVariable Long id, @Valid @RequestBody BikeModelDTO dto) {
        return ResponseEntity.ok(bikeModelService.updateBikeModel(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Delete bike model")
    public ResponseEntity<Void> deleteBikeModel(@PathVariable Long id) {
        bikeModelService.deleteBikeModel(id);
        return ResponseEntity.noContent().build();
    }
}








