package org.clickenrent.authservice.mapper;

import org.clickenrent.authservice.dto.CompanyRoleDTO;
import org.clickenrent.authservice.entity.CompanyRole;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between CompanyRole entity and CompanyRoleDTO.
 */
@Component
public class CompanyRoleMapper {
    
    public CompanyRoleDTO toDto(CompanyRole companyRole) {
        if (companyRole == null) {
            return null;
        }
        
        return CompanyRoleDTO.builder()
                .id(companyRole.getId())
                .name(companyRole.getName())
                .build();
    }
    
    public CompanyRole toEntity(CompanyRoleDTO dto) {
        if (dto == null) {
            return null;
        }
        
        return CompanyRole.builder()
                .id(dto.getId())
                .name(dto.getName())
                .build();
    }
    
    public void updateEntityFromDto(CompanyRoleDTO dto, CompanyRole companyRole) {
        if (dto == null || companyRole == null) {
            return;
        }
        
        if (dto.getName() != null) {
            companyRole.setName(dto.getName());
        }
    }
}


