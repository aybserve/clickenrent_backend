package org.clickenrent.rentalservice.service;

import org.clickenrent.rentalservice.dto.BikeStatusDTO;
import org.clickenrent.rentalservice.entity.BikeStatus;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.mapper.BikeStatusMapper;
import org.clickenrent.rentalservice.repository.BikeStatusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BikeStatusServiceTest {

    @Mock
    private BikeStatusRepository bikeStatusRepository;

    @Mock
    private BikeStatusMapper bikeStatusMapper;

    @Mock
    private SecurityService securityService;


    @InjectMocks
    private BikeStatusService bikeStatusService;

    private BikeStatus testStatus;
    private BikeStatusDTO testStatusDTO;

    @BeforeEach
    void setUp() {
        testStatus = BikeStatus.builder()
        .id(1L)
        .name("Available")
        .build();

        testStatusDTO = BikeStatusDTO.builder()
        .id(1L)
        .name("Available")
        .build();
    }

    @Test
    void getAllBikeStatuses_ReturnsAllStatuses() {
        when(bikeStatusRepository.findAll()).thenReturn(Arrays.asList(testStatus));
        when(bikeStatusMapper.toDto(testStatus)).thenReturn(testStatusDTO);

        var result = bikeStatusService.getAllBikeStatuses();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bikeStatusRepository, times(1)).findAll();
    }

    @Test
    void getBikeStatusById_Success() {
        when(bikeStatusRepository.findById(1L)).thenReturn(Optional.of(testStatus));
        when(bikeStatusMapper.toDto(testStatus)).thenReturn(testStatusDTO);

        BikeStatusDTO result = bikeStatusService.getBikeStatusById(1L);

        assertNotNull(result);
        assertEquals("Available", result.getName());
        verify(bikeStatusRepository, times(1)).findById(1L);
    }

    @Test
    void getBikeStatusById_NotFound() {
        when(bikeStatusRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bikeStatusService.getBikeStatusById(999L));
    }

    @Test
    void createBikeStatus_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeStatusMapper.toEntity(testStatusDTO)).thenReturn(testStatus);
        when(bikeStatusRepository.save(any())).thenReturn(testStatus);
        when(bikeStatusMapper.toDto(testStatus)).thenReturn(testStatusDTO);

        BikeStatusDTO result = bikeStatusService.createBikeStatus(testStatusDTO);

        assertNotNull(result);
        verify(bikeStatusRepository, times(1)).save(any());
    }

    @Test
    void updateBikeStatus_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeStatusRepository.findById(1L)).thenReturn(Optional.of(testStatus));
        when(bikeStatusRepository.save(any())).thenReturn(testStatus);
        when(bikeStatusMapper.toDto(testStatus)).thenReturn(testStatusDTO);

        BikeStatusDTO result = bikeStatusService.updateBikeStatus(1L, testStatusDTO);

        assertNotNull(result);
        verify(bikeStatusRepository, times(1)).save(any());
    }

    @Test
    void deleteBikeStatus_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeStatusRepository.findById(1L)).thenReturn(Optional.of(testStatus));
        doNothing().when(bikeStatusRepository).delete(testStatus);

        bikeStatusService.deleteBikeStatus(1L);

        verify(bikeStatusRepository, times(1)).delete(testStatus);
    }
}
