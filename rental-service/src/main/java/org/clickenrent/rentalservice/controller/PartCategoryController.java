package org.clickenrent.rentalservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.PartCategoryDTO;
import org.clickenrent.rentalservice.service.PartCategoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/part-categories")
@RequiredArgsConstructor
@Tag(name = "PartCategory", description = "Part category management with hierarchical structure")
@SecurityRequirement(name = "bearerAuth")
public class PartCategoryController {

    private final PartCategoryService partCategoryService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all part categories")
    public ResponseEntity<Page<PartCategoryDTO>> getAllCategories(
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(partCategoryService.getAllCategories(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get category by ID")
    public ResponseEntity<PartCategoryDTO> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(partCategoryService.getCategoryById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Create part category")
    public ResponseEntity<PartCategoryDTO> createCategory(@Valid @RequestBody PartCategoryDTO dto) {
        return new ResponseEntity<>(partCategoryService.createCategory(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Update category")
    public ResponseEntity<PartCategoryDTO> updateCategory(@PathVariable Long id, @Valid @RequestBody PartCategoryDTO dto) {
        return ResponseEntity.ok(partCategoryService.updateCategory(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Delete category")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        partCategoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}

