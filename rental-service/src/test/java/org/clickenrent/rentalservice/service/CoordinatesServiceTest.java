package org.clickenrent.rentalservice.service;

import org.clickenrent.rentalservice.dto.CoordinatesDTO;
import org.clickenrent.rentalservice.entity.Coordinates;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.mapper.CoordinatesMapper;
import org.clickenrent.rentalservice.repository.CoordinatesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CoordinatesServiceTest {

    @Mock
    private CoordinatesRepository coordinatesRepository;

    @Mock
    private CoordinatesMapper coordinatesMapper;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private CoordinatesService coordinatesService;

    private Coordinates testCoordinates;
    private CoordinatesDTO testCoordinatesDTO;

    @BeforeEach
    void setUp() {
        testCoordinates = Coordinates.builder()
        .id(1L)
        .latitude(new BigDecimal("52.370216"))
        .longitude(new BigDecimal("4.895168"))
        .build();

        testCoordinatesDTO = CoordinatesDTO.builder()
        .id(1L)
        .latitude(new BigDecimal("52.370216"))
        .longitude(new BigDecimal("4.895168"))
        .build();
    }

    @Test
    void getCoordinatesById_Success() {
        when(coordinatesRepository.findById(1L)).thenReturn(Optional.of(testCoordinates));
        when(coordinatesMapper.toDto(testCoordinates)).thenReturn(testCoordinatesDTO);

        CoordinatesDTO result = coordinatesService.getCoordinatesById(1L);

        assertNotNull(result);
        assertEquals(new BigDecimal("52.370216"), result.getLatitude());
        assertEquals(new BigDecimal("4.895168"), result.getLongitude());
        verify(coordinatesRepository, times(1)).findById(1L);
    }

    @Test
    void getCoordinatesById_NotFound() {
        when(coordinatesRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> coordinatesService.getCoordinatesById(999L));
    }

    @Test
    void createCoordinates_Success() {
        when(coordinatesMapper.toEntity(testCoordinatesDTO)).thenReturn(testCoordinates);
        when(coordinatesRepository.save(any())).thenReturn(testCoordinates);
        when(coordinatesMapper.toDto(testCoordinates)).thenReturn(testCoordinatesDTO);

        CoordinatesDTO result = coordinatesService.createCoordinates(testCoordinatesDTO);

        assertNotNull(result);
        verify(coordinatesRepository, times(1)).save(any());
    }

    @Test
    void updateCoordinates_Success() {
        when(coordinatesRepository.findById(1L)).thenReturn(Optional.of(testCoordinates));
        doNothing().when(coordinatesMapper).updateEntityFromDto(testCoordinatesDTO, testCoordinates);
        when(coordinatesRepository.save(any())).thenReturn(testCoordinates);
        when(coordinatesMapper.toDto(testCoordinates)).thenReturn(testCoordinatesDTO);

        CoordinatesDTO result = coordinatesService.updateCoordinates(1L, testCoordinatesDTO);

        assertNotNull(result);
        verify(coordinatesMapper, times(1)).updateEntityFromDto(testCoordinatesDTO, testCoordinates);
        verify(coordinatesRepository, times(1)).save(any());
    }

    @Test
    void deleteCoordinates_Success() {
        when(coordinatesRepository.findById(1L)).thenReturn(Optional.of(testCoordinates));
        doNothing().when(coordinatesRepository).delete(testCoordinates);

        coordinatesService.deleteCoordinates(1L);

        verify(coordinatesRepository, times(1)).delete(testCoordinates);
    }
}

