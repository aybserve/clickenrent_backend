package org.clickenrent.supportservice.service;

import org.clickenrent.supportservice.dto.SupportRequestStatusDTO;
import org.clickenrent.supportservice.entity.SupportRequestStatus;
import org.clickenrent.supportservice.exception.ResourceNotFoundException;
import org.clickenrent.supportservice.exception.UnauthorizedException;
import org.clickenrent.supportservice.mapper.SupportRequestStatusMapper;
import org.clickenrent.supportservice.repository.SupportRequestStatusRepository;
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
class SupportRequestStatusServiceTest {

    @Mock
    private SupportRequestStatusRepository supportRequestStatusRepository;

    @Mock
    private SupportRequestStatusMapper supportRequestStatusMapper;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private SupportRequestStatusService supportRequestStatusService;

    private SupportRequestStatus testStatus;
    private SupportRequestStatusDTO testStatusDTO;

    @BeforeEach
    void setUp() {
        testStatus = SupportRequestStatus.builder()
                .id(1L)
                .name("OPEN")
                .build();

        testStatusDTO = SupportRequestStatusDTO.builder()
                .id(1L)
                .name("OPEN")
                .build();
    }

    @Test
    void getAll_ReturnsAllStatuses() {
        when(supportRequestStatusRepository.findAll()).thenReturn(Arrays.asList(testStatus));
        when(supportRequestStatusMapper.toDto(testStatus)).thenReturn(testStatusDTO);

        List<SupportRequestStatusDTO> result = supportRequestStatusService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("OPEN", result.get(0).getName());
        verify(supportRequestStatusRepository, times(1)).findAll();
    }

    @Test
    void getById_Success() {
        when(supportRequestStatusRepository.findById(1L)).thenReturn(Optional.of(testStatus));
        when(supportRequestStatusMapper.toDto(testStatus)).thenReturn(testStatusDTO);

        SupportRequestStatusDTO result = supportRequestStatusService.getById(1L);

        assertNotNull(result);
        assertEquals("OPEN", result.getName());
        verify(supportRequestStatusRepository, times(1)).findById(1L);
    }

    @Test
    void getById_NotFound() {
        when(supportRequestStatusRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> supportRequestStatusService.getById(999L));
    }

    @Test
    void getByName_Success() {
        when(supportRequestStatusRepository.findByName("OPEN")).thenReturn(Optional.of(testStatus));
        when(supportRequestStatusMapper.toDto(testStatus)).thenReturn(testStatusDTO);

        SupportRequestStatusDTO result = supportRequestStatusService.getByName("OPEN");

        assertNotNull(result);
        assertEquals("OPEN", result.getName());
        verify(supportRequestStatusRepository, times(1)).findByName("OPEN");
    }

    @Test
    void getByName_NotFound() {
        when(supportRequestStatusRepository.findByName("INVALID")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> supportRequestStatusService.getByName("INVALID"));
    }

    @Test
    void create_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(supportRequestStatusMapper.toEntity(testStatusDTO)).thenReturn(testStatus);
        when(supportRequestStatusRepository.save(any(SupportRequestStatus.class))).thenReturn(testStatus);
        when(supportRequestStatusMapper.toDto(testStatus)).thenReturn(testStatusDTO);

        SupportRequestStatusDTO result = supportRequestStatusService.create(testStatusDTO);

        assertNotNull(result);
        assertEquals("OPEN", result.getName());
        verify(supportRequestStatusRepository, times(1)).save(any(SupportRequestStatus.class));
    }

    @Test
    void create_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> supportRequestStatusService.create(testStatusDTO));
        verify(supportRequestStatusRepository, never()).save(any(SupportRequestStatus.class));
    }

    @Test
    void update_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(supportRequestStatusRepository.findById(1L)).thenReturn(Optional.of(testStatus));
        doNothing().when(supportRequestStatusMapper).updateEntityFromDto(testStatusDTO, testStatus);
        when(supportRequestStatusRepository.save(any(SupportRequestStatus.class))).thenReturn(testStatus);
        when(supportRequestStatusMapper.toDto(testStatus)).thenReturn(testStatusDTO);

        SupportRequestStatusDTO result = supportRequestStatusService.update(1L, testStatusDTO);

        assertNotNull(result);
        verify(supportRequestStatusRepository, times(1)).save(any(SupportRequestStatus.class));
    }

    @Test
    void update_NotFound() {
        when(securityService.isAdmin()).thenReturn(true);
        when(supportRequestStatusRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> supportRequestStatusService.update(999L, testStatusDTO));
    }

    @Test
    void update_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> supportRequestStatusService.update(1L, testStatusDTO));
    }

    @Test
    void delete_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(supportRequestStatusRepository.findById(1L)).thenReturn(Optional.of(testStatus));
        doNothing().when(supportRequestStatusRepository).delete(testStatus);

        supportRequestStatusService.delete(1L);

        verify(supportRequestStatusRepository, times(1)).delete(testStatus);
    }

    @Test
    void delete_NotFound() {
        when(securityService.isAdmin()).thenReturn(true);
        when(supportRequestStatusRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> supportRequestStatusService.delete(999L));
    }

    @Test
    void delete_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> supportRequestStatusService.delete(1L));
    }
}


