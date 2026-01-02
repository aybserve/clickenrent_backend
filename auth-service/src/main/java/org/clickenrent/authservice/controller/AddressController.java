package org.clickenrent.authservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.authservice.dto.AddressDTO;
import org.clickenrent.authservice.service.AddressService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for Address management operations.
 */
@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
@Tag(name = "Address", description = "Address management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class AddressController {
    
    private final AddressService addressService;
    
    /**
     * Get all addresses.
     * GET /api/addresses
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Get all addresses", description = "Retrieve a list of all addresses")
    public ResponseEntity<List<AddressDTO>> getAllAddresses() {
        List<AddressDTO> addresses = addressService.getAllAddresses();
        return ResponseEntity.ok(addresses);
    }
    
    /**
     * Get address by ID.
     * GET /api/addresses/{id}
     * Access: SUPERADMIN/ADMIN can see all, B2B/CUSTOMER can see only their own addresses
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN') or @resourceSecurity.canAccessAddress(#id)")
    @Operation(summary = "Get address by ID", description = "Retrieve a specific address by its ID")
    public ResponseEntity<AddressDTO> getAddressById(@PathVariable Long id) {
        AddressDTO address = addressService.getAddressById(id);
        return ResponseEntity.ok(address);
    }
    
    /**
     * Get address by external ID.
     * GET /api/addresses/external/{externalId}
     * Access: SUPERADMIN/ADMIN can see all, B2B/CUSTOMER can see only their own addresses
     */
    @GetMapping("/external/{externalId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B', 'CUSTOMER')")
    @Operation(summary = "Get address by external ID", description = "Retrieve a specific address by its external ID")
    public ResponseEntity<AddressDTO> getAddressByExternalId(@PathVariable String externalId) {
        AddressDTO address = addressService.getAddressByExternalId(externalId);
        return ResponseEntity.ok(address);
    }
    
    /**
     * Create a new address.
     * POST /api/addresses
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B', 'CUSTOMER')")
    @Operation(summary = "Create address", description = "Create a new address")
    public ResponseEntity<AddressDTO> createAddress(@Valid @RequestBody AddressDTO addressDTO) {
        AddressDTO createdAddress = addressService.createAddress(addressDTO);
        return new ResponseEntity<>(createdAddress, HttpStatus.CREATED);
    }
    
    /**
     * Update address by ID.
     * PUT /api/addresses/{id}
     * Access: SUPERADMIN/ADMIN can update all, B2B/CUSTOMER can update only their own addresses
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN') or @resourceSecurity.canAccessAddress(#id)")
    @Operation(summary = "Update address", description = "Update an existing address")
    public ResponseEntity<AddressDTO> updateAddress(
            @PathVariable Long id,
            @Valid @RequestBody AddressDTO addressDTO) {
        AddressDTO updatedAddress = addressService.updateAddress(id, addressDTO);
        return ResponseEntity.ok(updatedAddress);
    }
    
    /**
     * Delete address by ID.
     * DELETE /api/addresses/{id}
     * Access: SUPERADMIN/ADMIN can delete all, B2B/CUSTOMER can delete only their own addresses
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN') or @resourceSecurity.canAccessAddress(#id)")
    @Operation(summary = "Delete address", description = "Delete an address by its ID")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long id) {
        addressService.deleteAddress(id);
        return ResponseEntity.noContent().build();
    }
}

