package org.clickenrent.paymentservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.paymentservice.dto.PaymentStatusDTO;
import org.clickenrent.paymentservice.service.PaymentStatusService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payment-statuses")
@RequiredArgsConstructor
@Tag(name = "Payment Status", description = "Payment status management API")
public class PaymentStatusController {

    private final PaymentStatusService paymentStatusService;

    @GetMapping
    @Operation(summary = "Get all payment statuses")
    public ResponseEntity<List<PaymentStatusDTO>> getAll() {
        return ResponseEntity.ok(paymentStatusService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payment status by ID")
    public ResponseEntity<PaymentStatusDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentStatusService.findById(id));
    }

    @GetMapping("/external/{externalId}")
    @Operation(summary = "Get payment status by external ID")
    public ResponseEntity<PaymentStatusDTO> getByExternalId(@PathVariable UUID externalId) {
        return ResponseEntity.ok(paymentStatusService.findByExternalId(externalId));
    }

    @PostMapping
    @Operation(summary = "Create new payment status")
    public ResponseEntity<PaymentStatusDTO> create(@Valid @RequestBody PaymentStatusDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentStatusService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update payment status")
    public ResponseEntity<PaymentStatusDTO> update(@PathVariable Long id, @Valid @RequestBody PaymentStatusDTO dto) {
        return ResponseEntity.ok(paymentStatusService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete payment status")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        paymentStatusService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

