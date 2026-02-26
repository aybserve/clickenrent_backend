package org.clickenrent.supportservice.service;

import org.clickenrent.supportservice.dto.BikeUnitDTO;
import org.clickenrent.supportservice.entity.BikeUnit;
import org.clickenrent.supportservice.exception.ResourceNotFoundException;
import org.clickenrent.supportservice.exception.UnauthorizedException;
import org.clickenrent.supportservice.mapper.BikeUnitMapper;
import org.clickenrent.supportservice.repository.BikeUnitRepository;
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
class BikeUnitServiceTest {

    @Mock
    private BikeUnitRepository bikeUnitRepository;

    @Mock
    private BikeUnitMapper bikeUnitMapper;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private BikeUnitService bikeUnitService;

    private BikeUnit testEntity;
    private BikeUnitDTO testDTO;

    @BeforeEach
    void setUp() {
        testEntity = BikeUnit.builder()
                .id(1L)
                .externalId("unit-uuid-1")
                .name("Unit A")
                .companyExternalId("company-uuid-1")
                .build();

        testDTO = BikeUnitDTO.builder()
                .id(1L)
                .externalId("unit-uuid-1")
                .name("Unit A")
                .companyExternalId("company-uuid-1")
                .build();
    }

    @Test
    void getAll_ReturnsAll() {
        when(bikeUnitRepository.findAll()).thenReturn(Arrays.asList(testEntity));
        when(bikeUnitMapper.toDto(testEntity)).thenReturn(testDTO);

        List<BikeUnitDTO> result = bikeUnitService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Unit A", result.get(0).getName());
        verify(bikeUnitRepository, times(1)).findAll();
    }

    @Test
    void getById_Success() {
        when(bikeUnitRepository.findById(1L)).thenReturn(Optional.of(testEntity));
        when(bikeUnitMapper.toDto(testEntity)).thenReturn(testDTO);

        BikeUnitDTO result = bikeUnitService.getById(1L);

        assertNotNull(result);
        assertEquals("Unit A", result.getName());
        verify(bikeUnitRepository, times(1)).findById(1L);
    }

    @Test
    void getById_NotFound() {
        when(bikeUnitRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bikeUnitService.getById(999L));
    }

    @Test
    void getByExternalId_Success() {
        when(bikeUnitRepository.findByExternalId("unit-uuid-1")).thenReturn(Optional.of(testEntity));
        when(bikeUnitMapper.toDto(testEntity)).thenReturn(testDTO);

        BikeUnitDTO result = bikeUnitService.getByExternalId("unit-uuid-1");

        assertNotNull(result);
        assertEquals("unit-uuid-1", result.getExternalId());
    }

    @Test
    void getByExternalId_NotFound() {
        when(bikeUnitRepository.findByExternalId("invalid")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bikeUnitService.getByExternalId("invalid"));
    }

    @Test
    void getByCompanyExternalId_Success() {
        when(bikeUnitRepository.findByCompanyExternalId("company-uuid-1")).thenReturn(Arrays.asList(testEntity));
        when(bikeUnitMapper.toDto(testEntity)).thenReturn(testDTO);

        List<BikeUnitDTO> result = bikeUnitService.getByCompanyExternalId("company-uuid-1");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bikeUnitRepository, times(1)).findByCompanyExternalId("company-uuid-1");
    }

    @Test
    void create_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeUnitMapper.toEntity(testDTO)).thenReturn(testEntity);
        when(bikeUnitRepository.save(any(BikeUnit.class))).thenReturn(testEntity);
        when(bikeUnitMapper.toDto(testEntity)).thenReturn(testDTO);

        BikeUnitDTO result = bikeUnitService.create(testDTO);

        assertNotNull(result);
        verify(bikeUnitRepository, times(1)).save(any(BikeUnit.class));
    }

    @Test
    void create_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> bikeUnitService.create(testDTO));
        verify(bikeUnitRepository, never()).save(any(BikeUnit.class));
    }

    @Test
    void update_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeUnitRepository.findById(1L)).thenReturn(Optional.of(testEntity));
        doNothing().when(bikeUnitMapper).updateEntityFromDto(testDTO, testEntity);
        when(bikeUnitRepository.save(any(BikeUnit.class))).thenReturn(testEntity);
        when(bikeUnitMapper.toDto(testEntity)).thenReturn(testDTO);

        BikeUnitDTO result = bikeUnitService.update(1L, testDTO);

        assertNotNull(result);
        verify(bikeUnitRepository, times(1)).save(any(BikeUnit.class));
    }

    @Test
    void update_NotFound() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeUnitRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bikeUnitService.update(999L, testDTO));
    }

    @Test
    void update_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> bikeUnitService.update(1L, testDTO));
    }

    @Test
    void delete_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeUnitRepository.findById(1L)).thenReturn(Optional.of(testEntity));
        doNothing().when(bikeUnitRepository).delete(testEntity);

        bikeUnitService.delete(1L);

        verify(bikeUnitRepository, times(1)).delete(testEntity);
    }

    @Test
    void delete_NotFound() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeUnitRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bikeUnitService.delete(999L));
    }

    @Test
    void delete_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> bikeUnitService.delete(1L));
    }
}
