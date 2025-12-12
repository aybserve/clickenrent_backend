package org.clickenrent.paymentservice.mapper;

import org.clickenrent.paymentservice.dto.B2BRevenueSharePayoutItemDTO;
import org.clickenrent.paymentservice.entity.B2BRevenueSharePayoutItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for B2BRevenueSharePayoutItem entity and DTO
 */
@Component
public class B2BRevenueSharePayoutItemMapper {

    public B2BRevenueSharePayoutItemDTO toDTO(B2BRevenueSharePayoutItem entity) {
        if (entity == null) {
            return null;
        }
        
        return B2BRevenueSharePayoutItemDTO.builder()
                .id(entity.getId())
                .externalId(entity.getExternalId())
                .b2bRevenueSharePayoutId(entity.getB2bRevenueSharePayout() != null ? 
                        entity.getB2bRevenueSharePayout().getId() : null)
                .bikeRentalId(entity.getBikeRentalId())
                .amount(entity.getAmount())
                .build();
    }

    public B2BRevenueSharePayoutItem toEntity(B2BRevenueSharePayoutItemDTO dto) {
        if (dto == null) {
            return null;
        }
        
        return B2BRevenueSharePayoutItem.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .bikeRentalId(dto.getBikeRentalId())
                .amount(dto.getAmount())
                .build();
    }

    public List<B2BRevenueSharePayoutItemDTO> toDTOList(List<B2BRevenueSharePayoutItem> entities) {
        if (entities == null) {
            return List.of();
        }
        
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
