package org.clickenrent.rentalservice.service;

import org.clickenrent.rentalservice.dto.BikeEngineDTO;
import org.clickenrent.rentalservice.entity.BikeEngine;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.mapper.BikeEngineMapper;
import org.clickenrent.rentalservice.repository.BikeEngineRepository;
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
class BikeEngineServiceTest {

    @Mock
    private BikeEngineRepository bikeEngineRepository;

    @Mock
    private BikeEngineMapper bikeEngineMapper;

    @Mock
    private SecurityService securityService;


    @InjectMocks
    private BikeEngineService bikeEngineService;

    private BikeEngine testEngine;
    private BikeEngineDTO testEngineDTO;

    @BeforeEach
    void setUp() {
        testEngine = BikeEngine.builder()
        .id(1L)
        .externalId("BE001")
        .name("Bosch Performance Line")
        .build();

        testEngineDTO = BikeEngineDTO.builder()
        .id(1L)
        .externalId("BE001")
        .name("Bosch Performance Line")
        .build();
    }

    @Test
    void getAllBikeEngines_ReturnsAllEngines() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<BikeEngine> enginePage = new PageImpl<>(Collections.singletonList(testEngine));
        when(bikeEngineRepository.findAll(pageable)).thenReturn(enginePage);
        when(bikeEngineMapper.toDto(testEngine)).thenReturn(testEngineDTO);

        Page<BikeEngineDTO> result = bikeEngineService.getAllBikeEngines(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(bikeEngineRepository, times(1)).findAll(pageable);
    }

    @Test
    void getBikeEngineById_Success() {
        when(bikeEngineRepository.findById(1L)).thenReturn(Optional.of(testEngine));
        when(bikeEngineMapper.toDto(testEngine)).thenReturn(testEngineDTO);

        BikeEngineDTO result = bikeEngineService.getBikeEngineById(1L);

        assertNotNull(result);
        assertEquals("Bosch Performance Line", result.getName());
        verify(bikeEngineRepository, times(1)).findById(1L);
    }

    @Test
    void getBikeEngineById_NotFound() {
        when(bikeEngineRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bikeEngineService.getBikeEngineById(999L));
    }

    @Test
    void createBikeEngine_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeEngineMapper.toEntity(testEngineDTO)).thenReturn(testEngine);
        when(bikeEngineRepository.save(any())).thenReturn(testEngine);
        when(bikeEngineMapper.toDto(testEngine)).thenReturn(testEngineDTO);

        BikeEngineDTO result = bikeEngineService.createBikeEngine(testEngineDTO);

        assertNotNull(result);
        verify(bikeEngineRepository, times(1)).save(any());
    }

    @Test
    void updateBikeEngine_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeEngineRepository.findById(1L)).thenReturn(Optional.of(testEngine));
        when(bikeEngineRepository.save(any())).thenReturn(testEngine);
        when(bikeEngineMapper.toDto(testEngine)).thenReturn(testEngineDTO);

        BikeEngineDTO result = bikeEngineService.updateBikeEngine(1L, testEngineDTO);

        assertNotNull(result);
        verify(bikeEngineRepository, times(1)).save(any());
    }

    @Test
    void deleteBikeEngine_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeEngineRepository.findById(1L)).thenReturn(Optional.of(testEngine));
        doNothing().when(bikeEngineRepository).delete(testEngine);

        bikeEngineService.deleteBikeEngine(1L);

        verify(bikeEngineRepository, times(1)).delete(testEngine);
    }
}







