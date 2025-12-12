package org.clickenrent.rentalservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.HubImageDTO;
import org.clickenrent.rentalservice.service.HubImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hub-images")
@RequiredArgsConstructor
@Tag(name = "HubImage", description = "Hub image management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class HubImageController {

    private final HubImageService hubImageService;

    @GetMapping("/by-hub/{hubId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get images by hub")
    public ResponseEntity<List<HubImageDTO>> getImagesByHub(@PathVariable Long hubId) {
        return ResponseEntity.ok(hubImageService.getImagesByHub(hubId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get image by ID")
    public ResponseEntity<HubImageDTO> getImageById(@PathVariable Long id) {
        return ResponseEntity.ok(hubImageService.getImageById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(summary = "Create hub image")
    public ResponseEntity<HubImageDTO> createImage(@Valid @RequestBody HubImageDTO dto) {
        return new ResponseEntity<>(hubImageService.createImage(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(summary = "Update hub image")
    public ResponseEntity<HubImageDTO> updateImage(@PathVariable Long id, @Valid @RequestBody HubImageDTO dto) {
        return ResponseEntity.ok(hubImageService.updateImage(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B')")
    @Operation(summary = "Delete hub image")
    public ResponseEntity<Void> deleteImage(@PathVariable Long id) {
        hubImageService.deleteImage(id);
        return ResponseEntity.noContent().build();
    }
}
