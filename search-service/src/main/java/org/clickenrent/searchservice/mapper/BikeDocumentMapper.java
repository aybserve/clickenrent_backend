package org.clickenrent.searchservice.mapper;

import org.clickenrent.contracts.rental.BikeDTO;
import org.clickenrent.searchservice.document.BikeDocument;
import org.springframework.stereotype.Component;

/**
 * Mapper to convert BikeDTO to BikeDocument for Elasticsearch indexing.
 * 
 * @author Vitaliy Shvetsov
 */
@Component
public class BikeDocumentMapper {

    /**
     * Convert BikeDTO to BikeDocument
     */
    public BikeDocument toDocument(BikeDTO dto, String companyExternalId) {
        if (dto == null) {
            return null;
        }

        String searchableText = buildSearchableText(dto);

        return BikeDocument.builder()
                .id(dto.getExternalId())
                .externalId(dto.getExternalId())
                .companyExternalId(companyExternalId)
                .code(dto.getCode())
                .qrCodeUrl(dto.getQrCodeUrl())
                .frameNumber(dto.getFrameNumber())
                .bikeStatusId(dto.getBikeStatusId())
                .batteryLevel(dto.getBatteryLevel())
                .bikeTypeId(dto.getBikeTypeId())
                .bikeModelId(dto.getBikeModelId())
                .hubId(dto.getHubId())
                .searchableText(searchableText)
                .build();
    }

    /**
     * Build combined searchable text field from bike attributes
     */
    private String buildSearchableText(BikeDTO dto) {
        StringBuilder sb = new StringBuilder();
        
        if (dto.getCode() != null) {
            sb.append(dto.getCode()).append(" ");
        }
        if (dto.getFrameNumber() != null) {
            sb.append(dto.getFrameNumber()).append(" ");
        }
        
        return sb.toString().trim();
    }
}
