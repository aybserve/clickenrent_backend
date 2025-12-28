package org.clickenrent.supportservice.service;

import org.clickenrent.supportservice.dto.BikeTypeBikeIssueDTO;
import org.clickenrent.supportservice.entity.BikeIssue;
import org.clickenrent.supportservice.entity.BikeTypeBikeIssue;
import org.clickenrent.supportservice.exception.ResourceNotFoundException;
import org.clickenrent.supportservice.exception.UnauthorizedException;
import org.clickenrent.supportservice.mapper.BikeTypeBikeIssueMapper;
import org.clickenrent.supportservice.repository.BikeTypeBikeIssueRepository;
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
class BikeTypeBikeIssueServiceTest {

    @Mock
    private BikeTypeBikeIssueRepository bikeTypeBikeIssueRepository;

    @Mock
    private BikeTypeBikeIssueMapper bikeTypeBikeIssueMapper;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private BikeTypeBikeIssueService bikeTypeBikeIssueService;

    private BikeTypeBikeIssue testJunction;
    private BikeTypeBikeIssueDTO testJunctionDTO;
    private BikeIssue testBikeIssue;

    @BeforeEach
    void setUp() {
        testBikeIssue = BikeIssue.builder()
                .id(1L)
                .name("Battery Issues")
                .build();

        testJunction = BikeTypeBikeIssue.builder()
                .id(1L)
                .bikeTypeExternalId("bike-type-uuid-1")
                .bikeIssue(testBikeIssue)
                .build();

        testJunctionDTO = BikeTypeBikeIssueDTO.builder()
                .id(1L)
                .bikeTypeExternalId("bike-type-uuid-1")
                .build();
    }

    @Test
    void getAll_ReturnsAllJunctions() {
        when(bikeTypeBikeIssueRepository.findAll()).thenReturn(Arrays.asList(testJunction));
        when(bikeTypeBikeIssueMapper.toDto(testJunction)).thenReturn(testJunctionDTO);

        List<BikeTypeBikeIssueDTO> result = bikeTypeBikeIssueService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bikeTypeBikeIssueRepository, times(1)).findAll();
    }

    @Test
    void getById_Success() {
        when(bikeTypeBikeIssueRepository.findById(1L)).thenReturn(Optional.of(testJunction));
        when(bikeTypeBikeIssueMapper.toDto(testJunction)).thenReturn(testJunctionDTO);

        BikeTypeBikeIssueDTO result = bikeTypeBikeIssueService.getById(1L);

        assertNotNull(result);
        assertEquals("bike-type-uuid-1", result.getBikeTypeExternalId());
        verify(bikeTypeBikeIssueRepository, times(1)).findById(1L);
    }

    @Test
    void getById_NotFound() {
        when(bikeTypeBikeIssueRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bikeTypeBikeIssueService.getById(999L));
    }

    @Test
    void getByBikeTypeExternalId_Success() {
        when(bikeTypeBikeIssueRepository.findByBikeTypeExternalId("bike-type-uuid-1")).thenReturn(Arrays.asList(testJunction));
        when(bikeTypeBikeIssueMapper.toDto(testJunction)).thenReturn(testJunctionDTO);

        List<BikeTypeBikeIssueDTO> result = bikeTypeBikeIssueService.getByBikeTypeExternalId("bike-type-uuid-1");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("bike-type-uuid-1", result.get(0).getBikeTypeExternalId());
        verify(bikeTypeBikeIssueRepository, times(1)).findByBikeTypeExternalId("bike-type-uuid-1");
    }

    @Test
    void getByBikeIssueId_Success() {
        when(bikeTypeBikeIssueRepository.findByBikeIssueId(1L)).thenReturn(Arrays.asList(testJunction));
        when(bikeTypeBikeIssueMapper.toDto(testJunction)).thenReturn(testJunctionDTO);

        List<BikeTypeBikeIssueDTO> result = bikeTypeBikeIssueService.getByBikeIssueId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bikeTypeBikeIssueRepository, times(1)).findByBikeIssueId(1L);
    }

    @Test
    void create_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeTypeBikeIssueMapper.toEntity(testJunctionDTO)).thenReturn(testJunction);
        when(bikeTypeBikeIssueRepository.save(any(BikeTypeBikeIssue.class))).thenReturn(testJunction);
        when(bikeTypeBikeIssueMapper.toDto(testJunction)).thenReturn(testJunctionDTO);

        BikeTypeBikeIssueDTO result = bikeTypeBikeIssueService.create(testJunctionDTO);

        assertNotNull(result);
        verify(bikeTypeBikeIssueRepository, times(1)).save(any(BikeTypeBikeIssue.class));
    }

    @Test
    void create_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> bikeTypeBikeIssueService.create(testJunctionDTO));
        verify(bikeTypeBikeIssueRepository, never()).save(any(BikeTypeBikeIssue.class));
    }

    @Test
    void update_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeTypeBikeIssueRepository.findById(1L)).thenReturn(Optional.of(testJunction));
        doNothing().when(bikeTypeBikeIssueMapper).updateEntityFromDto(testJunctionDTO, testJunction);
        when(bikeTypeBikeIssueRepository.save(any(BikeTypeBikeIssue.class))).thenReturn(testJunction);
        when(bikeTypeBikeIssueMapper.toDto(testJunction)).thenReturn(testJunctionDTO);

        BikeTypeBikeIssueDTO result = bikeTypeBikeIssueService.update(1L, testJunctionDTO);

        assertNotNull(result);
        verify(bikeTypeBikeIssueRepository, times(1)).save(any(BikeTypeBikeIssue.class));
    }

    @Test
    void update_NotFound() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeTypeBikeIssueRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bikeTypeBikeIssueService.update(999L, testJunctionDTO));
    }

    @Test
    void update_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> bikeTypeBikeIssueService.update(1L, testJunctionDTO));
    }

    @Test
    void delete_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeTypeBikeIssueRepository.findById(1L)).thenReturn(Optional.of(testJunction));
        doNothing().when(bikeTypeBikeIssueRepository).delete(testJunction);

        bikeTypeBikeIssueService.delete(1L);

        verify(bikeTypeBikeIssueRepository, times(1)).delete(testJunction);
    }

    @Test
    void delete_NotFound() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeTypeBikeIssueRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bikeTypeBikeIssueService.delete(999L));
    }

    @Test
    void delete_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> bikeTypeBikeIssueService.delete(1L));
    }
}








