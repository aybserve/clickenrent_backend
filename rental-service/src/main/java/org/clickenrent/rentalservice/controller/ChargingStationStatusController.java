package org.clickenrent.rentalservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.ChargingStationStatusDTO;
import org.clickenrent.rentalservice.service.ChargingStationStatusService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/charging-station-statuses")
@RequiredArgsConstructor
@Tag(name = "ChargingStationStatus", description = "Charging station status management")
@SecurityRequirement(name = "bearerAuth")
public class ChargingStationStatusController {

    private final ChargingStationStatusService chargingStationStatusService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all charging station statuses")
    public ResponseEntity<List<ChargingStationStatusDTO>> getAllStatuses() {
        return ResponseEntity.ok(chargingStationStatusService.getAllStatuses());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get status by ID")
    public ResponseEntity<ChargingStationStatusDTO> getStatusById(@PathVariable Long id) {
        return ResponseEntity.ok(chargingStationStatusService.getStatusById(id));
    }
}







