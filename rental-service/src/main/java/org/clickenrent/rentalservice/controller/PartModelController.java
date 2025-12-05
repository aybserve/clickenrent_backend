package org.clickenrent.rentalservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.PartModelDTO;
import org.clickenrent.rentalservice.service.PartModelService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/part-models")
@RequiredArgsConstructor
@Tag(name = "PartModel", description = "Part model management")
@SecurityRequirement(name = "bearerAuth")
public class PartModelController {

    private final PartModelService partModelService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all part models")
    public ResponseEntity<Page<PartModelDTO>> getAllModels(
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(partModelService.getAllModels(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get model by ID")
    public ResponseEntity<PartModelDTO> getModelById(@PathVariable Long id) {
        return ResponseEntity.ok(partModelService.getModelById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Create part model")
    public ResponseEntity<PartModelDTO> createModel(@Valid @RequestBody PartModelDTO dto) {
        return new ResponseEntity<>(partModelService.createModel(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Update model")
    public ResponseEntity<PartModelDTO> updateModel(@PathVariable Long id, @Valid @RequestBody PartModelDTO dto) {
        return ResponseEntity.ok(partModelService.updateModel(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Delete model")
    public ResponseEntity<Void> deleteModel(@PathVariable Long id) {
        partModelService.deleteModel(id);
        return ResponseEntity.noContent().build();
    }
}
