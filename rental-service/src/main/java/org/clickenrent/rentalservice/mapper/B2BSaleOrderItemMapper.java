package org.clickenrent.rentalservice.mapper;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.B2BSaleOrderItemDTO;
import org.clickenrent.rentalservice.entity.B2BSaleOrderItem;
import org.clickenrent.rentalservice.repository.B2BSaleOrderRepository;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between B2BSaleOrderItem entity and B2BSaleOrderItemDTO.
 */
@Component
@RequiredArgsConstructor
public class B2BSaleOrderItemMapper {

    private final B2BSaleOrderRepository b2bSaleOrderRepository;
    private final org.clickenrent.rentalservice.repository.ProductRepository productRepository;

    public B2BSaleOrderItemDTO toDto(B2BSaleOrderItem item) {
        if (item == null) {
            return null;
        }

        return B2BSaleOrderItemDTO.builder()
                .id(item.getId())
                .externalId(item.getExternalId())
                .b2bSaleOrderId(item.getB2bSaleOrder() != null ? item.getB2bSaleOrder().getId() : null)
                .productId(item.getProduct() != null ? item.getProduct().getId() : null)
                .quantity(item.getQuantity())
                .price(item.getPrice())
                .totalPrice(item.getTotalPrice())
                .build();
    }

    public B2BSaleOrderItem toEntity(B2BSaleOrderItemDTO dto) {
        if (dto == null) {
            return null;
        }

        var builder = B2BSaleOrderItem.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .quantity(dto.getQuantity())
                .price(dto.getPrice())
                .totalPrice(dto.getTotalPrice());

        if (dto.getB2bSaleOrderId() != null) {
            builder.b2bSaleOrder(b2bSaleOrderRepository.findById(dto.getB2bSaleOrderId()).orElse(null));
        }
        if (dto.getProductId() != null) {
            builder.product(productRepository.findById(dto.getProductId()).orElse(null));
        }

        return builder.build();
    }

    public void updateEntityFromDto(B2BSaleOrderItemDTO dto, B2BSaleOrderItem item) {
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

