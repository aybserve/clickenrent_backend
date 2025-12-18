package org.clickenrent.rentalservice.service;

import org.clickenrent.rentalservice.dto.BikeModelDTO;
import org.clickenrent.rentalservice.entity.BikeModel;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.exception.UnauthorizedException;
import org.clickenrent.rentalservice.mapper.BikeModelMapper;
import org.clickenrent.rentalservice.repository.BikeModelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for BikeModelService.
 */
@ExtendWith(MockitoExtension.class)
class BikeModelServiceTest {

    @Mock
    private BikeModelRepository bikeModelRepository;

    @Mock
    private BikeModelMapper bikeModelMapper;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private BikeModelService bikeModelService;

    private BikeModel testBikeModel;
    private BikeModelDTO testBikeModelDTO;

    @BeforeEach
    void setUp() {
        testBikeModel = BikeModel.builder()
        .id(1L)
        .externalId("BM001")
        .name("VanMoof S3")
        .build();

        testBikeModelDTO = BikeModelDTO.builder()
        .id(1L)
        .externalId("BM001")
        .name("VanMoof S3")
        .bikeBrandId(1L)
        .bikeTypeId(1L)
        .bikeEngineId(1L)
        .build();
    }

    @Test
    void getAllBikeModels_ReturnsAllModels() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 20);
        Page<BikeModel> modelPage = new PageImpl<>(Collections.singletonList(testBikeModel));
        when(bikeModelRepository.findAll(pageable)).thenReturn(modelPage);
        when(bikeModelMapper.toDto(testBikeModel)).thenReturn(testBikeModelDTO);

        // Act
        Page<BikeModelDTO> result = bikeModelService.getAllBikeModels(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("VanMoof S3", result.getContent().get(0).getName());
        verify(bikeModelRepository, times(1)).findAll(pageable);
    }

    @Test
    void getBikeModelById_Success() {
        // Arrange
        when(bikeModelRepository.findById(1L)).thenReturn(Optional.of(testBikeModel));
        when(bikeModelMapper.toDto(testBikeModel)).thenReturn(testBikeModelDTO);

        // Act
        BikeModelDTO result = bikeModelService.getBikeModelById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("VanMoof S3", result.getName());
        verify(bikeModelRepository, times(1)).findById(1L);
    }

    @Test
    void getBikeModelById_NotFound() {
        // Arrange
        when(bikeModelRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> bikeModelService.getBikeModelById(999L));
    }

    @Test
    void createBikeModel_WithAdminRole_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        // Arrange
        when(bikeModelMapper.toEntity(testBikeModelDTO)).thenReturn(testBikeModel);
        when(bikeModelRepository.save(any(BikeModel.class))).thenReturn(testBikeModel);
        when(bikeModelMapper.toDto(testBikeModel)).thenReturn(testBikeModelDTO);

        // Act
        BikeModelDTO result = bikeModelService.createBikeModel(testBikeModelDTO);

        // Assert
        assertNotNull(result);
        assertEquals("VanMoof S3", result.getName());
        verify(bikeModelRepository, times(1)).save(any(BikeModel.class));
    }

    @Test
    void createBikeModel_WithoutAdminRole_ThrowsUnauthorizedException() {
        // Arrange
        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> bikeModelService.createBikeModel(testBikeModelDTO));
        verify(bikeModelRepository, never()).save(any(BikeModel.class));
    }

    @Test
    void updateBikeModel_WithAdminRole_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        // Arrange
        when(bikeModelRepository.findById(1L)).thenReturn(Optional.of(testBikeModel));
        when(bikeModelRepository.save(any(BikeModel.class))).thenReturn(testBikeModel);
        when(bikeModelMapper.toDto(testBikeModel)).thenReturn(testBikeModelDTO);

        // Act
        BikeModelDTO result = bikeModelService.updateBikeModel(1L, testBikeModelDTO);

        // Assert
        assertNotNull(result);
        verify(bikeModelRepository, times(1)).findById(1L);
        verify(bikeModelRepository, times(1)).save(any(BikeModel.class));
    }

    @Test
    void updateBikeModel_WithoutAdminRole_ThrowsUnauthorizedException() {
        // Arrange
        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> bikeModelService.updateBikeModel(1L, testBikeModelDTO));
        verify(bikeModelRepository, never()).save(any(BikeModel.class));
    }

    @Test
    void updateBikeModel_NotFound() {
        when(securityService.isAdmin()).thenReturn(true);
        // Arrange
        when(bikeModelRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> bikeModelService.updateBikeModel(999L, testBikeModelDTO));
    }

    @Test
    void deleteBikeModel_WithAdminRole_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        // Arrange
        when(bikeModelRepository.findById(1L)).thenReturn(Optional.of(testBikeModel));
        doNothing().when(bikeModelRepository).delete(testBikeModel);

        // Act
        bikeModelService.deleteBikeModel(1L);

        // Assert
        verify(bikeModelRepository, times(1)).findById(1L);
        verify(bikeModelRepository, times(1)).delete(testBikeModel);
    }

    @Test
    void deleteBikeModel_WithoutAdminRole_ThrowsUnauthorizedException() {
        // Arrange
        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> bikeModelService.deleteBikeModel(1L));
        verify(bikeModelRepository, never()).delete(any(BikeModel.class));
    }

    @Test
    void deleteBikeModel_NotFound() {
        when(securityService.isAdmin()).thenReturn(true);
        // Arrange
        when(bikeModelRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> bikeModelService.deleteBikeModel(999L));
        verify(bikeModelRepository, never()).delete(any(BikeModel.class));
    }
}

