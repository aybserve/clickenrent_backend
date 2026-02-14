package org.clickenrent.authservice.mapper;

import org.clickenrent.authservice.dto.CompanyTypeDTO;
import org.clickenrent.authservice.entity.CompanyType;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between CompanyType entity and CompanyTypeDTO.
 */
@Component
public class CompanyTypeMapper {
    
    public CompanyTypeDTO toDto(CompanyType companyType) {
        if (companyType == null) {
            return null;
        }
        
        return CompanyTypeDTO.builder()
                .id(companyType.getId())
                .externalId(companyType.getExternalId())
                .name(companyType.getName())
                .build();
    }
    
    public CompanyType toEntity(CompanyTypeDTO dto) {
        if (dto == null) {
            return null;
        }
        
        return CompanyType.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .name(dto.getName())
                .build();
    }
    
    public void updateEntityFromDto(CompanyTypeDTO dto, CompanyType companyType) {
        if (dto == null || companyType == null) {
            return;
        }
        
        if (dto.getExternalId() != null) {
            companyType.setExternalId(dto.getExternalId());
        }
        if (dto.getName() != null) {
            companyType.setName(dto.getName());
        }
    }
}










