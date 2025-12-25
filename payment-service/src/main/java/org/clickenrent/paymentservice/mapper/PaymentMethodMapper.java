package org.clickenrent.paymentservice.mapper;

import org.clickenrent.paymentservice.dto.PaymentMethodDTO;
import org.clickenrent.paymentservice.entity.PaymentMethod;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for PaymentMethod entity and DTO
 */
@Component
public class PaymentMethodMapper {

    public PaymentMethodDTO toDTO(PaymentMethod entity) {
        if (entity == null) {
            return null;
        }
        
        return PaymentMethodDTO.builder()
                .id(entity.getId())
                .externalId(entity.getExternalId())
                .code(entity.getCode())
                .name(entity.getName())
                .isActive(entity.getIsActive())
                .build();
    }

    public PaymentMethod toEntity(PaymentMethodDTO dto) {
        if (dto == null) {
            return null;
        }
        
        return PaymentMethod.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .code(dto.getCode())
                .name(dto.getName())
                .isActive(dto.getIsActive())
                .build();
    }

    public List<PaymentMethodDTO> toDTOList(List<PaymentMethod> entities) {
        if (entities == null) {
            return List.of();
        }
        
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}






