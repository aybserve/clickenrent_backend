package org.clickenrent.rentalservice.service;

import org.clickenrent.rentalservice.dto.B2BSaleOrderStatusDTO;
import org.clickenrent.rentalservice.entity.B2BSaleOrderStatus;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.mapper.B2BSaleOrderStatusMapper;
import org.clickenrent.rentalservice.repository.B2BSaleOrderStatusRepository;
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
class B2BSaleOrderStatusServiceTest {

    @Mock
    private B2BSaleOrderStatusRepository b2bSaleOrderStatusRepository;

    @Mock
    private B2BSaleOrderStatusMapper b2bSaleOrderStatusMapper;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private B2BSaleOrderStatusService b2bSaleOrderStatusService;

    private B2BSaleOrderStatus testStatus;
    private B2BSaleOrderStatusDTO testStatusDTO;

    @BeforeEach
    void setUp() {
        testStatus = B2BSaleOrderStatus.builder()
        .id(1L)
        .name("Processing")
        .build();

        testStatusDTO = B2BSaleOrderStatusDTO.builder()
        .id(1L)
        .name("Processing")
        .build();
    }

    @Test
    void getAllStatuses_ReturnsAllStatuses() {
        when(b2bSaleOrderStatusRepository.findAll()).thenReturn(Arrays.asList(testStatus));
        when(b2bSaleOrderStatusMapper.toDto(testStatus)).thenReturn(testStatusDTO);

        var result = b2bSaleOrderStatusService.getAllStatuses();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Processing", result.get(0).getName());
        verify(b2bSaleOrderStatusRepository, times(1)).findAll();
    }

    @Test
    void getStatusById_Success() {
        when(b2bSaleOrderStatusRepository.findById(1L)).thenReturn(Optional.of(testStatus));
        when(b2bSaleOrderStatusMapper.toDto(testStatus)).thenReturn(testStatusDTO);

        B2BSaleOrderStatusDTO result = b2bSaleOrderStatusService.getStatusById(1L);

        assertNotNull(result);
        assertEquals("Processing", result.getName());
        verify(b2bSaleOrderStatusRepository, times(1)).findById(1L);
    }

    @Test
    void getStatusById_NotFound() {
        when(b2bSaleOrderStatusRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> b2bSaleOrderStatusService.getStatusById(999L));
    }
}




