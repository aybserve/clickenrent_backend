package org.clickenrent.rentalservice.service;

import org.clickenrent.rentalservice.dto.B2BSaleStatusDTO;
import org.clickenrent.rentalservice.entity.B2BSaleStatus;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.mapper.B2BSaleStatusMapper;
import org.clickenrent.rentalservice.repository.B2BSaleStatusRepository;
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
class B2BSaleStatusServiceTest {

    @Mock
    private B2BSaleStatusRepository b2bSaleStatusRepository;

    @Mock
    private B2BSaleStatusMapper b2bSaleStatusMapper;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private B2BSaleStatusService b2bSaleStatusService;

    private B2BSaleStatus testStatus;
    private B2BSaleStatusDTO testStatusDTO;

    @BeforeEach
    void setUp() {
        testStatus = B2BSaleStatus.builder()
        .id(1L)
        .name("Ordered")
        .build();

        testStatusDTO = B2BSaleStatusDTO.builder()
        .id(1L)
        .name("Ordered")
        .build();
    }

    @Test
    void getAllStatuses_ReturnsAllStatuses() {
        when(b2bSaleStatusRepository.findAll()).thenReturn(Arrays.asList(testStatus));
        when(b2bSaleStatusMapper.toDto(testStatus)).thenReturn(testStatusDTO);

        var result = b2bSaleStatusService.getAllStatuses();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Ordered", result.get(0).getName());
        verify(b2bSaleStatusRepository, times(1)).findAll();
    }

    @Test
    void getStatusById_Success() {
        when(b2bSaleStatusRepository.findById(1L)).thenReturn(Optional.of(testStatus));
        when(b2bSaleStatusMapper.toDto(testStatus)).thenReturn(testStatusDTO);

        B2BSaleStatusDTO result = b2bSaleStatusService.getStatusById(1L);

        assertNotNull(result);
        assertEquals("Ordered", result.getName());
        verify(b2bSaleStatusRepository, times(1)).findById(1L);
    }

    @Test
    void getStatusById_NotFound() {
        when(b2bSaleStatusRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> b2bSaleStatusService.getStatusById(999L));
    }
}







