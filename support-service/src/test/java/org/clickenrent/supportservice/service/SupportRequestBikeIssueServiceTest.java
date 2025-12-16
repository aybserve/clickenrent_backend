package org.clickenrent.supportservice.service;

import org.clickenrent.supportservice.dto.SupportRequestBikeIssueDTO;
import org.clickenrent.supportservice.entity.BikeIssue;
import org.clickenrent.supportservice.entity.SupportRequest;
import org.clickenrent.supportservice.entity.SupportRequestBikeIssue;
import org.clickenrent.supportservice.exception.ResourceNotFoundException;
import org.clickenrent.supportservice.exception.UnauthorizedException;
import org.clickenrent.supportservice.mapper.SupportRequestBikeIssueMapper;
import org.clickenrent.supportservice.repository.SupportRequestBikeIssueRepository;
import org.clickenrent.supportservice.repository.SupportRequestRepository;
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
class SupportRequestBikeIssueServiceTest {

    @Mock
    private SupportRequestBikeIssueRepository supportRequestBikeIssueRepository;

    @Mock
    private SupportRequestBikeIssueMapper supportRequestBikeIssueMapper;

    @Mock
    private SupportRequestRepository supportRequestRepository;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private SupportRequestBikeIssueService supportRequestBikeIssueService;

    private SupportRequestBikeIssue testJunction;
    private SupportRequestBikeIssueDTO testJunctionDTO;
    private SupportRequest testRequest;
    private BikeIssue testBikeIssue;

    @BeforeEach
    void setUp() {
        testRequest = SupportRequest.builder()
                .id(1L)
                .userId(1L)
                .build();

        testBikeIssue = BikeIssue.builder()
                .id(1L)
                .name("Battery Issues")
                .build();

        testJunction = SupportRequestBikeIssue.builder()
                .id(1L)
                .supportRequest(testRequest)
                .bikeIssue(testBikeIssue)
                .build();

        testJunctionDTO = SupportRequestBikeIssueDTO.builder()
                .id(1L)
                .supportRequestId(1L)
                .build();
    }

    @Test
    void getAll_AsAdmin_ReturnsAllJunctions() {
        when(securityService.isAdmin()).thenReturn(true);
        when(supportRequestBikeIssueRepository.findAll()).thenReturn(Arrays.asList(testJunction));
        when(supportRequestBikeIssueMapper.toDto(testJunction)).thenReturn(testJunctionDTO);

        List<SupportRequestBikeIssueDTO> result = supportRequestBikeIssueService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(supportRequestBikeIssueRepository, times(1)).findAll();
    }

    @Test
    void getAll_AsNonAdmin_ThrowsUnauthorized() {
        when(securityService.isAdmin()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> supportRequestBikeIssueService.getAll());
    }

    @Test
    void getById_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(supportRequestBikeIssueRepository.findById(1L)).thenReturn(Optional.of(testJunction));
        when(supportRequestBikeIssueMapper.toDto(testJunction)).thenReturn(testJunctionDTO);

        SupportRequestBikeIssueDTO result = supportRequestBikeIssueService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getSupportRequestId());
        verify(supportRequestBikeIssueRepository, times(1)).findById(1L);
    }

    @Test
    void getById_NotFound() {
        when(supportRequestBikeIssueRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> supportRequestBikeIssueService.getById(999L));
    }

    @Test
    void getById_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.hasAccessToUser(1L)).thenReturn(false);
        when(supportRequestBikeIssueRepository.findById(1L)).thenReturn(Optional.of(testJunction));

        assertThrows(UnauthorizedException.class, () -> supportRequestBikeIssueService.getById(1L));
    }

    @Test
    void getBySupportRequestId_Success() {
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.hasAccessToUser(1L)).thenReturn(true);
        when(supportRequestRepository.findById(1L)).thenReturn(Optional.of(testRequest));
        when(supportRequestBikeIssueRepository.findBySupportRequestId(1L)).thenReturn(Arrays.asList(testJunction));
        when(supportRequestBikeIssueMapper.toDto(testJunction)).thenReturn(testJunctionDTO);

        List<SupportRequestBikeIssueDTO> result = supportRequestBikeIssueService.getBySupportRequestId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(supportRequestBikeIssueRepository, times(1)).findBySupportRequestId(1L);
    }

    @Test
    void getBySupportRequestId_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.hasAccessToUser(1L)).thenReturn(false);
        when(supportRequestRepository.findById(1L)).thenReturn(Optional.of(testRequest));

        assertThrows(UnauthorizedException.class, () -> supportRequestBikeIssueService.getBySupportRequestId(1L));
    }

    @Test
    void getByBikeIssueId_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(supportRequestBikeIssueRepository.findByBikeIssueId(1L)).thenReturn(Arrays.asList(testJunction));
        when(supportRequestBikeIssueMapper.toDto(testJunction)).thenReturn(testJunctionDTO);

        List<SupportRequestBikeIssueDTO> result = supportRequestBikeIssueService.getByBikeIssueId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(supportRequestBikeIssueRepository, times(1)).findByBikeIssueId(1L);
    }

    @Test
    void getByBikeIssueId_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> supportRequestBikeIssueService.getByBikeIssueId(1L));
    }

    @Test
    void create_Success() {
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.hasAccessToUser(1L)).thenReturn(true);
        when(supportRequestRepository.findById(1L)).thenReturn(Optional.of(testRequest));
        when(supportRequestBikeIssueMapper.toEntity(testJunctionDTO)).thenReturn(testJunction);
        when(supportRequestBikeIssueRepository.save(any(SupportRequestBikeIssue.class))).thenReturn(testJunction);
        when(supportRequestBikeIssueMapper.toDto(testJunction)).thenReturn(testJunctionDTO);

        SupportRequestBikeIssueDTO result = supportRequestBikeIssueService.create(testJunctionDTO);

        assertNotNull(result);
        verify(supportRequestBikeIssueRepository, times(1)).save(any(SupportRequestBikeIssue.class));
    }

    @Test
    void create_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.hasAccessToUser(1L)).thenReturn(false);
        when(supportRequestRepository.findById(1L)).thenReturn(Optional.of(testRequest));

        assertThrows(UnauthorizedException.class, () -> supportRequestBikeIssueService.create(testJunctionDTO));
    }

    @Test
    void delete_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(supportRequestBikeIssueRepository.findById(1L)).thenReturn(Optional.of(testJunction));
        doNothing().when(supportRequestBikeIssueRepository).delete(testJunction);

        supportRequestBikeIssueService.delete(1L);

        verify(supportRequestBikeIssueRepository, times(1)).delete(testJunction);
    }

    @Test
    void delete_NotFound() {
        when(supportRequestBikeIssueRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> supportRequestBikeIssueService.delete(999L));
    }

    @Test
    void delete_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.hasAccessToUser(1L)).thenReturn(false);
        when(supportRequestBikeIssueRepository.findById(1L)).thenReturn(Optional.of(testJunction));

        assertThrows(UnauthorizedException.class, () -> supportRequestBikeIssueService.delete(1L));
    }
}
