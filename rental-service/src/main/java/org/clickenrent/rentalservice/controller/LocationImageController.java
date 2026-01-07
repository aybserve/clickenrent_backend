package org.clickenrent.rentalservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.LocationImageDTO;
import org.clickenrent.rentalservice.service.LocationImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/location-images")
@RequiredArgsConstructor
@Tag(name = "Location Image", description = "Location image management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class LocationImageController {

    private final LocationImageService locationImageService;

    @GetMapping("/by-location/{locationId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get images by location")
    public ResponseEntity<List<LocationImageDTO>> getImagesByLocation(@PathVariable Long locationId) {
        return ResponseEntity.ok(locationImageService.getImagesByLocation(locationId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get image by ID")
    public ResponseEntity<LocationImageDTO> getImageById(@PathVariable Long id) {
        return ResponseEntity.ok(locationImageService.getImageById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(summary = "Create location image")
    public ResponseEntity<LocationImageDTO> createImage(@Valid @RequestBody LocationImageDTO dto) {
        return new ResponseEntity<>(locationImageService.createImage(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(summary = "Update location image")
    public ResponseEntity<LocationImageDTO> updateImage(@PathVariable Long id, @Valid @RequestBody LocationImageDTO dto) {
        return ResponseEntity.ok(locationImageService.updateImage(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(summary = "Delete location image")
    public ResponseEntity<Void> deleteImage(@PathVariable Long id) {
        locationImageService.deleteImage(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/external/{externalId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get location image by external ID", description = "Retrieve location image by external ID for cross-service communication")
    public ResponseEntity<LocationImageDTO> getByExternalId(@PathVariable String externalId) {
        return ResponseEntity.ok(locationImageService.getImageByExternalId(externalId));
    }
}








