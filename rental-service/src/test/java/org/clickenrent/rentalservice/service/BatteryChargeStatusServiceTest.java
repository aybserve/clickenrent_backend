package org.clickenrent.rentalservice.service;

import org.clickenrent.rentalservice.dto.BatteryChargeStatusDTO;
import org.clickenrent.rentalservice.entity.BatteryChargeStatus;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.mapper.BatteryChargeStatusMapper;
import org.clickenrent.rentalservice.repository.BatteryChargeStatusRepository;
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
class BatteryChargeStatusServiceTest {

    @Mock
    private BatteryChargeStatusRepository batteryChargeStatusRepository;

    @Mock
    private BatteryChargeStatusMapper batteryChargeStatusMapper;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private BatteryChargeStatusService batteryChargeStatusService;

    private BatteryChargeStatus testStatus;
    private BatteryChargeStatusDTO testStatusDTO;

    @BeforeEach
    void setUp() {
        testStatus = BatteryChargeStatus.builder()
        .id(1L)
        .name("Charging")
        .build();

        testStatusDTO = BatteryChargeStatusDTO.builder()
        .id(1L)
        .name("Charging")
        .build();

    }

    @Test
    void getAllStatuses_ReturnsAllStatuses() {
        when(batteryChargeStatusRepository.findAll()).thenReturn(Arrays.asList(testStatus));
        when(batteryChargeStatusMapper.toDto(testStatus)).thenReturn(testStatusDTO);

        var result = batteryChargeStatusService.getAllStatuses();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(batteryChargeStatusRepository, times(1)).findAll();
    }

    @Test
    void getStatusById_Success() {
        when(batteryChargeStatusRepository.findById(1L)).thenReturn(Optional.of(testStatus));
        when(batteryChargeStatusMapper.toDto(testStatus)).thenReturn(testStatusDTO);

        BatteryChargeStatusDTO result = batteryChargeStatusService.getStatusById(1L);

        assertNotNull(result);
        assertEquals("Charging", result.getName());
        verify(batteryChargeStatusRepository, times(1)).findById(1L);
    }

    @Test
    void getStatusById_NotFound() {
        when(batteryChargeStatusRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> batteryChargeStatusService.getStatusById(999L));
    }

    @Test
    void createStatus_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(batteryChargeStatusMapper.toEntity(testStatusDTO)).thenReturn(testStatus);
        when(batteryChargeStatusRepository.save(any())).thenReturn(testStatus);
        when(batteryChargeStatusMapper.toDto(testStatus)).thenReturn(testStatusDTO);

        BatteryChargeStatusDTO result = batteryChargeStatusService.createStatus(testStatusDTO);

        assertNotNull(result);
        verify(batteryChargeStatusRepository, times(1)).save(any());
    }

    @Test
    void updateStatus_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(batteryChargeStatusRepository.findById(1L)).thenReturn(Optional.of(testStatus));
        doNothing().when(batteryChargeStatusMapper).updateEntityFromDto(testStatusDTO, testStatus);
        when(batteryChargeStatusRepository.save(any())).thenReturn(testStatus);
        when(batteryChargeStatusMapper.toDto(testStatus)).thenReturn(testStatusDTO);

        BatteryChargeStatusDTO result = batteryChargeStatusService.updateStatus(1L, testStatusDTO);

        assertNotNull(result);
        verify(batteryChargeStatusMapper, times(1)).updateEntityFromDto(testStatusDTO, testStatus);
        verify(batteryChargeStatusRepository, times(1)).save(any());
    }

    @Test
    void deleteStatus_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(batteryChargeStatusRepository.findById(1L)).thenReturn(Optional.of(testStatus));
        doNothing().when(batteryChargeStatusRepository).delete(testStatus);

        batteryChargeStatusService.deleteStatus(1L);

        verify(batteryChargeStatusRepository, times(1)).delete(testStatus);
    }

    @Test
    void deleteStatus_NotFound() {
        when(securityService.isAdmin()).thenReturn(true);
        when(batteryChargeStatusRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> batteryChargeStatusService.deleteStatus(999L));
    }
}








