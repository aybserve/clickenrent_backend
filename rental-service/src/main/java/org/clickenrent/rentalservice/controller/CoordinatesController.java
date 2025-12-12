package org.clickenrent.rentalservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.CoordinatesDTO;
import org.clickenrent.rentalservice.service.CoordinatesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/coordinates")
@RequiredArgsConstructor
@Tag(name = "Coordinates", description = "Geographic coordinates management")
@SecurityRequirement(name = "bearerAuth")
public class CoordinatesController {

    private final CoordinatesService coordinatesService;

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get coordinates by ID")
    public ResponseEntity<CoordinatesDTO> getCoordinatesById(@PathVariable Long id) {
        return ResponseEntity.ok(coordinatesService.getCoordinatesById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(summary = "Create coordinates")
    public ResponseEntity<CoordinatesDTO> createCoordinates(@Valid @RequestBody CoordinatesDTO dto) {
        return new ResponseEntity<>(coordinatesService.createCoordinates(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(summary = "Update coordinates")
    public ResponseEntity<CoordinatesDTO> updateCoordinates(@PathVariable Long id, @Valid @RequestBody CoordinatesDTO dto) {
        return ResponseEntity.ok(coordinatesService.updateCoordinates(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Delete coordinates")
    public ResponseEntity<Void> deleteCoordinates(@PathVariable Long id) {
        coordinatesService.deleteCoordinates(id);
        return ResponseEntity.noContent().build();
    }
}
