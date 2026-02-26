package org.clickenrent.rentalservice.service;

import org.clickenrent.rentalservice.dto.B2BSubscriptionStatusDTO;
import org.clickenrent.rentalservice.entity.B2BSubscriptionStatus;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.mapper.B2BSubscriptionStatusMapper;
import org.clickenrent.rentalservice.repository.B2BSubscriptionStatusRepository;
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
class B2BSubscriptionStatusServiceTest {

    @Mock
    private B2BSubscriptionStatusRepository b2bSubscriptionStatusRepository;

    @Mock
    private B2BSubscriptionStatusMapper b2bSubscriptionStatusMapper;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private B2BSubscriptionStatusService b2bSubscriptionStatusService;

    private B2BSubscriptionStatus testStatus;
    private B2BSubscriptionStatusDTO testStatusDTO;

    @BeforeEach
    void setUp() {
        testStatus = B2BSubscriptionStatus.builder()
        .id(1L)
        .name("Active")
        .build();

        testStatusDTO = B2BSubscriptionStatusDTO.builder()
        .id(1L)
        .name("Active")
        .build();
    }

    @Test
    void getAllStatuses_ReturnsAllStatuses() {
        when(b2bSubscriptionStatusRepository.findAll()).thenReturn(Arrays.asList(testStatus));
        when(b2bSubscriptionStatusMapper.toDto(testStatus)).thenReturn(testStatusDTO);

        List<B2BSubscriptionStatusDTO> result = b2bSubscriptionStatusService.getAllStatuses();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Active", result.get(0).getName());
        verify(b2bSubscriptionStatusRepository, times(1)).findAll();
    }

    @Test
    void getStatusById_Success() {
        when(b2bSubscriptionStatusRepository.findById(1L)).thenReturn(Optional.of(testStatus));
        when(b2bSubscriptionStatusMapper.toDto(testStatus)).thenReturn(testStatusDTO);

        B2BSubscriptionStatusDTO result = b2bSubscriptionStatusService.getStatusById(1L);

        assertNotNull(result);
        assertEquals("Active", result.getName());
        verify(b2bSubscriptionStatusRepository, times(1)).findById(1L);
    }

    @Test
    void getStatusById_NotFound() {
        when(b2bSubscriptionStatusRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> b2bSubscriptionStatusService.getStatusById(999L));
    }
}
