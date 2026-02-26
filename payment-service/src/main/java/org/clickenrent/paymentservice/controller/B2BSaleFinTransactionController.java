package org.clickenrent.paymentservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.paymentservice.dto.B2BSaleFinTransactionDTO;
import org.clickenrent.paymentservice.service.B2BSaleFinTransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/b2b-sale-fin-transactions")
@RequiredArgsConstructor
@Tag(name = "B2B Sale Financial Transaction", description = "B2B sale financial transaction management API")
public class B2BSaleFinTransactionController {

    private final B2BSaleFinTransactionService b2bSaleFinTransactionService;

    @GetMapping
    @Operation(summary = "Get all B2B sale transactions")
    public ResponseEntity<List<B2BSaleFinTransactionDTO>> getAll() {
        return ResponseEntity.ok(b2bSaleFinTransactionService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    @Operation(summary = "Get B2B sale transaction by ID")
    public ResponseEntity<B2BSaleFinTransactionDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(b2bSaleFinTransactionService.findById(id));
    }

    @GetMapping("/external/{externalId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    @Operation(summary = "Get B2B sale transaction by external ID")
    public ResponseEntity<B2BSaleFinTransactionDTO> getByExternalId(@PathVariable String externalId) {
        return ResponseEntity.ok(b2bSaleFinTransactionService.findByExternalId(externalId));
    }

    @PostMapping
    @Operation(summary = "Create new B2B sale transaction")
    public ResponseEntity<B2BSaleFinTransactionDTO> create(@Valid @RequestBody B2BSaleFinTransactionDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(b2bSaleFinTransactionService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update B2B sale transaction")
    public ResponseEntity<B2BSaleFinTransactionDTO> update(@PathVariable Long id, @Valid @RequestBody B2BSaleFinTransactionDTO dto) {
        return ResponseEntity.ok(b2bSaleFinTransactionService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete B2B sale transaction")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        b2bSaleFinTransactionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}








