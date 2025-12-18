package org.clickenrent.supportservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.supportservice.dto.SupportRequestGuideItemDTO;
import org.clickenrent.supportservice.service.SupportRequestGuideItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing SupportRequestGuideItem entities.
 */
@RestController
@RequestMapping("/api/support-request-guide-items")
@RequiredArgsConstructor
@Tag(name = "Support Request Guide Item", description = "Support request guide item management")
@SecurityRequirement(name = "bearerAuth")
public class SupportRequestGuideItemController {

    private final SupportRequestGuideItemService supportRequestGuideItemService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all guide items")
    public ResponseEntity<List<SupportRequestGuideItemDTO>> getAll() {
        return ResponseEntity.ok(supportRequestGuideItemService.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get guide item by ID")
    public ResponseEntity<SupportRequestGuideItemDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(supportRequestGuideItemService.getById(id));
    }

    @GetMapping("/bike-issue/{bikeIssueId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get guide items by bike issue ID")
    public ResponseEntity<List<SupportRequestGuideItemDTO>> getByBikeIssueId(@PathVariable Long bikeIssueId) {
        return ResponseEntity.ok(supportRequestGuideItemService.getByBikeIssueId(bikeIssueId));
    }

    @GetMapping("/bike-issue/{bikeIssueId}/status/{statusId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get guide items by bike issue and status")
    public ResponseEntity<List<SupportRequestGuideItemDTO>> getByBikeIssueAndStatus(
            @PathVariable Long bikeIssueId, @PathVariable Long statusId) {
        return ResponseEntity.ok(supportRequestGuideItemService.getByBikeIssueAndStatus(bikeIssueId, statusId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Create guide item")
    public ResponseEntity<SupportRequestGuideItemDTO> create(@Valid @RequestBody SupportRequestGuideItemDTO dto) {
        return new ResponseEntity<>(supportRequestGuideItemService.create(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Update guide item")
    public ResponseEntity<SupportRequestGuideItemDTO> update(@PathVariable Long id, @Valid @RequestBody SupportRequestGuideItemDTO dto) {
        return ResponseEntity.ok(supportRequestGuideItemService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Delete guide item")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        supportRequestGuideItemService.delete(id);
        return ResponseEntity.noContent().build();
    }
}


