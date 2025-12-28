package org.clickenrent.rentalservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.BikeModelRentalPlanDTO;
import org.clickenrent.rentalservice.service.BikeModelRentalPlanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bike-model-rental-plans")
@RequiredArgsConstructor
@Tag(name = "BikeModelRentalPlan", description = "Bike model rental plan pricing")
@SecurityRequirement(name = "bearerAuth")
public class BikeModelRentalPlanController {

    private final BikeModelRentalPlanService bikeModelRentalPlanService;

    @GetMapping("/by-bike-model/{bikeModelId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get rental plans for a bike model")
    public ResponseEntity<List<BikeModelRentalPlanDTO>> getPlansByBikeModel(@PathVariable Long bikeModelId) {
        return ResponseEntity.ok(bikeModelRentalPlanService.getPlansByBikeModel(bikeModelId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get bike model rental plan by ID")
    public ResponseEntity<BikeModelRentalPlanDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(bikeModelRentalPlanService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Create bike model rental plan")
    public ResponseEntity<BikeModelRentalPlanDTO> create(@Valid @RequestBody BikeModelRentalPlanDTO dto) {
        return new ResponseEntity<>(bikeModelRentalPlanService.create(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Update bike model rental plan")
    public ResponseEntity<BikeModelRentalPlanDTO> update(@PathVariable Long id, @Valid @RequestBody BikeModelRentalPlanDTO dto) {
        return ResponseEntity.ok(bikeModelRentalPlanService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Delete bike model rental plan")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bikeModelRentalPlanService.delete(id);
        return ResponseEntity.noContent().build();
    }
}








