package org.clickenrent.rentalservice.mapper;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.B2BSubscriptionOrderDTO;
import org.clickenrent.rentalservice.entity.B2BSubscriptionOrder;
import org.clickenrent.rentalservice.repository.B2BSubscriptionOrderStatusRepository;
import org.clickenrent.rentalservice.repository.B2BSubscriptionRepository;
import org.clickenrent.rentalservice.repository.LocationRepository;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between B2BSubscriptionOrder entity and B2BSubscriptionOrderDTO.
 */
@Component
@RequiredArgsConstructor
public class B2BSubscriptionOrderMapper {

    private final B2BSubscriptionOrderStatusRepository b2bSubscriptionOrderStatusRepository;
    private final LocationRepository locationRepository;
    private final B2BSubscriptionRepository b2bSubscriptionRepository;

    public B2BSubscriptionOrderDTO toDto(B2BSubscriptionOrder b2bSubscriptionOrder) {
        if (b2bSubscriptionOrder == null) {
            return null;
        }

        return B2BSubscriptionOrderDTO.builder()
                .id(b2bSubscriptionOrder.getId())
                .externalId(b2bSubscriptionOrder.getExternalId())
                .locationId(b2bSubscriptionOrder.getLocation() != null ? b2bSubscriptionOrder.getLocation().getId() : null)
                .dateTime(b2bSubscriptionOrder.getDateTime())
                .b2bSubscriptionOrderStatusId(b2bSubscriptionOrder.getB2bSubscriptionOrderStatus() != null ? b2bSubscriptionOrder.getB2bSubscriptionOrderStatus().getId() : null)
                .b2bSubscriptionId(b2bSubscriptionOrder.getB2bSubscription() != null ? b2bSubscriptionOrder.getB2bSubscription().getId() : null)
                .build();
    }

    public B2BSubscriptionOrder toEntity(B2BSubscriptionOrderDTO dto) {
        if (dto == null) {
            return null;
        }

        var builder = B2BSubscriptionOrder.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .dateTime(dto.getDateTime());

        if (dto.getLocationId() != null) {
            builder.location(locationRepository.findById(dto.getLocationId()).orElse(null));
        }
        if (dto.getB2bSubscriptionOrderStatusId() != null) {
            builder.b2bSubscriptionOrderStatus(b2bSubscriptionOrderStatusRepository.findById(dto.getB2bSubscriptionOrderStatusId()).orElse(null));
        }
        if (dto.getB2bSubscriptionId() != null) {
            builder.b2bSubscription(b2bSubscriptionRepository.findById(dto.getB2bSubscriptionId()).orElse(null));
        }

        return builder.build();
    }

    public void updateEntityFromDto(B2BSubscriptionOrderDTO dto, B2BSubscriptionOrder b2bSubscriptionOrder) {
        if (dto == null || b2bSubscriptionOrder == null) {
            return;
        }

        if (dto.getDateTime() != null) {
            b2bSubscriptionOrder.setDateTime(dto.getDateTime());
        }
        if (dto.getB2bSubscriptionOrderStatusId() != null) {
            b2bSubscriptionOrderStatusRepository.findById(dto.getB2bSubscriptionOrderStatusId())
                    .ifPresent(b2bSubscriptionOrder::setB2bSubscriptionOrderStatus);
        }
        if (dto.getB2bSubscriptionId() != null) {
            b2bSubscriptionRepository.findById(dto.getB2bSubscriptionId())
                    .ifPresent(b2bSubscriptionOrder::setB2bSubscription);
        }
    }
}








