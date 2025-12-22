package org.clickenrent.paymentservice.service;

import org.clickenrent.paymentservice.client.RentalServiceClient;
import org.clickenrent.paymentservice.dto.B2BSaleFinTransactionDTO;
import org.clickenrent.paymentservice.dto.FinancialTransactionDTO;
import org.clickenrent.paymentservice.entity.B2BSaleFinTransaction;
import org.clickenrent.paymentservice.entity.FinancialTransaction;
import org.clickenrent.paymentservice.exception.ResourceNotFoundException;
import org.clickenrent.paymentservice.exception.UnauthorizedException;
import org.clickenrent.paymentservice.mapper.B2BSaleFinTransactionMapper;
import org.clickenrent.paymentservice.repository.B2BSaleFinTransactionRepository;
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
class B2BSaleFinTransactionServiceTest {

    @Mock
    private B2BSaleFinTransactionRepository b2bSaleFinTransactionRepository;

    @Mock
    private B2BSaleFinTransactionMapper b2bSaleFinTransactionMapper;

    @Mock
    private SecurityService securityService;

    @Mock
    private RentalServiceClient rentalServiceClient;

    @InjectMocks
    private B2BSaleFinTransactionService b2bSaleFinTransactionService;

    private B2BSaleFinTransaction testB2BSaleFinTransaction;
    private B2BSaleFinTransactionDTO testB2BSaleFinTransactionDTO;
    private String testExternalId;

    @BeforeEach
    void setUp() {
        testExternalId = UUID.randomUUID().toString();

        FinancialTransaction financialTransaction = FinancialTransaction.builder()
                .id(1L)
                .build();

        testB2BSaleFinTransaction = B2BSaleFinTransaction.builder()
                .id(1L)
                .externalId(testExternalId)
                .b2bSaleExternalId("b2b-sale-ext-123")
                .financialTransaction(financialTransaction)
                .build();

        testB2BSaleFinTransactionDTO = B2BSaleFinTransactionDTO.builder()
                .id(1L)
                .externalId(testExternalId)
                .b2bSaleExternalId("b2b-sale-ext-123")
                .financialTransaction(FinancialTransactionDTO.builder().id(1L).build())
                .build();

        lenient().when(securityService.isAdmin()).thenReturn(true);
    }

    @Test
    void findAll_AsAdmin_ReturnsAllTransactions() {
        when(b2bSaleFinTransactionRepository.findAll()).thenReturn(Arrays.asList(testB2BSaleFinTransaction));
        when(b2bSaleFinTransactionMapper.toDTOList(anyList())).thenReturn(Arrays.asList(testB2BSaleFinTransactionDTO));

        List<B2BSaleFinTransactionDTO> result = b2bSaleFinTransactionService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(b2bSaleFinTransactionRepository, times(1)).findAll();
    }

    @Test
    void findAll_AsB2B_ReturnsAllTransactions() {
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.isB2B()).thenReturn(true);
        when(b2bSaleFinTransactionRepository.findAll()).thenReturn(Arrays.asList(testB2BSaleFinTransaction));
        when(b2bSaleFinTransactionMapper.toDTOList(anyList())).thenReturn(Arrays.asList(testB2BSaleFinTransactionDTO));

        List<B2BSaleFinTransactionDTO> result = b2bSaleFinTransactionService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void findAll_AsNonAdminNonB2B_ThrowsUnauthorized() {
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.isB2B()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> b2bSaleFinTransactionService.findAll());
    }

    @Test
    void findById_Success() {
        when(b2bSaleFinTransactionRepository.findById(1L)).thenReturn(Optional.of(testB2BSaleFinTransaction));
        when(b2bSaleFinTransactionMapper.toDTO(testB2BSaleFinTransaction)).thenReturn(testB2BSaleFinTransactionDTO);

        B2BSaleFinTransactionDTO result = b2bSaleFinTransactionService.findById(1L);

        assertNotNull(result);
        assertEquals("b2b-sale-ext-123", result.getB2bSaleExternalId());
        verify(b2bSaleFinTransactionRepository, times(1)).findById(1L);
    }

    @Test
    void findById_NotFound() {
        when(b2bSaleFinTransactionRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> b2bSaleFinTransactionService.findById(999L));
    }

    @Test
    void findById_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.isB2B()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> b2bSaleFinTransactionService.findById(1L));
    }

    @Test
    void findByExternalId_Success() {
        when(b2bSaleFinTransactionRepository.findByExternalId(testExternalId)).thenReturn(Optional.of(testB2BSaleFinTransaction));
        when(b2bSaleFinTransactionMapper.toDTO(testB2BSaleFinTransaction)).thenReturn(testB2BSaleFinTransactionDTO);

        B2BSaleFinTransactionDTO result = b2bSaleFinTransactionService.findByExternalId(testExternalId);

        assertNotNull(result);
        verify(b2bSaleFinTransactionRepository, times(1)).findByExternalId(testExternalId);
    }

    @Test
    void findByExternalId_NotFound() {
        String randomId = UUID.randomUUID().toString();
        when(b2bSaleFinTransactionRepository.findByExternalId(randomId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> b2bSaleFinTransactionService.findByExternalId(randomId));
    }

    @Test
    void create_Success() {
        when(rentalServiceClient.checkB2BSaleExists(1L)).thenReturn(true);
        when(b2bSaleFinTransactionMapper.toEntity(testB2BSaleFinTransactionDTO)).thenReturn(testB2BSaleFinTransaction);
        when(b2bSaleFinTransactionRepository.save(any(B2BSaleFinTransaction.class))).thenReturn(testB2BSaleFinTransaction);
        when(b2bSaleFinTransactionMapper.toDTO(testB2BSaleFinTransaction)).thenReturn(testB2BSaleFinTransactionDTO);

        B2BSaleFinTransactionDTO result = b2bSaleFinTransactionService.create(testB2BSaleFinTransactionDTO);

        assertNotNull(result);
        verify(rentalServiceClient, times(1)).checkB2BSaleExists(1L);
        verify(b2bSaleFinTransactionRepository, times(1)).save(any(B2BSaleFinTransaction.class));
    }

    @Test
    void update_Success() {
        when(b2bSaleFinTransactionRepository.findById(1L)).thenReturn(Optional.of(testB2BSaleFinTransaction));
        when(b2bSaleFinTransactionRepository.save(any(B2BSaleFinTransaction.class))).thenReturn(testB2BSaleFinTransaction);
        when(b2bSaleFinTransactionMapper.toDTO(testB2BSaleFinTransaction)).thenReturn(testB2BSaleFinTransactionDTO);

        B2BSaleFinTransactionDTO result = b2bSaleFinTransactionService.update(1L, testB2BSaleFinTransactionDTO);

        assertNotNull(result);
        verify(b2bSaleFinTransactionRepository, times(1)).save(any(B2BSaleFinTransaction.class));
    }

    @Test
    void update_NotFound() {
        when(b2bSaleFinTransactionRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> b2bSaleFinTransactionService.update(999L, testB2BSaleFinTransactionDTO));
    }

    @Test
    void update_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> b2bSaleFinTransactionService.update(1L, testB2BSaleFinTransactionDTO));
    }

    @Test
    void delete_Success() {
        when(b2bSaleFinTransactionRepository.existsById(1L)).thenReturn(true);
        doNothing().when(b2bSaleFinTransactionRepository).deleteById(1L);

        b2bSaleFinTransactionService.delete(1L);

        verify(b2bSaleFinTransactionRepository, times(1)).deleteById(1L);
    }

    @Test
    void delete_NotFound() {
        when(b2bSaleFinTransactionRepository.existsById(999L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> b2bSaleFinTransactionService.delete(999L));
    }

    @Test
    void delete_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> b2bSaleFinTransactionService.delete(1L));
    }
}
