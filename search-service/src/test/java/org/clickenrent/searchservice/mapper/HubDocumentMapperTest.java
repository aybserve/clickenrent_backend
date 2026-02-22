package org.clickenrent.searchservice.mapper;

import org.clickenrent.contracts.rental.HubDTO;
import org.clickenrent.searchservice.document.HubDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for HubDocumentMapper.
 *
 * @author Vitaliy Shvetsov
 */
class HubDocumentMapperTest {

    private HubDocumentMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new HubDocumentMapper();
    }

    @Test
    void toDocument_validHub_mapsAllFields() {
        // Given
        HubDTO dto = HubDTO.builder()
                .externalId("hub-123")
                .companyExternalId("company-1")
                .name("Downtown Hub")
                .locationId(5L)
                .capacity(20)
                .isActive(true)
                .description("Main downtown hub")
                .build();

        // When
        HubDocument document = mapper.toDocument(dto);

        // Then
        assertNotNull(document);
        assertEquals("hub-123", document.getId());
        assertEquals("hub-123", document.getExternalId());
        assertEquals("company-1", document.getCompanyExternalId());
        assertEquals("Downtown Hub", document.getName());
        assertEquals(5L, document.getLocationId());
        assertEquals(20, document.getCapacity());
        assertTrue(document.getIsActive());
        assertEquals("Main downtown hub", document.getDescription());
        assertNotNull(document.getSearchableText());
        assertTrue(document.getSearchableText().contains("Downtown Hub"));
        assertTrue(document.getSearchableText().contains("Main downtown hub"));
    }

    @Test
    void toDocument_partialHub_mapsNonNullFields() {
        // Given
        HubDTO dto = HubDTO.builder()
                .externalId("hub-456")
                .name("Minimal Hub")
                .capacity(10)
                .build();

        // When
        HubDocument document = mapper.toDocument(dto);

        // Then
        assertNotNull(document);
        assertEquals("hub-456", document.getExternalId());
        assertEquals("Minimal Hub", document.getName());
        assertEquals(10, document.getCapacity());
        assertNull(document.getLocationId());
        assertNull(document.getDescription());
        assertNotNull(document.getSearchableText());
        assertTrue(document.getSearchableText().contains("Minimal Hub"));
    }

    @Test
    void toDocument_null_returnsNull() {
        // When
        HubDocument document = mapper.toDocument(null);

        // Then
        assertNull(document);
    }
}
