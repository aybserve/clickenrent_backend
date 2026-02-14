package org.clickenrent.rentalservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.B2BSubscriptionDTO;
import org.clickenrent.rentalservice.service.B2BSubscriptionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/b2b-subscriptions")
@RequiredArgsConstructor
@Tag(name = "B2B Subscription", description = "B2B subscription management")
@SecurityRequirement(name = "bearerAuth")
public class B2BSubscriptionController {

    private final B2BSubscriptionService b2bSubscriptionService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Get all B2B subscriptions")
    public ResponseEntity<Page<B2BSubscriptionDTO>> getAllSubscriptions(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(b2bSubscriptionService.getAllSubscriptions(pageable));
    }

    @GetMapping("/by-location/{locationId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(summary = "Get subscriptions by location")
    public ResponseEntity<List<B2BSubscriptionDTO>> getSubscriptionsByLocation(@PathVariable Long locationId) {
        return ResponseEntity.ok(b2bSubscriptionService.getSubscriptionsByLocation(locationId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(summary = "Get subscription by ID")
    public ResponseEntity<B2BSubscriptionDTO> getSubscriptionById(@PathVariable Long id) {
        return ResponseEntity.ok(b2bSubscriptionService.getSubscriptionById(id));
    }

    @GetMapping("/{id}/exists")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Check if B2B subscription exists by ID")
    public ResponseEntity<Boolean> checkSubscriptionExists(@PathVariable Long id) {
        boolean exists = b2bSubscriptionService.getSubscriptionById(id) != null;
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/external/{externalId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(summary = "Get subscription by external ID")
    public ResponseEntity<B2BSubscriptionDTO> getSubscriptionByExternalId(@PathVariable String externalId) {
        return ResponseEntity.ok(b2bSubscriptionService.findByExternalId(externalId));
    }

    @GetMapping("/external/{externalId}/exists")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Check if B2B subscription exists by external ID")
    public ResponseEntity<Boolean> checkSubscriptionExistsByExternalId(@PathVariable String externalId) {
        return ResponseEntity.ok(b2bSubscriptionService.existsByExternalId(externalId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(summary = "Create B2B subscription")
    public ResponseEntity<B2BSubscriptionDTO> createSubscription(@Valid @RequestBody B2BSubscriptionDTO dto) {
        return new ResponseEntity<>(b2bSubscriptionService.createSubscription(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(summary = "Update subscription")
    public ResponseEntity<B2BSubscriptionDTO> updateSubscription(@PathVariable Long id, @Valid @RequestBody B2BSubscriptionDTO dto) {
        return ResponseEntity.ok(b2bSubscriptionService.updateSubscription(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Delete subscription")
    public ResponseEntity<Void> deleteSubscription(@PathVariable Long id) {
        b2bSubscriptionService.deleteSubscription(id);
        return ResponseEntity.noContent().build();
    }
}
