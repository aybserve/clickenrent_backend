package org.clickenrent.supportservice.service;

import org.clickenrent.supportservice.dto.SupportRequestDTO;
import org.clickenrent.supportservice.entity.SupportRequest;
import org.clickenrent.supportservice.entity.SupportRequestStatus;
import org.clickenrent.supportservice.exception.ResourceNotFoundException;
import org.clickenrent.supportservice.exception.UnauthorizedException;
import org.clickenrent.supportservice.mapper.SupportRequestMapper;
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
class SupportRequestServiceTest {

    @Mock
    private SupportRequestRepository supportRequestRepository;

    @Mock
    private SupportRequestMapper supportRequestMapper;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private SupportRequestService supportRequestService;

    private SupportRequest testRequest;
    private SupportRequestDTO testRequestDTO;
    private SupportRequestStatus testStatus;

    @BeforeEach
    void setUp() {
        testStatus = SupportRequestStatus.builder()
                .id(1L)
                .name("OPEN")
                .build();

        testRequest = SupportRequest.builder()
                .id(1L)
                .externalId("550e8400-e29b-41d4-a716-446655440401")
                .userId(1L)
                .bikeId(201L)
                .isNearLocation(true)
                .photoUrl("https://example.com/photo.jpg")
                .supportRequestStatus(testStatus)
                .build();

        testRequestDTO = SupportRequestDTO.builder()
                .id(1L)
                .externalId("550e8400-e29b-41d4-a716-446655440401")
                .userId(1L)
                .bikeId(201L)
                .isNearLocation(true)
                .photoUrl("https://example.com/photo.jpg")
                .build();
    }

    @Test
    void getAll_AsAdmin_ReturnsAllRequests() {
        when(securityService.isAdmin()).thenReturn(true);
        when(supportRequestRepository.findAll()).thenReturn(Arrays.asList(testRequest));
        when(supportRequestMapper.toDto(testRequest)).thenReturn(testRequestDTO);

        List<SupportRequestDTO> result = supportRequestService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(supportRequestRepository, times(1)).findAll();
    }

    @Test
    void getAll_AsNonAdmin_ReturnsUserRequests() {
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.getCurrentUserId()).thenReturn(1L);
        when(supportRequestRepository.findByUserId(1L)).thenReturn(Arrays.asList(testRequest));
        when(supportRequestMapper.toDto(testRequest)).thenReturn(testRequestDTO);

        List<SupportRequestDTO> result = supportRequestService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(supportRequestRepository, times(1)).findByUserId(1L);
    }

    @Test
    void getById_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(supportRequestRepository.findById(1L)).thenReturn(Optional.of(testRequest));
        when(supportRequestMapper.toDto(testRequest)).thenReturn(testRequestDTO);

        SupportRequestDTO result = supportRequestService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        verify(supportRequestRepository, times(1)).findById(1L);
    }

    @Test
    void getById_NotFound() {
        when(supportRequestRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> supportRequestService.getById(999L));
    }

    @Test
    void getById_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.hasAccessToUser(1L)).thenReturn(false);
        when(supportRequestRepository.findById(1L)).thenReturn(Optional.of(testRequest));

        assertThrows(UnauthorizedException.class, () -> supportRequestService.getById(1L));
    }

    @Test
    void getByExternalId_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(supportRequestRepository.findByExternalId("550e8400-e29b-41d4-a716-446655440401"))
                .thenReturn(Optional.of(testRequest));
        when(supportRequestMapper.toDto(testRequest)).thenReturn(testRequestDTO);

        SupportRequestDTO result = supportRequestService.getByExternalId("550e8400-e29b-41d4-a716-446655440401");

        assertNotNull(result);
        assertEquals("550e8400-e29b-41d4-a716-446655440401", result.getExternalId());
    }

    @Test
    void getByExternalId_NotFound() {
        when(supportRequestRepository.findByExternalId("invalid")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> supportRequestService.getByExternalId("invalid"));
    }

    @Test
    void getByUserId_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(supportRequestRepository.findByUserId(1L)).thenReturn(Arrays.asList(testRequest));
        when(supportRequestMapper.toDto(testRequest)).thenReturn(testRequestDTO);

        List<SupportRequestDTO> result = supportRequestService.getByUserId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(supportRequestRepository, times(1)).findByUserId(1L);
    }

    @Test
    void getByUserId_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.hasAccessToUser(2L)).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> supportRequestService.getByUserId(2L));
    }

    @Test
    void create_Success() {
        when(securityService.getCurrentUserId()).thenReturn(1L);
        when(supportRequestMapper.toEntity(testRequestDTO)).thenReturn(testRequest);
        when(supportRequestRepository.save(any(SupportRequest.class))).thenReturn(testRequest);
        when(supportRequestMapper.toDto(testRequest)).thenReturn(testRequestDTO);

        SupportRequestDTO result = supportRequestService.create(testRequestDTO);

        assertNotNull(result);
        verify(supportRequestRepository, times(1)).save(any(SupportRequest.class));
    }

    @Test
    void create_WithoutUserId_SetsCurrentUser() {
        SupportRequestDTO dtoWithoutUserId = SupportRequestDTO.builder()
                .bikeId(201L)
                .isNearLocation(true)
                .build();
        
        when(securityService.getCurrentUserId()).thenReturn(1L);
        when(supportRequestMapper.toEntity(any(SupportRequestDTO.class))).thenReturn(testRequest);
        when(supportRequestRepository.save(any(SupportRequest.class))).thenReturn(testRequest);
        when(supportRequestMapper.toDto(testRequest)).thenReturn(testRequestDTO);

        SupportRequestDTO result = supportRequestService.create(dtoWithoutUserId);

        assertNotNull(result);
        assertEquals(1L, dtoWithoutUserId.getUserId());
    }

    @Test
    void create_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.getCurrentUserId()).thenReturn(1L);
        testRequestDTO.setUserId(2L);

        assertThrows(UnauthorizedException.class, () -> supportRequestService.create(testRequestDTO));
    }

    @Test
    void update_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(supportRequestRepository.findById(1L)).thenReturn(Optional.of(testRequest));
        doNothing().when(supportRequestMapper).updateEntityFromDto(testRequestDTO, testRequest);
        when(supportRequestRepository.save(any(SupportRequest.class))).thenReturn(testRequest);
        when(supportRequestMapper.toDto(testRequest)).thenReturn(testRequestDTO);

        SupportRequestDTO result = supportRequestService.update(1L, testRequestDTO);

        assertNotNull(result);
        verify(supportRequestRepository, times(1)).save(any(SupportRequest.class));
    }

    @Test
    void update_NotFound() {
        when(supportRequestRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> supportRequestService.update(999L, testRequestDTO));
    }

    @Test
    void update_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.hasAccessToUser(1L)).thenReturn(false);
        when(supportRequestRepository.findById(1L)).thenReturn(Optional.of(testRequest));

        assertThrows(UnauthorizedException.class, () -> supportRequestService.update(1L, testRequestDTO));
    }

    @Test
    void delete_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(supportRequestRepository.findById(1L)).thenReturn(Optional.of(testRequest));
        doNothing().when(supportRequestRepository).delete(testRequest);

        supportRequestService.delete(1L);

        verify(supportRequestRepository, times(1)).delete(testRequest);
    }

    @Test
    void delete_NotFound() {
        when(supportRequestRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> supportRequestService.delete(999L));
    }

    @Test
    void delete_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.hasAccessToUser(1L)).thenReturn(false);
        when(supportRequestRepository.findById(1L)).thenReturn(Optional.of(testRequest));

        assertThrows(UnauthorizedException.class, () -> supportRequestService.delete(1L));
    }
}

