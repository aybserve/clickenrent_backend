package org.clickenrent.paymentservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.paymentservice.dto.B2BRevenueSharePayoutDTO;
import org.clickenrent.paymentservice.service.B2BRevenueSharePayoutService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/b2b-revenue-share-payouts")
@RequiredArgsConstructor
@Tag(name = "B2B Revenue Share Payout", description = "B2B revenue share payout management API")
public class B2BRevenueSharePayoutController {

    private final B2BRevenueSharePayoutService b2bRevenueSharePayoutService;

    @GetMapping
    @Operation(summary = "Get all B2B revenue share payouts")
    public ResponseEntity<List<B2BRevenueSharePayoutDTO>> getAll() {
        return ResponseEntity.ok(b2bRevenueSharePayoutService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payout by ID")
    public ResponseEntity<B2BRevenueSharePayoutDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(b2bRevenueSharePayoutService.findById(id));
    }

    @GetMapping("/external/{externalId}")
    @Operation(summary = "Get payout by external ID")
    public ResponseEntity<B2BRevenueSharePayoutDTO> getByExternalId(@PathVariable UUID externalId) {
        return ResponseEntity.ok(b2bRevenueSharePayoutService.findByExternalId(externalId));
    }

    @GetMapping("/company/{companyId}")
    @Operation(summary = "Get payouts by company ID")
    public ResponseEntity<List<B2BRevenueSharePayoutDTO>> getByCompanyId(@PathVariable Long companyId) {
        return ResponseEntity.ok(b2bRevenueSharePayoutService.findByCompanyId(companyId));
    }

    @PostMapping
    @Operation(summary = "Create new payout")
    public ResponseEntity<B2BRevenueSharePayoutDTO> create(@Valid @RequestBody B2BRevenueSharePayoutDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(b2bRevenueSharePayoutService.create(dto));
    }

    @PostMapping("/calculate")
    @Operation(summary = "Calculate payout for company")
    public ResponseEntity<B2BRevenueSharePayoutDTO> calculatePayout(
            @RequestParam Long companyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(b2bRevenueSharePayoutService.calculatePayout(companyId, startDate, endDate));
    }

    @PostMapping("/{id}/process")
    @Operation(summary = "Process payout")
    public ResponseEntity<B2BRevenueSharePayoutDTO> processPayout(@PathVariable Long id) {
        return ResponseEntity.ok(b2bRevenueSharePayoutService.processPayout(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update payout")
    public ResponseEntity<B2BRevenueSharePayoutDTO> update(@PathVariable Long id, @Valid @RequestBody B2BRevenueSharePayoutDTO dto) {
        return ResponseEntity.ok(b2bRevenueSharePayoutService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete payout")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        b2bRevenueSharePayoutService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
