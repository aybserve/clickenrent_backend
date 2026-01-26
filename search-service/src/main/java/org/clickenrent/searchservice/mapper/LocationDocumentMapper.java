package org.clickenrent.searchservice.mapper;

import org.clickenrent.contracts.rental.LocationDTO;
import org.clickenrent.searchservice.document.LocationDocument;
import org.springframework.stereotype.Component;

/**
 * Mapper to convert LocationDTO to LocationDocument for Elasticsearch indexing.
 * 
 * @author Vitaliy Shvetsov
 */
@Component
public class LocationDocumentMapper {

    /**
     * Convert LocationDTO to LocationDocument
     */
    public LocationDocument toDocument(LocationDTO dto) {
        if (dto == null) {
            return null;
        }

        String searchableText = buildSearchableText(dto);

        return LocationDocument.builder()
                .id(dto.getExternalId())
                .externalId(dto.getExternalId())
                .companyExternalId(dto.getCompanyExternalId())
                .name(dto.getName())
                .address(dto.getAddress())
                .description(dto.getDescription())
                .isPublic(dto.getIsPublic())
                .coordinatesId(dto.getCoordinatesId())
                .searchableText(searchableText)
                .build();
    }

    /**
     * Build combined searchable text field from location attributes
     */
    private String buildSearchableText(LocationDTO dto) {
        StringBuilder sb = new StringBuilder();
        
        if (dto.getName() != null) {
            sb.append(dto.getName()).append(" ");
        }
        if (dto.getAddress() != null) {
            sb.append(dto.getAddress()).append(" ");
        }
        if (dto.getDescription() != null) {
            sb.append(dto.getDescription()).append(" ");
        }
        
        return sb.toString().trim();
    }
}
