package org.clickenrent.paymentservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.paymentservice.dto.LocationBankAccountDTO;
import org.clickenrent.paymentservice.service.LocationBankAccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for managing location bank accounts
 * Used for payout processing
 */
@RestController
@RequestMapping("/api/v1/location-bank-accounts")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Location Bank Accounts", description = "Manage bank account information for location payouts")
@SecurityRequirement(name = "bearerAuth")
public class LocationBankAccountController {
    
    private final LocationBankAccountService locationBankAccountService;
    
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(
        summary = "Create location bank account",
        description = "Register bank account details for a location to enable automatic payouts"
    )
    public ResponseEntity<LocationBankAccountDTO> createLocationBankAccount(
            @Valid @RequestBody LocationBankAccountDTO dto) {
        log.info("Creating location bank account for location: {}", dto.getLocationExternalId());
        LocationBankAccountDTO created = locationBankAccountService.createLocationBankAccount(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @GetMapping("/location/{locationExternalId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'B2B_CLIENT')")
    @Operation(
        summary = "Get bank account by location external ID",
        description = "Retrieve bank account details for a specific location"
    )
    public ResponseEntity<LocationBankAccountDTO> getByLocationExternalId(
            @PathVariable String locationExternalId) {
        log.debug("Fetching bank account for location: {}", locationExternalId);
        LocationBankAccountDTO bankAccount = locationBankAccountService.getByLocationExternalId(locationExternalId);
        
        if (bankAccount == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(bankAccount);
    }
    
    @GetMapping("/{externalId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'B2B_CLIENT')")
    @Operation(
        summary = "Get bank account by external ID",
        description = "Retrieve bank account details by its external ID"
    )
    public ResponseEntity<LocationBankAccountDTO> getByExternalId(
            @PathVariable String externalId) {
        log.debug("Fetching bank account by external ID: {}", externalId);
        LocationBankAccountDTO bankAccount = locationBankAccountService.getByExternalId(externalId);
        
        if (bankAccount == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(bankAccount);
    }
    
    @GetMapping("/company/{companyExternalId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(
        summary = "Get all bank accounts for a company",
        description = "Retrieve all bank accounts for all locations of a specific company"
    )
    public ResponseEntity<List<LocationBankAccountDTO>> getAllByCompanyExternalId(
            @PathVariable String companyExternalId) {
        log.debug("Fetching all bank accounts for company: {}", companyExternalId);
        List<LocationBankAccountDTO> bankAccounts = locationBankAccountService.getAllByCompanyExternalId(companyExternalId);
        return ResponseEntity.ok(bankAccounts);
    }
    
    @PutMapping("/{externalId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(
        summary = "Update location bank account",
        description = "Update bank account details for a location"
    )
    public ResponseEntity<LocationBankAccountDTO> updateLocationBankAccount(
            @PathVariable String externalId,
            @Valid @RequestBody LocationBankAccountDTO dto) {
        log.info("Updating location bank account: {}", externalId);
        LocationBankAccountDTO updated = locationBankAccountService.updateLocationBankAccount(externalId, dto);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/{externalId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(
        summary = "Deactivate location bank account",
        description = "Deactivate a bank account (soft delete). Payouts will no longer be sent to this account."
    )
    public ResponseEntity<Map<String, Object>> deactivateLocationBankAccount(
            @PathVariable String externalId) {
        log.info("Deactivating location bank account: {}", externalId);
        locationBankAccountService.deactivateLocationBankAccount(externalId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Bank account deactivated successfully");
        response.put("externalId", externalId);
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{externalId}/verify")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(
        summary = "Verify location bank account",
        description = "Mark a bank account as verified after manual verification process"
    )
    public ResponseEntity<LocationBankAccountDTO> verifyLocationBankAccount(
            @PathVariable String externalId,
            @RequestBody(required = false) Map<String, String> requestBody) {
        log.info("Verifying location bank account: {}", externalId);
        
        String notes = requestBody != null ? requestBody.get("notes") : null;
        LocationBankAccountDTO verified = locationBankAccountService.verifyLocationBankAccount(externalId, notes);
        
        return ResponseEntity.ok(verified);
    }
}
