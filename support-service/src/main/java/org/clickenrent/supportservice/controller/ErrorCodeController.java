package org.clickenrent.supportservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.supportservice.dto.ErrorCodeDTO;
import org.clickenrent.supportservice.service.ErrorCodeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing ErrorCode entities.
 */
@RestController
@RequestMapping("/api/error-codes")
@RequiredArgsConstructor
@Tag(name = "Error Code", description = "Error code management")
@SecurityRequirement(name = "bearerAuth")
public class ErrorCodeController {

    private final ErrorCodeService errorCodeService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all error codes")
    public ResponseEntity<List<ErrorCodeDTO>> getAll() {
        return ResponseEntity.ok(errorCodeService.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get error code by ID")
    public ResponseEntity<ErrorCodeDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(errorCodeService.getById(id));
    }

    @GetMapping("/external/{externalId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get error code by external ID")
    public ResponseEntity<ErrorCodeDTO> getByExternalId(@PathVariable String externalId) {
        return ResponseEntity.ok(errorCodeService.getByExternalId(externalId));
    }

    @GetMapping("/bike-engine/{bikeEngineExternalId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get error codes by bike engine external ID")
    public ResponseEntity<List<ErrorCodeDTO>> getByBikeEngineExternalId(@PathVariable String bikeEngineExternalId) {
        return ResponseEntity.ok(errorCodeService.getByBikeEngineExternalId(bikeEngineExternalId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Create error code")
    public ResponseEntity<ErrorCodeDTO> create(@Valid @RequestBody ErrorCodeDTO dto) {
        return new ResponseEntity<>(errorCodeService.create(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Update error code")
    public ResponseEntity<ErrorCodeDTO> update(@PathVariable Long id, @Valid @RequestBody ErrorCodeDTO dto) {
        return ResponseEntity.ok(errorCodeService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Delete error code")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        errorCodeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}


