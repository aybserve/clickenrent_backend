package org.clickenrent.paymentservice.service;

import org.clickenrent.paymentservice.dto.*;
import org.clickenrent.paymentservice.entity.*;
import org.clickenrent.paymentservice.exception.ResourceNotFoundException;
import org.clickenrent.paymentservice.exception.UnauthorizedException;
import org.clickenrent.paymentservice.mapper.FinancialTransactionMapper;
import org.clickenrent.paymentservice.repository.FinancialTransactionRepository;
import org.clickenrent.paymentservice.repository.PaymentStatusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class FinancialTransactionServiceTest {

    @Mock
    private FinancialTransactionRepository financialTransactionRepository;

    @Mock
    private FinancialTransactionMapper financialTransactionMapper;

    @Mock
    private PaymentStatusRepository paymentStatusRepository;

    @Mock
    private SecurityService securityService;

    @Mock
    private StripeService stripeService;

    @InjectMocks
    private FinancialTransactionService financialTransactionService;

    private FinancialTransaction testTransaction;
    private FinancialTransactionDTO testTransactionDTO;
    private PaymentStatus testStatus;
    private Currency testCurrency;
    private PaymentMethod testPaymentMethod;
    private ServiceProvider testServiceProvider;
    private String testExternalId;

    @BeforeEach
    void setUp() {
        testExternalId = UUID.randomUUID().toString();

        testCurrency = Currency.builder()
                .id(1L)
                .code("USD")
                .name("US Dollar")
                .build();

        testPaymentMethod = PaymentMethod.builder()
                .id(1L)
                .code("CREDIT_CARD")
                .name("Credit Card")
                .isActive(true)
                .build();

        testStatus = PaymentStatus.builder()
                .id(1L)
                .code("SUCCEEDED")
                .name("Payment Succeeded")
                .build();

        testServiceProvider = ServiceProvider.builder()
                .id(1L)
                .code("STRIPE")
                .name("Stripe")
                .build();

        testTransaction = FinancialTransaction.builder()
                .id(1L)
                .externalId(testExternalId)
                .payerExternalId("payer-ext-123")
                .recipientExternalId("recipient-ext-456")
                .amount(new BigDecimal("100.00"))
                .currency(testCurrency)
                .dateTime(LocalDateTime.now())
                .paymentMethod(testPaymentMethod)
                .paymentStatus(testStatus)
                .serviceProvider(testServiceProvider)
                .build();

        testTransactionDTO = FinancialTransactionDTO.builder()
                .id(1L)
                .externalId(testExternalId)
                .payerExternalId("payer-ext-123")
                .recipientExternalId("recipient-ext-456")
                .amount(new BigDecimal("100.00"))
                .currency(CurrencyDTO.builder().id(1L).code("USD").name("US Dollar").build())
                .dateTime(LocalDateTime.now())
                .paymentMethod(PaymentMethodDTO.builder().id(1L).code("CREDIT_CARD").name("Credit Card").isActive(true).build())
                .paymentStatus(PaymentStatusDTO.builder().id(1L).code("SUCCEEDED").name("Payment Succeeded").build())
                .serviceProvider(ServiceProviderDTO.builder().id(1L).code("STRIPE").name("Stripe").build())
                .build();

        lenient().when(securityService.isAdmin()).thenReturn(true);
        lenient().when(securityService.hasAccessToUser(anyLong())).thenReturn(true);
        lenient().when(securityService.getCurrentUserId()).thenReturn(1L);
    }

    @Test
    void findAll_AsAdmin_ReturnsAllTransactions() {
        when(financialTransactionRepository.findAll()).thenReturn(Arrays.asList(testTransaction));
        when(financialTransactionMapper.toDTOList(anyList())).thenReturn(Arrays.asList(testTransactionDTO));

        List<FinancialTransactionDTO> result = financialTransactionService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(financialTransactionRepository, times(1)).findAll();
    }

    @Test
    void findAll_AsNonAdmin_FiltersTransactions() {
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.getCurrentUserId()).thenReturn(1L);
        when(financialTransactionRepository.findAll()).thenReturn(Arrays.asList(testTransaction));
        when(financialTransactionMapper.toDTOList(anyList())).thenReturn(Arrays.asList(testTransactionDTO));

        List<FinancialTransactionDTO> result = financialTransactionService.findAll();

        assertNotNull(result);
        verify(financialTransactionRepository, times(1)).findAll();
    }

    @Test
    void findById_Success() {
        when(financialTransactionRepository.findById(1L)).thenReturn(Optional.of(testTransaction));
        when(financialTransactionMapper.toDTO(testTransaction)).thenReturn(testTransactionDTO);

        FinancialTransactionDTO result = financialTransactionService.findById(1L);

        assertNotNull(result);
        assertEquals(new BigDecimal("100.00"), result.getAmount());
        verify(financialTransactionRepository, times(1)).findById(1L);
    }

    @Test
    void findById_NotFound() {
        when(financialTransactionRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> financialTransactionService.findById(999L));
    }

    @Test
    void findByExternalId_Success() {
        when(financialTransactionRepository.findByExternalId(testExternalId)).thenReturn(Optional.of(testTransaction));
        when(financialTransactionMapper.toDTO(testTransaction)).thenReturn(testTransactionDTO);

        FinancialTransactionDTO result = financialTransactionService.findByExternalId(testExternalId);

        assertNotNull(result);
        verify(financialTransactionRepository, times(1)).findByExternalId(testExternalId);
    }

    @Test
    void findByPayerExternalId_Success() {
        when(financialTransactionRepository.findByPayerExternalId("payer-ext-123")).thenReturn(Arrays.asList(testTransaction));
        when(financialTransactionMapper.toDTOList(anyList())).thenReturn(Arrays.asList(testTransactionDTO));

        List<FinancialTransactionDTO> result = financialTransactionService.findByPayerExternalId("payer-ext-123");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(financialTransactionRepository, times(1)).findByPayerExternalId("payer-ext-123");
    }

    @Test
    void findByPayerExternalId_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> financialTransactionService.findByPayerExternalId("payer-ext-123"));
    }

    @Test
    void create_Success() {
        when(financialTransactionMapper.toEntity(testTransactionDTO)).thenReturn(testTransaction);
        when(financialTransactionRepository.save(any(FinancialTransaction.class))).thenReturn(testTransaction);
        when(financialTransactionMapper.toDTO(testTransaction)).thenReturn(testTransactionDTO);

        FinancialTransactionDTO result = financialTransactionService.create(testTransactionDTO);

        assertNotNull(result);
        verify(financialTransactionRepository, times(1)).save(any(FinancialTransaction.class));
    }

    @Test
    void processPayment_Success() {
        when(stripeService.createPaymentIntent(any(BigDecimal.class), anyString(), any())).thenReturn("pi_test");
        when(stripeService.confirmPaymentIntent("pi_test")).thenReturn("ch_test");
        when(paymentStatusRepository.findByCode("SUCCEEDED")).thenReturn(Optional.of(testStatus));
        when(financialTransactionMapper.toEntity(any())).thenReturn(testTransaction);
        when(financialTransactionRepository.save(any(FinancialTransaction.class))).thenReturn(testTransaction);
        when(financialTransactionMapper.toDTO(testTransaction)).thenReturn(testTransactionDTO);

        FinancialTransactionDTO result = financialTransactionService.processPayment(testTransactionDTO);

        assertNotNull(result);
        verify(stripeService, times(1)).createPaymentIntent(any(BigDecimal.class), anyString(), any());
        verify(financialTransactionRepository, times(1)).save(any(FinancialTransaction.class));
    }

    @Test
    void refundTransaction_Success() {
        testTransaction.setStripeChargeId("ch_test");
        
        when(financialTransactionRepository.findById(1L)).thenReturn(Optional.of(testTransaction));
        when(stripeService.createRefund("ch_test", new BigDecimal("50.00"))).thenReturn("re_test");
        when(paymentStatusRepository.findByCode("PARTIALLY_REFUNDED")).thenReturn(Optional.of(testStatus));
        when(financialTransactionRepository.save(any(FinancialTransaction.class))).thenReturn(testTransaction);
        when(financialTransactionMapper.toDTO(any(FinancialTransaction.class))).thenReturn(testTransactionDTO);

        FinancialTransactionDTO result = financialTransactionService.refundTransaction(1L, new BigDecimal("50.00"));

        assertNotNull(result);
        verify(stripeService, times(1)).createRefund("ch_test", new BigDecimal("50.00"));
        verify(financialTransactionRepository, times(2)).save(any(FinancialTransaction.class));
    }

    @Test
    void update_Success() {
        when(financialTransactionRepository.findById(1L)).thenReturn(Optional.of(testTransaction));
        when(financialTransactionMapper.toEntity(testTransactionDTO)).thenReturn(testTransaction);
        when(financialTransactionRepository.save(any(FinancialTransaction.class))).thenReturn(testTransaction);
        when(financialTransactionMapper.toDTO(testTransaction)).thenReturn(testTransactionDTO);

        FinancialTransactionDTO result = financialTransactionService.update(1L, testTransactionDTO);

        assertNotNull(result);
        verify(financialTransactionRepository, times(1)).save(any(FinancialTransaction.class));
    }

    @Test
    void update_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);
        when(financialTransactionRepository.findById(1L)).thenReturn(Optional.of(testTransaction));

        assertThrows(UnauthorizedException.class, () -> financialTransactionService.update(1L, testTransactionDTO));
    }

    @Test
    void delete_Success() {
        when(financialTransactionRepository.existsById(1L)).thenReturn(true);
        doNothing().when(financialTransactionRepository).deleteById(1L);

        financialTransactionService.delete(1L);

        verify(financialTransactionRepository, times(1)).deleteById(1L);
    }

    @Test
    void delete_NotFound() {
        when(financialTransactionRepository.existsById(999L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> financialTransactionService.delete(999L));
    }

    @Test
    void delete_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> financialTransactionService.delete(1L));
    }
}
