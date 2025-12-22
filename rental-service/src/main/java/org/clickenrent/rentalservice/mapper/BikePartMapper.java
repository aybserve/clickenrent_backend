package org.clickenrent.rentalservice.mapper;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.BikePartDTO;
import org.clickenrent.rentalservice.entity.BikePart;
import org.clickenrent.rentalservice.repository.BikeRepository;
import org.clickenrent.rentalservice.repository.PartRepository;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between BikePart entity and BikePartDTO.
 */
@Component
@RequiredArgsConstructor
public class BikePartMapper {

    private final BikeRepository bikeRepository;
    private final PartRepository partRepository;

    public BikePartDTO toDto(BikePart bikePart) {
        if (bikePart == null) {
            return null;
        }

        return BikePartDTO.builder()
                .id(bikePart.getId())
                .bikeId(bikePart.getBike() != null ? bikePart.getBike().getId() : null)
                .partId(bikePart.getPart() != null ? bikePart.getPart().getId() : null)
                .dateCreated(bikePart.getDateCreated())
                .lastDateModified(bikePart.getLastDateModified())
                .createdBy(bikePart.getCreatedBy())
                .lastModifiedBy(bikePart.getLastModifiedBy())
                .build();
    }

    public BikePart toEntity(BikePartDTO dto) {
        if (dto == null) {
            return null;
        }

        BikePart.BikePartBuilder<?, ?> builder = BikePart.builder()
                .id(dto.getId());

        if (dto.getBikeId() != null) {
            builder.bike(bikeRepository.findById(dto.getBikeId()).orElse(null));
        }
        if (dto.getPartId() != null) {
            builder.part(partRepository.findById(dto.getPartId()).orElse(null));
        }

        return builder.build();
    }
}




