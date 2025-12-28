package org.clickenrent.paymentservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.paymentservice.dto.B2BSubscriptionFinTransactionDTO;
import org.clickenrent.paymentservice.service.B2BSubscriptionFinTransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/b2b-subscription-fin-transactions")
@RequiredArgsConstructor
@Tag(name = "B2B Subscription Financial Transaction", description = "B2B subscription financial transaction management API")
public class B2BSubscriptionFinTransactionController {

    private final B2BSubscriptionFinTransactionService b2bSubscriptionFinTransactionService;

    @GetMapping
    @Operation(summary = "Get all B2B subscription transactions")
    public ResponseEntity<List<B2BSubscriptionFinTransactionDTO>> getAll() {
        return ResponseEntity.ok(b2bSubscriptionFinTransactionService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get B2B subscription transaction by ID")
    public ResponseEntity<B2BSubscriptionFinTransactionDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(b2bSubscriptionFinTransactionService.findById(id));
    }

    @GetMapping("/external/{externalId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get B2B subscription transaction by external ID")
    public ResponseEntity<B2BSubscriptionFinTransactionDTO> getByExternalId(@PathVariable String externalId) {
        return ResponseEntity.ok(b2bSubscriptionFinTransactionService.findByExternalId(externalId));
    }

    @PostMapping
    @Operation(summary = "Create new B2B subscription transaction")
    public ResponseEntity<B2BSubscriptionFinTransactionDTO> create(@Valid @RequestBody B2BSubscriptionFinTransactionDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(b2bSubscriptionFinTransactionService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update B2B subscription transaction")
    public ResponseEntity<B2BSubscriptionFinTransactionDTO> update(@PathVariable Long id, @Valid @RequestBody B2BSubscriptionFinTransactionDTO dto) {
        return ResponseEntity.ok(b2bSubscriptionFinTransactionService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete B2B subscription transaction")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        b2bSubscriptionFinTransactionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}








