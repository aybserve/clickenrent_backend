package org.clickenrent.paymentservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.paymentservice.dto.PaymentMethodDTO;
import org.clickenrent.paymentservice.service.PaymentMethodService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payment-methods")
@RequiredArgsConstructor
@Tag(name = "Payment Method", description = "Payment method management API")
public class PaymentMethodController {

    private final PaymentMethodService paymentMethodService;

    @GetMapping
    @Operation(summary = "Get all payment methods")
    public ResponseEntity<List<PaymentMethodDTO>> getAll() {
        return ResponseEntity.ok(paymentMethodService.findAll());
    }

    @GetMapping("/active")
    @Operation(summary = "Get active payment methods")
    public ResponseEntity<List<PaymentMethodDTO>> getActive() {
        return ResponseEntity.ok(paymentMethodService.findActivePaymentMethods());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payment method by ID")
    public ResponseEntity<PaymentMethodDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentMethodService.findById(id));
    }

    @GetMapping("/external/{externalId}")
    @Operation(summary = "Get payment method by external ID")
    public ResponseEntity<PaymentMethodDTO> getByExternalId(@PathVariable UUID externalId) {
        return ResponseEntity.ok(paymentMethodService.findByExternalId(externalId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create new payment method")
    public ResponseEntity<PaymentMethodDTO> create(@Valid @RequestBody PaymentMethodDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentMethodService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update payment method")
    public ResponseEntity<PaymentMethodDTO> update(@PathVariable Long id, @Valid @RequestBody PaymentMethodDTO dto) {
        return ResponseEntity.ok(paymentMethodService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete payment method")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        paymentMethodService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
