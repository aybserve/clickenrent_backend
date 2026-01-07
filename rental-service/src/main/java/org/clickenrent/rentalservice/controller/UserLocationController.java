package org.clickenrent.rentalservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.UserLocationDTO;
import org.clickenrent.rentalservice.service.UserLocationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user-locations")
@RequiredArgsConstructor
@Tag(name = "User Location", description = "User location assignment endpoints")
@SecurityRequirement(name = "bearerAuth")
public class UserLocationController {

    private final UserLocationService userLocationService;

    @GetMapping("/by-user/{userExternalId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get locations for a user")
    public ResponseEntity<List<UserLocationDTO>> getUserLocationsByUserExternalId(@PathVariable String userExternalId) {
        return ResponseEntity.ok(userLocationService.getUserLocationsByUserExternalId(userExternalId));
    }

    @GetMapping("/by-location/{locationId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(summary = "Get users for a location")
    public ResponseEntity<List<UserLocationDTO>> getUserLocationsByLocation(@PathVariable Long locationId) {
        return ResponseEntity.ok(userLocationService.getUserLocationsByLocation(locationId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(summary = "Assign user to location with role")
    public ResponseEntity<UserLocationDTO> assignUserToLocation(@Valid @RequestBody UserLocationDTO dto) {
        return new ResponseEntity<>(userLocationService.assignUserToLocation(dto), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(summary = "Remove user from location")
    public ResponseEntity<Void> removeUserFromLocation(@PathVariable Long id) {
        userLocationService.removeUserFromLocation(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/external/{externalId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get user location by external ID", description = "Retrieve user location by external ID for cross-service communication")
    public ResponseEntity<UserLocationDTO> getByExternalId(@PathVariable String externalId) {
        return ResponseEntity.ok(userLocationService.getUserLocationByExternalId(externalId));
    }
}




