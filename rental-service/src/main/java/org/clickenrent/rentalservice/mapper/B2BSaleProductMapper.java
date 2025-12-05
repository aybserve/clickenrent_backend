package org.clickenrent.rentalservice.mapper;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.B2BSaleProductDTO;
import org.clickenrent.rentalservice.entity.B2BSaleProduct;
import org.clickenrent.rentalservice.repository.B2BSaleRepository;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between B2BSaleProduct entity and B2BSaleProductDTO.
 */
@Component
@RequiredArgsConstructor
public class B2BSaleProductMapper {

    private final B2BSaleRepository b2bSaleRepository;

    public B2BSaleProductDTO toDto(B2BSaleProduct b2bSaleProduct) {
        if (b2bSaleProduct == null) {
            return null;
        }

        return B2BSaleProductDTO.builder()
                .id(b2bSaleProduct.getId())
                .externalId(b2bSaleProduct.getExternalId())
                .b2bSaleId(b2bSaleProduct.getB2bSale() != null ? b2bSaleProduct.getB2bSale().getId() : null)
                .productId(b2bSaleProduct.getProductId())
                .price(b2bSaleProduct.getPrice())
                .build();
    }

    public B2BSaleProduct toEntity(B2BSaleProductDTO dto) {
        if (dto == null) {
            return null;
        }

        B2BSaleProduct.B2BSaleProductBuilder builder = B2BSaleProduct.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .productId(dto.getProductId())
                .price(dto.getPrice());

        if (dto.getB2bSaleId() != null) {
            builder.b2bSale(b2bSaleRepository.findById(dto.getB2bSaleId()).orElse(null));
        }

        return builder.build();
    }

    public void updateEntityFromDto(B2BSaleProductDTO dto, B2BSaleProduct entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.getPrice() != null) {
            entity.setPrice(dto.getPrice());
        }
    }
}
