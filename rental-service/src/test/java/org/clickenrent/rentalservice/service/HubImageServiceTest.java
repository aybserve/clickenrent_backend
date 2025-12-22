package org.clickenrent.rentalservice.service;

import org.clickenrent.rentalservice.dto.HubImageDTO;
import org.clickenrent.rentalservice.entity.Hub;
import org.clickenrent.rentalservice.entity.HubImage;
import org.clickenrent.rentalservice.entity.Location;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.mapper.HubImageMapper;
import org.clickenrent.rentalservice.repository.HubImageRepository;
import org.clickenrent.rentalservice.repository.HubRepository;
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
class HubImageServiceTest {

    @Mock
    private HubImageRepository hubImageRepository;

    @Mock
    private HubRepository hubRepository;

    @Mock
    private HubImageMapper hubImageMapper;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private HubImageService hubImageService;

    private HubImage testImage;
    private HubImageDTO testImageDTO;
    private Hub testHub;

    @BeforeEach
    void setUp() {
        Location testLocation = Location.builder().id(1L).companyId(1L).build();
        testHub = Hub.builder().id(1L).location(testLocation).build();
        
        testImage = HubImage.builder()
        .id(1L)
        .externalId("HUBIMG001")
        .hub(testHub)
        .imageUrl("https://example.com/hub.jpg")
        .sortOrder(1)
        .isThumbnail(true)
        .build();

        testImageDTO = HubImageDTO.builder()
        .id(1L)
        .externalId("HUBIMG001")
        .hubId(1L)
        .imageUrl("https://example.com/hub.jpg")
        .sortOrder(1)
        .isThumbnail(true)
        .build();
        
            }

    @Test
    void getImagesByHub_ReturnsAll() {
        when(securityService.isAdmin()).thenReturn(true);
        when(hubRepository.findById(1L)).thenReturn(Optional.of(testHub));
        when(hubImageRepository.findByHub(testHub)).thenReturn(Arrays.asList(testImage));
        when(hubImageMapper.toDto(testImage)).thenReturn(testImageDTO);

        var result = hubImageService.getImagesByHub(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("https://example.com/hub.jpg", result.get(0).getImageUrl());
        verify(hubImageRepository, times(1)).findByHub(testHub);
    }

    @Test
    void getImageById_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(hubImageRepository.findById(1L)).thenReturn(Optional.of(testImage));
        when(hubImageMapper.toDto(testImage)).thenReturn(testImageDTO);

        HubImageDTO result = hubImageService.getImageById(1L);

        assertNotNull(result);
        assertEquals("https://example.com/hub.jpg", result.getImageUrl());
        verify(hubImageRepository, times(1)).findById(1L);
    }

    @Test
    void getImageById_NotFound() {
        when(hubImageRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> hubImageService.getImageById(999L));
    }

    @Test
    void createImage_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(hubRepository.findById(1L)).thenReturn(Optional.of(testHub));
        when(hubImageMapper.toEntity(testImageDTO)).thenReturn(testImage);
        when(hubImageRepository.save(any())).thenReturn(testImage);
        when(hubImageMapper.toDto(testImage)).thenReturn(testImageDTO);

        HubImageDTO result = hubImageService.createImage(testImageDTO);

        assertNotNull(result);
        verify(hubRepository, times(1)).findById(1L);
        verify(hubImageRepository, times(1)).save(any());
    }

    @Test
    void updateImage_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(hubImageRepository.findById(1L)).thenReturn(Optional.of(testImage));
        doNothing().when(hubImageMapper).updateEntityFromDto(testImageDTO, testImage);
        when(hubImageRepository.save(any())).thenReturn(testImage);
        when(hubImageMapper.toDto(testImage)).thenReturn(testImageDTO);

        HubImageDTO result = hubImageService.updateImage(1L, testImageDTO);

        assertNotNull(result);
        verify(hubImageMapper, times(1)).updateEntityFromDto(testImageDTO, testImage);
        verify(hubImageRepository, times(1)).save(any());
    }

    @Test
    void deleteImage_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(hubImageRepository.findById(1L)).thenReturn(Optional.of(testImage));
        doNothing().when(hubImageRepository).delete(testImage);

        hubImageService.deleteImage(1L);

        verify(hubImageRepository, times(1)).delete(testImage);
    }

    @Test
    void deleteImage_NotFound() {
        when(hubImageRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> hubImageService.deleteImage(999L));
    }
}




