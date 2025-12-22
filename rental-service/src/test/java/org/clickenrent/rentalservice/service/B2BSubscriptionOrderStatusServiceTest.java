package org.clickenrent.rentalservice.service;

import org.clickenrent.rentalservice.dto.B2BSubscriptionOrderStatusDTO;
import org.clickenrent.rentalservice.entity.B2BSubscriptionOrderStatus;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.mapper.B2BSubscriptionOrderStatusMapper;
import org.clickenrent.rentalservice.repository.B2BSubscriptionOrderStatusRepository;
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
class B2BSubscriptionOrderStatusServiceTest {

    @Mock
    private B2BSubscriptionOrderStatusRepository b2bSubscriptionOrderStatusRepository;

    @Mock
    private B2BSubscriptionOrderStatusMapper b2bSubscriptionOrderStatusMapper;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private B2BSubscriptionOrderStatusService b2bSubscriptionOrderStatusService;

    private B2BSubscriptionOrderStatus testStatus;
    private B2BSubscriptionOrderStatusDTO testStatusDTO;

    @BeforeEach
    void setUp() {
        testStatus = B2BSubscriptionOrderStatus.builder()
        .id(1L)
        .name("Pending")
        .build();

        testStatusDTO = B2BSubscriptionOrderStatusDTO.builder()
        .id(1L)
        .name("Pending")
        .build();
    }

    @Test
    void getAllStatuses_ReturnsAllStatuses() {
        when(b2bSubscriptionOrderStatusRepository.findAll()).thenReturn(Arrays.asList(testStatus));
        when(b2bSubscriptionOrderStatusMapper.toDto(testStatus)).thenReturn(testStatusDTO);

        var result = b2bSubscriptionOrderStatusService.getAllStatuses();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Pending", result.get(0).getName());
        verify(b2bSubscriptionOrderStatusRepository, times(1)).findAll();
    }

    @Test
    void getStatusById_Success() {
        when(b2bSubscriptionOrderStatusRepository.findById(1L)).thenReturn(Optional.of(testStatus));
        when(b2bSubscriptionOrderStatusMapper.toDto(testStatus)).thenReturn(testStatusDTO);

        B2BSubscriptionOrderStatusDTO result = b2bSubscriptionOrderStatusService.getStatusById(1L);

        assertNotNull(result);
        assertEquals("Pending", result.getName());
        verify(b2bSubscriptionOrderStatusRepository, times(1)).findById(1L);
    }

    @Test
    void getStatusById_NotFound() {
        when(b2bSubscriptionOrderStatusRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> b2bSubscriptionOrderStatusService.getStatusById(999L));
    }
}




