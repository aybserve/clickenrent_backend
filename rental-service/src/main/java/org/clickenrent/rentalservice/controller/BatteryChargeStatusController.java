package org.clickenrent.rentalservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.BatteryChargeStatusDTO;
import org.clickenrent.rentalservice.service.BatteryChargeStatusService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/battery-charge-statuses")
@RequiredArgsConstructor
@Tag(name = "BatteryChargeStatus", description = "Battery charge status management")
@SecurityRequirement(name = "bearerAuth")
public class BatteryChargeStatusController {

    private final BatteryChargeStatusService batteryChargeStatusService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all battery charge statuses")
    public ResponseEntity<List<BatteryChargeStatusDTO>> getAllStatuses() {
        return ResponseEntity.ok(batteryChargeStatusService.getAllStatuses());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get status by ID")
    public ResponseEntity<BatteryChargeStatusDTO> getStatusById(@PathVariable Long id) {
        return ResponseEntity.ok(batteryChargeStatusService.getStatusById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Create battery charge status")
    public ResponseEntity<BatteryChargeStatusDTO> createStatus(@Valid @RequestBody BatteryChargeStatusDTO dto) {
        return new ResponseEntity<>(batteryChargeStatusService.createStatus(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Update status")
    public ResponseEntity<BatteryChargeStatusDTO> updateStatus(@PathVariable Long id, @Valid @RequestBody BatteryChargeStatusDTO dto) {
        return ResponseEntity.ok(batteryChargeStatusService.updateStatus(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Delete status")
    public ResponseEntity<Void> deleteStatus(@PathVariable Long id) {
        batteryChargeStatusService.deleteStatus(id);
        return ResponseEntity.noContent().build();
    }
}
