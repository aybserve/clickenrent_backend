package org.clickenrent.paymentservice.service;

import org.clickenrent.paymentservice.dto.RefundStatusDTO;
import org.clickenrent.paymentservice.entity.RefundStatus;
import org.clickenrent.paymentservice.exception.ResourceNotFoundException;
import org.clickenrent.paymentservice.mapper.RefundStatusMapper;
import org.clickenrent.paymentservice.repository.RefundStatusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefundStatusServiceTest {

    @Mock
    private RefundStatusRepository refundStatusRepository;

    @Mock
    private RefundStatusMapper refundStatusMapper;

    @InjectMocks
    private RefundStatusService refundStatusService;

    private RefundStatus testStatus;
    private RefundStatusDTO testStatusDTO;
    private String testExternalId;

    @BeforeEach
    void setUp() {
        testExternalId = UUID.randomUUID().toString();
        testStatus = RefundStatus.builder()
                .id(1L)
                .externalId(testExternalId)
                .code("PROCESSING")
                .name("Processing")
                .build();
        testStatusDTO = RefundStatusDTO.builder()
                .id(1L)
                .externalId(testExternalId)
                .code("PROCESSING")
                .name("Processing")
                .build();
    }

    @Test
    void findAll_ReturnsAllStatuses() {
        when(refundStatusRepository.findAll()).thenReturn(Arrays.asList(testStatus));
        when(refundStatusMapper.toDTOList(anyList())).thenReturn(Arrays.asList(testStatusDTO));

        List<RefundStatusDTO> result = refundStatusService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("PROCESSING", result.get(0).getCode());
        verify(refundStatusRepository).findAll();
    }

    @Test
    void findById_Success() {
        when(refundStatusRepository.findById(1L)).thenReturn(Optional.of(testStatus));
        when(refundStatusMapper.toDTO(testStatus)).thenReturn(testStatusDTO);

        RefundStatusDTO result = refundStatusService.findById(1L);

        assertNotNull(result);
        assertEquals("PROCESSING", result.getCode());
        verify(refundStatusRepository).findById(1L);
    }

    @Test
    void findById_NotFound() {
        when(refundStatusRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> refundStatusService.findById(999L));
        verify(refundStatusRepository).findById(999L);
    }

    @Test
    void findByCode_Success() {
        when(refundStatusRepository.findByCode("PROCESSING")).thenReturn(Optional.of(testStatus));
        when(refundStatusMapper.toDTO(testStatus)).thenReturn(testStatusDTO);

        RefundStatusDTO result = refundStatusService.findByCode("PROCESSING");

        assertNotNull(result);
        assertEquals("PROCESSING", result.getCode());
        verify(refundStatusRepository).findByCode("PROCESSING");
    }

    @Test
    void findByCode_NotFound() {
        when(refundStatusRepository.findByCode("UNKNOWN")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> refundStatusService.findByCode("UNKNOWN"));
        verify(refundStatusRepository).findByCode("UNKNOWN");
    }
}
