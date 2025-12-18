package org.clickenrent.paymentservice.mapper;

import org.clickenrent.paymentservice.dto.ServiceProviderDTO;
import org.clickenrent.paymentservice.entity.ServiceProvider;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for ServiceProvider entity and DTO
 */
@Component
public class ServiceProviderMapper {

    public ServiceProviderDTO toDTO(ServiceProvider entity) {
        if (entity == null) {
            return null;
        }
        
        return ServiceProviderDTO.builder()
                .id(entity.getId())
                .externalId(entity.getExternalId())
                .code(entity.getCode())
                .name(entity.getName())
                .build();
    }

    public ServiceProvider toEntity(ServiceProviderDTO dto) {
        if (dto == null) {
            return null;
        }
        
        return ServiceProvider.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .code(dto.getCode())
                .name(dto.getName())
                .build();
    }

    public List<ServiceProviderDTO> toDTOList(List<ServiceProvider> entities) {
        if (entities == null) {
            return List.of();
        }
        
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}

