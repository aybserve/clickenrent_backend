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
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Get address by ID", description = "Retrieve a specific address by its ID")
    public ResponseEntity<AddressDTO> getAddressById(@PathVariable Long id) {
        AddressDTO address = addressService.getAddressById(id);
        return ResponseEntity.ok(address);
    }
    
    /**
     * Get addresses by city ID.
     * GET /api/addresses/by-city/{cityId}
     */
    @GetMapping("/by-city/{cityId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Get addresses by city", description = "Retrieve all addresses in a specific city")
    public ResponseEntity<List<AddressDTO>> getAddressesByCityId(@PathVariable Long cityId) {
        List<AddressDTO> addresses = addressService.getAddressesByCityId(cityId);
        return ResponseEntity.ok(addresses);
    }
    
    /**
     * Create a new address.
     * POST /api/addresses
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Create address", description = "Create a new address")
    public ResponseEntity<AddressDTO> createAddress(@Valid @RequestBody AddressDTO addressDTO) {
        AddressDTO createdAddress = addressService.createAddress(addressDTO);
        return new ResponseEntity<>(createdAddress, HttpStatus.CREATED);
    }
    
    /**
     * Update address by ID.
     * PUT /api/addresses/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
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
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Delete address", description = "Delete an address by its ID")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long id) {
        addressService.deleteAddress(id);
        return ResponseEntity.noContent().build();
    }
}

