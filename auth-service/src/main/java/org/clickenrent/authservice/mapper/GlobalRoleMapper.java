package org.clickenrent.authservice.mapper;

import org.clickenrent.authservice.dto.GlobalRoleDTO;
import org.clickenrent.authservice.entity.GlobalRole;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between GlobalRole entity and GlobalRoleDTO.
 */
@Component
public class GlobalRoleMapper {
    
    public GlobalRoleDTO toDto(GlobalRole globalRole) {
        if (globalRole == null) {
            return null;
        }
        
        return GlobalRoleDTO.builder()
                .id(globalRole.getId())
                .name(globalRole.getName())
                .build();
    }
    
    public GlobalRole toEntity(GlobalRoleDTO dto) {
        if (dto == null) {
            return null;
        }
        
        return GlobalRole.builder()
                .id(dto.getId())
                .name(dto.getName())
                .build();
    }
    
    public void updateEntityFromDto(GlobalRoleDTO dto, GlobalRole globalRole) {
        if (dto == null || globalRole == null) {
            return;
        }
        
        if (dto.getName() != null) {
            globalRole.setName(dto.getName());
        }
    }
}








