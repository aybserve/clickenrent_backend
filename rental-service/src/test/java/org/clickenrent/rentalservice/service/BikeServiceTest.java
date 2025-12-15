package org.clickenrent.rentalservice.service;

import org.clickenrent.rentalservice.dto.BikeDTO;
import org.clickenrent.rentalservice.entity.*;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.exception.UnauthorizedException;
import org.clickenrent.rentalservice.mapper.BikeMapper;
import org.clickenrent.rentalservice.repository.*;
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
 * Unit tests for BikeService.
 */
@ExtendWith(MockitoExtension.class)
class BikeServiceTest {

    @Mock
    private BikeRepository bikeRepository;

    @Mock
    private BikeMapper bikeMapper;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private BikeService bikeService;

    private Bike testBike;
    private BikeDTO testBikeDTO;

    @BeforeEach
    void setUp() {
        testBike = Bike.builder()
                .id(1L)
                .code("BIKE001")
                .frameNumber("FR123456")
                .build();

        testBikeDTO = BikeDTO.builder()
                .id(1L)
                .code("BIKE001")
                .frameNumber("FR123456")
                .build();
    }

    @Test
    void getAllBikes_WithAdminRole_ReturnsAllBikes() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 20);
        Page<Bike> bikePage = new PageImpl<>(Collections.singletonList(testBike));
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeRepository.findAll(pageable)).thenReturn(bikePage);
        when(bikeMapper.toDto(testBike)).thenReturn(testBikeDTO);

        // Act
        Page<BikeDTO> result = bikeService.getAllBikes(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("BIKE001", result.getContent().get(0).getCode());
        verify(bikeRepository, times(1)).findAll(pageable);
        verify(securityService, times(1)).isAdmin();
    }

    @Test
    void getAllBikes_WithoutAdminRole_ThrowsUnauthorizedException() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 20);
        when(securityService.isAdmin()).thenReturn(false);

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> bikeService.getAllBikes(pageable));
        verify(bikeRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    void getBikeById_Success() {
        // Arrange
        when(bikeRepository.findById(1L)).thenReturn(Optional.of(testBike));
        when(bikeMapper.toDto(testBike)).thenReturn(testBikeDTO);

        // Act
        BikeDTO result = bikeService.getBikeById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("BIKE001", result.getCode());
        verify(bikeRepository, times(1)).findById(1L);
    }

    @Test
    void getBikeById_NotFound() {
        // Arrange
        when(bikeRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> bikeService.getBikeById(999L));
    }

    @Test
    void getBikeByCode_Success() {
        // Arrange
        when(bikeRepository.findByCode("BIKE001")).thenReturn(Optional.of(testBike));
        when(bikeMapper.toDto(testBike)).thenReturn(testBikeDTO);

        // Act
        BikeDTO result = bikeService.getBikeByCode("BIKE001");

        // Assert
        assertNotNull(result);
        assertEquals("BIKE001", result.getCode());
        verify(bikeRepository, times(1)).findByCode("BIKE001");
    }

    @Test
    void getBikeByCode_NotFound() {
        // Arrange
        when(bikeRepository.findByCode("NONEXISTENT")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> bikeService.getBikeByCode("NONEXISTENT"));
    }

    @Test
    void createBike_WithAdminRole_Success() {
        // Arrange
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeMapper.toEntity(testBikeDTO)).thenReturn(testBike);
        when(bikeRepository.save(any(Bike.class))).thenReturn(testBike);
        when(bikeMapper.toDto(testBike)).thenReturn(testBikeDTO);

        // Act
        BikeDTO result = bikeService.createBike(testBikeDTO);

        // Assert
        assertNotNull(result);
        assertEquals("BIKE001", result.getCode());
        verify(bikeRepository, times(1)).save(any(Bike.class));
        verify(securityService, times(1)).isAdmin();
    }

    @Test
    void createBike_WithoutAdminRole_ThrowsUnauthorizedException() {
        // Arrange
        when(securityService.isAdmin()).thenReturn(false);

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> bikeService.createBike(testBikeDTO));
        verify(bikeRepository, never()).save(any(Bike.class));
    }

    @Test
    void updateBike_WithAdminRole_Success() {
        // Arrange
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeRepository.findById(1L)).thenReturn(Optional.of(testBike));
        when(bikeRepository.save(any(Bike.class))).thenReturn(testBike);
        when(bikeMapper.toDto(testBike)).thenReturn(testBikeDTO);

        // Act
        BikeDTO result = bikeService.updateBike(1L, testBikeDTO);

        // Assert
        assertNotNull(result);
        verify(bikeRepository, times(1)).findById(1L);
        verify(bikeRepository, times(1)).save(any(Bike.class));
    }

    @Test
    void updateBike_WithoutAdminRole_ThrowsUnauthorizedException() {
        // Arrange
        when(bikeRepository.findById(1L)).thenReturn(Optional.of(testBike));
        when(securityService.isAdmin()).thenReturn(false);

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> bikeService.updateBike(1L, testBikeDTO));
        verify(bikeRepository, never()).save(any(Bike.class));
    }

    @Test
    void updateBike_NotFound() {
        // Arrange
        when(bikeRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> bikeService.updateBike(999L, testBikeDTO));
        verify(bikeRepository, never()).save(any(Bike.class));
    }

    @Test
    void deleteBike_WithAdminRole_Success() {
        // Arrange
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeRepository.findById(1L)).thenReturn(Optional.of(testBike));
        doNothing().when(bikeRepository).delete(testBike);

        // Act
        bikeService.deleteBike(1L);

        // Assert
        verify(bikeRepository, times(1)).findById(1L);
        verify(bikeRepository, times(1)).delete(testBike);
    }

    @Test
    void deleteBike_WithoutAdminRole_ThrowsUnauthorizedException() {
        // Arrange
        when(bikeRepository.findById(1L)).thenReturn(Optional.of(testBike));
        when(securityService.isAdmin()).thenReturn(false);

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> bikeService.deleteBike(1L));
        verify(bikeRepository, never()).delete(any(Bike.class));
    }

    @Test
    void deleteBike_NotFound() {
        // Arrange
        when(bikeRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> bikeService.deleteBike(999L));
        verify(bikeRepository, never()).delete(any(Bike.class));
    }
}
