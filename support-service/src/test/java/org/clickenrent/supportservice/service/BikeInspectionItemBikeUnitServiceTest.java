package org.clickenrent.supportservice.service;

import org.clickenrent.supportservice.dto.BikeInspectionItemBikeUnitDTO;
import org.clickenrent.supportservice.entity.BikeInspection;
import org.clickenrent.supportservice.entity.BikeInspectionItem;
import org.clickenrent.supportservice.entity.BikeInspectionItemBikeUnit;
import org.clickenrent.supportservice.entity.BikeInspectionItemStatus;
import org.clickenrent.supportservice.entity.BikeUnit;
import org.clickenrent.supportservice.exception.ResourceNotFoundException;
import org.clickenrent.supportservice.exception.UnauthorizedException;
import org.clickenrent.supportservice.mapper.BikeInspectionItemBikeUnitMapper;
import org.clickenrent.supportservice.repository.BikeInspectionItemBikeUnitRepository;
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
class BikeInspectionItemBikeUnitServiceTest {

    @Mock
    private BikeInspectionItemBikeUnitRepository bikeInspectionItemBikeUnitRepository;

    @Mock
    private BikeInspectionItemBikeUnitMapper bikeInspectionItemBikeUnitMapper;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private BikeInspectionItemBikeUnitService bikeInspectionItemBikeUnitService;

    private BikeInspectionItemBikeUnit testEntity;
    private BikeInspectionItemBikeUnitDTO testDTO;

    @BeforeEach
    void setUp() {
        BikeInspection inspection = BikeInspection.builder().id(1L).companyExternalId("company-uuid-1").build();
        BikeInspectionItemStatus itemStatus = BikeInspectionItemStatus.builder().id(1L).name("OK").build();
        BikeInspectionItem item = BikeInspectionItem.builder().id(1L).externalId("i1").companyExternalId("company-uuid-1").bikeInspection(inspection).bikeInspectionItemStatus(itemStatus).build();
        BikeUnit unit = BikeUnit.builder().id(1L).externalId("u1").name("Unit A").companyExternalId("company-uuid-1").build();
        testEntity = BikeInspectionItemBikeUnit.builder()
                .id(1L)
                .externalId("link-uuid-1")
                .bikeInspectionItem(item)
                .bikeUnit(unit)
                .hasProblem(false)
                .companyExternalId("company-uuid-1")
                .build();

        testDTO = BikeInspectionItemBikeUnitDTO.builder()
                .id(1L)
                .externalId("link-uuid-1")
                .bikeInspectionItemId(1L)
                .bikeUnitId(1L)
                .bikeUnitName("Unit A")
                .hasProblem(false)
                .companyExternalId("company-uuid-1")
                .build();
    }

    @Test
    void getAll_ReturnsAll() {
        when(bikeInspectionItemBikeUnitRepository.findAll()).thenReturn(Arrays.asList(testEntity));
        when(bikeInspectionItemBikeUnitMapper.toDto(testEntity)).thenReturn(testDTO);

        List<BikeInspectionItemBikeUnitDTO> result = bikeInspectionItemBikeUnitService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("link-uuid-1", result.get(0).getExternalId());
        verify(bikeInspectionItemBikeUnitRepository, times(1)).findAll();
    }

    @Test
    void getById_Success() {
        when(bikeInspectionItemBikeUnitRepository.findById(1L)).thenReturn(Optional.of(testEntity));
        when(bikeInspectionItemBikeUnitMapper.toDto(testEntity)).thenReturn(testDTO);

        BikeInspectionItemBikeUnitDTO result = bikeInspectionItemBikeUnitService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(bikeInspectionItemBikeUnitRepository, times(1)).findById(1L);
    }

    @Test
    void getById_NotFound() {
        when(bikeInspectionItemBikeUnitRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bikeInspectionItemBikeUnitService.getById(999L));
    }

    @Test
    void getByExternalId_Success() {
        when(bikeInspectionItemBikeUnitRepository.findByExternalId("link-uuid-1")).thenReturn(Optional.of(testEntity));
        when(bikeInspectionItemBikeUnitMapper.toDto(testEntity)).thenReturn(testDTO);

        BikeInspectionItemBikeUnitDTO result = bikeInspectionItemBikeUnitService.getByExternalId("link-uuid-1");

        assertNotNull(result);
        assertEquals("link-uuid-1", result.getExternalId());
    }

    @Test
    void getByExternalId_NotFound() {
        when(bikeInspectionItemBikeUnitRepository.findByExternalId("invalid")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bikeInspectionItemBikeUnitService.getByExternalId("invalid"));
    }

    @Test
    void getByBikeInspectionItemId_Success() {
        when(bikeInspectionItemBikeUnitRepository.findByBikeInspectionItemId(1L)).thenReturn(Arrays.asList(testEntity));
        when(bikeInspectionItemBikeUnitMapper.toDto(testEntity)).thenReturn(testDTO);

        List<BikeInspectionItemBikeUnitDTO> result = bikeInspectionItemBikeUnitService.getByBikeInspectionItemId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bikeInspectionItemBikeUnitRepository, times(1)).findByBikeInspectionItemId(1L);
    }

    @Test
    void getByBikeUnitId_Success() {
        when(bikeInspectionItemBikeUnitRepository.findByBikeUnitId(1L)).thenReturn(Arrays.asList(testEntity));
        when(bikeInspectionItemBikeUnitMapper.toDto(testEntity)).thenReturn(testDTO);

        List<BikeInspectionItemBikeUnitDTO> result = bikeInspectionItemBikeUnitService.getByBikeUnitId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bikeInspectionItemBikeUnitRepository, times(1)).findByBikeUnitId(1L);
    }

    @Test
    void getByCompanyExternalId_Success() {
        when(bikeInspectionItemBikeUnitRepository.findByCompanyExternalId("company-uuid-1")).thenReturn(Arrays.asList(testEntity));
        when(bikeInspectionItemBikeUnitMapper.toDto(testEntity)).thenReturn(testDTO);

        List<BikeInspectionItemBikeUnitDTO> result = bikeInspectionItemBikeUnitService.getByCompanyExternalId("company-uuid-1");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bikeInspectionItemBikeUnitRepository, times(1)).findByCompanyExternalId("company-uuid-1");
    }

    @Test
    void create_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeInspectionItemBikeUnitMapper.toEntity(testDTO)).thenReturn(testEntity);
        when(bikeInspectionItemBikeUnitRepository.save(any(BikeInspectionItemBikeUnit.class))).thenReturn(testEntity);
        when(bikeInspectionItemBikeUnitMapper.toDto(testEntity)).thenReturn(testDTO);

        BikeInspectionItemBikeUnitDTO result = bikeInspectionItemBikeUnitService.create(testDTO);

        assertNotNull(result);
        verify(bikeInspectionItemBikeUnitRepository, times(1)).save(any(BikeInspectionItemBikeUnit.class));
    }

    @Test
    void create_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> bikeInspectionItemBikeUnitService.create(testDTO));
        verify(bikeInspectionItemBikeUnitRepository, never()).save(any(BikeInspectionItemBikeUnit.class));
    }

    @Test
    void update_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeInspectionItemBikeUnitRepository.findById(1L)).thenReturn(Optional.of(testEntity));
        doNothing().when(bikeInspectionItemBikeUnitMapper).updateEntityFromDto(testDTO, testEntity);
        when(bikeInspectionItemBikeUnitRepository.save(any(BikeInspectionItemBikeUnit.class))).thenReturn(testEntity);
        when(bikeInspectionItemBikeUnitMapper.toDto(testEntity)).thenReturn(testDTO);

        BikeInspectionItemBikeUnitDTO result = bikeInspectionItemBikeUnitService.update(1L, testDTO);

        assertNotNull(result);
        verify(bikeInspectionItemBikeUnitRepository, times(1)).save(any(BikeInspectionItemBikeUnit.class));
    }

    @Test
    void update_NotFound() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeInspectionItemBikeUnitRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bikeInspectionItemBikeUnitService.update(999L, testDTO));
    }

    @Test
    void update_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> bikeInspectionItemBikeUnitService.update(1L, testDTO));
    }

    @Test
    void delete_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeInspectionItemBikeUnitRepository.findById(1L)).thenReturn(Optional.of(testEntity));
        doNothing().when(bikeInspectionItemBikeUnitRepository).delete(testEntity);

        bikeInspectionItemBikeUnitService.delete(1L);

        verify(bikeInspectionItemBikeUnitRepository, times(1)).delete(testEntity);
    }

    @Test
    void delete_NotFound() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeInspectionItemBikeUnitRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bikeInspectionItemBikeUnitService.delete(999L));
    }

    @Test
    void delete_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> bikeInspectionItemBikeUnitService.delete(1L));
    }
}
