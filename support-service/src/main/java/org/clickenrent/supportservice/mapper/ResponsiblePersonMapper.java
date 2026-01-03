package org.clickenrent.supportservice.mapper;

import org.clickenrent.supportservice.dto.ResponsiblePersonDTO;
import org.clickenrent.supportservice.entity.ResponsiblePerson;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between ResponsiblePerson entity and ResponsiblePersonDTO.
 */
@Component
public class ResponsiblePersonMapper {

    public ResponsiblePersonDTO toDto(ResponsiblePerson entity) {
        if (entity == null) {
            return null;
        }

        return ResponsiblePersonDTO.builder()
                .id(entity.getId())
                .externalId(entity.getExternalId())
                .name(entity.getName())
                .dateCreated(entity.getDateCreated())
                .lastDateModified(entity.getLastDateModified())
                .createdBy(entity.getCreatedBy())
                .lastModifiedBy(entity.getLastModifiedBy())
                .build();
    }

    public ResponsiblePerson toEntity(ResponsiblePersonDTO dto) {
        if (dto == null) {
            return null;
        }

        return ResponsiblePerson.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .name(dto.getName())
                .build();
    }

    public void updateEntityFromDto(ResponsiblePersonDTO dto, ResponsiblePerson entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.getExternalId() != null) {
            entity.setExternalId(dto.getExternalId());
        }
        if (dto.getName() != null) {
            entity.setName(dto.getName());
        }
    }
}








