package org.clickenrent.rentalservice.mapper;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.B2BSubscriptionItemDTO;
import org.clickenrent.rentalservice.entity.B2BSubscriptionItem;
import org.clickenrent.rentalservice.repository.B2BSubscriptionRepository;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between B2BSubscriptionItem entity and B2BSubscriptionItemDTO.
 */
@Component
@RequiredArgsConstructor
public class B2BSubscriptionItemMapper {

    private final B2BSubscriptionRepository b2bSubscriptionRepository;
    private final org.clickenrent.rentalservice.repository.ProductRepository productRepository;

    public B2BSubscriptionItemDTO toDto(B2BSubscriptionItem item) {
        if (item == null) {
            return null;
        }

        return B2BSubscriptionItemDTO.builder()
                .id(item.getId())
                .externalId(item.getExternalId())
                .b2bSubscriptionId(item.getB2bSubscription() != null ? item.getB2bSubscription().getId() : null)
                .productId(item.getProduct() != null ? item.getProduct().getId() : null)
                .startDateTime(item.getStartDateTime())
                .endDateTime(item.getEndDateTime())
                .price(item.getPrice())
                .totalPrice(item.getTotalPrice())
                .dateCreated(item.getDateCreated())
                .lastDateModified(item.getLastDateModified())
                .createdBy(item.getCreatedBy())
                .lastModifiedBy(item.getLastModifiedBy())
                .build();
    }

    public B2BSubscriptionItem toEntity(B2BSubscriptionItemDTO dto) {
        if (dto == null) {
            return null;
        }

        B2BSubscriptionItem.B2BSubscriptionItemBuilder builder = B2BSubscriptionItem.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .startDateTime(dto.getStartDateTime())
                .endDateTime(dto.getEndDateTime())
                .price(dto.getPrice())
                .totalPrice(dto.getTotalPrice());

        if (dto.getB2bSubscriptionId() != null) {
            builder.b2bSubscription(b2bSubscriptionRepository.findById(dto.getB2bSubscriptionId()).orElse(null));
        }
        if (dto.getProductId() != null) {
            builder.product(productRepository.findById(dto.getProductId()).orElse(null));
        }

        return builder.build();
    }

    public void updateEntityFromDto(B2BSubscriptionItemDTO dto, B2BSubscriptionItem item) {
        if (dto == null || item == null) {
            return;
        }

        if (dto.getEndDateTime() != null) {
            item.setEndDateTime(dto.getEndDateTime());
        }
        if (dto.getPrice() != null) {
            item.setPrice(dto.getPrice());
        }
        if (dto.getTotalPrice() != null) {
            item.setTotalPrice(dto.getTotalPrice());
        }
    }
}
