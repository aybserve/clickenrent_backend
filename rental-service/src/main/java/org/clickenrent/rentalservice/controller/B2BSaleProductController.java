package org.clickenrent.rentalservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.B2BSaleProductDTO;
import org.clickenrent.rentalservice.service.B2BSaleProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/b2b-sale-products")
@RequiredArgsConstructor
@Tag(name = "B2BSaleProduct", description = "B2B sale products management")
@SecurityRequirement(name = "bearerAuth")
public class B2BSaleProductController {

    private final B2BSaleProductService b2bSaleProductService;

    @GetMapping("/by-sale/{b2bSaleId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(summary = "Get products by B2B sale")
    public ResponseEntity<List<B2BSaleProductDTO>> getProductsBySale(@PathVariable Long b2bSaleId) {
        return ResponseEntity.ok(b2bSaleProductService.getProductsBySale(b2bSaleId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(summary = "Get product by ID")
    public ResponseEntity<B2BSaleProductDTO> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(b2bSaleProductService.getProductById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(summary = "Add product to B2B sale")
    public ResponseEntity<B2BSaleProductDTO> createProduct(@Valid @RequestBody B2BSaleProductDTO dto) {
        return new ResponseEntity<>(b2bSaleProductService.createProduct(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(summary = "Update B2B sale product")
    public ResponseEntity<B2BSaleProductDTO> updateProduct(@PathVariable Long id, @Valid @RequestBody B2BSaleProductDTO dto) {
        return ResponseEntity.ok(b2bSaleProductService.updateProduct(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Delete product from B2B sale")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        b2bSaleProductService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
