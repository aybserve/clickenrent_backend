package org.clickenrent.supportservice.service;

import org.clickenrent.supportservice.dto.SupportRequestGuideItemDTO;
import org.clickenrent.supportservice.entity.BikeIssue;
import org.clickenrent.supportservice.entity.SupportRequestGuideItem;
import org.clickenrent.supportservice.entity.SupportRequestStatus;
import org.clickenrent.supportservice.exception.ResourceNotFoundException;
import org.clickenrent.supportservice.exception.UnauthorizedException;
import org.clickenrent.supportservice.mapper.SupportRequestGuideItemMapper;
import org.clickenrent.supportservice.repository.SupportRequestGuideItemRepository;
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
class SupportRequestGuideItemServiceTest {

    @Mock
    private SupportRequestGuideItemRepository supportRequestGuideItemRepository;

    @Mock
    private SupportRequestGuideItemMapper supportRequestGuideItemMapper;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private SupportRequestGuideItemService supportRequestGuideItemService;

    private SupportRequestGuideItem testGuideItem;
    private SupportRequestGuideItemDTO testGuideItemDTO;
    private BikeIssue testBikeIssue;
    private SupportRequestStatus testStatus;

    @BeforeEach
    void setUp() {
        testBikeIssue = BikeIssue.builder()
                .id(1L)
                .name("Battery Issues")
                .build();

        testStatus = SupportRequestStatus.builder()
                .id(1L)
                .name("OPEN")
                .build();

        testGuideItem = SupportRequestGuideItem.builder()
                .id(1L)
                .itemIndex(1)
                .description("Check if battery is properly connected")
                .bikeIssue(testBikeIssue)
                .supportRequestStatus(testStatus)
                .build();

        testGuideItemDTO = SupportRequestGuideItemDTO.builder()
                .id(1L)
                .itemIndex(1)
                .description("Check if battery is properly connected")
                .build();
    }

    @Test
    void getAll_ReturnsAllGuideItems() {
        when(supportRequestGuideItemRepository.findAll()).thenReturn(Arrays.asList(testGuideItem));
        when(supportRequestGuideItemMapper.toDto(testGuideItem)).thenReturn(testGuideItemDTO);

        List<SupportRequestGuideItemDTO> result = supportRequestGuideItemService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Check if battery is properly connected", result.get(0).getDescription());
        verify(supportRequestGuideItemRepository, times(1)).findAll();
    }

    @Test
    void getById_Success() {
        when(supportRequestGuideItemRepository.findById(1L)).thenReturn(Optional.of(testGuideItem));
        when(supportRequestGuideItemMapper.toDto(testGuideItem)).thenReturn(testGuideItemDTO);

        SupportRequestGuideItemDTO result = supportRequestGuideItemService.getById(1L);

        assertNotNull(result);
        assertEquals(1, result.getItemIndex());
        verify(supportRequestGuideItemRepository, times(1)).findById(1L);
    }

    @Test
    void getById_NotFound() {
        when(supportRequestGuideItemRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> supportRequestGuideItemService.getById(999L));
    }

    @Test
    void getByBikeIssueId_Success() {
        when(supportRequestGuideItemRepository.findByBikeIssueId(1L)).thenReturn(Arrays.asList(testGuideItem));
        when(supportRequestGuideItemMapper.toDto(testGuideItem)).thenReturn(testGuideItemDTO);

        List<SupportRequestGuideItemDTO> result = supportRequestGuideItemService.getByBikeIssueId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(supportRequestGuideItemRepository, times(1)).findByBikeIssueId(1L);
    }

    @Test
    void getByBikeIssueAndStatus_Success() {
        when(supportRequestGuideItemRepository
                .findByBikeIssueIdAndSupportRequestStatusIdOrderByItemIndexAsc(1L, 1L))
                .thenReturn(Arrays.asList(testGuideItem));
        when(supportRequestGuideItemMapper.toDto(testGuideItem)).thenReturn(testGuideItemDTO);

        List<SupportRequestGuideItemDTO> result = supportRequestGuideItemService.getByBikeIssueAndStatus(1L, 1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(supportRequestGuideItemRepository, times(1))
                .findByBikeIssueIdAndSupportRequestStatusIdOrderByItemIndexAsc(1L, 1L);
    }

    @Test
    void create_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(supportRequestGuideItemMapper.toEntity(testGuideItemDTO)).thenReturn(testGuideItem);
        when(supportRequestGuideItemRepository.save(any(SupportRequestGuideItem.class))).thenReturn(testGuideItem);
        when(supportRequestGuideItemMapper.toDto(testGuideItem)).thenReturn(testGuideItemDTO);

        SupportRequestGuideItemDTO result = supportRequestGuideItemService.create(testGuideItemDTO);

        assertNotNull(result);
        assertEquals(1, result.getItemIndex());
        verify(supportRequestGuideItemRepository, times(1)).save(any(SupportRequestGuideItem.class));
    }

    @Test
    void create_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> supportRequestGuideItemService.create(testGuideItemDTO));
        verify(supportRequestGuideItemRepository, never()).save(any(SupportRequestGuideItem.class));
    }

    @Test
    void update_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(supportRequestGuideItemRepository.findById(1L)).thenReturn(Optional.of(testGuideItem));
        doNothing().when(supportRequestGuideItemMapper).updateEntityFromDto(testGuideItemDTO, testGuideItem);
        when(supportRequestGuideItemRepository.save(any(SupportRequestGuideItem.class))).thenReturn(testGuideItem);
        when(supportRequestGuideItemMapper.toDto(testGuideItem)).thenReturn(testGuideItemDTO);

        SupportRequestGuideItemDTO result = supportRequestGuideItemService.update(1L, testGuideItemDTO);

        assertNotNull(result);
        verify(supportRequestGuideItemRepository, times(1)).save(any(SupportRequestGuideItem.class));
    }

    @Test
    void update_NotFound() {
        when(securityService.isAdmin()).thenReturn(true);
        when(supportRequestGuideItemRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> supportRequestGuideItemService.update(999L, testGuideItemDTO));
    }

    @Test
    void update_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> supportRequestGuideItemService.update(1L, testGuideItemDTO));
    }

    @Test
    void delete_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(supportRequestGuideItemRepository.findById(1L)).thenReturn(Optional.of(testGuideItem));
        doNothing().when(supportRequestGuideItemRepository).delete(testGuideItem);

        supportRequestGuideItemService.delete(1L);

        verify(supportRequestGuideItemRepository, times(1)).delete(testGuideItem);
    }

    @Test
    void delete_NotFound() {
        when(securityService.isAdmin()).thenReturn(true);
        when(supportRequestGuideItemRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> supportRequestGuideItemService.delete(999L));
    }

    @Test
    void delete_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> supportRequestGuideItemService.delete(1L));
    }
}







