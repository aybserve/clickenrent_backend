package org.clickenrent.paymentservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.clickenrent.paymentservice.dto.RefundStatusDTO;
import org.clickenrent.paymentservice.service.RefundStatusService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/refund-statuses")
@RequiredArgsConstructor
@Tag(name = "Refund Status", description = "Refund status lookup API")
public class RefundStatusController {

    private final RefundStatusService refundStatusService;

    @GetMapping
    @Operation(summary = "Get all refund statuses")
    public ResponseEntity<List<RefundStatusDTO>> getAll() {
        return ResponseEntity.ok(refundStatusService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get refund status by ID")
    public ResponseEntity<RefundStatusDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(refundStatusService.findById(id));
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get refund status by code")
    public ResponseEntity<RefundStatusDTO> getByCode(@PathVariable String code) {
        return ResponseEntity.ok(refundStatusService.findByCode(code));
    }
}
