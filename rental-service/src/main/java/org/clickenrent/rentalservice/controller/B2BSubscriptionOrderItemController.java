package org.clickenrent.rentalservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.B2BSubscriptionOrderItemDTO;
import org.clickenrent.rentalservice.service.B2BSubscriptionOrderItemService;
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
@RequestMapping("/api/b2b-subscription-order-items")
@RequiredArgsConstructor
@Tag(name = "B2BSubscriptionOrderItem", description = "B2B subscription order item management")
@SecurityRequirement(name = "bearerAuth")
public class B2BSubscriptionOrderItemController {

    private final B2BSubscriptionOrderItemService b2bSubscriptionOrderItemService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Get all B2B subscription order items")
    public ResponseEntity<Page<B2BSubscriptionOrderItemDTO>> getAllItems(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(b2bSubscriptionOrderItemService.getAllItems(pageable));
    }

    @GetMapping("/by-order/{orderId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(summary = "Get items by order ID")
    public ResponseEntity<List<B2BSubscriptionOrderItemDTO>> getItemsByOrderId(@PathVariable Long orderId) {
        return ResponseEntity.ok(b2bSubscriptionOrderItemService.getItemsByOrderId(orderId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(summary = "Get item by ID")
    public ResponseEntity<B2BSubscriptionOrderItemDTO> getItemById(@PathVariable Long id) {
        return ResponseEntity.ok(b2bSubscriptionOrderItemService.getItemById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(summary = "Create B2B subscription order item")
    public ResponseEntity<B2BSubscriptionOrderItemDTO> createItem(@Valid @RequestBody B2BSubscriptionOrderItemDTO dto) {
        return new ResponseEntity<>(b2bSubscriptionOrderItemService.createItem(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(summary = "Update B2B subscription order item")
    public ResponseEntity<B2BSubscriptionOrderItemDTO> updateItem(@PathVariable Long id, @Valid @RequestBody B2BSubscriptionOrderItemDTO dto) {
        return ResponseEntity.ok(b2bSubscriptionOrderItemService.updateItem(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Delete B2B subscription order item")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        b2bSubscriptionOrderItemService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }
}




