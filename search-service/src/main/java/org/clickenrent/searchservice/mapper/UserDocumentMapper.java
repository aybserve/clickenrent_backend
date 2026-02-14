package org.clickenrent.searchservice.mapper;

import org.clickenrent.contracts.auth.UserDTO;
import org.clickenrent.searchservice.document.UserDocument;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mapper to convert UserDTO to UserDocument for Elasticsearch indexing.
 * 
 * @author Vitaliy Shvetsov
 */
@Component
public class UserDocumentMapper {

    /**
     * Convert UserDTO to UserDocument
     */
    public UserDocument toDocument(UserDTO dto, List<String> companyExternalIds) {
        if (dto == null) {
            return null;
        }

        String searchableText = buildSearchableText(dto);

        return UserDocument.builder()
                .id(dto.getExternalId())
                .externalId(dto.getExternalId())
                .companyExternalIds(companyExternalIds)
                .userName(dto.getUserName())
                .email(dto.getEmail())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .phone(dto.getPhone())
                .imageUrl(dto.getImageUrl())
                .isActive(dto.getIsActive())
                .searchableText(searchableText)
                .build();
    }

    /**
     * Build combined searchable text field from user attributes
     */
    private String buildSearchableText(UserDTO dto) {
        StringBuilder sb = new StringBuilder();
        
        if (dto.getUserName() != null) {
            sb.append(dto.getUserName()).append(" ");
        }
        if (dto.getEmail() != null) {
            sb.append(dto.getEmail()).append(" ");
        }
        if (dto.getFirstName() != null) {
            sb.append(dto.getFirstName()).append(" ");
        }
        if (dto.getLastName() != null) {
            sb.append(dto.getLastName()).append(" ");
        }
        if (dto.getPhone() != null) {
            sb.append(dto.getPhone()).append(" ");
        }
        
        return sb.toString().trim();
    }
}
