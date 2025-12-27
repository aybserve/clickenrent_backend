package org.clickenrent.rentalservice.service;

import org.clickenrent.rentalservice.dto.BikeTypeDTO;
import org.clickenrent.rentalservice.entity.BikeType;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.mapper.BikeTypeMapper;
import org.clickenrent.rentalservice.repository.BikeTypeRepository;
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
class BikeTypeServiceTest {

    @Mock
    private BikeTypeRepository bikeTypeRepository;

    @Mock
    private BikeTypeMapper bikeTypeMapper;

    @Mock
    private SecurityService securityService;


    @InjectMocks
    private BikeTypeService bikeTypeService;

    private BikeType testType;
    private BikeTypeDTO testTypeDTO;

    @BeforeEach
    void setUp() {
        testType = BikeType.builder()
        .id(1L)
        .externalId("bike-type-uuid-1")
        .name("Electric bike")
        .build();

        testTypeDTO = BikeTypeDTO.builder()
        .id(1L)
        .externalId("bike-type-uuid-1")
        .name("Electric bike")
        .build();
    }

    @Test
    void getAllBikeTypes_ReturnsAllTypes() {
        when(bikeTypeRepository.findAll()).thenReturn(Arrays.asList(testType));
        when(bikeTypeMapper.toDto(testType)).thenReturn(testTypeDTO);

        var result = bikeTypeService.getAllBikeTypes();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bikeTypeRepository, times(1)).findAll();
    }

    @Test
    void getBikeTypeById_Success() {
        when(bikeTypeRepository.findById(1L)).thenReturn(Optional.of(testType));
        when(bikeTypeMapper.toDto(testType)).thenReturn(testTypeDTO);

        BikeTypeDTO result = bikeTypeService.getBikeTypeById(1L);

        assertNotNull(result);
        assertEquals("Electric bike", result.getName());
        verify(bikeTypeRepository, times(1)).findById(1L);
    }

    @Test
    void getBikeTypeById_NotFound() {
        when(bikeTypeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bikeTypeService.getBikeTypeById(999L));
    }

    @Test
    void createBikeType_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeTypeMapper.toEntity(testTypeDTO)).thenReturn(testType);
        when(bikeTypeRepository.save(any())).thenReturn(testType);
        when(bikeTypeMapper.toDto(testType)).thenReturn(testTypeDTO);

        BikeTypeDTO result = bikeTypeService.createBikeType(testTypeDTO);

        assertNotNull(result);
        verify(bikeTypeRepository, times(1)).save(any());
    }

    @Test
    void updateBikeType_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeTypeRepository.findById(1L)).thenReturn(Optional.of(testType));
        when(bikeTypeRepository.save(any())).thenReturn(testType);
        when(bikeTypeMapper.toDto(testType)).thenReturn(testTypeDTO);

        BikeTypeDTO result = bikeTypeService.updateBikeType(1L, testTypeDTO);

        assertNotNull(result);
        verify(bikeTypeRepository, times(1)).save(any());
    }

    @Test
    void deleteBikeType_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeTypeRepository.findById(1L)).thenReturn(Optional.of(testType));
        doNothing().when(bikeTypeRepository).delete(testType);

        bikeTypeService.deleteBikeType(1L);

        verify(bikeTypeRepository, times(1)).delete(testType);
    }
}







