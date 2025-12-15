package org.clickenrent.rentalservice.service;

import org.clickenrent.rentalservice.dto.B2BSubscriptionOrderDTO;
import org.clickenrent.rentalservice.entity.B2BSubscriptionOrder;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.mapper.B2BSubscriptionOrderMapper;
import org.clickenrent.rentalservice.repository.B2BSubscriptionOrderRepository;
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
class B2BSubscriptionOrderServiceTest {

    @Mock
    private B2BSubscriptionOrderRepository b2bSubscriptionOrderRepository;

    @Mock
    private B2BSubscriptionOrderMapper b2bSubscriptionOrderMapper;

    @InjectMocks
    private B2BSubscriptionOrderService b2bSubscriptionOrderService;

    private B2BSubscriptionOrder testOrder;
    private B2BSubscriptionOrderDTO testOrderDTO;

    @BeforeEach
    void setUp() {
        testOrder = B2BSubscriptionOrder.builder()
                .id(1L)
                .externalId("B2BSORD001")
                .dateTime(LocalDateTime.now())
                .build();

        testOrderDTO = B2BSubscriptionOrderDTO.builder()
                .id(1L)
                .externalId("B2BSORD001")
                .locationId(1L)
                .dateTime(LocalDateTime.now())
                .b2bSubscriptionOrderStatusId(2L)
                .b2bSubscriptionId(1L)
                .build();
    }

    @Test
    void getAllOrders_ReturnsAll() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<B2BSubscriptionOrder> orderPage = new PageImpl<>(Collections.singletonList(testOrder));
        when(b2bSubscriptionOrderRepository.findAll(pageable)).thenReturn(orderPage);
        when(b2bSubscriptionOrderMapper.toDto(testOrder)).thenReturn(testOrderDTO);

        Page<B2BSubscriptionOrderDTO> result = b2bSubscriptionOrderService.getAllOrders(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(b2bSubscriptionOrderRepository, times(1)).findAll(pageable);
    }

    @Test
    void getOrderById_Success() {
        when(b2bSubscriptionOrderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(b2bSubscriptionOrderMapper.toDto(testOrder)).thenReturn(testOrderDTO);

        B2BSubscriptionOrderDTO result = b2bSubscriptionOrderService.getOrderById(1L);

        assertNotNull(result);
        assertEquals("B2BSORD001", result.getExternalId());
        verify(b2bSubscriptionOrderRepository, times(1)).findById(1L);
    }

    @Test
    void getOrderById_NotFound() {
        when(b2bSubscriptionOrderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> b2bSubscriptionOrderService.getOrderById(999L));
    }

    @Test
    void createOrder_Success() {
        when(b2bSubscriptionOrderMapper.toEntity(testOrderDTO)).thenReturn(testOrder);
        when(b2bSubscriptionOrderRepository.save(any())).thenReturn(testOrder);
        when(b2bSubscriptionOrderMapper.toDto(testOrder)).thenReturn(testOrderDTO);

        B2BSubscriptionOrderDTO result = b2bSubscriptionOrderService.createOrder(testOrderDTO);

        assertNotNull(result);
        assertEquals("B2BSORD001", result.getExternalId());
        verify(b2bSubscriptionOrderRepository, times(1)).save(any());
    }

    @Test
    void updateOrder_Success() {
        when(b2bSubscriptionOrderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        doNothing().when(b2bSubscriptionOrderMapper).updateEntityFromDto(testOrderDTO, testOrder);
        when(b2bSubscriptionOrderRepository.save(any())).thenReturn(testOrder);
        when(b2bSubscriptionOrderMapper.toDto(testOrder)).thenReturn(testOrderDTO);

        B2BSubscriptionOrderDTO result = b2bSubscriptionOrderService.updateOrder(1L, testOrderDTO);

        assertNotNull(result);
        assertEquals("B2BSORD001", result.getExternalId());
        verify(b2bSubscriptionOrderMapper, times(1)).updateEntityFromDto(testOrderDTO, testOrder);
        verify(b2bSubscriptionOrderRepository, times(1)).save(any());
    }

    @Test
    void deleteOrder_Success() {
        when(b2bSubscriptionOrderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        doNothing().when(b2bSubscriptionOrderRepository).delete(testOrder);

        b2bSubscriptionOrderService.deleteOrder(1L);

        verify(b2bSubscriptionOrderRepository, times(1)).delete(testOrder);
    }

    @Test
    void deleteOrder_NotFound() {
        when(b2bSubscriptionOrderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> b2bSubscriptionOrderService.deleteOrder(999L));
    }
}
