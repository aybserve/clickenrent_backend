package org.clickenrent.rentalservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.B2BSubscriptionOrderStatusDTO;
import org.clickenrent.rentalservice.service.B2BSubscriptionOrderStatusService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/b2b-subscription-order-statuses")
@RequiredArgsConstructor
@Tag(name = "B2BSubscriptionOrderStatus", description = "B2B subscription order status management")
@SecurityRequirement(name = "bearerAuth")
public class B2BSubscriptionOrderStatusController {

    private final B2BSubscriptionOrderStatusService b2bSubscriptionOrderStatusService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(summary = "Get all B2B subscription order statuses")
    public ResponseEntity<List<B2BSubscriptionOrderStatusDTO>> getAllStatuses() {
        return ResponseEntity.ok(b2bSubscriptionOrderStatusService.getAllStatuses());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(summary = "Get status by ID")
    public ResponseEntity<B2BSubscriptionOrderStatusDTO> getStatusById(@PathVariable Long id) {
        return ResponseEntity.ok(b2bSubscriptionOrderStatusService.getStatusById(id));
    }
}


