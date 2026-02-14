package org.clickenrent.authservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.authservice.dto.UserAddressDTO;
import org.clickenrent.authservice.service.UserAddressService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for UserAddress management operations.
 */
@RestController
@RequestMapping("/api/v1/user-addresses")
@RequiredArgsConstructor
@Tag(name = "User Address", description = "User address link management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class UserAddressController {
    
    private final UserAddressService userAddressService;
    
    /**
     * Get all user-address links.
     * GET /api/user-addresses
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Get all user-address links", description = "Retrieve a list of all user-address associations")
    public ResponseEntity<List<UserAddressDTO>> getAllUserAddresses() {
        List<UserAddressDTO> userAddresses = userAddressService.getAllUserAddresses();
        return ResponseEntity.ok(userAddresses);
    }
    
    /**
     * Get user-address link by ID.
     * GET /api/user-addresses/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN') or @resourceSecurity.canAccessUserAddress(#id)")
    @Operation(summary = "Get user-address link by ID", description = "Retrieve a specific user-address link by its ID")
    public ResponseEntity<UserAddressDTO> getUserAddressById(@PathVariable Long id) {
        UserAddressDTO userAddress = userAddressService.getUserAddressById(id);
        return ResponseEntity.ok(userAddress);
    }
    
    /**
     * Get user-address link by external ID.
     * GET /api/user-addresses/external/{externalId}
     */
    @GetMapping("/external/{externalId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN') or @resourceSecurity.canAccessUserAddress(#externalId)")
    @Operation(summary = "Get user-address link by external ID", description = "Retrieve a user-address link by its external ID")
    public ResponseEntity<UserAddressDTO> getUserAddressByExternalId(@PathVariable String externalId) {
        UserAddressDTO userAddress = userAddressService.getUserAddressByExternalId(externalId);
        return ResponseEntity.ok(userAddress);
    }
    
    /**
     * Get all addresses for a user.
     * GET /api/user-addresses/by-user/{userId}
     */
    @GetMapping("/by-user/{userId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN') or @resourceSecurity.canAccessUser(#userId)")
    @Operation(summary = "Get addresses by user", description = "Retrieve all addresses associated with a specific user")
    public ResponseEntity<List<UserAddressDTO>> getUserAddressesByUserId(@PathVariable Long userId) {
        List<UserAddressDTO> userAddresses = userAddressService.getUserAddressesByUserId(userId);
        return ResponseEntity.ok(userAddresses);
    }
    
    /**
     * Get all users for an address.
     * GET /api/user-addresses/by-address/{addressId}
     */
    @GetMapping("/by-address/{addressId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN') or @resourceSecurity.canAccessAddress(#addressId)")
    @Operation(summary = "Get users by address", description = "Retrieve all users associated with a specific address")
    public ResponseEntity<List<UserAddressDTO>> getUserAddressesByAddressId(@PathVariable Long addressId) {
        List<UserAddressDTO> userAddresses = userAddressService.getUserAddressesByAddressId(addressId);
        return ResponseEntity.ok(userAddresses);
    }
    
    /**
     * Link a user to an address.
     * POST /api/user-addresses
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(summary = "Create user-address link", description = "Link a user to an address")
    public ResponseEntity<UserAddressDTO> createUserAddress(@Valid @RequestBody UserAddressDTO userAddressDTO) {
        UserAddressDTO createdUserAddress = userAddressService.createUserAddress(userAddressDTO);
        return new ResponseEntity<>(createdUserAddress, HttpStatus.CREATED);
    }
    
    /**
     * Update user-address link by ID.
     * PUT /api/user-addresses/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN') or @resourceSecurity.canAccessUserAddress(#id)")
    @Operation(summary = "Update user-address link", description = "Update an existing user-address link")
    public ResponseEntity<UserAddressDTO> updateUserAddress(
            @PathVariable Long id,
            @Valid @RequestBody UserAddressDTO userAddressDTO) {
        UserAddressDTO updatedUserAddress = userAddressService.updateUserAddress(id, userAddressDTO);
        return ResponseEntity.ok(updatedUserAddress);
    }
    
    /**
     * Remove user-address link.
     * DELETE /api/user-addresses/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN') or @resourceSecurity.canAccessUserAddress(#id)")
    @Operation(summary = "Delete user-address link", description = "Remove a user-address link by its ID")
    public ResponseEntity<Void> deleteUserAddress(@PathVariable Long id) {
        userAddressService.deleteUserAddress(id);
        return ResponseEntity.noContent().build();
    }
}

