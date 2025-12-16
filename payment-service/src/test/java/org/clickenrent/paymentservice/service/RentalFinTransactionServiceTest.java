package org.clickenrent.paymentservice.service;

import org.clickenrent.paymentservice.client.RentalServiceClient;
import org.clickenrent.paymentservice.dto.FinancialTransactionDTO;
import org.clickenrent.paymentservice.dto.RentalFinTransactionDTO;
import org.clickenrent.paymentservice.entity.FinancialTransaction;
import org.clickenrent.paymentservice.entity.RentalFinTransaction;
import org.clickenrent.paymentservice.exception.ResourceNotFoundException;
import org.clickenrent.paymentservice.exception.UnauthorizedException;
import org.clickenrent.paymentservice.mapper.RentalFinTransactionMapper;
import org.clickenrent.paymentservice.repository.RentalFinTransactionRepository;
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
class RentalFinTransactionServiceTest {

    @Mock
    private RentalFinTransactionRepository rentalFinTransactionRepository;

    @Mock
    private RentalFinTransactionMapper rentalFinTransactionMapper;

    @Mock
    private SecurityService securityService;

    @Mock
    private RentalServiceClient rentalServiceClient;

    @InjectMocks
    private RentalFinTransactionService rentalFinTransactionService;

    private RentalFinTransaction testRentalFinTransaction;
    private RentalFinTransactionDTO testRentalFinTransactionDTO;
    private FinancialTransaction testFinancialTransaction;
    private UUID testExternalId;

    @BeforeEach
    void setUp() {
        testExternalId = UUID.randomUUID();

        testFinancialTransaction = FinancialTransaction.builder()
                .id(1L)
                .build();

        testRentalFinTransaction = RentalFinTransaction.builder()
                .id(1L)
                .externalId(testExternalId)
                .rentalId(1L)
                .financialTransaction(testFinancialTransaction)
                .build();

        testRentalFinTransactionDTO = RentalFinTransactionDTO.builder()
                .id(1L)
                .externalId(testExternalId)
                .rentalId(1L)
                .financialTransaction(FinancialTransactionDTO.builder().id(1L).build())
                .build();

        lenient().when(securityService.isAdmin()).thenReturn(true);
    }

    @Test
    void findAll_AsAdmin_ReturnsAllTransactions() {
        when(rentalFinTransactionRepository.findAll()).thenReturn(Arrays.asList(testRentalFinTransaction));
        when(rentalFinTransactionMapper.toDTOList(anyList())).thenReturn(Arrays.asList(testRentalFinTransactionDTO));

        List<RentalFinTransactionDTO> result = rentalFinTransactionService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(rentalFinTransactionRepository, times(1)).findAll();
    }

    @Test
    void findAll_AsNonAdmin_ThrowsUnauthorized() {
        when(securityService.isAdmin()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> rentalFinTransactionService.findAll());
    }

    @Test
    void findById_Success() {
        when(rentalFinTransactionRepository.findById(1L)).thenReturn(Optional.of(testRentalFinTransaction));
        when(rentalFinTransactionMapper.toDTO(testRentalFinTransaction)).thenReturn(testRentalFinTransactionDTO);

        RentalFinTransactionDTO result = rentalFinTransactionService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getRentalId());
        verify(rentalFinTransactionRepository, times(1)).findById(1L);
    }

    @Test
    void findById_NotFound() {
        when(rentalFinTransactionRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> rentalFinTransactionService.findById(999L));
    }

    @Test
    void findById_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> rentalFinTransactionService.findById(1L));
    }

    @Test
    void findByExternalId_Success() {
        when(rentalFinTransactionRepository.findByExternalId(testExternalId)).thenReturn(Optional.of(testRentalFinTransaction));
        when(rentalFinTransactionMapper.toDTO(testRentalFinTransaction)).thenReturn(testRentalFinTransactionDTO);

        RentalFinTransactionDTO result = rentalFinTransactionService.findByExternalId(testExternalId);

        assertNotNull(result);
        verify(rentalFinTransactionRepository, times(1)).findByExternalId(testExternalId);
    }

    @Test
    void findByExternalId_NotFound() {
        UUID randomId = UUID.randomUUID();
        when(rentalFinTransactionRepository.findByExternalId(randomId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> rentalFinTransactionService.findByExternalId(randomId));
    }

    @Test
    void create_Success() {
        when(rentalServiceClient.checkRentalExists(1L)).thenReturn(true);
        when(rentalFinTransactionMapper.toEntity(testRentalFinTransactionDTO)).thenReturn(testRentalFinTransaction);
        when(rentalFinTransactionRepository.save(any(RentalFinTransaction.class))).thenReturn(testRentalFinTransaction);
        when(rentalFinTransactionMapper.toDTO(testRentalFinTransaction)).thenReturn(testRentalFinTransactionDTO);

        RentalFinTransactionDTO result = rentalFinTransactionService.create(testRentalFinTransactionDTO);

        assertNotNull(result);
        verify(rentalServiceClient, times(1)).checkRentalExists(1L);
        verify(rentalFinTransactionRepository, times(1)).save(any(RentalFinTransaction.class));
    }

    @Test
    void update_Success() {
        when(rentalFinTransactionRepository.findById(1L)).thenReturn(Optional.of(testRentalFinTransaction));
        when(rentalFinTransactionRepository.save(any(RentalFinTransaction.class))).thenReturn(testRentalFinTransaction);
        when(rentalFinTransactionMapper.toDTO(testRentalFinTransaction)).thenReturn(testRentalFinTransactionDTO);

        RentalFinTransactionDTO result = rentalFinTransactionService.update(1L, testRentalFinTransactionDTO);

        assertNotNull(result);
        verify(rentalFinTransactionRepository, times(1)).save(any(RentalFinTransaction.class));
    }

    @Test
    void update_NotFound() {
        when(rentalFinTransactionRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> rentalFinTransactionService.update(999L, testRentalFinTransactionDTO));
    }

    @Test
    void update_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> rentalFinTransactionService.update(1L, testRentalFinTransactionDTO));
    }

    @Test
    void delete_Success() {
        when(rentalFinTransactionRepository.existsById(1L)).thenReturn(true);
        doNothing().when(rentalFinTransactionRepository).deleteById(1L);

        rentalFinTransactionService.delete(1L);

        verify(rentalFinTransactionRepository, times(1)).deleteById(1L);
    }

    @Test
    void delete_NotFound() {
        when(rentalFinTransactionRepository.existsById(999L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> rentalFinTransactionService.delete(999L));
    }

    @Test
    void delete_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> rentalFinTransactionService.delete(1L));
    }
}
