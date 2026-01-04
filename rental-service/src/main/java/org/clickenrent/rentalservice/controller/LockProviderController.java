package org.clickenrent.rentalservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.LockProviderDTO;
import org.clickenrent.rentalservice.service.LockProviderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lock-providers")
@RequiredArgsConstructor
@Tag(name = "Lock Provider", description = "Lock provider/manufacturer management (Admin only)")
@SecurityRequirement(name = "bearerAuth")
public class LockProviderController {

    private final LockProviderService lockProviderService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Get all lock providers")
    public ResponseEntity<Page<LockProviderDTO>> getAllLockProviders(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(lockProviderService.getAllLockProviders(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Get lock provider by ID")
    public ResponseEntity<LockProviderDTO> getLockProviderById(@PathVariable Long id) {
        return ResponseEntity.ok(lockProviderService.getLockProviderById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Create lock provider")
    public ResponseEntity<LockProviderDTO> createLockProvider(@Valid @RequestBody LockProviderDTO dto) {
        return new ResponseEntity<>(lockProviderService.createLockProvider(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Update lock provider")
    public ResponseEntity<LockProviderDTO> updateLockProvider(@PathVariable Long id, @Valid @RequestBody LockProviderDTO dto) {
        return ResponseEntity.ok(lockProviderService.updateLockProvider(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Delete lock provider")
    public ResponseEntity<Void> deleteLockProvider(@PathVariable Long id) {
        lockProviderService.deleteLockProvider(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/external/{externalId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get lock provider by external ID", description = "Retrieve lock provider by external ID for cross-service communication")
    public ResponseEntity<LockProviderDTO> getByExternalId(@PathVariable String externalId) {
        return ResponseEntity.ok(lockProviderService.findByExternalId(externalId));
    }
}








