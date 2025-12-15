package org.clickenrent.rentalservice.service;

import org.clickenrent.rentalservice.dto.ServiceProductDTO;
import org.clickenrent.rentalservice.entity.ServiceProduct;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.mapper.ServiceProductMapper;
import org.clickenrent.rentalservice.repository.ServiceProductRepository;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceProductServiceTest {

    @Mock
    private ServiceProductRepository serviceProductRepository;

    @Mock
    private ServiceProductMapper serviceProductMapper;

    @InjectMocks
    private ServiceProductService serviceProductService;

    private ServiceProduct testServiceProduct;
    private ServiceProductDTO testServiceProductDTO;

    @BeforeEach
    void setUp() {
        testServiceProduct = ServiceProduct.builder()
                .id(1L)
                .externalId("SP001")
                .productId(1L)
                .isB2BRentable(true)
                .build();

        testServiceProductDTO = ServiceProductDTO.builder()
                .id(1L)
                .externalId("SP001")
                .serviceId(1L)
                .productId(1L)
                .isB2BRentable(true)
                .build();
    }

    @Test
    void getAllServiceProducts_ReturnsAll() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<ServiceProduct> serviceProductPage = new PageImpl<>(Collections.singletonList(testServiceProduct));
        when(serviceProductRepository.findAll(pageable)).thenReturn(serviceProductPage);
        when(serviceProductMapper.toDto(testServiceProduct)).thenReturn(testServiceProductDTO);

        Page<ServiceProductDTO> result = serviceProductService.getAllServiceProducts(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getServiceProductById_Success() {
        when(serviceProductRepository.findById(1L)).thenReturn(Optional.of(testServiceProduct));
        when(serviceProductMapper.toDto(testServiceProduct)).thenReturn(testServiceProductDTO);

        ServiceProductDTO result = serviceProductService.getServiceProductById(1L);

        assertNotNull(result);
    }

    @Test
    void getServiceProductById_NotFound() {
        when(serviceProductRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> serviceProductService.getServiceProductById(999L));
    }

    @Test
    void createServiceProduct_Success() {
        when(serviceProductMapper.toEntity(testServiceProductDTO)).thenReturn(testServiceProduct);
        when(serviceProductRepository.save(any())).thenReturn(testServiceProduct);
        when(serviceProductMapper.toDto(testServiceProduct)).thenReturn(testServiceProductDTO);

        ServiceProductDTO result = serviceProductService.createServiceProduct(testServiceProductDTO);

        assertNotNull(result);
    }

    @Test
    void deleteServiceProduct_Success() {
        when(serviceProductRepository.findById(1L)).thenReturn(Optional.of(testServiceProduct));
        doNothing().when(serviceProductRepository).delete(testServiceProduct);

        serviceProductService.deleteServiceProduct(1L);

        verify(serviceProductRepository, times(1)).delete(testServiceProduct);
    }
}
