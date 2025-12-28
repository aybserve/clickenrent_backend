package org.clickenrent.rentalservice.mapper;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.HubImageDTO;
import org.clickenrent.rentalservice.entity.HubImage;
import org.clickenrent.rentalservice.repository.HubRepository;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between HubImage entity and HubImageDTO.
 */
@Component
@RequiredArgsConstructor
public class HubImageMapper {

    private final HubRepository hubRepository;

    public HubImageDTO toDto(HubImage hubImage) {
        if (hubImage == null) {
            return null;
        }

        return HubImageDTO.builder()
                .id(hubImage.getId())
                .externalId(hubImage.getExternalId())
                .hubId(hubImage.getHub() != null ? hubImage.getHub().getId() : null)
                .imageUrl(hubImage.getImageUrl())
                .sortOrder(hubImage.getSortOrder())
                .isThumbnail(hubImage.getIsThumbnail())
                .dateCreated(hubImage.getDateCreated())
                .lastDateModified(hubImage.getLastDateModified())
                .createdBy(hubImage.getCreatedBy())
                .lastModifiedBy(hubImage.getLastModifiedBy())
                .build();
    }

    public HubImage toEntity(HubImageDTO dto) {
        if (dto == null) {
            return null;
        }

        HubImage.HubImageBuilder builder = HubImage.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .imageUrl(dto.getImageUrl())
                .sortOrder(dto.getSortOrder())
                .isThumbnail(dto.getIsThumbnail());

        if (dto.getHubId() != null) {
            builder.hub(hubRepository.findById(dto.getHubId()).orElse(null));
        }

        return builder.build();
    }

    public void updateEntityFromDto(HubImageDTO dto, HubImage hubImage) {
        if (dto == null || hubImage == null) {
            return;
        }

        if (dto.getImageUrl() != null) {
            hubImage.setImageUrl(dto.getImageUrl());
        }
        if (dto.getSortOrder() != null) {
            hubImage.setSortOrder(dto.getSortOrder());
        }
        if (dto.getIsThumbnail() != null) {
            hubImage.setIsThumbnail(dto.getIsThumbnail());
        }
    }
}








