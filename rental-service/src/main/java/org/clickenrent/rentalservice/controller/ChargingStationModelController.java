package org.clickenrent.rentalservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.ChargingStationModelDTO;
import org.clickenrent.rentalservice.service.ChargingStationModelService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/charging-station-models")
@RequiredArgsConstructor
@Tag(name = "Charging Station Models", description = "Charging station model management")
@SecurityRequirement(name = "bearerAuth")
public class ChargingStationModelController {

    private final ChargingStationModelService chargingStationModelService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all charging station models")
    public ResponseEntity<Page<ChargingStationModelDTO>> getAllModels(
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(chargingStationModelService.getAllModels(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get model by ID")
    public ResponseEntity<ChargingStationModelDTO> getModelById(@PathVariable Long id) {
        return ResponseEntity.ok(chargingStationModelService.getModelById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Create charging station model")
    public ResponseEntity<ChargingStationModelDTO> createModel(@Valid @RequestBody ChargingStationModelDTO dto) {
        return new ResponseEntity<>(chargingStationModelService.createModel(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Update model")
    public ResponseEntity<ChargingStationModelDTO> updateModel(@PathVariable Long id, @Valid @RequestBody ChargingStationModelDTO dto) {
        return ResponseEntity.ok(chargingStationModelService.updateModel(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Delete model")
    public ResponseEntity<Void> deleteModel(@PathVariable Long id) {
        chargingStationModelService.deleteModel(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/external/{externalId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get charging station model by external ID", description = "Retrieve model by external ID for cross-service communication")
    public ResponseEntity<ChargingStationModelDTO> getByExternalId(@PathVariable String externalId) {
        return ResponseEntity.ok(chargingStationModelService.findByExternalId(externalId));
    }
}








