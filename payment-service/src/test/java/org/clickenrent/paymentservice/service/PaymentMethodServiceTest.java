package org.clickenrent.paymentservice.service;

import org.clickenrent.paymentservice.dto.PaymentMethodDTO;
import org.clickenrent.paymentservice.entity.PaymentMethod;
import org.clickenrent.paymentservice.exception.DuplicateResourceException;
import org.clickenrent.paymentservice.exception.ResourceNotFoundException;
import org.clickenrent.paymentservice.mapper.PaymentMethodMapper;
import org.clickenrent.paymentservice.repository.PaymentMethodRepository;
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
class PaymentMethodServiceTest {

    @Mock
    private PaymentMethodRepository paymentMethodRepository;

    @Mock
    private PaymentMethodMapper paymentMethodMapper;

    @InjectMocks
    private PaymentMethodService paymentMethodService;

    private PaymentMethod testPaymentMethod;
    private PaymentMethodDTO testPaymentMethodDTO;
    private UUID testExternalId;

    @BeforeEach
    void setUp() {
        testExternalId = UUID.randomUUID();
        
        testPaymentMethod = PaymentMethod.builder()
                .id(1L)
                .externalId(testExternalId)
                .code("CREDIT_CARD")
                .name("Credit Card")
                .isActive(true)
                .build();

        testPaymentMethodDTO = PaymentMethodDTO.builder()
                .id(1L)
                .externalId(testExternalId)
                .code("CREDIT_CARD")
                .name("Credit Card")
                .isActive(true)
                .build();
    }

    @Test
    void findAll_ReturnsAllPaymentMethods() {
        when(paymentMethodRepository.findAll()).thenReturn(Arrays.asList(testPaymentMethod));
        when(paymentMethodMapper.toDTOList(anyList())).thenReturn(Arrays.asList(testPaymentMethodDTO));

        List<PaymentMethodDTO> result = paymentMethodService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("CREDIT_CARD", result.get(0).getCode());
        verify(paymentMethodRepository, times(1)).findAll();
    }

    @Test
    void findActivePaymentMethods_ReturnsActiveOnly() {
        when(paymentMethodRepository.findByIsActive(true)).thenReturn(Arrays.asList(testPaymentMethod));
        when(paymentMethodMapper.toDTOList(anyList())).thenReturn(Arrays.asList(testPaymentMethodDTO));

        List<PaymentMethodDTO> result = paymentMethodService.findActivePaymentMethods();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getIsActive());
        verify(paymentMethodRepository, times(1)).findByIsActive(true);
    }

    @Test
    void findById_Success() {
        when(paymentMethodRepository.findById(1L)).thenReturn(Optional.of(testPaymentMethod));
        when(paymentMethodMapper.toDTO(testPaymentMethod)).thenReturn(testPaymentMethodDTO);

        PaymentMethodDTO result = paymentMethodService.findById(1L);

        assertNotNull(result);
        assertEquals("CREDIT_CARD", result.getCode());
        assertEquals("Credit Card", result.getName());
        verify(paymentMethodRepository, times(1)).findById(1L);
    }

    @Test
    void findById_NotFound() {
        when(paymentMethodRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> paymentMethodService.findById(999L));
        verify(paymentMethodRepository, times(1)).findById(999L);
    }

    @Test
    void findByExternalId_Success() {
        when(paymentMethodRepository.findByExternalId(testExternalId)).thenReturn(Optional.of(testPaymentMethod));
        when(paymentMethodMapper.toDTO(testPaymentMethod)).thenReturn(testPaymentMethodDTO);

        PaymentMethodDTO result = paymentMethodService.findByExternalId(testExternalId);

        assertNotNull(result);
        assertEquals("CREDIT_CARD", result.getCode());
        verify(paymentMethodRepository, times(1)).findByExternalId(testExternalId);
    }

    @Test
    void findByExternalId_NotFound() {
        UUID randomId = UUID.randomUUID();
        when(paymentMethodRepository.findByExternalId(randomId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> paymentMethodService.findByExternalId(randomId));
    }

    @Test
    void findByCode_Success() {
        when(paymentMethodRepository.findByCode("CREDIT_CARD")).thenReturn(Optional.of(testPaymentMethod));
        when(paymentMethodMapper.toDTO(testPaymentMethod)).thenReturn(testPaymentMethodDTO);

        PaymentMethodDTO result = paymentMethodService.findByCode("CREDIT_CARD");

        assertNotNull(result);
        assertEquals("CREDIT_CARD", result.getCode());
        verify(paymentMethodRepository, times(1)).findByCode("CREDIT_CARD");
    }

    @Test
    void findByCode_NotFound() {
        when(paymentMethodRepository.findByCode("UNKNOWN")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> paymentMethodService.findByCode("UNKNOWN"));
    }

    @Test
    void create_Success() {
        when(paymentMethodRepository.findByCode("CREDIT_CARD")).thenReturn(Optional.empty());
        when(paymentMethodMapper.toEntity(testPaymentMethodDTO)).thenReturn(testPaymentMethod);
        when(paymentMethodRepository.save(any(PaymentMethod.class))).thenReturn(testPaymentMethod);
        when(paymentMethodMapper.toDTO(testPaymentMethod)).thenReturn(testPaymentMethodDTO);

        PaymentMethodDTO result = paymentMethodService.create(testPaymentMethodDTO);

        assertNotNull(result);
        assertEquals("CREDIT_CARD", result.getCode());
        verify(paymentMethodRepository, times(1)).save(any(PaymentMethod.class));
    }

    @Test
    void create_DuplicateCode_ThrowsException() {
        when(paymentMethodRepository.findByCode("CREDIT_CARD")).thenReturn(Optional.of(testPaymentMethod));

        assertThrows(DuplicateResourceException.class, () -> paymentMethodService.create(testPaymentMethodDTO));
        verify(paymentMethodRepository, never()).save(any(PaymentMethod.class));
    }

    @Test
    void update_Success() {
        when(paymentMethodRepository.findById(1L)).thenReturn(Optional.of(testPaymentMethod));
        when(paymentMethodRepository.save(any(PaymentMethod.class))).thenReturn(testPaymentMethod);
        when(paymentMethodMapper.toDTO(testPaymentMethod)).thenReturn(testPaymentMethodDTO);

        PaymentMethodDTO result = paymentMethodService.update(1L, testPaymentMethodDTO);

        assertNotNull(result);
        assertEquals("CREDIT_CARD", result.getCode());
        verify(paymentMethodRepository, times(1)).findById(1L);
        verify(paymentMethodRepository, times(1)).save(any(PaymentMethod.class));
    }

    @Test
    void update_NotFound() {
        when(paymentMethodRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> paymentMethodService.update(999L, testPaymentMethodDTO));
        verify(paymentMethodRepository, never()).save(any(PaymentMethod.class));
    }

    @Test
    void update_DuplicateCode_ThrowsException() {
        PaymentMethod existingMethod = PaymentMethod.builder()
                .id(1L)
                .code("DEBIT_CARD")
                .name("Debit Card")
                .isActive(true)
                .build();
        
        when(paymentMethodRepository.findById(1L)).thenReturn(Optional.of(existingMethod));
        when(paymentMethodRepository.findByCode("CREDIT_CARD")).thenReturn(Optional.of(testPaymentMethod));

        assertThrows(DuplicateResourceException.class, () -> paymentMethodService.update(1L, testPaymentMethodDTO));
        verify(paymentMethodRepository, never()).save(any(PaymentMethod.class));
    }

    @Test
    void delete_Success() {
        when(paymentMethodRepository.existsById(1L)).thenReturn(true);
        doNothing().when(paymentMethodRepository).deleteById(1L);

        paymentMethodService.delete(1L);

        verify(paymentMethodRepository, times(1)).existsById(1L);
        verify(paymentMethodRepository, times(1)).deleteById(1L);
    }

    @Test
    void delete_NotFound() {
        when(paymentMethodRepository.existsById(999L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> paymentMethodService.delete(999L));
        verify(paymentMethodRepository, never()).deleteById(anyLong());
    }
}


