package org.clickenrent.rentalservice.service;

import org.clickenrent.rentalservice.dto.RentalStatusDTO;
import org.clickenrent.rentalservice.entity.RentalStatus;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.mapper.RentalStatusMapper;
import org.clickenrent.rentalservice.repository.RentalStatusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RentalStatusServiceTest {

    @Mock
    private RentalStatusRepository rentalStatusRepository;

    @Mock
    private RentalStatusMapper rentalStatusMapper;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private RentalStatusService rentalStatusService;

    private RentalStatus testStatus;
    private RentalStatusDTO testStatusDTO;

    @BeforeEach
    void setUp() {
        testStatus = RentalStatus.builder()
        .id(1L)
        .name("Active")
        .build();

        testStatusDTO = RentalStatusDTO.builder()
        .id(1L)
        .name("Active")
        .build();
    }

    @Test
    void getAllStatuses_ReturnsAllStatuses() {
        when(rentalStatusRepository.findAll()).thenReturn(Arrays.asList(testStatus));
        when(rentalStatusMapper.toDto(testStatus)).thenReturn(testStatusDTO);

        var result = rentalStatusService.getAllStatuses();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(rentalStatusRepository, times(1)).findAll();
    }

    @Test
    void getStatusById_Success() {
        when(rentalStatusRepository.findById(1L)).thenReturn(Optional.of(testStatus));
        when(rentalStatusMapper.toDto(testStatus)).thenReturn(testStatusDTO);

        RentalStatusDTO result = rentalStatusService.getStatusById(1L);

        assertNotNull(result);
        assertEquals("Active", result.getName());
        verify(rentalStatusRepository, times(1)).findById(1L);
    }

    @Test
    void getStatusById_NotFound() {
        when(rentalStatusRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> rentalStatusService.getStatusById(999L));
    }

    @Test
    void createStatus_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(rentalStatusMapper.toEntity(testStatusDTO)).thenReturn(testStatus);
        when(rentalStatusRepository.save(any())).thenReturn(testStatus);
        when(rentalStatusMapper.toDto(testStatus)).thenReturn(testStatusDTO);

        RentalStatusDTO result = rentalStatusService.createStatus(testStatusDTO);

        assertNotNull(result);
        verify(rentalStatusRepository, times(1)).save(any());
    }

    @Test
    void updateStatus_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(rentalStatusRepository.findById(1L)).thenReturn(Optional.of(testStatus));
        doNothing().when(rentalStatusMapper).updateEntityFromDto(testStatusDTO, testStatus);
        when(rentalStatusRepository.save(any())).thenReturn(testStatus);
        when(rentalStatusMapper.toDto(testStatus)).thenReturn(testStatusDTO);

        RentalStatusDTO result = rentalStatusService.updateStatus(1L, testStatusDTO);

        assertNotNull(result);
        verify(rentalStatusRepository, times(1)).save(any());
    }

    @Test
    void deleteStatus_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(rentalStatusRepository.findById(1L)).thenReturn(Optional.of(testStatus));
        doNothing().when(rentalStatusRepository).delete(testStatus);

        rentalStatusService.deleteStatus(1L);

        verify(rentalStatusRepository, times(1)).delete(testStatus);
    }
}


