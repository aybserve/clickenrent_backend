package org.clickenrent.supportservice.service;

import org.clickenrent.supportservice.dto.BikeInspectionDTO;
import org.clickenrent.supportservice.entity.BikeInspection;
import org.clickenrent.supportservice.exception.ResourceNotFoundException;
import org.clickenrent.supportservice.exception.UnauthorizedException;
import org.clickenrent.supportservice.mapper.BikeInspectionMapper;
import org.clickenrent.supportservice.repository.BikeInspectionRepository;
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
class BikeInspectionServiceTest {

    @Mock
    private BikeInspectionRepository bikeInspectionRepository;

    @Mock
    private BikeInspectionMapper bikeInspectionMapper;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private BikeInspectionService bikeInspectionService;

    private BikeInspection testEntity;
    private BikeInspectionDTO testDTO;

    @BeforeEach
    void setUp() {
        testEntity = BikeInspection.builder()
                .id(1L)
                .externalId("inspection-uuid-1")
                .userExternalId("user-uuid-1")
                .companyExternalId("company-uuid-1")
                .comment("Check")
                .build();

        testDTO = BikeInspectionDTO.builder()
                .id(1L)
                .externalId("inspection-uuid-1")
                .userExternalId("user-uuid-1")
                .companyExternalId("company-uuid-1")
                .comment("Check")
                .bikeInspectionStatusId(1L)
                .bikeInspectionStatusName("PENDING")
                .build();
    }

    @Test
    void getAll_ReturnsAll() {
        when(bikeInspectionRepository.findAll()).thenReturn(Arrays.asList(testEntity));
        when(bikeInspectionMapper.toDto(testEntity)).thenReturn(testDTO);

        List<BikeInspectionDTO> result = bikeInspectionService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("inspection-uuid-1", result.get(0).getExternalId());
        verify(bikeInspectionRepository, times(1)).findAll();
    }

    @Test
    void getById_Success() {
        when(bikeInspectionRepository.findById(1L)).thenReturn(Optional.of(testEntity));
        when(bikeInspectionMapper.toDto(testEntity)).thenReturn(testDTO);

        BikeInspectionDTO result = bikeInspectionService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(bikeInspectionRepository, times(1)).findById(1L);
    }

    @Test
    void getById_NotFound() {
        when(bikeInspectionRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bikeInspectionService.getById(999L));
    }

    @Test
    void getByExternalId_Success() {
        when(bikeInspectionRepository.findByExternalId("inspection-uuid-1")).thenReturn(Optional.of(testEntity));
        when(bikeInspectionMapper.toDto(testEntity)).thenReturn(testDTO);

        BikeInspectionDTO result = bikeInspectionService.getByExternalId("inspection-uuid-1");

        assertNotNull(result);
        assertEquals("inspection-uuid-1", result.getExternalId());
    }

    @Test
    void getByExternalId_NotFound() {
        when(bikeInspectionRepository.findByExternalId("invalid")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bikeInspectionService.getByExternalId("invalid"));
    }

    @Test
    void getByUserExternalId_Success() {
        when(bikeInspectionRepository.findByUserExternalId("user-uuid-1")).thenReturn(Arrays.asList(testEntity));
        when(bikeInspectionMapper.toDto(testEntity)).thenReturn(testDTO);

        List<BikeInspectionDTO> result = bikeInspectionService.getByUserExternalId("user-uuid-1");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bikeInspectionRepository, times(1)).findByUserExternalId("user-uuid-1");
    }

    @Test
    void getByCompanyExternalId_Success() {
        when(bikeInspectionRepository.findByCompanyExternalId("company-uuid-1")).thenReturn(Arrays.asList(testEntity));
        when(bikeInspectionMapper.toDto(testEntity)).thenReturn(testDTO);

        List<BikeInspectionDTO> result = bikeInspectionService.getByCompanyExternalId("company-uuid-1");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bikeInspectionRepository, times(1)).findByCompanyExternalId("company-uuid-1");
    }

    @Test
    void getByStatusId_Success() {
        when(bikeInspectionRepository.findByBikeInspectionStatusId(1L)).thenReturn(Arrays.asList(testEntity));
        when(bikeInspectionMapper.toDto(testEntity)).thenReturn(testDTO);

        List<BikeInspectionDTO> result = bikeInspectionService.getByStatusId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bikeInspectionRepository, times(1)).findByBikeInspectionStatusId(1L);
    }

    @Test
    void create_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeInspectionMapper.toEntity(testDTO)).thenReturn(testEntity);
        when(bikeInspectionRepository.save(any(BikeInspection.class))).thenReturn(testEntity);
        when(bikeInspectionMapper.toDto(testEntity)).thenReturn(testDTO);

        BikeInspectionDTO result = bikeInspectionService.create(testDTO);

        assertNotNull(result);
        verify(bikeInspectionRepository, times(1)).save(any(BikeInspection.class));
    }

    @Test
    void create_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> bikeInspectionService.create(testDTO));
        verify(bikeInspectionRepository, never()).save(any(BikeInspection.class));
    }

    @Test
    void update_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeInspectionRepository.findById(1L)).thenReturn(Optional.of(testEntity));
        doNothing().when(bikeInspectionMapper).updateEntityFromDto(testDTO, testEntity);
        when(bikeInspectionRepository.save(any(BikeInspection.class))).thenReturn(testEntity);
        when(bikeInspectionMapper.toDto(testEntity)).thenReturn(testDTO);

        BikeInspectionDTO result = bikeInspectionService.update(1L, testDTO);

        assertNotNull(result);
        verify(bikeInspectionRepository, times(1)).save(any(BikeInspection.class));
    }

    @Test
    void update_NotFound() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeInspectionRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bikeInspectionService.update(999L, testDTO));
    }

    @Test
    void update_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> bikeInspectionService.update(1L, testDTO));
    }

    @Test
    void delete_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeInspectionRepository.findById(1L)).thenReturn(Optional.of(testEntity));
        doNothing().when(bikeInspectionRepository).delete(testEntity);

        bikeInspectionService.delete(1L);

        verify(bikeInspectionRepository, times(1)).delete(testEntity);
    }

    @Test
    void delete_NotFound() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeInspectionRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bikeInspectionService.delete(999L));
    }

    @Test
    void delete_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> bikeInspectionService.delete(1L));
    }
}
