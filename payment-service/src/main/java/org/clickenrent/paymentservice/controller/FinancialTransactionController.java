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

@RestController
@RequestMapping("/api/v1/financial-transactions")
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
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    @Operation(summary = "Get financial transaction by ID")
    public ResponseEntity<FinancialTransactionDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(financialTransactionService.findById(id));
    }

    @GetMapping("/external/{externalId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    @Operation(summary = "Get financial transaction by external ID")
    public ResponseEntity<FinancialTransactionDTO> getByExternalId(@PathVariable String externalId) {
        return ResponseEntity.ok(financialTransactionService.findByExternalId(externalId));
    }

    @PutMapping("/external/{externalId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Update financial transaction by external ID", description = "Admin only - updates payment status")
    public ResponseEntity<FinancialTransactionDTO> updateByExternalId(
            @PathVariable String externalId,
            @Valid @RequestBody FinancialTransactionDTO dto) {
        return ResponseEntity.ok(financialTransactionService.updateByExternalId(externalId, dto));
    }

    @DeleteMapping("/external/{externalId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    @Operation(summary = "Delete financial transaction by external ID", description = "SUPERADMIN only - for data correction")
    public ResponseEntity<Void> deleteByExternalId(@PathVariable String externalId) {
        financialTransactionService.deleteByExternalId(externalId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/payer/{payerExternalId}")
    @Operation(summary = "Get transactions by payer external ID")
    public ResponseEntity<List<FinancialTransactionDTO>> getByPayerExternalId(@PathVariable String payerExternalId) {
        return ResponseEntity.ok(financialTransactionService.findByPayerExternalId(payerExternalId));
    }
    
    @GetMapping("/recipient/{recipientExternalId}")
    @Operation(summary = "Get transactions by recipient external ID")
    public ResponseEntity<List<FinancialTransactionDTO>> getByRecipientExternalId(@PathVariable String recipientExternalId) {
        return ResponseEntity.ok(financialTransactionService.findByRecipientExternalId(recipientExternalId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
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
