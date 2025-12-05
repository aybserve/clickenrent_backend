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

    public B2BSubscriptionItemDTO toDto(B2BSubscriptionItem item) {
        if (item == null) {
            return null;
        }

        return B2BSubscriptionItemDTO.builder()
                .id(item.getId())
                .externalId(item.getExternalId())
                .b2bSubscriptionId(item.getB2bSubscription() != null ? item.getB2bSubscription().getId() : null)
                .productId(item.getProductId())
                .startDateTime(item.getStartDateTime())
                .endDateTime(item.getEndDateTime())
                .agreedMonthlyFee(item.getAgreedMonthlyFee())
                .build();
    }

    public B2BSubscriptionItem toEntity(B2BSubscriptionItemDTO dto) {
        if (dto == null) {
            return null;
        }

        B2BSubscriptionItem.B2BSubscriptionItemBuilder builder = B2BSubscriptionItem.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .productId(dto.getProductId())
                .startDateTime(dto.getStartDateTime())
                .endDateTime(dto.getEndDateTime())
                .agreedMonthlyFee(dto.getAgreedMonthlyFee());

        if (dto.getB2bSubscriptionId() != null) {
            builder.b2bSubscription(b2bSubscriptionRepository.findById(dto.getB2bSubscriptionId()).orElse(null));
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
        if (dto.getAgreedMonthlyFee() != null) {
            item.setAgreedMonthlyFee(dto.getAgreedMonthlyFee());
        }
    }
}
