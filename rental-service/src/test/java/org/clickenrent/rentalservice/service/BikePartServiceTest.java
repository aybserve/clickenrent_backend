package org.clickenrent.rentalservice.service;

import org.clickenrent.rentalservice.dto.BikePartDTO;
import org.clickenrent.rentalservice.entity.Bike;
import org.clickenrent.rentalservice.entity.BikePart;
import org.clickenrent.rentalservice.entity.Part;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.mapper.BikePartMapper;
import org.clickenrent.rentalservice.repository.BikePartRepository;
import org.clickenrent.rentalservice.repository.BikeRepository;
import org.clickenrent.rentalservice.repository.PartRepository;
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

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BikePartServiceTest {

    @Mock
    private BikePartRepository bikePartRepository;

    @Mock
    private BikeRepository bikeRepository;

    @Mock
    private PartRepository partRepository;

    @Mock
    private BikePartMapper bikePartMapper;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private BikePartService bikePartService;

    private BikePart testBikePart;
    private BikePartDTO testBikePartDTO;

    @BeforeEach
    void setUp() {
        testBikePart = BikePart.builder()
        .id(1L)
        .build();

        testBikePartDTO = BikePartDTO.builder()
        .id(1L)
        .bikeId(1L)
        .partId(1L)
        .build();
    }

    @Test
    void getAllBikeParts_ReturnsAll() {
        when(securityService.isAdmin()).thenReturn(true);
        Pageable pageable = PageRequest.of(0, 20);
        Page<BikePart> bikePartPage = new PageImpl<>(Collections.singletonList(testBikePart));
        when(bikePartRepository.findAll(pageable)).thenReturn(bikePartPage);
        when(bikePartMapper.toDto(testBikePart)).thenReturn(testBikePartDTO);

        Page<BikePartDTO> result = bikePartService.getAllBikeParts(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getBikePartById_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikePartRepository.findById(1L)).thenReturn(Optional.of(testBikePart));
        when(bikePartMapper.toDto(testBikePart)).thenReturn(testBikePartDTO);

        BikePartDTO result = bikePartService.getBikePartById(1L);

        assertNotNull(result);
    }

    @Test
    void getBikePartById_NotFound() {
        when(bikePartRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bikePartService.getBikePartById(999L));
    }

    @Test
    void createBikePart_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeRepository.findById(anyLong())).thenReturn(Optional.of(new Bike()));
        when(partRepository.findById(anyLong())).thenReturn(Optional.of(new Part()));
        when(bikePartMapper.toEntity(testBikePartDTO)).thenReturn(testBikePart);
        when(bikePartRepository.save(any())).thenReturn(testBikePart);
        when(bikePartMapper.toDto(testBikePart)).thenReturn(testBikePartDTO);

        BikePartDTO result = bikePartService.createBikePart(testBikePartDTO);

        assertNotNull(result);
    }

    @Test
    void deleteBikePart_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikePartRepository.findById(1L)).thenReturn(Optional.of(testBikePart));
        doNothing().when(bikePartRepository).delete(testBikePart);

        bikePartService.deleteBikePart(1L);

        verify(bikePartRepository, times(1)).delete(testBikePart);
    }
}
