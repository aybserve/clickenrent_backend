package org.clickenrent.rentalservice.service;

import org.clickenrent.rentalservice.dto.B2BSubscriptionOrderItemDTO;
import org.clickenrent.rentalservice.entity.B2BSubscriptionOrderItem;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.mapper.B2BSubscriptionOrderItemMapper;
import org.clickenrent.rentalservice.repository.B2BSubscriptionOrderItemRepository;
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

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class B2BSubscriptionOrderItemServiceTest {

    @Mock
    private B2BSubscriptionOrderItemRepository b2bSubscriptionOrderItemRepository;

    @Mock
    private B2BSubscriptionOrderItemMapper b2bSubscriptionOrderItemMapper;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private B2BSubscriptionOrderItemService b2bSubscriptionOrderItemService;

    private B2BSubscriptionOrderItem testItem;
    private B2BSubscriptionOrderItemDTO testItemDTO;

    @BeforeEach
    void setUp() {
        testItem = B2BSubscriptionOrderItem.builder()
        .id(1L)
        .externalId("BSOI001")
        .productModelType("BikeModel")
        .productModelId(1L)
        .quantity(10)
        .price(new BigDecimal("100.00"))
        .totalPrice(new BigDecimal("1000.00"))
        .build();

        testItemDTO = B2BSubscriptionOrderItemDTO.builder()
        .id(1L)
        .externalId("BSOI001")
        .b2bSubscriptionOrderId(1L)
        .productModelType("BikeModel")
        .productModelId(1L)
        .quantity(10)
        .price(new BigDecimal("100.00"))
        .totalPrice(new BigDecimal("1000.00"))
        .build();

    }

    @Test
    void getAllItems_ReturnsAll() {
        when(securityService.isAdmin()).thenReturn(true);
        Pageable pageable = PageRequest.of(0, 20);
        Page<B2BSubscriptionOrderItem> itemPage = new PageImpl<>(Collections.singletonList(testItem));
        when(b2bSubscriptionOrderItemRepository.findAll(pageable)).thenReturn(itemPage);
        when(b2bSubscriptionOrderItemMapper.toDto(testItem)).thenReturn(testItemDTO);

        Page<B2BSubscriptionOrderItemDTO> result = b2bSubscriptionOrderItemService.getAllItems(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getItemsByOrderId_ReturnsAll() {
        when(b2bSubscriptionOrderItemRepository.findByB2bSubscriptionOrderId(1L))
        .thenReturn(Collections.singletonList(testItem));
        when(b2bSubscriptionOrderItemMapper.toDto(testItem)).thenReturn(testItemDTO);

        List<B2BSubscriptionOrderItemDTO> result = b2bSubscriptionOrderItemService.getItemsByOrderId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getItemById_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(b2bSubscriptionOrderItemRepository.findById(1L)).thenReturn(Optional.of(testItem));
        when(b2bSubscriptionOrderItemMapper.toDto(testItem)).thenReturn(testItemDTO);

        B2BSubscriptionOrderItemDTO result = b2bSubscriptionOrderItemService.getItemById(1L);

        assertNotNull(result);
    }

    @Test
    void getItemById_NotFound() {
        when(b2bSubscriptionOrderItemRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> b2bSubscriptionOrderItemService.getItemById(999L));
    }

    @Test
    void createItem_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(b2bSubscriptionOrderItemMapper.toEntity(testItemDTO)).thenReturn(testItem);
        when(b2bSubscriptionOrderItemRepository.save(any())).thenReturn(testItem);
        when(b2bSubscriptionOrderItemMapper.toDto(testItem)).thenReturn(testItemDTO);

        B2BSubscriptionOrderItemDTO result = b2bSubscriptionOrderItemService.createItem(testItemDTO);

        assertNotNull(result);
    }

    @Test
    void updateItem_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(b2bSubscriptionOrderItemRepository.findById(1L)).thenReturn(Optional.of(testItem));
        doNothing().when(b2bSubscriptionOrderItemMapper).updateEntityFromDto(testItemDTO, testItem);
        when(b2bSubscriptionOrderItemRepository.save(any())).thenReturn(testItem);
        when(b2bSubscriptionOrderItemMapper.toDto(testItem)).thenReturn(testItemDTO);

        B2BSubscriptionOrderItemDTO result = b2bSubscriptionOrderItemService.updateItem(1L, testItemDTO);

        assertNotNull(result);
    }

    @Test
    void deleteItem_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(b2bSubscriptionOrderItemRepository.findById(1L)).thenReturn(Optional.of(testItem));
        doNothing().when(b2bSubscriptionOrderItemRepository).delete(testItem);

        b2bSubscriptionOrderItemService.deleteItem(1L);

        verify(b2bSubscriptionOrderItemRepository, times(1)).delete(testItem);
    }
}


