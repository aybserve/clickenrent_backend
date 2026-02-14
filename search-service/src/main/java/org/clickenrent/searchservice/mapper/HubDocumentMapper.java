package org.clickenrent.searchservice.mapper;

import org.clickenrent.contracts.rental.HubDTO;
import org.clickenrent.searchservice.document.HubDocument;
import org.springframework.stereotype.Component;

/**
 * Mapper to convert HubDTO to HubDocument for Elasticsearch indexing.
 * 
 * @author Vitaliy Shvetsov
 */
@Component
public class HubDocumentMapper {

    /**
     * Convert HubDTO to HubDocument
     */
    public HubDocument toDocument(HubDTO dto) {
        if (dto == null) {
            return null;
        }

        String searchableText = buildSearchableText(dto);

        return HubDocument.builder()
                .id(dto.getExternalId())
                .externalId(dto.getExternalId())
                .companyExternalId(dto.getCompanyExternalId())
                .name(dto.getName())
                .locationId(dto.getLocationId())
                .capacity(dto.getCapacity())
                .isActive(dto.getIsActive())
                .description(dto.getDescription())
                .searchableText(searchableText)
                .build();
    }

    /**
     * Build combined searchable text field from hub attributes
     */
    private String buildSearchableText(HubDTO dto) {
        StringBuilder sb = new StringBuilder();
        
        if (dto.getName() != null) {
            sb.append(dto.getName()).append(" ");
        }
        if (dto.getDescription() != null) {
            sb.append(dto.getDescription()).append(" ");
        }
        
        return sb.toString().trim();
    }
}
