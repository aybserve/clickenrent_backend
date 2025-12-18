package org.clickenrent.supportservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.supportservice.dto.ResponsiblePersonDTO;
import org.clickenrent.supportservice.service.ResponsiblePersonService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing ResponsiblePerson entities.
 */
@RestController
@RequestMapping("/api/responsible-persons")
@RequiredArgsConstructor
@Tag(name = "Responsible Person", description = "Responsible person management")
@SecurityRequirement(name = "bearerAuth")
public class ResponsiblePersonController {

    private final ResponsiblePersonService responsiblePersonService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all responsible persons")
    public ResponseEntity<List<ResponsiblePersonDTO>> getAll() {
        return ResponseEntity.ok(responsiblePersonService.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get responsible person by ID")
    public ResponseEntity<ResponsiblePersonDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(responsiblePersonService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Create responsible person")
    public ResponseEntity<ResponsiblePersonDTO> create(@Valid @RequestBody ResponsiblePersonDTO dto) {
        return new ResponseEntity<>(responsiblePersonService.create(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Update responsible person")
    public ResponseEntity<ResponsiblePersonDTO> update(@PathVariable Long id, @Valid @RequestBody ResponsiblePersonDTO dto) {
        return ResponseEntity.ok(responsiblePersonService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(summary = "Delete responsible person")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        responsiblePersonService.delete(id);
        return ResponseEntity.noContent().build();
    }
}


