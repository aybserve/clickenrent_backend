package org.clickenrent.rentalservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.BikeEngineDTO;
import org.clickenrent.rentalservice.service.BikeEngineService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bike-engines")
@RequiredArgsConstructor
@Tag(name = "BikeEngine", description = "Bike engine management")
@SecurityRequirement(name = "bearerAuth")
public class BikeEngineController {

    private final BikeEngineService bikeEngineService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all bike engines")
    public ResponseEntity<Page<BikeEngineDTO>> getAllBikeEngines(
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(bikeEngineService.getAllBikeEngines(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get bike engine by ID")
    public ResponseEntity<BikeEngineDTO> getBikeEngineById(@PathVariable Long id) {
        return ResponseEntity.ok(bikeEngineService.getBikeEngineById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Create bike engine")
    public ResponseEntity<BikeEngineDTO> createBikeEngine(@Valid @RequestBody BikeEngineDTO dto) {
        return new ResponseEntity<>(bikeEngineService.createBikeEngine(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Update bike engine")
    public ResponseEntity<BikeEngineDTO> updateBikeEngine(@PathVariable Long id, @Valid @RequestBody BikeEngineDTO dto) {
        return ResponseEntity.ok(bikeEngineService.updateBikeEngine(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Delete bike engine")
    public ResponseEntity<Void> deleteBikeEngine(@PathVariable Long id) {
        bikeEngineService.deleteBikeEngine(id);
        return ResponseEntity.noContent().build();
    }
}







