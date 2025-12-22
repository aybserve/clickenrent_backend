package org.clickenrent.rentalservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.RentalUnitDTO;
import org.clickenrent.rentalservice.service.RentalUnitService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rental-units")
@RequiredArgsConstructor
@Tag(name = "RentalUnit", description = "Rental unit management (Day, Hour, Week)")
@SecurityRequirement(name = "bearerAuth")
public class RentalUnitController {

    private final RentalUnitService rentalUnitService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all rental units")
    public ResponseEntity<List<RentalUnitDTO>> getAllUnits() {
        return ResponseEntity.ok(rentalUnitService.getAllUnits());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get unit by ID")
    public ResponseEntity<RentalUnitDTO> getUnitById(@PathVariable Long id) {
        return ResponseEntity.ok(rentalUnitService.getUnitById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Create rental unit")
    public ResponseEntity<RentalUnitDTO> createUnit(@Valid @RequestBody RentalUnitDTO dto) {
        return new ResponseEntity<>(rentalUnitService.createUnit(dto), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Delete rental unit")
    public ResponseEntity<Void> deleteUnit(@PathVariable Long id) {
        rentalUnitService.deleteUnit(id);
        return ResponseEntity.noContent().build();
    }
}




