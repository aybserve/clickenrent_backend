package org.clickenrent.rentalservice.mapper;

import org.clickenrent.rentalservice.dto.LocationRoleDTO;
import org.clickenrent.rentalservice.entity.LocationRole;
import org.springframework.stereotype.Component;

@Component
public class LocationRoleMapper {

    public LocationRoleDTO toDto(LocationRole role) {
        if (role == null) return null;
        return LocationRoleDTO.builder()
                .id(role.getId())
                .externalId(role.getExternalId())
                .name(role.getName())
                .dateCreated(role.getDateCreated())
                .lastDateModified(role.getLastDateModified())
                .createdBy(role.getCreatedBy())
                .lastModifiedBy(role.getLastModifiedBy())
                .build();
    }

    public LocationRole toEntity(LocationRoleDTO dto) {
        if (dto == null) return null;
        return LocationRole.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .name(dto.getName())
                .build();
    }

    public void updateEntityFromDto(LocationRoleDTO dto, LocationRole role) {
        if (dto == null || role == null) return;
        if (dto.getExternalId() != null) role.setExternalId(dto.getExternalId());
        if (dto.getName() != null) role.setName(dto.getName());
    }
}








