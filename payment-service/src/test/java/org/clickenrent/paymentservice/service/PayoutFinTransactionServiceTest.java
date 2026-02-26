package org.clickenrent.paymentservice.service;

import org.clickenrent.paymentservice.dto.B2BRevenueSharePayoutDTO;
import org.clickenrent.paymentservice.dto.FinancialTransactionDTO;
import org.clickenrent.paymentservice.dto.PayoutFinTransactionDTO;
import org.clickenrent.paymentservice.entity.B2BRevenueSharePayout;
import org.clickenrent.paymentservice.entity.FinancialTransaction;
import org.clickenrent.paymentservice.entity.PayoutFinTransaction;
import org.clickenrent.paymentservice.exception.ResourceNotFoundException;
import org.clickenrent.paymentservice.exception.UnauthorizedException;
import org.clickenrent.paymentservice.mapper.PayoutFinTransactionMapper;
import org.clickenrent.paymentservice.repository.PayoutFinTransactionRepository;
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
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class PayoutFinTransactionServiceTest {

    @Mock
    private PayoutFinTransactionRepository payoutFinTransactionRepository;

    @Mock
    private PayoutFinTransactionMapper payoutFinTransactionMapper;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private PayoutFinTransactionService payoutFinTransactionService;

    private PayoutFinTransaction testPayoutFinTransaction;
    private PayoutFinTransactionDTO testPayoutFinTransactionDTO;
    private String testExternalId;

    @BeforeEach
    void setUp() {
        testExternalId = UUID.randomUUID().toString();

        B2BRevenueSharePayout payout = B2BRevenueSharePayout.builder()
                .id(1L)
                .build();

        FinancialTransaction financialTransaction = FinancialTransaction.builder()
                .id(1L)
                .build();

        testPayoutFinTransaction = PayoutFinTransaction.builder()
                .id(1L)
                .externalId(testExternalId)
                .b2bRevenueSharePayout(payout)
                .financialTransaction(financialTransaction)
                .build();

        testPayoutFinTransactionDTO = PayoutFinTransactionDTO.builder()
                .id(1L)
                .externalId(testExternalId)
                .b2bRevenueSharePayout(B2BRevenueSharePayoutDTO.builder().id(1L).build())
                .financialTransaction(FinancialTransactionDTO.builder().id(1L).build())
                .build();

        lenient().when(securityService.isAdmin()).thenReturn(true);
        lenient().when(securityService.hasAccessToCompany(anyLong())).thenReturn(true);
        lenient().when(securityService.getCurrentUserCompanyIds()).thenReturn(Arrays.asList(1L));
    }

    @Test
    void findAll_AsAdmin_ReturnsAllTransactions() {
        when(payoutFinTransactionRepository.findAll()).thenReturn(Arrays.asList(testPayoutFinTransaction));
        when(payoutFinTransactionMapper.toDTOList(anyList())).thenReturn(Arrays.asList(testPayoutFinTransactionDTO));

        List<PayoutFinTransactionDTO> result = payoutFinTransactionService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(payoutFinTransactionRepository, times(1)).findAll();
    }

    @Test
    void findAll_AsB2B_ReturnsAllTransactions() {
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.isB2B()).thenReturn(true);
        when(payoutFinTransactionRepository.findAll()).thenReturn(Arrays.asList(testPayoutFinTransaction));
        when(payoutFinTransactionMapper.toDTOList(anyList())).thenReturn(Arrays.asList(testPayoutFinTransactionDTO));

        List<PayoutFinTransactionDTO> result = payoutFinTransactionService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void findAll_AsNonAdminNonB2B_ThrowsUnauthorized() {
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.isB2B()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> payoutFinTransactionService.findAll());
    }

    @Test
    void findById_Success() {
        when(payoutFinTransactionRepository.findById(1L)).thenReturn(Optional.of(testPayoutFinTransaction));
        when(payoutFinTransactionMapper.toDTO(testPayoutFinTransaction)).thenReturn(testPayoutFinTransactionDTO);

        PayoutFinTransactionDTO result = payoutFinTransactionService.findById(1L);

        assertNotNull(result);
        verify(payoutFinTransactionRepository, times(1)).findById(1L);
    }

    @Test
    void findById_NotFound() {
        when(payoutFinTransactionRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> payoutFinTransactionService.findById(999L));
    }

    @Test
    void findById_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.isB2B()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> payoutFinTransactionService.findById(1L));
    }

    @Test
    void findByExternalId_Success() {
        when(payoutFinTransactionRepository.findByExternalId(testExternalId)).thenReturn(Optional.of(testPayoutFinTransaction));
        when(payoutFinTransactionMapper.toDTO(testPayoutFinTransaction)).thenReturn(testPayoutFinTransactionDTO);

        PayoutFinTransactionDTO result = payoutFinTransactionService.findByExternalId(testExternalId);

        assertNotNull(result);
        verify(payoutFinTransactionRepository, times(1)).findByExternalId(testExternalId);
    }

    @Test
    void findByExternalId_NotFound() {
        String randomId = UUID.randomUUID().toString();
        when(payoutFinTransactionRepository.findByExternalId(randomId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> payoutFinTransactionService.findByExternalId(randomId));
    }

    @Test
    void create_Success() {
        when(payoutFinTransactionMapper.toEntity(testPayoutFinTransactionDTO)).thenReturn(testPayoutFinTransaction);
        when(payoutFinTransactionRepository.save(any(PayoutFinTransaction.class))).thenReturn(testPayoutFinTransaction);
        when(payoutFinTransactionMapper.toDTO(testPayoutFinTransaction)).thenReturn(testPayoutFinTransactionDTO);

        PayoutFinTransactionDTO result = payoutFinTransactionService.create(testPayoutFinTransactionDTO);

        assertNotNull(result);
        verify(payoutFinTransactionRepository, times(1)).save(any(PayoutFinTransaction.class));
    }

    @Test
    void update_Success() {
        when(payoutFinTransactionRepository.findById(1L)).thenReturn(Optional.of(testPayoutFinTransaction));
        when(payoutFinTransactionRepository.save(any(PayoutFinTransaction.class))).thenReturn(testPayoutFinTransaction);
        when(payoutFinTransactionMapper.toDTO(testPayoutFinTransaction)).thenReturn(testPayoutFinTransactionDTO);

        PayoutFinTransactionDTO result = payoutFinTransactionService.update(1L, testPayoutFinTransactionDTO);

        assertNotNull(result);
        verify(payoutFinTransactionRepository, times(1)).save(any(PayoutFinTransaction.class));
    }

    @Test
    void update_NotFound() {
        when(payoutFinTransactionRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> payoutFinTransactionService.update(999L, testPayoutFinTransactionDTO));
    }

    @Test
    void update_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> payoutFinTransactionService.update(1L, testPayoutFinTransactionDTO));
    }

    @Test
    void delete_Success() {
        when(payoutFinTransactionRepository.existsById(1L)).thenReturn(true);
        doNothing().when(payoutFinTransactionRepository).deleteById(1L);

        payoutFinTransactionService.delete(1L);

        verify(payoutFinTransactionRepository, times(1)).deleteById(1L);
    }

    @Test
    void delete_NotFound() {
        when(payoutFinTransactionRepository.existsById(999L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> payoutFinTransactionService.delete(999L));
    }

    @Test
    void delete_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> payoutFinTransactionService.delete(1L));
    }
}
