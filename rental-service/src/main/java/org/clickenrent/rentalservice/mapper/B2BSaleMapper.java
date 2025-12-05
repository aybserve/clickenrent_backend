package org.clickenrent.rentalservice.mapper;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.B2BSaleDTO;
import org.clickenrent.rentalservice.entity.B2BSale;
import org.clickenrent.rentalservice.repository.B2BSaleStatusRepository;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between B2BSale entity and B2BSaleDTO.
 */
@Component
@RequiredArgsConstructor
public class B2BSaleMapper {

    private final B2BSaleStatusRepository b2bSaleStatusRepository;

    public B2BSaleDTO toDto(B2BSale b2bSale) {
        if (b2bSale == null) {
            return null;
        }

        return B2BSaleDTO.builder()
                .id(b2bSale.getId())
                .externalId(b2bSale.getExternalId())
                .sellerCompanyId(b2bSale.getSellerCompanyId())
                .buyerCompanyId(b2bSale.getBuyerCompanyId())
                .b2bSaleStatusId(b2bSale.getB2bSaleStatus() != null ? b2bSale.getB2bSaleStatus().getId() : null)
                .dateTime(b2bSale.getDateTime())
                .build();
    }

    public B2BSale toEntity(B2BSaleDTO dto) {
        if (dto == null) {
            return null;
        }

        B2BSale.B2BSaleBuilder builder = B2BSale.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .sellerCompanyId(dto.getSellerCompanyId())
                .buyerCompanyId(dto.getBuyerCompanyId())
                .dateTime(dto.getDateTime());

        if (dto.getB2bSaleStatusId() != null) {
            builder.b2bSaleStatus(b2bSaleStatusRepository.findById(dto.getB2bSaleStatusId()).orElse(null));
        }

        return builder.build();
    }

    public void updateEntityFromDto(B2BSaleDTO dto, B2BSale b2bSale) {
        if (dto == null || b2bSale == null) {
            return;
        }

        if (dto.getDateTime() != null) {
            b2bSale.setDateTime(dto.getDateTime());
        }
    }
}
