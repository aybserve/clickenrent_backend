package org.clickenrent.rentalservice.mapper;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.B2BSaleOrderDTO;
import org.clickenrent.rentalservice.entity.B2BSaleOrder;
import org.clickenrent.rentalservice.repository.B2BSaleOrderStatusRepository;
import org.clickenrent.rentalservice.repository.B2BSaleRepository;
import org.clickenrent.rentalservice.repository.LocationRepository;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between B2BSaleOrder entity and B2BSaleOrderDTO.
 */
@Component
@RequiredArgsConstructor
public class B2BSaleOrderMapper {

    private final B2BSaleOrderStatusRepository b2bSaleOrderStatusRepository;
    private final LocationRepository locationRepository;
    private final B2BSaleRepository b2bSaleRepository;

    public B2BSaleOrderDTO toDto(B2BSaleOrder b2bSaleOrder) {
        if (b2bSaleOrder == null) {
            return null;
        }

        return B2BSaleOrderDTO.builder()
                .id(b2bSaleOrder.getId())
                .externalId(b2bSaleOrder.getExternalId())
                .sellerCompanyId(b2bSaleOrder.getSellerCompanyId())
                .buyerCompanyId(b2bSaleOrder.getBuyerCompanyId())
                .b2bSaleOrderStatusId(b2bSaleOrder.getB2bSaleOrderStatus() != null ? b2bSaleOrder.getB2bSaleOrderStatus().getId() : null)
                .locationId(b2bSaleOrder.getLocation() != null ? b2bSaleOrder.getLocation().getId() : null)
                .b2bSaleId(b2bSaleOrder.getB2bSale() != null ? b2bSaleOrder.getB2bSale().getId() : null)
                .dateTime(b2bSaleOrder.getDateTime())
                .build();
    }

    public B2BSaleOrder toEntity(B2BSaleOrderDTO dto) {
        if (dto == null) {
            return null;
        }

        var builder = B2BSaleOrder.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .sellerCompanyId(dto.getSellerCompanyId())
                .buyerCompanyId(dto.getBuyerCompanyId())
                .dateTime(dto.getDateTime());

        if (dto.getB2bSaleOrderStatusId() != null) {
            builder.b2bSaleOrderStatus(b2bSaleOrderStatusRepository.findById(dto.getB2bSaleOrderStatusId()).orElse(null));
        }
        if (dto.getLocationId() != null) {
            builder.location(locationRepository.findById(dto.getLocationId()).orElse(null));
        }
        if (dto.getB2bSaleId() != null) {
            builder.b2bSale(b2bSaleRepository.findById(dto.getB2bSaleId()).orElse(null));
        }

        return builder.build();
    }

    public void updateEntityFromDto(B2BSaleOrderDTO dto, B2BSaleOrder b2bSaleOrder) {
        if (dto == null || b2bSaleOrder == null) {
            return;
        }

        if (dto.getDateTime() != null) {
            b2bSaleOrder.setDateTime(dto.getDateTime());
        }
        if (dto.getB2bSaleOrderStatusId() != null) {
            b2bSaleOrderStatusRepository.findById(dto.getB2bSaleOrderStatusId())
                    .ifPresent(b2bSaleOrder::setB2bSaleOrderStatus);
        }
        if (dto.getB2bSaleId() != null) {
            b2bSaleRepository.findById(dto.getB2bSaleId())
                    .ifPresent(b2bSaleOrder::setB2bSale);
        }
    }
}


