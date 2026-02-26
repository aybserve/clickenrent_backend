package org.clickenrent.supportservice.service;

import org.clickenrent.supportservice.dto.BikeInspectionItemStatusDTO;
import org.clickenrent.supportservice.entity.BikeInspectionItemStatus;
import org.clickenrent.supportservice.exception.ResourceNotFoundException;
import org.clickenrent.supportservice.exception.UnauthorizedException;
import org.clickenrent.supportservice.mapper.BikeInspectionItemStatusMapper;
import org.clickenrent.supportservice.repository.BikeInspectionItemStatusRepository;
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
class BikeInspectionItemStatusServiceTest {

    @Mock
    private BikeInspectionItemStatusRepository bikeInspectionItemStatusRepository;

    @Mock
    private BikeInspectionItemStatusMapper bikeInspectionItemStatusMapper;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private BikeInspectionItemStatusService bikeInspectionItemStatusService;

    private BikeInspectionItemStatus testEntity;
    private BikeInspectionItemStatusDTO testDTO;

    @BeforeEach
    void setUp() {
        testEntity = BikeInspectionItemStatus.builder()
                .id(1L)
                .externalId("item-status-uuid-1")
                .name("OK")
                .build();

        testDTO = BikeInspectionItemStatusDTO.builder()
                .id(1L)
                .externalId("item-status-uuid-1")
                .name("OK")
                .build();
    }

    @Test
    void getAll_ReturnsAll() {
        when(bikeInspectionItemStatusRepository.findAll()).thenReturn(Arrays.asList(testEntity));
        when(bikeInspectionItemStatusMapper.toDto(testEntity)).thenReturn(testDTO);

        List<BikeInspectionItemStatusDTO> result = bikeInspectionItemStatusService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("OK", result.get(0).getName());
        verify(bikeInspectionItemStatusRepository, times(1)).findAll();
    }

    @Test
    void getById_Success() {
        when(bikeInspectionItemStatusRepository.findById(1L)).thenReturn(Optional.of(testEntity));
        when(bikeInspectionItemStatusMapper.toDto(testEntity)).thenReturn(testDTO);

        BikeInspectionItemStatusDTO result = bikeInspectionItemStatusService.getById(1L);

        assertNotNull(result);
        assertEquals("OK", result.getName());
        verify(bikeInspectionItemStatusRepository, times(1)).findById(1L);
    }

    @Test
    void getById_NotFound() {
        when(bikeInspectionItemStatusRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bikeInspectionItemStatusService.getById(999L));
    }

    @Test
    void getByExternalId_Success() {
        when(bikeInspectionItemStatusRepository.findByExternalId("item-status-uuid-1")).thenReturn(Optional.of(testEntity));
        when(bikeInspectionItemStatusMapper.toDto(testEntity)).thenReturn(testDTO);

        BikeInspectionItemStatusDTO result = bikeInspectionItemStatusService.getByExternalId("item-status-uuid-1");

        assertNotNull(result);
        assertEquals("item-status-uuid-1", result.getExternalId());
    }

    @Test
    void getByExternalId_NotFound() {
        when(bikeInspectionItemStatusRepository.findByExternalId("invalid")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bikeInspectionItemStatusService.getByExternalId("invalid"));
    }

    @Test
    void getByName_Success() {
        when(bikeInspectionItemStatusRepository.findByName("OK")).thenReturn(Optional.of(testEntity));
        when(bikeInspectionItemStatusMapper.toDto(testEntity)).thenReturn(testDTO);

        BikeInspectionItemStatusDTO result = bikeInspectionItemStatusService.getByName("OK");

        assertNotNull(result);
        assertEquals("OK", result.getName());
        verify(bikeInspectionItemStatusRepository, times(1)).findByName("OK");
    }

    @Test
    void getByName_NotFound() {
        when(bikeInspectionItemStatusRepository.findByName("UNKNOWN")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bikeInspectionItemStatusService.getByName("UNKNOWN"));
    }

    @Test
    void create_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeInspectionItemStatusMapper.toEntity(testDTO)).thenReturn(testEntity);
        when(bikeInspectionItemStatusRepository.save(any(BikeInspectionItemStatus.class))).thenReturn(testEntity);
        when(bikeInspectionItemStatusMapper.toDto(testEntity)).thenReturn(testDTO);

        BikeInspectionItemStatusDTO result = bikeInspectionItemStatusService.create(testDTO);

        assertNotNull(result);
        verify(bikeInspectionItemStatusRepository, times(1)).save(any(BikeInspectionItemStatus.class));
    }

    @Test
    void create_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> bikeInspectionItemStatusService.create(testDTO));
        verify(bikeInspectionItemStatusRepository, never()).save(any(BikeInspectionItemStatus.class));
    }

    @Test
    void update_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeInspectionItemStatusRepository.findById(1L)).thenReturn(Optional.of(testEntity));
        doNothing().when(bikeInspectionItemStatusMapper).updateEntityFromDto(testDTO, testEntity);
        when(bikeInspectionItemStatusRepository.save(any(BikeInspectionItemStatus.class))).thenReturn(testEntity);
        when(bikeInspectionItemStatusMapper.toDto(testEntity)).thenReturn(testDTO);

        BikeInspectionItemStatusDTO result = bikeInspectionItemStatusService.update(1L, testDTO);

        assertNotNull(result);
        verify(bikeInspectionItemStatusRepository, times(1)).save(any(BikeInspectionItemStatus.class));
    }

    @Test
    void update_NotFound() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeInspectionItemStatusRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bikeInspectionItemStatusService.update(999L, testDTO));
    }

    @Test
    void update_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> bikeInspectionItemStatusService.update(1L, testDTO));
    }

    @Test
    void delete_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeInspectionItemStatusRepository.findById(1L)).thenReturn(Optional.of(testEntity));
        doNothing().when(bikeInspectionItemStatusRepository).delete(testEntity);

        bikeInspectionItemStatusService.delete(1L);

        verify(bikeInspectionItemStatusRepository, times(1)).delete(testEntity);
    }

    @Test
    void delete_NotFound() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeInspectionItemStatusRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bikeInspectionItemStatusService.delete(999L));
    }

    @Test
    void delete_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> bikeInspectionItemStatusService.delete(1L));
    }
}
