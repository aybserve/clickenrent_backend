package org.clickenrent.rentalservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.KeyDTO;
import org.clickenrent.rentalservice.service.KeyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/keys")
@RequiredArgsConstructor
@Tag(name = "Key", description = "Lock key management (Admin only)")
@SecurityRequirement(name = "bearerAuth")
public class KeyController {

    private final KeyService keyService;

    @GetMapping("/by-lock/{lockId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Get keys by lock")
    public ResponseEntity<List<KeyDTO>> getKeysByLock(@PathVariable Long lockId) {
        return ResponseEntity.ok(keyService.getKeysByLock(lockId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Get key by ID")
    public ResponseEntity<KeyDTO> getKeyById(@PathVariable Long id) {
        return ResponseEntity.ok(keyService.getKeyById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Create key")
    public ResponseEntity<KeyDTO> createKey(@Valid @RequestBody KeyDTO dto) {
        return new ResponseEntity<>(keyService.createKey(dto), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Delete key")
    public ResponseEntity<Void> deleteKey(@PathVariable Long id) {
        keyService.deleteKey(id);
        return ResponseEntity.noContent().build();
    }
}








