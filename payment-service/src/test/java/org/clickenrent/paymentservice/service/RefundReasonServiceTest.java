package org.clickenrent.paymentservice.service;

import org.clickenrent.paymentservice.dto.RefundReasonDTO;
import org.clickenrent.paymentservice.entity.RefundReason;
import org.clickenrent.paymentservice.exception.ResourceNotFoundException;
import org.clickenrent.paymentservice.mapper.RefundReasonMapper;
import org.clickenrent.paymentservice.repository.RefundReasonRepository;
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
class RefundReasonServiceTest {

    @Mock
    private RefundReasonRepository refundReasonRepository;

    @Mock
    private RefundReasonMapper refundReasonMapper;

    @InjectMocks
    private RefundReasonService refundReasonService;

    private RefundReason testReason;
    private RefundReasonDTO testReasonDTO;
    private String testExternalId;

    @BeforeEach
    void setUp() {
        testExternalId = UUID.randomUUID().toString();
        testReason = RefundReason.builder()
                .id(1L)
                .externalId(testExternalId)
                .code("CUSTOMER_REQUEST")
                .name("Customer Request")
                .build();
        testReasonDTO = RefundReasonDTO.builder()
                .id(1L)
                .externalId(testExternalId)
                .code("CUSTOMER_REQUEST")
                .name("Customer Request")
                .build();
    }

    @Test
    void findAll_ReturnsAllReasons() {
        when(refundReasonRepository.findAll()).thenReturn(Arrays.asList(testReason));
        when(refundReasonMapper.toDTOList(anyList())).thenReturn(Arrays.asList(testReasonDTO));

        List<RefundReasonDTO> result = refundReasonService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("CUSTOMER_REQUEST", result.get(0).getCode());
        verify(refundReasonRepository).findAll();
    }

    @Test
    void findById_Success() {
        when(refundReasonRepository.findById(1L)).thenReturn(Optional.of(testReason));
        when(refundReasonMapper.toDTO(testReason)).thenReturn(testReasonDTO);

        RefundReasonDTO result = refundReasonService.findById(1L);

        assertNotNull(result);
        assertEquals("CUSTOMER_REQUEST", result.getCode());
        verify(refundReasonRepository).findById(1L);
    }

    @Test
    void findById_NotFound() {
        when(refundReasonRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> refundReasonService.findById(999L));
        verify(refundReasonRepository).findById(999L);
    }

    @Test
    void findByCode_Success() {
        when(refundReasonRepository.findByCode("CUSTOMER_REQUEST")).thenReturn(Optional.of(testReason));
        when(refundReasonMapper.toDTO(testReason)).thenReturn(testReasonDTO);

        RefundReasonDTO result = refundReasonService.findByCode("CUSTOMER_REQUEST");

        assertNotNull(result);
        assertEquals("CUSTOMER_REQUEST", result.getCode());
        verify(refundReasonRepository).findByCode("CUSTOMER_REQUEST");
    }

    @Test
    void findByCode_NotFound() {
        when(refundReasonRepository.findByCode("UNKNOWN")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> refundReasonService.findByCode("UNKNOWN"));
        verify(refundReasonRepository).findByCode("UNKNOWN");
    }
}
