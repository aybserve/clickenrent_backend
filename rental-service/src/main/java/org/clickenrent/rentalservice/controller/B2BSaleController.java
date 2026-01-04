package org.clickenrent.rentalservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.B2BSaleDTO;
import org.clickenrent.rentalservice.service.B2BSaleService;
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
@RequestMapping("/api/b2b-sales")
@RequiredArgsConstructor
@Tag(name = "B2B Sale", description = "B2B sales management")
@SecurityRequirement(name = "bearerAuth")
public class B2BSaleController {

    private final B2BSaleService b2bSaleService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Get all B2B sales")
    public ResponseEntity<Page<B2BSaleDTO>> getAllSales(
            @PageableDefault(size = 20, sort = "dateTime", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(b2bSaleService.getAllSales(pageable));
    }

    @GetMapping("/by-location/{locationId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(summary = "Get sales by location")
    public ResponseEntity<List<B2BSaleDTO>> getSalesByLocation(@PathVariable Long locationId) {
        return ResponseEntity.ok(b2bSaleService.getSalesByLocation(locationId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(summary = "Get B2B sale by ID")
    public ResponseEntity<B2BSaleDTO> getSaleById(@PathVariable Long id) {
        return ResponseEntity.ok(b2bSaleService.getSaleById(id));
    }

    @GetMapping("/{id}/exists")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Check if B2B sale exists by ID")
    public ResponseEntity<Boolean> checkSaleExists(@PathVariable Long id) {
        boolean exists = b2bSaleService.getSaleById(id) != null;
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/external/{externalId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(summary = "Get B2B sale by external ID")
    public ResponseEntity<B2BSaleDTO> getSaleByExternalId(@PathVariable String externalId) {
        return ResponseEntity.ok(b2bSaleService.findByExternalId(externalId));
    }

    @GetMapping("/external/{externalId}/exists")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Check if B2B sale exists by external ID")
    public ResponseEntity<Boolean> checkSaleExistsByExternalId(@PathVariable String externalId) {
        return ResponseEntity.ok(b2bSaleService.existsByExternalId(externalId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(summary = "Create B2B sale")
    public ResponseEntity<B2BSaleDTO> createSale(@Valid @RequestBody B2BSaleDTO dto) {
        return new ResponseEntity<>(b2bSaleService.createSale(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(summary = "Update B2B sale")
    public ResponseEntity<B2BSaleDTO> updateSale(@PathVariable Long id, @Valid @RequestBody B2BSaleDTO dto) {
        return ResponseEntity.ok(b2bSaleService.updateSale(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Delete B2B sale")
    public ResponseEntity<Void> deleteSale(@PathVariable Long id) {
        b2bSaleService.deleteSale(id);
        return ResponseEntity.noContent().build();
    }
}
