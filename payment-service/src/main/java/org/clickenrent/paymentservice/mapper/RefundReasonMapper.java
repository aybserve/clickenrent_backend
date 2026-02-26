package org.clickenrent.paymentservice.mapper;

import org.clickenrent.paymentservice.dto.RefundReasonDTO;
import org.clickenrent.paymentservice.entity.RefundReason;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for RefundReason entity and DTO
 */
@Component
public class RefundReasonMapper {

    public RefundReasonDTO toDTO(RefundReason entity) {
        if (entity == null) {
            return null;
        }
        
        return RefundReasonDTO.builder()
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

    public RefundReason toEntity(RefundReasonDTO dto) {
        if (dto == null) {
            return null;
        }
        
        return RefundReason.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .code(dto.getCode())
                .name(dto.getName())
                .build();
    }

    public List<RefundReasonDTO> toDTOList(List<RefundReason> entities) {
        if (entities == null) {
            return List.of();
        }
        
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
