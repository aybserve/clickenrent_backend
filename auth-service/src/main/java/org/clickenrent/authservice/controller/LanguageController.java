package org.clickenrent.authservice.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.authservice.dto.LanguageDTO;
import org.clickenrent.authservice.service.LanguageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for Language management operations.
 */
@RestController
@RequestMapping("/api/languages")
@RequiredArgsConstructor
@Tag(name = "Language", description = "Language management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class LanguageController {
    
    private final LanguageService languageService;
    
    /**
     * Get all languages.
     * GET /api/languages
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B', 'CUSTOMER')")
    public ResponseEntity<List<LanguageDTO>> getAllLanguages() {
        List<LanguageDTO> languages = languageService.getAllLanguages();
        return ResponseEntity.ok(languages);
    }
    
    /**
     * Get language by ID.
     * GET /api/languages/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'B2B', 'CUSTOMER')")
    public ResponseEntity<LanguageDTO> getLanguageById(@PathVariable Long id) {
        LanguageDTO language = languageService.getLanguageById(id);
        return ResponseEntity.ok(language);
    }
    
    /**
     * Create a new language.
     * POST /api/languages
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<LanguageDTO> createLanguage(@Valid @RequestBody LanguageDTO languageDTO) {
        LanguageDTO createdLanguage = languageService.createLanguage(languageDTO);
        return new ResponseEntity<>(createdLanguage, HttpStatus.CREATED);
    }
    
    /**
     * Update language by ID.
     * PUT /api/languages/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<LanguageDTO> updateLanguage(
            @PathVariable Long id,
            @Valid @RequestBody LanguageDTO languageDTO) {
        LanguageDTO updatedLanguage = languageService.updateLanguage(id, languageDTO);
        return ResponseEntity.ok(updatedLanguage);
    }
    
    /**
     * Delete language by ID.
     * DELETE /api/languages/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<Void> deleteLanguage(@PathVariable Long id) {
        languageService.deleteLanguage(id);
        return ResponseEntity.noContent().build();
    }
}


