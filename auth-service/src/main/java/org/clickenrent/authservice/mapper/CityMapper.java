package org.clickenrent.authservice.mapper;

import org.clickenrent.authservice.dto.CityDTO;
import org.clickenrent.authservice.entity.City;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between City entity and CityDTO.
 */
@Component
public class CityMapper {
    
    public CityDTO toDto(City city) {
        if (city == null) {
            return null;
        }
        
        return CityDTO.builder()
                .id(city.getId())
                .name(city.getName())
                .countryId(city.getCountry() != null ? city.getCountry().getId() : null)
                .build();
    }
    
    public City toEntity(CityDTO dto) {
        if (dto == null) {
            return null;
        }
        
        return City.builder()
                .id(dto.getId())
                .name(dto.getName())
                .build();
    }
    
    public void updateEntityFromDto(CityDTO dto, City city) {
        if (dto == null || city == null) {
            return;
        }
        
        if (dto.getName() != null) {
            city.setName(dto.getName());
        }
    }
}



