package org.clickenrent.paymentservice.mapper;

import org.clickenrent.paymentservice.dto.PaymentStatusDTO;
import org.clickenrent.paymentservice.entity.PaymentStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for PaymentStatus entity and DTO
 */
@Component
public class PaymentStatusMapper {

    public PaymentStatusDTO toDTO(PaymentStatus entity) {
        if (entity == null) {
            return null;
        }
        
        return PaymentStatusDTO.builder()
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

    public PaymentStatus toEntity(PaymentStatusDTO dto) {
        if (dto == null) {
            return null;
        }
        
        return PaymentStatus.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .code(dto.getCode())
                .name(dto.getName())
                .build();
    }

    public List<PaymentStatusDTO> toDTOList(List<PaymentStatus> entities) {
        if (entities == null) {
            return List.of();
        }
        
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}








