package org.clickenrent.rentalservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.ChargingStationDTO;
import org.clickenrent.rentalservice.service.ChargingStationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/charging-stations")
@RequiredArgsConstructor
@Tag(name = "ChargingStation", description = "Charging station management")
@SecurityRequirement(name = "bearerAuth")
public class ChargingStationController {

    private final ChargingStationService chargingStationService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all charging stations")
    public ResponseEntity<Page<ChargingStationDTO>> getAllChargingStations(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(chargingStationService.getAllChargingStations(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get charging station by ID")
    public ResponseEntity<ChargingStationDTO> getChargingStationById(@PathVariable Long id) {
        return ResponseEntity.ok(chargingStationService.getChargingStationById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Create charging station")
    public ResponseEntity<ChargingStationDTO> createChargingStation(@Valid @RequestBody ChargingStationDTO dto) {
        return new ResponseEntity<>(chargingStationService.createChargingStation(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Update charging station")
    public ResponseEntity<ChargingStationDTO> updateChargingStation(@PathVariable Long id, @Valid @RequestBody ChargingStationDTO dto) {
        return ResponseEntity.ok(chargingStationService.updateChargingStation(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Delete charging station")
    public ResponseEntity<Void> deleteChargingStation(@PathVariable Long id) {
        chargingStationService.deleteChargingStation(id);
        return ResponseEntity.noContent().build();
    }
}
