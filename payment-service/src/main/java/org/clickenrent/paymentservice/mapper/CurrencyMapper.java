package org.clickenrent.paymentservice.mapper;

import org.clickenrent.paymentservice.dto.CurrencyDTO;
import org.clickenrent.paymentservice.entity.Currency;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for Currency entity and DTO
 */
@Component
public class CurrencyMapper {

    public CurrencyDTO toDTO(Currency entity) {
        if (entity == null) {
            return null;
        }
        
        return CurrencyDTO.builder()
                .id(entity.getId())
                .externalId(entity.getExternalId())
                .code(entity.getCode())
                .name(entity.getName())
                .build();
    }

    public Currency toEntity(CurrencyDTO dto) {
        if (dto == null) {
            return null;
        }
        
        return Currency.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .code(dto.getCode())
                .name(dto.getName())
                .build();
    }

    public List<CurrencyDTO> toDTOList(List<Currency> entities) {
        if (entities == null) {
            return List.of();
        }
        
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}




