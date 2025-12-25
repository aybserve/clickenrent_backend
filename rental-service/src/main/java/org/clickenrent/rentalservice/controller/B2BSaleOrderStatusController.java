package org.clickenrent.rentalservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.B2BSaleOrderStatusDTO;
import org.clickenrent.rentalservice.service.B2BSaleOrderStatusService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/b2b-sale-order-statuses")
@RequiredArgsConstructor
@Tag(name = "B2BSaleOrderStatus", description = "B2B sale order status management")
@SecurityRequirement(name = "bearerAuth")
public class B2BSaleOrderStatusController {

    private final B2BSaleOrderStatusService b2bSaleOrderStatusService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(summary = "Get all B2B sale order statuses")
    public ResponseEntity<List<B2BSaleOrderStatusDTO>> getAllStatuses() {
        return ResponseEntity.ok(b2bSaleOrderStatusService.getAllStatuses());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(summary = "Get status by ID")
    public ResponseEntity<B2BSaleOrderStatusDTO> getStatusById(@PathVariable Long id) {
        return ResponseEntity.ok(b2bSaleOrderStatusService.getStatusById(id));
    }
}






