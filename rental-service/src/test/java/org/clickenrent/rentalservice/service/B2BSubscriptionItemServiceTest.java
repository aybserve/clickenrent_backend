package org.clickenrent.rentalservice.service;

import org.clickenrent.rentalservice.dto.B2BSubscriptionItemDTO;
import org.clickenrent.rentalservice.entity.B2BSubscription;
import org.clickenrent.rentalservice.entity.B2BSubscriptionItem;
import org.clickenrent.rentalservice.entity.Location;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.mapper.B2BSubscriptionItemMapper;
import org.clickenrent.rentalservice.repository.B2BSubscriptionItemRepository;
import org.clickenrent.rentalservice.repository.B2BSubscriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class B2BSubscriptionItemServiceTest {

    @Mock
    private B2BSubscriptionItemRepository b2bSubscriptionItemRepository;

    @Mock
    private B2BSubscriptionRepository b2bSubscriptionRepository;

    @Mock
    private B2BSubscriptionItemMapper b2bSubscriptionItemMapper;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private B2BSubscriptionItemService b2bSubscriptionItemService;

    private B2BSubscriptionItem testItem;
    private B2BSubscriptionItemDTO testItemDTO;
    private B2BSubscription testSubscription;
    private Location testLocation;

    @BeforeEach
    void setUp() {
        testLocation = Location.builder()
        .id(1L)
        .companyId(1L)
        .build();

        testSubscription = B2BSubscription.builder()
        .id(1L)
        .externalId("B2BSUB001")
        .location(testLocation)
        .build();

        testItem = B2BSubscriptionItem.builder()
        .id(1L)
        .externalId("BSUBI001")
        .b2bSubscription(testSubscription)
        .productId(1L)
        .startDateTime(LocalDateTime.now())
        .endDateTime(LocalDateTime.now().plusMonths(1))
        .price(new BigDecimal("100.00"))
        .totalPrice(new BigDecimal("3000.00"))
        .build();

        testItemDTO = B2BSubscriptionItemDTO.builder()
        .id(1L)
        .externalId("BSUBI001")
        .b2bSubscriptionId(1L)
        .productId(1L)
        .startDateTime(LocalDateTime.now())
        .endDateTime(LocalDateTime.now().plusMonths(1))
        .price(new BigDecimal("100.00"))
        .totalPrice(new BigDecimal("3000.00"))
        .build();

    }

    @Test
    void getItemsBySubscription_ReturnsAll() {
        when(securityService.isAdmin()).thenReturn(true);
        when(b2bSubscriptionRepository.findById(1L)).thenReturn(Optional.of(testSubscription));
        when(b2bSubscriptionItemRepository.findByB2bSubscription(testSubscription))
        .thenReturn(Collections.singletonList(testItem));
        when(b2bSubscriptionItemMapper.toDto(testItem)).thenReturn(testItemDTO);

        List<B2BSubscriptionItemDTO> result = b2bSubscriptionItemService.getItemsBySubscription(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getItemById_Success() {
        when(b2bSubscriptionItemRepository.findById(1L)).thenReturn(Optional.of(testItem));
        when(b2bSubscriptionItemMapper.toDto(testItem)).thenReturn(testItemDTO);

        B2BSubscriptionItemDTO result = b2bSubscriptionItemService.getItemById(1L);

        assertNotNull(result);
    }

    @Test
    void getItemById_NotFound() {
        when(b2bSubscriptionItemRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> b2bSubscriptionItemService.getItemById(999L));
    }

    @Test
    void createItem_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(b2bSubscriptionRepository.findById(1L)).thenReturn(Optional.of(testSubscription));
        when(b2bSubscriptionItemMapper.toEntity(testItemDTO)).thenReturn(testItem);
        when(b2bSubscriptionItemRepository.save(any())).thenReturn(testItem);
        when(b2bSubscriptionItemMapper.toDto(testItem)).thenReturn(testItemDTO);

        B2BSubscriptionItemDTO result = b2bSubscriptionItemService.createItem(testItemDTO);

        assertNotNull(result);
    }

    @Test
    void updateItem_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(b2bSubscriptionItemRepository.findById(1L)).thenReturn(Optional.of(testItem));
        doNothing().when(b2bSubscriptionItemMapper).updateEntityFromDto(testItemDTO, testItem);
        when(b2bSubscriptionItemRepository.save(any())).thenReturn(testItem);
        when(b2bSubscriptionItemMapper.toDto(testItem)).thenReturn(testItemDTO);

        B2BSubscriptionItemDTO result = b2bSubscriptionItemService.updateItem(1L, testItemDTO);

        assertNotNull(result);
    }

    @Test
    void deleteItem_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(b2bSubscriptionItemRepository.findById(1L)).thenReturn(Optional.of(testItem));
        doNothing().when(b2bSubscriptionItemRepository).delete(testItem);

        b2bSubscriptionItemService.deleteItem(1L);

        verify(b2bSubscriptionItemRepository, times(1)).delete(testItem);
    }
}


