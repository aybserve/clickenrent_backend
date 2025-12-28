package org.clickenrent.paymentservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.paymentservice.dto.B2BRevenueSharePayoutItemDTO;
import org.clickenrent.paymentservice.service.B2BRevenueSharePayoutItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/b2b-revenue-share-payout-items")
@RequiredArgsConstructor
@Tag(name = "B2B Revenue Share Payout Item", description = "B2B revenue share payout item management API")
public class B2BRevenueSharePayoutItemController {

    private final B2BRevenueSharePayoutItemService payoutItemService;

    @GetMapping
    @Operation(summary = "Get all payout items")
    public ResponseEntity<List<B2BRevenueSharePayoutItemDTO>> getAll() {
        return ResponseEntity.ok(payoutItemService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get payout item by ID")
    public ResponseEntity<B2BRevenueSharePayoutItemDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(payoutItemService.findById(id));
    }

    @GetMapping("/external/{externalId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get payout item by external ID")
    public ResponseEntity<B2BRevenueSharePayoutItemDTO> getByExternalId(@PathVariable String externalId) {
        return ResponseEntity.ok(payoutItemService.findByExternalId(externalId));
    }

    @GetMapping("/payout/{payoutId}")
    @Operation(summary = "Get payout items by payout ID")
    public ResponseEntity<List<B2BRevenueSharePayoutItemDTO>> getByPayoutId(@PathVariable Long payoutId) {
        return ResponseEntity.ok(payoutItemService.findByPayoutId(payoutId));
    }

    @PostMapping
    @Operation(summary = "Create new payout item")
    public ResponseEntity<B2BRevenueSharePayoutItemDTO> create(@Valid @RequestBody B2BRevenueSharePayoutItemDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(payoutItemService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update payout item")
    public ResponseEntity<B2BRevenueSharePayoutItemDTO> update(@PathVariable Long id, @Valid @RequestBody B2BRevenueSharePayoutItemDTO dto) {
        return ResponseEntity.ok(payoutItemService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete payout item")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        payoutItemService.delete(id);
        return ResponseEntity.noContent().build();
    }
}








