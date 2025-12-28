package org.clickenrent.rentalservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.B2BSaleStatusDTO;
import org.clickenrent.rentalservice.service.B2BSaleStatusService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/b2b-sale-statuses")
@RequiredArgsConstructor
@Tag(name = "B2BSaleStatus", description = "B2B sale status management")
@SecurityRequirement(name = "bearerAuth")
public class B2BSaleStatusController {

    private final B2BSaleStatusService b2bSaleStatusService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(summary = "Get all B2B sale statuses")
    public ResponseEntity<List<B2BSaleStatusDTO>> getAllStatuses() {
        return ResponseEntity.ok(b2bSaleStatusService.getAllStatuses());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(summary = "Get status by ID")
    public ResponseEntity<B2BSaleStatusDTO> getStatusById(@PathVariable Long id) {
        return ResponseEntity.ok(b2bSaleStatusService.getStatusById(id));
    }
}








