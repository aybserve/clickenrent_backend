package org.clickenrent.rentalservice.service;

import org.clickenrent.rentalservice.dto.B2BSaleOrderItemDTO;
import org.clickenrent.rentalservice.entity.B2BSaleOrderItem;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.mapper.B2BSaleOrderItemMapper;
import org.clickenrent.rentalservice.repository.B2BSaleOrderItemRepository;
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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class B2BSaleOrderItemServiceTest {

    @Mock
    private B2BSaleOrderItemRepository b2bSaleOrderItemRepository;

    @Mock
    private B2BSaleOrderItemMapper b2bSaleOrderItemMapper;

    @Mock
    private SecurityService securityService;


    @InjectMocks
    private B2BSaleOrderItemService b2bSaleOrderItemService;

    private B2BSaleOrderItem testItem;
    private B2BSaleOrderItemDTO testItemDTO;

    @BeforeEach
    void setUp() {
        testItem = B2BSaleOrderItem.builder()
        .id(1L)
        .build();

        testItemDTO = B2BSaleOrderItemDTO.builder()
        .id(1L)
        .b2bSaleOrderId(1L)
        .build();
    }

    @Test
    void getAllB2BSaleOrderItems_ReturnsAll() {
        when(securityService.isAdmin()).thenReturn(true);
        Pageable pageable = PageRequest.of(0, 20);
        Page<B2BSaleOrderItem> itemPage = new PageImpl<>(Collections.singletonList(testItem));
        when(b2bSaleOrderItemRepository.findAll(pageable)).thenReturn(itemPage);
        when(b2bSaleOrderItemMapper.toDto(testItem)).thenReturn(testItemDTO);

        Page<B2BSaleOrderItemDTO> result = b2bSaleOrderItemService.getAllItems(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(b2bSaleOrderItemRepository, times(1)).findAll(pageable);
    }

    @Test
    void getItemsByOrderId_Success() {
        when(b2bSaleOrderItemRepository.findByB2bSaleOrderId(1L))
        .thenReturn(Collections.singletonList(testItem));
        when(b2bSaleOrderItemMapper.toDto(testItem)).thenReturn(testItemDTO);

        List<B2BSaleOrderItemDTO> result = b2bSaleOrderItemService.getItemsByOrderId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(b2bSaleOrderItemRepository, times(1)).findByB2bSaleOrderId(1L);
    }

    @Test
    void getItemById_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(b2bSaleOrderItemRepository.findById(1L)).thenReturn(Optional.of(testItem));
        when(b2bSaleOrderItemMapper.toDto(testItem)).thenReturn(testItemDTO);

        B2BSaleOrderItemDTO result = b2bSaleOrderItemService.getItemById(1L);

        assertNotNull(result);
        verify(b2bSaleOrderItemRepository, times(1)).findById(1L);
    }

    @Test
    void getItemById_NotFound() {
        when(b2bSaleOrderItemRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> b2bSaleOrderItemService.getItemById(999L));
    }

    @Test
    void createItem_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(b2bSaleOrderItemMapper.toEntity(testItemDTO)).thenReturn(testItem);
        when(b2bSaleOrderItemRepository.save(any())).thenReturn(testItem);
        when(b2bSaleOrderItemMapper.toDto(testItem)).thenReturn(testItemDTO);

        B2BSaleOrderItemDTO result = b2bSaleOrderItemService.createItem(testItemDTO);

        assertNotNull(result);
        verify(b2bSaleOrderItemRepository, times(1)).save(any());
    }

    @Test
    void updateItem_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(b2bSaleOrderItemRepository.findById(1L)).thenReturn(Optional.of(testItem));
        doNothing().when(b2bSaleOrderItemMapper).updateEntityFromDto(testItemDTO, testItem);
        when(b2bSaleOrderItemRepository.save(any())).thenReturn(testItem);
        when(b2bSaleOrderItemMapper.toDto(testItem)).thenReturn(testItemDTO);

        B2BSaleOrderItemDTO result = b2bSaleOrderItemService.updateItem(1L, testItemDTO);

        assertNotNull(result);
        verify(b2bSaleOrderItemMapper, times(1)).updateEntityFromDto(testItemDTO, testItem);
        verify(b2bSaleOrderItemRepository, times(1)).save(any());
    }

    @Test
    void deleteItem_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(b2bSaleOrderItemRepository.findById(1L)).thenReturn(Optional.of(testItem));
        doNothing().when(b2bSaleOrderItemRepository).delete(testItem);

        b2bSaleOrderItemService.deleteItem(1L);

        verify(b2bSaleOrderItemRepository, times(1)).delete(testItem);
    }

    @Test
    void deleteItem_NotFound() {
        when(securityService.isAdmin()).thenReturn(true);
        when(b2bSaleOrderItemRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> b2bSaleOrderItemService.deleteItem(999L));
    }
}

