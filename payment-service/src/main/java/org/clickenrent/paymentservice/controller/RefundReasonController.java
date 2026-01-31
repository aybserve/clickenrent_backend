package org.clickenrent.paymentservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.clickenrent.paymentservice.dto.RefundReasonDTO;
import org.clickenrent.paymentservice.service.RefundReasonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/refund-reasons")
@RequiredArgsConstructor
@Tag(name = "Refund Reason", description = "Refund reason lookup API")
public class RefundReasonController {

    private final RefundReasonService refundReasonService;

    @GetMapping
    @Operation(summary = "Get all refund reasons")
    public ResponseEntity<List<RefundReasonDTO>> getAll() {
        return ResponseEntity.ok(refundReasonService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get refund reason by ID")
    public ResponseEntity<RefundReasonDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(refundReasonService.findById(id));
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get refund reason by code")
    public ResponseEntity<RefundReasonDTO> getByCode(@PathVariable String code) {
        return ResponseEntity.ok(refundReasonService.findByCode(code));
    }
}
