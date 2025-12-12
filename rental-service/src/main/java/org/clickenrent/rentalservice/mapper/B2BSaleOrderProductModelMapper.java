package org.clickenrent.rentalservice.mapper;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.B2BSaleOrderProductModelDTO;
import org.clickenrent.rentalservice.entity.B2BSaleOrderProductModel;
import org.clickenrent.rentalservice.repository.B2BSaleOrderRepository;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between B2BSaleOrderProductModel entity and B2BSaleOrderProductModelDTO.
 */
@Component
@RequiredArgsConstructor
public class B2BSaleOrderProductModelMapper {

    private final B2BSaleOrderRepository b2bSaleOrderRepository;

    public B2BSaleOrderProductModelDTO toDto(B2BSaleOrderProductModel productModel) {
        if (productModel == null) {
            return null;
        }

        return B2BSaleOrderProductModelDTO.builder()
                .id(productModel.getId())
                .externalId(productModel.getExternalId())
                .b2bSaleOrderId(productModel.getB2bSaleOrder() != null ? productModel.getB2bSaleOrder().getId() : null)
                .productModelType(productModel.getProductModelType())
                .productModelId(productModel.getProductModelId())
                .quantity(productModel.getQuantity())
                .price(productModel.getPrice())
                .totalPrice(productModel.getTotalPrice())
                .build();
    }

    public B2BSaleOrderProductModel toEntity(B2BSaleOrderProductModelDTO dto) {
        if (dto == null) {
            return null;
        }

        var builder = B2BSaleOrderProductModel.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .productModelType(dto.getProductModelType())
                .productModelId(dto.getProductModelId())
                .quantity(dto.getQuantity())
                .price(dto.getPrice())
                .totalPrice(dto.getTotalPrice());

        if (dto.getB2bSaleOrderId() != null) {
            builder.b2bSaleOrder(b2bSaleOrderRepository.findById(dto.getB2bSaleOrderId()).orElse(null));
        }

        return builder.build();
    }

    public void updateEntityFromDto(B2BSaleOrderProductModelDTO dto, B2BSaleOrderProductModel productModel) {
        if (dto == null || productModel == null) {
            return;
        }

        if (dto.getQuantity() != null) {
            productModel.setQuantity(dto.getQuantity());
        }
        if (dto.getPrice() != null) {
            productModel.setPrice(dto.getPrice());
        }
        if (dto.getTotalPrice() != null) {
            productModel.setTotalPrice(dto.getTotalPrice());
        }
    }
}
