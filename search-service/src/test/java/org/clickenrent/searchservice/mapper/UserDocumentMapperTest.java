package org.clickenrent.searchservice.mapper;

import org.clickenrent.contracts.auth.UserDTO;
import org.clickenrent.searchservice.document.UserDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for UserDocumentMapper.
 * 
 * @author Vitaliy Shvetsov
 */
class UserDocumentMapperTest {

    private UserDocumentMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new UserDocumentMapper();
    }

    @Test
    void testToDocument_ValidUser() {
        // Given
        UserDTO userDTO = UserDTO.builder()
                .externalId("user-123")
                .userName("johndoe")
                .email("john@example.com")
                .firstName("John")
                .lastName("Doe")
                .phone("+1234567890")
                .imageUrl("https://example.com/image.jpg")
                .isActive(true)
                .build();
        
        List<String> companyIds = List.of("company-1", "company-2");

        // When
        UserDocument document = mapper.toDocument(userDTO, companyIds);

        // Then
        assertNotNull(document);
        assertEquals("user-123", document.getExternalId());
        assertEquals("johndoe", document.getUserName());
        assertEquals("john@example.com", document.getEmail());
        assertEquals("John", document.getFirstName());
        assertEquals("Doe", document.getLastName());
        assertEquals(companyIds, document.getCompanyExternalIds());
        assertTrue(document.getIsActive());
        assertNotNull(document.getSearchableText());
        assertTrue(document.getSearchableText().contains("John"));
        assertTrue(document.getSearchableText().contains("Doe"));
        assertTrue(document.getSearchableText().contains("john@example.com"));
    }

    @Test
    void testToDocument_NullUser() {
        // When
        UserDocument document = mapper.toDocument(null, List.of());

        // Then
        assertNull(document);
    }

    @Test
    void testToDocument_PartialUser() {
        // Given
        UserDTO userDTO = UserDTO.builder()
                .externalId("user-456")
                .firstName("Jane")
                .lastName("Smith")
                .build();
        
        // When
        UserDocument document = mapper.toDocument(userDTO, List.of("company-1"));

        // Then
        assertNotNull(document);
        assertEquals("user-456", document.getExternalId());
        assertEquals("Jane", document.getFirstName());
        assertEquals("Smith", document.getLastName());
        assertNull(document.getUserName());
        assertNull(document.getEmail());
    }
}
