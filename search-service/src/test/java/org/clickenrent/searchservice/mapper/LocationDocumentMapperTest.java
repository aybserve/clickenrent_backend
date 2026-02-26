package org.clickenrent.searchservice.mapper;

import org.clickenrent.contracts.rental.LocationDTO;
import org.clickenrent.searchservice.document.LocationDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for LocationDocumentMapper.
 *
 * @author Vitaliy Shvetsov
 */
class LocationDocumentMapperTest {

    private LocationDocumentMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new LocationDocumentMapper();
    }

    @Test
    void toDocument_validLocation_mapsAllFields() {
        // Given
        LocationDTO dto = LocationDTO.builder()
                .externalId("loc-123")
                .companyExternalId("company-1")
                .name("Central Park")
                .address("123 Main St")
                .description("A nice park")
                .isPublic(true)
                .coordinatesId(1L)
                .thumbnailImageUrl("https://example.com/loc.jpg")
                .build();

        // When
        LocationDocument document = mapper.toDocument(dto);

        // Then
        assertNotNull(document);
        assertEquals("loc-123", document.getId());
        assertEquals("loc-123", document.getExternalId());
        assertEquals("company-1", document.getCompanyExternalId());
        assertEquals("Central Park", document.getName());
        assertEquals("123 Main St", document.getAddress());
        assertEquals("A nice park", document.getDescription());
        assertTrue(document.getIsPublic());
        assertEquals(1L, document.getCoordinatesId());
        assertEquals("https://example.com/loc.jpg", document.getImageUrl());
        assertNotNull(document.getSearchableText());
        assertTrue(document.getSearchableText().contains("Central Park"));
        assertTrue(document.getSearchableText().contains("123 Main St"));
        assertTrue(document.getSearchableText().contains("A nice park"));
    }

    @Test
    void toDocument_partialLocation_mapsNonNullFields() {
        // Given
        LocationDTO dto = LocationDTO.builder()
                .externalId("loc-456")
                .name("Minimal Location")
                .build();

        // When
        LocationDocument document = mapper.toDocument(dto);

        // Then
        assertNotNull(document);
        assertEquals("loc-456", document.getExternalId());
        assertEquals("Minimal Location", document.getName());
        assertNull(document.getAddress());
        assertNull(document.getCompanyExternalId());
        assertNotNull(document.getSearchableText());
        assertTrue(document.getSearchableText().contains("Minimal Location"));
    }

    @Test
    void toDocument_null_returnsNull() {
        // When
        LocationDocument document = mapper.toDocument(null);

        // Then
        assertNull(document);
    }
}
