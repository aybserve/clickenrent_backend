package org.clickenrent.authservice.mapper;

import org.clickenrent.authservice.dto.CountryDTO;
import org.clickenrent.authservice.entity.Country;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between Country entity and CountryDTO.
 */
@Component
public class CountryMapper {
    
    public CountryDTO toDto(Country country) {
        if (country == null) {
            return null;
        }
        
        return CountryDTO.builder()
                .id(country.getId())
                .name(country.getName())
                .build();
    }
    
    public Country toEntity(CountryDTO dto) {
        if (dto == null) {
            return null;
        }
        
        return Country.builder()
                .id(dto.getId())
                .name(dto.getName())
                .build();
    }
    
    public void updateEntityFromDto(CountryDTO dto, Country country) {
        if (dto == null || country == null) {
            return;
        }
        
        if (dto.getName() != null) {
            country.setName(dto.getName());
        }
    }
}





