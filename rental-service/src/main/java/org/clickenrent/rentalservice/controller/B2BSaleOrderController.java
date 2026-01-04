package org.clickenrent.rentalservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.B2BSaleOrderDTO;
import org.clickenrent.rentalservice.service.B2BSaleOrderService;
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
@RequestMapping("/api/b2b-sale-orders")
@RequiredArgsConstructor
@Tag(name = "B2BSaleOrder", description = "B2B sale order management")
@SecurityRequirement(name = "bearerAuth")
public class B2BSaleOrderController {

    private final B2BSaleOrderService b2bSaleOrderService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Get all B2B sale orders")
    public ResponseEntity<Page<B2BSaleOrderDTO>> getAllOrders(
            @PageableDefault(size = 20, sort = "dateTime", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(b2bSaleOrderService.getAllOrders(pageable));
    }

    @GetMapping("/by-seller/{sellerCompanyExternalId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(summary = "Get orders by seller company external ID")
    public ResponseEntity<List<B2BSaleOrderDTO>> getOrdersBySellerCompanyExternalId(@PathVariable String sellerCompanyExternalId) {
        return ResponseEntity.ok(b2bSaleOrderService.getOrdersBySellerCompanyExternalId(sellerCompanyExternalId));
    }

    @GetMapping("/by-buyer/{buyerCompanyExternalId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(summary = "Get orders by buyer company external ID")
    public ResponseEntity<List<B2BSaleOrderDTO>> getOrdersByBuyerCompanyExternalId(@PathVariable String buyerCompanyExternalId) {
        return ResponseEntity.ok(b2bSaleOrderService.getOrdersByBuyerCompanyExternalId(buyerCompanyExternalId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(summary = "Get B2B sale order by ID")
    public ResponseEntity<B2BSaleOrderDTO> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(b2bSaleOrderService.getOrderById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(summary = "Create B2B sale order")
    public ResponseEntity<B2BSaleOrderDTO> createOrder(@Valid @RequestBody B2BSaleOrderDTO dto) {
        return new ResponseEntity<>(b2bSaleOrderService.createOrder(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(summary = "Update B2B sale order")
    public ResponseEntity<B2BSaleOrderDTO> updateOrder(@PathVariable Long id, @Valid @RequestBody B2BSaleOrderDTO dto) {
        return ResponseEntity.ok(b2bSaleOrderService.updateOrder(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Delete B2B sale order")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        b2bSaleOrderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/external/{externalId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get B2B sale order by external ID", description = "Retrieve order by external ID for cross-service communication")
    public ResponseEntity<B2BSaleOrderDTO> getByExternalId(@PathVariable String externalId) {
        return ResponseEntity.ok(b2bSaleOrderService.getB2BSaleOrderByExternalId(externalId));
    }
}




