package org.clickenrent.supportservice.service;

import org.clickenrent.supportservice.dto.BikeInspectionItemBikeIssueDTO;
import org.clickenrent.supportservice.entity.BikeInspection;
import org.clickenrent.supportservice.entity.BikeInspectionItem;
import org.clickenrent.supportservice.entity.BikeInspectionItemBikeIssue;
import org.clickenrent.supportservice.entity.BikeInspectionItemStatus;
import org.clickenrent.supportservice.entity.BikeIssue;
import org.clickenrent.supportservice.exception.ResourceNotFoundException;
import org.clickenrent.supportservice.exception.UnauthorizedException;
import org.clickenrent.supportservice.mapper.BikeInspectionItemBikeIssueMapper;
import org.clickenrent.supportservice.repository.BikeInspectionItemBikeIssueRepository;
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
class BikeInspectionItemBikeIssueServiceTest {

    @Mock
    private BikeInspectionItemBikeIssueRepository bikeInspectionItemBikeIssueRepository;

    @Mock
    private BikeInspectionItemBikeIssueMapper bikeInspectionItemBikeIssueMapper;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private BikeInspectionItemBikeIssueService bikeInspectionItemBikeIssueService;

    private BikeInspectionItemBikeIssue testEntity;
    private BikeInspectionItemBikeIssueDTO testDTO;

    @BeforeEach
    void setUp() {
        BikeInspection inspection = BikeInspection.builder().id(1L).companyExternalId("company-uuid-1").build();
        BikeInspectionItemStatus itemStatus = BikeInspectionItemStatus.builder().id(1L).name("OK").build();
        BikeInspectionItem item = BikeInspectionItem.builder().id(1L).externalId("i1").companyExternalId("company-uuid-1").bikeInspection(inspection).bikeInspectionItemStatus(itemStatus).build();
        BikeIssue issue = BikeIssue.builder().id(1L).name("Battery Issue").build();
        testEntity = BikeInspectionItemBikeIssue.builder()
                .id(1L)
                .externalId("link-uuid-1")
                .bikeInspectionItem(item)
                .bikeIssue(issue)
                .companyExternalId("company-uuid-1")
                .build();

        testDTO = BikeInspectionItemBikeIssueDTO.builder()
                .id(1L)
                .externalId("link-uuid-1")
                .bikeInspectionItemId(1L)
                .bikeIssueId(1L)
                .bikeIssueName("Battery Issue")
                .companyExternalId("company-uuid-1")
                .build();
    }

    @Test
    void getAll_ReturnsAll() {
        when(bikeInspectionItemBikeIssueRepository.findAll()).thenReturn(Arrays.asList(testEntity));
        when(bikeInspectionItemBikeIssueMapper.toDto(testEntity)).thenReturn(testDTO);

        List<BikeInspectionItemBikeIssueDTO> result = bikeInspectionItemBikeIssueService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("link-uuid-1", result.get(0).getExternalId());
        verify(bikeInspectionItemBikeIssueRepository, times(1)).findAll();
    }

    @Test
    void getById_Success() {
        when(bikeInspectionItemBikeIssueRepository.findById(1L)).thenReturn(Optional.of(testEntity));
        when(bikeInspectionItemBikeIssueMapper.toDto(testEntity)).thenReturn(testDTO);

        BikeInspectionItemBikeIssueDTO result = bikeInspectionItemBikeIssueService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(bikeInspectionItemBikeIssueRepository, times(1)).findById(1L);
    }

    @Test
    void getById_NotFound() {
        when(bikeInspectionItemBikeIssueRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bikeInspectionItemBikeIssueService.getById(999L));
    }

    @Test
    void getByExternalId_Success() {
        when(bikeInspectionItemBikeIssueRepository.findByExternalId("link-uuid-1")).thenReturn(Optional.of(testEntity));
        when(bikeInspectionItemBikeIssueMapper.toDto(testEntity)).thenReturn(testDTO);

        BikeInspectionItemBikeIssueDTO result = bikeInspectionItemBikeIssueService.getByExternalId("link-uuid-1");

        assertNotNull(result);
        assertEquals("link-uuid-1", result.getExternalId());
    }

    @Test
    void getByExternalId_NotFound() {
        when(bikeInspectionItemBikeIssueRepository.findByExternalId("invalid")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bikeInspectionItemBikeIssueService.getByExternalId("invalid"));
    }

    @Test
    void getByBikeInspectionItemId_Success() {
        when(bikeInspectionItemBikeIssueRepository.findByBikeInspectionItemId(1L)).thenReturn(Arrays.asList(testEntity));
        when(bikeInspectionItemBikeIssueMapper.toDto(testEntity)).thenReturn(testDTO);

        List<BikeInspectionItemBikeIssueDTO> result = bikeInspectionItemBikeIssueService.getByBikeInspectionItemId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bikeInspectionItemBikeIssueRepository, times(1)).findByBikeInspectionItemId(1L);
    }

    @Test
    void getByBikeIssueId_Success() {
        when(bikeInspectionItemBikeIssueRepository.findByBikeIssueId(1L)).thenReturn(Arrays.asList(testEntity));
        when(bikeInspectionItemBikeIssueMapper.toDto(testEntity)).thenReturn(testDTO);

        List<BikeInspectionItemBikeIssueDTO> result = bikeInspectionItemBikeIssueService.getByBikeIssueId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bikeInspectionItemBikeIssueRepository, times(1)).findByBikeIssueId(1L);
    }

    @Test
    void getByCompanyExternalId_Success() {
        when(bikeInspectionItemBikeIssueRepository.findByCompanyExternalId("company-uuid-1")).thenReturn(Arrays.asList(testEntity));
        when(bikeInspectionItemBikeIssueMapper.toDto(testEntity)).thenReturn(testDTO);

        List<BikeInspectionItemBikeIssueDTO> result = bikeInspectionItemBikeIssueService.getByCompanyExternalId("company-uuid-1");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bikeInspectionItemBikeIssueRepository, times(1)).findByCompanyExternalId("company-uuid-1");
    }

    @Test
    void create_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeInspectionItemBikeIssueMapper.toEntity(testDTO)).thenReturn(testEntity);
        when(bikeInspectionItemBikeIssueRepository.save(any(BikeInspectionItemBikeIssue.class))).thenReturn(testEntity);
        when(bikeInspectionItemBikeIssueMapper.toDto(testEntity)).thenReturn(testDTO);

        BikeInspectionItemBikeIssueDTO result = bikeInspectionItemBikeIssueService.create(testDTO);

        assertNotNull(result);
        verify(bikeInspectionItemBikeIssueRepository, times(1)).save(any(BikeInspectionItemBikeIssue.class));
    }

    @Test
    void create_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> bikeInspectionItemBikeIssueService.create(testDTO));
        verify(bikeInspectionItemBikeIssueRepository, never()).save(any(BikeInspectionItemBikeIssue.class));
    }

    @Test
    void update_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeInspectionItemBikeIssueRepository.findById(1L)).thenReturn(Optional.of(testEntity));
        doNothing().when(bikeInspectionItemBikeIssueMapper).updateEntityFromDto(testDTO, testEntity);
        when(bikeInspectionItemBikeIssueRepository.save(any(BikeInspectionItemBikeIssue.class))).thenReturn(testEntity);
        when(bikeInspectionItemBikeIssueMapper.toDto(testEntity)).thenReturn(testDTO);

        BikeInspectionItemBikeIssueDTO result = bikeInspectionItemBikeIssueService.update(1L, testDTO);

        assertNotNull(result);
        verify(bikeInspectionItemBikeIssueRepository, times(1)).save(any(BikeInspectionItemBikeIssue.class));
    }

    @Test
    void update_NotFound() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeInspectionItemBikeIssueRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bikeInspectionItemBikeIssueService.update(999L, testDTO));
    }

    @Test
    void update_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> bikeInspectionItemBikeIssueService.update(1L, testDTO));
    }

    @Test
    void delete_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeInspectionItemBikeIssueRepository.findById(1L)).thenReturn(Optional.of(testEntity));
        doNothing().when(bikeInspectionItemBikeIssueRepository).delete(testEntity);

        bikeInspectionItemBikeIssueService.delete(1L);

        verify(bikeInspectionItemBikeIssueRepository, times(1)).delete(testEntity);
    }

    @Test
    void delete_NotFound() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeInspectionItemBikeIssueRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bikeInspectionItemBikeIssueService.delete(999L));
    }

    @Test
    void delete_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> bikeInspectionItemBikeIssueService.delete(1L));
    }
}
