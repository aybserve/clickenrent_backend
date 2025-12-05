package org.clickenrent.rentalservice.mapper;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.B2BSubscriptionDTO;
import org.clickenrent.rentalservice.entity.B2BSubscription;
import org.clickenrent.rentalservice.repository.B2BSubscriptionStatusRepository;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between B2BSubscription entity and B2BSubscriptionDTO.
 */
@Component
@RequiredArgsConstructor
public class B2BSubscriptionMapper {

    private final B2BSubscriptionStatusRepository b2bSubscriptionStatusRepository;

    public B2BSubscriptionDTO toDto(B2BSubscription b2bSubscription) {
        if (b2bSubscription == null) {
            return null;
        }

        return B2BSubscriptionDTO.builder()
                .id(b2bSubscription.getId())
                .externalId(b2bSubscription.getExternalId())
                .companyId(b2bSubscription.getCompanyId())
                .endDateTime(b2bSubscription.getEndDateTime())
                .b2bSubscriptionStatusId(b2bSubscription.getB2bSubscriptionStatus() != null ? 
                        b2bSubscription.getB2bSubscriptionStatus().getId() : null)
                .build();
    }

    public B2BSubscription toEntity(B2BSubscriptionDTO dto) {
        if (dto == null) {
            return null;
        }

        B2BSubscription.B2BSubscriptionBuilder builder = B2BSubscription.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .companyId(dto.getCompanyId())
                .endDateTime(dto.getEndDateTime());

        if (dto.getB2bSubscriptionStatusId() != null) {
            builder.b2bSubscriptionStatus(b2bSubscriptionStatusRepository.findById(dto.getB2bSubscriptionStatusId()).orElse(null));
        }

        return builder.build();
    }

    public void updateEntityFromDto(B2BSubscriptionDTO dto, B2BSubscription subscription) {
        if (dto == null || subscription == null) {
            return;
        }

        if (dto.getEndDateTime() != null) {
            subscription.setEndDateTime(dto.getEndDateTime());
        }
    }
}
