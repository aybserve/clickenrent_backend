package org.clickenrent.paymentservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.paymentservice.dto.CreateRefundRequestDTO;
import org.clickenrent.paymentservice.dto.RefundDTO;
import org.clickenrent.paymentservice.service.RefundService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/refunds")
@RequiredArgsConstructor
@Tag(name = "Refund", description = "Refund management API")
public class RefundController {

    private final RefundService refundService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    @Operation(summary = "Get all refunds", description = "Admin only - retrieves all refunds")
    public ResponseEntity<List<RefundDTO>> getAll() {
        return ResponseEntity.ok(refundService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    @Operation(summary = "Get refund by ID")
    public ResponseEntity<RefundDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(refundService.findById(id));
    }

    @GetMapping("/external/{externalId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    @Operation(summary = "Get refund by external ID")
    public ResponseEntity<RefundDTO> getByExternalId(@PathVariable String externalId) {
        return ResponseEntity.ok(refundService.findByExternalId(externalId));
    }

    @GetMapping("/transaction/{transactionId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    @Operation(summary = "Get refunds by financial transaction ID")
    public ResponseEntity<List<RefundDTO>> getByFinancialTransactionId(@PathVariable Long transactionId) {
        return ResponseEntity.ok(refundService.findByFinancialTransactionId(transactionId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    @Operation(summary = "Create a new refund", description = "Admin only - creates and processes a refund")
    public ResponseEntity<RefundDTO> createRefund(@Valid @RequestBody CreateRefundRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(refundService.createRefund(request));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    @Operation(summary = "Update refund status", description = "Admin only - updates refund status")
    public ResponseEntity<RefundDTO> updateRefundStatus(
            @PathVariable Long id,
            @RequestParam String statusCode) {
        return ResponseEntity.ok(refundService.updateRefundStatus(id, statusCode));
    }

    @DeleteMapping("/external/{externalId}")
    @PreAuthorize("hasRole('SUPERADMIN')")
    @Operation(summary = "Delete refund by external ID", description = "SUPERADMIN only - soft deletes a refund")
    public ResponseEntity<Void> deleteByExternalId(@PathVariable String externalId) {
        refundService.deleteByExternalId(externalId);
        return ResponseEntity.noContent().build();
    }
}
