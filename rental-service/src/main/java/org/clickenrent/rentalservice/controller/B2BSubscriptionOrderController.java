package org.clickenrent.rentalservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.B2BSubscriptionOrderDTO;
import org.clickenrent.rentalservice.service.B2BSubscriptionOrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/b2b-subscription-orders")
@RequiredArgsConstructor
@Tag(name = "B2BSubscriptionOrder", description = "B2B subscription order management")
@SecurityRequirement(name = "bearerAuth")
public class B2BSubscriptionOrderController {

    private final B2BSubscriptionOrderService b2bSubscriptionOrderService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Get all B2B subscription orders")
    public ResponseEntity<Page<B2BSubscriptionOrderDTO>> getAllOrders(
            @PageableDefault(size = 20, sort = "dateTime", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(b2bSubscriptionOrderService.getAllOrders(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(summary = "Get B2B subscription order by ID")
    public ResponseEntity<B2BSubscriptionOrderDTO> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(b2bSubscriptionOrderService.getOrderById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(summary = "Create B2B subscription order")
    public ResponseEntity<B2BSubscriptionOrderDTO> createOrder(@Valid @RequestBody B2BSubscriptionOrderDTO dto) {
        return new ResponseEntity<>(b2bSubscriptionOrderService.createOrder(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(summary = "Update B2B subscription order")
    public ResponseEntity<B2BSubscriptionOrderDTO> updateOrder(@PathVariable Long id, @Valid @RequestBody B2BSubscriptionOrderDTO dto) {
        return ResponseEntity.ok(b2bSubscriptionOrderService.updateOrder(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Delete B2B subscription order")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        b2bSubscriptionOrderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}

