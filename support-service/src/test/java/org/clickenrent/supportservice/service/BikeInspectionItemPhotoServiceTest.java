package org.clickenrent.supportservice.service;

import org.clickenrent.supportservice.dto.BikeInspectionItemPhotoDTO;
import org.clickenrent.supportservice.entity.BikeInspection;
import org.clickenrent.supportservice.entity.BikeInspectionItem;
import org.clickenrent.supportservice.entity.BikeInspectionItemPhoto;
import org.clickenrent.supportservice.entity.BikeInspectionItemStatus;
import org.clickenrent.supportservice.exception.ResourceNotFoundException;
import org.clickenrent.supportservice.exception.UnauthorizedException;
import org.clickenrent.supportservice.mapper.BikeInspectionItemPhotoMapper;
import org.clickenrent.supportservice.repository.BikeInspectionItemPhotoRepository;
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
class BikeInspectionItemPhotoServiceTest {

    @Mock
    private BikeInspectionItemPhotoRepository bikeInspectionItemPhotoRepository;

    @Mock
    private BikeInspectionItemPhotoMapper bikeInspectionItemPhotoMapper;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private BikeInspectionItemPhotoService bikeInspectionItemPhotoService;

    private BikeInspectionItemPhoto testEntity;
    private BikeInspectionItemPhotoDTO testDTO;

    @BeforeEach
    void setUp() {
        BikeInspection inspection = BikeInspection.builder().id(1L).companyExternalId("company-uuid-1").build();
        BikeInspectionItemStatus itemStatus = BikeInspectionItemStatus.builder().id(1L).name("OK").build();
        BikeInspectionItem item = BikeInspectionItem.builder().id(1L).externalId("i1").companyExternalId("company-uuid-1").bikeInspection(inspection).bikeInspectionItemStatus(itemStatus).build();
        testEntity = BikeInspectionItemPhoto.builder()
                .id(1L)
                .bikeInspectionItem(item)
                .photoUrl("https://example.com/photo.jpg")
                .companyExternalId("company-uuid-1")
                .build();

        testDTO = BikeInspectionItemPhotoDTO.builder()
                .id(1L)
                .bikeInspectionItemId(1L)
                .photoUrl("https://example.com/photo.jpg")
                .companyExternalId("company-uuid-1")
                .build();
    }

    @Test
    void getAll_ReturnsAll() {
        when(bikeInspectionItemPhotoRepository.findAll()).thenReturn(Arrays.asList(testEntity));
        when(bikeInspectionItemPhotoMapper.toDto(testEntity)).thenReturn(testDTO);

        List<BikeInspectionItemPhotoDTO> result = bikeInspectionItemPhotoService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("https://example.com/photo.jpg", result.get(0).getPhotoUrl());
        verify(bikeInspectionItemPhotoRepository, times(1)).findAll();
    }

    @Test
    void getById_Success() {
        when(bikeInspectionItemPhotoRepository.findById(1L)).thenReturn(Optional.of(testEntity));
        when(bikeInspectionItemPhotoMapper.toDto(testEntity)).thenReturn(testDTO);

        BikeInspectionItemPhotoDTO result = bikeInspectionItemPhotoService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(bikeInspectionItemPhotoRepository, times(1)).findById(1L);
    }

    @Test
    void getById_NotFound() {
        when(bikeInspectionItemPhotoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bikeInspectionItemPhotoService.getById(999L));
    }

    @Test
    void getByBikeInspectionItemId_Success() {
        when(bikeInspectionItemPhotoRepository.findByBikeInspectionItemId(1L)).thenReturn(Arrays.asList(testEntity));
        when(bikeInspectionItemPhotoMapper.toDto(testEntity)).thenReturn(testDTO);

        List<BikeInspectionItemPhotoDTO> result = bikeInspectionItemPhotoService.getByBikeInspectionItemId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bikeInspectionItemPhotoRepository, times(1)).findByBikeInspectionItemId(1L);
    }

    @Test
    void getByCompanyExternalId_Success() {
        when(bikeInspectionItemPhotoRepository.findByCompanyExternalId("company-uuid-1")).thenReturn(Arrays.asList(testEntity));
        when(bikeInspectionItemPhotoMapper.toDto(testEntity)).thenReturn(testDTO);

        List<BikeInspectionItemPhotoDTO> result = bikeInspectionItemPhotoService.getByCompanyExternalId("company-uuid-1");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bikeInspectionItemPhotoRepository, times(1)).findByCompanyExternalId("company-uuid-1");
    }

    @Test
    void create_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeInspectionItemPhotoMapper.toEntity(testDTO)).thenReturn(testEntity);
        when(bikeInspectionItemPhotoRepository.save(any(BikeInspectionItemPhoto.class))).thenReturn(testEntity);
        when(bikeInspectionItemPhotoMapper.toDto(testEntity)).thenReturn(testDTO);

        BikeInspectionItemPhotoDTO result = bikeInspectionItemPhotoService.create(testDTO);

        assertNotNull(result);
        verify(bikeInspectionItemPhotoRepository, times(1)).save(any(BikeInspectionItemPhoto.class));
    }

    @Test
    void create_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> bikeInspectionItemPhotoService.create(testDTO));
        verify(bikeInspectionItemPhotoRepository, never()).save(any(BikeInspectionItemPhoto.class));
    }

    @Test
    void update_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeInspectionItemPhotoRepository.findById(1L)).thenReturn(Optional.of(testEntity));
        doNothing().when(bikeInspectionItemPhotoMapper).updateEntityFromDto(testDTO, testEntity);
        when(bikeInspectionItemPhotoRepository.save(any(BikeInspectionItemPhoto.class))).thenReturn(testEntity);
        when(bikeInspectionItemPhotoMapper.toDto(testEntity)).thenReturn(testDTO);

        BikeInspectionItemPhotoDTO result = bikeInspectionItemPhotoService.update(1L, testDTO);

        assertNotNull(result);
        verify(bikeInspectionItemPhotoRepository, times(1)).save(any(BikeInspectionItemPhoto.class));
    }

    @Test
    void update_NotFound() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeInspectionItemPhotoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bikeInspectionItemPhotoService.update(999L, testDTO));
    }

    @Test
    void update_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> bikeInspectionItemPhotoService.update(1L, testDTO));
    }

    @Test
    void delete_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeInspectionItemPhotoRepository.findById(1L)).thenReturn(Optional.of(testEntity));
        doNothing().when(bikeInspectionItemPhotoRepository).delete(testEntity);

        bikeInspectionItemPhotoService.delete(1L);

        verify(bikeInspectionItemPhotoRepository, times(1)).delete(testEntity);
    }

    @Test
    void delete_NotFound() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeInspectionItemPhotoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bikeInspectionItemPhotoService.delete(999L));
    }

    @Test
    void delete_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> bikeInspectionItemPhotoService.delete(1L));
    }
}
