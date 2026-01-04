package org.clickenrent.rentalservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.LockDTO;
import org.clickenrent.rentalservice.dto.LockStatusResponseDTO;
import org.clickenrent.rentalservice.service.LockService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/locks")
@RequiredArgsConstructor
@Tag(name = "Lock", description = "Bike lock management (Admin only)")
@SecurityRequirement(name = "bearerAuth")
public class LockController {

    private final LockService lockService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Get all locks")
    public ResponseEntity<Page<LockDTO>> getAllLocks(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(lockService.getAllLocks(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Get lock by ID")
    public ResponseEntity<LockDTO> getLockById(@PathVariable Long id) {
        return ResponseEntity.ok(lockService.getLockById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Create lock")
    public ResponseEntity<LockDTO> createLock(@Valid @RequestBody LockDTO dto) {
        return new ResponseEntity<>(lockService.createLock(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Update lock")
    public ResponseEntity<LockDTO> updateLock(@PathVariable Long id, @Valid @RequestBody LockDTO dto) {
        return ResponseEntity.ok(lockService.updateLock(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Delete lock")
    public ResponseEntity<Void> deleteLock(@PathVariable Long id) {
        lockService.deleteLock(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{lockId}/status")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get lock status",
            description = "Returns current lock status including battery level and last seen timestamp"
    )
    public ResponseEntity<LockStatusResponseDTO> getLockStatus(@PathVariable Long lockId) {
        LockStatusResponseDTO response = lockService.getLockStatus(lockId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/external/{externalId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get lock by external ID", description = "Retrieve lock by external ID for cross-service communication")
    public ResponseEntity<LockDTO> getByExternalId(@PathVariable String externalId) {
        return ResponseEntity.ok(lockService.findByExternalId(externalId));
    }
}
