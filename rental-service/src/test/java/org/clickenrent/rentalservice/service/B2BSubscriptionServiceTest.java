package org.clickenrent.rentalservice.service;

import org.clickenrent.rentalservice.dto.B2BSubscriptionDTO;
import org.clickenrent.rentalservice.entity.B2BSubscription;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.mapper.B2BSubscriptionMapper;
import org.clickenrent.rentalservice.repository.B2BSubscriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class B2BSubscriptionServiceTest {

    @Mock
    private B2BSubscriptionRepository b2bSubscriptionRepository;

    @Mock
    private B2BSubscriptionMapper b2bSubscriptionMapper;

    @Mock
    private SecurityService securityService;


    @InjectMocks
    private B2BSubscriptionService b2bSubscriptionService;

    private B2BSubscription testSubscription;
    private B2BSubscriptionDTO testSubscriptionDTO;

    @BeforeEach
    void setUp() {
        testSubscription = B2BSubscription.builder()
        .id(1L)
        .externalId("B2BSUB001")
        .endDateTime(LocalDateTime.now().plusYears(1))
        .build();

        testSubscriptionDTO = B2BSubscriptionDTO.builder()
        .id(1L)
        .externalId("B2BSUB001")
        .locationId(1L)
        .b2bSubscriptionStatusId(2L)
        .endDateTime(LocalDateTime.now().plusYears(1))
        .build();
    }

    @Test
    void getAllSubscriptions_ReturnsAllSubscriptions() {
        when(securityService.isAdmin()).thenReturn(true);
        Pageable pageable = PageRequest.of(0, 20);
        Page<B2BSubscription> subscriptionPage = new PageImpl<>(Collections.singletonList(testSubscription));
        when(b2bSubscriptionRepository.findAll(pageable)).thenReturn(subscriptionPage);
        when(b2bSubscriptionMapper.toDto(testSubscription)).thenReturn(testSubscriptionDTO);

        Page<B2BSubscriptionDTO> result = b2bSubscriptionService.getAllSubscriptions(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getSubscriptionById_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(b2bSubscriptionRepository.findById(1L)).thenReturn(Optional.of(testSubscription));
        when(b2bSubscriptionMapper.toDto(testSubscription)).thenReturn(testSubscriptionDTO);

        B2BSubscriptionDTO result = b2bSubscriptionService.getSubscriptionById(1L);

        assertNotNull(result);
    }

    @Test
    void getSubscriptionById_NotFound() {
        when(b2bSubscriptionRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> b2bSubscriptionService.getSubscriptionById(999L));
    }

    @Test
    void createSubscription_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(b2bSubscriptionMapper.toEntity(testSubscriptionDTO)).thenReturn(testSubscription);
        when(b2bSubscriptionRepository.save(any())).thenReturn(testSubscription);
        when(b2bSubscriptionMapper.toDto(testSubscription)).thenReturn(testSubscriptionDTO);

        B2BSubscriptionDTO result = b2bSubscriptionService.createSubscription(testSubscriptionDTO);

        assertNotNull(result);
    }

    @Test
    void updateSubscription_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(b2bSubscriptionRepository.findById(1L)).thenReturn(Optional.of(testSubscription));
        when(b2bSubscriptionRepository.save(any())).thenReturn(testSubscription);
        when(b2bSubscriptionMapper.toDto(testSubscription)).thenReturn(testSubscriptionDTO);

        B2BSubscriptionDTO result = b2bSubscriptionService.updateSubscription(1L, testSubscriptionDTO);

        assertNotNull(result);
    }

    @Test
    void deleteSubscription_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(b2bSubscriptionRepository.findById(1L)).thenReturn(Optional.of(testSubscription));
        doNothing().when(b2bSubscriptionRepository).delete(testSubscription);

        b2bSubscriptionService.deleteSubscription(1L);

        verify(b2bSubscriptionRepository, times(1)).delete(testSubscription);
    }
}
