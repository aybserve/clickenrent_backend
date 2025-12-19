package org.clickenrent.paymentservice.service;

import org.clickenrent.paymentservice.client.RentalServiceClient;
import org.clickenrent.paymentservice.dto.B2BRevenueSharePayoutItemDTO;
import org.clickenrent.paymentservice.entity.B2BRevenueSharePayout;
import org.clickenrent.paymentservice.entity.B2BRevenueSharePayoutItem;
import org.clickenrent.paymentservice.exception.ResourceNotFoundException;
import org.clickenrent.paymentservice.exception.UnauthorizedException;
import org.clickenrent.paymentservice.mapper.B2BRevenueSharePayoutItemMapper;
import org.clickenrent.paymentservice.repository.B2BRevenueSharePayoutItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class B2BRevenueSharePayoutItemServiceTest {

    @Mock
    private B2BRevenueSharePayoutItemRepository payoutItemRepository;

    @Mock
    private B2BRevenueSharePayoutItemMapper payoutItemMapper;

    @Mock
    private SecurityService securityService;

    @Mock
    private RentalServiceClient rentalServiceClient;

    @InjectMocks
    private B2BRevenueSharePayoutItemService payoutItemService;

    private B2BRevenueSharePayoutItem testPayoutItem;
    private B2BRevenueSharePayoutItemDTO testPayoutItemDTO;
    private B2BRevenueSharePayout testPayout;
    private String testExternalId;

    @BeforeEach
    void setUp() {
        testExternalId = UUID.randomUUID().toString();

        testPayout = B2BRevenueSharePayout.builder()
                .id(1L)
                .build();

        testPayoutItem = B2BRevenueSharePayoutItem.builder()
                .id(1L)
                .externalId(testExternalId)
                .b2bRevenueSharePayout(testPayout)
                .bikeRentalId(1L)
                .amount(new BigDecimal("50.00"))
                .build();

        testPayoutItemDTO = B2BRevenueSharePayoutItemDTO.builder()
                .id(1L)
                .externalId(testExternalId)
                .b2bRevenueSharePayoutId(1L)
                .bikeRentalId(1L)
                .amount(new BigDecimal("50.00"))
                .build();

        lenient().when(securityService.isAdmin()).thenReturn(true);
    }

    @Test
    void findAll_AsAdmin_ReturnsAllItems() {
        when(payoutItemRepository.findAll()).thenReturn(Arrays.asList(testPayoutItem));
        when(payoutItemMapper.toDTOList(anyList())).thenReturn(Arrays.asList(testPayoutItemDTO));

        List<B2BRevenueSharePayoutItemDTO> result = payoutItemService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(payoutItemRepository, times(1)).findAll();
    }

    @Test
    void findAll_AsB2B_ReturnsAllItems() {
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.isB2B()).thenReturn(true);
        when(payoutItemRepository.findAll()).thenReturn(Arrays.asList(testPayoutItem));
        when(payoutItemMapper.toDTOList(anyList())).thenReturn(Arrays.asList(testPayoutItemDTO));

        List<B2BRevenueSharePayoutItemDTO> result = payoutItemService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void findAll_AsNonAdminNonB2B_ThrowsUnauthorized() {
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.isB2B()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> payoutItemService.findAll());
    }

    @Test
    void findById_Success() {
        when(payoutItemRepository.findById(1L)).thenReturn(Optional.of(testPayoutItem));
        when(payoutItemMapper.toDTO(testPayoutItem)).thenReturn(testPayoutItemDTO);

        B2BRevenueSharePayoutItemDTO result = payoutItemService.findById(1L);

        assertNotNull(result);
        assertEquals(new BigDecimal("50.00"), result.getAmount());
        verify(payoutItemRepository, times(1)).findById(1L);
    }

    @Test
    void findById_NotFound() {
        when(payoutItemRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> payoutItemService.findById(999L));
    }

    @Test
    void findById_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.isB2B()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> payoutItemService.findById(1L));
    }

    @Test
    void findByExternalId_Success() {
        when(payoutItemRepository.findByExternalId(testExternalId)).thenReturn(Optional.of(testPayoutItem));
        when(payoutItemMapper.toDTO(testPayoutItem)).thenReturn(testPayoutItemDTO);

        B2BRevenueSharePayoutItemDTO result = payoutItemService.findByExternalId(testExternalId);

        assertNotNull(result);
        verify(payoutItemRepository, times(1)).findByExternalId(testExternalId);
    }

    @Test
    void findByExternalId_NotFound() {
        String randomId = UUID.randomUUID().toString();
        when(payoutItemRepository.findByExternalId(randomId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> payoutItemService.findByExternalId(randomId));
    }

    @Test
    void findByPayoutId_Success() {
        when(payoutItemRepository.findByB2bRevenueSharePayoutId(1L)).thenReturn(Arrays.asList(testPayoutItem));
        when(payoutItemMapper.toDTOList(anyList())).thenReturn(Arrays.asList(testPayoutItemDTO));

        List<B2BRevenueSharePayoutItemDTO> result = payoutItemService.findByPayoutId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(payoutItemRepository, times(1)).findByB2bRevenueSharePayoutId(1L);
    }

    @Test
    void create_Success() {
        when(rentalServiceClient.checkBikeRentalExists(1L)).thenReturn(true);
        when(payoutItemMapper.toEntity(testPayoutItemDTO)).thenReturn(testPayoutItem);
        when(payoutItemRepository.save(any(B2BRevenueSharePayoutItem.class))).thenReturn(testPayoutItem);
        when(payoutItemMapper.toDTO(testPayoutItem)).thenReturn(testPayoutItemDTO);

        B2BRevenueSharePayoutItemDTO result = payoutItemService.create(testPayoutItemDTO);

        assertNotNull(result);
        verify(rentalServiceClient, times(1)).checkBikeRentalExists(1L);
        verify(payoutItemRepository, times(1)).save(any(B2BRevenueSharePayoutItem.class));
    }

    @Test
    void update_Success() {
        when(payoutItemRepository.findById(1L)).thenReturn(Optional.of(testPayoutItem));
        when(payoutItemRepository.save(any(B2BRevenueSharePayoutItem.class))).thenReturn(testPayoutItem);
        when(payoutItemMapper.toDTO(testPayoutItem)).thenReturn(testPayoutItemDTO);

        B2BRevenueSharePayoutItemDTO result = payoutItemService.update(1L, testPayoutItemDTO);

        assertNotNull(result);
        verify(payoutItemRepository, times(1)).save(any(B2BRevenueSharePayoutItem.class));
    }

    @Test
    void update_NotFound() {
        when(payoutItemRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> payoutItemService.update(999L, testPayoutItemDTO));
    }

    @Test
    void update_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> payoutItemService.update(1L, testPayoutItemDTO));
    }

    @Test
    void delete_Success() {
        when(payoutItemRepository.existsById(1L)).thenReturn(true);
        doNothing().when(payoutItemRepository).deleteById(1L);

        payoutItemService.delete(1L);

        verify(payoutItemRepository, times(1)).deleteById(1L);
    }

    @Test
    void delete_NotFound() {
        when(payoutItemRepository.existsById(999L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> payoutItemService.delete(999L));
    }

    @Test
    void delete_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> payoutItemService.delete(1L));
    }
}
