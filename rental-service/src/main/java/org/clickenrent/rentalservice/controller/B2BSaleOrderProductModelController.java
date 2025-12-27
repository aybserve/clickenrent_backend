package org.clickenrent.rentalservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.B2BSaleOrderProductModelDTO;
import org.clickenrent.rentalservice.service.B2BSaleOrderProductModelService;
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
@RequestMapping("/api/b2b-sale-order-product-models")
@RequiredArgsConstructor
@Tag(name = "B2BSaleOrderProductModel", description = "B2B sale order product model management")
@SecurityRequirement(name = "bearerAuth")
public class B2BSaleOrderProductModelController {

    private final B2BSaleOrderProductModelService b2bSaleOrderProductModelService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Get all B2B sale order product models")
    public ResponseEntity<Page<B2BSaleOrderProductModelDTO>> getAllProductModels(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(b2bSaleOrderProductModelService.getAllProductModels(pageable));
    }

    @GetMapping("/by-order/{orderId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(summary = "Get product models by order ID")
    public ResponseEntity<List<B2BSaleOrderProductModelDTO>> getProductModelsByOrderId(@PathVariable Long orderId) {
        return ResponseEntity.ok(b2bSaleOrderProductModelService.getProductModelsByOrderId(orderId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(summary = "Get product model by ID")
    public ResponseEntity<B2BSaleOrderProductModelDTO> getProductModelById(@PathVariable Long id) {
        return ResponseEntity.ok(b2bSaleOrderProductModelService.getProductModelById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(summary = "Create B2B sale order product model")
    public ResponseEntity<B2BSaleOrderProductModelDTO> createProductModel(@Valid @RequestBody B2BSaleOrderProductModelDTO dto) {
        return new ResponseEntity<>(b2bSaleOrderProductModelService.createProductModel(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(summary = "Update B2B sale order product model")
    public ResponseEntity<B2BSaleOrderProductModelDTO> updateProductModel(@PathVariable Long id, @Valid @RequestBody B2BSaleOrderProductModelDTO dto) {
        return ResponseEntity.ok(b2bSaleOrderProductModelService.updateProductModel(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Delete B2B sale order product model")
    public ResponseEntity<Void> deleteProductModel(@PathVariable Long id) {
        b2bSaleOrderProductModelService.deleteProductModel(id);
        return ResponseEntity.noContent().build();
    }
}







