package org.clickenrent.rentalservice.mapper;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.B2BSaleItemDTO;
import org.clickenrent.rentalservice.entity.B2BSaleItem;
import org.clickenrent.rentalservice.repository.B2BSaleRepository;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between B2BSaleItem entity and B2BSaleItemDTO.
 */
@Component
@RequiredArgsConstructor
public class B2BSaleItemMapper {

    private final B2BSaleRepository b2bSaleRepository;
    private final org.clickenrent.rentalservice.repository.ProductRepository productRepository;

    public B2BSaleItemDTO toDto(B2BSaleItem b2bSaleItem) {
        if (b2bSaleItem == null) {
            return null;
        }

        return B2BSaleItemDTO.builder()
                .id(b2bSaleItem.getId())
                .externalId(b2bSaleItem.getExternalId())
                .b2bSaleId(b2bSaleItem.getB2bSale() != null ? b2bSaleItem.getB2bSale().getId() : null)
                .productId(b2bSaleItem.getProduct() != null ? b2bSaleItem.getProduct().getId() : null)
                .price(b2bSaleItem.getPrice())
                .quantity(b2bSaleItem.getQuantity())
                .totalPrice(b2bSaleItem.getTotalPrice())
                .dateCreated(b2bSaleItem.getDateCreated())
                .lastDateModified(b2bSaleItem.getLastDateModified())
                .createdBy(b2bSaleItem.getCreatedBy())
                .lastModifiedBy(b2bSaleItem.getLastModifiedBy())
                .build();
    }

    public B2BSaleItem toEntity(B2BSaleItemDTO dto) {
        if (dto == null) {
            return null;
        }

        B2BSaleItem.B2BSaleItemBuilder builder = B2BSaleItem.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .price(dto.getPrice())
                .quantity(dto.getQuantity())
                .totalPrice(dto.getTotalPrice());

        if (dto.getB2bSaleId() != null) {
            builder.b2bSale(b2bSaleRepository.findById(dto.getB2bSaleId()).orElse(null));
        }
        if (dto.getProductId() != null) {
            builder.product(productRepository.findById(dto.getProductId()).orElse(null));
        }

        return builder.build();
    }

    public void updateEntityFromDto(B2BSaleItemDTO dto, B2BSaleItem entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.getPrice() != null) {
            entity.setPrice(dto.getPrice());
        }
        if (dto.getQuantity() != null) {
            entity.setQuantity(dto.getQuantity());
        }
        if (dto.getTotalPrice() != null) {
            entity.setTotalPrice(dto.getTotalPrice());
        }
    }
}

