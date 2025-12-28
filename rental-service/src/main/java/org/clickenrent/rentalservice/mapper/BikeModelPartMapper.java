package org.clickenrent.rentalservice.mapper;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.BikeModelPartDTO;
import org.clickenrent.rentalservice.entity.BikeModelPart;
import org.clickenrent.rentalservice.repository.BikeModelRepository;
import org.clickenrent.rentalservice.repository.PartRepository;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between BikeModelPart entity and BikeModelPartDTO.
 */
@Component
@RequiredArgsConstructor
public class BikeModelPartMapper {

    private final BikeModelRepository bikeModelRepository;
    private final PartRepository partRepository;

    public BikeModelPartDTO toDto(BikeModelPart bikeModelPart) {
        if (bikeModelPart == null) {
            return null;
        }

        return BikeModelPartDTO.builder()
                .id(bikeModelPart.getId())
                .bikeModelId(bikeModelPart.getBikeModel() != null ? bikeModelPart.getBikeModel().getId() : null)
                .partId(bikeModelPart.getPart() != null ? bikeModelPart.getPart().getId() : null)
                .dateCreated(bikeModelPart.getDateCreated())
                .lastDateModified(bikeModelPart.getLastDateModified())
                .createdBy(bikeModelPart.getCreatedBy())
                .lastModifiedBy(bikeModelPart.getLastModifiedBy())
                .build();
    }

    public BikeModelPart toEntity(BikeModelPartDTO dto) {
        if (dto == null) {
            return null;
        }

        BikeModelPart.BikeModelPartBuilder<?, ?> builder = BikeModelPart.builder()
                .id(dto.getId());

        if (dto.getBikeModelId() != null) {
            builder.bikeModel(bikeModelRepository.findById(dto.getBikeModelId()).orElse(null));
        }
        if (dto.getPartId() != null) {
            builder.part(partRepository.findById(dto.getPartId()).orElse(null));
        }

        return builder.build();
    }
}





