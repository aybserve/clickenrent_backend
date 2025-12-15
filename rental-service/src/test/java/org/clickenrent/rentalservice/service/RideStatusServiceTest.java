package org.clickenrent.rentalservice.service;

import org.clickenrent.rentalservice.dto.RideStatusDTO;
import org.clickenrent.rentalservice.entity.RideStatus;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.mapper.RideStatusMapper;
import org.clickenrent.rentalservice.repository.RideStatusRepository;
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
class RideStatusServiceTest {

    @Mock
    private RideStatusRepository rideStatusRepository;

    @Mock
    private RideStatusMapper rideStatusMapper;

    @InjectMocks
    private RideStatusService rideStatusService;

    private RideStatus testStatus;
    private RideStatusDTO testStatusDTO;

    @BeforeEach
    void setUp() {
        testStatus = RideStatus.builder()
                .id(1L)
                .name("Active")
                .build();

        testStatusDTO = RideStatusDTO.builder()
                .id(1L)
                .name("Active")
                .build();
    }

    @Test
    void getAllStatuses_ReturnsAllStatuses() {
        when(rideStatusRepository.findAll()).thenReturn(Arrays.asList(testStatus));
        when(rideStatusMapper.toDto(testStatus)).thenReturn(testStatusDTO);

        var result = rideStatusService.getAllStatuses();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Active", result.get(0).getName());
        verify(rideStatusRepository, times(1)).findAll();
    }

    @Test
    void getStatusById_Success() {
        when(rideStatusRepository.findById(1L)).thenReturn(Optional.of(testStatus));
        when(rideStatusMapper.toDto(testStatus)).thenReturn(testStatusDTO);

        RideStatusDTO result = rideStatusService.getStatusById(1L);

        assertNotNull(result);
        assertEquals("Active", result.getName());
        verify(rideStatusRepository, times(1)).findById(1L);
    }

    @Test
    void getStatusById_NotFound() {
        when(rideStatusRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> rideStatusService.getStatusById(999L));
    }
}
