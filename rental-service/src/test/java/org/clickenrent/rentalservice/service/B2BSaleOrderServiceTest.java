package org.clickenrent.rentalservice.service;

import org.clickenrent.rentalservice.dto.B2BSaleOrderDTO;
import org.clickenrent.rentalservice.entity.B2BSaleOrder;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.mapper.B2BSaleOrderMapper;
import org.clickenrent.rentalservice.repository.B2BSaleOrderRepository;
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
class B2BSaleOrderServiceTest {

    @Mock
    private B2BSaleOrderRepository b2bSaleOrderRepository;

    @Mock
    private B2BSaleOrderMapper b2bSaleOrderMapper;

    @Mock
    private SecurityService securityService;


    @InjectMocks
    private B2BSaleOrderService b2bSaleOrderService;

    private B2BSaleOrder testOrder;
    private B2BSaleOrderDTO testOrderDTO;

    @BeforeEach
    void setUp() {
        testOrder = B2BSaleOrder.builder()
        .id(1L)
        .externalId("B2BSO001")
        .sellerCompanyId(1L)
        .buyerCompanyId(2L)
        .dateTime(LocalDateTime.now())
        .build();

        testOrderDTO = B2BSaleOrderDTO.builder()
        .id(1L)
        .externalId("B2BSO001")
        .sellerCompanyId(1L)
        .buyerCompanyId(2L)
        .b2bSaleOrderStatusId(2L)
        .locationId(1L)
        .b2bSaleId(1L)
        .dateTime(LocalDateTime.now())
        .build();
    }

    @Test
    void getAllOrders_ReturnsAllOrders() {
        when(securityService.isAdmin()).thenReturn(true);
        Pageable pageable = PageRequest.of(0, 20);
        Page<B2BSaleOrder> orderPage = new PageImpl<>(Collections.singletonList(testOrder));
        when(b2bSaleOrderRepository.findAll(pageable)).thenReturn(orderPage);
        when(b2bSaleOrderMapper.toDto(testOrder)).thenReturn(testOrderDTO);

        Page<B2BSaleOrderDTO> result = b2bSaleOrderService.getAllOrders(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getOrderById_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(b2bSaleOrderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(b2bSaleOrderMapper.toDto(testOrder)).thenReturn(testOrderDTO);

        B2BSaleOrderDTO result = b2bSaleOrderService.getOrderById(1L);

        assertNotNull(result);
    }

    @Test
    void getOrderById_NotFound() {
        when(b2bSaleOrderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> b2bSaleOrderService.getOrderById(999L));
    }

    @Test
    void createOrder_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(b2bSaleOrderMapper.toEntity(testOrderDTO)).thenReturn(testOrder);
        when(b2bSaleOrderRepository.save(any())).thenReturn(testOrder);
        when(b2bSaleOrderMapper.toDto(testOrder)).thenReturn(testOrderDTO);

        B2BSaleOrderDTO result = b2bSaleOrderService.createOrder(testOrderDTO);

        assertNotNull(result);
    }

    @Test
    void updateOrder_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(b2bSaleOrderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(b2bSaleOrderRepository.save(any())).thenReturn(testOrder);
        when(b2bSaleOrderMapper.toDto(testOrder)).thenReturn(testOrderDTO);

        B2BSaleOrderDTO result = b2bSaleOrderService.updateOrder(1L, testOrderDTO);

        assertNotNull(result);
    }

    @Test
    void deleteOrder_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(b2bSaleOrderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        doNothing().when(b2bSaleOrderRepository).delete(testOrder);

        b2bSaleOrderService.deleteOrder(1L);

        verify(b2bSaleOrderRepository, times(1)).delete(testOrder);
    }
}

