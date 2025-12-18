package org.clickenrent.rentalservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.B2BSubscriptionItemDTO;
import org.clickenrent.rentalservice.service.B2BSubscriptionItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/b2b-subscription-items")
@RequiredArgsConstructor
@Tag(name = "B2BSubscriptionItem", description = "B2B subscription items management")
@SecurityRequirement(name = "bearerAuth")
public class B2BSubscriptionItemController {

    private final B2BSubscriptionItemService b2bSubscriptionItemService;

    @GetMapping("/by-subscription/{subscriptionId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(summary = "Get items by subscription")
    public ResponseEntity<List<B2BSubscriptionItemDTO>> getItemsBySubscription(@PathVariable Long subscriptionId) {
        return ResponseEntity.ok(b2bSubscriptionItemService.getItemsBySubscription(subscriptionId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(summary = "Get subscription item by ID")
    public ResponseEntity<B2BSubscriptionItemDTO> getItemById(@PathVariable Long id) {
        return ResponseEntity.ok(b2bSubscriptionItemService.getItemById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(summary = "Add item to subscription")
    public ResponseEntity<B2BSubscriptionItemDTO> createItem(@Valid @RequestBody B2BSubscriptionItemDTO dto) {
        return new ResponseEntity<>(b2bSubscriptionItemService.createItem(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(summary = "Update subscription item")
    public ResponseEntity<B2BSubscriptionItemDTO> updateItem(@PathVariable Long id, @Valid @RequestBody B2BSubscriptionItemDTO dto) {
        return ResponseEntity.ok(b2bSubscriptionItemService.updateItem(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Delete subscription item")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        b2bSubscriptionItemService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }
}

