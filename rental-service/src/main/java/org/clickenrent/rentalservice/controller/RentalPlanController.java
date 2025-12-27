package org.clickenrent.rentalservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.RentalPlanDTO;
import org.clickenrent.rentalservice.service.RentalPlanService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rental-plans")
@RequiredArgsConstructor
@Tag(name = "RentalPlan", description = "Rental plan management")
@SecurityRequirement(name = "bearerAuth")
public class RentalPlanController {

    private final RentalPlanService rentalPlanService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all rental plans")
    public ResponseEntity<Page<RentalPlanDTO>> getAllRentalPlans(
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(rentalPlanService.getAllRentalPlans(pageable));
    }

    @GetMapping("/by-location/{locationId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get rental plans by location")
    public ResponseEntity<List<RentalPlanDTO>> getRentalPlansByLocation(@PathVariable Long locationId) {
        return ResponseEntity.ok(rentalPlanService.getRentalPlansByLocation(locationId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get rental plan by ID")
    public ResponseEntity<RentalPlanDTO> getRentalPlanById(@PathVariable Long id) {
        return ResponseEntity.ok(rentalPlanService.getRentalPlanById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(summary = "Create rental plan")
    public ResponseEntity<RentalPlanDTO> createRentalPlan(@Valid @RequestBody RentalPlanDTO dto) {
        return new ResponseEntity<>(rentalPlanService.createRentalPlan(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(summary = "Update rental plan")
    public ResponseEntity<RentalPlanDTO> updateRentalPlan(@PathVariable Long id, @Valid @RequestBody RentalPlanDTO dto) {
        return ResponseEntity.ok(rentalPlanService.updateRentalPlan(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Delete rental plan")
    public ResponseEntity<Void> deleteRentalPlan(@PathVariable Long id) {
        rentalPlanService.deleteRentalPlan(id);
        return ResponseEntity.noContent().build();
    }
}







