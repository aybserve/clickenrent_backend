package org.clickenrent.rentalservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.PartTypeDTO;
import org.clickenrent.rentalservice.service.PartTypeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/part-types")
@RequiredArgsConstructor
@Tag(name = "PartType", description = "Part type management (With serial numbers, Without serial numbers)")
@SecurityRequirement(name = "bearerAuth")
public class PartTypeController {

    private final PartTypeService partTypeService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all part types")
    public ResponseEntity<List<PartTypeDTO>> getAllTypes() {
        return ResponseEntity.ok(partTypeService.getAllTypes());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get type by ID")
    public ResponseEntity<PartTypeDTO> getTypeById(@PathVariable Long id) {
        return ResponseEntity.ok(partTypeService.getTypeById(id));
    }
}
