package org.clickenrent.rentalservice.service;

import org.clickenrent.rentalservice.dto.ChargingStationDTO;
import org.clickenrent.rentalservice.entity.ChargingStation;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.exception.UnauthorizedException;
import org.clickenrent.rentalservice.mapper.ChargingStationMapper;
import org.clickenrent.rentalservice.repository.ChargingStationRepository;
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

@ExtendWith(MockitoExtension.class)
class ChargingStationServiceTest {

    @Mock
    private ChargingStationRepository chargingStationRepository;

    @Mock
    private ChargingStationMapper chargingStationMapper;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private ChargingStationService chargingStationService;

    private ChargingStation testStation;
    private ChargingStationDTO testStationDTO;

    @BeforeEach
    void setUp() {
        testStation = ChargingStation.builder()
        .id(1L)
        .code("CS001")
        .build();

        testStationDTO = ChargingStationDTO.builder()
        .id(1L)
        .code("CS001")
        .chargingStationModelId(1L)
        .chargingStationStatusId(1L)
        .hubId(1L)
        .build();
    }

    @Test
    void getAllChargingStations_WithAdminRole_ReturnsAllStations() {
        when(securityService.isAdmin()).thenReturn(true);
        Pageable pageable = PageRequest.of(0, 20);
        Page<ChargingStation> stationPage = new PageImpl<>(Collections.singletonList(testStation));
        when(chargingStationRepository.findAll(pageable)).thenReturn(stationPage);
        when(chargingStationMapper.toDto(testStation)).thenReturn(testStationDTO);

        Page<ChargingStationDTO> result = chargingStationService.getAllChargingStations(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(chargingStationRepository, times(1)).findAll(pageable);
    }

    @Test
    void getAllChargingStations_WithoutAdminRole_ThrowsUnauthorizedException() {
        Pageable pageable = PageRequest.of(0, 20);
        assertThrows(UnauthorizedException.class, () -> chargingStationService.getAllChargingStations(pageable));
    }

    @Test
    void getChargingStationById_Success() {
        when(chargingStationRepository.findById(1L)).thenReturn(Optional.of(testStation));
        when(chargingStationMapper.toDto(testStation)).thenReturn(testStationDTO);

        ChargingStationDTO result = chargingStationService.getChargingStationById(1L);

        assertNotNull(result);
        assertEquals("CS001", result.getCode());
        verify(chargingStationRepository, times(1)).findById(1L);
    }

    @Test
    void getChargingStationById_NotFound() {
        when(chargingStationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> chargingStationService.getChargingStationById(999L));
    }

    @Test
    void createChargingStation_WithAdminRole_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(chargingStationMapper.toEntity(testStationDTO)).thenReturn(testStation);
        when(chargingStationRepository.save(any())).thenReturn(testStation);
        when(chargingStationMapper.toDto(testStation)).thenReturn(testStationDTO);

        ChargingStationDTO result = chargingStationService.createChargingStation(testStationDTO);

        assertNotNull(result);
        verify(chargingStationRepository, times(1)).save(any());
    }

    @Test
    void createChargingStation_WithoutAdminRole_ThrowsUnauthorizedException() {
        assertThrows(UnauthorizedException.class, () -> chargingStationService.createChargingStation(testStationDTO));
    }

    @Test
    void updateChargingStation_WithAdminRole_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(chargingStationRepository.findById(1L)).thenReturn(Optional.of(testStation));
        when(chargingStationRepository.save(any())).thenReturn(testStation);
        when(chargingStationMapper.toDto(testStation)).thenReturn(testStationDTO);

        ChargingStationDTO result = chargingStationService.updateChargingStation(1L, testStationDTO);

        assertNotNull(result);
        verify(chargingStationRepository, times(1)).save(any());
    }

    @Test
    void deleteChargingStation_WithAdminRole_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(chargingStationRepository.findById(1L)).thenReturn(Optional.of(testStation));
        doNothing().when(chargingStationRepository).delete(testStation);

        chargingStationService.deleteChargingStation(1L);

        verify(chargingStationRepository, times(1)).delete(testStation);
    }
}
