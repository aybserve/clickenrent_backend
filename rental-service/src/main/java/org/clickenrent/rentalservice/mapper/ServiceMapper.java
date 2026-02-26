package org.clickenrent.rentalservice.mapper;

import org.clickenrent.rentalservice.dto.ServiceDTO;
import org.clickenrent.rentalservice.entity.Service;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between Service entity and ServiceDTO.
 */
@Component
public class ServiceMapper {

    public ServiceDTO toDto(Service service) {
        if (service == null) {
            return null;
        }

        return ServiceDTO.builder()
                .id(service.getId())
                .externalId(service.getExternalId())
                .name(service.getName())
                .dateCreated(service.getDateCreated())
                .lastDateModified(service.getLastDateModified())
                .createdBy(service.getCreatedBy())
                .lastModifiedBy(service.getLastModifiedBy())
                .build();
    }

    public Service toEntity(ServiceDTO dto) {
        if (dto == null) {
            return null;
        }

        return Service.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .name(dto.getName())
                .build();
    }

    public void updateEntityFromDto(ServiceDTO dto, Service service) {
        if (dto == null || service == null) {
            return;
        }

        if (dto.getExternalId() != null) {
            service.setExternalId(dto.getExternalId());
        }
        if (dto.getName() != null) {
            service.setName(dto.getName());
        }
    }
}
