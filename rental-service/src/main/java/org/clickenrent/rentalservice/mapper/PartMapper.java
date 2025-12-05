package org.clickenrent.rentalservice.mapper;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.PartDTO;
import org.clickenrent.rentalservice.entity.Part;
import org.clickenrent.rentalservice.repository.HubRepository;
import org.clickenrent.rentalservice.repository.PartModelRepository;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between Part entity and PartDTO.
 */
@Component
@RequiredArgsConstructor
public class PartMapper {

    private final HubRepository hubRepository;
    private final PartModelRepository partModelRepository;

    public PartDTO toDto(Part part) {
        if (part == null) {
            return null;
        }

        return PartDTO.builder()
                .id(part.getId())
                .externalId(part.getExternalId())
                .hubId(part.getHub() != null ? part.getHub().getId() : null)
                .partModelId(part.getPartModel() != null ? part.getPartModel().getId() : null)
                .isB2BRentable(part.getIsB2BRentable())
                .dateCreated(part.getDateCreated())
                .lastDateModified(part.getLastDateModified())
                .createdBy(part.getCreatedBy())
                .lastModifiedBy(part.getLastModifiedBy())
                .build();
    }

    public Part toEntity(PartDTO dto) {
        if (dto == null) {
            return null;
        }

        Part.PartBuilder<?, ?> builder = Part.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .isB2BRentable(dto.getIsB2BRentable());

        if (dto.getHubId() != null) {
            builder.hub(hubRepository.findById(dto.getHubId()).orElse(null));
        }
        if (dto.getPartModelId() != null) {
            builder.partModel(partModelRepository.findById(dto.getPartModelId()).orElse(null));
        }

        return builder.build();
    }
}
