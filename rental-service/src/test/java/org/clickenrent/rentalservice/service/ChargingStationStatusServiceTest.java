package org.clickenrent.rentalservice.service;

import org.clickenrent.rentalservice.dto.ChargingStationStatusDTO;
import org.clickenrent.rentalservice.entity.ChargingStationStatus;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.mapper.ChargingStationStatusMapper;
import org.clickenrent.rentalservice.repository.ChargingStationStatusRepository;
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
class ChargingStationStatusServiceTest {

    @Mock
    private ChargingStationStatusRepository chargingStationStatusRepository;

    @Mock
    private ChargingStationStatusMapper chargingStationStatusMapper;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private ChargingStationStatusService chargingStationStatusService;

    private ChargingStationStatus testStatus;
    private ChargingStationStatusDTO testStatusDTO;

    @BeforeEach
    void setUp() {
        testStatus = ChargingStationStatus.builder()
        .id(1L)
        .name("Idle")
        .build();

        testStatusDTO = ChargingStationStatusDTO.builder()
        .id(1L)
        .name("Idle")
        .build();
    }

    @Test
    void getAllStatuses_ReturnsAllStatuses() {
        when(chargingStationStatusRepository.findAll()).thenReturn(Arrays.asList(testStatus));
        when(chargingStationStatusMapper.toDto(testStatus)).thenReturn(testStatusDTO);

        var result = chargingStationStatusService.getAllStatuses();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Idle", result.get(0).getName());
        verify(chargingStationStatusRepository, times(1)).findAll();
    }

    @Test
    void getStatusById_Success() {
        when(chargingStationStatusRepository.findById(1L)).thenReturn(Optional.of(testStatus));
        when(chargingStationStatusMapper.toDto(testStatus)).thenReturn(testStatusDTO);

        ChargingStationStatusDTO result = chargingStationStatusService.getStatusById(1L);

        assertNotNull(result);
        assertEquals("Idle", result.getName());
        verify(chargingStationStatusRepository, times(1)).findById(1L);
    }

    @Test
    void getStatusById_NotFound() {
        when(chargingStationStatusRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> chargingStationStatusService.getStatusById(999L));
    }
}

