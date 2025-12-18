package org.clickenrent.rentalservice.service;

import org.clickenrent.rentalservice.dto.RentalUnitDTO;
import org.clickenrent.rentalservice.entity.RentalUnit;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.mapper.RentalUnitMapper;
import org.clickenrent.rentalservice.repository.RentalUnitRepository;
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
class RentalUnitServiceTest {

    @Mock
    private RentalUnitRepository rentalUnitRepository;

    @Mock
    private RentalUnitMapper rentalUnitMapper;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private RentalUnitService rentalUnitService;

    private RentalUnit testUnit;
    private RentalUnitDTO testUnitDTO;

    @BeforeEach
    void setUp() {
        testUnit = RentalUnit.builder()
        .id(1L)
        .name("Day")
        .build();

        testUnitDTO = RentalUnitDTO.builder()
        .id(1L)
        .name("Day")
        .build();
    }

    @Test
    void getAllUnits_ReturnsAllUnits() {
        when(rentalUnitRepository.findAll()).thenReturn(Arrays.asList(testUnit));
        when(rentalUnitMapper.toDto(testUnit)).thenReturn(testUnitDTO);

        var result = rentalUnitService.getAllUnits();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(rentalUnitRepository, times(1)).findAll();
    }

    @Test
    void getUnitById_Success() {
        when(rentalUnitRepository.findById(1L)).thenReturn(Optional.of(testUnit));
        when(rentalUnitMapper.toDto(testUnit)).thenReturn(testUnitDTO);

        RentalUnitDTO result = rentalUnitService.getUnitById(1L);

        assertNotNull(result);
        assertEquals("Day", result.getName());
        verify(rentalUnitRepository, times(1)).findById(1L);
    }

    @Test
    void getUnitById_NotFound() {
        when(rentalUnitRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> rentalUnitService.getUnitById(999L));
    }

    @Test
    void createUnit_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(rentalUnitMapper.toEntity(testUnitDTO)).thenReturn(testUnit);
        when(rentalUnitRepository.save(any())).thenReturn(testUnit);
        when(rentalUnitMapper.toDto(testUnit)).thenReturn(testUnitDTO);

        RentalUnitDTO result = rentalUnitService.createUnit(testUnitDTO);

        assertNotNull(result);
        verify(rentalUnitRepository, times(1)).save(any());
    }

    @Test
    void deleteUnit_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(rentalUnitRepository.findById(1L)).thenReturn(Optional.of(testUnit));
        doNothing().when(rentalUnitRepository).delete(testUnit);

        rentalUnitService.deleteUnit(1L);

        verify(rentalUnitRepository, times(1)).delete(testUnit);
    }
}


