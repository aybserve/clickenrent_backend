package org.clickenrent.supportservice.service;

import org.clickenrent.supportservice.dto.BikeInspectionStatusDTO;
import org.clickenrent.supportservice.entity.BikeInspectionStatus;
import org.clickenrent.supportservice.exception.ResourceNotFoundException;
import org.clickenrent.supportservice.exception.UnauthorizedException;
import org.clickenrent.supportservice.mapper.BikeInspectionStatusMapper;
import org.clickenrent.supportservice.repository.BikeInspectionStatusRepository;
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
class BikeInspectionStatusServiceTest {

    @Mock
    private BikeInspectionStatusRepository bikeInspectionStatusRepository;

    @Mock
    private BikeInspectionStatusMapper bikeInspectionStatusMapper;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private BikeInspectionStatusService bikeInspectionStatusService;

    private BikeInspectionStatus testEntity;
    private BikeInspectionStatusDTO testDTO;

    @BeforeEach
    void setUp() {
        testEntity = BikeInspectionStatus.builder()
                .id(1L)
                .externalId("status-uuid-1")
                .name("PENDING")
                .build();

        testDTO = BikeInspectionStatusDTO.builder()
                .id(1L)
                .externalId("status-uuid-1")
                .name("PENDING")
                .build();
    }

    @Test
    void getAll_ReturnsAll() {
        when(bikeInspectionStatusRepository.findAll()).thenReturn(Arrays.asList(testEntity));
        when(bikeInspectionStatusMapper.toDto(testEntity)).thenReturn(testDTO);

        List<BikeInspectionStatusDTO> result = bikeInspectionStatusService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("PENDING", result.get(0).getName());
        verify(bikeInspectionStatusRepository, times(1)).findAll();
    }

    @Test
    void getById_Success() {
        when(bikeInspectionStatusRepository.findById(1L)).thenReturn(Optional.of(testEntity));
        when(bikeInspectionStatusMapper.toDto(testEntity)).thenReturn(testDTO);

        BikeInspectionStatusDTO result = bikeInspectionStatusService.getById(1L);

        assertNotNull(result);
        assertEquals("PENDING", result.getName());
        verify(bikeInspectionStatusRepository, times(1)).findById(1L);
    }

    @Test
    void getById_NotFound() {
        when(bikeInspectionStatusRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bikeInspectionStatusService.getById(999L));
    }

    @Test
    void getByExternalId_Success() {
        when(bikeInspectionStatusRepository.findByExternalId("status-uuid-1")).thenReturn(Optional.of(testEntity));
        when(bikeInspectionStatusMapper.toDto(testEntity)).thenReturn(testDTO);

        BikeInspectionStatusDTO result = bikeInspectionStatusService.getByExternalId("status-uuid-1");

        assertNotNull(result);
        assertEquals("status-uuid-1", result.getExternalId());
    }

    @Test
    void getByExternalId_NotFound() {
        when(bikeInspectionStatusRepository.findByExternalId("invalid")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bikeInspectionStatusService.getByExternalId("invalid"));
    }

    @Test
    void getByName_Success() {
        when(bikeInspectionStatusRepository.findByName("PENDING")).thenReturn(Optional.of(testEntity));
        when(bikeInspectionStatusMapper.toDto(testEntity)).thenReturn(testDTO);

        BikeInspectionStatusDTO result = bikeInspectionStatusService.getByName("PENDING");

        assertNotNull(result);
        assertEquals("PENDING", result.getName());
        verify(bikeInspectionStatusRepository, times(1)).findByName("PENDING");
    }

    @Test
    void getByName_NotFound() {
        when(bikeInspectionStatusRepository.findByName("UNKNOWN")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bikeInspectionStatusService.getByName("UNKNOWN"));
    }

    @Test
    void create_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeInspectionStatusMapper.toEntity(testDTO)).thenReturn(testEntity);
        when(bikeInspectionStatusRepository.save(any(BikeInspectionStatus.class))).thenReturn(testEntity);
        when(bikeInspectionStatusMapper.toDto(testEntity)).thenReturn(testDTO);

        BikeInspectionStatusDTO result = bikeInspectionStatusService.create(testDTO);

        assertNotNull(result);
        verify(bikeInspectionStatusRepository, times(1)).save(any(BikeInspectionStatus.class));
    }

    @Test
    void create_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> bikeInspectionStatusService.create(testDTO));
        verify(bikeInspectionStatusRepository, never()).save(any(BikeInspectionStatus.class));
    }

    @Test
    void update_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeInspectionStatusRepository.findById(1L)).thenReturn(Optional.of(testEntity));
        doNothing().when(bikeInspectionStatusMapper).updateEntityFromDto(testDTO, testEntity);
        when(bikeInspectionStatusRepository.save(any(BikeInspectionStatus.class))).thenReturn(testEntity);
        when(bikeInspectionStatusMapper.toDto(testEntity)).thenReturn(testDTO);

        BikeInspectionStatusDTO result = bikeInspectionStatusService.update(1L, testDTO);

        assertNotNull(result);
        verify(bikeInspectionStatusRepository, times(1)).save(any(BikeInspectionStatus.class));
    }

    @Test
    void update_NotFound() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeInspectionStatusRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bikeInspectionStatusService.update(999L, testDTO));
    }

    @Test
    void update_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> bikeInspectionStatusService.update(1L, testDTO));
    }

    @Test
    void delete_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeInspectionStatusRepository.findById(1L)).thenReturn(Optional.of(testEntity));
        doNothing().when(bikeInspectionStatusRepository).delete(testEntity);

        bikeInspectionStatusService.delete(1L);

        verify(bikeInspectionStatusRepository, times(1)).delete(testEntity);
    }

    @Test
    void delete_NotFound() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeInspectionStatusRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bikeInspectionStatusService.delete(999L));
    }

    @Test
    void delete_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> bikeInspectionStatusService.delete(1L));
    }
}
