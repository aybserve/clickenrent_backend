package org.clickenrent.paymentservice.service;

import org.clickenrent.paymentservice.client.RentalServiceClient;
import org.clickenrent.paymentservice.dto.B2BSubscriptionFinTransactionDTO;
import org.clickenrent.paymentservice.dto.FinancialTransactionDTO;
import org.clickenrent.paymentservice.entity.B2BSubscriptionFinTransaction;
import org.clickenrent.paymentservice.entity.FinancialTransaction;
import org.clickenrent.paymentservice.exception.ResourceNotFoundException;
import org.clickenrent.paymentservice.exception.UnauthorizedException;
import org.clickenrent.paymentservice.mapper.B2BSubscriptionFinTransactionMapper;
import org.clickenrent.paymentservice.repository.B2BSubscriptionFinTransactionRepository;
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

@ExtendWith(MockitoExtension.class)
class B2BSubscriptionFinTransactionServiceTest {

    @Mock
    private B2BSubscriptionFinTransactionRepository b2bSubscriptionFinTransactionRepository;

    @Mock
    private B2BSubscriptionFinTransactionMapper b2bSubscriptionFinTransactionMapper;

    @Mock
    private SecurityService securityService;

    @Mock
    private RentalServiceClient rentalServiceClient;

    @InjectMocks
    private B2BSubscriptionFinTransactionService b2bSubscriptionFinTransactionService;

    private B2BSubscriptionFinTransaction testB2BSubscriptionFinTransaction;
    private B2BSubscriptionFinTransactionDTO testB2BSubscriptionFinTransactionDTO;
    private String testExternalId;

    @BeforeEach
    void setUp() {
        testExternalId = UUID.randomUUID().toString();

        FinancialTransaction financialTransaction = FinancialTransaction.builder()
                .id(1L)
                .build();

        testB2BSubscriptionFinTransaction = B2BSubscriptionFinTransaction.builder()
                .id(1L)
                .externalId(testExternalId)
                .b2bSubscriptionId(1L)
                .financialTransaction(financialTransaction)
                .build();

        testB2BSubscriptionFinTransactionDTO = B2BSubscriptionFinTransactionDTO.builder()
                .id(1L)
                .externalId(testExternalId)
                .b2bSubscriptionId(1L)
                .financialTransaction(FinancialTransactionDTO.builder().id(1L).build())
                .build();

        lenient().when(securityService.isAdmin()).thenReturn(true);
    }

    @Test
    void findAll_AsAdmin_ReturnsAllTransactions() {
        when(b2bSubscriptionFinTransactionRepository.findAll()).thenReturn(Arrays.asList(testB2BSubscriptionFinTransaction));
        when(b2bSubscriptionFinTransactionMapper.toDTOList(anyList())).thenReturn(Arrays.asList(testB2BSubscriptionFinTransactionDTO));

        List<B2BSubscriptionFinTransactionDTO> result = b2bSubscriptionFinTransactionService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(b2bSubscriptionFinTransactionRepository, times(1)).findAll();
    }

    @Test
    void findAll_AsB2B_ReturnsAllTransactions() {
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.isB2B()).thenReturn(true);
        when(b2bSubscriptionFinTransactionRepository.findAll()).thenReturn(Arrays.asList(testB2BSubscriptionFinTransaction));
        when(b2bSubscriptionFinTransactionMapper.toDTOList(anyList())).thenReturn(Arrays.asList(testB2BSubscriptionFinTransactionDTO));

        List<B2BSubscriptionFinTransactionDTO> result = b2bSubscriptionFinTransactionService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void findAll_AsNonAdminNonB2B_ThrowsUnauthorized() {
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.isB2B()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> b2bSubscriptionFinTransactionService.findAll());
    }

    @Test
    void findById_Success() {
        when(b2bSubscriptionFinTransactionRepository.findById(1L)).thenReturn(Optional.of(testB2BSubscriptionFinTransaction));
        when(b2bSubscriptionFinTransactionMapper.toDTO(testB2BSubscriptionFinTransaction)).thenReturn(testB2BSubscriptionFinTransactionDTO);

        B2BSubscriptionFinTransactionDTO result = b2bSubscriptionFinTransactionService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getB2bSubscriptionId());
        verify(b2bSubscriptionFinTransactionRepository, times(1)).findById(1L);
    }

    @Test
    void findById_NotFound() {
        when(b2bSubscriptionFinTransactionRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> b2bSubscriptionFinTransactionService.findById(999L));
    }

    @Test
    void findById_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.isB2B()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> b2bSubscriptionFinTransactionService.findById(1L));
    }

    @Test
    void findByExternalId_Success() {
        when(b2bSubscriptionFinTransactionRepository.findByExternalId(testExternalId)).thenReturn(Optional.of(testB2BSubscriptionFinTransaction));
        when(b2bSubscriptionFinTransactionMapper.toDTO(testB2BSubscriptionFinTransaction)).thenReturn(testB2BSubscriptionFinTransactionDTO);

        B2BSubscriptionFinTransactionDTO result = b2bSubscriptionFinTransactionService.findByExternalId(testExternalId);

        assertNotNull(result);
        verify(b2bSubscriptionFinTransactionRepository, times(1)).findByExternalId(testExternalId);
    }

    @Test
    void findByExternalId_NotFound() {
        String randomId = UUID.randomUUID().toString();
        when(b2bSubscriptionFinTransactionRepository.findByExternalId(randomId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> b2bSubscriptionFinTransactionService.findByExternalId(randomId));
    }

    @Test
    void create_Success() {
        when(rentalServiceClient.checkB2BSubscriptionExists(1L)).thenReturn(true);
        when(b2bSubscriptionFinTransactionMapper.toEntity(testB2BSubscriptionFinTransactionDTO)).thenReturn(testB2BSubscriptionFinTransaction);
        when(b2bSubscriptionFinTransactionRepository.save(any(B2BSubscriptionFinTransaction.class))).thenReturn(testB2BSubscriptionFinTransaction);
        when(b2bSubscriptionFinTransactionMapper.toDTO(testB2BSubscriptionFinTransaction)).thenReturn(testB2BSubscriptionFinTransactionDTO);

        B2BSubscriptionFinTransactionDTO result = b2bSubscriptionFinTransactionService.create(testB2BSubscriptionFinTransactionDTO);

        assertNotNull(result);
        verify(rentalServiceClient, times(1)).checkB2BSubscriptionExists(1L);
        verify(b2bSubscriptionFinTransactionRepository, times(1)).save(any(B2BSubscriptionFinTransaction.class));
    }

    @Test
    void update_Success() {
        when(b2bSubscriptionFinTransactionRepository.findById(1L)).thenReturn(Optional.of(testB2BSubscriptionFinTransaction));
        when(b2bSubscriptionFinTransactionRepository.save(any(B2BSubscriptionFinTransaction.class))).thenReturn(testB2BSubscriptionFinTransaction);
        when(b2bSubscriptionFinTransactionMapper.toDTO(testB2BSubscriptionFinTransaction)).thenReturn(testB2BSubscriptionFinTransactionDTO);

        B2BSubscriptionFinTransactionDTO result = b2bSubscriptionFinTransactionService.update(1L, testB2BSubscriptionFinTransactionDTO);

        assertNotNull(result);
        verify(b2bSubscriptionFinTransactionRepository, times(1)).save(any(B2BSubscriptionFinTransaction.class));
    }

    @Test
    void update_NotFound() {
        when(b2bSubscriptionFinTransactionRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> b2bSubscriptionFinTransactionService.update(999L, testB2BSubscriptionFinTransactionDTO));
    }

    @Test
    void update_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> b2bSubscriptionFinTransactionService.update(1L, testB2BSubscriptionFinTransactionDTO));
    }

    @Test
    void delete_Success() {
        when(b2bSubscriptionFinTransactionRepository.existsById(1L)).thenReturn(true);
        doNothing().when(b2bSubscriptionFinTransactionRepository).deleteById(1L);

        b2bSubscriptionFinTransactionService.delete(1L);

        verify(b2bSubscriptionFinTransactionRepository, times(1)).deleteById(1L);
    }

    @Test
    void delete_NotFound() {
        when(b2bSubscriptionFinTransactionRepository.existsById(999L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> b2bSubscriptionFinTransactionService.delete(999L));
    }

    @Test
    void delete_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> b2bSubscriptionFinTransactionService.delete(1L));
    }
}
