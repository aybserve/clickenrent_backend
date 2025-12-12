package org.clickenrent.rentalservice.mapper;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.RentalDTO;
import org.clickenrent.rentalservice.entity.Rental;
import org.clickenrent.rentalservice.repository.RentalStatusRepository;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between Rental entity and RentalDTO.
 */
@Component
@RequiredArgsConstructor
public class RentalMapper {

    private final RentalStatusRepository rentalStatusRepository;

    public RentalDTO toDto(Rental rental) {
        if (rental == null) {
            return null;
        }

        return RentalDTO.builder()
                .id(rental.getId())
                .externalId(rental.getExternalId())
                .userId(rental.getUserId())
                .companyId(rental.getCompanyId())
                .rentalStatusId(rental.getRentalStatus() != null ? rental.getRentalStatus().getId() : null)
                .erpRentalOrderId(rental.getErpRentalOrderId())
                .dateCreated(rental.getDateCreated())
                .lastDateModified(rental.getLastDateModified())
                .createdBy(rental.getCreatedBy())
                .lastModifiedBy(rental.getLastModifiedBy())
                .build();
    }

    public Rental toEntity(RentalDTO dto) {
        if (dto == null) {
            return null;
        }

        Rental.RentalBuilder builder = Rental.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .userId(dto.getUserId())
                .companyId(dto.getCompanyId())
                .erpRentalOrderId(dto.getErpRentalOrderId());

        if (dto.getRentalStatusId() != null) {
            builder.rentalStatus(rentalStatusRepository.findById(dto.getRentalStatusId()).orElse(null));
        }

        return builder.build();
    }

    public void updateEntityFromDto(RentalDTO dto, Rental rental) {
        if (dto == null || rental == null) {
            return;
        }

        if (dto.getErpRentalOrderId() != null) {
            rental.setErpRentalOrderId(dto.getErpRentalOrderId());
        }
    }
}
