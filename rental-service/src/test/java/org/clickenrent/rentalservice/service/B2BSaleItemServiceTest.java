package org.clickenrent.rentalservice.service;

import org.clickenrent.rentalservice.dto.B2BSaleItemDTO;
import org.clickenrent.rentalservice.entity.B2BSale;
import org.clickenrent.rentalservice.entity.B2BSaleItem;
import org.clickenrent.rentalservice.entity.Location;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.mapper.B2BSaleItemMapper;
import org.clickenrent.rentalservice.repository.B2BSaleItemRepository;
import org.clickenrent.rentalservice.repository.B2BSaleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class B2BSaleItemServiceTest {

    @Mock
    private B2BSaleItemRepository b2bSaleItemRepository;

    @Mock
    private B2BSaleRepository b2bSaleRepository;

    @Mock
    private B2BSaleItemMapper b2bSaleItemMapper;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private B2BSaleItemService b2bSaleItemService;

    private B2BSaleItem testItem;
    private B2BSaleItemDTO testItemDTO;
    private B2BSale testSale;
    private Location testLocation;

    @BeforeEach
    void setUp() {
        testLocation = Location.builder()
        .id(1L)
        .companyExternalId("company-ext-001")
        .build();

        testSale = B2BSale.builder()
        .id(1L)
        .externalId("B2BS001")
        .location(testLocation)
        .build();

        testItem = B2BSaleItem.builder()
        .id(1L)
        .externalId("B2BSI001")
        .b2bSale(testSale)
        .productId(1L)
        .quantity(10)
        .price(new BigDecimal("250.00"))
        .totalPrice(new BigDecimal("2500.00"))
        .build();

        testItemDTO = B2BSaleItemDTO.builder()
        .id(1L)
        .externalId("B2BSI001")
        .b2bSaleId(1L)
        .productId(1L)
        .quantity(10)
        .price(new BigDecimal("250.00"))
        .totalPrice(new BigDecimal("2500.00"))
        .build();

    }

    @Test
    void getItemsBySale_ReturnsAllItems() {
        when(securityService.isAdmin()).thenReturn(true);
        when(b2bSaleRepository.findById(1L)).thenReturn(Optional.of(testSale));
        when(b2bSaleItemRepository.findByB2bSale(testSale))
        .thenReturn(Collections.singletonList(testItem));
        when(b2bSaleItemMapper.toDto(testItem)).thenReturn(testItemDTO);

        List<B2BSaleItemDTO> result = b2bSaleItemService.getItemsBySale(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getItemById_Success() {
        when(b2bSaleItemRepository.findById(1L)).thenReturn(Optional.of(testItem));
        when(b2bSaleItemMapper.toDto(testItem)).thenReturn(testItemDTO);

        B2BSaleItemDTO result = b2bSaleItemService.getItemById(1L);

        assertNotNull(result);
    }

    @Test
    void getItemById_NotFound() {
        when(b2bSaleItemRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> b2bSaleItemService.getItemById(999L));
    }

    @Test
    void createItem_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(b2bSaleRepository.findById(1L)).thenReturn(Optional.of(testSale));
        when(b2bSaleItemMapper.toEntity(testItemDTO)).thenReturn(testItem);
        when(b2bSaleItemRepository.save(any())).thenReturn(testItem);
        when(b2bSaleItemMapper.toDto(testItem)).thenReturn(testItemDTO);

        B2BSaleItemDTO result = b2bSaleItemService.createItem(testItemDTO);

        assertNotNull(result);
    }

    @Test
    void updateItem_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(b2bSaleItemRepository.findById(1L)).thenReturn(Optional.of(testItem));
        doNothing().when(b2bSaleItemMapper).updateEntityFromDto(testItemDTO, testItem);
        when(b2bSaleItemRepository.save(any())).thenReturn(testItem);
        when(b2bSaleItemMapper.toDto(testItem)).thenReturn(testItemDTO);

        B2BSaleItemDTO result = b2bSaleItemService.updateItem(1L, testItemDTO);

        assertNotNull(result);
    }

    @Test
    void deleteItem_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(b2bSaleItemRepository.findById(1L)).thenReturn(Optional.of(testItem));
        doNothing().when(b2bSaleItemRepository).delete(testItem);

        b2bSaleItemService.deleteItem(1L);

        verify(b2bSaleItemRepository, times(1)).delete(testItem);
    }
}

