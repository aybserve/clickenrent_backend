package org.clickenrent.paymentservice.service;

import org.clickenrent.paymentservice.dto.PaymentStatusDTO;
import org.clickenrent.paymentservice.entity.PaymentStatus;
import org.clickenrent.paymentservice.exception.DuplicateResourceException;
import org.clickenrent.paymentservice.exception.ResourceNotFoundException;
import org.clickenrent.paymentservice.mapper.PaymentStatusMapper;
import org.clickenrent.paymentservice.repository.PaymentStatusRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentStatusServiceTest {

    @Mock
    private PaymentStatusRepository paymentStatusRepository;

    @Mock
    private PaymentStatusMapper paymentStatusMapper;

    @InjectMocks
    private PaymentStatusService paymentStatusService;

    private PaymentStatus testPaymentStatus;
    private PaymentStatusDTO testPaymentStatusDTO;
    private UUID testExternalId;

    @BeforeEach
    void setUp() {
        testExternalId = UUID.randomUUID();
        
        testPaymentStatus = PaymentStatus.builder()
                .id(1L)
                .externalId(testExternalId)
                .code("SUCCEEDED")
                .name("Payment Succeeded")
                .build();

        testPaymentStatusDTO = PaymentStatusDTO.builder()
                .id(1L)
                .externalId(testExternalId)
                .code("SUCCEEDED")
                .name("Payment Succeeded")
                .build();
    }

    @Test
    void findAll_ReturnsAllStatuses() {
        when(paymentStatusRepository.findAll()).thenReturn(Arrays.asList(testPaymentStatus));
        when(paymentStatusMapper.toDTOList(anyList())).thenReturn(Arrays.asList(testPaymentStatusDTO));

        List<PaymentStatusDTO> result = paymentStatusService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("SUCCEEDED", result.get(0).getCode());
        verify(paymentStatusRepository, times(1)).findAll();
    }

    @Test
    void findById_Success() {
        when(paymentStatusRepository.findById(1L)).thenReturn(Optional.of(testPaymentStatus));
        when(paymentStatusMapper.toDTO(testPaymentStatus)).thenReturn(testPaymentStatusDTO);

        PaymentStatusDTO result = paymentStatusService.findById(1L);

        assertNotNull(result);
        assertEquals("SUCCEEDED", result.getCode());
        assertEquals("Payment Succeeded", result.getName());
        verify(paymentStatusRepository, times(1)).findById(1L);
    }

    @Test
    void findById_NotFound() {
        when(paymentStatusRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> paymentStatusService.findById(999L));
        verify(paymentStatusRepository, times(1)).findById(999L);
    }

    @Test
    void findByExternalId_Success() {
        when(paymentStatusRepository.findByExternalId(testExternalId)).thenReturn(Optional.of(testPaymentStatus));
        when(paymentStatusMapper.toDTO(testPaymentStatus)).thenReturn(testPaymentStatusDTO);

        PaymentStatusDTO result = paymentStatusService.findByExternalId(testExternalId);

        assertNotNull(result);
        assertEquals("SUCCEEDED", result.getCode());
        verify(paymentStatusRepository, times(1)).findByExternalId(testExternalId);
    }

    @Test
    void findByExternalId_NotFound() {
        UUID randomId = UUID.randomUUID();
        when(paymentStatusRepository.findByExternalId(randomId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> paymentStatusService.findByExternalId(randomId));
    }

    @Test
    void findByCode_Success() {
        when(paymentStatusRepository.findByCode("SUCCEEDED")).thenReturn(Optional.of(testPaymentStatus));
        when(paymentStatusMapper.toDTO(testPaymentStatus)).thenReturn(testPaymentStatusDTO);

        PaymentStatusDTO result = paymentStatusService.findByCode("SUCCEEDED");

        assertNotNull(result);
        assertEquals("SUCCEEDED", result.getCode());
        verify(paymentStatusRepository, times(1)).findByCode("SUCCEEDED");
    }

    @Test
    void findByCode_NotFound() {
        when(paymentStatusRepository.findByCode("UNKNOWN")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> paymentStatusService.findByCode("UNKNOWN"));
    }

    @Test
    void create_Success() {
        when(paymentStatusRepository.findByCode("SUCCEEDED")).thenReturn(Optional.empty());
        when(paymentStatusMapper.toEntity(testPaymentStatusDTO)).thenReturn(testPaymentStatus);
        when(paymentStatusRepository.save(any(PaymentStatus.class))).thenReturn(testPaymentStatus);
        when(paymentStatusMapper.toDTO(testPaymentStatus)).thenReturn(testPaymentStatusDTO);

        PaymentStatusDTO result = paymentStatusService.create(testPaymentStatusDTO);

        assertNotNull(result);
        assertEquals("SUCCEEDED", result.getCode());
        verify(paymentStatusRepository, times(1)).save(any(PaymentStatus.class));
    }

    @Test
    void create_DuplicateCode_ThrowsException() {
        when(paymentStatusRepository.findByCode("SUCCEEDED")).thenReturn(Optional.of(testPaymentStatus));

        assertThrows(DuplicateResourceException.class, () -> paymentStatusService.create(testPaymentStatusDTO));
        verify(paymentStatusRepository, never()).save(any(PaymentStatus.class));
    }

    @Test
    void update_Success() {
        when(paymentStatusRepository.findById(1L)).thenReturn(Optional.of(testPaymentStatus));
        when(paymentStatusRepository.save(any(PaymentStatus.class))).thenReturn(testPaymentStatus);
        when(paymentStatusMapper.toDTO(testPaymentStatus)).thenReturn(testPaymentStatusDTO);

        PaymentStatusDTO result = paymentStatusService.update(1L, testPaymentStatusDTO);

        assertNotNull(result);
        assertEquals("SUCCEEDED", result.getCode());
        verify(paymentStatusRepository, times(1)).findById(1L);
        verify(paymentStatusRepository, times(1)).save(any(PaymentStatus.class));
    }

    @Test
    void update_NotFound() {
        when(paymentStatusRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> paymentStatusService.update(999L, testPaymentStatusDTO));
        verify(paymentStatusRepository, never()).save(any(PaymentStatus.class));
    }

    @Test
    void update_DuplicateCode_ThrowsException() {
        PaymentStatus existingStatus = PaymentStatus.builder()
                .id(1L)
                .code("PENDING")
                .name("Payment Pending")
                .build();
        
        when(paymentStatusRepository.findById(1L)).thenReturn(Optional.of(existingStatus));
        when(paymentStatusRepository.findByCode("SUCCEEDED")).thenReturn(Optional.of(testPaymentStatus));

        assertThrows(DuplicateResourceException.class, () -> paymentStatusService.update(1L, testPaymentStatusDTO));
        verify(paymentStatusRepository, never()).save(any(PaymentStatus.class));
    }

    @Test
    void delete_Success() {
        when(paymentStatusRepository.existsById(1L)).thenReturn(true);
        doNothing().when(paymentStatusRepository).deleteById(1L);

        paymentStatusService.delete(1L);

        verify(paymentStatusRepository, times(1)).existsById(1L);
        verify(paymentStatusRepository, times(1)).deleteById(1L);
    }

    @Test
    void delete_NotFound() {
        when(paymentStatusRepository.existsById(999L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> paymentStatusService.delete(999L));
        verify(paymentStatusRepository, never()).deleteById(anyLong());
    }
}


