package org.clickenrent.rentalservice.service;

import org.clickenrent.rentalservice.dto.BikeDTO;
import org.clickenrent.rentalservice.entity.*;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.mapper.BikeMapper;
import org.clickenrent.rentalservice.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    void createBike_Success() {
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
    }
}
