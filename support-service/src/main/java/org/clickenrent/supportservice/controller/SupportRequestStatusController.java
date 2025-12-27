package org.clickenrent.supportservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.supportservice.dto.SupportRequestStatusDTO;
import org.clickenrent.supportservice.service.SupportRequestStatusService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing SupportRequestStatus entities.
 */
@RestController
@RequestMapping("/api/support-request-statuses")
@RequiredArgsConstructor
@Tag(name = "Support Request Status", description = "Support request status management")
@SecurityRequirement(name = "bearerAuth")
public class SupportRequestStatusController {

    private final SupportRequestStatusService supportRequestStatusService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all support request statuses")
    public ResponseEntity<List<SupportRequestStatusDTO>> getAll() {
        return ResponseEntity.ok(supportRequestStatusService.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get support request status by ID")
    public ResponseEntity<SupportRequestStatusDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(supportRequestStatusService.getById(id));
    }

    @GetMapping("/name/{name}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get support request status by name")
    public ResponseEntity<SupportRequestStatusDTO> getByName(@PathVariable String name) {
        return ResponseEntity.ok(supportRequestStatusService.getByName(name));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Create support request status")
    public ResponseEntity<SupportRequestStatusDTO> create(@Valid @RequestBody SupportRequestStatusDTO dto) {
        return new ResponseEntity<>(supportRequestStatusService.create(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Update support request status")
    public ResponseEntity<SupportRequestStatusDTO> update(@PathVariable Long id, @Valid @RequestBody SupportRequestStatusDTO dto) {
        return ResponseEntity.ok(supportRequestStatusService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Delete support request status")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        supportRequestStatusService.delete(id);
        return ResponseEntity.noContent().build();
    }
}







