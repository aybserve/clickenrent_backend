package org.clickenrent.paymentservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.paymentservice.dto.RentalFinTransactionDTO;
import org.clickenrent.paymentservice.service.RentalFinTransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rental-fin-transactions")
@RequiredArgsConstructor
@Tag(name = "Rental Financial Transaction", description = "Rental financial transaction management API")
public class RentalFinTransactionController {

    private final RentalFinTransactionService rentalFinTransactionService;

    @GetMapping
    @Operation(summary = "Get all rental financial transactions")
    public ResponseEntity<List<RentalFinTransactionDTO>> getAll() {
        return ResponseEntity.ok(rentalFinTransactionService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get rental transaction by ID")
    public ResponseEntity<RentalFinTransactionDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(rentalFinTransactionService.findById(id));
    }

    @GetMapping("/external/{externalId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get rental transaction by external ID")
    public ResponseEntity<RentalFinTransactionDTO> getByExternalId(@PathVariable String externalId) {
        return ResponseEntity.ok(rentalFinTransactionService.findByExternalId(externalId));
    }

    @PostMapping
    @Operation(summary = "Create new rental transaction")
    public ResponseEntity<RentalFinTransactionDTO> create(@Valid @RequestBody RentalFinTransactionDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rentalFinTransactionService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update rental transaction")
    public ResponseEntity<RentalFinTransactionDTO> update(@PathVariable Long id, @Valid @RequestBody RentalFinTransactionDTO dto) {
        return ResponseEntity.ok(rentalFinTransactionService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete rental transaction")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        rentalFinTransactionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}






