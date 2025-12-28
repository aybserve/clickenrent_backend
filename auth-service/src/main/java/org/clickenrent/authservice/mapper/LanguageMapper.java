package org.clickenrent.authservice.mapper;

import org.clickenrent.authservice.dto.LanguageDTO;
import org.clickenrent.authservice.entity.Language;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between Language entity and LanguageDTO.
 */
@Component
public class LanguageMapper {
    
    public LanguageDTO toDto(Language language) {
        if (language == null) {
            return null;
        }
        
        return LanguageDTO.builder()
                .id(language.getId())
                .name(language.getName())
                .build();
    }
    
    public Language toEntity(LanguageDTO dto) {
        if (dto == null) {
            return null;
        }
        
        return Language.builder()
                .id(dto.getId())
                .name(dto.getName())
                .build();
    }
    
    public void updateEntityFromDto(LanguageDTO dto, Language language) {
        if (dto == null || language == null) {
            return;
        }
        
        if (dto.getName() != null) {
            language.setName(dto.getName());
        }
    }
}










