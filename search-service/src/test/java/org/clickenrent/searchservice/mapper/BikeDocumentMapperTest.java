package org.clickenrent.searchservice.mapper;

import org.clickenrent.contracts.rental.BikeDTO;
import org.clickenrent.searchservice.document.BikeDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for BikeDocumentMapper.
 *
 * @author Vitaliy Shvetsov
 */
class BikeDocumentMapperTest {

    private BikeDocumentMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new BikeDocumentMapper();
    }

    @Test
    void toDocument_validBike_mapsAllFields() {
        // Given
        BikeDTO dto = BikeDTO.builder()
                .externalId("bike-123")
                .code("BK-001")
                .qrCodeUrl("https://qr.example.com/1")
                .frameNumber("FRAME-456")
                .bikeStatusId(1L)
                .batteryLevel(85)
                .bikeTypeId(2L)
                .bikeModelId(3L)
                .hubId(10L)
                .bikeModelImageUrl("https://example.com/bike.jpg")
                .build();
        String companyExternalId = "company-1";

        // When
        BikeDocument document = mapper.toDocument(dto, companyExternalId);

        // Then
        assertNotNull(document);
        assertEquals("bike-123", document.getId());
        assertEquals("bike-123", document.getExternalId());
        assertEquals("company-1", document.getCompanyExternalId());
        assertEquals("BK-001", document.getCode());
        assertEquals("https://qr.example.com/1", document.getQrCodeUrl());
        assertEquals("FRAME-456", document.getFrameNumber());
        assertEquals(1L, document.getBikeStatusId());
        assertEquals(85, document.getBatteryLevel());
        assertEquals(2L, document.getBikeTypeId());
        assertEquals(3L, document.getBikeModelId());
        assertEquals(10L, document.getHubId());
        assertEquals("https://example.com/bike.jpg", document.getImageUrl());
        assertNotNull(document.getSearchableText());
        assertTrue(document.getSearchableText().contains("BK-001"));
        assertTrue(document.getSearchableText().contains("FRAME-456"));
    }

    @Test
    void toDocument_partialBike_mapsNonNullFields() {
        // Given
        BikeDTO dto = BikeDTO.builder()
                .externalId("bike-456")
                .code("BK-002")
                .build();

        // When
        BikeDocument document = mapper.toDocument(dto, "company-2");

        // Then
        assertNotNull(document);
        assertEquals("bike-456", document.getExternalId());
        assertEquals("company-2", document.getCompanyExternalId());
        assertEquals("BK-002", document.getCode());
        assertNull(document.getFrameNumber());
        assertNull(document.getBatteryLevel());
        assertNotNull(document.getSearchableText());
        assertTrue(document.getSearchableText().contains("BK-002"));
    }

    @Test
    void toDocument_null_returnsNull() {
        // When
        BikeDocument document = mapper.toDocument(null, "company-1");

        // Then
        assertNull(document);
    }
}
