package org.clickenrent.paymentservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.paymentservice.dto.PayoutFinTransactionDTO;
import org.clickenrent.paymentservice.service.PayoutFinTransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payout-fin-transactions")
@RequiredArgsConstructor
@Tag(name = "Payout Financial Transaction", description = "Payout financial transaction management API")
public class PayoutFinTransactionController {

    private final PayoutFinTransactionService payoutFinTransactionService;

    @GetMapping
    @Operation(summary = "Get all payout transactions")
    public ResponseEntity<List<PayoutFinTransactionDTO>> getAll() {
        return ResponseEntity.ok(payoutFinTransactionService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get payout transaction by ID")
    public ResponseEntity<PayoutFinTransactionDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(payoutFinTransactionService.findById(id));
    }

    @GetMapping("/external/{externalId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get payout transaction by external ID")
    public ResponseEntity<PayoutFinTransactionDTO> getByExternalId(@PathVariable String externalId) {
        return ResponseEntity.ok(payoutFinTransactionService.findByExternalId(externalId));
    }

    @PostMapping
    @Operation(summary = "Create new payout transaction")
    public ResponseEntity<PayoutFinTransactionDTO> create(@Valid @RequestBody PayoutFinTransactionDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(payoutFinTransactionService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update payout transaction")
    public ResponseEntity<PayoutFinTransactionDTO> update(@PathVariable Long id, @Valid @RequestBody PayoutFinTransactionDTO dto) {
        return ResponseEntity.ok(payoutFinTransactionService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete payout transaction")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        payoutFinTransactionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}


