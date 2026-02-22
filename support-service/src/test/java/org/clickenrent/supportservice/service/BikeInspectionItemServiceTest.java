package org.clickenrent.supportservice.service;

import org.clickenrent.supportservice.dto.BikeInspectionItemDTO;
import org.clickenrent.supportservice.entity.BikeInspection;
import org.clickenrent.supportservice.entity.BikeInspectionItem;
import org.clickenrent.supportservice.entity.BikeInspectionItemStatus;
import org.clickenrent.supportservice.exception.ResourceNotFoundException;
import org.clickenrent.supportservice.exception.UnauthorizedException;
import org.clickenrent.supportservice.mapper.BikeInspectionItemMapper;
import org.clickenrent.supportservice.repository.BikeInspectionItemRepository;
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
class BikeInspectionItemServiceTest {

    @Mock
    private BikeInspectionItemRepository bikeInspectionItemRepository;

    @Mock
    private BikeInspectionItemMapper bikeInspectionItemMapper;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private BikeInspectionItemService bikeInspectionItemService;

    private BikeInspectionItem testEntity;
    private BikeInspectionItemDTO testDTO;

    @BeforeEach
    void setUp() {
        BikeInspection inspection = BikeInspection.builder().id(1L).companyExternalId("company-uuid-1").build();
        BikeInspectionItemStatus itemStatus = BikeInspectionItemStatus.builder().id(1L).name("OK").build();
        testEntity = BikeInspectionItem.builder()
                .id(1L)
                .externalId("item-uuid-1")
                .bikeInspection(inspection)
                .bikeInspectionItemStatus(itemStatus)
                .bikeExternalId("bike-uuid-1")
                .companyExternalId("company-uuid-1")
                .comment("Check")
                .build();

        testDTO = BikeInspectionItemDTO.builder()
                .id(1L)
                .externalId("item-uuid-1")
                .bikeInspectionId(1L)
                .bikeExternalId("bike-uuid-1")
                .companyExternalId("company-uuid-1")
                .comment("Check")
                .bikeInspectionItemStatusId(1L)
                .bikeInspectionItemStatusName("OK")
                .errorCodeId(1L)
                .errorCodeName("E001")
                .build();
    }

    @Test
    void getAll_ReturnsAll() {
        when(bikeInspectionItemRepository.findAll()).thenReturn(Arrays.asList(testEntity));
        when(bikeInspectionItemMapper.toDto(testEntity)).thenReturn(testDTO);

        List<BikeInspectionItemDTO> result = bikeInspectionItemService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("item-uuid-1", result.get(0).getExternalId());
        verify(bikeInspectionItemRepository, times(1)).findAll();
    }

    @Test
    void getById_Success() {
        when(bikeInspectionItemRepository.findById(1L)).thenReturn(Optional.of(testEntity));
        when(bikeInspectionItemMapper.toDto(testEntity)).thenReturn(testDTO);

        BikeInspectionItemDTO result = bikeInspectionItemService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(bikeInspectionItemRepository, times(1)).findById(1L);
    }

    @Test
    void getById_NotFound() {
        when(bikeInspectionItemRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bikeInspectionItemService.getById(999L));
    }

    @Test
    void getByExternalId_Success() {
        when(bikeInspectionItemRepository.findByExternalId("item-uuid-1")).thenReturn(Optional.of(testEntity));
        when(bikeInspectionItemMapper.toDto(testEntity)).thenReturn(testDTO);

        BikeInspectionItemDTO result = bikeInspectionItemService.getByExternalId("item-uuid-1");

        assertNotNull(result);
        assertEquals("item-uuid-1", result.getExternalId());
    }

    @Test
    void getByExternalId_NotFound() {
        when(bikeInspectionItemRepository.findByExternalId("invalid")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bikeInspectionItemService.getByExternalId("invalid"));
    }

    @Test
    void getByBikeInspectionId_Success() {
        when(bikeInspectionItemRepository.findByBikeInspectionId(1L)).thenReturn(Arrays.asList(testEntity));
        when(bikeInspectionItemMapper.toDto(testEntity)).thenReturn(testDTO);

        List<BikeInspectionItemDTO> result = bikeInspectionItemService.getByBikeInspectionId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bikeInspectionItemRepository, times(1)).findByBikeInspectionId(1L);
    }

    @Test
    void getByBikeExternalId_Success() {
        when(bikeInspectionItemRepository.findByBikeExternalId("bike-uuid-1")).thenReturn(Arrays.asList(testEntity));
        when(bikeInspectionItemMapper.toDto(testEntity)).thenReturn(testDTO);

        List<BikeInspectionItemDTO> result = bikeInspectionItemService.getByBikeExternalId("bike-uuid-1");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bikeInspectionItemRepository, times(1)).findByBikeExternalId("bike-uuid-1");
    }

    @Test
    void getByCompanyExternalId_Success() {
        when(bikeInspectionItemRepository.findByCompanyExternalId("company-uuid-1")).thenReturn(Arrays.asList(testEntity));
        when(bikeInspectionItemMapper.toDto(testEntity)).thenReturn(testDTO);

        List<BikeInspectionItemDTO> result = bikeInspectionItemService.getByCompanyExternalId("company-uuid-1");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bikeInspectionItemRepository, times(1)).findByCompanyExternalId("company-uuid-1");
    }

    @Test
    void getByStatusId_Success() {
        when(bikeInspectionItemRepository.findByBikeInspectionItemStatusId(1L)).thenReturn(Arrays.asList(testEntity));
        when(bikeInspectionItemMapper.toDto(testEntity)).thenReturn(testDTO);

        List<BikeInspectionItemDTO> result = bikeInspectionItemService.getByStatusId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bikeInspectionItemRepository, times(1)).findByBikeInspectionItemStatusId(1L);
    }

    @Test
    void getByErrorCodeId_Success() {
        when(bikeInspectionItemRepository.findByErrorCodeId(1L)).thenReturn(Arrays.asList(testEntity));
        when(bikeInspectionItemMapper.toDto(testEntity)).thenReturn(testDTO);

        List<BikeInspectionItemDTO> result = bikeInspectionItemService.getByErrorCodeId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bikeInspectionItemRepository, times(1)).findByErrorCodeId(1L);
    }

    @Test
    void create_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeInspectionItemMapper.toEntity(testDTO)).thenReturn(testEntity);
        when(bikeInspectionItemRepository.save(any(BikeInspectionItem.class))).thenReturn(testEntity);
        when(bikeInspectionItemMapper.toDto(testEntity)).thenReturn(testDTO);

        BikeInspectionItemDTO result = bikeInspectionItemService.create(testDTO);

        assertNotNull(result);
        verify(bikeInspectionItemRepository, times(1)).save(any(BikeInspectionItem.class));
    }

    @Test
    void create_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> bikeInspectionItemService.create(testDTO));
        verify(bikeInspectionItemRepository, never()).save(any(BikeInspectionItem.class));
    }

    @Test
    void update_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeInspectionItemRepository.findById(1L)).thenReturn(Optional.of(testEntity));
        doNothing().when(bikeInspectionItemMapper).updateEntityFromDto(testDTO, testEntity);
        when(bikeInspectionItemRepository.save(any(BikeInspectionItem.class))).thenReturn(testEntity);
        when(bikeInspectionItemMapper.toDto(testEntity)).thenReturn(testDTO);

        BikeInspectionItemDTO result = bikeInspectionItemService.update(1L, testDTO);

        assertNotNull(result);
        verify(bikeInspectionItemRepository, times(1)).save(any(BikeInspectionItem.class));
    }

    @Test
    void update_NotFound() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeInspectionItemRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bikeInspectionItemService.update(999L, testDTO));
    }

    @Test
    void update_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> bikeInspectionItemService.update(1L, testDTO));
    }

    @Test
    void delete_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeInspectionItemRepository.findById(1L)).thenReturn(Optional.of(testEntity));
        doNothing().when(bikeInspectionItemRepository).delete(testEntity);

        bikeInspectionItemService.delete(1L);

        verify(bikeInspectionItemRepository, times(1)).delete(testEntity);
    }

    @Test
    void delete_NotFound() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeInspectionItemRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bikeInspectionItemService.delete(999L));
    }

    @Test
    void delete_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> bikeInspectionItemService.delete(1L));
    }
}
