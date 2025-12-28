package org.clickenrent.authservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.authservice.dto.LanguageDTO;
import org.clickenrent.authservice.entity.Language;
import org.clickenrent.authservice.exception.ResourceNotFoundException;
import org.clickenrent.authservice.mapper.LanguageMapper;
import org.clickenrent.authservice.repository.LanguageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing Language entities.
 */
@Service
@RequiredArgsConstructor
public class LanguageService {
    
    private final LanguageRepository languageRepository;
    private final LanguageMapper languageMapper;
    
    @Transactional(readOnly = true)
    public List<LanguageDTO> getAllLanguages() {
        return languageRepository.findAll().stream()
                .map(languageMapper::toDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public LanguageDTO getLanguageById(Long id) {
        Language language = languageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Language", "id", id));
        return languageMapper.toDto(language);
    }
    
    @Transactional
    public LanguageDTO createLanguage(LanguageDTO languageDTO) {
        Language language = languageMapper.toEntity(languageDTO);
        language = languageRepository.save(language);
        return languageMapper.toDto(language);
    }
    
    @Transactional
    public LanguageDTO updateLanguage(Long id, LanguageDTO languageDTO) {
        Language language = languageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Language", "id", id));
        
        languageMapper.updateEntityFromDto(languageDTO, language);
        language = languageRepository.save(language);
        return languageMapper.toDto(language);
    }
    
    @Transactional
    public void deleteLanguage(Long id) {
        Language language = languageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Language", "id", id));
        languageRepository.delete(language);
    }
}










