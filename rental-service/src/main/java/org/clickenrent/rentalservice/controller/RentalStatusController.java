package org.clickenrent.rentalservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.RentalStatusDTO;
import org.clickenrent.rentalservice.service.RentalStatusService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rental-statuses")
@RequiredArgsConstructor
@Tag(name = "RentalStatus", description = "Rental status management")
@SecurityRequirement(name = "bearerAuth")
public class RentalStatusController {

    private final RentalStatusService rentalStatusService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all rental statuses")
    public ResponseEntity<List<RentalStatusDTO>> getAllStatuses() {
        return ResponseEntity.ok(rentalStatusService.getAllStatuses());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get status by ID")
    public ResponseEntity<RentalStatusDTO> getStatusById(@PathVariable Long id) {
        return ResponseEntity.ok(rentalStatusService.getStatusById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Create rental status")
    public ResponseEntity<RentalStatusDTO> createStatus(@Valid @RequestBody RentalStatusDTO dto) {
        return new ResponseEntity<>(rentalStatusService.createStatus(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Update status")
    public ResponseEntity<RentalStatusDTO> updateStatus(@PathVariable Long id, @Valid @RequestBody RentalStatusDTO dto) {
        return ResponseEntity.ok(rentalStatusService.updateStatus(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Delete status")
    public ResponseEntity<Void> deleteStatus(@PathVariable Long id) {
        rentalStatusService.deleteStatus(id);
        return ResponseEntity.noContent().build();
    }
}








