package org.clickenrent.rentalservice.service;

import org.clickenrent.rentalservice.dto.LocationImageDTO;
import org.clickenrent.rentalservice.entity.Location;
import org.clickenrent.rentalservice.entity.LocationImage;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.mapper.LocationImageMapper;
import org.clickenrent.rentalservice.repository.LocationImageRepository;
import org.clickenrent.rentalservice.repository.LocationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocationImageServiceTest {

    @Mock
    private LocationImageRepository locationImageRepository;

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private LocationImageMapper locationImageMapper;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private LocationImageService locationImageService;

    private LocationImage testImage;
    private LocationImageDTO testImageDTO;
    private Location testLocation;

    @BeforeEach
    void setUp() {
        testLocation = Location.builder().id(1L).companyExternalId("company-ext-001").build();
        
        testImage = LocationImage.builder()
        .id(1L)
        .externalId("IMG001")
        .location(testLocation)
        .imageUrl("https://example.com/location.jpg")
        .sortOrder(1)
        .isThumbnail(true)
        .build();

        testImageDTO = LocationImageDTO.builder()
        .id(1L)
        .externalId("IMG001")
        .locationId(1L)
        .imageUrl("https://example.com/location.jpg")
        .sortOrder(1)
        .isThumbnail(true)
        .build();
        
            }

    @Test
    void getImagesByLocation_ReturnsAll() {
        when(securityService.isAdmin()).thenReturn(true);
        when(locationRepository.findById(1L)).thenReturn(Optional.of(testLocation));
        when(locationImageRepository.findByLocation(testLocation)).thenReturn(Arrays.asList(testImage));
        when(locationImageMapper.toDto(testImage)).thenReturn(testImageDTO);

        var result = locationImageService.getImagesByLocation(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("https://example.com/location.jpg", result.get(0).getImageUrl());
        verify(locationImageRepository, times(1)).findByLocation(testLocation);
    }

    @Test
    void getImageById_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(locationImageRepository.findById(1L)).thenReturn(Optional.of(testImage));
        when(locationImageMapper.toDto(testImage)).thenReturn(testImageDTO);

        LocationImageDTO result = locationImageService.getImageById(1L);

        assertNotNull(result);
        assertEquals("https://example.com/location.jpg", result.getImageUrl());
        verify(locationImageRepository, times(1)).findById(1L);
    }

    @Test
    void getImageById_NotFound() {
        when(locationImageRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> locationImageService.getImageById(999L));
    }

    @Test
    void createImage_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(locationRepository.findById(1L)).thenReturn(Optional.of(testLocation));
        when(locationImageMapper.toEntity(testImageDTO)).thenReturn(testImage);
        when(locationImageRepository.save(any())).thenReturn(testImage);
        when(locationImageMapper.toDto(testImage)).thenReturn(testImageDTO);

        LocationImageDTO result = locationImageService.createImage(testImageDTO);

        assertNotNull(result);
        verify(locationRepository, times(1)).findById(1L);
        verify(locationImageRepository, times(1)).save(any());
    }

    @Test
    void updateImage_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(locationImageRepository.findById(1L)).thenReturn(Optional.of(testImage));
        doNothing().when(locationImageMapper).updateEntityFromDto(testImageDTO, testImage);
        when(locationImageRepository.save(any())).thenReturn(testImage);
        when(locationImageMapper.toDto(testImage)).thenReturn(testImageDTO);

        LocationImageDTO result = locationImageService.updateImage(1L, testImageDTO);

        assertNotNull(result);
        verify(locationImageMapper, times(1)).updateEntityFromDto(testImageDTO, testImage);
        verify(locationImageRepository, times(1)).save(any());
    }

    @Test
    void deleteImage_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(locationImageRepository.findById(1L)).thenReturn(Optional.of(testImage));
        doNothing().when(locationImageRepository).delete(testImage);

        locationImageService.deleteImage(1L);

        verify(locationImageRepository, times(1)).delete(testImage);
    }

    @Test
    void deleteImage_NotFound() {
        when(locationImageRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> locationImageService.deleteImage(999L));
    }
}




