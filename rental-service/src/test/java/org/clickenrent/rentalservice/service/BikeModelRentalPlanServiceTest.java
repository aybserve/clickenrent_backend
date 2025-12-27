package org.clickenrent.rentalservice.service;

import org.clickenrent.rentalservice.dto.BikeModelRentalPlanDTO;
import org.clickenrent.rentalservice.entity.BikeModel;
import org.clickenrent.rentalservice.entity.BikeModelRentalPlan;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.mapper.BikeModelRentalPlanMapper;
import org.clickenrent.rentalservice.repository.BikeModelRentalPlanRepository;
import org.clickenrent.rentalservice.repository.BikeModelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BikeModelRentalPlanServiceTest {

    @Mock
    private BikeModelRentalPlanRepository bikeModelRentalPlanRepository;

    @Mock
    private BikeModelRepository bikeModelRepository;

    @Mock
    private BikeModelRentalPlanMapper bikeModelRentalPlanMapper;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private BikeModelRentalPlanService bikeModelRentalPlanService;

    private BikeModelRentalPlan testPlan;
    private BikeModelRentalPlanDTO testPlanDTO;
    private BikeModel testBikeModel;

    @BeforeEach
    void setUp() {
        testBikeModel = BikeModel.builder()
        .id(1L)
        .build();

        testPlan = BikeModelRentalPlan.builder()
        .id(1L)
        .bikeModel(testBikeModel)
        .build();

        testPlanDTO = BikeModelRentalPlanDTO.builder()
        .id(1L)
        .bikeModelId(1L)
        .rentalPlanId(1L)
        .build();

    }

    @Test
    void getPlansByBikeModel_Success() {
        when(bikeModelRepository.findById(1L)).thenReturn(Optional.of(testBikeModel));
        when(bikeModelRentalPlanRepository.findByBikeModel(testBikeModel))
        .thenReturn(Collections.singletonList(testPlan));
        when(bikeModelRentalPlanMapper.toDto(testPlan)).thenReturn(testPlanDTO);

        List<BikeModelRentalPlanDTO> result = bikeModelRentalPlanService.getPlansByBikeModel(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getBikeModelId());
        verify(bikeModelRepository, times(1)).findById(1L);
    }

    @Test
    void getById_Success() {
        when(bikeModelRentalPlanRepository.findById(1L)).thenReturn(Optional.of(testPlan));
        when(bikeModelRentalPlanMapper.toDto(testPlan)).thenReturn(testPlanDTO);

        BikeModelRentalPlanDTO result = bikeModelRentalPlanService.getById(1L);

        assertNotNull(result);
        verify(bikeModelRentalPlanRepository, times(1)).findById(1L);
    }

    @Test
    void getById_NotFound() {
        when(bikeModelRentalPlanRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bikeModelRentalPlanService.getById(999L));
    }

    @Test
    void create_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeModelRentalPlanMapper.toEntity(testPlanDTO)).thenReturn(testPlan);
        when(bikeModelRentalPlanRepository.save(any())).thenReturn(testPlan);
        when(bikeModelRentalPlanMapper.toDto(testPlan)).thenReturn(testPlanDTO);

        BikeModelRentalPlanDTO result = bikeModelRentalPlanService.create(testPlanDTO);

        assertNotNull(result);
        verify(bikeModelRentalPlanRepository, times(1)).save(any());
    }

    @Test
    void update_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeModelRentalPlanRepository.findById(1L)).thenReturn(Optional.of(testPlan));
        doNothing().when(bikeModelRentalPlanMapper).updateEntityFromDto(testPlanDTO, testPlan);
        when(bikeModelRentalPlanRepository.save(any())).thenReturn(testPlan);
        when(bikeModelRentalPlanMapper.toDto(testPlan)).thenReturn(testPlanDTO);

        BikeModelRentalPlanDTO result = bikeModelRentalPlanService.update(1L, testPlanDTO);

        assertNotNull(result);
        verify(bikeModelRentalPlanMapper, times(1)).updateEntityFromDto(testPlanDTO, testPlan);
    }

    @Test
    void delete_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeModelRentalPlanRepository.findById(1L)).thenReturn(Optional.of(testPlan));
        doNothing().when(bikeModelRentalPlanRepository).delete(testPlan);

        bikeModelRentalPlanService.delete(1L);

        verify(bikeModelRentalPlanRepository, times(1)).delete(testPlan);
    }

    @Test
    void delete_NotFound() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeModelRentalPlanRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bikeModelRentalPlanService.delete(999L));
    }
}







