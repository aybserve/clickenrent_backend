package org.clickenrent.supportservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.supportservice.dto.SupportRequestDTO;
import org.clickenrent.supportservice.service.SupportRequestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing SupportRequest entities.
 */
@RestController
@RequestMapping("/api/v1/support-requests")
@RequiredArgsConstructor
@Tag(name = "Support Request", description = "Support request management")
@SecurityRequirement(name = "bearerAuth")
public class SupportRequestController {

    private final SupportRequestService supportRequestService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all support requests (or user's own requests)")
    public ResponseEntity<List<SupportRequestDTO>> getAll() {
        return ResponseEntity.ok(supportRequestService.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get support request by ID")
    public ResponseEntity<SupportRequestDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(supportRequestService.getById(id));
    }

    @GetMapping("/external/{externalId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get support request by external ID")
    public ResponseEntity<SupportRequestDTO> getByExternalId(@PathVariable String externalId) {
        return ResponseEntity.ok(supportRequestService.getByExternalId(externalId));
    }

    @PutMapping("/external/{externalId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Update support request by external ID")
    public ResponseEntity<SupportRequestDTO> updateByExternalId(
            @PathVariable String externalId,
            @Valid @RequestBody SupportRequestDTO dto) {
        return ResponseEntity.ok(supportRequestService.updateByExternalId(externalId, dto));
    }

    @DeleteMapping("/external/{externalId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Delete support request by external ID")
    public ResponseEntity<Void> deleteByExternalId(@PathVariable String externalId) {
        supportRequestService.deleteByExternalId(externalId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userExternalId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get support requests by user external ID")
    public ResponseEntity<List<SupportRequestDTO>> getByUserExternalId(@PathVariable String userExternalId) {
        return ResponseEntity.ok(supportRequestService.getByUserExternalId(userExternalId));
    }

    @GetMapping("/bike/{bikeExternalId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get support requests by bike external ID")
    public ResponseEntity<List<SupportRequestDTO>> getByBikeExternalId(@PathVariable String bikeExternalId) {
        return ResponseEntity.ok(supportRequestService.getByBikeExternalId(bikeExternalId));
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Create support request")
    public ResponseEntity<SupportRequestDTO> create(@Valid @RequestBody SupportRequestDTO dto) {
        return new ResponseEntity<>(supportRequestService.create(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Update support request")
    public ResponseEntity<SupportRequestDTO> update(@PathVariable Long id, @Valid @RequestBody SupportRequestDTO dto) {
        return ResponseEntity.ok(supportRequestService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Delete support request")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        supportRequestService.delete(id);
        return ResponseEntity.noContent().build();
    }
}








