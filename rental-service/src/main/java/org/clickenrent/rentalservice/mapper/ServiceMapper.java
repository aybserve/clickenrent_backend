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
                .name(service.getName())
                .b2bSubscriptionPrice(service.getB2bSubscriptionPrice())
                .build();
    }

    public Service toEntity(ServiceDTO dto) {
        if (dto == null) {
            return null;
        }

        return Service.builder()
                .id(dto.getId())
                .name(dto.getName())
                .b2bSubscriptionPrice(dto.getB2bSubscriptionPrice())
                .build();
    }

    public void updateEntityFromDto(ServiceDTO dto, Service service) {
        if (dto == null || service == null) {
            return;
        }

        if (dto.getName() != null) {
            service.setName(dto.getName());
        }
        if (dto.getB2bSubscriptionPrice() != null) {
            service.setB2bSubscriptionPrice(dto.getB2bSubscriptionPrice());
        }
    }
}
