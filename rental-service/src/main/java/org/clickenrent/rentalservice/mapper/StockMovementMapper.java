package org.clickenrent.rentalservice.mapper;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.StockMovementDTO;
import org.clickenrent.rentalservice.entity.StockMovement;
import org.clickenrent.rentalservice.repository.HubRepository;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between StockMovement entity and StockMovementDTO.
 */
@Component
@RequiredArgsConstructor
public class StockMovementMapper {

    private final HubRepository hubRepository;

    public StockMovementDTO toDto(StockMovement stockMovement) {
        if (stockMovement == null) {
            return null;
        }

        return StockMovementDTO.builder()
                .id(stockMovement.getId())
                .externalId(stockMovement.getExternalId())
                .productId(stockMovement.getProductId())
                .fromHubId(stockMovement.getFromHub() != null ? stockMovement.getFromHub().getId() : null)
                .toHubId(stockMovement.getToHub() != null ? stockMovement.getToHub().getId() : null)
                .dateTime(stockMovement.getDateTime())
                .build();
    }

    public StockMovement toEntity(StockMovementDTO dto) {
        if (dto == null) {
            return null;
        }

        StockMovement.StockMovementBuilder builder = StockMovement.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .productId(dto.getProductId())
                .dateTime(dto.getDateTime());

        if (dto.getFromHubId() != null) {
            builder.fromHub(hubRepository.findById(dto.getFromHubId()).orElse(null));
        }
        if (dto.getToHubId() != null) {
            builder.toHub(hubRepository.findById(dto.getToHubId()).orElse(null));
        }

        return builder.build();
    }
}







