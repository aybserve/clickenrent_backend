package org.clickenrent.paymentservice.service;

import org.clickenrent.paymentservice.dto.RefundDTO;
import org.clickenrent.paymentservice.entity.Refund;
import org.clickenrent.paymentservice.exception.ResourceNotFoundException;
import org.clickenrent.paymentservice.exception.UnauthorizedException;
import org.clickenrent.paymentservice.mapper.RefundMapper;
import org.clickenrent.paymentservice.repository.RefundRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefundServiceTest {

    @Mock
    private RefundRepository refundRepository;

    @Mock
    private RefundMapper refundMapper;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private RefundService refundService;

    private Refund testRefund;
    private RefundDTO testRefundDTO;

    @BeforeEach
    void setUp() {
        testRefund = Refund.builder()
                .id(1L)
                .externalId("ref-ext-1")
                .companyExternalId("company-1")
                .build();
        testRefundDTO = RefundDTO.builder()
                .id(1L)
                .externalId("ref-ext-1")
                .financialTransactionId(100L)
                .build();
    }

    @Test
    void findAll_WhenAdmin_ReturnsList() {
        when(securityService.isAdmin()).thenReturn(true);
        when(refundRepository.findAll()).thenReturn(Arrays.asList(testRefund));
        when(refundMapper.toDTOList(anyList())).thenReturn(Arrays.asList(testRefundDTO));

        List<RefundDTO> result = refundService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        verify(refundRepository).findAll();
    }

    @Test
    void findAll_WhenNotAdmin_ThrowsUnauthorizedException() {
        when(securityService.isAdmin()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> refundService.findAll());
        verify(refundRepository, never()).findAll();
    }

    @Test
    void findById_WhenAdmin_ReturnsDto() {
        when(securityService.isAdmin()).thenReturn(true);
        when(refundRepository.findById(1L)).thenReturn(Optional.of(testRefund));
        when(refundMapper.toDTO(testRefund)).thenReturn(testRefundDTO);

        RefundDTO result = refundService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(refundRepository).findById(1L);
    }

    @Test
    void findById_WhenNotFound_ThrowsResourceNotFoundException() {
        when(refundRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> refundService.findById(999L));
        verify(refundRepository).findById(999L);
    }

    @Test
    void findByExternalId_WhenAdmin_ReturnsDto() {
        when(securityService.isAdmin()).thenReturn(true);
        when(refundRepository.findByExternalId("ref-ext-1")).thenReturn(Optional.of(testRefund));
        when(refundMapper.toDTO(testRefund)).thenReturn(testRefundDTO);

        RefundDTO result = refundService.findByExternalId("ref-ext-1");

        assertNotNull(result);
        assertEquals("ref-ext-1", result.getExternalId());
        verify(refundRepository).findByExternalId("ref-ext-1");
    }
}
