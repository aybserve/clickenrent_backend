package org.clickenrent.rentalservice.mapper;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.B2BSaleDTO;
import org.clickenrent.rentalservice.entity.B2BSale;
import org.clickenrent.rentalservice.repository.B2BSaleStatusRepository;
import org.clickenrent.rentalservice.repository.LocationRepository;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between B2BSale entity and B2BSaleDTO.
 */
@Component
@RequiredArgsConstructor
public class B2BSaleMapper {

    private final B2BSaleStatusRepository b2bSaleStatusRepository;
    private final LocationRepository locationRepository;

    public B2BSaleDTO toDto(B2BSale b2bSale) {
        if (b2bSale == null) {
            return null;
        }

        return B2BSaleDTO.builder()
                .id(b2bSale.getId())
                .externalId(b2bSale.getExternalId())
                .locationId(b2bSale.getLocation() != null ? b2bSale.getLocation().getId() : null)
                .b2bSaleStatusId(b2bSale.getB2bSaleStatus() != null ? b2bSale.getB2bSaleStatus().getId() : null)
                .dateTime(b2bSale.getDateTime())
                .sellerCompanyExternalId(b2bSale.getSellerCompanyExternalId())
                .buyerCompanyExternalId(b2bSale.getBuyerCompanyExternalId())
                .build();
    }

    public B2BSale toEntity(B2BSaleDTO dto) {
        if (dto == null) {
            return null;
        }

        var builder = B2BSale.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .dateTime(dto.getDateTime())
                .sellerCompanyExternalId(dto.getSellerCompanyExternalId())
                .buyerCompanyExternalId(dto.getBuyerCompanyExternalId());

        if (dto.getLocationId() != null) {
            builder.location(locationRepository.findById(dto.getLocationId()).orElse(null));
        }
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
