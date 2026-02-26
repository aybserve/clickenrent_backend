package org.clickenrent.rentalservice.mapper;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.B2BSubscriptionDTO;
import org.clickenrent.rentalservice.entity.B2BSubscription;
import org.clickenrent.rentalservice.repository.B2BSubscriptionStatusRepository;
import org.clickenrent.rentalservice.repository.LocationRepository;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between B2BSubscription entity and B2BSubscriptionDTO.
 */
@Component
@RequiredArgsConstructor
public class B2BSubscriptionMapper {

    private final B2BSubscriptionStatusRepository b2bSubscriptionStatusRepository;
    private final LocationRepository locationRepository;

    public B2BSubscriptionDTO toDto(B2BSubscription b2bSubscription) {
        if (b2bSubscription == null) {
            return null;
        }

        return B2BSubscriptionDTO.builder()
                .id(b2bSubscription.getId())
                .externalId(b2bSubscription.getExternalId())
                .locationId(b2bSubscription.getLocation() != null ? b2bSubscription.getLocation().getId() : null)
                .endDateTime(b2bSubscription.getEndDateTime())
                .b2bSubscriptionStatusId(b2bSubscription.getB2bSubscriptionStatus() != null ? 
                        b2bSubscription.getB2bSubscriptionStatus().getId() : null)
                .dateCreated(b2bSubscription.getDateCreated())
                .lastDateModified(b2bSubscription.getLastDateModified())
                .createdBy(b2bSubscription.getCreatedBy())
                .lastModifiedBy(b2bSubscription.getLastModifiedBy())
                .build();
    }

    public B2BSubscription toEntity(B2BSubscriptionDTO dto) {
        if (dto == null) {
            return null;
        }

        var builder = B2BSubscription.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .endDateTime(dto.getEndDateTime())
                .dateCreated(dto.getDateCreated())
                .lastDateModified(dto.getLastDateModified())
                .createdBy(dto.getCreatedBy())
                .lastModifiedBy(dto.getLastModifiedBy());

        if (dto.getLocationId() != null) {
            builder.location(locationRepository.findById(dto.getLocationId()).orElse(null));
        }
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
