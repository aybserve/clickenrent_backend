package org.clickenrent.rentalservice.mapper;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.B2BSubscriptionOrderItemDTO;
import org.clickenrent.rentalservice.entity.B2BSubscriptionOrderItem;
import org.clickenrent.rentalservice.repository.B2BSubscriptionOrderRepository;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between B2BSubscriptionOrderItem entity and B2BSubscriptionOrderItemDTO.
 */
@Component
@RequiredArgsConstructor
public class B2BSubscriptionOrderItemMapper {

    private final B2BSubscriptionOrderRepository b2bSubscriptionOrderRepository;

    public B2BSubscriptionOrderItemDTO toDto(B2BSubscriptionOrderItem item) {
        if (item == null) {
            return null;
        }

        return B2BSubscriptionOrderItemDTO.builder()
                .id(item.getId())
                .externalId(item.getExternalId())
                .b2bSubscriptionOrderId(item.getB2bSubscriptionOrder() != null ? item.getB2bSubscriptionOrder().getId() : null)
                .productModelType(item.getProductModelType())
                .productModelId(item.getProductModelId())
                .quantity(item.getQuantity())
                .price(item.getPrice())
                .totalPrice(item.getTotalPrice())
                .build();
    }

    public B2BSubscriptionOrderItem toEntity(B2BSubscriptionOrderItemDTO dto) {
        if (dto == null) {
            return null;
        }

        var builder = B2BSubscriptionOrderItem.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .productModelType(dto.getProductModelType())
                .productModelId(dto.getProductModelId())
                .quantity(dto.getQuantity())
                .price(dto.getPrice())
                .totalPrice(dto.getTotalPrice());

        if (dto.getB2bSubscriptionOrderId() != null) {
            builder.b2bSubscriptionOrder(b2bSubscriptionOrderRepository.findById(dto.getB2bSubscriptionOrderId()).orElse(null));
        }

        return builder.build();
    }

    public void updateEntityFromDto(B2BSubscriptionOrderItemDTO dto, B2BSubscriptionOrderItem item) {
        if (dto == null || item == null) {
            return;
        }

        if (dto.getQuantity() != null) {
            item.setQuantity(dto.getQuantity());
        }
        if (dto.getPrice() != null) {
            item.setPrice(dto.getPrice());
        }
        if (dto.getTotalPrice() != null) {
            item.setTotalPrice(dto.getTotalPrice());
        }
    }
}




