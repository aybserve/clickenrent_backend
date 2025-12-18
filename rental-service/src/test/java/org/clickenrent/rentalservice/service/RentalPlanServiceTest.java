package org.clickenrent.rentalservice.service;

import org.clickenrent.rentalservice.dto.RentalPlanDTO;
import org.clickenrent.rentalservice.entity.Location;
import org.clickenrent.rentalservice.entity.RentalPlan;
import org.clickenrent.rentalservice.entity.RentalUnit;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.mapper.RentalPlanMapper;
import org.clickenrent.rentalservice.repository.LocationRepository;
import org.clickenrent.rentalservice.repository.RentalPlanRepository;
import org.clickenrent.rentalservice.repository.RentalUnitRepository;
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

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RentalPlanServiceTest {

    @Mock
    private RentalPlanRepository rentalPlanRepository;

    @Mock
    private RentalUnitRepository rentalUnitRepository;

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private RentalPlanMapper rentalPlanMapper;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private RentalPlanService rentalPlanService;

    private RentalPlan testPlan;
    private RentalPlanDTO testPlanDTO;
    private RentalUnit testRentalUnit;
    private Location testLocation;

    @BeforeEach
    void setUp() {
        testRentalUnit = RentalUnit.builder()
        .id(1L)
        .name("Hour")
        .build();

        testLocation = Location.builder()
        .id(1L)
        .companyId(1L)
        .build();

        testPlan = RentalPlan.builder()
        .id(1L)
        .name("Daily Plan")
        .rentalUnit(testRentalUnit)
        .minUnit(1)
        .maxUnit(24)
        .location(testLocation)
        .defaultPrice(new BigDecimal("25.00"))
        .build();

        testPlanDTO = RentalPlanDTO.builder()
        .id(1L)
        .name("Daily Plan")
        .rentalUnitId(1L)
        .minUnit(1)
        .maxUnit(24)
        .locationId(1L)
        .defaultPrice(new BigDecimal("25.00"))
        .build();

    }

    @Test
    void getAllRentalPlans_ReturnsAll() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<RentalPlan> planPage = new PageImpl<>(Collections.singletonList(testPlan));
        when(rentalPlanRepository.findAll(pageable)).thenReturn(planPage);
        when(rentalPlanMapper.toDto(testPlan)).thenReturn(testPlanDTO);

        Page<RentalPlanDTO> result = rentalPlanService.getAllRentalPlans(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getRentalPlanById_Success() {
        when(rentalPlanRepository.findById(1L)).thenReturn(Optional.of(testPlan));
        when(rentalPlanMapper.toDto(testPlan)).thenReturn(testPlanDTO);

        RentalPlanDTO result = rentalPlanService.getRentalPlanById(1L);

        assertNotNull(result);
        assertEquals("Daily Plan", result.getName());
    }

    @Test
    void getRentalPlanById_NotFound() {
        when(rentalPlanRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> rentalPlanService.getRentalPlanById(999L));
    }

    @Test
    void createRentalPlan_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(locationRepository.findById(anyLong())).thenReturn(Optional.of(new Location()));
        when(securityService.isAdmin()).thenReturn(true);
        when(rentalPlanMapper.toEntity(testPlanDTO)).thenReturn(testPlan);
        when(rentalPlanRepository.save(any())).thenReturn(testPlan);
        when(rentalPlanMapper.toDto(testPlan)).thenReturn(testPlanDTO);

        RentalPlanDTO result = rentalPlanService.createRentalPlan(testPlanDTO);

        assertNotNull(result);
    }

    @Test
    void updateRentalPlan_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(rentalPlanRepository.findById(1L)).thenReturn(Optional.of(testPlan));
        doNothing().when(rentalPlanMapper).updateEntityFromDto(testPlanDTO, testPlan);
        when(rentalPlanRepository.save(any())).thenReturn(testPlan);
        when(rentalPlanMapper.toDto(testPlan)).thenReturn(testPlanDTO);

        RentalPlanDTO result = rentalPlanService.updateRentalPlan(1L, testPlanDTO);

        assertNotNull(result);
        verify(rentalPlanMapper, times(1)).updateEntityFromDto(testPlanDTO, testPlan);
    }

    @Test
    void deleteRentalPlan_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(rentalPlanRepository.findById(1L)).thenReturn(Optional.of(testPlan));
        doNothing().when(rentalPlanRepository).delete(testPlan);

        rentalPlanService.deleteRentalPlan(1L);

        verify(rentalPlanRepository, times(1)).delete(testPlan);
    }
}


