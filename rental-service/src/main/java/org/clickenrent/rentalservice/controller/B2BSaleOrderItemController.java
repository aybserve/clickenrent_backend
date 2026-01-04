package org.clickenrent.rentalservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.B2BSaleOrderItemDTO;
import org.clickenrent.rentalservice.service.B2BSaleOrderItemService;
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
@RequestMapping("/api/b2b-sale-order-items")
@RequiredArgsConstructor
@Tag(name = "B2B Sale Order Items", description = "B2B sale order item management")
@SecurityRequirement(name = "bearerAuth")
public class B2BSaleOrderItemController {

    private final B2BSaleOrderItemService b2bSaleOrderItemService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Get all B2B sale order items")
    public ResponseEntity<Page<B2BSaleOrderItemDTO>> getAllItems(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(b2bSaleOrderItemService.getAllItems(pageable));
    }

    @GetMapping("/by-order/{orderId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(summary = "Get items by order ID")
    public ResponseEntity<List<B2BSaleOrderItemDTO>> getItemsByOrderId(@PathVariable Long orderId) {
        return ResponseEntity.ok(b2bSaleOrderItemService.getItemsByOrderId(orderId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(summary = "Get item by ID")
    public ResponseEntity<B2BSaleOrderItemDTO> getItemById(@PathVariable Long id) {
        return ResponseEntity.ok(b2bSaleOrderItemService.getItemById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(summary = "Create B2B sale order item")
    public ResponseEntity<B2BSaleOrderItemDTO> createItem(@Valid @RequestBody B2BSaleOrderItemDTO dto) {
        return new ResponseEntity<>(b2bSaleOrderItemService.createItem(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(summary = "Update B2B sale order item")
    public ResponseEntity<B2BSaleOrderItemDTO> updateItem(@PathVariable Long id, @Valid @RequestBody B2BSaleOrderItemDTO dto) {
        return ResponseEntity.ok(b2bSaleOrderItemService.updateItem(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Delete B2B sale order item")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        b2bSaleOrderItemService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/external/{externalId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get B2B sale order item by external ID", description = "Retrieve item by external ID for cross-service communication")
    public ResponseEntity<B2BSaleOrderItemDTO> getByExternalId(@PathVariable String externalId) {
        return ResponseEntity.ok(b2bSaleOrderItemService.findByExternalId(externalId));
    }
}

