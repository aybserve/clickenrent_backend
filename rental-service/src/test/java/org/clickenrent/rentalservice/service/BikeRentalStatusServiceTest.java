package org.clickenrent.rentalservice.service;

import org.clickenrent.rentalservice.dto.BikeRentalStatusDTO;
import org.clickenrent.rentalservice.entity.BikeRentalStatus;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.mapper.BikeRentalStatusMapper;
import org.clickenrent.rentalservice.repository.BikeRentalStatusRepository;
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
class BikeRentalStatusServiceTest {

    @Mock
    private BikeRentalStatusRepository bikeRentalStatusRepository;

    @Mock
    private BikeRentalStatusMapper bikeRentalStatusMapper;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private BikeRentalStatusService bikeRentalStatusService;

    private BikeRentalStatus testStatus;
    private BikeRentalStatusDTO testStatusDTO;

    @BeforeEach
    void setUp() {
        testStatus = BikeRentalStatus.builder()
        .id(1L)
        .name("Active")
        .build();

        testStatusDTO = BikeRentalStatusDTO.builder()
        .id(1L)
        .name("Active")
        .build();
    }

    @Test
    void getAllStatuses_ReturnsAllStatuses() {
        when(bikeRentalStatusRepository.findAll()).thenReturn(Arrays.asList(testStatus));
        when(bikeRentalStatusMapper.toDto(testStatus)).thenReturn(testStatusDTO);

        var result = bikeRentalStatusService.getAllStatuses();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Active", result.get(0).getName());
        verify(bikeRentalStatusRepository, times(1)).findAll();
    }

    @Test
    void getStatusById_Success() {
        when(bikeRentalStatusRepository.findById(1L)).thenReturn(Optional.of(testStatus));
        when(bikeRentalStatusMapper.toDto(testStatus)).thenReturn(testStatusDTO);

        BikeRentalStatusDTO result = bikeRentalStatusService.getStatusById(1L);

        assertNotNull(result);
        assertEquals("Active", result.getName());
        verify(bikeRentalStatusRepository, times(1)).findById(1L);
    }

    @Test
    void getStatusById_NotFound() {
        when(bikeRentalStatusRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bikeRentalStatusService.getStatusById(999L));
    }
}







