package org.clickenrent.rentalservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.LockStatusDTO;
import org.clickenrent.rentalservice.service.LockStatusService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lock-statuses")
@RequiredArgsConstructor
@Tag(name = "LockStatus", description = "Lock status management (Admin only)")
@SecurityRequirement(name = "bearerAuth")
public class LockStatusController {

    private final LockStatusService lockStatusService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all lock statuses")
    public ResponseEntity<Page<LockStatusDTO>> getAllLockStatuses(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(lockStatusService.getAllLockStatuses(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get lock status by ID")
    public ResponseEntity<LockStatusDTO> getLockStatusById(@PathVariable Long id) {
        return ResponseEntity.ok(lockStatusService.getLockStatusById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Create lock status")
    public ResponseEntity<LockStatusDTO> createLockStatus(@Valid @RequestBody LockStatusDTO dto) {
        return new ResponseEntity<>(lockStatusService.createLockStatus(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Update lock status")
    public ResponseEntity<LockStatusDTO> updateLockStatus(@PathVariable Long id, @Valid @RequestBody LockStatusDTO dto) {
        return ResponseEntity.ok(lockStatusService.updateLockStatus(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Delete lock status")
    public ResponseEntity<Void> deleteLockStatus(@PathVariable Long id) {
        lockStatusService.deleteLockStatus(id);
        return ResponseEntity.noContent().build();
    }
}








