package org.clickenrent.paymentservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.paymentservice.dto.FinancialTransactionDTO;
import org.clickenrent.paymentservice.service.FinancialTransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/financial-transactions")
@RequiredArgsConstructor
@Tag(name = "Financial Transaction", description = "Financial transaction management and payment processing API")
public class FinancialTransactionController {

    private final FinancialTransactionService financialTransactionService;

    @GetMapping
    @Operation(summary = "Get all financial transactions")
    public ResponseEntity<List<FinancialTransactionDTO>> getAll() {
        return ResponseEntity.ok(financialTransactionService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get financial transaction by ID")
    public ResponseEntity<FinancialTransactionDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(financialTransactionService.findById(id));
    }

    @GetMapping("/external/{externalId}")
    @Operation(summary = "Get financial transaction by external ID")
    public ResponseEntity<FinancialTransactionDTO> getByExternalId(@PathVariable UUID externalId) {
        return ResponseEntity.ok(financialTransactionService.findByExternalId(externalId));
    }

    @GetMapping("/payer/{payerId}")
    @Operation(summary = "Get transactions by payer ID")
    public ResponseEntity<List<FinancialTransactionDTO>> getByPayerId(@PathVariable Long payerId) {
        return ResponseEntity.ok(financialTransactionService.findByPayerId(payerId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create new financial transaction")
    public ResponseEntity<FinancialTransactionDTO> create(@Valid @RequestBody FinancialTransactionDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(financialTransactionService.create(dto));
    }

    @PostMapping("/process")
    @Operation(summary = "Process payment with Stripe integration")
    public ResponseEntity<FinancialTransactionDTO> processPayment(@Valid @RequestBody FinancialTransactionDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(financialTransactionService.processPayment(dto));
    }

    @PostMapping("/{id}/refund")
    @Operation(summary = "Refund a transaction")
    public ResponseEntity<FinancialTransactionDTO> refundTransaction(
            @PathVariable Long id,
            @RequestParam(required = false) BigDecimal amount) {
        return ResponseEntity.ok(financialTransactionService.refundTransaction(id, amount));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update financial transaction")
    public ResponseEntity<FinancialTransactionDTO> update(@PathVariable Long id, @Valid @RequestBody FinancialTransactionDTO dto) {
        return ResponseEntity.ok(financialTransactionService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete financial transaction")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        financialTransactionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
