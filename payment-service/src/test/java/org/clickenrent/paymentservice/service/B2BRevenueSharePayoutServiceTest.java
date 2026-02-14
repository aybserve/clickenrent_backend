package org.clickenrent.paymentservice.service;

import org.clickenrent.paymentservice.dto.B2BRevenueSharePayoutDTO;
import org.clickenrent.paymentservice.dto.PaymentStatusDTO;
import org.clickenrent.paymentservice.entity.B2BRevenueSharePayout;
import org.clickenrent.paymentservice.entity.PaymentStatus;
import org.clickenrent.paymentservice.exception.ResourceNotFoundException;
import org.clickenrent.paymentservice.exception.UnauthorizedException;
import org.clickenrent.paymentservice.mapper.B2BRevenueSharePayoutMapper;
import org.clickenrent.paymentservice.repository.B2BRevenueSharePayoutRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
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
class B2BRevenueSharePayoutServiceTest {

    @Mock
    private B2BRevenueSharePayoutRepository b2bRevenueSharePayoutRepository;

    @Mock
    private B2BRevenueSharePayoutMapper b2bRevenueSharePayoutMapper;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private B2BRevenueSharePayoutService b2bRevenueSharePayoutService;

    private B2BRevenueSharePayout testPayout;
    private B2BRevenueSharePayoutDTO testPayoutDTO;
    private PaymentStatus testPaymentStatus;
    private String testExternalId;

    @BeforeEach
    void setUp() {
        testExternalId = UUID.randomUUID().toString();

        testPaymentStatus = PaymentStatus.builder()
                .id(1L)
                .code("PENDING")
                .name("Pending")
                .build();

        testPayout = B2BRevenueSharePayout.builder()
                .id(1L)
                .externalId(testExternalId)
                .companyExternalId("company-ext-123")
                .paymentStatus(testPaymentStatus)
                .dueDate(LocalDate.now().plusDays(30))
                .totalAmount(new BigDecimal("1000.00"))
                .paidAmount(BigDecimal.ZERO)
                .remainingAmount(new BigDecimal("1000.00"))
                .build();

        testPayoutDTO = B2BRevenueSharePayoutDTO.builder()
                .id(1L)
                .externalId(testExternalId)
                .companyExternalId("company-ext-123")
                .paymentStatus(PaymentStatusDTO.builder().id(1L).code("PENDING").name("Pending").build())
                .dueDate(LocalDate.now().plusDays(30))
                .totalAmount(new BigDecimal("1000.00"))
                .paidAmount(BigDecimal.ZERO)
                .remainingAmount(new BigDecimal("1000.00"))
                .build();

        lenient().when(securityService.isAdmin()).thenReturn(true);
        lenient().when(securityService.hasAccessToCompany(anyLong())).thenReturn(true);
        lenient().when(securityService.getCurrentUserCompanyIds()).thenReturn(Arrays.asList(1L));
    }

    @Test
    void findAll_AsAdmin_ReturnsAllPayouts() {
        when(b2bRevenueSharePayoutRepository.findAll()).thenReturn(Arrays.asList(testPayout));
        when(b2bRevenueSharePayoutMapper.toDTOList(anyList())).thenReturn(Arrays.asList(testPayoutDTO));

        List<B2BRevenueSharePayoutDTO> result = b2bRevenueSharePayoutService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(b2bRevenueSharePayoutRepository, times(1)).findAll();
    }

    @Test
    void findAll_AsNonAdmin_FiltersPayouts() {
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.getCurrentUserCompanyIds()).thenReturn(Arrays.asList(1L));
        when(b2bRevenueSharePayoutRepository.findAll()).thenReturn(Arrays.asList(testPayout));
        when(b2bRevenueSharePayoutMapper.toDTOList(anyList())).thenReturn(Arrays.asList(testPayoutDTO));

        List<B2BRevenueSharePayoutDTO> result = b2bRevenueSharePayoutService.findAll();

        assertNotNull(result);
        verify(b2bRevenueSharePayoutRepository, times(1)).findAll();
    }

    @Test
    void findById_Success() {
        when(b2bRevenueSharePayoutRepository.findById(1L)).thenReturn(Optional.of(testPayout));
        when(b2bRevenueSharePayoutMapper.toDTO(testPayout)).thenReturn(testPayoutDTO);

        B2BRevenueSharePayoutDTO result = b2bRevenueSharePayoutService.findById(1L);

        assertNotNull(result);
        assertEquals(new BigDecimal("1000.00"), result.getTotalAmount());
        verify(b2bRevenueSharePayoutRepository, times(1)).findById(1L);
    }

    @Test
    void findById_NotFound() {
        when(b2bRevenueSharePayoutRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> b2bRevenueSharePayoutService.findById(999L));
    }

    @Test
    void findByExternalId_Success() {
        when(b2bRevenueSharePayoutRepository.findByExternalId(testExternalId)).thenReturn(Optional.of(testPayout));
        when(b2bRevenueSharePayoutMapper.toDTO(testPayout)).thenReturn(testPayoutDTO);

        B2BRevenueSharePayoutDTO result = b2bRevenueSharePayoutService.findByExternalId(testExternalId);

        assertNotNull(result);
        verify(b2bRevenueSharePayoutRepository, times(1)).findByExternalId(testExternalId);
    }

    @Test
    void findByCompanyExternalId_Success() {
        when(b2bRevenueSharePayoutRepository.findByCompanyExternalId("company-ext-123")).thenReturn(Arrays.asList(testPayout));
        when(b2bRevenueSharePayoutMapper.toDTOList(anyList())).thenReturn(Arrays.asList(testPayoutDTO));

        List<B2BRevenueSharePayoutDTO> result = b2bRevenueSharePayoutService.findByCompanyExternalId("company-ext-123");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(b2bRevenueSharePayoutRepository, times(1)).findByCompanyExternalId("company-ext-123");
    }

    @Test
    void findByCompanyExternalId_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> b2bRevenueSharePayoutService.findByCompanyExternalId("company-ext-123"));
    }

    @Test
    void create_Success() {
        when(b2bRevenueSharePayoutMapper.toEntity(testPayoutDTO)).thenReturn(testPayout);
        when(b2bRevenueSharePayoutRepository.save(any(B2BRevenueSharePayout.class))).thenReturn(testPayout);
        when(b2bRevenueSharePayoutMapper.toDTO(testPayout)).thenReturn(testPayoutDTO);

        B2BRevenueSharePayoutDTO result = b2bRevenueSharePayoutService.create(testPayoutDTO);

        assertNotNull(result);
        verify(b2bRevenueSharePayoutRepository, times(1)).save(any(B2BRevenueSharePayout.class));
    }

    @Test
    void create_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.hasAccessToCompany(1L)).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> b2bRevenueSharePayoutService.create(testPayoutDTO));
    }

    @Test
    void update_Success() {
        when(b2bRevenueSharePayoutRepository.findById(1L)).thenReturn(Optional.of(testPayout));
        when(b2bRevenueSharePayoutMapper.toEntity(testPayoutDTO)).thenReturn(testPayout);
        when(b2bRevenueSharePayoutRepository.save(any(B2BRevenueSharePayout.class))).thenReturn(testPayout);
        when(b2bRevenueSharePayoutMapper.toDTO(testPayout)).thenReturn(testPayoutDTO);

        B2BRevenueSharePayoutDTO result = b2bRevenueSharePayoutService.update(1L, testPayoutDTO);

        assertNotNull(result);
        verify(b2bRevenueSharePayoutRepository, times(1)).save(any(B2BRevenueSharePayout.class));
    }

    @Test
    void update_NotFound() {
        when(b2bRevenueSharePayoutRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> b2bRevenueSharePayoutService.update(999L, testPayoutDTO));
    }

    @Test
    void delete_Success() {
        when(b2bRevenueSharePayoutRepository.findById(1L)).thenReturn(Optional.of(testPayout));
        doNothing().when(b2bRevenueSharePayoutRepository).deleteById(1L);

        b2bRevenueSharePayoutService.delete(1L);

        verify(b2bRevenueSharePayoutRepository, times(1)).deleteById(1L);
    }

    @Test
    void delete_NotFound() {
        when(b2bRevenueSharePayoutRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> b2bRevenueSharePayoutService.delete(999L));
    }

    @Test
    void delete_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);
        when(b2bRevenueSharePayoutRepository.findById(1L)).thenReturn(Optional.of(testPayout));

        assertThrows(UnauthorizedException.class, () -> b2bRevenueSharePayoutService.delete(1L));
    }
}
