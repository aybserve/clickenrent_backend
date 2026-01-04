package org.clickenrent.rentalservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.B2BSaleItemDTO;
import org.clickenrent.rentalservice.service.B2BSaleItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/b2b-sale-items")
@RequiredArgsConstructor
@Tag(name = "B2BSaleItem", description = "B2B sale items management")
@SecurityRequirement(name = "bearerAuth")
public class B2BSaleItemController {

    private final B2BSaleItemService b2bSaleItemService;

    @GetMapping("/by-sale/{b2bSaleId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(summary = "Get items by B2B sale")
    public ResponseEntity<List<B2BSaleItemDTO>> getItemsBySale(@PathVariable Long b2bSaleId) {
        return ResponseEntity.ok(b2bSaleItemService.getItemsBySale(b2bSaleId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(summary = "Get item by ID")
    public ResponseEntity<B2BSaleItemDTO> getItemById(@PathVariable Long id) {
        return ResponseEntity.ok(b2bSaleItemService.getItemById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(summary = "Add item to B2B sale")
    public ResponseEntity<B2BSaleItemDTO> createItem(@Valid @RequestBody B2BSaleItemDTO dto) {
        return new ResponseEntity<>(b2bSaleItemService.createItem(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(summary = "Update B2B sale item")
    public ResponseEntity<B2BSaleItemDTO> updateItem(@PathVariable Long id, @Valid @RequestBody B2BSaleItemDTO dto) {
        return ResponseEntity.ok(b2bSaleItemService.updateItem(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Delete item from B2B sale")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        b2bSaleItemService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }
}

