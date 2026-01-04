package org.clickenrent.rentalservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.B2BSubscriptionStatusDTO;
import org.clickenrent.rentalservice.service.B2BSubscriptionStatusService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/b2b-subscription-statuses")
@RequiredArgsConstructor
@Tag(name = "B2BSubscriptionStatus", description = "B2B subscription status management")
@SecurityRequirement(name = "bearerAuth")
public class B2BSubscriptionStatusController {

    private final B2BSubscriptionStatusService b2bSubscriptionStatusService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(summary = "Get all B2B subscription statuses")
    public ResponseEntity<List<B2BSubscriptionStatusDTO>> getAllStatuses() {
        return ResponseEntity.ok(b2bSubscriptionStatusService.getAllStatuses());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(summary = "Get status by ID")
    public ResponseEntity<B2BSubscriptionStatusDTO> getStatusById(@PathVariable Long id) {
        return ResponseEntity.ok(b2bSubscriptionStatusService.getStatusById(id));
    }

    @GetMapping("/external/{externalId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get B2B subscription status by external ID", description = "Retrieve status by external ID for cross-service communication")
    public ResponseEntity<B2BSubscriptionStatusDTO> getByExternalId(@PathVariable String externalId) {
        return ResponseEntity.ok(b2bSubscriptionStatusService.findByExternalId(externalId));
    }
}








