package org.clickenrent.paymentservice.mapper;

import org.clickenrent.paymentservice.dto.RefundStatusDTO;
import org.clickenrent.paymentservice.entity.RefundStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for RefundStatus entity and DTO
 */
@Component
public class RefundStatusMapper {

    public RefundStatusDTO toDTO(RefundStatus entity) {
        if (entity == null) {
            return null;
        }
        
        return RefundStatusDTO.builder()
                .id(entity.getId())
                .externalId(entity.getExternalId())
                .code(entity.getCode())
                .name(entity.getName())
                .dateCreated(entity.getDateCreated())
                .lastDateModified(entity.getLastDateModified())
                .createdBy(entity.getCreatedBy())
                .lastModifiedBy(entity.getLastModifiedBy())
                .build();
    }

    public RefundStatus toEntity(RefundStatusDTO dto) {
        if (dto == null) {
            return null;
        }
        
        return RefundStatus.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .code(dto.getCode())
                .name(dto.getName())
                .build();
    }

    public List<RefundStatusDTO> toDTOList(List<RefundStatus> entities) {
        if (entities == null) {
            return List.of();
        }
        
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
