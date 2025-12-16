package org.clickenrent.paymentservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.paymentservice.dto.CurrencyDTO;
import org.clickenrent.paymentservice.service.CurrencyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for Currency management
 */
@RestController
@RequestMapping("/api/currencies")
@RequiredArgsConstructor
@Tag(name = "Currency", description = "Currency management API")
public class CurrencyController {

    private final CurrencyService currencyService;

    @GetMapping
    @Operation(summary = "Get all currencies")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved currencies")
    public ResponseEntity<List<CurrencyDTO>> getAllCurrencies() {
        return ResponseEntity.ok(currencyService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get currency by ID")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved currency")
    @ApiResponse(responseCode = "404", description = "Currency not found")
    public ResponseEntity<CurrencyDTO> getCurrencyById(@PathVariable Long id) {
        return ResponseEntity.ok(currencyService.findById(id));
    }

    @GetMapping("/external/{externalId}")
    @Operation(summary = "Get currency by external ID")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved currency")
    @ApiResponse(responseCode = "404", description = "Currency not found")
    public ResponseEntity<CurrencyDTO> getCurrencyByExternalId(@PathVariable UUID externalId) {
        return ResponseEntity.ok(currencyService.findByExternalId(externalId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create new currency")
    @ApiResponse(responseCode = "201", description = "Currency created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    @ApiResponse(responseCode = "409", description = "Currency already exists")
    public ResponseEntity<CurrencyDTO> createCurrency(@Valid @RequestBody CurrencyDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(currencyService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update currency")
    @ApiResponse(responseCode = "200", description = "Currency updated successfully")
    @ApiResponse(responseCode = "404", description = "Currency not found")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    public ResponseEntity<CurrencyDTO> updateCurrency(
            @PathVariable Long id,
            @Valid @RequestBody CurrencyDTO dto) {
        return ResponseEntity.ok(currencyService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete currency")
    @ApiResponse(responseCode = "204", description = "Currency deleted successfully")
    @ApiResponse(responseCode = "404", description = "Currency not found")
    public ResponseEntity<Void> deleteCurrency(@PathVariable Long id) {
        currencyService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
