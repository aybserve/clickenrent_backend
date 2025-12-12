package org.clickenrent.authservice.mapper;

import org.clickenrent.authservice.dto.UserGlobalRoleDTO;
import org.clickenrent.authservice.entity.UserGlobalRole;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between UserGlobalRole entity and UserGlobalRoleDTO.
 */
@Component
public class UserGlobalRoleMapper {
    
    public UserGlobalRoleDTO toDto(UserGlobalRole userGlobalRole) {
        if (userGlobalRole == null) {
            return null;
        }
        
        return UserGlobalRoleDTO.builder()
                .id(userGlobalRole.getId())
                .userId(userGlobalRole.getUser() != null ? userGlobalRole.getUser().getId() : null)
                .globalRoleId(userGlobalRole.getGlobalRole() != null ? userGlobalRole.getGlobalRole().getId() : null)
                .dateCreated(userGlobalRole.getDateCreated())
                .lastDateModified(userGlobalRole.getLastDateModified())
                .createdBy(userGlobalRole.getCreatedBy())
                .lastModifiedBy(userGlobalRole.getLastModifiedBy())
                .build();
    }
    
    /**
     * Converts DTO to entity.
     * NOTE: This method only sets the ID. User and GlobalRole relationships
     * must be set separately by the service layer when creating new entities.
     * This is intentional to avoid loading entities unnecessarily in the mapper.
     */
    public UserGlobalRole toEntity(UserGlobalRoleDTO dto) {
        if (dto == null) {
            return null;
        }
        
        return UserGlobalRole.builder()
                .id(dto.getId())
                // User and GlobalRole must be set by the service layer
                .build();
    }
}


