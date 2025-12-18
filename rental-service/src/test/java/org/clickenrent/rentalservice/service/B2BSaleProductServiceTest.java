package org.clickenrent.rentalservice.service;

import org.clickenrent.rentalservice.dto.B2BSaleProductDTO;
import org.clickenrent.rentalservice.entity.B2BSale;
import org.clickenrent.rentalservice.entity.B2BSaleProduct;
import org.clickenrent.rentalservice.entity.Location;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.mapper.B2BSaleProductMapper;
import org.clickenrent.rentalservice.repository.B2BSaleProductRepository;
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
class B2BSaleProductServiceTest {

    @Mock
    private B2BSaleProductRepository b2bSaleProductRepository;

    @Mock
    private B2BSaleRepository b2bSaleRepository;

    @Mock
    private B2BSaleProductMapper b2bSaleProductMapper;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private B2BSaleProductService b2bSaleProductService;

    private B2BSaleProduct testProduct;
    private B2BSaleProductDTO testProductDTO;
    private B2BSale testSale;
    private Location testLocation;

    @BeforeEach
    void setUp() {
        testLocation = Location.builder()
        .id(1L)
        .companyId(1L)
        .build();

        testSale = B2BSale.builder()
        .id(1L)
        .externalId("B2BS001")
        .location(testLocation)
        .build();

        testProduct = B2BSaleProduct.builder()
        .id(1L)
        .externalId("B2BSP001")
        .b2bSale(testSale)
        .productId(1L)
        .quantity(10)
        .price(new BigDecimal("250.00"))
        .totalPrice(new BigDecimal("2500.00"))
        .build();

        testProductDTO = B2BSaleProductDTO.builder()
        .id(1L)
        .externalId("B2BSP001")
        .b2bSaleId(1L)
        .productId(1L)
        .quantity(10)
        .price(new BigDecimal("250.00"))
        .totalPrice(new BigDecimal("2500.00"))
        .build();

    }

    @Test
    void getProductsBySale_ReturnsAllProducts() {
        when(securityService.isAdmin()).thenReturn(true);
        when(b2bSaleRepository.findById(1L)).thenReturn(Optional.of(testSale));
        when(b2bSaleProductRepository.findByB2bSale(testSale))
        .thenReturn(Collections.singletonList(testProduct));
        when(b2bSaleProductMapper.toDto(testProduct)).thenReturn(testProductDTO);

        List<B2BSaleProductDTO> result = b2bSaleProductService.getProductsBySale(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getProductById_Success() {
        when(b2bSaleProductRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(b2bSaleProductMapper.toDto(testProduct)).thenReturn(testProductDTO);

        B2BSaleProductDTO result = b2bSaleProductService.getProductById(1L);

        assertNotNull(result);
    }

    @Test
    void getProductById_NotFound() {
        when(b2bSaleProductRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> b2bSaleProductService.getProductById(999L));
    }

    @Test
    void createProduct_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(b2bSaleRepository.findById(1L)).thenReturn(Optional.of(testSale));
        when(b2bSaleProductMapper.toEntity(testProductDTO)).thenReturn(testProduct);
        when(b2bSaleProductRepository.save(any())).thenReturn(testProduct);
        when(b2bSaleProductMapper.toDto(testProduct)).thenReturn(testProductDTO);

        B2BSaleProductDTO result = b2bSaleProductService.createProduct(testProductDTO);

        assertNotNull(result);
    }

    @Test
    void updateProduct_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(b2bSaleProductRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        doNothing().when(b2bSaleProductMapper).updateEntityFromDto(testProductDTO, testProduct);
        when(b2bSaleProductRepository.save(any())).thenReturn(testProduct);
        when(b2bSaleProductMapper.toDto(testProduct)).thenReturn(testProductDTO);

        B2BSaleProductDTO result = b2bSaleProductService.updateProduct(1L, testProductDTO);

        assertNotNull(result);
    }

    @Test
    void deleteProduct_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(b2bSaleProductRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        doNothing().when(b2bSaleProductRepository).delete(testProduct);

        b2bSaleProductService.deleteProduct(1L);

        verify(b2bSaleProductRepository, times(1)).delete(testProduct);
    }
}


